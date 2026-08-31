# 県踏破度 実装計画

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Foursquare のチェックイン履歴から都道府県ごとの踏破レベルを 6 段階で算出し、日本地図の塗り分けとスコア（0〜235）で可視化する画面を追加する。

**Architecture:** `core/domain` に純粋な判定ロジック（都道府県 enum・名称解決・point-in-polygon・レベル算出ユースケース）を置き、`core/data` に GeoJSON アセットの読み込みと Room による手動上書きの永続化を置く。UI は `feature/map` に `ui/prefectures/` として追加し、Google Maps ではなく Compose Canvas で日本地図を描く。判定用ポリゴンと描画用ポリゴンは同一データを共用する。

**Tech Stack:** Kotlin / Jetpack Compose (Material 3) / Hilt / Room / kotlinx.serialization / JUnit 5 + kotlin.test + MockK

**設計ドキュメント:** [docs/superpowers/specs/2026-08-31-prefecture-completion-design.md](../specs/2026-08-31-prefecture-completion-design.md)

## Global Constraints

- 呼称は **県踏破度**。「経県値」はアプリの UI 文言に使わない（クレジット表記の中でのみ使う）
- スコアは `effectiveLevel` の総和。満点は 47 × 5 = 235
- レベルは 6 段階: 未踏 0 / 通過 1 / 接地 2 / 訪問 3 / 宿泊 4 / 居住 5
- 手動上書き（`manualLevel`）は常に自動判定（`automaticLevel`）より優先する。自動値より低い値への上書きも許可する
- 都道府県の特定は `state` 文字列の正規化を第一経路、座標の point-in-polygon を第二経路、最寄りポリゴン 20km 以内へのフォールバックを第三経路とする
- `VenueLocation.countryCode` が `JP` 以外のベニューは都道府県判定に一切かけない
- ポリゴンアセットは判定・描画・タップ判定で同一のものを共用する
- レベル配色は固定色を持たず `MaterialTheme.colorScheme` から導出し、ライトとダークで明度の方向を反転させる
- UI 文字列は `values`（英語）・`values-ja`・`values-ko-rKR` の 3 ロケールすべてに追加する（既存モジュールがすべて 3 ロケール揃っているため）
- コード内のコメントは日本語、ログとエラーメッセージは英語
- トップレベルの `const val` と `private val` は SCREAMING_SNAKE_CASE。リポジトリの既存慣習
  （`FOURSQUARE_API_VERSION`、`STAY_RADIUS_METERS` など）であり、detekt の `TopLevelPropertyNaming` と
  `PropertyName` が既定でこれを要求する
- テストメソッド名は英語 camelCase（既存の `ArchitectureTest` に合わせる）。日本語のバッククォート名は
  detekt の `UnnecessaryBackticks` と `FunctionName` のどちらかに必ず抵触し、`detekt.yml` の緩和が必要になるため使わない
- コミットメッセージは Conventional Commits 形式、末尾に `Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>` を付ける
- 検証コマンド: `./gradlew testLocalDebug`、`./gradlew detekt`、`./gradlew lintLocalDebug`

## File Structure

**新規作成**

| パス | 責務 |
| --- | --- |
| `docs/superpowers/assets/prefectures/generate.py` | Natural Earth から都道府県ポリゴンを抽出・簡略化して JSON を出力 |
| `docs/superpowers/assets/prefectures/requirements.txt` | 生成スクリプトの Python 依存（Renovate 追跡用） |
| `docs/superpowers/assets/prefectures/README.md` | 取得元・バージョン・再生成手順 |
| `core/data/src/main/assets/prefectures.json` | 生成済みの都道府県ポリゴン（判定・描画共用） |
| `core/domain/.../model/Prefecture.kt` | 47 都道府県の enum |
| `core/domain/.../model/PrefectureLevel.kt` | 6 段階のレベル enum |
| `core/domain/.../model/PrefectureCompletion.kt` | 1 都道府県分の判定結果 |
| `core/domain/.../model/PrefectureBoundary.kt` | 1 都道府県分のポリゴン |
| `core/domain/.../usecase/PrefectureNameResolver.kt` | `state` 文字列 → `Prefecture` の解決 |
| `core/domain/.../usecase/PrefectureLocator.kt` | 座標 → `Prefecture` の解決（point-in-polygon + 最寄りフォールバック） |
| `core/domain/.../usecase/PrefectureBoundaryRepository.kt` | ポリゴン取得のインターフェース |
| `core/domain/.../usecase/PrefectureLevelRepository.kt` | 手動上書きの読み書きインターフェース |
| `core/domain/.../usecase/StayVenueCategories.kt` | 宿泊系カテゴリの判定 |
| `core/domain/.../model/PrefectureCompletionSummary.kt` | 47 都道府県分の集計結果と訪問国 |
| `core/domain/.../usecase/CalculatePrefectureCompletionsUseCase.kt` | 判定フロー全体 |
| `core/data/.../asset/PrefectureBoundaryAsset.kt` | アセット JSON のスキーマ・ドメイン変換・パーサ |
| `core/data/.../repository/PrefectureBoundaryRepositoryImpl.kt` | アセット読み込み実装 |
| `core/data/.../database/entity/PrefectureLevelOverride.kt` | Room エンティティ |
| `core/data/.../database/dao/PrefectureLevelOverrideDao.kt` | Room DAO |
| `core/data/.../database/migration/Migration6To7.kt` | `prefecture_levels` 追加のマイグレーション |
| `core/data/.../repository/PrefectureLevelRepositoryImpl.kt` | 手動上書きの Room 実装 |
| `core/data/.../di/PrefectureBoundaryRepositoryModule.kt` | ポリゴン読み込み実装の Hilt バインド |
| `core/data/.../di/PrefectureLevelRepositoryModule.kt` | 手動上書き実装の Hilt バインド |
| `feature/map/.../ui/prefectures/PrefectureLevelColors.kt` | レベル配色 |
| `feature/map/.../ui/prefectures/JapanMapProjection.kt` | 緯度経度 → Canvas 座標（沖縄インセット込み） |
| `feature/map/.../ui/prefectures/PrefectureMap.kt` | Canvas 日本地図 |
| `feature/map/.../ui/prefectures/PrefectureLevelSheet.kt` | レベル変更ボトムシート |
| `feature/map/.../ui/prefectures/PrefectureCompletionScreen.kt` | 画面本体 |
| `feature/map/.../ui/prefectures/PrefectureCompletionScreenViewModel.kt` | 状態管理 |

**変更**

| パス | 変更内容 |
| --- | --- |
| `core/data/.../database/MitsubachiDatabase.kt` | version 6 → 7、`PrefectureLevel` エンティティと DAO を追加 |
| `core/data/src/debug/.../PlainMitsubachiDatabaseModule.kt` | `addMigrations(Migration6To7)` |
| `core/data/src/release/.../EncryptedMitsubachiDatabaseModule.kt` | `addMigrations(Migration6To7)` |
| `core/data/.../di/DaoModule.kt` | `PrefectureLevelOverrideDao` の provide を追加 |
| `app/.../RouteKey.kt` | `PrefectureCompletion` ルートを追加 |
| `app/.../App.kt` | ルートの `NavEntry` とボトムバー対象への追加 |
| `feature/map/.../ui/histories/VenueHistoriesScreen.kt` | 県踏破度へ遷移する FAB を追加 |
| `feature/map/src/main/res/values*/strings.xml` | 文字列を 3 ロケールに追加 |
| `feature/map/build.gradle.kts` | 変更不要（`core/domain` に既に依存している） |

---

### Task 1: 都道府県ポリゴンアセットの生成

Natural Earth から 47 都道府県のポリゴンを抽出し、簡略化した JSON をアセットとして生成する。
再生成できるようスクリプトと手順を残す。

**Files:**
- Create: `docs/superpowers/assets/prefectures/generate.py`
- Create: `docs/superpowers/assets/prefectures/requirements.txt`
- Create: `docs/superpowers/assets/prefectures/README.md`
- Create: `core/data/src/main/assets/prefectures.json`（スクリプトの出力）

**Interfaces:**
- Consumes: なし
- Produces: `core/data/src/main/assets/prefectures.json`。スキーマは次のとおり。

```json
{
  "source": "Natural Earth 1:10m Admin 1 – States, Provinces 5.1.1",
  "simplifyTolerance": 0.005,
  "prefectures": [
    { "code": 1, "name": "北海道", "rings": [[[141.0, 45.0], [141.1, 45.0], ...]] }
  ]
}
```

`rings` は `[経度, 緯度]` の配列。各リングは始点と終点が一致する閉リング。座標は小数第 5 位で丸める。

- [ ] **Step 1: 依存とディレクトリを用意する**

```bash
mkdir -p docs/superpowers/assets/prefectures
printf 'pyshp==2.3.1\n' > docs/superpowers/assets/prefectures/requirements.txt
```

- [ ] **Step 2: 生成スクリプトを書く**

`docs/superpowers/assets/prefectures/generate.py`:

```python
"""Natural Earth の admin-1 データから日本の 47 都道府県ポリゴンを抽出して簡略化する。

使い方は同ディレクトリの README.md を参照。
"""

import argparse
import io
import json
import math
import os
import urllib.request
import zipfile

import shapefile

SOURCE_URL = "https://naciscdn.org/naturalearth/10m/cultural/ne_10m_admin_1_states_provinces.zip"
SOURCE_LABEL = "Natural Earth 1:10m Admin 1 - States, Provinces 5.1.1"
SIMPLIFY_TOLERANCE = 0.005
MIN_RING_AREA = 0.0002
COORDINATE_PRECISION = 5


def download(work_dir):
    """シェープファイル一式を work_dir に展開してベース名を返す。"""
    archive = os.path.join(work_dir, "ne_10m_admin_1_states_provinces.zip")
    if not os.path.exists(archive):
        with urllib.request.urlopen(SOURCE_URL) as response:
            payload = response.read()
        with open(archive, "wb") as f:
            f.write(payload)
    with zipfile.ZipFile(io.BytesIO(open(archive, "rb").read())) as z:
        z.extractall(work_dir)
    return os.path.join(work_dir, "ne_10m_admin_1_states_provinces")


def douglas_peucker(points, tolerance):
    """Douglas-Peucker で折れ線を間引く。"""
    if len(points) < 3:
        return points
    x1, y1 = points[0]
    x2, y2 = points[-1]
    dx, dy = x2 - x1, y2 - y1
    denominator = math.hypot(dx, dy)
    max_distance, max_index = 0.0, 0
    for index in range(1, len(points) - 1):
        x0, y0 = points[index]
        if denominator:
            distance = abs(dy * x0 - dx * y0 + x2 * y1 - y2 * x1) / denominator
        else:
            distance = math.hypot(x0 - x1, y0 - y1)
        if distance > max_distance:
            max_distance, max_index = distance, index
    if max_distance > tolerance:
        left = douglas_peucker(points[: max_index + 1], tolerance)
        right = douglas_peucker(points[max_index:], tolerance)
        return left[:-1] + right
    return [points[0], points[-1]]


def ring_area(ring):
    """閉リングの面積 (平方度) を返す。"""
    total = 0.0
    for i in range(len(ring) - 1):
        total += ring[i][0] * ring[i + 1][1] - ring[i + 1][0] * ring[i][1]
    return abs(total) / 2


def build(shapefile_base):
    reader = shapefile.Reader(shapefile_base)
    fields = [f[0] for f in reader.fields[1:]]
    iso_index = fields.index("iso_3166_2")
    country_index = fields.index("iso_a2")
    name_index = fields.index("name_ja")

    prefectures = []
    for shape_record in reader.shapeRecords():
        record = shape_record.record
        if record[country_index] != "JP":
            continue
        code = int(record[iso_index].split("-")[1])
        parts = list(shape_record.shape.parts) + [len(shape_record.shape.points)]
        rings = []
        for start, end in zip(parts, parts[1:]):
            ring = [
                (round(x, COORDINATE_PRECISION), round(y, COORDINATE_PRECISION))
                for x, y in shape_record.shape.points[start:end]
            ]
            if ring_area(ring) < MIN_RING_AREA:
                continue
            simplified = douglas_peucker(ring, SIMPLIFY_TOLERANCE)
            if len(simplified) < 4:
                continue
            if simplified[0] != simplified[-1]:
                simplified.append(simplified[0])
            rings.append([list(point) for point in simplified])
        if not rings:
            continue
        prefectures.append({"code": code, "name": record[name_index], "rings": rings})

    prefectures.sort(key=lambda p: p["code"])
    return prefectures


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--work-dir", default=".work")
    parser.add_argument(
        "--output",
        default="../../../../core/data/src/main/assets/prefectures.json",
    )
    args = parser.parse_args()

    os.makedirs(args.work_dir, exist_ok=True)
    prefectures = build(download(args.work_dir))

    if len(prefectures) != 47:
        raise SystemExit(f"expected 47 prefectures, got {len(prefectures)}")

    document = {
        "source": SOURCE_LABEL,
        "simplifyTolerance": SIMPLIFY_TOLERANCE,
        "prefectures": prefectures,
    }
    payload = json.dumps(document, ensure_ascii=False, separators=(",", ":"))
    os.makedirs(os.path.dirname(os.path.abspath(args.output)), exist_ok=True)
    with open(args.output, "w", encoding="utf-8") as f:
        f.write(payload)

    rings = sum(len(p["rings"]) for p in prefectures)
    points = sum(len(r) for p in prefectures for r in p["rings"])
    print(f"prefectures={len(prefectures)} rings={rings} points={points} bytes={len(payload.encode())}")


if __name__ == "__main__":
    main()
```

- [ ] **Step 3: README を書く**

`docs/superpowers/assets/prefectures/README.md`:

```markdown
# 都道府県ポリゴンの生成

`core/data/src/main/assets/prefectures.json` を生成する。判定 (point-in-polygon)・地図描画・タップ判定で
同じデータを共用する。

## 取得元

- Natural Earth 1:10m Admin 1 – States, Provinces バージョン 5.1.1
- https://naciscdn.org/naturalearth/10m/cultural/ne_10m_admin_1_states_provinces.zip
- パブリックドメイン（Natural Earth の利用条件による）

日本の 47 都道府県が過不足なく含まれ、`iso_3166_2` が `JP-01`〜`JP-47` の形で JIS X 0401 コードと一致する。
日本語名は `name_ja` を使う。

## 再生成

```bash
cd docs/superpowers/assets/prefectures
python -m venv .venv
.venv/bin/pip install -r requirements.txt
.venv/bin/python generate.py
```

出力の想定値は `prefectures=47 rings=135 points=5931 bytes=約125000`。

## Renovate

Natural Earth のデータは URL 直接ダウンロードのため Renovate では追跡できない。
バージョンを上げるときは `generate.py` の `SOURCE_URL` と `SOURCE_LABEL` を手で更新し、再生成する。
Python の依存 (`requirements.txt`) は Renovate の pip-requirements マネージャが追跡する。
```

- [ ] **Step 4: アセットを生成して想定値を確認する**

