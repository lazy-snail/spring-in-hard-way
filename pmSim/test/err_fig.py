# -*- coding: utf-8 -*-

# Created by Neil on 2019/2/21.

import os, sys
import random
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

path = str(os.path.abspath('..'))
data = pd.read_csv(path + "//dat//err2.csv")
x_data = data.p

# x = np.linspace(0, 1, 500)

len = len(x_data)

for i in range(len):
    # cpu = random.uniform(2.5, 10.1)
    # mem = random.uniform(16.7, 28.2)
    err = random.uniform(-18, 15)
    print("%.2f" % ((err / 100 + 1) * x_data[i]))

# print(x_data)

# print(y.var())
# plt.figure(figsize=(8, 6))
# plt.plot(y, c='b')
# plt.axhline(0, c='black')
# plt.show()
