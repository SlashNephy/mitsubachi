"""Mitsubachi のランチャーアイコンの図形定義。

SVG（プレビュー用）と VectorDrawable（出荷用）を同一の定義から生成し、
検証した見た目と出荷物の乖離を防ぐ。
"""

from __future__ import annotations

import math
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
            clip_id = f"clip{abs(hash(node.clip)) % 100000}"
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

LAYERS = [BACKGROUND, FOREGROUND]


if __name__ == "__main__":
    import pathlib
    import sys

    out_dir = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    for layer in LAYERS:
        (out_dir / f"{layer.name}.svg").write_text(to_svg(layer), encoding="utf-8")
        print(f"wrote {layer.name}.svg")