```bash
cd docs/superpowers/assets/prefectures && python -m venv .venv && .venv/bin/pip install -r requirements.txt && .venv/bin/python generate.py
```

Expected: `prefectures=47 rings=135 points=5931 bytes=` に続く 12 万台の数値が出力される。
47 以外なら `expected 47 prefectures` で異常終了するので、その場合は取得元の変更を疑う。

- [ ] **Step 5: 生成物の妥当性を目視確認する**

```bash
python3 -c "import json;d=json.load(open('core/data/src/main/assets/prefectures.json'));p=d['prefectures'];print(len(p), p[0]['code'], p[0]['name'], p[-1]['code'], p[-1]['name'])"
```

Expected: `47 1 北海道 47 沖縄県`

- [ ] **Step 6: コミット**

```bash
git add docs/superpowers/assets/prefectures core/data/src/main/assets/prefectures.json
git commit -m "feat: 都道府県ポリゴンのアセットと生成スクリプトを追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

なお `.venv` と `.work` はコミットしない。`docs/superpowers/assets/prefectures/.gitignore` に
`.venv/` と `.work/` を書いて一緒にコミットする。

---

### Task 2: ドメインモデル（Prefecture / PrefectureLevel / PrefectureCompletion）

47 都道府県の enum、6 段階のレベル enum、1 都道府県分の判定結果を定義し、スコア計算をテストで固定する。

**Files:**
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/Prefecture.kt`
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureLevel.kt`
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureCompletion.kt`
- Test: `core/domain/src/test/java/blue/starry/mitsubachi/core/domain/model/PrefectureCompletionTest.kt`

**Interfaces:**
- Consumes: なし
- Produces:
  - `enum class Prefecture(val code: Int, val japaneseName: String, val romajiName: String)` — 47 定数。`Prefecture.Companion.fromCode(code: Int): Prefecture?`
  - `enum class PrefectureLevel(val score: Int)` — `Unvisited`(0) / `PassedThrough`(1) / `Landed`(2) / `Visited`(3) / `Stayed`(4) / `Lived`(5)。`PrefectureLevel.Companion.MaxTotalScore: Int` = 235、`PrefectureLevel.Companion.fromScore(score: Int): PrefectureLevel?`
  - `data class PrefectureCompletion(val prefecture: Prefecture, val automaticLevel: PrefectureLevel, val manualLevel: PrefectureLevel?, val venueCount: Int)` — `val effectiveLevel: PrefectureLevel`
  - `val List<PrefectureCompletion>.totalScore: Int`

- [ ] **Step 1: 失敗するテストを書く**

`core/domain/src/test/java/blue/starry/mitsubachi/core/domain/model/PrefectureCompletionTest.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrefectureCompletionTest {
  @Test
  fun allFortySevenPrefecturesAreDefinedWithUniqueCodes() {
    assertEquals(47, Prefecture.entries.size)
    assertEquals((1..47).toList(), Prefecture.entries.map { it.code }.sorted())
  }

  @Test
  fun findsPrefectureByCode() {
    assertEquals(Prefecture.Hokkaido, Prefecture.fromCode(1))
    assertEquals(Prefecture.Tokyo, Prefecture.fromCode(13))
    assertEquals(Prefecture.Okinawa, Prefecture.fromCode(47))
    assertNull(Prefecture.fromCode(0))
    assertNull(Prefecture.fromCode(48))
  }

  @Test
  fun maxTotalScoreIs235() {
    assertEquals(235, PrefectureLevel.MaxTotalScore)
  }

  @Test
  fun scoreIsZeroWhenEveryPrefectureIsUnvisited() {
    val completions = Prefecture.entries.map { completion(it, PrefectureLevel.Unvisited) }

    assertEquals(0, completions.totalScore)
  }

  @Test
  fun scoreIsMaxWhenEveryPrefectureIsLived() {
    val completions = Prefecture.entries.map { completion(it, PrefectureLevel.Lived) }

    assertEquals(PrefectureLevel.MaxTotalScore, completions.totalScore)
  }

  @Test
  fun sumsScoresOfMixedLevels() {
    val completions = listOf(
      completion(Prefecture.Tokyo, PrefectureLevel.Lived),
      completion(Prefecture.Kanagawa, PrefectureLevel.Stayed),
      completion(Prefecture.Chiba, PrefectureLevel.Visited),
      completion(Prefecture.Saitama, PrefectureLevel.PassedThrough),
      completion(Prefecture.Gunma, PrefectureLevel.Unvisited),
    )

    assertEquals(5 + 4 + 3 + 1 + 0, completions.totalScore)
  }

  @Test
  fun manualLevelTakesPrecedenceOverAutomaticLevel() {
    val completion = PrefectureCompletion(
      prefecture = Prefecture.Tokyo,
      automaticLevel = PrefectureLevel.Visited,
      manualLevel = PrefectureLevel.Lived,
      venueCount = 10,
    )

    assertEquals(PrefectureLevel.Lived, completion.effectiveLevel)
  }

  @Test
  fun manualLevelTakesPrecedenceEvenWhenLowerThanAutomatic() {
    val completion = PrefectureCompletion(
      prefecture = Prefecture.Ibaraki,
      automaticLevel = PrefectureLevel.Visited,
      manualLevel = PrefectureLevel.PassedThrough,
      venueCount = 1,
    )

    assertEquals(PrefectureLevel.PassedThrough, completion.effectiveLevel)
    assertEquals(1, listOf(completion).totalScore)
  }

  @Test
  fun usesAutomaticLevelWhenManualLevelIsAbsent() {
    val completion = PrefectureCompletion(
      prefecture = Prefecture.Tokyo,
      automaticLevel = PrefectureLevel.Stayed,
      manualLevel = null,
      venueCount = 3,
    )

    assertEquals(PrefectureLevel.Stayed, completion.effectiveLevel)
  }

  private fun completion(prefecture: Prefecture, level: PrefectureLevel): PrefectureCompletion {
    return PrefectureCompletion(
      prefecture = prefecture,
      automaticLevel = level,
      manualLevel = null,
      venueCount = 0,
    )
  }
}
```

- [ ] **Step 2: テストが失敗することを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*PrefectureCompletionTest*'
```

Expected: FAIL。`Unresolved reference: Prefecture` のコンパイルエラーになる。

- [ ] **Step 3: Prefecture を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/Prefecture.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 47 都道府県。[code] は JIS X 0401 の都道府県コード。
 */
@Immutable
enum class Prefecture(val code: Int, val japaneseName: String, val romajiName: String) {
  Hokkaido(1, "北海道", "hokkaido"),
  Aomori(2, "青森県", "aomori"),
  Iwate(3, "岩手県", "iwate"),
  Miyagi(4, "宮城県", "miyagi"),
  Akita(5, "秋田県", "akita"),
  Yamagata(6, "山形県", "yamagata"),
  Fukushima(7, "福島県", "fukushima"),
  Ibaraki(8, "茨城県", "ibaraki"),
  Tochigi(9, "栃木県", "tochigi"),
  Gunma(10, "群馬県", "gunma"),
  Saitama(11, "埼玉県", "saitama"),
  Chiba(12, "千葉県", "chiba"),
  Tokyo(13, "東京都", "tokyo"),
  Kanagawa(14, "神奈川県", "kanagawa"),
  Niigata(15, "新潟県", "niigata"),
  Toyama(16, "富山県", "toyama"),
  Ishikawa(17, "石川県", "ishikawa"),
  Fukui(18, "福井県", "fukui"),
  Yamanashi(19, "山梨県", "yamanashi"),
  Nagano(20, "長野県", "nagano"),
  Gifu(21, "岐阜県", "gifu"),
  Shizuoka(22, "静岡県", "shizuoka"),
  Aichi(23, "愛知県", "aichi"),
  Mie(24, "三重県", "mie"),
  Shiga(25, "滋賀県", "shiga"),
  Kyoto(26, "京都府", "kyoto"),
  Osaka(27, "大阪府", "osaka"),
  Hyogo(28, "兵庫県", "hyogo"),
  Nara(29, "奈良県", "nara"),
  Wakayama(30, "和歌山県", "wakayama"),
  Tottori(31, "鳥取県", "tottori"),
  Shimane(32, "島根県", "shimane"),
  Okayama(33, "岡山県", "okayama"),
  Hiroshima(34, "広島県", "hiroshima"),
  Yamaguchi(35, "山口県", "yamaguchi"),
  Tokushima(36, "徳島県", "tokushima"),
  Kagawa(37, "香川県", "kagawa"),
  Ehime(38, "愛媛県", "ehime"),
  Kochi(39, "高知県", "kochi"),
  Fukuoka(40, "福岡県", "fukuoka"),
  Saga(41, "佐賀県", "saga"),
  Nagasaki(42, "長崎県", "nagasaki"),
  Kumamoto(43, "熊本県", "kumamoto"),
  Oita(44, "大分県", "oita"),
  Miyazaki(45, "宮崎県", "miyazaki"),
  Kagoshima(46, "鹿児島県", "kagoshima"),
  Okinawa(47, "沖縄県", "okinawa"),
  ;

  companion object {
    private val byCode = entries.associateBy { it.code }

    fun fromCode(code: Int): Prefecture? {
      return byCode[code]
    }
  }
}
```

- [ ] **Step 4: PrefectureLevel を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureLevel.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 都道府県との関わりの深さ。[score] がそのまま得点になる。
 */
@Immutable
enum class PrefectureLevel(val score: Int) {
  Unvisited(0),
  PassedThrough(1),
  Landed(2),
  Visited(3),
  Stayed(4),
  Lived(5),
  ;

  companion object {
    /** 47 都道府県すべてが [Lived] のときの得点。 */
    val MaxTotalScore: Int = Prefecture.entries.size * Lived.score

    private val byScore = entries.associateBy { it.score }

    fun fromScore(score: Int): PrefectureLevel? {
      return byScore[score]
    }
  }
}
```

- [ ] **Step 5: PrefectureCompletion を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureCompletion.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 1 都道府県分の踏破状況。
 *
 * @param automaticLevel チェックイン履歴から自動判定したレベル
 * @param manualLevel ユーザーが手動で設定したレベル。未設定なら null
 * @param venueCount 自動判定の根拠になったベニュー数
 */
@Immutable
data class PrefectureCompletion(
  val prefecture: Prefecture,
  val automaticLevel: PrefectureLevel,
  val manualLevel: PrefectureLevel?,
  val venueCount: Int,
) {
  /** 手動上書きがあればそれを、なければ自動判定を採用する。上書きが自動判定より低くても上書きを優先する。 */
  val effectiveLevel: PrefectureLevel
    get() = manualLevel ?: automaticLevel
}

val List<PrefectureCompletion>.totalScore: Int
  get() = sumOf { it.effectiveLevel.score }
```

- [ ] **Step 6: テストが通ることを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*PrefectureCompletionTest*'
```

Expected: PASS（8 tests）

- [ ] **Step 7: コミット**

```bash
git add core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model core/domain/src/test
git commit -m "feat: 県踏破度のドメインモデルを追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 3: PrefectureNameResolver（`state` 文字列の正規化）

Foursquare の `VenueLocation.state` を都道府県に解決する。実データの表記ゆれを吸収し、解決できない値は null を返す。

**Files:**
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureNameResolver.kt`
- Test: `core/domain/src/test/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureNameResolverTest.kt`

**Interfaces:**
- Consumes: `Prefecture`（Task 2）
- Produces: `object PrefectureNameResolver { fun resolve(state: String?): Prefecture? }`

- [ ] **Step 1: 失敗するテストを書く**

`core/domain/src/test/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureNameResolverTest.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PrefectureNameResolverTest {
  @Test
  fun resolvesEnglishNameWithPrefectureSuffix() {
    // 実データで最多の表記
    assertEquals(Prefecture.Tokyo, PrefectureNameResolver.resolve("Tokyo Prefecture"))
    assertEquals(Prefecture.Tochigi, PrefectureNameResolver.resolve("Tochigi Prefecture"))
    assertEquals(Prefecture.Kyoto, PrefectureNameResolver.resolve("Kyoto Prefecture"))
  }

  @Test
  fun resolvesEnglishNameWithoutSuffix() {
    assertEquals(Prefecture.Miyagi, PrefectureNameResolver.resolve("Miyagi"))
    assertEquals(Prefecture.Chiba, PrefectureNameResolver.resolve("Chiba"))
    assertEquals(Prefecture.Hyogo, PrefectureNameResolver.resolve("Hyogo"))
  }

  @Test
  fun resolvesRomajiNameWithMacron() {
    // 実データに Hokkaidō が 240 件ある
    assertEquals(Prefecture.Hokkaido, PrefectureNameResolver.resolve("Hokkaidō"))
    assertEquals(Prefecture.Osaka, PrefectureNameResolver.resolve("Ōsaka"))
    assertEquals(Prefecture.Kochi, PrefectureNameResolver.resolve("Kōchi"))
  }

  @Test
  fun resolvesJapaneseName() {
    assertEquals(Prefecture.Okinawa, PrefectureNameResolver.resolve("沖縄県"))
    assertEquals(Prefecture.Hokkaido, PrefectureNameResolver.resolve("北海道"))
    assertEquals(Prefecture.Tokyo, PrefectureNameResolver.resolve("東京都"))
  }

  @Test
  fun resolvesJapaneseNameFollowedByMunicipalityByPrefix() {
    // 実データに 沖縄県伊良部町 がある
    assertEquals(Prefecture.Okinawa, PrefectureNameResolver.resolve("沖縄県伊良部町"))
  }

  @Test
  fun doesNotResolveSlashSeparatedCompositeValue() {
    // 実データに 東京都_北海道 のような値がある。どちらかに寄せず座標判定へ回す
    assertNull(PrefectureNameResolver.resolve("東京都/北海道"))
    assertNull(PrefectureNameResolver.resolve("千葉県/東京都"))
    assertNull(PrefectureNameResolver.resolve("群馬県/栃木県"))
  }

  @Test
  fun doesNotResolveNullOrBlank() {
    assertNull(PrefectureNameResolver.resolve(null))
    assertNull(PrefectureNameResolver.resolve(""))
    assertNull(PrefectureNameResolver.resolve("   "))
  }

  @Test
  fun doesNotResolveNonJapaneseState() {
    assertNull(PrefectureNameResolver.resolve("Seoul"))
    assertNull(PrefectureNameResolver.resolve("CA"))
    assertNull(PrefectureNameResolver.resolve("City of Manila"))
  }

  @Test
  fun prefectureNameDataIsComplete() {
    // resolve に自分自身のフィールドを戻す往復テストは typo を検出できないため、データ側を検査する
    val romajiNames = Prefecture.entries.map { it.romajiName }
    assertEquals(romajiNames.size, romajiNames.toSet().size, "romajiName is duplicated")

    for (prefecture in Prefecture.entries) {
      assertTrue(
        prefecture.romajiName.all { it in 'a'..'z' },
        "${'$'}{prefecture.name} has a non lowercase ascii romajiName: ${'$'}{prefecture.romajiName}",
      )
      assertTrue(
        prefecture.japaneseName.last() in "都道府県",
        "${'$'}{prefecture.name} has an unexpected japaneseName: ${'$'}{prefecture.japaneseName}",
      )
    }
  }
}
```

- [ ] **Step 2: テストが失敗することを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*PrefectureNameResolverTest*'
```

