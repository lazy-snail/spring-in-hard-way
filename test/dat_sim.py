# -*- coding: utf-8 -*-

# Created by Neil on 2019/1/12.


def get_power(c, m):
    return (375.9 * c - 401 * c * c + 164.5 * c * c * c \
            - 30.7 * m + 41.9 * m * m - 19.9 * m * m * m) \
           / 1000000


c = 40.6
m = 0.5
print(get_power(c, m))
