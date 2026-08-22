#!/usr/bin/env bash
# レガシー mipmap（adaptive icon 非対応の消費者向け）を SVG から再生成する。
# 出力は PNG。Firebase App Distribution など APK からアイコンを取り出す外部の
# コンシューマには WebP を解釈できないものがあるため、可搬性の高い形式で出す。
set -euo pipefail

dir="$(cd "$(dirname "$0")" && pwd)"
res="$dir/../../../../app/src/main/res"
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

rsvg-convert -w 768 -h 768 "$dir/ic_launcher_background.svg" -o "$tmp/bg.png"
rsvg-convert -w 768 -h 768 "$dir/ic_launcher_foreground.svg" -o "$tmp/fg.png"
magick "$tmp/bg.png" "$tmp/fg.png" -compose over -composite \
  -resize 1152x1152 -gravity center -crop 768x768+0+0 +repage "$tmp/visible.png"

magick "$tmp/visible.png" \
  \( -size 768x768 xc:none -fill white -draw "roundrectangle 0,0 767,767 210,210" \) \
  -alpha set -compose DstIn -composite "$tmp/squircle.png"
magick "$tmp/visible.png" \
  \( -size 768x768 xc:none -fill white -draw "circle 384,384 384,4" \) \
  -alpha set -compose DstIn -composite "$tmp/circle.png"

emit() { # $1=density $2=size
  mkdir -p "$res/mipmap-$1"
  magick "$tmp/squircle.png" -resize "$2x$2" "$res/mipmap-$1/ic_launcher.png"
  magick "$tmp/circle.png" -resize "$2x$2" "$res/mipmap-$1/ic_launcher_round.png"
  echo "wrote mipmap-$1 (${2}px)"
}

emit mdpi 48
emit hdpi 72
emit xhdpi 96
emit xxhdpi 144
emit xxxhdpi 192
