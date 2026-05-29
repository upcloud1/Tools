package demo.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StreamExcelListener extends AnalysisEventListener<Map<Integer, String>> {

    private final ExcelRowCallback rowCallback;
    private int currentRowNum = 0;
    private String currentSheetName = "";
    List<String> sheetNames = new ArrayList<>();

    public StreamExcelListener(ExcelRowCallback callback) {
        this.rowCallback = callback;
    }

    @Override
    public void invoke(Map<Integer, String> rowMap, AnalysisContext context) {
        List<String> rowList = new ArrayList<>();
        int maxCol = rowMap.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        for (int i = 0; i <= maxCol; i++) {
            rowList.add(rowMap.getOrDefault(i, ""));
        }
        String csn = context.readSheetHolder().getSheetName();
		if (currentSheetName.isEmpty() || !currentSheetName.equals(csn)) {
			currentSheetName = csn;
			currentRowNum = 0;
		}

        // 直接回调出去，不存入全局大集合
        rowCallback.onRowRead(rowList, currentRowNum++, currentSheetName, false);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 读取完成后置逻辑
    	System.out.println("read finish");
    	rowCallback.onRowRead(null, 0, "", true);
    }
    
    @Override
	public void invokeHead(Map headMap, AnalysisContext context) {
    	String sheetName = context.readSheetHolder().getSheetName();
        if (sheetName != null && !sheetName.trim().isEmpty()) {
            sheetNames.add(sheetName);
        }
	}
}

