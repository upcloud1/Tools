import tkinter as tk
from tkinter import messagebox
from tkinter import ttk

from Water import Water
from SuperHeatedSteam import SuperHeatedSteam
from SaturatedSteam import SaturatedSteam
from SaturatedWater import SaturatedWater


# create main window
root = tk.Tk()
root.title("焓值计算")
root.geometry("400x300") # size of window

# 一行布局：标签 + 下拉框
frame = tk.Frame(root)
frame.pack(pady=10)

# 标签
tk.Label(frame, text="焓值类型").grid(row=0, column=0, padx=5)

options = ["过热蒸汽", "水", "饱和蒸汽", "饱和水"] 

# 绑定选中的值
selected = tk.StringVar()
combo = ttk.Combobox(frame, textvariable=selected, values=options, width=15)
combo.grid(row=0, column=1)
# 默认选中第一个
combo.current(0)

# 2 压力输入
frame2 = tk.Frame(root)
frame2.pack(pady=6)

tk.Label(frame2, text="压力").grid(row=0, column=0, padx=5)
entry_p = tk.Entry(frame2, width=18)
entry_p.grid(row=0, column=1)

# 3 温度输入
frame3 = tk.Frame(root)
frame3.pack(pady=6)

tk.Label(frame3, text="温度").grid(row=0, column=0, padx=5)
entry_t = tk.Entry(frame3, width=18)
entry_t.grid(row=0, column=1)

# 切换下拉自动灰化温度框
def change_status(event):
    idx = combo.current()
    # 第0、第1项：过热焓、饱和液焓 → 温度禁用灰色
    if idx == 2 or idx == 3:
        entry_t.config(state="disabled")
    else:
        entry_t.config(state="normal")

combo.bind("<<ComboboxSelected>>", change_status)

# 初始化默认禁用
change_status(None)

# 计算按钮
def calc():
    p = float(entry_p.get())
    t = float(entry_t.get())
    idx1 = combo.current()
    if idx1 == 0:
        steam = SuperHeatedSteam(pressure=p, temperature=t)
        res = steam.get_enthalpy()
        entry_result.delete(0, tk.END)
        entry_result.insert(0, str(res))
    elif idx1 == 1:
        water = Water(pressure=p, temperature=t)
        res = water.get_enthalpy()
        entry_result.delete(0, tk.END)
        entry_result.insert(0, str(res))
    elif idx1 == 2:
        ssteam = SaturatedSteam(pressure=p)
        res = ssteam.get_enthalpy()
        entry_result.delete(0, tk.END)
        entry_result.insert(0, str(res))
    else:
        water1 = SaturatedWater(pressure=p)
        res = water1.get_enthalpy()
        entry_result.delete(0, tk.END)
        entry_result.insert(0, str(res))
    

btn = tk.Button(root, text="计算焓值", command=calc, width=12)
btn.pack(pady=8)

# 焓值结果输出
frame4 = tk.Frame(root)
frame4.pack(pady=6)

tk.Label(frame4, text="焓值").grid(row=0, column=0, padx=5)
entry_result = tk.Entry(frame4, width=18)
entry_result.grid(row=0, column=1)

# loop execution
root.mainloop()
    