Expected: FAIL。`Unresolved reference: PrefectureNameResolver`

- [ ] **Step 3: 実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureNameResolver.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import java.text.Normalizer

/**
 * Foursquare の `VenueLocation.state` を都道府県に解決する。
 *
 * 実データの state は英語表記 (Tokyo Prefecture)、マクロンつきローマ字 (Hokkaidō)、日本語表記 (沖縄県)、
 * 市町村名の混入 (沖縄県伊良部町)、複数県のスラッシュ区切り (東京都/北海道) が混在している。
 * 解決できない値は null を返し、呼び出し側で座標判定にフォールバックする。
 */
object PrefectureNameResolver {
  private val byRomaji = Prefecture.entries.associateBy { it.romajiName }

  fun resolve(state: String?): Prefecture? {
    val trimmed = state?.trim().orEmpty()
    if (trimmed.isEmpty()) {
      return null
    }

    // 複数県を跨ぐ値はどちらか一方に寄せられないため、座標判定に委ねる
    if (trimmed.contains('/')) {
      return null
    }

    // 日本語表記は市町村名が続くことがあるので前方一致で判定する
    Prefecture.entries.firstOrNull { trimmed.startsWith(it.japaneseName) }?.also {
      return it
    }

    return byRomaji[normalizeRomaji(trimmed)]
  }

  private fun normalizeRomaji(value: String): String {
    return Normalizer.normalize(value, Normalizer.Form.NFKD)
      .filter { !it.isMarkCharacter() }
      .lowercase()
      .replace("prefecture", "")
      .replace("-ken", "")
      .filter { !it.isWhitespace() && it != '\'' && it != '’' }
  }

  private fun Char.isMarkCharacter(): Boolean {
    val type = Character.getType(this).toByte()
    return type == Character.NON_SPACING_MARK ||
      type == Character.COMBINING_SPACING_MARK ||
      type == Character.ENCLOSING_MARK
  }
}
```

- [ ] **Step 4: テストが通ることを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*PrefectureNameResolverTest*'
```

Expected: PASS（9 tests）

- [ ] **Step 5: コミット**

```bash
git add core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureNameResolver.kt core/domain/src/test/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureNameResolverTest.kt
git commit -m "feat: state 文字列から都道府県を解決する PrefectureNameResolver を追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 4: PrefectureBoundary と PrefectureLocator（座標判定）

ポリゴンのドメインモデルと、座標から都道府県を引く point-in-polygon を実装する。
合成ポリゴンで境界条件を固める。実アセットを使った検証は Task 5 で行う。

**Files:**
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureBoundary.kt`
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureLocator.kt`
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureBoundaryRepository.kt`
- Test: `core/domain/src/test/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureLocatorTest.kt`

**Interfaces:**
- Consumes: `Prefecture`（Task 2）
- Produces:
  - `data class PrefectureBoundary(val prefecture: Prefecture, val rings: List<List<DoubleArray>>)` — `rings` は閉リングの配列、各点は `doubleArrayOf(経度, 緯度)`
  - `class PrefectureLocator(private val boundaries: List<PrefectureBoundary>) { fun locate(latitude: Double, longitude: Double): Prefecture? }`
  - `interface PrefectureBoundaryRepository { suspend fun findAll(): List<PrefectureBoundary> }`
  - `const val FALLBACK_DISTANCE_KILOMETERS = 20.0`

- [ ] **Step 1: 失敗するテストを書く**

`core/domain/src/test/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureLocatorTest.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PrefectureLocatorTest {
  // 経度 139..140 / 緯度 35..36 の正方形を東京都に、経度 140..141 の隣接正方形を千葉県に見立てる
  private val locator = PrefectureLocator(
    listOf(
      PrefectureBoundary(Prefecture.Tokyo, listOf(square(139.0, 35.0, 140.0, 36.0))),
      PrefectureBoundary(Prefecture.Chiba, listOf(square(140.0, 35.0, 141.0, 36.0))),
    ),
  )

  @Test
  fun resolvesPointInsidePolygon() {
    assertEquals(Prefecture.Tokyo, locator.locate(latitude = 35.5, longitude = 139.5))
    assertEquals(Prefecture.Chiba, locator.locate(latitude = 35.5, longitude = 140.5))
  }

  @Test
  fun resolvesPointOutsidePolygonWithinTwentyKilometers() {
    // 経度 138.9 は東京ポリゴンの西側 0.1 度 (約 9km) の位置
    assertEquals(Prefecture.Tokyo, locator.locate(latitude = 35.5, longitude = 138.9))
  }

  @Test
  fun doesNotResolvePointFartherThanTwentyKilometers() {
    // 経度 138.0 は東京ポリゴンから約 90km 西
    assertNull(locator.locate(latitude = 35.5, longitude = 138.0))
    // 完全な国外
    assertNull(locator.locate(latitude = 37.5665, longitude = 126.9780))
    assertNull(locator.locate(latitude = 37.7749, longitude = -122.4194))
  }

  @Test
  fun resolvesAdjacentPolygonsWithoutOverlap() {
    assertEquals(Prefecture.Tokyo, locator.locate(latitude = 35.5, longitude = 139.99))
    assertEquals(Prefecture.Chiba, locator.locate(latitude = 35.5, longitude = 140.01))
  }

  @Test
  fun resolvesNothingWhenNoBoundaryIsGiven() {
    assertNull(PrefectureLocator(emptyList()).locate(latitude = 35.5, longitude = 139.5))
  }

  private fun square(
    west: Double,
    south: Double,
    east: Double,
    north: Double,
  ): List<DoubleArray> {
    return listOf(
      doubleArrayOf(west, south),
      doubleArrayOf(east, south),
      doubleArrayOf(east, north),
      doubleArrayOf(west, north),
      doubleArrayOf(west, south),
    )
  }
}
```

- [ ] **Step 2: テストが失敗することを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*PrefectureLocatorTest*'
```

Expected: FAIL。`Unresolved reference: PrefectureBoundary`

- [ ] **Step 3: PrefectureBoundary を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureBoundary.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 1 都道府県分の境界ポリゴン。
 *
 * @param rings 閉リングの配列。各点は `doubleArrayOf(経度, 緯度)`。始点と終点は一致する
 */
@Immutable
data class PrefectureBoundary(
  val prefecture: Prefecture,
  val rings: List<List<DoubleArray>>,
) {
  /** リングごとの外接矩形 (west, south, east, north)。判定の枝刈りに使う。 */
  val boundingBoxes: List<DoubleArray> = rings.map { ring ->
    doubleArrayOf(
      ring.minOf { it[0] },
      ring.minOf { it[1] },
      ring.maxOf { it[0] },
      ring.maxOf { it[1] },
    )
  }
}
```

- [ ] **Step 4: PrefectureLocator を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureLocator.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sqrt

/** ポリゴン外の点を最寄りの都道府県に寄せる上限距離 (km)。 */
const val FALLBACK_DISTANCE_KILOMETERS = 20.0

private const val KILOMETERS_PER_DEGREE = 111.0

/**
 * 座標から都道府県を引く。
 *
 * ポリゴンは簡略化されているため海岸線付近の点が外れることがある。
 * どのポリゴンにも入らない点は最寄りのポリゴンまでの距離を測り、[FALLBACK_DISTANCE_KILOMETERS] 以内なら
 * その都道府県に寄せる。それを超えたら null を返す。
 */
class PrefectureLocator(private val boundaries: List<PrefectureBoundary>) {
  fun locate(latitude: Double, longitude: Double): Prefecture? {
    return locateInside(latitude, longitude) ?: locateNearest(latitude, longitude)
  }

  private fun locateInside(latitude: Double, longitude: Double): Prefecture? {
    for (boundary in boundaries) {
      for (index in boundary.rings.indices) {
        val box = boundary.boundingBoxes[index]
        if (longitude < box[0] || longitude > box[2] || latitude < box[1] || latitude > box[3]) {
          continue
        }
        if (contains(boundary.rings[index], longitude, latitude)) {
          return boundary.prefecture
        }
      }
    }
    return null
  }

  private fun locateNearest(latitude: Double, longitude: Double): Prefecture? {
    // 緯度による経度の縮尺を補正して比較する
    val longitudeScale = cos(Math.toRadians(latitude))
    var nearest: Prefecture? = null
    var nearestSquaredDegrees = Double.MAX_VALUE

    for (boundary in boundaries) {
      for (ring in boundary.rings) {
        // 頂点だけでなく辺 (線分) への距離を測る。頂点間が長い簡略化ポリゴンでは
        // 頂点距離だと辺のすぐ横の点を実際よりはるかに遠いと誤判定する
        for (index in 0 until ring.size - 1) {
          val squared = squaredDistanceToSegment(
            longitude = longitude,
            latitude = latitude,
            longitudeScale = longitudeScale,
            from = ring[index],
            to = ring[index + 1],
          )
          if (squared < nearestSquaredDegrees) {
            nearestSquaredDegrees = squared
            nearest = boundary.prefecture
          }
        }
      }
    }

    if (nearest == null) {
      return null
    }
    val kilometers = sqrt(nearestSquaredDegrees) * KILOMETERS_PER_DEGREE
    return nearest.takeIf { kilometers <= FALLBACK_DISTANCE_KILOMETERS }
  }

  // 点と線分の距離の 2 乗を度単位で返す
  private fun squaredDistanceToSegment(
    longitude: Double,
    latitude: Double,
    longitudeScale: Double,
    from: DoubleArray,
    to: DoubleArray,
  ): Double {
    val x0 = longitude * longitudeScale
    val y0 = latitude
    val x1 = from[0] * longitudeScale
    val y1 = from[1]
    val x2 = to[0] * longitudeScale
    val y2 = to[1]

    val dx = x2 - x1
    val dy = y2 - y1
    val lengthSquared = dx * dx + dy * dy

    // 線分が退化している場合は端点との距離を返す
    val t = if (lengthSquared == 0.0) {
      0.0
    } else {
      (((x0 - x1) * dx + (y0 - y1) * dy) / lengthSquared).coerceIn(0.0, 1.0)
    }

    val projectedX = x1 + t * dx
    val projectedY = y1 + t * dy
    val distanceX = x0 - projectedX
    val distanceY = y0 - projectedY
    return distanceX * distanceX + distanceY * distanceY
  }

  private fun contains(ring: List<DoubleArray>, x: Double, y: Double): Boolean {
    var inside = false
    var j = ring.lastIndex
    for (i in ring.indices) {
      val (xi, yi) = ring[i].let { it[0] to it[1] }
      val (xj, yj) = ring[j].let { it[0] to it[1] }
      if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
        inside = !inside
      }
      j = i
    }
    return inside
  }
}
```

`hypot` は使わないので import から外すこと。実装後に `./gradlew :core:domain:detekt` で未使用 import が
検出されないことを確認する。

距離は頂点ではなく**辺 (線分) への距離**で測る。簡略化されたポリゴンは頂点間が長く、頂点距離だけで測ると
辺のすぐ横にある点を実際よりはるかに遠いと誤判定する（テストの「20km 以内」ケースは辺への距離で約 9km、
頂点への距離だと約 56km になる）。

この変更でフォールバックは頂点距離のときより緩くなる。Task 5 の実アセットを使った国外座標のテスト
（ソウル・サンフランシスコ・台北・マニラが null を返すこと）が、緩めすぎていないことの実測ガードになる。

- [ ] **Step 5: PrefectureBoundaryRepository を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureBoundaryRepository.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary

interface PrefectureBoundaryRepository {
  /** 47 都道府県分の境界ポリゴンを返す。読み込みに失敗した場合は例外を投げる。 */
  suspend fun findAll(): List<PrefectureBoundary>
}
```

- [ ] **Step 6: テストが通ることを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*PrefectureLocatorTest*'
```

Expected: PASS（5 tests）

- [ ] **Step 7: コミット**

```bash
git add core/domain/src/main/java/blue/starry/mitsubachi/core/domain core/domain/src/test
git commit -m "feat: 座標から都道府県を引く PrefectureLocator を追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 5: アセットの読み込みと実データによる判定検証

`prefectures.json` をパースして `PrefectureBoundary` に変換し、Hilt から注入できるようにする。
実アセットを使って 47 都道府県の代表都市が正しく判定されることを固定する。

