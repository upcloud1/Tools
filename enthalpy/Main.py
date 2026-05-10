from Table0 import Table0
from Table1 import Table1
from Table2 import Table2
from Constants import Constants
from Water import Water
from SuperHeatedSteam import SuperHeatedSteam
from SaturatedSteam import SaturatedSteam
from SaturatedWater import SaturatedWater

table = Table0()
print(table.get_Ji(0))   # 输出: 0
print(table.get_ni(1))   # 输出: 10.08665597
print(table.get_len())   # 输出: 9

table1 = Table1()
print(table1.get_Ii(0))   # 输出: 1
print(table1.get_Ji(0))   # 输出: 0
print(table1.get_ni(0))   # 输出: -0.001773174
print(table1.get_len())   # 输出: 43

table2 = Table2()
print(table2.get_Ii(0))   # 输出: 0
print(table2.get_Ji(0))   # 输出: -2
print(table2.get_ni(0))   # 输出: 0.146329712
print(table2.get_len())   # 输出: 34

print(Constants.R)

# 假设你已经定义好了 Table2 和 Constants
water = Water(pressure=3, temperature=120)
print("Sum Table2:", water.get_sum_table2())
print("Enthalpy:", water.get_enthalpy())

steam = SuperHeatedSteam(pressure=5, temperature=280)
print("Sum Table0:", steam.get_sum_table0())
print("Sum Table1:", steam.get_sum_table1())
print("Enthalpy:", steam.get_enthalpy())

# 假设所有依赖类和模块都已正确导入
ssteam = SaturatedSteam(pressure=5)
print("Sum Table0:", ssteam.get_sum_table0())
print("Sum Table1:", ssteam.get_sum_table1())
print("Enthalpy:", ssteam.get_enthalpy())

water1 = SaturatedWater(pressure=5)
print("Enthalpy (from pressure):", water1.get_enthalpy())



