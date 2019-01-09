# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.


from sklearn import datasets, linear_model, discriminant_analysis
import numpy as np
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split


# 加载数据
def load_data():
    diabetes = datasets.load_diabetes()
    return train_test_split(diabetes.data, diabetes.target, test_size=0.25, random_state=0)


# 定义岭回归模型
def test_Ridge(x_train, x_test, y_train, y_test):
    regr = linear_model.Ridge()
    regr.fit(x_train, y_train)
    print('Coefficients:%s,intercept %.2f' % (regr.coef_, regr.intercept_))

    print("Residual sum of square:%.2f" % np.mean((regr.predict(x_test) - y_test) ** 2))
    print('Score:%.2f' % regr.score(x_test, y_test))
    plt.grid()
    plt.show()


x_train, x_test, y_train, y_test = load_data()

test_Ridge(x_train, x_test, y_train, y_test)
