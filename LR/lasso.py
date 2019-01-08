# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.

import matplotlib.pyplot as plt
import numpy as np
from sklearn import linear_model

from util import datautils


def lasso_regression_test(datapath):
    X_train, X_test, Y_train, Y_test = datautils.load_train_test_set(datapath)
    regr = linear_model.Lasso()
    regr.fit(X_train, Y_train)
    print('Coefficients: %s\nintercept: %.2f' % (regr.coef_, regr.intercept_))
    print("Residual sum of square: %.2f" % np.mean((regr.predict(X_test) - Y_test) ** 2))
    print('Score: %.2f' % regr.score(X_test, Y_test))

    Y_pred = regr.predict(X_test)
    plt.plot(range(len(Y_pred)), Y_pred, 'r', label="predict")
    plt.plot(range(len(Y_pred)), Y_test, 'b', label="real")
    plt.show()


dp = "D:\prj\py\py37\Sim\dat\sysUtilization.csv"
lasso_regression_test(dp)
