# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/9.

import math
import os, sys
# import matplotlib.pyplot as plt
# import pandas as pd
# import seaborn as sns
# from sklearn.linear_model import LinearRegression
# from sklearn.model_selection import train_test_split

# 通过read_csv来读取我们的目的数据集
# data = pd.read_csv("D:\prj\py\py37\Sim\dat\\test.csv")
# print(data.iloc[:, 0])
# m = 0.2
# print(70.7 * m + 61.9 * m * m - 1.9 * m * m * m + 150)  # p: 170-280
# print(310-p)  # p: 270-310
# print(70.7 * m + 61.9 * m * m - 1.9 * m * m * m)  # p: 310-360
# for i in range(-10000, 10000):
# print(i/100)
#
# def get_root(p):
#     a = 3 / 280
#     b = .25
#     c = 161 - p
#     delta = b*b - 4*a*c
#     d = math.sqrt(delta)-b
#     return d/2/a
#
# print(get_root(170))

import numpy as np
import random
import matplotlib.pyplot as plt

for i in range(1000):
    x = random.gauss(0.1, 0.1)
    y = random.gauss(0.1, 0.1)
    plt.scatter(x, y)
plt.show()

print(os.getcwd())
print(os.path.abspath('.'))
print(os.path.abspath('..'))