**Files:**
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/asset/PrefectureBoundaryAsset.kt`
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/repository/PrefectureBoundaryRepositoryImpl.kt`
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/di/PrefectureBoundaryRepositoryModule.kt`
- Test: `core/data/src/test/java/blue/starry/mitsubachi/core/data/asset/PrefectureBoundaryAssetTest.kt`

**Interfaces:**
- Consumes: `Prefecture`、`PrefectureBoundary`、`PrefectureLocator`、`PrefectureBoundaryRepository`（Task 2・4）、`core/data/src/main/assets/prefectures.json`（Task 1）
- Produces:
  - `@Serializable internal data class PrefectureBoundaryAsset(val source: String, val simplifyTolerance: Double, val prefectures: List<Entry>)` — `Entry(val code: Int, val name: String, val rings: List<List<List<Double>>>)`
  - `internal fun PrefectureBoundaryAsset.toDomain(): List<PrefectureBoundary>`
  - `internal object PrefectureBoundaryParser { fun parse(json: String): List<PrefectureBoundary> }`
  - `PrefectureBoundaryRepositoryImpl : PrefectureBoundaryRepository` — 結果をメモリにキャッシュする

- [ ] **Step 1: 失敗するテストを書く**

`core/data/src/test/java/blue/starry/mitsubachi/core/data/asset/PrefectureBoundaryAssetTest.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.asset

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLocator
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PrefectureBoundaryAssetTest {
  private val boundaries by lazy {
    PrefectureBoundaryParser.parse(File("src/main/assets/prefectures.json").readText())
  }
  private val locator by lazy { PrefectureLocator(boundaries) }

  @Test
  fun assetContainsExactlyFortySevenPrefectures() {
    assertEquals(47, boundaries.size)
    assertEquals(Prefecture.entries.toSet(), boundaries.map { it.prefecture }.toSet())
  }

  @Test
  fun everyRingIsClosedAndHasAtLeastFourPoints() {
    for (boundary in boundaries) {
      assertTrue(boundary.rings.isNotEmpty(), "${boundary.prefecture} has no ring")
      for (ring in boundary.rings) {
        assertTrue(ring.size >= 4, "${boundary.prefecture} has a ring with ${ring.size} points")
        assertEquals(
          ring.first().toList(),
          ring.last().toList(),
          "${boundary.prefecture} has an unclosed ring",
        )
      }
    }
  }

  @Test
  fun everyRepresentativeCityResolvesToItsOwnPrefecture() {
    for (fixture in cityFixtures) {
      assertEquals(
        fixture.prefecture,
        locator.locate(latitude = fixture.latitude, longitude = fixture.longitude),
        "${fixture.city} should be in ${fixture.prefecture}",
      )
    }
  }

  @Test
  fun resolvesOffshorePointToNearestPrefecture() {
    // 東京湾上。20km 以内に陸地がある
    assertTrue(locator.locate(latitude = 35.45, longitude = 139.85) != null)
  }

  @Test
  fun doesNotResolvePointOutsideJapan() {
    assertNull(locator.locate(latitude = 37.5665, longitude = 126.9780)) // ソウル
    assertNull(locator.locate(latitude = 37.7749, longitude = -122.4194)) // サンフランシスコ
    assertNull(locator.locate(latitude = 25.0330, longitude = 121.5654)) // 台北
    assertNull(locator.locate(latitude = 14.5995, longitude = 120.9842)) // マニラ
  }

  private data class CityFixture(
    val prefecture: Prefecture,
    val city: String,
    val latitude: Double,
    val longitude: Double,
  )

  private companion object {
    val cityFixtures = listOf(
      CityFixture(Prefecture.Hokkaido, "Sapporo", 43.0769, 141.3381),
      CityFixture(Prefecture.Aomori, "Aomori", 40.825, 140.71),
      CityFixture(Prefecture.Iwate, "Morioka", 39.72, 141.13),
      CityFixture(Prefecture.Miyagi, "Sendai", 38.2684, 140.8697),
      CityFixture(Prefecture.Akita, "Akita", 39.71, 140.09),
      CityFixture(Prefecture.Yamagata, "Yamagata", 38.2705, 140.32),
      CityFixture(Prefecture.Fukushima, "Iwaki", 37.0553, 140.89),
      CityFixture(Prefecture.Ibaraki, "Mito", 36.3704, 140.48),
      CityFixture(Prefecture.Tochigi, "Utsunomiya", 36.55, 139.87),
      CityFixture(Prefecture.Gunma, "Maebashi", 36.3927, 139.0727),
      CityFixture(Prefecture.Saitama, "Kawagoe", 35.9177, 139.4911),
      CityFixture(Prefecture.Chiba, "Chiba", 35.6074, 140.1065),
      CityFixture(Prefecture.Tokyo, "Tokyo", 35.687, 139.7495),
      CityFixture(Prefecture.Kanagawa, "Yokohama", 35.4307, 139.602),
      CityFixture(Prefecture.Niigata, "Niigata", 37.92, 139.04),
      CityFixture(Prefecture.Toyama, "Toyama", 36.7, 137.23),
      CityFixture(Prefecture.Ishikawa, "Kanazawa", 36.56, 136.64),
      CityFixture(Prefecture.Fukui, "Fukui", 36.0704, 136.22),
      CityFixture(Prefecture.Yamanashi, "Kofu", 35.6504, 138.5833),
      CityFixture(Prefecture.Nagano, "Nagano", 36.65, 138.17),
      CityFixture(Prefecture.Gifu, "Gifu", 35.4231, 136.7628),
      CityFixture(Prefecture.Shizuoka, "Hamamatsu", 34.7181, 137.7327),
      CityFixture(Prefecture.Aichi, "Nagoya", 35.1569, 136.913),
      CityFixture(Prefecture.Mie, "Tsu", 34.7171, 136.5167),
      CityFixture(Prefecture.Shiga, "Otsu", 35.0064, 135.8674),
      CityFixture(Prefecture.Kyoto, "Kyoto", 35.0319, 135.7481),
      CityFixture(Prefecture.Osaka, "Osaka", 34.6911, 135.5038),
      CityFixture(Prefecture.Hyogo, "Kobe", 34.68, 135.17),
      CityFixture(Prefecture.Nara, "Nara", 34.6851, 135.8048),
      CityFixture(Prefecture.Wakayama, "Wakayama", 34.2231, 135.1677),
      CityFixture(Prefecture.Tottori, "Tottori", 35.5004, 134.2333),
      CityFixture(Prefecture.Shimane, "Matsue", 35.467, 133.0666),
      CityFixture(Prefecture.Okayama, "Okayama", 34.672, 133.9171),
      CityFixture(Prefecture.Hiroshima, "Hiroshima", 34.3898, 132.441),
      CityFixture(Prefecture.Yamaguchi, "Shimonoseki", 33.9654, 130.9454),
      CityFixture(Prefecture.Tokushima, "Tokushima", 34.0674, 134.5525),
      CityFixture(Prefecture.Kagawa, "Takamatsu", 34.3447, 134.0448),
      CityFixture(Prefecture.Ehime, "Matsuyama", 33.8455, 132.7658),
      CityFixture(Prefecture.Kochi, "Kochi", 33.5624, 133.5375),
      CityFixture(Prefecture.Fukuoka, "Fukuoka", 33.597, 130.4081),
      CityFixture(Prefecture.Saga, "Saga", 33.2494, 130.2988),
      CityFixture(Prefecture.Nagasaki, "Nagasaki", 32.765, 129.885),
      CityFixture(Prefecture.Kumamoto, "Kumamoto", 32.8009, 130.7006),
      CityFixture(Prefecture.Oita, "Oita", 33.2432, 131.5979),
      CityFixture(Prefecture.Miyazaki, "Miyazaki", 31.9182, 131.4184),
      CityFixture(Prefecture.Kagoshima, "Kagoshima", 31.586, 130.5611),
      CityFixture(Prefecture.Okinawa, "Naha", 26.2072, 127.673),
    )
  }
}
```

- [ ] **Step 2: テストが失敗することを確認する**

```bash
./gradlew :core:data:testDebugUnitTest --tests '*PrefectureBoundaryAssetTest*'
```

Expected: FAIL。`Unresolved reference: PrefectureBoundaryParser`

- [ ] **Step 3: アセットのスキーマとパーサを実装する**

`core/data/src/main/java/blue/starry/mitsubachi/core/data/asset/PrefectureBoundaryAsset.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.asset

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * `assets/prefectures.json` のスキーマ。
 * 生成手順は docs/superpowers/assets/prefectures/README.md を参照。
 */
@Serializable
internal data class PrefectureBoundaryAsset(
  val source: String,
  val simplifyTolerance: Double,
  val prefectures: List<Entry>,
) {
  @Serializable
  internal data class Entry(
    val code: Int,
    val name: String,
    // [経度, 緯度] の点からなる閉リングの配列
    val rings: List<List<List<Double>>>,
  )
}

internal fun PrefectureBoundaryAsset.toDomain(): List<PrefectureBoundary> {
  return prefectures.mapNotNull { entry ->
    val prefecture = Prefecture.fromCode(entry.code) ?: return@mapNotNull null
    PrefectureBoundary(
      prefecture = prefecture,
      rings = entry.rings.map { ring -> ring.map { doubleArrayOf(it[0], it[1]) } },
    )
  }
}

internal object PrefectureBoundaryParser {
  private val json = Json { ignoreUnknownKeys = true }

  fun parse(text: String): List<PrefectureBoundary> {
    return json.decodeFromString<PrefectureBoundaryAsset>(text).toDomain()
  }
}
```

- [ ] **Step 4: テストが通ることを確認する**

```bash
./gradlew :core:data:testDebugUnitTest --tests '*PrefectureBoundaryAssetTest*'
```

Expected: PASS（5 tests）

もし `47 都道府県の代表都市` が落ちた場合、アセットの簡略化が想定より粗い。
Task 1 の `SIMPLIFY_TOLERANCE` を 0.002 に下げて再生成し、再度実行する。

`File("src/main/assets/prefectures.json")` は Gradle のユニットテストの作業ディレクトリが
モジュールディレクトリであることを前提にしている。`FileNotFoundException` になった場合は、
テストを緩めるのではなく作業ディレクトリを確認し、モジュールルートからの相対パスで解決し直すこと。

- [ ] **Step 5: リポジトリ実装を書く**

`core/data/src/main/java/blue/starry/mitsubachi/core/data/repository/PrefectureBoundaryRepositoryImpl.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.repository

import android.content.Context
import blue.starry.mitsubachi.core.data.asset.PrefectureBoundaryParser
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.usecase.PrefectureBoundaryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val ASSET_FILE_NAME = "prefectures.json"

@Singleton
internal class PrefectureBoundaryRepositoryImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : PrefectureBoundaryRepository {
  private val mutex = Mutex()
  private var cache: List<PrefectureBoundary>? = null

  override suspend fun findAll(): List<PrefectureBoundary> {
    cache?.also {
      return it
    }

    return mutex.withLock {
      cache ?: withContext(Dispatchers.IO) {
        val text = context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
        PrefectureBoundaryParser.parse(text)
      }.also {
        cache = it
      }
    }
  }
}
```

- [ ] **Step 6: Hilt モジュールを書く**

`core/data/src/main/java/blue/starry/mitsubachi/core/data/di/PrefectureBoundaryRepositoryModule.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.repository.PrefectureBoundaryRepositoryImpl
import blue.starry.mitsubachi.core.domain.usecase.PrefectureBoundaryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PrefectureBoundaryRepositoryModule {
  @Binds
  @Singleton
  internal abstract fun bind(impl: PrefectureBoundaryRepositoryImpl): PrefectureBoundaryRepository
}
```

- [ ] **Step 7: ビルドとテストを通す**

```bash
./gradlew :core:data:testDebugUnitTest :core:data:detekt
```

Expected: PASS

- [ ] **Step 8: コミット**

```bash
git add core/data/src/main/java/blue/starry/mitsubachi/core/data core/data/src/test
git commit -m "feat: 都道府県ポリゴンアセットの読み込みを追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 6: 手動上書きの永続化（Room）

`prefecture_levels` テーブルを追加し、アカウント単位で手動上書きを保存する。
既存データを壊さないよう version 6 → 7 のマイグレーションを必ず書く。

**Files:**
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureLevelRepository.kt`
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/database/entity/PrefectureLevelOverride.kt`
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/database/dao/PrefectureLevelOverrideDao.kt`
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/database/migration/Migration6To7.kt`
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/repository/PrefectureLevelRepositoryImpl.kt`
- Create: `core/data/src/main/java/blue/starry/mitsubachi/core/data/di/PrefectureLevelRepositoryModule.kt`
- Modify: `core/data/src/main/java/blue/starry/mitsubachi/core/data/database/MitsubachiDatabase.kt`
- Modify: `core/data/src/main/java/blue/starry/mitsubachi/core/data/di/DaoModule.kt`
- Modify: `core/data/src/debug/java/blue/starry/mitsubachi/core/data/di/PlainMitsubachiDatabaseModule.kt`
- Modify: `core/data/src/release/java/blue/starry/mitsubachi/core/data/di/EncryptedMitsubachiDatabaseModule.kt`

**Interfaces:**
- Consumes: `Prefecture`、`PrefectureLevel`（Task 2）、`FoursquareAccount`（既存）
- Produces:
  - `interface PrefectureLevelRepository { fun flow(account: FoursquareAccount): Flow<Map<Prefecture, PrefectureLevel>>; suspend fun set(account: FoursquareAccount, prefecture: Prefecture, level: PrefectureLevel); suspend fun clear(account: FoursquareAccount, prefecture: Prefecture) }`

- [ ] **Step 1: ドメイン側のインターフェースを書く**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/PrefectureLevelRepository.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import kotlinx.coroutines.flow.Flow

/** 都道府県ごとの手動上書きを読み書きする。 */
interface PrefectureLevelRepository {
  fun flow(account: FoursquareAccount): Flow<Map<Prefecture, PrefectureLevel>>

  suspend fun set(account: FoursquareAccount, prefecture: Prefecture, level: PrefectureLevel)

  /** 手動上書きを取り消し、自動判定に戻す。 */
  suspend fun clear(account: FoursquareAccount, prefecture: Prefecture)
}
```

- [ ] **Step 2: エンティティと DAO を書く**

`core/data/src/main/java/blue/starry/mitsubachi/core/data/database/entity/PrefectureLevelOverride.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
  tableName = "prefecture_levels",
  primaryKeys = ["foursquare_account_id", "prefecture_code"],
)
data class PrefectureLevelOverride(
  @ColumnInfo("foursquare_account_id") val foursquareAccountId: String,
  // JIS X 0401 の都道府県コード (1..47)
  @ColumnInfo("prefecture_code") val prefectureCode: Int,
  // PrefectureLevel.score (0..5)
  @ColumnInfo("level") val level: Int,
)
```

`core/data/src/main/java/blue/starry/mitsubachi/core/data/database/dao/PrefectureLevelOverrideDao.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.core.data.database.entity.PrefectureLevelOverride
import kotlinx.coroutines.flow.Flow

@Dao
interface PrefectureLevelOverrideDao {
  @Query("SELECT * FROM `prefecture_levels` WHERE `foursquare_account_id` = :accountId")
  fun findByFoursquareAccountId(accountId: String): Flow<List<PrefectureLevelOverride>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: PrefectureLevelOverride)

  @Query(
    "DELETE FROM `prefecture_levels` " +
      "WHERE `foursquare_account_id` = :accountId AND `prefecture_code` = :prefectureCode",
  )
  suspend fun delete(accountId: String, prefectureCode: Int)
}
```

- [ ] **Step 3: マイグレーションを書く**

`core/data/src/main/java/blue/starry/mitsubachi/core/data/database/migration/Migration6To7.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * `prefecture_levels` を追加する。
 *
 * 既存の DatabaseModule は fallbackToDestructiveMigration を指定しているため、
 * マイグレーションを登録しないと version を上げた時点で foursquare_accounts ごと全テーブルが破棄され、
 * 既存ユーザーが再ログインを強いられる。テーブル追加だけでもマイグレーションを必ず登録すること。
 */
internal object Migration6To7 : Migration(6, 7) {
  override fun migrate(connection: SQLiteConnection) {
    connection.execSQL(
      "CREATE TABLE IF NOT EXISTS `prefecture_levels` (" +
        "`foursquare_account_id` TEXT NOT NULL, " +
        "`prefecture_code` INTEGER NOT NULL, " +
        "`level` INTEGER NOT NULL, " +
        "PRIMARY KEY(`foursquare_account_id`, `prefecture_code`))",
    )
  }
}
```

- [ ] **Step 4: データベースに登録する**

`core/data/src/main/java/blue/starry/mitsubachi/core/data/database/MitsubachiDatabase.kt` を次のとおり書き換える。

