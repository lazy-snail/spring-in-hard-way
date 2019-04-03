# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.

import os
import matplotlib.pyplot as plt
import numpy as np
from sklearn import linear_model, svm

from util import datautils


def lasso_regression_test(datapath):
    # 加载数据集，默认训练：测试数据比例为 4：1
    X_train, X_test, Y_train, Y_test = datautils.load_train_test_set(datapath)
    # 选择套索回归模型
    regr = linear_model.ElasticNet()
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
    y的总波动 = Σ(y实际值 - y平均值)^2Z
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

    alphas = np.logspace(-2, 2)
    rhos = np.linspace(0.01, 1)
    scores = []
    for i in alphas:
        for j in rhos:
            regr = linear_model.ElasticNet(alpha=i, l1_ratio=j)
            regr.fit(X_train, Y_train)
            scores.append(regr.score(X_test, Y_test))
    # 绘图
    alphas, rhos = np.meshgrid(alphas, rhos)
    scores = np.array(scores).reshape(alphas.shape)
    from mpl_toolkits.mplot3d import Axes3D
    from matplotlib import cm
    fig = plt.figure()
    ax = Axes3D(fig)
    surf = ax.plot_surface(alphas, rhos, scores, rstride=1, cstride=1, cmap=cm.jet, linewidth=0, antialiased=False)
    fig.colorbar(surf, shrink=0.5, aspect=5)
    ax.set_title('ElasticNet')
    ax.set_xlabel(r"$\alpha$")
    ax.set_ylabel(r"$\rho$")
    ax.set_zlabel("score")
    plt.show()


path = str(os.path.abspath('..'))
dp = path + "\\dat\\test.csv"
lasso_regression_test(dp)
