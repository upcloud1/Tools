package demo;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class WriteExcelDemo {
	
	/**
	 * 判断是否为中文字符
	 */
	private boolean isChinese(char c) {
	    return (c >= 0x4E00 && c <= 0x9FA5) ||   // 中文汉字
	           (c >= 0x3000 && c <= 0x303F) ||   // 中文标点
	           (c >= 0xFF00 && c <= 0xFFEF);     // 全角字符
	}
	
	/**
	 * 计算包含中文的字符串实际宽度
	 * 中文 = 2个宽度，英文数字 = 1个宽度
	 */
	private int getChineseLength(String str) {
	    int length = 0;
	    for (char c : str.toCharArray()) {
	        if (isChinese(c)) {
	            length += 2;  // 中文字符按2个宽度计算
	        } else {
	            length += 1;  // 其他字符按1个宽度计算
	        }
	    }
	    return length;
	}

    public static void main(String[] args) throws Exception {
        //write();
    	read();
    }
    
    private static void read()
    {
    	Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(new File("E:\\output.xlsx"));
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Sheet sheet = workbook.getSheetAt(0);
    	System.out.println("行数: " + sheet.getLastRowNum());
    	for (Row row : sheet) {
    	    for (Cell cell : row) {
    	        System.out.print(cell + " ");
    	    }
    	    System.out.println();
    	}
    }

	private static void write() throws IOException, FileNotFoundException {
		// 创建工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // 创建sheet
            Sheet sheet = workbook.createSheet("学生信息");
            
            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // 写入表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"姓名", "年龄", "成绩", "日期"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 写入数据
            Object[][] data = {
                {"张三", 20, 95.5, new Date()},
                {"李四", 22, 88.0, new Date()},
                {"王五", 19, 92.3, new Date()}
            };
            
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue((String) data[i][0]);  // 姓名
                row.createCell(1).setCellValue((Integer) data[i][1]); // 年龄
                row.createCell(2).setCellValue((Double) data[i][2]);  // 成绩
                row.createCell(3).setCellValue((Date) data[i][3]);    // 日期
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i)*2);
            }
            
            // 写入文件
            try (FileOutputStream fos = new FileOutputStream("E:\\output.xlsx")) {
                workbook.write(fos);
            }
            
            System.out.println("写入成功！");
        }
	}
}
