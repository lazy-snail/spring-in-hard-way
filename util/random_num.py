# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.


import random

# 生成随机数，浮点类型
# 控制随机数的精度round(数值，精度)
for _ in range(990):
    a = random.uniform(0, 17)
    print(round(a, 1))
