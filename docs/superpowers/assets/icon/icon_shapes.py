"""Mitsubachi のランチャーアイコンの図形定義。

SVG（プレビュー用）と VectorDrawable（出荷用）を同一の定義から生成し、
検証した見た目と出荷物の乖離を防ぐ。
"""

from __future__ import annotations

import math
import zlib
from dataclasses import dataclass, field

# ---------------------------------------------------------------- パス生成

def _arc(rx: float, ry: float, sweep: int, dx: float, dy: float) -> str:
    return f"a{rx},{ry} 0 1,{sweep} {dx},{dy}"


def ellipse_path(cx: float, cy: float, rx: float, ry: float, cw: bool = True) -> str:
    """楕円を 2 つの円弧で表現する。cw=False で逆回りになり、nonZero 塗りの穴になる。"""
    sweep = 1 if cw else 0
    return (
        f"M{cx - rx},{cy} "
        f"{_arc(rx, ry, sweep, 2 * rx, 0)} "
        f"{_arc(rx, ry, sweep, -2 * rx, 0)} Z"
    )


def circle_path(cx: float, cy: float, r: float, cw: bool = True) -> str:
    return ellipse_path(cx, cy, r, r, cw)


def rrect_path(x: float, y: float, w: float, h: float, r: float, cw: bool = True) -> str:
    """角丸矩形。cw=False で逆回りになる。"""
    if cw:
        return (
            f"M{x + r},{y} L{x + w - r},{y} a{r},{r} 0 0,1 {r},{r} "
            f"L{x + w},{y + h - r} a{r},{r} 0 0,1 {-r},{r} "
            f"L{x + r},{y + h} a{r},{r} 0 0,1 {-r},{-r} "
            f"L{x},{y + r} a{r},{r} 0 0,1 {r},{-r} Z"
        )
    return (
        f"M{x + r},{y} a{r},{r} 0 0,0 {-r},{r} "
        f"L{x},{y + h - r} a{r},{r} 0 0,0 {r},{r} "
        f"L{x + w - r},{y + h} a{r},{r} 0 0,0 {r},{-r} "
        f"L{x + w},{y + r} a{r},{r} 0 0,0 {-r},{-r} Z"
    )


def hex_path(cx: float, cy: float, r: float) -> str:
    """頂点が上下に来る六角形。"""
    pts = []
    for i in range(6):
        angle = math.radians(90 + 60 * i)
        pts.append((cx + r * math.cos(angle), cy - r * math.sin(angle)))
    head = f"M{pts[0][0]:.2f},{pts[0][1]:.2f}"
    rest = " ".join(f"L{x:.2f},{y:.2f}" for x, y in pts[1:])
    return f"{head} {rest} Z"


# ---------------------------------------------------------------- モデル

@dataclass
class Gradient:
    kind: str  # "linear" | "radial"
    stops: list[tuple[float, str]]
    # linear
    x1: float = 0.0
    y1: float = 0.0
    x2: float = 0.0
    y2: float = 0.0
    # radial
    cx: float = 0.0
    cy: float = 0.0
    radius: float = 0.0


@dataclass
class Shape:
    d: str
    fill: str | None = None          # "#RRGGBB" または Gradient の名前 "grad:xxx"
    fill_alpha: float = 1.0
    fill_type: str = "nonZero"       # "nonZero" | "evenOdd"
    stroke: str | None = None
    stroke_width: float = 0.0
    stroke_alpha: float = 1.0
    cap: str = "butt"                # "butt" | "round"
    join: str = "miter"              # "miter" | "round"


@dataclass
class Group:
    children: list = field(default_factory=list)
    pivot: tuple[float, float] | None = None
    rotation: float = 0.0
    scale: float = 1.0
    clip: str | None = None


@dataclass
class Layer:
    name: str
    gradients: dict[str, Gradient] = field(default_factory=dict)
    children: list = field(default_factory=list)


# ---------------------------------------------------------------- SVG 出力

def _svg_paint(value: str | None) -> str:
    if value is None:
        return "none"
    if value.startswith("grad:"):
        return f"url(#{value[5:]})"
    return value