```kotlin
package blue.starry.mitsubachi.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import blue.starry.mitsubachi.core.data.database.dao.CacheDao
import blue.starry.mitsubachi.core.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.core.data.database.dao.PrefectureLevelOverrideDao
import blue.starry.mitsubachi.core.data.database.dao.UserSettingsDao
import blue.starry.mitsubachi.core.data.database.entity.Cache
import blue.starry.mitsubachi.core.data.database.entity.FoursquareAccount
import blue.starry.mitsubachi.core.data.database.entity.PrefectureLevelOverride
import blue.starry.mitsubachi.core.data.database.entity.UserSettings
import blue.starry.mitsubachi.core.data.database.entity.converter.InstantConverter
import blue.starry.mitsubachi.core.data.database.entity.converter.UserSettingsPayloadConverter

@Database(
  version = 7,
  entities = [
    FoursquareAccount::class,
    Cache::class,
    UserSettings::class,
    PrefectureLevelOverride::class,
  ],
)
@TypeConverters(InstantConverter::class, UserSettingsPayloadConverter::class)
internal abstract class MitsubachiDatabase : RoomDatabase() {
  abstract fun foursquareAccount(): FoursquareAccountDao
  abstract fun cache(): CacheDao
  abstract fun userSettings(): UserSettingsDao
  abstract fun prefectureLevelOverride(): PrefectureLevelOverrideDao
}
```

`core/data/src/main/java/blue/starry/mitsubachi/core/data/di/DaoModule.kt` に次を追加する。

```kotlin
  @Provides
  @Singleton
  internal fun providePrefectureLevelOverrideDao(
    database: MitsubachiDatabase,
  ): PrefectureLevelOverrideDao {
    return database.prefectureLevelOverride()
  }
```

import に `blue.starry.mitsubachi.core.data.database.dao.PrefectureLevelOverrideDao` を追加する。

- [ ] **Step 5: 両方の DatabaseModule にマイグレーションを登録する**

`core/data/src/debug/java/blue/starry/mitsubachi/core/data/di/PlainMitsubachiDatabaseModule.kt` の
`.setDriver(BundledSQLiteDriver())` の直後に次の 1 行を挿入する。

```kotlin
      .addMigrations(Migration6To7)
```

`core/data/src/release/java/blue/starry/mitsubachi/core/data/di/EncryptedMitsubachiDatabaseModule.kt` の
`.openHelperFactory(factory)` の直後に同じ 1 行を挿入する。

どちらも import に `blue.starry.mitsubachi.core.data.database.migration.Migration6To7` を追加する。

- [ ] **Step 6: リポジトリ実装を書く**

`core/data/src/main/java/blue/starry/mitsubachi/core/data/repository/PrefectureLevelRepositoryImpl.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.dao.PrefectureLevelOverrideDao
import blue.starry.mitsubachi.core.data.database.entity.PrefectureLevelOverride
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLevelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PrefectureLevelRepositoryImpl @Inject constructor(
  private val dao: PrefectureLevelOverrideDao,
) : PrefectureLevelRepository {
  override fun flow(account: FoursquareAccount): Flow<Map<Prefecture, PrefectureLevel>> {
    return dao.findByFoursquareAccountId(account.id).map { entities ->
      entities.mapNotNull { entity ->
        val prefecture = Prefecture.fromCode(entity.prefectureCode) ?: return@mapNotNull null
        val level = PrefectureLevel.fromScore(entity.level) ?: return@mapNotNull null
        prefecture to level
      }.toMap()
    }
  }

  override suspend fun set(
    account: FoursquareAccount,
    prefecture: Prefecture,
    level: PrefectureLevel,
  ) {
    dao.insertOrUpdate(
      PrefectureLevelOverride(
        foursquareAccountId = account.id,
        prefectureCode = prefecture.code,
        level = level.score,
      ),
    )
  }

  override suspend fun clear(account: FoursquareAccount, prefecture: Prefecture) {
    dao.delete(accountId = account.id, prefectureCode = prefecture.code)
  }
}
```

`core/data/src/main/java/blue/starry/mitsubachi/core/data/di/PrefectureLevelRepositoryModule.kt`:

```kotlin
package blue.starry.mitsubachi.core.data.di

import blue.starry.mitsubachi.core.data.repository.PrefectureLevelRepositoryImpl
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLevelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PrefectureLevelRepositoryModule {
  @Binds
  @Singleton
  internal abstract fun bind(impl: PrefectureLevelRepositoryImpl): PrefectureLevelRepository
}
```

- [ ] **Step 7: ビルドが通ることを確認する**

```bash
./gradlew :core:data:assembleDebug :core:data:detekt
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 8: マイグレーションが既存データを壊さないことを実機で確認する**

エミュレータには version 6 のデータベースを持つ `blue.starry.mitsubachi.local` がインストールされている。
アンインストールせずに上書きインストールし、ログイン状態が維持されることを確認する。

```bash
./gradlew :app:installLocalDebug
```

```bash
adb shell run-as blue.starry.mitsubachi.local sqlite3 databases/mitsubachi_debug.db "SELECT count(*) FROM foursquare_accounts; SELECT name FROM sqlite_master WHERE type='table' AND name='prefecture_levels';"
```

Expected: アカウント件数が 1 以上のまま、`prefecture_levels` が出力される。
`sqlite3` が端末にない場合は DB を `adb pull` してホスト側の `sqlite3` で確認する。
アカウント件数が 0 になっていたらマイグレーションが登録できておらず破壊的フォールバックが走っているので、
Step 5 をやり直す。

- [ ] **Step 9: コミット**

```bash
git add core/domain core/data
git commit -m "feat: 都道府県レベルの手動上書きを永続化する

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 7: 判定ユースケース（CalculatePrefectureCompletionsUseCase）

ベニュー履歴・ポリゴン・手動上書きを束ねて 47 都道府県分の `PrefectureCompletion` と訪問国数を返す。

**Files:**
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/StayVenueCategories.kt`
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureCompletionSummary.kt`
- Create: `core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/CalculatePrefectureCompletionsUseCase.kt`
- Test: `core/domain/src/test/java/blue/starry/mitsubachi/core/domain/usecase/CalculatePrefectureCompletionsUseCaseTest.kt`

**Interfaces:**
- Consumes: `FetchUserVenueHistoriesUseCase`（既存）、`PrefectureBoundaryRepository`、`PrefectureLevelRepository`、`PrefectureNameResolver`、`PrefectureLocator`、`FindFoursquareAccountUseCase`（既存）
- Produces:
  - `object StayVenueCategories { fun matches(venue: Venue): Boolean }`
  - `data class PrefectureCompletionSummary(val completions: ImmutableList<PrefectureCompletion>, val visitedCountryCodes: ImmutableSet<String>)` — `val totalScore: Int`、`val maxScore: Int`
  - `class CalculatePrefectureCompletionsUseCase { suspend operator fun invoke(policy: FetchPolicy = FetchPolicy.CacheOrNetwork): PrefectureCompletionSummary }`

- [ ] **Step 1: 失敗するテストを書く**

`core/domain/src/test/java/blue/starry/mitsubachi/core/domain/usecase/CalculatePrefectureCompletionsUseCaseTest.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueCategory
import blue.starry.mitsubachi.core.domain.model.VenueLocation
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatePrefectureCompletionsUseCaseTest {
  private val account = mockk<FoursquareAccount>(relaxed = true)
  private val fetchUserVenueHistories = mockk<FetchUserVenueHistoriesUseCase>()
  private val boundaryRepository = mockk<PrefectureBoundaryRepository>()
  private val levelRepository = mockk<PrefectureLevelRepository>()
  private val findFoursquareAccount = mockk<FindFoursquareAccountUseCase>()

  private val useCase = CalculatePrefectureCompletionsUseCase(
    fetchUserVenueHistoriesUseCase = fetchUserVenueHistories,
    prefectureBoundaryRepository = boundaryRepository,
    prefectureLevelRepository = levelRepository,
    findFoursquareAccountUseCase = findFoursquareAccount,
  )

  private fun setUp(
    histories: List<VenueHistory>,
    overrides: Map<Prefecture, PrefectureLevel> = emptyMap(),
  ) {
    every { account.id } returns "account-1"
    coEvery { findFoursquareAccount() } returns account
    coEvery { fetchUserVenueHistories(any()) } returns histories
    coEvery { boundaryRepository.findAll() } returns listOf(
      PrefectureBoundary(Prefecture.Tokyo, listOf(square(139.0, 35.0, 140.0, 36.0))),
    )
    every { levelRepository.flow(account) } returns flowOf(overrides)
  }

  @Test
  fun prefectureWithCheckInBecomesVisited() = runTest {
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
    assertEquals(3, summary.totalScore)
  }

  @Test
  fun prefectureWithoutCheckInStaysUnvisited() = runTest {
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(47, summary.completions.size)
    assertEquals(PrefectureLevel.Unvisited, summary.levelOf(Prefecture.Okinawa))
  }

  @Test
  fun prefectureWithLodgingVenueBecomesStayed() = runTest {
    setUp(
      listOf(
        history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5),
        history(
          state = "Tokyo Prefecture",
          latitude = 35.6,
          longitude = 139.6,
          categoryName = "Hotel",
        ),
      ),
    )

    val summary = useCase()

    assertEquals(PrefectureLevel.Stayed, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun nonLodgingCategoryStopsAtVisited() = runTest {
    setUp(
      listOf(
        history(
          state = "Tokyo Prefecture",
          latitude = 35.5,
          longitude = 139.5,
          categoryName = "Ramen Restaurant",
        ),
        // inn の部分一致で誤って宿泊にならないこと。
        // Dinner は "inn" を部分文字列として含むため、単語境界で判定していないと宿泊と誤判定される
        history(
          state = "Tokyo Prefecture",
          latitude = 35.51,
          longitude = 139.51,
          categoryName = "Dinner",
        ),
      ),
    )

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun manualLevelTakesPrecedenceOverAutomaticLevel() = runTest {
    setUp(
      histories = listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)),
      overrides = mapOf(Prefecture.Tokyo to PrefectureLevel.Lived),
    )

    val summary = useCase()

    assertEquals(PrefectureLevel.Lived, summary.levelOf(Prefecture.Tokyo))
    assertEquals(5, summary.totalScore)
  }

  @Test
  fun doesNotUsePolygonWhenStateIsResolvable() = runTest {
    // ポリゴンの外にある座標でも state で東京都に解決される
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 12.0, longitude = 100.0)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun fallsBackToCoordinatesWhenStateIsUnresolvable() = runTest {
    setUp(listOf(history(state = null, latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun fallsBackToCoordinatesForCompositeState() = runTest {
    setUp(listOf(history(state = "東京都/北海道", latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun venuesOutsideJapanAreCountedAsCountriesNotPrefectures() = runTest {
    setUp(
      listOf(
        history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5),
        history(state = "Seoul", latitude = 37.5665, longitude = 126.978, countryCode = "KR"),
        history(state = "CA", latitude = 37.7749, longitude = -122.4194, countryCode = "US"),
        history(state = "Taoyuan", latitude = 25.033, longitude = 121.5654, countryCode = "TW"),
      ),
    )

    val summary = useCase()

    assertEquals(3, summary.totalScore)
    assertEquals(setOf("KR", "US", "TW"), summary.visitedCountryCodes)
  }

  @Test
  fun maxTotalScoreIs235() = runTest {
    setUp(emptyList())

    val summary = useCase()

    assertEquals(235, summary.maxScore)
    assertEquals(0, summary.totalScore)
  }

  @Test
  fun usesAutomaticLevelsWhenNoAccountIsSignedIn() = runTest {
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)))
    // サインイン済みアカウントがない場合は手動上書きを読めないので自動判定だけになる
    coEvery { findFoursquareAccount() } returns null

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
    assertEquals(3, summary.totalScore)
  }

  @Test
  fun passesFetchPolicyThrough() = runTest {
    setUp(emptyList())

    useCase(FetchPolicy.NetworkOnly)

    coVerify { fetchUserVenueHistories(FetchPolicy.NetworkOnly) }
  }

  private fun PrefectureCompletionSummary.levelOf(prefecture: Prefecture): PrefectureLevel {
    return completions.first { it.prefecture == prefecture }.effectiveLevel
  }

  private fun history(
    state: String?,
    latitude: Double,
    longitude: Double,
    countryCode: String = "JP",
    categoryName: String = "Train Station",
  ): VenueHistory {
    return VenueHistory(
      venue = Venue(
        id = "venue-$latitude-$longitude-$categoryName",
        name = "venue",
        location = VenueLocation(
          latitude = latitude,
          longitude = longitude,
          distance = null,
          country = countryCode,
          countryCode = countryCode,
          postalCode = null,
          state = state,
          city = null,
          address = null,
          crossStreet = null,
          neighborhood = null,
        ),
        createdAt = ZonedDateTime.parse("2020-01-01T00:00:00+09:00"),
        categories = listOf(
          VenueCategory(
            id = "category-$categoryName",
            name = categoryName,
            iconUrl = "https://example.com/icon.png",
            isPrimary = true,
          ),
        ),
      ),
      count = 1,
    )
  }

  private fun square(
    west: Double,
    south: Double,
    east: Double,
    north: Double,
  ): List<DoubleArray> {
    return listOf(
      doubleArrayOf(west, south),
      doubleArrayOf(east, south),
      doubleArrayOf(east, north),
      doubleArrayOf(west, north),
      doubleArrayOf(west, south),
    )
  }
}
```

- [ ] **Step 2: テスト用の依存を追加する**

`core/domain/build.gradle.kts` の `dependencies` ブロックに次を追加する。

```kotlin
  testImplementation(libs.kotlinx.coroutines.test)
```

`gradle/libs.versions.toml` の `[libraries]` に定義がなければ追加する。

```toml
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "kotlinx-coroutines" }
```

`version.ref` は既存の kotlinx-coroutines の定義に合わせる。既存に別名があるならそれを使い、
新しいバージョン番号を勝手に足さないこと。

- [ ] **Step 3: テストが失敗することを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*CalculatePrefectureCompletionsUseCaseTest*'
```

Expected: FAIL。`Unresolved reference: CalculatePrefectureCompletionsUseCase`

`FoursquareAccount` は data class なので MockK でのモックが失敗することがある。
その場合は mockk-agent の設定を足すのではなく、実インスタンスを組み立てて使うこと。

- [ ] **Step 4: StayVenueCategories を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/StayVenueCategories.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Venue

/**
 * 宿泊施設に相当する Foursquare のカテゴリ判定。
 *
 * カテゴリ ID は Foursquare 側で増減するため、カテゴリ名で判定する。
 * ホテルのラウンジに立ち寄っただけでも宿泊と判定されうるが、手動上書きで修正できる前提とする。
 */
object StayVenueCategories {
  // 語として一致させるキーワード。inn は Dinner のような語に部分一致してしまうため語単位で見る
  private val wordKeywords = setOf(
    "hotel",
    "hotels",
    "hostel",
    "motel",
    "inn",
    "ryokan",
    "resort",
    "guesthouse",
    "capsule",
    "lodge",
    "lodging",
    "minshuku",
  )

  // 空白を含むので語分割では拾えないもの。連結した文字列に対する部分一致で見る
  private val phraseKeywords = listOf(
    "bed & breakfast",
    "bed and breakfast",
    "guest house",
  )

  private val wordSeparator = Regex("[^a-z&]+")

  fun matches(venue: Venue): Boolean {
    return venue.categories.any { category ->
      val name = category.name.lowercase()
      phraseKeywords.any { name.contains(it) } ||
        name.split(wordSeparator).any { it in wordKeywords }
    }
  }
}
```

