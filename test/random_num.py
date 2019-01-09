# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.


import random

# 生成随机数，浮点类型
# 控制随机数的精度round(数值，精度)
for _ in range(90):
    p = random.uniform(167.5, 180)
    cpu = random.uniform(0, 25)
    mem = random.uniform(7, 30)
    print(round(p, 1))
    # print(round(cpu, 1))
    # print(round(mem, 1))
    # print(round(p, 1), round(cpu, 1), round(mem, 1))
