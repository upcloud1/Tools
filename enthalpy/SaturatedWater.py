from Table2 import Table2  # 假设 Table2 类已定义
from SaturationRegion import SaturationRegion  # 假设 SaturationRegion 类已定义
from Constants import Constants  # 假设 Constants 包含常量 R

class SaturatedWater:
    def __init__(self, pressure):
        self.pressure = pressure
        sr = SaturationRegion(pressure)
        self.T = sr.get_T()
        self.pi = pressure / 16.53
        self.tau = 1386 / self.T
        self.temperature = self.T - 273.15

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
        result = self.tau * self.get_sum_table2() * Constants.R * self.T
        return result