- [ ] **Step 5: PrefectureCompletionSummary を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/model/PrefectureCompletionSummary.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

/**
 * 県踏破度の集計結果。
 *
 * @param completions 47 都道府県分。コード順に並ぶ
 * @param visitedCountryCodes 日本以外でチェックインした国の ISO 3166-1 alpha-2 コード
 */
@Immutable
data class PrefectureCompletionSummary(
  val completions: ImmutableList<PrefectureCompletion>,
  val visitedCountryCodes: ImmutableSet<String>,
) {
  val totalScore: Int
    get() = completions.totalScore

  val maxScore: Int
    get() = PrefectureLevel.MaxTotalScore
}
```

- [ ] **Step 6: CalculatePrefectureCompletionsUseCase を実装する**

`core/domain/src/main/java/blue/starry/mitsubachi/core/domain/usecase/CalculatePrefectureCompletionsUseCase.kt`:

```kotlin
package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletion
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.model.Venue
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private const val JAPAN_COUNTRY_CODE = "JP"

@Singleton
class CalculatePrefectureCompletionsUseCase @Inject constructor(
  private val fetchUserVenueHistoriesUseCase: FetchUserVenueHistoriesUseCase,
  private val prefectureBoundaryRepository: PrefectureBoundaryRepository,
  private val prefectureLevelRepository: PrefectureLevelRepository,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
) {
  suspend operator fun invoke(
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): PrefectureCompletionSummary {
    val histories = fetchUserVenueHistoriesUseCase(policy)
    val locator = PrefectureLocator(prefectureBoundaryRepository.findAll())

    val venueCounts = mutableMapOf<Prefecture, Int>()
    val stayed = mutableSetOf<Prefecture>()
    val countryCodes = mutableSetOf<String>()

    for (history in histories) {
      val venue = history.venue
      if (!venue.location.countryCode.equals(JAPAN_COUNTRY_CODE, ignoreCase = true)) {
        countryCodes += venue.location.countryCode.uppercase()
        continue
      }

      val prefecture = locate(venue, locator) ?: continue
      venueCounts[prefecture] = (venueCounts[prefecture] ?: 0) + 1
      if (StayVenueCategories.matches(venue)) {
        stayed += prefecture
      }
    }

    val overrides = findFoursquareAccountUseCase()
      ?.let { prefectureLevelRepository.flow(it).first() }
      .orEmpty()

    val completions = Prefecture.entries.map { prefecture ->
      val count = venueCounts[prefecture] ?: 0
      PrefectureCompletion(
        prefecture = prefecture,
        automaticLevel = automaticLevelOf(count, prefecture in stayed),
        manualLevel = overrides[prefecture],
        venueCount = count,
      )
    }

    return PrefectureCompletionSummary(
      completions = completions.toImmutableList(),
      visitedCountryCodes = countryCodes.toImmutableSet(),
    )
  }

  // state は Foursquare が住所から導出した値で、県境では座標より信頼できるため先に見る
  private fun locate(venue: Venue, locator: PrefectureLocator): Prefecture? {
    PrefectureNameResolver.resolve(venue.location.state)?.also {
      return it
    }
    return locator.locate(
      latitude = venue.location.latitude,
      longitude = venue.location.longitude,
    )
  }

  private fun automaticLevelOf(venueCount: Int, hasStayVenue: Boolean): PrefectureLevel {
    return when {
      venueCount == 0 -> PrefectureLevel.Unvisited
      hasStayVenue -> PrefectureLevel.Stayed
      else -> PrefectureLevel.Visited
    }
  }
}
```

- [ ] **Step 7: テストが通ることを確認する**

```bash
./gradlew :core:domain:testDebugUnitTest --tests '*CalculatePrefectureCompletionsUseCaseTest*'
```

Expected: PASS（11 tests）

- [ ] **Step 8: モジュール全体のテストと静的解析を通す**

```bash
./gradlew :core:domain:testDebugUnitTest :core:domain:detekt :core:data:testDebugUnitTest :core:data:detekt
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 9: コミット**

```bash
git add core/domain gradle/libs.versions.toml
git commit -m "feat: 県踏破度の判定ユースケースを追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 8: レベル配色

6 段階の色を `MaterialTheme.colorScheme` から導出する。ライトとダークで明度の方向が反転することをテストで固定する。

**Files:**
- Create: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureLevelColors.kt`
- Test: `feature/map/src/test/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureLevelColorsTest.kt`

**Interfaces:**
- Consumes: `PrefectureLevel`（Task 2）
- Produces: `fun ColorScheme.prefectureLevelColor(level: PrefectureLevel): Color`

**方式:** `lerp(surfaceContainerLow, primary, fraction)` で塗る。ライトの `primary` は暗い色、ダークの `primary` は
明るい色なので、同じ式のままレベルが上がるほどライトでは濃く、ダークでは明るくなる。
レベル 0 は `surfaceContainerLow` そのもので彩度を持たない。

- [ ] **Step 1: 失敗するテストを書く**

`feature/map/src/test/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureLevelColorsTest.kt`:

```kotlin
package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.luminance
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrefectureLevelColorsTest {
  private val light = lightColorScheme()
  private val dark = darkColorScheme()

  @Test
  fun unvisitedUsesSurfaceColorItself() {
    assertEquals(light.surfaceContainerLow, light.prefectureLevelColor(PrefectureLevel.Unvisited))
    assertEquals(dark.surfaceContainerLow, dark.prefectureLevelColor(PrefectureLevel.Unvisited))
  }

  @Test
  fun getsDarkerAsLevelRisesInLightTheme() {
    val luminances = PrefectureLevel.entries.map { light.prefectureLevelColor(it).luminance() }

    for (index in 1 until luminances.size) {
      assertTrue(
        luminances[index] < luminances[index - 1],
        "level $index should be darker than level ${index - 1}",
      )
    }
  }

  @Test
  fun getsBrighterAsLevelRisesInDarkTheme() {
    val luminances = PrefectureLevel.entries.map { dark.prefectureLevelColor(it).luminance() }

    for (index in 1 until luminances.size) {
      assertTrue(
        luminances[index] > luminances[index - 1],
        "level $index should be brighter than level ${index - 1}",
      )
    }
  }

  @Test
  fun keepsLuminanceGapBetweenAdjacentLevels() {
    for (scheme in listOf(light, dark)) {
      val luminances = PrefectureLevel.entries.map { scheme.prefectureLevelColor(it).luminance() }
      for (index in 1 until luminances.size) {
        val difference = kotlin.math.abs(luminances[index] - luminances[index - 1])
        // しきい値は実装の定数を読まずにハードコードする。
        // 実装側の定数を参照すると、その定数を下げる変更をこのテストが検出できなくなる
        assertTrue(
          difference >= 0.02f,
          "levels ${index - 1} and $index differ by only $difference",
        )
      }
    }
  }

  @Test
  fun allSixLevelsGetDistinctColors() {
    for (scheme in listOf(light, dark)) {
      val colors = PrefectureLevel.entries.map { scheme.prefectureLevelColor(it) }
      assertEquals(colors.size, colors.toSet().size)
    }
  }
}
```

- [ ] **Step 2: テストが失敗することを確認する**

```bash
./gradlew :feature:map:testDebugUnitTest --tests '*PrefectureLevelColorsTest*'
```

Expected: FAIL。`Unresolved reference: prefectureLevelColor`

- [ ] **Step 3: 実装する**

`feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureLevelColors.kt`:

```kotlin
package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel

/** 隣接レベル間で最低限確保する輝度差。テストと実装で共有する。 */
const val MINIMUM_ADJACENT_LUMINANCE_DIFFERENCE = 0.02f

// 未踏 (0.0) から居住 (1.0) までの補間比率。等間隔だと低いレベル同士の差が潰れるため序盤を広めに取る
private val LEVEL_FRACTIONS = floatArrayOf(0f, 0.24f, 0.43f, 0.62f, 0.81f, 1f)

/**
 * レベルに対応する塗り色を返す。
 *
 * ライトテーマの primary は暗い色、ダークテーマの primary は明るい色なので、
 * 同じ補間式のままレベルが上がるとライトでは濃く、ダークでは明るくなる。
 * 未踏は surfaceContainerLow そのもので、彩度を持たない。
 */
fun ColorScheme.prefectureLevelColor(level: PrefectureLevel): Color {
  return lerp(surfaceContainerLow, primary, LEVEL_FRACTIONS[level.score])
}
```

- [ ] **Step 4: テストが通ることを確認する**

```bash
./gradlew :feature:map:testDebugUnitTest --tests '*PrefectureLevelColorsTest*'
```

Expected: PASS（5 tests）

`keepsLuminanceGapBetweenAdjacentLevels` が落ちた場合は `LEVEL_FRACTIONS` の間隔を調整する。
`MINIMUM_ADJACENT_LUMINANCE_DIFFERENCE` を下げて通すことはしない。テスト側はしきい値をハードコードしており、
実装の定数を下げてもテストは緑にならない。

- [ ] **Step 5: コミット**

```bash
git add feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures feature/map/src/test
git commit -m "feat: 県踏破度のレベル配色を追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 9: Canvas 日本地図

都道府県ポリゴンを Compose Canvas に描く。沖縄は左下にインセット表示し、タップで都道府県を返す。

**Files:**
- Create: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/JapanMapProjection.kt`
- Create: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureMap.kt`
- Test: `feature/map/src/test/java/blue/starry/mitsubachi/feature/map/ui/prefectures/JapanMapProjectionTest.kt`

**Interfaces:**
- Consumes: `PrefectureBoundary`、`Prefecture`、`PrefectureLevel`、`prefectureLevelColor`（Task 4・8）
- Produces:
  - `class JapanMapProjection(boundaries: List<PrefectureBoundary>, width: Float, height: Float)` — `val projected: List<ProjectedPrefecture>`、`val insetBounds: Rect`、`fun hitTest(x: Float, y: Float): Prefecture?`
  - `data class ProjectedPrefecture(val prefecture: Prefecture, val path: Path, val rings: List<FloatArray>)`
  - `@Composable fun PrefectureMap(boundaries: ImmutableList<PrefectureBoundary>, levels: ImmutableMap<Prefecture, PrefectureLevel>, selected: Prefecture?, onSelect: (Prefecture) -> Unit, modifier: Modifier)`

**投影方式:** 沖縄県 (`Prefecture.Okinawa`) を除いた 46 都道府県の外接矩形に等積で収まる正射的な投影を作る。
経度方向は本州中央の緯度 `cos(36°)` で縮める。沖縄県は同じ縮尺で別に投影し、キャンバス左下の
インセット枠（幅・高さともキャンバス短辺の 26%）に収めて枠線を描く。
ヒットテストは投影後のスクリーン座標で even-odd 判定を行い、判定用と描画用で同じ点列を使う。

- [ ] **Step 1: 失敗するテストを書く**

`feature/map/src/test/java/blue/starry/mitsubachi/feature/map/ui/prefectures/JapanMapProjectionTest.kt`:

```kotlin
package blue.starry.mitsubachi.feature.map.ui.prefectures

import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JapanMapProjectionTest {
  private val boundaries = listOf(
    // 本州側に見立てた 2 県
    PrefectureBoundary(Prefecture.Tokyo, listOf(square(139.0, 35.0, 140.0, 36.0))),
    PrefectureBoundary(Prefecture.Hokkaido, listOf(square(141.0, 43.0, 143.0, 45.0))),
    // 遠く南西にある沖縄県
    PrefectureBoundary(Prefecture.Okinawa, listOf(square(127.0, 26.0, 128.0, 27.0))),
  )
  private val projection = JapanMapProjection(boundaries, width = 400f, height = 600f)

  @Test
  fun projectsEveryPrefecture() {
    assertEquals(3, projection.projected.size)
    assertEquals(
      boundaries.map { it.prefecture }.toSet(),
      projection.projected.map { it.prefecture }.toSet(),
    )
  }

  @Test
  fun projectedCoordinatesFitInsideCanvas() {
    for (projected in projection.projected) {
      for (ring in projected.rings) {
        var index = 0
        while (index < ring.size) {
          assertTrue(ring[index] in 0f..400f, "x=${ring[index]} out of canvas")
          assertTrue(ring[index + 1] in 0f..600f, "y=${ring[index + 1]} out of canvas")
          index += 2
        }
      }
    }
  }

  @Test
  fun drawsNorthernPrefecturesHigher() {
    val tokyo = projection.centerOf(Prefecture.Tokyo)
    val hokkaido = projection.centerOf(Prefecture.Hokkaido)

    assertTrue(hokkaido.second < tokyo.second, "Hokkaido should be drawn above Tokyo")
  }

  @Test
  fun drawsOkinawaInBottomLeftInset() {
    val okinawa = projection.centerOf(Prefecture.Okinawa)

    assertTrue(okinawa.first < 400f * 0.3f, "Okinawa should be near the left edge")
    assertTrue(okinawa.second > 600f * 0.7f, "Okinawa should be near the bottom edge")
  }

  @Test
  fun hitTestReturnsPrefectureAtItsCenter() {
    for (prefecture in listOf(Prefecture.Tokyo, Prefecture.Hokkaido, Prefecture.Okinawa)) {
      val (x, y) = projection.centerOf(prefecture)
      assertEquals(prefecture, projection.hitTest(x, y), "hit test failed for $prefecture")
    }
  }

  @Test
  fun hitTestReturnsNullOutsideEveryPrefecture() {
    assertNull(projection.hitTest(-10f, -10f))
    assertNull(projection.hitTest(399f, 1f))
  }

  @Test
  fun exposesInsetBounds() {
    assertNotNull(projection.insetBounds)
  }

  private fun JapanMapProjection.centerOf(prefecture: Prefecture): Pair<Float, Float> {
    val ring = projected.first { it.prefecture == prefecture }.rings.first()
    var sumX = 0f
    var sumY = 0f
    var count = 0
    var index = 0
    // 閉リングの終点は始点と同じなので最後の 1 点を除いて平均する
    while (index < ring.size - 2) {
      sumX += ring[index]
      sumY += ring[index + 1]
      count++
      index += 2
    }
    return sumX / count to sumY / count
  }

  private fun square(
    west: Double,
    south: Double,
    east: Double,
    north: Double,
  ): List<DoubleArray> {
    return listOf(
      doubleArrayOf(west, south),
      doubleArrayOf(east, south),
      doubleArrayOf(east, north),
      doubleArrayOf(west, north),
      doubleArrayOf(west, south),
    )
  }
}
```

- [ ] **Step 2: テストが失敗することを確認する**

```bash
./gradlew :feature:map:testDebugUnitTest --tests '*JapanMapProjectionTest*'
```

Expected: FAIL。`Unresolved reference: JapanMapProjection`

- [ ] **Step 3: JapanMapProjection を実装する**

`feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/JapanMapProjection.kt`:

```kotlin
package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import kotlin.math.cos
import kotlin.math.min

