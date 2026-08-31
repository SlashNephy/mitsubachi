# 県踏破度（都道府県レベル別踏破スコア）設計

## 背景と目的

「経県値」（uub.jp / 都道府県市区町村 考案）に着想を得て、都道府県ごとの関わりの深さを 6 段階でスコア化し、
日本地図上で塗り分けて可視化する機能を Mitsubachi に追加する。
Foursquare のチェックイン履歴を持つアプリの強みとして、スコアの土台を自動で埋めた状態から始められる点を狙う。

呼称は「経県値」を使わず、アプリ独自に **県踏破度** とする。スコア表記は `123 / 235`。

## スコープ

### 対象

- 日本 47 都道府県のレベル判定・スコア算出・地図可視化
- チェックイン履歴からの自動判定と、ユーザーによる手動上書き
- 海外チェックインの「訪問国カウンター」表示

### 対象外（v1 では実装しない）

- 海外の州・道単位のレベル分け
- チェックイン全履歴のページングによる日跨ぎ滞在判定
- 市区町村単位の踏破度
- スコアの共有・SNS 投稿

## ドメインモデル

### `Prefecture`（`core/domain/model`）

47 都道府県の enum。以下を持つ。

- `code: Int` — JIS X 0401 コード（1〜47）
- `japaneseName: String` — 「北海道」「東京都」など

### `PrefectureLevel`（`core/domain/model`）

| 値 | 名称 | 点数 |
| --- | --- | --- |
| `Unvisited` | 未踏 | 0 |
| `PassedThrough` | 通過 | 1 |
| `Landed` | 接地 | 2 |
| `Visited` | 訪問 | 3 |
| `Stayed` | 宿泊 | 4 |
| `Lived` | 居住 | 5 |

満点は 47 × 5 = 235。

### `PrefectureCompletion`（`core/domain/model`）

1 都道府県分の判定結果。

- `prefecture: Prefecture`
- `automaticLevel: PrefectureLevel` — チェックイン履歴からの自動判定値
- `manualLevel: PrefectureLevel?` — ユーザーによる上書き（未設定なら null）
- `effectiveLevel: PrefectureLevel` — `manualLevel ?: automaticLevel`
- `checkInVenueCount: Int` — 判定根拠となったベニュー数

## レベル判定

### 判定フロー（`CalculatePrefectureCompletionsUseCase`）

1. `FetchUserVenueHistoriesUseCase` で全ベニュー履歴を取得する（1 リクエスト。現状の実データで 2648 件）。
2. 各ベニューの緯度経度に対し、都道府県ポリゴンで point-in-polygon 判定を行い、所属都道府県を決める。
3. 都道府県ごとに自動レベルを決める。
   - 該当ベニューが 1 件以上ある → `Visited`(3)
   - そのうち宿泊系カテゴリのベニューが 1 件以上ある → `Stayed`(4)
   - 該当ベニューなし → `Unvisited`(0)
4. `PrefectureLevelRepository` から手動上書きを読み、`effectiveLevel` を確定する。
5. `effectiveLevel` の合計をスコアとする。

### 位置判定に `VenueLocation.state` を使わない理由

実データ（エミュレータの debug DB、venue histories 2648 件）を調査した結果、`state` は次のように不安定だった。

| 実際の値 | 件数 |
| --- | --- |
| `Tokyo Prefecture` | 1010 |
| `Hokkaidō` | 240 |
| `Miyagi` | 183 |
| `Tochigi Prefecture` | 179 |
| （JP かつ null） | 21 |
| `沖縄県` | 3 |
| `東京都/北海道` 等の複合値 | 6 |
| `沖縄県伊良部町` | 1 |

英語表記・ローマ字（マクロン有無）・日本語表記・複合値・市町村名混入・null が混在しており、
正規化テーブルで吸収しても取りこぼしが残る。緯度経度は全件に存在するため、座標での判定を唯一の経路とする。

### 宿泊系カテゴリの判定

Foursquare の venue category 名で判定する。対象は Hotel / Hostel / Motel / Bed & Breakfast / Resort /
Inn / Ryokan など宿泊施設に相当するカテゴリ。判定対象のカテゴリ ID・名称は
`StayVenueCategories` として `core/domain` に定数で持ち、ユニットテストで固定する。

誤判定（ホテルのラウンジに立ち寄っただけ等）は起こりうるが、手動上書きで修正できる前提とする。

### 手動上書きの優先順位

`manualLevel` は常に `automaticLevel` より優先する。自動値より低い値への上書きも許可する。
これは「チェックインはしたが実際には乗り換えで通過しただけ」といった実態を表現できるようにするため。

上書きを解除すると `automaticLevel` に戻る。

### 国外の扱い

どの都道府県ポリゴンにも含まれず、フォールバックの距離しきい値も超えたベニューは国外とみなす。
国外ベニューは `VenueLocation.countryCode` ごとに集約し、「訪問国 N ヶ国」のカウンターとして表示する。
レベル分けもスコア加算も行わない。

