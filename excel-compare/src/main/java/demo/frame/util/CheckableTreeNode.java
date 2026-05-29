package demo.frame.util;

import javax.swing.tree.DefaultMutableTreeNode;

public class CheckableTreeNode extends DefaultMutableTreeNode {
    private boolean checked = false;
    private boolean indeterminate = false; // 新增：灰化状态

    public CheckableTreeNode(Object userObject) {
        super(userObject);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
