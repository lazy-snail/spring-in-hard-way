# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/12.
import random
import math


def get_root(p):
    a = 3 / 280
    b = .25
    c = 161 - p
    ep = random.uniform(-21.7, 13.2)
    delta = b * b - 4 * a * c + ep
    if delta < 0:
        delta = 0 - delta
    d = math.sqrt(delta) - b
    return d / 2 / a


def get_mem(p):
    res = 0
    if p <= 270:
        ep = random.uniform(-10.7, 13.2)
        res = get_root(p) + ep
    if 270 < p < 300:
        ep = random.uniform(-13.7, 13.2)
        res = 630 - p * 2 + ep
    if p >= 300:
        ep = random.uniform(-12.7, 9.2)
        res = p - 270 + ep
    return "%.2f" % res


# 0 cpu: 2.5-10, n: 50, ep: ±9.5, power: 170
for i in range(20):
    cpu = random.uniform(2.5, 10.1)
    ep = random.uniform(-2.5, 2.6)
    power = ep + 170
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 1 cpu: 7.6-21.3, n: 150, ep: ±10.5, power: 185
for i in range(50):
    cpu = random.uniform(7.6, 21.3)
    ep = random.uniform(-3.5, 3.6)
    power = ep + 185
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 2 cpu: 18.3-31.7, n: 100, ep: ±8.5, power: 210
for i in range(35):
    cpu = random.uniform(18.3, 31.7)
    ep = random.uniform(-2.5, 1.6)
    power = ep + 210
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 3 cpu: 31.0-42.9, n: 150, ep: ±10.5, power: 225
for i in range(50):
    cpu = random.uniform(31.0, 42.9)
    ep = random.uniform(-1.5, 2.6)
    power = ep + 225
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 4 cpu: 30.0-57.9, n: 200, ep: ±10.5, power: 245
for i in range(80):
    cpu = random.uniform(30.0, 57.9)
    ep = random.uniform(-5.5, 3.6)
    power = ep + 245
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 5 cpu: 39.2-52.1, n: 150, ep: ±10.5, power: 270
for i in range(50):
    cpu = random.uniform(37.2, 52.1)
    ep = random.uniform(-4.5, 4.6)
    power = ep + 270
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 6 cpu: 51.1-63.2, n: 300, ep: ±10.5, power: 310
for i in range(100):
    cpu = random.uniform(51.1, 63.2)
    ep = random.uniform(-3.5, 1.6)
    power = ep + 310
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 7 cpu: 50.1-72.2, n: 200, ep: ±10.5, power: 330
for i in range(120):
    cpu = random.uniform(50.1, 72.2)
    ep = random.uniform(-1.5, 2.6)
    power = ep + 330
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 8 cpu: 63.1-82.2, n: 300, ep: ±10.5, power: 345
for i in range(70):
    cpu = random.uniform(63.1, 82.2)
    ep = random.uniform(-3.5, 1.6)
    power = ep + 345
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))

# 9 cpu: 79-97, n: 300, ep: ±10.5, power: 360
for i in range(100):
    cpu = random.uniform(79, 97)
    ep = random.uniform(-1.5, 4.6)
    power = ep + 360
    mem = get_mem(power)
    print("%.2f, %.2f, %s" % (power, cpu, get_mem(power)))
