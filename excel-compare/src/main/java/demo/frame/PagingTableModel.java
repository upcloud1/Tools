package demo.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class PagingTableModel extends AbstractTableModel {

    private final String[] columnNames = {"ID", "Name", "Age", "City"};
    private List<Vector<Object>> data = new ArrayList<>();
    private int pageSize = 100;
    private int totalRecords = 500; // 假设有 500 条数据
    private int loadedPages = 0;
    private boolean isLastPage = false;

    public PagingTableModel(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (row >= data.size()) return null;
        return data.get(row).get(column);
    }

    // 模拟异步加载数据（当滚动到末尾时触发）
    public void loadNextPageIfNeeded(int visibleRow) {
        if (isLastPage || visibleRow < data.size() - 1) return;

        int nextPage = loadedPages + 1;
        int start = nextPage * pageSize;
        int end = Math.min(start + pageSize, totalRecords);

        if (start >= totalRecords) {
            isLastPage = true;
            return;
        }

        // 模拟加载数据
        for (int i = start; i < end; i++) {
            Vector<Object> row = new Vector<>();
            row.add(i + 1);
            row.add("用户" + (i + 1));
            row.add(20 + (i % 60));
            row.add("城市" + ((i % 10) + 1));
            data.add(row);
        }

        loadedPages++;
        fireTableDataChanged(); // 通知表格刷新
    }

    // 清理旧数据（可选：如滚动后删除前面几百行）
    public void cleanupOldData() {
        if (data.size() > 300) { // 保留最近 300 行
            data = new ArrayList<>(data.subList(data.size() - 300, data.size()));
            fireTableDataChanged();
        }
    }

    public List<Vector<Object>> getAllData() {
        return data;
    }

    public void reset() {
        data.clear();
        loadedPages = 0;
        isLastPage = false;
        fireTableDataChanged();
    }
}