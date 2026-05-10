import math

class SaturationRegion:
    # 静态常量数组 nis（对应 Java 中的 public static final double[] nis）
    nis = [
        1167.052145,   # Y30
        -724213.167,   # Y31
        -17.07384694,  # Y32
        12020.8247,    # Y33
        -3232555.032,  # Y34
        14.91510861,   # Y35
        -4823.265736,  # Y36
        405113.4054,   # Y37
        -0.238555576,  # Y38
        650.1753484    # Y39
    ]

    def __init__(self, pressure):
        self.P = pressure  # Y41 = F10
        self.Beta = math.pow(self.P, 0.25)  # Y42 = Y41^0.25
        self.E = (self.Beta ** 2) + self.nis[2] * math.pow(self.P, 0.25) + self.nis[5]  # Y43
        self.F = self.nis[0] * (self.Beta ** 2) + self.nis[3] * self.Beta + self.nis[6]  # Y44
        self.G = self.nis[1] * (self.Beta ** 2) + self.nis[4] * self.Beta + self.nis[7]  # Y45
        self.D = (2 * self.G) / (-self.F - math.sqrt(self.F ** 2 - 4 * self.E * self.G))  # Y46
        self.T = (self.nis[9] + self.D - math.sqrt((self.nis[9] + self.D) ** 2 - 4 * (self.nis[8] + self.nis[9] * self.D))) / 2  # Y47
        self.t = self.T - 273.15  # Y48

    def set_P(self, pressure):
        self.P = pressure

    def get_P(self):
        return self.P

    def get_Beta(self):
        return self.Beta

    def get_E(self):
        return self.E

    def get_F(self):
        return self.F

    def get_G(self):
        return self.G

    def get_D(self):
        return self.D

    def get_T(self):
        return self.T

    def get_t(self):
        return self.t