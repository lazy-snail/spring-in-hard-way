# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/7.


# -*- coding: utf-8 -*-
"""
Created on Thu Apr 05 19:52:39 2018
@author: Alvin AI
"""

from sklearn.datasets import load_boston
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error
import matplotlib.pyplot as plt
from sklearn.preprocessing import PolynomialFeatures
from itertools import combinations
from sklearn.feature_selection import RFE


# 载入数据
def get_data():
    data = load_boston()
    x = data['data']
    y = data['target']
    return x, y


# 建立模型
# 让回归特征消除（RFE-recursive feature elimination）只保留no_features个最重要的特征
def build_model(x, y, no_features):
    model = LinearRegression(normalize=True, fit_intercept=True)
    rfe_model = RFE(estimator=model, n_features_to_select=no_features)
    rfe_model.fit(x, y)
    return rfe_model


# 查看模型
def view_model(model):
    print("\nmodel coefficients")
    print("===================\n")
    # coef_提供了一个系数矩阵，intercept_提供了回归常数
    for i, coef in enumerate(model.coef_):
        print("\t coefficient %d %model" % (i + 1, coef))
    print("\n\tintercept %0.3f" % (model.intercept_))


# 计算均平方差用以评估模型误差
def model_worth(true_y, predicted_y):
    print("\t mean squared error = %0.2f" % (mean_squared_error(true_y, predicted_y)))
    return mean_squared_error(true_y, predicted_y)


# 绘制残差图
def plot_residual(y, predicted_y):
    plt.cla()
    plt.xlabel('predicted y')
    plt.ylabel('residual')
    plt.title('residual plot')
    plt.figure1(1)
    diff = y - predicted_y
    plt.plot(predicted_y, diff, 'go')
    plt.show()


if __name__ == "__main__":
    x, y = get_data()
    # 划分数据集
    x_train, x_test_all, y_train, y_test_all = train_test_split(x, y, \
                                                                test_size=0.3, random_state=9)
    x_dev, x_test, y_dev, y_test = train_test_split(x_test_all, y_test_all, \
                                                    test_size=0.3, random_state=9)
    # 准备一些多项式特征
    poly_features = PolynomialFeatures(interaction_only=True)  # 只有x1和x2交互一起的，x1^2这种不行
    x_train_poly = poly_features.fit_transform(x_train)
    x_dev_poly = poly_features.fit_transform(x_dev)
    choosen_model = build_model(x_train_poly, y_train, 20)
    predicted_y = choosen_model.predict(x_train_poly)
    mse = model_worth(y_train, predicted_y)

    x_test_poly = poly_features.fit_transform(x_test)
    predicted_y = choosen_model.predict(x_test_poly)

    model_worth(y_test, predicted_y)
