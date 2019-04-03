# -*- coding: utf-8 -*-

# Created by Neil on 2019/2/21.

import os, sys
import numpy as np

# In[*]
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

path = str(os.path.abspath('..'))
data = pd.read_csv(path + "\\dat\\err1.csv")
x_data = data.err

# x = np.linspace(0, 1, 500)
y = x_data
print(y.var())
# plt.figure(figsize=(8, 6))
# plt.plot(y, c='b')
# plt.axhline(0, c='black')
# plt.show()
