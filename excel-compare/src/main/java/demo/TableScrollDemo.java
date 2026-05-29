package demo;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class TableScrollDemo {
    public static void main(String[] args) {
        JFrame frame = new JFrame("滚动检测");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JTable table = new JTable(100, 3);
        JScrollPane scrollPane = new JScrollPane(table);

        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

        verticalBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int value = e.getValue();                  // 当前位置
                int max = e.getAdjustable().getMaximum(); // 最大值（总高度）
                int extent = e.getAdjustable().getVisibleAmount(); // 可见区域大小

                if (value == 0) {
                    System.out.println("✅ 滚动到顶部！");
                }
                if (value >= max - extent) {
                    System.out.println("✅ 滚动到底部！");
                }
            }
        });

        frame.add(scrollPane);
        frame.setVisible(true);
    }
}