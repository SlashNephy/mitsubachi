# 都道府県ポリゴンの生成

`core/data/src/main/assets/prefectures.json` を生成する。判定 (point-in-polygon)・地図描画・タップ判定で
同じデータを共用する。

## 取得元

- Natural Earth 1:10m Admin 1 – States, Provinces バージョン 5.1.1
- https://naciscdn.org/naturalearth/10m/cultural/ne_10m_admin_1_states_provinces.zip
- パブリックドメイン（Natural Earth の利用条件による）

日本の 47 都道府県が過不足なく含まれ、`iso_3166_2` が `JP-01`〜`JP-47` の形で JIS X 0401 コードと一致する。
日本語名は `name_ja` を使う。

### 北方領土

- Natural Earth 1:10m Admin 0 – Breakaway, Disputed Areas バージョン 5.1.1
- https://naciscdn.org/naturalearth/10m/cultural/ne_10m_admin_0_disputed_areas.zip

Admin 1 レイヤは北方領土をロシア（サハリン州）側に割り当てているため、日本の都道府県には 1 点も含まれない。
国内向けのアプリとして北方領土を北海道の一部として描くため、係争地レイヤから補っている。

該当するのは `BRK_NAME == "Kuril Is."` の 1 レコード（`NAME_JA` は「千島列島」、
`NOTE_BRK` は `Admin. by Russia; Claimed by Japan`、`ADM0_A3` は `RUS`）。
このレコードの 7 パートは経度 145.410–148.856 / 緯度 43.402–45.528 に収まり、
択捉島・国後島・色丹島・歯舞群島のみで北千島は含まれない。
取り込んだリングは都道府県と同じ簡略化（`SIMPLIFY_TOLERANCE` / `MIN_RING_AREA` / `COORDINATE_PRECISION`）を通し、
北海道（code 1）のリングの末尾に足している。`source` フィールドは両レイヤを併記する。

## 再生成

```bash
cd docs/superpowers/assets/prefectures
python -m venv .venv
.venv/bin/pip install -r requirements.txt
.venv/bin/python generate.py
```

出力の想定値は `prefectures=47 rings=142 points=6166 northernTerritoryRings=7 bytes=約130000`。

## Renovate

Natural Earth のデータは URL 直接ダウンロードのため Renovate では追跡できない。
バージョンを上げるときは `generate.py` の `ADMIN1_URL` / `DISPUTED_URL` / `SOURCE_LABEL` を手で更新し、再生成する。
Python の依存 (`requirements.txt`) は Renovate の pip-requirements マネージャが追跡する。
