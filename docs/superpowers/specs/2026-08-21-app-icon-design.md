# アプリアイコン設計

- 日付: 2026-08-21
- 対象: `app/src/main/res` 配下のランチャーアイコン一式
- 現状: Android Studio のテンプレート（ドロイドくん）のまま未着手

## 目的

Mitsubachi のランチャーアイコンを、ポップでかわいい独自アイコンに差し替える。
過去の検討では幾何学的なモチーフに寄って「無機質」な仕上がりになったため、今回は擬人化された表情を主役に据える。

## 採用デザイン

正面を向いたデフォルメのミツバチ 1 体を、蜂蜜色の背景に大きく配置する。

### モチーフと造形

- 頭部と胴体を分離しない 1 つの丸い胴体（chibi 表現）とし、上半分に顔、下半分に縞を置く。
- 表情は 大きな黒目 + ハイライト、ゆるいカーブの口、頬の赤み で構成する。
- 触角は ゆるいカーブ + 先端の玉。ドロイドくんのオマージュとして左右対称・上向きを維持する。
  - 検討の結果、膝状触角（実際のミツバチに近い折れ曲がり）は虫らしさが強く出すぎるため不採用とした。
  - 玉なしの直線（純ドロイド型）は 48px で線が消えかけるため不採用とした。
- 羽根は胴体の背後に左右 1 対。白 + 濃茶の輪郭。
- 輪郭線は黒ではなく濃い茶（`#4A2A00`）で統一し、ステッカー的な太線とする。

### 配色

`core/ui/common/.../MitsubachiMaterialColors.kt` の琥珀系パレットから導出し、アプリ本体と地続きにする。

| 用途 | 色 |
| --- | --- |
| 背景グラデーション | `#FFD98A` → `#FFA633`（放射） |
| 背景ハニカム | `#FFFFFF` / opacity 0.30 / stroke 2.6 |
| 背景の円 | `#FFF6E0` / opacity 0.55 |
| 胴体グラデーション | `#FFE380` → `#FFC12E`（縦） |
| 輪郭・縞・触角 | `#4A2A00` |
| 目 | `#3A2000` |
| 頬 | `#FF7A59` / opacity 0.6 |
| 羽根 | `#FFFFFF` / opacity 0.92 |

背景のハニカムは主役ではなく地の模様として不透明度を落とす。前回の「無機質」化は六角形を主役に据えたことが原因であり、格下げして使うことで蜂蜜の文脈だけを足す。

### レイヤー構成

`<adaptive-icon>` の 3 レイヤーに次のとおり割り当てる。

- background: 背景グラデーション + ハニカム + 中央の円
- foreground: ミツバチ本体（触角・羽根・胴体・縞・顔）
- monochrome: 専用の単色シルエット（後述）

中央の円は装飾的な地であり、パララックス時にミツバチと分離しても違和感が小さいため background に置く。

### セーフゾーン

- キャンバスは 108dp。マスク後に保証される可視領域は中央 66dp（`r = 33`）、実際に表示されうるのは中央 72dp（`r = 36`）。
- ミツバチ本体は中心 `(54, 54)` 基準で `scale(0.87)` を適用し、触角の先端と羽根の外端を含む全要素を `r ≒ 32` 以内に収める。
- 円マスク・スクワークルマスクの双方で欠けが出ないことを合格条件とする。
- 背景の意匠も、視認させたい要素は中央 72dp 内に配置する。外周 18dp はマスクで失われる。

### monochrome レイヤー

Android 13 以降のテーマアイコンは drawable のアルファのみを使って単色で塗り潰すため、カラー版の foreground を流用すると顔も縞も失われ、単なる丸い塊になる。

専用の drawable `ic_launcher_monochrome.xml` を新規に用意し、次の方針で描く。

