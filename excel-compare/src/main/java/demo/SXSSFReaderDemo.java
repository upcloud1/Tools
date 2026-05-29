package demo;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStream;

public class SXSSFReaderDemo {

    public static void main(String[] args) throws Exception {
        String filePath = "test.xlsx";
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            OPCPackage pkg = OPCPackage.open(fis);
            XSSFReader reader = new XSSFReader(pkg);
            
            // 获取共享字符串表和样式表
            SharedStringsTable sst = (SharedStringsTable) reader.getSharedStringsTable();
            StylesTable styles = reader.getStylesTable();
            
            // 获取第一个sheet的流
            try (InputStream sheet = reader.getSheet("rId1")) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();
                
                // 自定义处理器
                SheetContentHandler1 handler = new SheetContentHandler1();
                XSSFSheetXMLHandler sheetXMLHandler = 
                    new XSSFSheetXMLHandler(styles, sst, handler, false);
                
                xmlReader.setContentHandler(sheetXMLHandler);
                xmlReader.parse(new InputSource(sheet));
            }
            
            pkg.close();
        }
    }
}

// 自定义内容处理器
class SheetContentHandler1 implements XSSFSheetXMLHandler.SheetContentsHandler {
    
    @Override
    public void startRow(int rowNum) {
        System.out.print("第 " + rowNum + " 行: ");
    }
    
    @Override
    public void endRow(int rowNum) {
        System.out.println();
    }
    
    @Override
    public void cell(String cellRef, String formattedValue, XSSFComment comment) {
        System.out.print(formattedValue + "\t");
    }
    
    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
        // 可选实现
    }
}