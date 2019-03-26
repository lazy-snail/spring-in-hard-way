# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/12.
import random


def get_power(c, m, ep):
    c /= 100
    m /= 100
    res = (375.9 * c - 401 * c * c + 164.5 * c * c * c \
           - 31 * m + 41.9 * m * m - 19.9 * m * m * m) \
          + 230 + ep
    return "%.2f" % res


print("power,cpu,mem")
# 高斯分布细化修正
# 0 cpu: 2.5-10, mem: 8.7-13.2, n: 100, ep: ±2.5
for i in range(100):
    # cpu = random.uniform(2.5, 10.1)
    # mem = random.uniform(16.7, 28.2)
    cpu = random.normalvariate(6.2, 2)
    mem = random.normalvariate(22.5, 3.8)
    ep = random.uniform(-2.5, 2.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 1 cpu: 8.1-20.5, mem: 11.5-41, n: 250, ep: ±2.9
for i in range(100):
    cpu = random.uniform(8.1, 20.5)
    mem = random.uniform(25.5, 41)
    ep = random.uniform(-2.5, 2.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))
for i in range(150):
    cpu = random.normalvariate(14.3, 3.9)
    mem = random.normalvariate(33.2, 5)
    ep = random.uniform(-2.5, 2.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 2 cpu: 18.3-23.7, mem: 23.3-58.5, n: 150, ep: ±3.4
for i in range(150):
    cpu = random.uniform(18.3, 23.7)
    mem = random.uniform(30.3, 58.5)
    ep = random.uniform(-3.4, 3.5)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 3 cpu: 21.0-31.1, mem: 37.6-68.9, n: 200, ep: ±2.4
for i in range(100):
    cpu = random.uniform(21.0, 31.1)
    mem = random.uniform(39.6, 68.9)
    ep = random.uniform(-2.3, 2.4)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 4 cpu: 30.1-42.5, mem: 49.5-81.5, n: 200, ep: ±1.6
for i in range(130):
    cpu = random.uniform(30.1, 42.5)
    mem = random.uniform(49.5, 89.5)
    ep = random.uniform(-1.5, 1.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))
for i in range(70):
    cpu = random.normalvariate(36.3, 3.5)
    mem = random.normalvariate(69.5, 12)
    ep = random.uniform(-1.5, 1.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 5 cpu: 40.5-45.5, mem: 63.2-97.6, n: 100, ep: ±1.1
for i in range(90):
    cpu = random.uniform(40.5, 45.5)
    mem = random.uniform(73.2, 88.6)
    ep = random.uniform(-1.1, 1.1)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))
for i in range(60):
    cpu = random.normalvariate(43, 3)
    mem = random.normalvariate(70.4, 3.6)
    ep = random.uniform(-1.5, 1.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 6 cpu: 43.1-50.8, mem: 60.7-79.3, n: 200, ep: ±2.1
for i in range(160):
    cpu = random.uniform(43.1, 50.8)
    mem = random.uniform(15.7, 85.3)
    ep = random.uniform(-2.1, 2.1)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))
for i in range(90):
    cpu = random.normalvariate(46.9, 1.9)
    mem = random.normalvariate(50, 15.1)
    ep = random.uniform(-2.5, 2.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 7 cpu: 47.8-70.1, mem: 52.3-67.4, n: 300, ep: ±3.1 细化
for i in range(120):
    cpu = random.uniform(47.8, 70.1)
    mem = random.uniform(11.3, 46.4)
    ep = random.uniform(-1.1, 3.1)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))
for i in range(80):
    cpu = random.gauss(58.9, 5.2)
    mem = random.gauss(28.4, 5.2)
    ep = random.uniform(-1.5, 1.6)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 8 cpu: 68.2-80.1, mem: 35.8-57.6, n: 200, ep: ±3.2
for i in range(200):
    cpu = random.uniform(68.2, 80.1)
    mem = random.uniform(31.8, 57.6)
    ep = random.uniform(-1.2, 1.2)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 9 cpu: 75.6-85.4, mem: 27.5-73.2, n: 150, ep: ±3.7
for i in range(150):
    cpu = random.uniform(75.6, 85.4)
    mem = random.uniform(42.5, 71.2)
    ep = random.uniform(-2.7, 1.7)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 10 cpu: 82.1-91.7, mem: 53.7-83.5, n: 150, ep: ±4.3
for i in range(150):
    cpu = random.uniform(82.1, 91.7)
    mem = random.uniform(58.7, 83.5)
    ep = random.uniform(-2.3, 2.3)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))

# 11 cpu: 89.5-98.7, mem: 70.3-95.6, n: 100, ep: ±4.7
for i in range(100):
    cpu = random.uniform(89.5, 98.7)
    mem = random.uniform(70.3, 99.6)
    ep = random.uniform(-2.6, 2.7)
    print("%s, %.2f, %.2f" % (get_power(cpu, mem, ep), cpu, mem))