/** 経度の縮尺を合わせる基準緯度。本州中央あたり。 */
private const val REFERENCE_LATITUDE = 36.0

/** 本土を収める領域がキャンバスに占める割合。 */
private const val MAIN_AREA_RATIO = 0.98f

/** 沖縄インセット枠の一辺がキャンバス短辺に占める割合。 */
private const val INSET_SIDE_RATIO = 0.26f

/** インセット枠とキャンバス端の余白がキャンバス短辺に占める割合。 */
private const val INSET_MARGIN_RATIO = 0.02f

/** インセット枠の内側に取る余白の割合。 */
private const val INSET_PADDING_RATIO = 0.08f

/**
 * 投影済みの 1 都道府県。
 *
 * @param rings スクリーン座標。1 リングにつき `[x0, y0, x1, y1, ...]`。ヒットテストと Path 生成の両方に使う
 */
class ProjectedPrefecture(
  val prefecture: Prefecture,
  val rings: List<FloatArray>,
) {
  // Path は android.graphics.Path に依存するため、ユニットテストで触らずに済むよう遅延生成する
  val path: Path by lazy {
    Path().apply {
      for (ring in rings) {
        var index = 0
        while (index < ring.size) {
          if (index == 0) moveTo(ring[0], ring[1]) else lineTo(ring[index], ring[index + 1])
          index += 2
        }
        close()
      }
    }
  }
}

/**
 * 都道府県ポリゴンをキャンバス座標に落とす。
 *
 * 沖縄県は本土から遠いので、同じ縮尺のまま左下のインセット枠に別途配置する。
 * 描画とヒットテストで同じ点列を使うため、判定と見た目がずれない。
 */
class JapanMapProjection(
  boundaries: List<PrefectureBoundary>,
  private val width: Float,
  private val height: Float,
) {
  private val longitudeScale = cos(Math.toRadians(REFERENCE_LATITUDE)).toFloat()
  private val shortSide = min(width, height)
  private val insetMargin = shortSide * INSET_MARGIN_RATIO
  private val insetSide = shortSide * INSET_SIDE_RATIO

  val insetBounds: Rect = Rect(
    offset = Offset(insetMargin, height - insetMargin - insetSide),
    size = Size(insetSide, insetSide),
  )

  val projected: List<ProjectedPrefecture>

  init {
    val (inset, main) = boundaries.partition { it.prefecture == Prefecture.Okinawa }

    val mainArea = Rect(
      offset = Offset(0f, 0f),
      size = Size(width * MAIN_AREA_RATIO, height * MAIN_AREA_RATIO),
    ).translate(width * (1 - MAIN_AREA_RATIO) / 2, height * (1 - MAIN_AREA_RATIO) / 2)

    val insetPadding = insetSide * INSET_PADDING_RATIO
    val insetArea = Rect(
      offset = Offset(insetBounds.left + insetPadding, insetBounds.top + insetPadding),
      size = Size(insetSide - insetPadding * 2, insetSide - insetPadding * 2),
    )

    projected = project(main, mainArea) + project(inset, insetArea)
  }

  fun hitTest(x: Float, y: Float): Prefecture? {
    for (item in projected) {
      for (ring in item.rings) {
        if (contains(ring, x, y)) {
          return item.prefecture
        }
      }
    }
    return null
  }

  private fun project(
    boundaries: List<PrefectureBoundary>,
    area: Rect,
  ): List<ProjectedPrefecture> {
    if (boundaries.isEmpty()) {
      return emptyList()
    }

    // 経度は基準緯度で縮め、緯度は北が上になるよう符号を反転させた中間座標を作る
    var minX = Float.MAX_VALUE
    var maxX = -Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    var maxY = -Float.MAX_VALUE
    for (boundary in boundaries) {
      for (ring in boundary.rings) {
        for (point in ring) {
          val x = (point[0] * longitudeScale).toFloat()
          val y = (-point[1]).toFloat()
          if (x < minX) minX = x
          if (x > maxX) maxX = x
          if (y < minY) minY = y
          if (y > maxY) maxY = y
        }
      }
    }

    val sourceWidth = (maxX - minX).takeIf { it > 0f } ?: 1f
    val sourceHeight = (maxY - minY).takeIf { it > 0f } ?: 1f
    val scale = min(area.width / sourceWidth, area.height / sourceHeight)
    // アスペクト比を保ったまま領域の中央に置く
    val offsetX = area.left + (area.width - sourceWidth * scale) / 2
    val offsetY = area.top + (area.height - sourceHeight * scale) / 2

    return boundaries.map { boundary ->
      val rings = boundary.rings.map { ring ->
        val screen = FloatArray(ring.size * 2)
        ring.forEachIndexed { index, point ->
          screen[index * 2] = offsetX + ((point[0] * longitudeScale).toFloat() - minX) * scale
          screen[index * 2 + 1] = offsetY + ((-point[1]).toFloat() - minY) * scale
        }
        screen
      }
      ProjectedPrefecture(prefecture = boundary.prefecture, rings = rings)
    }
  }

  private fun contains(ring: FloatArray, x: Float, y: Float): Boolean {
    var inside = false
    val count = ring.size / 2
    var j = count - 1
    for (i in 0 until count) {
      val xi = ring[i * 2]
      val yi = ring[i * 2 + 1]
      val xj = ring[j * 2]
      val yj = ring[j * 2 + 1]
      if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
        inside = !inside
      }
      j = i
    }
    return inside
  }
}
```

`ProjectedPrefecture.path` は `by lazy` にしてある。`androidx.compose.ui.graphics.Path` は
`android.graphics.Path` に依存するため、ユニットテストからは `rings` だけを見て `path` に触れないこと。

- [ ] **Step 4: テストが通ることを確認する**

```bash
./gradlew :feature:map:testDebugUnitTest --tests '*JapanMapProjectionTest*'
```

Expected: PASS（7 tests）

- [ ] **Step 5: PrefectureMap を実装する**

`feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureMap.kt`:

```kotlin
package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

// 日本列島の外接矩形はおおむね縦長。幅に対する高さの比
private const val MAP_ASPECT_RATIO = 0.82f

@Composable
fun PrefectureMap(
  boundaries: ImmutableList<PrefectureBoundary>,
  levels: ImmutableMap<Prefecture, PrefectureLevel>,
  selected: Prefecture?,
  onSelect: (Prefecture) -> Unit,
  modifier: Modifier = Modifier,
) {
  val colorScheme = MaterialTheme.colorScheme
  val density = LocalDensity.current

  // pointerInput は projection が変わらない限りコルーチンを再起動しないため、
  // onSelect を直接キャプチャすると古いラムダを掴み続ける
  val currentOnSelect by rememberUpdatedState(onSelect)

  BoxWithConstraints(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(MAP_ASPECT_RATIO),
  ) {
    val widthPx = with(density) { maxWidth.toPx() }
    val heightPx = with(density) { maxHeight.toPx() }

    // 投影は composition のたびに作り直さない
    val projection = remember(boundaries, widthPx, heightPx) {
      JapanMapProjection(boundaries, widthPx, heightPx)
    }

    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(projection) {
          detectTapGestures { offset ->
            projection.hitTest(offset.x, offset.y)?.also(currentOnSelect)
          }
        },
    ) {
      for (item in projection.projected) {
        val level = levels[item.prefecture] ?: PrefectureLevel.Unvisited
        drawPath(item.path, colorScheme.prefectureLevelColor(level))
        // 同じレベルの隣接県が塊に見えないよう境界線は常に引く
        drawPath(
          path = item.path,
          color = colorScheme.outlineVariant,
          style = Stroke(width = 1.dp.toPx()),
        )
      }

      // 選択中の都道府県は塗りではなくアウトラインの太さで示す
      selected?.also { prefecture ->
        projection.projected.firstOrNull { it.prefecture == prefecture }?.also {
          drawPath(
            path = it.path,
            color = colorScheme.onSurface,
            style = Stroke(width = 3.dp.toPx()),
          )
        }
      }

      // 沖縄インセットの枠線
      drawRect(
        color = colorScheme.outlineVariant,
        topLeft = projection.insetBounds.topLeft,
        size = projection.insetBounds.size,
        style = Stroke(width = 1.dp.toPx()),
      )
    }
  }
}
```

- [ ] **Step 6: ビルドと静的解析を通す**

```bash
./gradlew :feature:map:assembleDebug :feature:map:detekt :feature:map:testDebugUnitTest
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 7: コミット**

```bash
git add feature/map/src
git commit -m "feat: 都道府県を塗り分ける Canvas 日本地図を追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 10: ViewModel

判定結果とポリゴンを束ね、レベル変更・上書き解除・リフレッシュを提供する。

**Files:**
- Create: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureCompletionScreenViewModel.kt`

**Interfaces:**
- Consumes: `CalculatePrefectureCompletionsUseCase`、`PrefectureBoundaryRepository`、`PrefectureLevelRepository`、`FindFoursquareAccountUseCase`
- Produces:
  - `sealed interface UiState { Loading; data class Success(val summary: PrefectureCompletionSummary, val boundaries: ImmutableList<PrefectureBoundary>, val isRefreshing: Boolean); data class Error(val exception: Exception) }`
  - `fun refresh(): Job`、`fun setLevel(prefecture: Prefecture, level: PrefectureLevel): Job`、`fun clearLevel(prefecture: Prefecture): Job`

- [ ] **Step 1: 実装する**

`feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureCompletionScreenViewModel.kt`:

```kotlin
package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.usecase.CalculatePrefectureCompletionsUseCase
import blue.starry.mitsubachi.core.domain.usecase.FindFoursquareAccountUseCase
import blue.starry.mitsubachi.core.domain.usecase.PrefectureBoundaryRepository
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLevelRepository
import blue.starry.mitsubachi.core.ui.compose.error.onException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrefectureCompletionScreenViewModel @Inject constructor(
  private val calculatePrefectureCompletionsUseCase: CalculatePrefectureCompletionsUseCase,
  private val prefectureBoundaryRepository: PrefectureBoundaryRepository,
  private val prefectureLevelRepository: PrefectureLevelRepository,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
) : ViewModel() {
  sealed interface UiState {
    data object Loading : UiState

    data class Success(
      val summary: PrefectureCompletionSummary,
      val boundaries: ImmutableList<PrefectureBoundary>,
      val isRefreshing: Boolean,
    ) : UiState

    data class Error(val exception: Exception) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state: StateFlow<UiState> = _state.asStateFlow()

  init {
    refresh()
  }

  fun refresh(): Job {
    return viewModelScope.launch {
      fetch()
    }
  }

  fun setLevel(prefecture: Prefecture, level: PrefectureLevel): Job {
    return viewModelScope.launch {
      val account = findFoursquareAccountUseCase() ?: return@launch
      prefectureLevelRepository.set(account, prefecture, level)
      // 上書きはキャッシュ済みのベニュー履歴で再計算できるのでネットワークには行かない
      fetch(policy = FetchPolicy.CacheOrNetwork, keepPreviousState = true)
    }
  }

  fun clearLevel(prefecture: Prefecture): Job {
    return viewModelScope.launch {
      val account = findFoursquareAccountUseCase() ?: return@launch
      prefectureLevelRepository.clear(account, prefecture)
      fetch(policy = FetchPolicy.CacheOrNetwork, keepPreviousState = true)
    }
  }

  private suspend fun fetch(
    policy: FetchPolicy? = null,
    keepPreviousState: Boolean = false,
  ) {
    val currentState = state.value
    val isRefreshing = currentState is UiState.Success

    if (isRefreshing) {
      _state.value = currentState.copy(isRefreshing = !keepPreviousState)
    } else {
      _state.value = UiState.Loading
    }

    runCatching {
      // 初回読み込みはキャッシュを使い、リフレッシュ時はネットワークから取得する
      val effectivePolicy = policy
        ?: if (isRefreshing) FetchPolicy.NetworkOnly else FetchPolicy.CacheOrNetwork
      calculatePrefectureCompletionsUseCase(effectivePolicy) to
        prefectureBoundaryRepository.findAll().toImmutableList()
    }.onSuccess { (summary, boundaries) ->
      _state.value = UiState.Success(
        summary = summary,
        boundaries = boundaries,
        isRefreshing = false,
      )
    }.onException { e ->
      _state.value = UiState.Error(e)
    }
  }
}
```

- [ ] **Step 2: ビルドが通ることを確認する**

```bash
./gradlew :feature:map:assembleDebug :feature:map:detekt
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 3: コミット**

```bash
git add feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureCompletionScreenViewModel.kt
git commit -m "feat: 県踏破度画面の ViewModel を追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 11: 画面本体（スコア・地図・凡例・リスト・ボトムシート・クレジット）

**Files:**
- Create: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureCompletionScreen.kt`
- Create: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureLevelSheet.kt`
- Modify: `feature/map/src/main/res/values/strings.xml`
- Modify: `feature/map/src/main/res/values-ja/strings.xml`
- Modify: `feature/map/src/main/res/values-ko-rKR/strings.xml`

**Interfaces:**
- Consumes: `PrefectureCompletionScreenViewModel`、`PrefectureMap`、`prefectureLevelColor`
- Produces: `@Composable fun PrefectureCompletionScreen(modifier: Modifier = Modifier, viewModel: PrefectureCompletionScreenViewModel = hiltViewModel())`

- [ ] **Step 1: 文字列を 3 ロケールに追加する**

`feature/map/src/main/res/values/strings.xml` に追加:

```xml
    <string name="prefecture_completion">Prefecture Completion</string>
    <string name="prefecture_completion_score">%1$d / %2$d</string>
    <string name="prefecture_completion_countries">%1$d countries outside Japan</string>
    <string name="prefecture_completion_legend">Legend</string>
    <string name="prefecture_completion_level_unvisited">Never been</string>
    <string name="prefecture_completion_level_passed_through">Passed through</string>
    <string name="prefecture_completion_level_landed">Set foot</string>
    <string name="prefecture_completion_level_visited">Visited</string>
    <string name="prefecture_completion_level_stayed">Stayed overnight</string>
    <string name="prefecture_completion_level_lived">Lived</string>
    <string name="prefecture_completion_level_points">%1$d pt</string>
    <string name="prefecture_completion_venue_count">%1$d places</string>
    <string name="prefecture_completion_overridden">Edited</string>
    <string name="prefecture_completion_clear_override">Reset to automatic</string>
    <string name="prefecture_completion_credit">Inspired by Keikenchi from Todofuken Shikuchoson (uub.jp)</string>
    <string name="prefecture_completion_open">Open prefecture completion</string>
```

`feature/map/src/main/res/values-ja/strings.xml` に追加:

