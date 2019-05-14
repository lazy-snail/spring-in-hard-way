# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/7.


from sklearn.datasets import load_boston
from sklearn.model_selection import train_test_split
from sklearn.linear_model import Lasso, LinearRegression
from sklearn.metrics import mean_squared_error
from sklearn.preprocessing import PolynomialFeatures
import matplotlib.pyplot as plt
import numpy as np


# 加载数据
def get_data():
    data = load_boston()
    x = data['data']
    y = data['target']
    return x, y


# 建立模型
def build_models(x, y):
    alpha_range = np.linspace(0, 0.5, 200)
    model = Lasso(normalize=True)  # 只需要标准化，不需要中心化
    coeffiecients = []
    # 对每个alpha值适配模型
    for alpha in alpha_range:
        model.set_params(alpha=alpha)
        model.fit(x, y)
        coeffiecients.append(model.coef_)  # 追踪系数用来绘图
        # print coeffiecients #维度为200*13
    # 绘制系数权重变化和对应的alpha值
    # 绘制模型的RMSE和对应的alpha值
    coeff_path(alpha_range, coeffiecients)
    # 查看系数值
    # view_model(model)


# 查看回归系数值
def view_model(model):
    print("\n model coeffiecients")
    for i, coef in enumerate(model.coef_):
        print("\t coefficient %d %0.3f" % (i + 1, coef))
    print("\n\t intercept %0.3f" % (model.intercept_))


# 评估模型
def model_worth(true_y, predicted_y):
    print("\t mean squared error = %0.2f\n" % \
          (mean_squared_error(true_y, predicted_y)))


# 绘制不同alpha值情况下的系数权重
def coeff_path(alpha_range, coeffiecients):
    plt.close('all')
    plt.cla()
    plt.figure(1)
    plt.xlabel("Alpha Values")
    plt.ylabel("coeffiecient weights for different alpha values")
    plt.plot(alpha_range, coeffiecients)
    plt.axis('tight')  # 修改x、y坐标的范围让所有的数据显示出来
    plt.show()


# 主函数调用,查看保留下来的回归系数有哪些
def get_coef(x, y, alpha):
    model = Lasso(normalize=True, alpha=alpha)
    model.fit(x, y)
    coefs = model.coef_
    indices = [i for i, coef in enumerate(coefs) if abs(coef) > 0.0]
    return indices


# 电泳所有函数
if __name__ == "__main__":
    x, y = get_data()
    # 用不用的alpha值多次建模，并绘出图形
    build_models(x, y)
    print("\npredicting using all the variables\n")
    full_model = LinearRegression(normalize=True)
    full_model.fit(x, y)
    predicted_y = full_model.predict(x)
    model_worth(y, predicted_y)

    print("\n models at different alpha values\n")
    alpa_values = [0.22, 0.08, 0.01]
    for alpha in alpa_values:
        indices = get_coef(x, y, alpha)
        print("\t alpha = %0.2f number of variables selected = %d\
        " % (alpha, len(indices)))  # 看保留下来的回归系数有多少
        print("\t attributes include ", indices)  # 看保留下来的回归系数有哪些
        x_new = x[:, indices]
        model = LinearRegression(normalize=True)
        model.fit(x_new, y)
        predicted_y = model.predict(x_new)
        model_worth(y, predicted_y)
