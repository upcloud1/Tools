class Table2Row:
    def __init__(self, Ii, Ji, ni):
        self.Ii = Ii
        self.Ji = Ji
        self.ni = ni


class Table2:
    def __init__(self):
        self.tableinfo = [
            Table2Row(0, -2, 0.146329712),
            Table2Row(0, -1, -0.845481872),
            Table2Row(0, 0, -3.756360367),
            Table2Row(0, 1, 3.385516917),
            Table2Row(0, 2, -0.957919634),
            Table2Row(0, 3, 0.157720385),
            Table2Row(0, 4, -0.016616417),
            Table2Row(0, 5, 0.000812146),
            Table2Row(1, -9, 0.000283191),
            Table2Row(1, -7, -0.000607063),
            Table2Row(1, -1, -0.018990068),
            Table2Row(1, 0, -0.032529749),
            Table2Row(1, 1, -0.021841717),
            Table2Row(1, 3, -0.5283835796993 * 0.0001),
            Table2Row(2, -3, -0.000471843),
            Table2Row(2, 0, -0.000300018),
            Table2Row(2, 1, 0.47661393906987 * 0.0001),
            Table2Row(2, 3, -0.44141845330846 * 0.00001),
            Table2Row(2, 17, -0.72694996297594 * 0.000000000000001),
            Table2Row(3, -4, -0.31679644845054 * 0.0001),
            Table2Row(3, 0, -0.28270797985312 * 0.00001),
            Table2Row(3, 6, -0.85205128120103 * 0.000000001),
            Table2Row(4, -5, -0.22425281908 * 0.00001),
            Table2Row(4, -2, -0.65171222895601 * 0.000001),
            Table2Row(4, 10, -0.14341729937924 * 0.000000000001),
            Table2Row(5, -8, -0.40516996860117 * 0.000001),
            Table2Row(8, -11, -0.12734301741641 * 0.00000001),
            Table2Row(8, -6, -0.17424871230634 * 0.000000001),
            Table2Row(21, -29, -0.68762131295531 * 1e-18),
            Table2Row(23, -31, 0.14478307828521 * 1e-19),
            Table2Row(29, -38, 0.26335781662795 * 1e-22),
            Table2Row(30, -39, -0.11947622640071 * 1e-22),
            Table2Row(31, -40, 0.1822894571404 * 1e-23),
            Table2Row(32, -41, -0.93537087292458 * 1e-25)
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