- 胴体・羽根・触角で構成する。触角と羽根は独立した塗りパスとし、胴体は逆回りのサブパスを重ねた 1 本の `pathData`（`fillType` は既定の `nonZero`）で表現する。
- 抜き穴は目 2 つと口のみとする。縞は単色シルエットにすると鼻の下のひげのように見えてしまうため、モノクロレイヤーでは持たない（カラー版の縞はそのまま）。
- 羽根は胴体に密着させず、胴体楕円を 2 単位膨らませた領域を `clip-path` で欠き取り、2 単位分の透明な隙間を空けて分離する。密着させると単色化時に胴体と羽根が地続きの 1 つの塊（マッシュルーム状の輪郭）に見えてしまうため。この `clip-path` は羽根の回転 Group の外側に付与し、ワールド座標のまま（羽根と一緒に回転させない）保つ。
- 内容はカラー版と同様にセーフゾーン内へ収める。
- 抜き穴が潰れないよう、各穴の最小幅はカラー版より太らせる。

`ic_launcher.xml` / `ic_launcher_round.xml` の `<monochrome>` 参照先を、現在の `@drawable/ic_launcher_foreground` からこの新規 drawable に差し替える。

## 成果物

| ファイル | 操作 |
| --- | --- |
| `app/src/main/res/drawable/ic_launcher_background.xml` | 全面差し替え |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | 全面差し替え |
| `app/src/main/res/drawable/ic_launcher_monochrome.xml` | 新規作成 |
| `app/src/main/res/mipmap-anydpi/ic_launcher.xml` | `<monochrome>` 参照先を変更 |
| `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml` | 同上 |
| `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher.webp` | 新アートから再生成（48/72/96/144/192px、スクワークル） |
| `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher_round.webp` | 同上（円形） |

Play Store 用の 512px アイコンはリポジトリに存在しないため、本作業のスコープ外とする。

## 制作フロー

VectorDrawable の path を直接手書きせず、SVG も手書きしない。両方とも
`docs/superpowers/assets/icon/icon_shapes.py` の図形定義（`Shape` / `Group` /
`Layer`）を唯一の原本とし、そこから同時に生成する。検証した見た目と出荷物が
乖離しないようにするための構成であり、SVG→VectorDrawable の手動変換は行わない。

1. `icon_shapes.py` に背景・前景・monochrome の 3 レイヤーを図形定義として記述する。
2. `python3 icon_shapes.py . ../../../../app/src/main/res/drawable` を実行し、
   プレビュー用 SVG（108×108 viewBox）と出荷用 VectorDrawable XML を同時に生成する。
   グラデーションは VectorDrawable 側で `<aapt:attr name="android:fillColor">` +
   `<gradient>` として出力される。
3. `render_preview.sh` が `rsvg-convert` で SVG を PNG にレンダリングし、円 /
   スクワークルのマスクを適用して実表示を確認する。
4. レガシー mipmap は `generate_mipmaps.sh` が合成済み PNG（background +
   foreground をマスク適用）から各密度へ縮小し、`magick` で webp 化する。

生成物である `app/src/main/res/drawable/ic_launcher_{background,foreground,monochrome}.xml`
は手で編集しない。図形定義は `docs/superpowers/assets/icon/icon_shapes.py` に
保管し、将来の再生成を可能にする。詳細は同ディレクトリの `README.md` を参照。

## 検証

`secrets.properties`、`app/google-services.json`、`keystore.properties` がいずれも未配置のため、このワークツリーで APK をビルドできない可能性が高い。検証は次の優先順で行う。

1. レンダリング検証（必須）
   - 432px でのフルキャンバス表示
   - 円マスク / スクワークルマスク適用後の表示（欠けがないこと）
   - 48px 相当での可読性（表情が潰れないこと）
   - monochrome レイヤーを単色で塗り潰した状態のシルエット確認
2. Gradle 検証（到達可能な範囲）
   - `./gradlew :app:lintDebug` あるいはリソース処理タスクを試行する。
   - 秘密情報の不足で失敗する場合はその旨を報告し、レンダリング検証を根拠とする。
3. 実機・エミュレータでのランチャー表示は、ビルドが通る場合にのみ実施する。

before / after は同一条件でレンダリングした比較画像を証跡として添付する。

## スコープ外

- アプリ内アイコン（`core/ui` 配下のアイコン類）の変更
- スプラッシュスクリーンやウィジェットの意匠変更
- Play Store 掲載素材の作成
