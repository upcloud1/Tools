package demo;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;

public class SXSSFWriteDemo {

    public static void main(String[] args) throws Exception {
        
        // 创建SXSSFWorkbook，保留100行在内存中，其余写入磁盘
        try (Workbook workbook = new SXSSFWorkbook(100)) {
            
            Sheet sheet = workbook.createSheet("大数据测试");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("编号");
            headerRow.createCell(1).setCellValue("内容");
            
            // 写入100万行数据
            int totalRows = 1_000_000;
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue("数据-" + i);
                
                // 每10000行刷新到磁盘，释放内存
                if (i % 10000 == 0) {
                    ((SXSSFSheet) sheet).flushRows(100);
                }
            }
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream("E:\\large_file.xlsx")) {
                workbook.write(fos);
            }
            
            // 清理临时文件
            ((SXSSFWorkbook) workbook).dispose();
            
            System.out.println("写入完成！共 " + totalRows + " 行");
        }
    }
}