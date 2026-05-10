from Table2 import Table2  # 假设 Table2 已保存在 table2.py 文件中
from Constants import Constants    # 假设 R 常量在 constants.py 文件中定义

class Water:
    def __init__(self, pressure, temperature):
        self.pressure = pressure
        self.temperature = temperature
        self.pi = pressure / 16.53
        self.tau = 1386 / (273.16 + temperature)
        self.t = Table2()
        self.sum_table2 = 0.0
        self.init_table2_steam()

    def set_pi(self, pi):
        self.pi = pi

    def set_tau(self, tau):
        self.tau = tau

    def init_table2_steam(self):
        table2 = []
        length = self.t.get_len()
        for i in range(length):
            ni = self.t.get_ni(i)
            Ii = self.t.get_Ii(i)
            Ji = self.t.get_Ji(i)
            term = ni * (7.1 - self.pi) ** Ii * Ji * (self.tau - 1.222) ** (Ji - 1)
            table2.append(term)

        self.sum_table2 = sum(table2)

    def get_sum_table2(self):
        return self.sum_table2

    def get_enthalpy(self):
        temp_k = 273.16 + self.temperature
        enthalpy = temp_k * Constants.R * self.get_sum_table2() * (1386 / temp_k)
        return enthalpy