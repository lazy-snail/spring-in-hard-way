# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.

import csv
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression

from sklearn.model_selection import train_test_split

"""
数据约定：
数据格式为 .csv，可以基于类似的数据结构添加相应的 utils 用以处理不同格式的数据。
数据的第一列为因变量，该项目即为“能耗”，详见数据统计说明
其余列为可能的影响因素，可以空缺行数据，不假定列数量
"""


def get_headinfo(datapath):
    with open(datapath) as file:
        reader = csv.reader(file)
        head_row = next(reader)
        return head_row


def get_datainfo(datapath):
    file = pd.read_csv(datapath)
    # 检查各列是否有数据缺失，统计各列缺失数量
    # todo: 数据补全方法
    print("数据缺失项检查：\n", file[file.isnull() == True].count())
    data_dimension = len(file.columns) - 1
    # 数据维度：除去因变量数据列，即因变量的影响因子有多少个
    print("数据维度：", data_dimension)
    # 得到我们所需要的数据集且查看其前几列以及数据形状
    print('head:\n', file.head(), '\nShape:', file.shape)
    # 数据描述
    print("数据基本描述：\n", file.describe())
    """
    相关性描述：数据列两两之间的相关性
    计算方程：r(相关系数) = x和y的协方差/(x的标准差*y的标准差) == cov（x,y）/σx*σy
    其中，
    0~0.3：弱相关
    0.3~0.6：中等程度相关
    0.6~1：强相关
    """
    print("相关性描述:\n", file.corr())


# 加载训练测试数据集
def load_train_test_set(datapath):
    file = pd.read_csv(datapath)
    p = file.columns[0]
    n = len(file.columns) - 1
    return train_test_split(file.iloc[:, 1:n], file[p], test_size=0.25)


def draw_boxplot(datapath):
    file = pd.read_csv(datapath).iloc[1:, :]
    file.boxplot()
    plt.show()


def draw_pairplot(datapath):
    file = pd.read_csv(datapath)
    cols = list(file.columns)
    # print(cols)
    # 参数 kind='reg'，用于添加一条最佳拟合直线和 95% 的置信带
    sns.pairplot(file, x_vars=cols[1:5], y_vars=cols[0], height=7, aspect=0.8, kind='reg')
    plt.show()

# datapath = "D:\prj\py\py37\Sim\dat\sysUtilization.csv"
# get_datainfo(datapath)
# draw_pairplot(datapath)

# X_train, X_test, Y_train, Y_test = load_train_test_set(datapath)
# model = LinearRegression()
# #
# model.fit(X_train, Y_train)
# #
# a = model.intercept_  # 截距
# #
# b = model.coef_  # 回归系数
# #
# print("简单线性回归：", model)
# print("最佳拟合线:截距", a, ",回归系数：", b)
