class Table1Row:
    def __init__(self, Ii, Ji, ni):
        self.Ii = Ii
        self.Ji = Ji
        self.ni = ni


class Table1:
    def __init__(self):
        self.tableinfo = [
            Table1Row(1, 0, -0.001773174),
            Table1Row(1, 1, -0.017834862),
            Table1Row(1, 2, -0.045996014),
            Table1Row(1, 3, -0.057581259),
            Table1Row(1, 6, -0.050325279),
            Table1Row(2, 1, -0.33032641670203 * 0.0001),
            Table1Row(2, 2, -0.00018949),
            Table1Row(2, 4, -0.003939278),
            Table1Row(2, 7, -0.043797296),
            Table1Row(2, 36, -0.26674547914087 * 0.0001),
            Table1Row(3, 0, 0.20481737692309 * 0.0000001),
            Table1Row(3, 1, 0.43870667284435 * 0.000001),
            Table1Row(3, 3, -0.3227767723857 * 0.0001),
            Table1Row(3, 6, -0.001503392),
            Table1Row(3, 35, -0.040668254),
            Table1Row(4, 1, -0.78847309559367 * 0.000000001),
            Table1Row(4, 2, 0.12790717852285 * 0.0000001),
            Table1Row(4, 3, 0.48225372818507 * 0.000001),
            Table1Row(5, 7, 0.22922076337661 * 0.00001),
            Table1Row(6, 3, -0.16714766451061 * 0.0000000001),
            Table1Row(6, 16, -0.002117147),
            Table1Row(6, 35, -23.89574193),
            Table1Row(7, 0, -0.5905956432427 * 0.00000000000000001),
            Table1Row(7, 11, -0.12621808899101 * 0.00001),
            Table1Row(7, 25, -0.038946842),
            Table1Row(8, 8, 0.11256211360459 * 0.0000000001),
            Table1Row(8, 36, -8.23113409),
            Table1Row(9, 13, 0.19809712802088 * 0.0000001),
            Table1Row(10, 4, 0.10406965210174 * 0.000000000000000001),
            Table1Row(10, 10, -0.10234747095929 * 0.000000000001),
            Table1Row(10, 14, -0.10018179379511 * 0.00000001),
            Table1Row(16, 29, -0.80882908646985 * 0.0000000001),
            Table1Row(16, 50, 0.106930319),
            Table1Row(18, 57, -0.336622506),
            Table1Row(20, 20, 0.89185845355421 * 1e-24),
            Table1Row(20, 35, 0.30629316876232 * 0.000000000001),
            Table1Row(20, 48, -0.42002467698208 * 0.00001),
            Table1Row(21, 21, -0.59056029685639 * 1e-25),
            Table1Row(22, 53, 0.37826947613457 * 0.00001),
            Table1Row(23, 39, -0.12768608934681 * 0.00000000000001),
            Table1Row(24, 26, 0.73087610595061 * 1e-28),
            Table1Row(24, 40, 0.55414715350778 * 0.0000000000000001),
            Table1Row(24, 58, -0.9436970724121 * 0.000001),
        ]

    def get_Ii(self, i):
        if i < 0 or i >= len(self.tableinfo):
            return 0
        return self.tableinfo[i].Ii

    def get_Ji(self, i):
        if i < 0 or i >= len(self.tableinfo):
            return 0
        return self.tableinfo[i].Ji

    def get_ni(self, i):
        if i < 0 or i >= len(self.tableinfo):
            return 0.0
        return self.tableinfo[i].ni

    def get_len(self):
        return len(self.tableinfo)
