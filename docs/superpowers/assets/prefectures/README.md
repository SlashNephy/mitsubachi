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