```xml
    <string name="prefecture_completion">県踏破度</string>
    <string name="prefecture_completion_score">%1$d / %2$d</string>
    <string name="prefecture_completion_countries">日本以外に %1$d ヶ国</string>
    <string name="prefecture_completion_legend">凡例</string>
    <string name="prefecture_completion_level_unvisited">未踏</string>
    <string name="prefecture_completion_level_passed_through">通過</string>
    <string name="prefecture_completion_level_landed">接地</string>
    <string name="prefecture_completion_level_visited">訪問</string>
    <string name="prefecture_completion_level_stayed">宿泊</string>
    <string name="prefecture_completion_level_lived">居住</string>
    <string name="prefecture_completion_level_points">%1$d 点</string>
    <string name="prefecture_completion_venue_count">%1$d か所</string>
    <string name="prefecture_completion_overridden">手動設定</string>
    <string name="prefecture_completion_clear_override">自動判定に戻す</string>
    <string name="prefecture_completion_credit">「経県値」（都道府県市区町村 uub.jp）に着想を得ています</string>
    <string name="prefecture_completion_open">県踏破度を開く</string>
```

`feature/map/src/main/res/values-ko-rKR/strings.xml` に追加:

**この韓国語訳は暫定訳であり、ネイティブによる確認を受けていない。**
ユーザーの判断により暫定訳のまま進める。PR 本文に「韓国語訳は未確認」と明記すること。
都道府県名も韓国語ロケールではローマ字表記になる（`Prefecture.displayName()` が日本語ロケール以外は
`romajiName` を返すため）。この点も同様に確認対象とする。

```xml
    <string name="prefecture_completion">현 답파도</string>
    <string name="prefecture_completion_score">%1$d / %2$d</string>
    <string name="prefecture_completion_countries">일본 외 %1$d개국</string>
    <string name="prefecture_completion_legend">범례</string>
    <string name="prefecture_completion_level_unvisited">미답</string>
    <string name="prefecture_completion_level_passed_through">통과</string>
    <string name="prefecture_completion_level_landed">발 디딤</string>
    <string name="prefecture_completion_level_visited">방문</string>
    <string name="prefecture_completion_level_stayed">숙박</string>
    <string name="prefecture_completion_level_lived">거주</string>
    <string name="prefecture_completion_level_points">%1$d점</string>
    <string name="prefecture_completion_venue_count">%1$d곳</string>
    <string name="prefecture_completion_overridden">수동 설정</string>
    <string name="prefecture_completion_clear_override">자동 판정으로 되돌리기</string>
    <string name="prefecture_completion_credit">‘경현치’(都道府県市区町村 uub.jp)에서 착안했습니다</string>
    <string name="prefecture_completion_open">현 답파도 열기</string>
```

- [ ] **Step 2: 都道府県名とレベル名の表示ヘルパを書く**

`PrefectureCompletionScreen.kt` と同じファイルの下部に private な composable として置く。

```kotlin
@Composable
private fun Prefecture.displayName(): String {
  // 日本語ロケールでは漢字表記、それ以外はローマ字表記にする
  val locale = LocalConfiguration.current.locales[0]
  return if (locale.language == "ja") japaneseName else romajiName.replaceFirstChar { it.uppercase() }
}

@Composable
private fun PrefectureLevel.displayName(): String {
  return stringResource(
    when (this) {
      PrefectureLevel.Unvisited -> R.string.prefecture_completion_level_unvisited
      PrefectureLevel.PassedThrough -> R.string.prefecture_completion_level_passed_through
      PrefectureLevel.Landed -> R.string.prefecture_completion_level_landed
      PrefectureLevel.Visited -> R.string.prefecture_completion_level_visited
      PrefectureLevel.Stayed -> R.string.prefecture_completion_level_stayed
      PrefectureLevel.Lived -> R.string.prefecture_completion_level_lived
    },
  )
}
```

- [ ] **Step 3: 画面を実装する**

`PrefectureCompletionScreen` は `LazyColumn` 1 本で次を縦に並べる。既存の `VenueHistoriesScreen` と同じく
`PullToRefreshBox` で囲み、`UiState` の 3 分岐で `LoadingScreen` / `ErrorScreen` を出す。

1. **スコアヘッダー** — `stringResource(R.string.prefecture_completion_score, summary.totalScore, summary.maxScore)` を
   `MaterialTheme.typography.displaySmall` で表示し、その下に
   `stringResource(R.string.prefecture_completion_countries, summary.visitedCountryCodes.size)` を
   `bodyMedium` で置く。`visitedCountryCodes` が空なら国カウンターの行を出さない
2. **日本地図** — Task 9 の `PrefectureMap`。`levels` は `summary.completions.associate { it.prefecture to it.effectiveLevel }` の
   `toImmutableMap()`。タップで `selectedPrefecture` を更新してボトムシートを開く
3. **凡例** — 6 段階を `FlowRow` に並べる。各項目は色見本の四角、レベル名、
   `stringResource(R.string.prefecture_completion_level_points, level.score)` の 3 点セット。
   色だけに情報を載せないため、レベル名と点数を必ず文字で添える
4. **都道府県リスト** — レベルの高い順にグループ化する。各グループの見出しはレベル名と件数。
   行は都道府県名、`stringResource(R.string.prefecture_completion_venue_count, completion.venueCount)`、
   手動上書きがある行には `AssistChip` で `stringResource(R.string.prefecture_completion_overridden)` を出す。
   行タップで地図と同じボトムシートを開く
5. **クレジット** — 最下部に `stringResource(R.string.prefecture_completion_credit)` を
   `MaterialTheme.typography.labelSmall` と `colorScheme.onSurfaceVariant` で表示する。
   タップで `https://uub.jp/` をブラウザで開く。

```kotlin
val context = LocalContext.current
Text(
  text = stringResource(R.string.prefecture_completion_credit),
  style = MaterialTheme.typography.labelSmall,
  color = MaterialTheme.colorScheme.onSurfaceVariant,
  modifier = Modifier
    .fillMaxWidth()
    .clickable {
      context.startActivity(Intent(Intent.ACTION_VIEW, "https://uub.jp/".toUri()))
    }
    .padding(16.dp),
)
```

- [ ] **Step 4: ボトムシートを実装する**

`feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureLevelSheet.kt`:

`ModalBottomSheet` に次を並べる。

- タイトル: 都道府県名
- 6 段階のラジオリスト。各行は色見本、レベル名、点数。現在の `effectiveLevel` を選択状態にする
- 選択で `onSelectLevel(level)` を呼ぶ
- `completion.manualLevel != null` のときだけ `TextButton` で
  `stringResource(R.string.prefecture_completion_clear_override)` を出し、`onClearOverride()` を呼ぶ

```kotlin
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PrefectureLevelSheet(
  completion: PrefectureCompletion,
  onSelectLevel: (PrefectureLevel) -> Unit,
  onClearOverride: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier) {
    // 実装は上記の並びに従う
  }
}
```

- [ ] **Step 5: ビルドと静的解析を通す**

```bash
./gradlew :feature:map:assembleDebug :feature:map:detekt :feature:map:lintLocalDebug
```

Expected: BUILD SUCCESSFUL。lint が `MissingTranslation` を出す場合は 3 ロケールすべてに
文字列が入っているか確認する。

- [ ] **Step 6: コミット**

```bash
git add feature/map/src
git commit -m "feat: 県踏破度画面を追加

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 12: ナビゲーション配線

Map 画面から県踏破度画面へ遷移できるようにする。

**Files:**
- Modify: `app/src/main/java/blue/starry/mitsubachi/RouteKey.kt`
- Modify: `app/src/main/java/blue/starry/mitsubachi/App.kt`
- Modify: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/histories/VenueHistoriesScreen.kt`

**Interfaces:**
- Consumes: `PrefectureCompletionScreen`（Task 11）、`MapScreenTopBar`（既存）
- Produces: `RouteKey.PrefectureCompletion`

**導線の決定:** 設計では Map 画面の TopBar に切替を置く想定だったが、`VenueHistoriesScreen` は
TopBar を持たない全面地図で、`AppTopBar` でも `else -> {}` に落ちている。TopBar を新設すると
地図の表示領域が削られる。既存の FAB 列（現在地・ズームイン・ズームアウト）に 4 つ目として
県踏破度ボタンを足すほうが既存の作りに沿うため、そちらを採る。
遷移先の県踏破度画面は `MapScreenTopBar` を再利用して戻れるようにする。

- [ ] **Step 1: RouteKey を追加する**

`app/src/main/java/blue/starry/mitsubachi/RouteKey.kt` の `VenueHistories` の直後に追加する。

```kotlin
  @Immutable
  @Serializable
  data object PrefectureCompletion : RouteKey
```

- [ ] **Step 2: App.kt に NavEntry を追加する**

`is RouteKey.VenueHistories -> { ... }` の直後に追加する。

```kotlin
      is RouteKey.PrefectureCompletion -> {
        NavEntry(key) {
          PrefectureCompletionScreen()
        }
      }
```

import に `blue.starry.mitsubachi.feature.map.ui.prefectures.PrefectureCompletionScreen` を追加する。

- [ ] **Step 3: App.kt の TopBar に追加する**

`AppTopBar` の `is RouteKey.Map -> { ... }` の直後に追加する。

```kotlin
    is RouteKey.PrefectureCompletion -> {
      MapScreenTopBar(
        onBack = {
          backStack.remove(key)
        },
      )
    }
```

`AppBottomBar` と `scaffoldPadding` は変更しない。県踏破度画面は TopBar を持ち、
ボトムバーは出さない（Map 画面からのサブ画面として扱う）。

- [ ] **Step 4: VenueHistoriesScreen に遷移用の FAB を追加する**

`VenueHistoriesScreen` のシグネチャに `onClickPrefectureCompletion: () -> Unit` を足し、
FAB 列の先頭（現在地ボタンの上）に次を挿入する。

```kotlin
            // 県踏破度へ
            SmallFloatingActionButton(
              onClick = onClickPrefectureCompletion,
            ) {
              Icon(
                painter = painterResource(MaterialSymbols.attractions),
                contentDescription = stringResource(R.string.prefecture_completion_open),
              )
            }
```

`MaterialSymbols` は `typealias MaterialSymbols = R.drawable` で、
`core/ui/symbols/src/main/res/drawable/` に置いた vector drawable がそのまま ID になる。
現状の一覧に踏破を表すアイコンがないため暫定で `attractions` を使う。
専用アイコンが欲しくなったら Material Symbols の vector drawable を同ディレクトリに追加すれば
`MaterialSymbols.<ファイル名>` で参照できる。一覧にない ID を書かないこと。

App.kt の `is RouteKey.VenueHistories` の `NavEntry` を次のように書き換える。

```kotlin
      is RouteKey.VenueHistories -> {
        NavEntry(key) {
          VenueHistoriesScreen(
            onClickPrefectureCompletion = {
              backStack.add(RouteKey.PrefectureCompletion)
            },
          )
        }
      }
```

- [ ] **Step 5: ビルドと全体テストを通す**

```bash
./gradlew assembleLocalDebug testLocalDebug detekt lintLocalDebug
```

Expected: BUILD SUCCESSFUL。`ArchitectureTest` も含めて通ること。
`domainLayerShouldNotDependOnAndroidPackages` が落ちた場合、`core/domain` に
Android の API（`Context` など）を持ち込んでいるので取り除く。

- [ ] **Step 6: コミット**

```bash
git add app feature/map
git commit -m "feat: Map 画面から県踏破度画面へ遷移できるようにする

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

---

### Task 13: 実機検証と配色の確定

**Files:**
- Create: `docs/superpowers/assets/prefectures/preview/`（レンダリング比較シートの出力先）
- Modify: `feature/map/src/main/java/blue/starry/mitsubachi/feature/map/ui/prefectures/PrefectureLevelColors.kt`（比較シートの結果に応じて `LEVEL_FRACTIONS` を調整）

**Interfaces:**
- Consumes: Task 1〜12 のすべて
- Produces: before / after の証跡（スクリーンショットと録画）

- [ ] **Step 1: 検証コマンドをすべて通す**

```bash
./gradlew assembleLocalDebug testLocalDebug detekt lintLocalDebug
```

Expected: BUILD SUCCESSFUL。出力をそのまま完了報告の証跡に使う。

- [ ] **Step 2: 配色のレンダリング比較シートを作る**

`@Preview` を使い、ライトとダークの両方で 6 段階の塗り分けを並べた比較シートを作る。
`LEVEL_FRACTIONS` の候補を 2〜3 パターン用意し、横並びで比較できるようにする。

比較で見るのは次の 3 点。

- 未踏とレベル 1（通過）が別の色として区別できるか
- レベル 4（宿泊）とレベル 5（居住）が区別できるか
- ダークテーマで塗りが背景に沈んでいないか

比較シートは画像として書き出し、`docs/superpowers/assets/prefectures/preview/` に置く。
判断はこのシートを見てから行い、目分量で `LEVEL_FRACTIONS` を触らない。

- [ ] **Step 3: エミュレータにインストールして画面を確認する**

```bash
./gradlew :app:installLocalDebug
```

mobile-mcp で次を確認し、スクリーンショットを撮る。

- Map 画面に県踏破度の FAB が出ていること
- 県踏破度画面でスコアが `N / 235` の形で表示されること
- 日本地図が 47 都道府県すべて描かれ、沖縄が左下のインセット枠に入っていること
- 訪問国カウンターが出ていること（実データでは 4 ヶ国前後）
- クレジットが最下部に出ていること

- [ ] **Step 4: ライトとダークの両方で確認する**

```bash
adb shell cmd uimode night yes
```

ダークテーマで同じ画面を撮り、ライトと並べて比較する。確認後に戻す。

```bash
adb shell cmd uimode night no
```

- [ ] **Step 5: レベル変更の一連の操作を録画する**

mobile-mcp の録画を開始し、次を通しで操作する。

1. 地図で都道府県をタップ
2. ボトムシートでレベルを「居住」に変更
3. 地図の色とスコアが更新されることを確認
4. リストから同じ都道府県を開き「自動判定に戻す」を押す
5. 色とスコアが元に戻ることを確認

録画は完了報告の証跡として添付する。

- [ ] **Step 6: 手動上書きが再起動後も残ることを確認する**

```bash
adb shell am force-stop blue.starry.mitsubachi.local
```

アプリを起動し直し、Step 5 で設定したレベルが残っていることを確認する。

- [ ] **Step 7: 配色の調整があればコミットする**

```bash
git add feature/map docs/superpowers/assets/prefectures/preview
git commit -m "feat: レンダリング比較にもとづきレベル配色を調整

Co-Authored-By: Claude Fable 6 <noreply@anthropic.com>"
```

- [ ] **Step 8: PR を作成する**

```bash
git push -u origin feature/prefecture-completion
```

PR の本文には次を含める。

- 機能の概要と設計ドキュメントへのリンク
- before / after のスクリーンショット（ライト・ダーク両方）と操作の録画
- 実データ検証の結果（`state` 解決率 98.9%、座標フォールバック 105 件、海外誤判定 0 件）
- `./gradlew assembleLocalDebug testLocalDebug detekt lintLocalDebug` の実行結果

画像の添付には `github-image-upload` スキル（`gh image upload`）を使う。
PR 作成後、ユーザーを Assign し、マージ可否を確認する。
未完事項が残っている場合は Draft PR にする。
