# ランチャーアイコン素材

Mitsubachi のランチャーアイコン（ミツバチ）の生成元一式。詳細な意匠仕様は
`docs/superpowers/specs/2026-08-21-app-icon-design.md` を参照。

## 構成

- `icon_shapes.py` — 図形定義の唯一の原本（Single Source of Truth）。
  背景・前景・monochrome の 3 レイヤーを SVG（プレビュー用）と
  VectorDrawable XML（出荷用）の両方に出力する。
- `ic_launcher_{background,foreground,monochrome}.svg` — `icon_shapes.py` の生成物。
- `render_preview.sh` — SVG から検証用のプレビューシート（`preview/` 配下）を作る。
- `generate_mipmaps.sh` — レガシー mipmap（`mipmap-*dpi/ic_launcher*.png`）を SVG から再生成する。
- `preview/` — プレビュー・検証用のスクラッチ領域（`.gitignore` 済み）。証跡画像は
  コミットせず、PR に添付する。

`app/src/main/res/drawable/ic_launcher_{background,foreground,monochrome}.xml` は
**`icon_shapes.py` からの生成物であり、手で編集してはならない**。Android Studio
のアイコンエディタ等で直接編集すると、この SSOT と出荷物が乖離する。

## 再生成コマンド

図形定義（`icon_shapes.py` 内の `Shape` / `Group` / `Layer`）を変更したら、次を
すべて実行して整合を取る。

```sh
# SVG（プレビュー用）と VectorDrawable（出荷用）を同時に再生成する
cd docs/superpowers/assets/icon && python3 icon_shapes.py . ../../../../app/src/main/res/drawable

# レガシー mipmap（mipmap-*dpi 配下の PNG）を再生成する
./generate_mipmaps.sh

# 検証用プレビューシートを再生成する
./render_preview.sh
```

## 必要な外部ツール

- `python3` — mise の `python` ツールでインストールできる。
- `imagemagick`（`magick` コマンド）— mise の `imagemagick` ツール（conda backend）でインストールできる。
- `rsvg-convert` — mise の `conda:librsvg`（conda backend）でインストールできる。レジストリに
  短縮名が無いため、`mise.toml` ではフル修飾名 `"conda:librsvg"` で参照する。

いずれも `mise.toml` にバージョン固定で登録済み。`mise install` で揃い、外部の
前提ツールは残っていない。
