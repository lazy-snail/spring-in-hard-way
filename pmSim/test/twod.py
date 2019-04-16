# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/9.


import os, sys
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import train_test_split

# data = np.random.randint(0, 255, size=[40, 40, 40])
#
# x, y, z = data[0], data[1], data[2]
# ax = plt.subplot(111, projection='3d')  # 创建一个三维的绘图工程
# #  将数据点分成三部分画，在颜色上有区分度
# ax.scatter(x[:10], y[:10], z[:10], c='y')  # 绘制数据点
# ax.scatter(x[10:20], y[10:20], z[10:20], c='r')
# ax.scatter(x[30:40], y[30:40], z[30:40], c='g')
#
# ax.set_zlabel('Z')  # 坐标轴
# ax.set_ylabel('Y')
# ax.set_xlabel('X')
# plt.show()
path = str(os.path.abspath('..'))
data = pd.read_csv(path + "//dat//sysUtilization2.csv")
x_data = data.cpu
y_data = data.power

fig = plt.figure()
ax = fig.add_subplot(111)

plt.xlabel("X")
plt.ylabel("Y")

ax.scatter(x=x_data, y=y_data, c='r')
plt.legend("X1")
plt.show()

x_data = data.mem
y_data = data.power

fig = plt.figure()
ax = fig.add_subplot(111)

plt.xlabel("X")
plt.ylabel("Y")

ax.scatter(x=x_data, y=y_data, c='r')
plt.legend("X1")
plt.show()
