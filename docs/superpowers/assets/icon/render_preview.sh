#!/usr/bin/env bash
# 背景と前景を合成し、円 / スクワークルマスクと 48px 縮小を適用した検証シートを作る。
# usage: ./render_preview.sh <out_dir>
set -euo pipefail

dir="$(cd "$(dirname "$0")" && pwd)"
out="${1:-$dir/preview}"
mkdir -p "$out"

rsvg-convert -w 432 -h 432 "$dir/ic_launcher_background.svg" -o "$out/bg.png"
rsvg-convert -w 432 -h 432 "$dir/ic_launcher_foreground.svg" -o "$out/fg.png"
magick "$out/bg.png" "$out/fg.png" -compose over -composite "$out/full.png"

# 108dp のうち中央 72dp のみが表示される
crop() {
  magick "$out/full.png" -resize 648x648 -gravity center -crop 432x432+0+0 +repage "$1"
}
crop "$out/visible.png"

magick "$out/visible.png" \
  \( -size 432x432 xc:none -fill white -draw "circle 216,216 216,4" \) \
  -alpha set -compose DstIn -composite "$out/mask_circle.png"
magick "$out/visible.png" \
  \( -size 432x432 xc:none -fill white -draw "roundrectangle 0,0 431,431 118,118" \) \
  -alpha set -compose DstIn -composite "$out/mask_squircle.png"
magick "$out/mask_circle.png" -resize 48x48 "$out/size48.png"
magick "$out/size48.png" -resize 432x432 "$out/size48_zoom.png"

magick \( "$out/full.png" "$out/mask_circle.png" +append \) \
       \( "$out/mask_squircle.png" "$out/size48_zoom.png" +append \) \
       -background '#FAFAFA' -append -bordercolor '#FAFAFA' -border 16 "$out/sheet.png"
echo "wrote $out/sheet.png"
