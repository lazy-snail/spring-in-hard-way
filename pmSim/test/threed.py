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
PLT_FIGURE = plt.figure(figsize=(12, 9))

path = str(os.path.abspath('..'))
data = pd.read_csv(path + "//dat//sysUtilization2.csv")

x = data.mem
y = data.cpu
z = data.power

ax = plt.subplot(111, projection='3d')  # 创建一个三维的绘图工程

# #  将数据点分成三部分画，在颜色上有区分度
# ax.scatter(x[:10], y[:10], z[:10], c='y')  # 绘制数据点
# ax.scatter(x[10:20], y[10:20], z[10:20], c='r')
# ax.scatter(x[30:40], y[30:40], z[30:40], c='g')
a = np.array(x)
b = np.array(z)
color = np.arctan(b, a)
ax.scatter(x, y, z, c=color)

ax.set_xlabel('mem')
ax.set_ylabel('cpu')
ax.set_zlabel('power')  # 坐标轴
ax.invert_xaxis()
# 改变绘制图像的视角, 即相机的位置, azim沿着z轴旋转, elev沿着y轴, 默认：azim=-37.5, elev=30
ax.view_init(azim=-37.5, elev=30)
plt.show()

plt.savefig('plot1.png')
plt.close()
