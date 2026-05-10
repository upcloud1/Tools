class Table0Row:
    def __init__(self, Ji, ni):
        self.Ji = Ji
        self.ni = ni


class Table0:
    def __init__(self):
        self.tableinfo = [
            Table0Row(0, -9.69276865),
            Table0Row(1, 10.08665597),
            Table0Row(-5, -0.005608791),
            Table0Row(-4, 0.071452738),
            Table0Row(-3, -0.407104982),
            Table0Row(-2, 1.424081917),
            Table0Row(-1, -4.383951132),
            Table0Row(2, -0.284086325),
            Table0Row(3, 0.021268464)
        ]

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