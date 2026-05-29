package demo.easyexcel;

import com.alibaba.excel.EasyExcel;

public class ExcelStreamReadUtil {

    /**
     * 超大Excel通用流式读取
     * 无实体、不限列结构、逐行回调、不堆积内存
     * @param filePath 文件路径
     * @param skipHeadRow 跳过表头行数
     * @param rowCallback 行数据回调
     */
    public static void streamReadExcel(String filePath, int skipHeadRow, ExcelRowCallback rowCallback) {
        StreamExcelListener listener = new StreamExcelListener(rowCallback);
        // 仅读第一个sheet
//        EasyExcel.read(filePath, listener)
//                .headRowNumber(skipHeadRow)
//                .sheet()
//                .doRead();  
    	
        // 读所有sheet
        EasyExcel.read(filePath, listener)
                .headRowNumber(skipHeadRow)
                .doReadAllSync();
    }
}

