package demo.frame.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CheckableTreeCellRenderer extends DefaultTreeCellRenderer {
    private JCheckBox checkBox = new JCheckBox();
    
    public CheckableTreeCellRenderer() {
        setLayout(new BorderLayout()); // 必须设置布局
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        
        checkBox = new JCheckBox();
        checkBox.setFocusable(false);
        checkBox.setFont(getFont());
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

    	 // ✅ 1. 调用父类方法设置基础样式
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        // ✅ 2. 清空当前内容，避免重复添加
        removeAll();

        // ✅ 3. 设置复选框内容和状态
		if (value instanceof CheckableTreeNode) {
			CheckableTreeNode node = (CheckableTreeNode) value;

			checkBox.setText(node.toString());
			checkBox.setSelected(node.isChecked());

		} else {
			checkBox.setText(value == null ? "" : value.toString());
			checkBox.setSelected(false);
		}
			 

        // ✅ 4. 设置复选框外观
        checkBox.setBackground(getBackground());
        checkBox.setFocusPainted(false);
        checkBox.setBorderPainted(false);
        checkBox.setOpaque(false);
        
        // ✅ 给复选框右边加点空隙
        checkBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // ✅ 5. 将复选框添加到面板中
        add(checkBox, BorderLayout.WEST); // 复选框放左边
        
        

        // ✅ 7. 返回整个面板，确保能被正确绘制
        return checkBox;
        

    }
}
