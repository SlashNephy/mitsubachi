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
