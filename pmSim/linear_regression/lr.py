# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.

import matplotlib.pyplot as plt
import numpy as np
from sklearn import linear_model

from util import datautils


def lasso_regression_test(datapath):
    # 加载数据集，默认训练：测试数据比例为 4：1
    X_train, X_test, Y_train, Y_test = datautils.load_train_test_set(datapath)
    # 选择套索回归模型
    regr = linear_model.LinearRegression()
    # 拟合训练数据
    regr.fit(X_train, Y_train)
    # 线性拟合结果：系数、截距
    print('Coefficients: %s\nintercept: %.2f' % (regr.coef_, regr.intercept_))
    # 残差平方和
    print("Residual sum of square: %.2f" % np.mean((regr.predict(X_test) - Y_test) ** 2))
    """
    拟合度：R方检测
    决定系数r平方
    对于评估模型的精确度
    y误差平方和 = Σ(y实际值 - y预测值)^2
    y的总波动 = Σ(y实际值 - y平均值)^2
    有多少百分比的y波动没有被回归拟合线所描述 = SSE/总波动
    有多少百分比的y波动被回归线描述 = 1 - SSE/总波动 = 决定系数R平方
    对于决定系数R平方来说1） 回归线拟合程度：有多少百分比的y波动刻印有回归线来描述(x的波动变化)
    2）值大小：R平方越高，回归模型越精确(取值范围0~1)，1无误差，0无法完成拟合
    """
    print('Score: %.2f' % regr.score(X_test, Y_test))

    # 测试结果与实际结果图形对比
    Y_pred = regr.predict(X_test)
    plt.plot(range(len(Y_pred)), Y_pred, 'r', label="predict")
    plt.plot(range(len(Y_pred)), Y_test, 'b', label="real")
    plt.show()


dp = "D:\prj\py\py37\Sim\dat\\sysUtilization1.csv"
datautils.get_datainfo(dp)
lasso_regression_test(dp)