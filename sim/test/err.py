# -*- coding: utf-8 -*-

# Created by Neil on 2019/2/21.

import numpy as np
import matplotlib.pyplot as plt

# In[*]
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

data = pd.read_csv("D:\prj\py\py37\Sim\dat\\err.csv")
x_data = data.err

# x = np.linspace(0, 1, 500)
y = x_data
plt.figure(figsize=(8, 6))
plt.plot(y, c='b')
plt.axhline(0, c='black')
plt.show()
