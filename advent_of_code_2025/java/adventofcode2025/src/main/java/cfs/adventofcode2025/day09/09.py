from collections import defaultdict
import numpy as np

p = np.loadtxt("input.txt", delimiter=",", dtype=int)

maxa = 0
maxb = 0
by_x = defaultdict(list)
by_y = defaultdict(list)
best_rect = None

for q in p:
    by_x[q[0]].append(q[1])
    by_y[q[1]].append(q[0])

h_segs = []
v_segs = []

for x, ys in by_x.items():
    yss = sorted(ys)
    for i in range(len(yss) // 2):
        a = [x, yss[2 * i]]
        b = [x, yss[2 * i + 1]]
        h_segs.append((a, b))

for y, xs in by_y.items():
    xss = sorted(xs)
    for i in range(len(xss) // 2):
        a = [xss[2 * i], y]
        b = [xss[2 * i + 1], y]
        v_segs.append((a, b))

for i, a in enumerate(p):
    for b in p[:i, :]:
        minx = min(a[0], b[0])
        maxx = max(a[0], b[0])

        miny = min(a[1], b[1])
        maxy = max(a[1], b[1])

        works = True
        for h0, h1 in h_segs:
            hx = h0[0]
            hy0 = min(h0[1], h1[1])
            hy1 = max(h0[1], h1[1])
            if hx > minx and hx < maxx:
                ok = hy1 <= miny or hy0 >= maxy
                if not ok:
                    works = False
                    break

        if works:
            for v0, v1 in v_segs:
                vy = v0[1]
                vx0 = min(v0[0], v1[0])
                vx1 = max(v0[0], v1[0])
                if vy > miny and vy < maxy:
                    ok = vx1 <= minx or vx0 >= maxx
                    if not ok:
                        works = False
                        break

        area = np.prod(np.abs(a - b) + 1)
        maxa = max(area, maxa)
        if works:
            if area > maxb:
                maxb = area
                best_rect = (
                    minx,
                    miny,
                    maxx,
                    maxy,
                )

print(maxa)
print(maxb)

if best_rect:
    bx0, by0, bx1, by1 = best_rect
    bw = abs(bx1 - bx0) + 1
    bh = abs(by1 - by0) + 1
    extras = np.array([[bx0, bx1 + 1], [by0, by1 + 1]])
else:
    bx0 = by0 = bw = bh = 0
    extras = np.empty((2, 0), dtype=int)

coords = np.concatenate([p.T, extras], axis=1)
x_min = np.min(coords[0])
x_max = np.max(coords[0])
y_min = np.min(coords[1])
y_max = np.max(coords[1])

width = max(1, x_max - x_min + 1)
height = max(1, y_max - y_min + 1)

scale = 100
lines = []
for a, b in h_segs:
    lines.append(
        f'<line x1="{a[0] / scale}" y1="{a[1] / scale}" x2="{b[0] / scale}" y2="{b[1] / scale}" stroke="black" stroke-width="1" stroke-linecap="square" />'
    )
for a, b in v_segs:
    lines.append(
        f'<line x1="{a[0] / scale}" y1="{a[1] / scale}" x2="{b[0] / scale}" y2="{b[1] / scale}" stroke="black" stroke-width="1" stroke-linecap="square" />'
    )
rect = ""
if best_rect:
    rect = f'<rect x="{bx0 / scale}" y="{by0 / scale}" width="{bw / scale}" height="{bh / scale}" fill="#790096" />'

# Connor - first wrong answer
#   resultPartTwoRectangle: java.awt.Rectangle[x=13983,y=18816,width=72121,height=62443]
# con1x = 13983
# con1y = 18816
# conw = 72121
# conh = 62443
# Connor - second answer
# resultPartTwoRectangle: java.awt.Rectangle[x=6271,y=50187,width=88311,height=18483]
con1x = 6271
con1y = 50187
conw = 88311
conh = 18483
con_rect = f'<rect x="{con1x / scale}" y="{con1y / scale}" width="{conw / scale}" height="{conh / scale}" fill="#ffeead" />'


svg = [
    f'<svg xmlns="http://www.w3.org/2000/svg" width="{width // scale + 1}" height="{height // scale + 1}" viewBox="{x_min // scale} {y_min // scale} {width // scale + 1} {height // scale + 1}">',
    f'<rect x="{x_min // scale}" y="{y_min // scale}" width="{width // scale + 1}" height="{height // scale + 1}" fill="white" />',
    rect,
    con_rect,
    *lines,
    "</svg>",
]

with open("09.svg", "w") as f:
    f.write("\n".join(filter(None, svg)))
