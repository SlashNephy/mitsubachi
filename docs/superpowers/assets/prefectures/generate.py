"""Natural Earth のデータから日本の 47 都道府県ポリゴンを抽出して簡略化する。

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

ADMIN1_NAME = "ne_10m_admin_1_states_provinces"
ADMIN1_URL = f"https://naciscdn.org/naturalearth/10m/cultural/{ADMIN1_NAME}.zip"

# admin-1 は北方領土をロシア (サハリン州) 側に置いており、日本の都道府県には含まれない。
# 国内向けのアプリとして北海道の一部として描くため、係争地レイヤから補う。
DISPUTED_NAME = "ne_10m_admin_0_disputed_areas"
DISPUTED_URL = f"https://naciscdn.org/naturalearth/10m/cultural/{DISPUTED_NAME}.zip"
# 北方領土に相当するレコードの BRK_NAME。NAME_JA は「千島列島」、
# NOTE_BRK は "Admin. by Russia; Claimed by Japan"。7 パートすべてが
# 択捉島・国後島・色丹島・歯舞群島で、北千島は含まれない
DISPUTED_BRK_NAME = "Kuril Is."
HOKKAIDO_CODE = 1

# admin-1 は奄美群島を沖縄県側に置いているが、実際には鹿児島県に属する。
# リングの外接矩形がこの矩形に収まるものを沖縄県から鹿児島県へ移す。
# インデックス直書きにしないのは、再生成でリングの順序が変わりうるため。
# 各境界は沖縄県のリングの外接矩形の実測から、空いている値の中央を取った。
#   西 128.16: 伊平屋島の西端 127.926E (沖縄県) と与論島の西端 128.396E のあいだ。
#     与論島 (27.021-27.067N) と伊平屋島 (27.010-27.092N) は緯度が完全に重なるため、
#     両者を分けられるのは経度だけ。
#   東 130.62: 喜界島の東端 130.030E と大東諸島の西端 131.212E (沖縄県) のあいだ。
#   南 26.49: 大東諸島の北端 25.951N (沖縄県) と与論島の南端 27.021N のあいだ。
#   北 29.00: 奄美大島の北端 28.510N とトカラ列島の南端 29.443N (鹿児島県) のあいだ。
AMAMI_BOX = (128.16, 26.49, 130.62, 29.00)
OKINAWA_CODE = 47
KAGOSHIMA_CODE = 46

SOURCE_LABEL = (
    "Natural Earth 1:10m Admin 1 - States, Provinces 5.1.1 "
    "+ Admin 0 - Breakaway, Disputed Areas 5.1.1 (Kuril Is.)"
)
SIMPLIFY_TOLERANCE = 0.005
MIN_RING_AREA = 0.0002
COORDINATE_PRECISION = 5


def download(work_dir, name, url):
    """シェープファイル一式を work_dir に展開してベース名を返す。"""
    archive = os.path.join(work_dir, f"{name}.zip")
    if not os.path.exists(archive):
        with urllib.request.urlopen(url) as response:
            payload = response.read()
        with open(archive, "wb") as f:
            f.write(payload)
    with zipfile.ZipFile(io.BytesIO(open(archive, "rb").read())) as z:
        z.extractall(work_dir)
    return os.path.join(work_dir, name)


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


def extract_rings(shape):
    """シェープのパートを簡略化した閉リングの配列にする。"""
    parts = list(shape.parts) + [len(shape.points)]
    rings = []
    for start, end in zip(parts, parts[1:]):
        ring = [
            (round(x, COORDINATE_PRECISION), round(y, COORDINATE_PRECISION))
            for x, y in shape.points[start:end]
        ]
        if ring_area(ring) < MIN_RING_AREA:
            continue
        simplified = douglas_peucker(ring, SIMPLIFY_TOLERANCE)
        if len(simplified) < 4:
            continue
        if simplified[0] != simplified[-1]:
            simplified.append(simplified[0])
        rings.append([list(point) for point in simplified])
    return rings


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
        rings = extract_rings(shape_record.shape)
        if not rings:
            continue
        prefectures.append({"code": code, "name": record[name_index], "rings": rings})

    prefectures.sort(key=lambda p: p["code"])
    return prefectures


def build_northern_territories(shapefile_base):
    """係争地レイヤから北方領土のリングを取り出す。"""
    reader = shapefile.Reader(shapefile_base)
    fields = [f[0] for f in reader.fields[1:]]
    brk_name_index = fields.index("BRK_NAME")

    for shape_record in reader.shapeRecords():
        if shape_record.record[brk_name_index] == DISPUTED_BRK_NAME:
            return extract_rings(shape_record.shape)
    raise SystemExit(f"{DISPUTED_BRK_NAME} not found in the disputed areas layer")


def is_amami(ring):
    """リングの外接矩形が [AMAMI_BOX] に収まるかを返す。"""
    west, south, east, north = AMAMI_BOX
    xs = [point[0] for point in ring]
    ys = [point[1] for point in ring]
    return min(xs) >= west and min(ys) >= south and max(xs) <= east and max(ys) <= north


def move_amami_to_kagoshima(prefectures):
    """奄美群島のリングを沖縄県から鹿児島県へ移し、移した本数を返す。"""
    okinawa = next(p for p in prefectures if p["code"] == OKINAWA_CODE)
    kagoshima = next(p for p in prefectures if p["code"] == KAGOSHIMA_CODE)
    moved = [r for r in okinawa["rings"] if is_amami(r)]
    if not moved:
        raise SystemExit("no Amami rings found in Okinawa; check AMAMI_BOX")
    okinawa["rings"] = [r for r in okinawa["rings"] if not is_amami(r)]
    kagoshima["rings"].extend(moved)
    return len(moved)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--work-dir", default=".work")
    parser.add_argument(
        "--output",
        default="../../../../core/data/src/main/assets/prefectures.json",
    )
    args = parser.parse_args()

    os.makedirs(args.work_dir, exist_ok=True)
    prefectures = build(download(args.work_dir, ADMIN1_NAME, ADMIN1_URL))

    if len(prefectures) != 47:
        raise SystemExit(f"expected 47 prefectures, got {len(prefectures)}")

    northern_territories = build_northern_territories(
        download(args.work_dir, DISPUTED_NAME, DISPUTED_URL)
    )
    hokkaido = next(p for p in prefectures if p["code"] == HOKKAIDO_CODE)
    hokkaido["rings"].extend(northern_territories)

    amami_rings = move_amami_to_kagoshima(prefectures)

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
    print(
        f"prefectures={len(prefectures)} rings={rings} points={points} "
        f"northernTerritoryRings={len(northern_territories)} amamiRings={amami_rings} "
        f"bytes={len(payload.encode())}"
    )


if __name__ == "__main__":
    main()