def _svg_node(node, indent: int) -> str:
    pad = "  " * indent
    if isinstance(node, Group):
        attrs = []
        transforms = []
        if node.scale != 1.0:
            px, py = node.pivot or (0.0, 0.0)
            transforms.append(f"translate({px} {py}) scale({node.scale}) translate({-px} {-py})")
        if node.rotation:
            px, py = node.pivot or (0.0, 0.0)
            transforms.append(f"rotate({node.rotation} {px} {py})")
        if transforms:
            attrs.append(f'transform="{" ".join(transforms)}"')
        clip_open = ""
        clip_close = ""
        if node.clip is not None:
            # Python の hash() は実行ごとに乱数化されるため、id には使わない
            clip_id = f"clip{zlib.crc32(node.clip.encode()) % 100000}"
            clip_open = (
                f'{pad}<clipPath id="{clip_id}"><path d="{node.clip}"/></clipPath>\n'
                f'{pad}<g clip-path="url(#{clip_id})">\n'
            )
            clip_close = f"{pad}</g>\n"
            indent += 1
            pad = "  " * indent
        inner = "".join(_svg_node(c, indent + 1) for c in node.children)
        body = f'{pad}<g {" ".join(attrs)}>\n{inner}{pad}</g>\n'
        return clip_open + body + clip_close
    attrs = [f'd="{node.d}"', f'fill="{_svg_paint(node.fill)}"']
    if node.fill_type == "evenOdd":
        attrs.append('fill-rule="evenodd"')
    if node.fill_alpha != 1.0:
        attrs.append(f'fill-opacity="{node.fill_alpha}"')
    if node.stroke is not None:
        attrs.append(f'stroke="{node.stroke}" stroke-width="{node.stroke_width}"')
        if node.stroke_alpha != 1.0:
            attrs.append(f'stroke-opacity="{node.stroke_alpha}"')
        if node.cap != "butt":
            attrs.append(f'stroke-linecap="{node.cap}"')
        if node.join != "miter":
            attrs.append(f'stroke-linejoin="{node.join}"')
    return f'{pad}<path {" ".join(attrs)}/>\n'


def to_svg(layer: Layer) -> str:
    defs = ""
    for name, g in layer.gradients.items():
        stops = "".join(
            f'      <stop offset="{o}" stop-color="{c}"/>\n' for o, c in g.stops
        )
        if g.kind == "linear":
            defs += (
                f'    <linearGradient id="{name}" gradientUnits="userSpaceOnUse" '
                f'x1="{g.x1}" y1="{g.y1}" x2="{g.x2}" y2="{g.y2}">\n{stops}'
                f"    </linearGradient>\n"
            )
        else:
            defs += (
                f'    <radialGradient id="{name}" gradientUnits="userSpaceOnUse" '
                f'cx="{g.cx}" cy="{g.cy}" r="{g.radius}">\n{stops}'
                f"    </radialGradient>\n"
            )
    body = "".join(_svg_node(c, 1) for c in layer.children)
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" width="108" height="108" '
        'viewBox="0 0 108 108">\n'
        f"  <defs>\n{defs}  </defs>\n"
        f"{body}</svg>\n"
    )


# ---------------------------------------------------------------- VD 出力

_VD_CAP = {"butt": "butt", "round": "round"}
_VD_JOIN = {"miter": "miter", "round": "round"}


def _vd_gradient(g: Gradient, indent: int) -> str:
    pad = "  " * indent
    if g.kind == "linear":
        head = (
            f'{pad}<gradient android:type="linear" '
            f'android:startX="{g.x1}" android:startY="{g.y1}" '
            f'android:endX="{g.x2}" android:endY="{g.y2}">'
        )
    else:
        head = (
            f'{pad}<gradient android:type="radial" '
            f'android:centerX="{g.cx}" android:centerY="{g.cy}" '
            f'android:gradientRadius="{g.radius}">'
        )
    items = "".join(
        f'{pad}  <item android:color="{c}" android:offset="{o}"/>\n' for o, c in g.stops
    )
    return f"{head}\n{items}{pad}</gradient>\n"


def _vd_node(node, layer: Layer, indent: int) -> str:
    pad = "  " * indent
    if isinstance(node, Group):
        attrs = []
        if node.pivot is not None and (node.scale != 1.0 or node.rotation):
            attrs.append(f'android:pivotX="{node.pivot[0]}"')
            attrs.append(f'android:pivotY="{node.pivot[1]}"')
        if node.scale != 1.0:
            attrs.append(f'android:scaleX="{node.scale}"')
            attrs.append(f'android:scaleY="{node.scale}"')
        if node.rotation:
            attrs.append(f'android:rotation="{node.rotation}"')
        clip = ""
        if node.clip is not None:
            clip = f'{pad}  <clip-path android:pathData="{node.clip}"/>\n'
        inner = "".join(_vd_node(c, layer, indent + 1) for c in node.children)
        joined = ("\n" + pad + "    ").join(attrs) if attrs else ""
        open_tag = f"{pad}<group {joined}>" if attrs else f"{pad}<group>"
        return f"{open_tag}\n{clip}{inner}{pad}</group>\n"

    attrs = [f'android:pathData="{node.d}"']
    gradient_child = None
    if node.fill is not None:
        if node.fill.startswith("grad:"):
            gradient_child = layer.gradients[node.fill[5:]]
        else:
            attrs.append(f'android:fillColor="{node.fill}"')
    else:
        attrs.append('android:fillColor="#00000000"')
    if node.fill_type == "evenOdd":
        attrs.append('android:fillType="evenOdd"')
    if node.fill_alpha != 1.0:
        attrs.append(f'android:fillAlpha="{node.fill_alpha}"')
    if node.stroke is not None:
        attrs.append(f'android:strokeColor="{node.stroke}"')
        attrs.append(f'android:strokeWidth="{node.stroke_width}"')
        if node.stroke_alpha != 1.0:
            attrs.append(f'android:strokeAlpha="{node.stroke_alpha}"')
        if node.cap != "butt":
            attrs.append(f'android:strokeLineCap="{_VD_CAP[node.cap]}"')
        if node.join != "miter":
            attrs.append(f'android:strokeLineJoin="{_VD_JOIN[node.join]}"')

    joined = ("\n" + pad + "    ").join(attrs)
    if gradient_child is None:
        return f"{pad}<path {joined}/>\n"
    grad = _vd_gradient(gradient_child, indent + 2)
    return (
        f"{pad}<path {joined}>\n"
        f'{pad}  <aapt:attr name="android:fillColor">\n'
        f"{grad}"
        f"{pad}  </aapt:attr>\n"
        f"{pad}</path>\n"
    )


