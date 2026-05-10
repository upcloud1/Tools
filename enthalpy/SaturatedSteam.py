from Table0 import Table0  # 假设 Table0 类已定义
from Table1 import Table1  # 假设 Table1 类已定义
from SaturationRegion import SaturationRegion  # 假设 SaturationRegion 类已定义
from Constants import Constants  # 假设 Constants 包含常量 R

class SaturatedSteam:
    def __init__(self, pressure):
        self.pressure = pressure
        sr = SaturationRegion(pressure)
        self.T = sr.get_T()
        self.pi = pressure
        self.tau = 540 / self.T
        self.temperature = self.T - 273.15

        self.t0 = Table0()
        self.t1 = Table1()
        self.sum_table0 = 0.0
        self.sum_table1 = 0.0

        self.init_table0_steam()
        self.init_table1_steam()

    def set_pi(self, pi):
        self.pi = pi

    def set_tau(self, tau):
        self.tau = tau

    def init_table0_steam(self):
        table0 = []
        length = self.t0.get_len()
        for i in range(length):
            ni = self.t0.get_ni(i)
            Ji = self.t0.get_Ji(i)
            term = ni * Ji * (self.tau ** (Ji - 1))
            table0.append(term)
        self.sum_table0 = sum(table0)

    def get_sum_table0(self):
        return self.sum_table0

    def init_table1_steam(self):
        table1 = []
        length = self.t1.get_len()
        for i in range(length):
            ni = self.t1.get_ni(i)
            Ii = self.t1.get_Ii(i)
            Ji = self.t1.get_Ji(i)
            term = ni * (self.pi ** Ii) * Ji * ((self.tau - 0.5) ** (Ji - 1))
            table1.append(term)
        self.sum_table1 = sum(table1)

    def get_sum_table1(self):
        return self.sum_table1

    def get_enthalpy(self):
        enthalpy = self.tau * (self.get_sum_table0() + self.get_sum_table1()) * Constants.R * self.T
        return enthalpy