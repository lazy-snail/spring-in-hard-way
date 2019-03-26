# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/8.


import csv
import pandas as pd


def load_data(path):
    file = pd.read_csv(path)
    print(len(file.columns))
    p = file.columns[0]
    print(p)
    print(file[p])


path = "D:\prj\py\py37\Sim\dat\sysUtilization.csv"
load_data(path)