def to_vector_drawable(layer: Layer) -> str:
    body = "".join(_vd_node(c, layer, 1) for c in layer.children)
    return (
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
        '    xmlns:aapt="http://schemas.android.com/aapt"\n'
        '    android:width="108dp"\n'
        '    android:height="108dp"\n'
        '    android:viewportWidth="108"\n'
        '    android:viewportHeight="108">\n'
        f"{body}"
        "</vector>\n"
    )


# ---------------------------------------------------------------- レイヤー定義

OUTLINE = "#4A2A00"
EYE = "#3A2000"
BLUSH = "#FF7A59"
WING = "#FFFFFF"

BODY_CX, BODY_CY, BODY_RX, BODY_RY = 54.0, 58.0, 25.0, 23.0
BODY_PATH = ellipse_path(BODY_CX, BODY_CY, BODY_RX, BODY_RY)

_HEX_CENTERS = [
    (14, 10), (14, 42), (14, 74),
    (41.6, -6), (41.6, 26), (41.6, 58), (41.6, 90),
    (69.2, 10), (69.2, 42), (69.2, 74),
    (96.8, 26), (96.8, 58),
]

BACKGROUND = Layer(
    name="ic_launcher_background",
    gradients={
        "bg": Gradient(
            kind="radial", cx=54.0, cy=37.8, radius=91.8,
            stops=[(0.0, "#FFD98A"), (1.0, "#FFA633")],
        ),
    },
    children=[
        Shape(d=rrect_path(0, 0, 108, 108, 0.001), fill="grad:bg"),
        Group(children=[
            Shape(d=hex_path(cx, cy, 16), fill=None, stroke=WING,
                  stroke_width=2.6, stroke_alpha=0.30)
            for cx, cy in _HEX_CENTERS
        ]),
        Shape(d=circle_path(54, 55, 38), fill="#FFF6E0", fill_alpha=0.55),
    ],
)

_BEE = [
    # 触角（線）
    Shape(d="M46,39 C43,32 41,29 39,27.5", fill=None, stroke=OUTLINE,
          stroke_width=3.2, cap="round"),
    Shape(d="M62,39 C65,32 67,29 69,27.5", fill=None, stroke=OUTLINE,
          stroke_width=3.2, cap="round"),
    # 触角（先端の玉）
    Shape(d=circle_path(38, 26.5, 3.2), fill=OUTLINE),
    Shape(d=circle_path(70, 26.5, 3.2), fill=OUTLINE),
    # 羽根
    Group(pivot=(33, 43), rotation=-28, children=[
        Shape(d=ellipse_path(33, 43, 12, 8.5), fill=WING, fill_alpha=0.92,
              stroke=OUTLINE, stroke_width=3.4),
    ]),
    Group(pivot=(75, 43), rotation=28, children=[
        Shape(d=ellipse_path(75, 43, 12, 8.5), fill=WING, fill_alpha=0.92,
              stroke=OUTLINE, stroke_width=3.4),
    ]),
    # 胴体
    Shape(d=BODY_PATH, fill="grad:body"),
    # 縞（胴体でクリップ）
    Group(clip=BODY_PATH, children=[
        Group(pivot=(BODY_CX, BODY_CY), rotation=-5, children=[
            Shape(d=rrect_path(24, 62, 60, 8, 4), fill=OUTLINE),
            Shape(d=rrect_path(24, 74, 60, 8, 4), fill=OUTLINE),
        ]),
    ]),
    # 胴体の輪郭
    Shape(d=BODY_PATH, fill=None, stroke=OUTLINE, stroke_width=4),
    # 顔
    Shape(d=ellipse_path(37.5, 57, 5, 3.2), fill=BLUSH, fill_alpha=0.6),
    Shape(d=ellipse_path(70.5, 57, 5, 3.2), fill=BLUSH, fill_alpha=0.6),
    Shape(d=circle_path(45.5, 50, 4.3), fill=EYE),
    Shape(d=circle_path(62.5, 50, 4.3), fill=EYE),
    Shape(d=circle_path(44.1, 48.4, 1.7), fill=WING),
    Shape(d=circle_path(61.1, 48.4, 1.7), fill=WING),
    Shape(d="M49.5,56.5 Q54,61 58.5,56.5", fill=None, stroke=EYE,
          stroke_width=2.6, cap="round"),
]

