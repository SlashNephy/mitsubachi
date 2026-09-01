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

### 奄美群島

Admin 1 レイヤは奄美群島を沖縄県 (`JP-47`) に割り当てているが、実際には鹿児島県 (`JP-46`) に属する。
北方領土と同じく元データ側の割り当ての問題なので、生成時にリングを付け替えている。

対象は外接矩形が `AMAMI_BOX`（西 128.16E / 南 26.49N / 東 130.62E / 北 29.00N）に収まるリングで、
奄美大島・加計呂麻島・徳之島・沖永良部島・与論島・喜界島の 6 本が該当する。
リングのインデックス直書きにしないのは、再生成で順序が変わりうるため。境界は沖縄県のリングの
外接矩形の実測から、空いている値の中央を取った。

- 西 128.16: 伊平屋島の西端 127.926E（沖縄県）と与論島の西端 128.396E のあいだ。
  与論島 (27.021–27.067N) と伊平屋島 (27.010–27.092N) は緯度が完全に重なるため、
  両者を分けられるのは経度だけで、緯度のしきい値では分けられない
- 東 130.62: 喜界島の東端 130.030E と大東諸島の西端 131.212E（沖縄県）のあいだ
- 南 26.49: 大東諸島の北端 25.951N（沖縄県）と与論島の南端 27.021N のあいだ
- 北 29.00: 奄美大島の北端 28.510N とトカラ列島の南端 29.443N（鹿児島県）のあいだ

該当が 0 本のときは元データの変化を疑うべきなので、生成を失敗させる。

## 再生成

```bash
cd docs/superpowers/assets/prefectures
python -m venv .venv
.venv/bin/pip install -r requirements.txt
.venv/bin/python generate.py
```

出力の想定値は `prefectures=47 rings=142 points=6166 northernTerritoryRings=7 amamiRings=6 bytes=約130000`。

## Renovate

Natural Earth のデータは URL 直接ダウンロードのため Renovate では追跡できない。
バージョンを上げるときは `generate.py` の `ADMIN1_URL` / `DISPUTED_URL` / `SOURCE_LABEL` を手で更新し、再生成する。
Python の依存 (`requirements.txt`) は Renovate の pip-requirements マネージャが追跡する。
