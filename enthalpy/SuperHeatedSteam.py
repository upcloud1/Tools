from Table0 import Table0  # 假设 Table0 已保存在 table0.py 文件中
from Table1 import Table1  # 假设 Table1 已保存在 table1.py 文件中
from Constants import Constants  # 假设 Constants 类中包含 R 常量

class SuperHeatedSteam:
    def __init__(self, pressure, temperature):
        self.pressure = pressure
        self.temperature = temperature
        self.pi = pressure
        self.tau = 540 / (temperature + 273.16)
        self.t0 = Table0()
        self.t1 = Table1()
        self.sum_table0 = 0.0
        self.sum_table1 = 0.0
        self.init_table0_steam()
        self.init_table1_steam()

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
        temp_k = self.temperature + 273.16
        enthalpy = (540 / temp_k) * (self.get_sum_table0() + self.get_sum_table1()) * Constants.R * temp_k
        return enthalpy