FOREGROUND = Layer(
    name="ic_launcher_foreground",
    gradients={
        "body": Gradient(
            kind="linear", x1=54.0, y1=35.0, x2=54.0, y2=81.0,
            stops=[(0.0, "#FFE380"), (1.0, "#FFC12E")],
        ),
    },
    children=[Group(pivot=(54, 54), scale=0.87, children=_BEE)],
)

_MONO = "#FFFFFF"

# 口は線ではなく塗りの帯として表現する（単色化すると細線が消えるため太らせる）
_MOUTH_HOLE = (
    "M45.8,58.0 Q54,66.4 62.2,58.0 L59.4,55.6 Q54,62 48.6,55.6 Z"
)

# 羽根と胴体の隙間（variant B）: キャンバス全体の矩形から、胴体楕円を
# 2 単位膨らませた楕円（逆回り）を抜いた領域。羽根の回転 Group の外側に
# clip として適用することで、clip 自体は無回転（ワールド座標）のまま
# 胴体境界の外側 2 単位の帯だけを隠し、羽根と胴体の間に隙間を作る。
_WING_GAP_CLIP = " ".join([
    rrect_path(0, 0, 108, 108, 0.001),
    ellipse_path(BODY_CX, BODY_CY, BODY_RX + 2, BODY_RY + 2, cw=False),
])

_MONOCHROME_BEE = [
    # 触角（線）と先端の玉 — 穴を持たないので独立したパス
    Shape(d="M46,39 C43,32 41,29 39,27.5", fill=None, stroke=_MONO,
          stroke_width=3.6, cap="round"),
    Shape(d="M62,39 C65,32 67,29 69,27.5", fill=None, stroke=_MONO,
          stroke_width=3.6, cap="round"),
    Shape(d=circle_path(38, 26.5, 3.4), fill=_MONO),
    Shape(d=circle_path(70, 26.5, 3.4), fill=_MONO),
    # 羽根 — 胴体との間に 2 単位の透明な隙間を空け、マッシュルーム状の
    # 一体化した輪郭に見えないようにする。clip は回転 Group の外側の
    # Group に付け、ワールド座標のまま（羽根と一緒に回転させない）。
    Group(clip=_WING_GAP_CLIP, children=[
        Group(pivot=(33, 43), rotation=-28, children=[
            Shape(d=ellipse_path(33, 43, 12, 8.5), fill=_MONO),
        ]),
    ]),
    Group(clip=_WING_GAP_CLIP, children=[
        Group(pivot=(75, 43), rotation=28, children=[
            Shape(d=ellipse_path(75, 43, 12, 8.5), fill=_MONO),
        ]),
    ]),
    # 胴体 + 抜き穴（逆回り）: 目2つと口のみ。縞は単色化するとひげに
    # 見えるため穴として持たない。
    Shape(
        d=" ".join([
            ellipse_path(BODY_CX, BODY_CY, BODY_RX, BODY_RY),
            circle_path(45.5, 50, 5.0, cw=False),
            circle_path(62.5, 50, 5.0, cw=False),
            _MOUTH_HOLE,
        ]),
        fill=_MONO,
    ),
]

MONOCHROME = Layer(
    name="ic_launcher_monochrome",
    children=[Group(pivot=(54, 54), scale=0.87, children=_MONOCHROME_BEE)],
)

LAYERS = [BACKGROUND, FOREGROUND, MONOCHROME]


if __name__ == "__main__":
    import pathlib
    import sys

    out_dir = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    res_dir = pathlib.Path(sys.argv[2]) if len(sys.argv) > 2 else None
    for layer in LAYERS:
        (out_dir / f"{layer.name}.svg").write_text(to_svg(layer), encoding="utf-8")
        print(f"wrote {layer.name}.svg")
        if res_dir is not None:
            (res_dir / f"{layer.name}.xml").write_text(
                to_vector_drawable(layer), encoding="utf-8"
            )
            print(f"wrote {layer.name}.xml")
