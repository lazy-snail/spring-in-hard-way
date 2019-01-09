# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/9.


import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import train_test_split

# 通过read_csv来读取我们的目的数据集
data = pd.read_csv("D:\prj\py\py37\Sim\dat\\test.csv")
print(data.iloc[:, 0])
