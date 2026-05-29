package demo.easyexcel;

import java.util.List;

/**
 * 通用行数据回调
 */
@FunctionalInterface
public interface ExcelRowCallback {
    /**
     * 每读取一行执行
     * @param rowData 当前行所有单元格数据
     * @param rowIndex 行号(从0开始)
     */
    void onRowRead(List<String> rowData, int rowIndex, String sheetName, boolean isOver);
}

