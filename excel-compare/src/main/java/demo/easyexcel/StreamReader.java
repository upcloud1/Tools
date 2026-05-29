package demo.easyexcel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import demo.ExcelInfos;
import demo.frame.util.ExcelStatusBar;
import demo.sqlite.SqliteOperation;

public class StreamReader {
	
	// 静态实例在类加载时就创建
    private static final StreamReader INSTANCE = new StreamReader();

    // 私有构造函数，防止外部实例化
    private StreamReader() {}

    public static StreamReader getInstance() {
        return INSTANCE;
    }
	
	private ExcelInfos excels = ExcelInfos.getInstance();
	private List<String> sqls = new ArrayList<>();
	SqliteOperation so = new SqliteOperation();
	
	
	private String CREATE_TABLE = "CREATE TABLE %s (%s)"; 
	private String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s);"; 
	
	private String currentTable = "";
	private String currentColumns = "";
	
	private int progress = 0;
	
	public int get_progress()
	{
		return progress;
	}
	

	
	private String get_data_with_apostrophe(List<String> rowData) {
		String result = "";
		for (String val : rowData) {
			result += "'" + val + "',";
		}

		if (!result.isEmpty()) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public synchronized void read(String excelPath)
	{
		//System.out.println("Excel: " + excelPath);
		ExcelStatusBar.show_info("加载" + excelPath + ": ");
		read(excelPath, false);
	}
	
	public void read(String excelPath, boolean isReference)
	{
		so.create_connection();
		progress = 0;
		//System.out.println(progress);
		
		// 不跳过表头
        ExcelStreamReadUtil.streamReadExcel(excelPath, 0, (rowData, rowIdx, sheetName, isOver) -> {
            // 读到一行立刻处理：入库、比对、校验、写入其他文件
            //System.out.println("Sheet名称: " + sheetName + " 行号：" + rowIdx + " 数据：" + rowData);
            
            // 如果表已经阅读结束，则将sql全部插入。
            if (isOver)
            {
            	if (sqls.size() != 0)
            	{
            		so.insert(sqls);
    				sqls.clear();
            	}
            	
            	SwingUtilities.invokeLater(() -> {
            		progress = 100;
            		//System.out.println(progress);
                	ExcelStatusBar.show_progress(progress);
                	progress = 0;
                	//System.out.println(progress);
                });            	
            	return;
            }
            
            // 第0行，是字段行，建表即可
			if (0 == rowIdx) {
				excels.init_one_table(excelPath, sheetName, rowData, isReference);
				currentTable = excels.get_table_name(excelPath, sheetName);
				currentColumns = excels.get_columns(excelPath, sheetName, rowData);
				String sql = String.format(CREATE_TABLE, currentTable, excels.get_columns_type(excelPath, sheetName, rowData));
				//System.out.println("create table: " + sql);
				so.create_table(sql);
				return;
			}

			// 第一行及以后行，是数据行， 整理sql语句，达到指定行数后，插入数据库
			String sql = String.format(INSERT_SQL, currentTable, currentColumns, get_data_with_apostrophe(rowData));
			//System.out.println("insert sql: " + sql);
			sqls.add(sql);
			if (sqls.size() >= 10000) {
				so.insert(sqls);
				sqls.clear();
				progress++;
				//System.out.println(progress);
				SwingUtilities.invokeLater(() -> {
                	ExcelStatusBar.show_progress(progress);
                });
			}
           
        });
        so.close_connection();
	}

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		String excelPath = "E:/large_file.xlsx";
//		StreamReader reader = new StreamReader();
//		reader.read(excelPath);
//		excelPath = "E:/output.xlsx";
//		reader.read(excelPath);
//	}

}