## 都道府県ポリゴン

### データ

Natural Earth 1:10m Admin 1 – States, Provinces（パブリックドメイン）から日本の 47 都道府県を抽出し、
簡略化した GeoJSON をアプリの `assets/` に同梱する。目標サイズは 200KB 前後。

生成手順は `docs/superpowers/assets/prefectures/` にスクリプトとして残し、取得元 URL とデータバージョンを
README に記録する（既存の `docs/superpowers/assets/icon/` と同じ流儀）。外部ダウンロードを含むため
Renovate では追跡できず、手動更新とすることを README に明記する。

### 判定と描画で同一データを使う

同じポリゴンを point-in-polygon 判定・地図描画・タップのヒットテストで共用する。
判定用と描画用のデータを分けない。

### フォールバック

簡略化により海岸線付近の点がどのポリゴンにも入らない場合がある。
どのポリゴンにも含まれない点は、最も近いポリゴンまでの距離を求め、しきい値（20km）以内ならその都道府県に割り当てる。
しきい値を超えたら国外扱いとする。

## 永続化

Room の既存 `MitsubachiDatabase` に新テーブルを追加する。

```
prefecture_levels(
  foursquare_account_id TEXT NOT NULL,
  prefecture_code       INTEGER NOT NULL,
  level                 INTEGER NOT NULL,
  PRIMARY KEY (foursquare_account_id, prefecture_code)
)
```

アカウント単位で上書きを分離する。DB version は 6 → 7 に上げ、テーブル追加のマイグレーションを書く。
リリースビルドでは既存どおり暗号化 DB に載る。

`PrefectureLevelRepository`（`core/domain/usecase`）が読み書きのインターフェースを定義し、
`core/data` に Room 実装を置く。

## UI

### 配置と導線

`feature/map` モジュール内に `ui/prefectures/` を新設する。
導線は Map 画面（`VenueHistoriesScreen`）の TopBar に切替を置き、「チェックイン地図」と「県踏破度」を行き来する。
ボトムバーの項目は増やさない。

### 画面構成

単一スクロールで上から次の順に並べる。

1. **スコアヘッダー** — `123 / 235` と訪問国カウンター
2. **日本地図** — レベル別に塗り分け
3. **都道府県リスト** — レベル別にグループ化

### Canvas 日本地図

- GeoJSON ポリゴンを Compose `Canvas` の `Path` に変換して塗る。投影と `Path` 生成は composition 外で
  `remember` にキャッシュし、再コンポジションごとに作り直さない
- **沖縄県は左下にインセット表示**する。本州・北海道・九州とスケールを揃えた別枠として描き、枠線で区切る
- 小笠原諸島・南西諸島の離島は描画から省く（判定では東京都・鹿児島県として拾う）
- タップで都道府県を選択し、ボトムシートで 6 段階を選び直せる。ヒットテストは判定と同じ point-in-polygon を使う
- 色はレベル 0〜5 の 6 段階。未踏はサーフェス色、上位ほど彩度を上げる。Material 3 のテーマ色から導出し、
  ライト / ダーク両対応とする

### 都道府県リスト

レベル別にグループ化して表示する。各行タップで地図と同じボトムシートを開く。
自動判定値と上書き値が異なる行には印を付け、上書きを解除できるようにする。

### エラーとローディング

`VenueHistoriesScreenViewModel` と同じ `UiState`（`Loading` / `Success` / `Error`）の形に揃える。
初回はキャッシュ（`FetchPolicy.CacheOrNetwork`）、プルリフレッシュでネットワーク（`FetchPolicy.NetworkOnly`）。
ポリゴン読み込み失敗は `Error` として扱い、リトライ可能にする。

## テスト

### ユニットテスト

- スコア合計の計算（全未踏 = 0、全居住 = 235、混在ケース）
- 手動上書きが自動判定より優先されること（上書き値が自動値より低い場合を含む）
- 上書き解除で自動判定値に戻ること
- 宿泊系カテゴリの判定（該当カテゴリで `Stayed`、非該当で `Visited` 止まり）
- point-in-polygon — 47 都道府県庁所在地の実座標がそれぞれ正しい都道府県に落ちること
- point-in-polygon — 県境付近の代表点、海上の点（フォールバックで拾えること）、国外の点（除外されること）
- GeoJSON パース — 47 件揃っていること、各ポリゴンが閉じていること

### 実機検証

エミュレータ上で mobile-mcp により画面を検証する。地図タップ → ボトムシート → レベル変更 →
スコア更新の一連の操作は録画を証跡として残す。

地図の見た目（配色・沖縄インセットの位置・都道府県の視認性）はレンダリング比較シートを作成してから確定する。

## 判断が必要な残件

- 地図の具体的な配色（レンダリング比較シートで決定する）
- 元ネタ（uub.jp「経県値」）へのクレジットを About 画面に載せるか
