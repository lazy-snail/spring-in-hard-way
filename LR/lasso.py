# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.


import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.linear_model import Lasso, LassoCV

# 通过read_csv来读取我们的目的数据集
adv_data = pd.read_csv("D:\prj\py\py37\Sim\dat\sysUtilization.csv")
# 清洗不需要的数据
new_adv_data = adv_data.iloc[:, 0:5]
# 得到我们所需要的数据集且查看其前几列以及数据形状
print('head:', new_adv_data.head(), '\nShape:', new_adv_data.shape)
#
# # 数据描述
print(new_adv_data.describe())

# 箱型图
new_adv_data.boxplot()
# plt.savefig("boxplot.png")
plt.show()

sns.pairplot(new_adv_data, x_vars=['cpu(%)', 'mem(%)', 'disk_io(Mbps)', 'net_io(Mbps)'], y_vars='power', height=7,
             aspect=0.8, kind='reg')
plt.show()

X_train, X_test, Y_train, Y_test = train_test_split(new_adv_data.iloc[:, 1:5], new_adv_data.power)
print("原始数据特征:", new_adv_data.iloc[:, 1:5].shape,
      ",训练数据特征:", X_train.shape,
      ",测试数据特征:", X_test.shape)
#
print("原始数据标签:", new_adv_data.power.shape,
      ",训练数据标签:", Y_train.shape,
      ",测试数据标签:", Y_test.shape)

al = LassoCV().fit(X_train, Y_train).alpha_

model = Lasso()
model.fit(X_train, Y_train)
a = model.intercept_  # 截距
#
b = model.coef_  # 回归系数
#
print("套索线性回归：", model)
print("最佳拟合线:截距", a, ",回归系数：", b)

#
# R方检测
# 决定系数r平方
# 对于评估模型的精确度
# y误差平方和 = Σ(y实际值 - y预测值)^2
# y的总波动 = Σ(y实际值 - y平均值)^2
# 有多少百分比的y波动没有被回归拟合线所描述 = SSE/总波动
# 有多少百分比的y波动被回归线描述 = 1 - SSE/总波动 = 决定系数R平方
# 对于决定系数R平方来说1） 回归线拟合程度：有多少百分比的y波动刻印有回归线来描述(x的波动变化)
# 2）值大小：R平方越高，回归模型越精确(取值范围0~1)，1无误差，0无法完成拟合
score = model.score(X_test, Y_test)
#
print("R方检测:", score)