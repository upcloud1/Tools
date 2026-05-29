package demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class FileInfo {
	String fileName;
	String innerFileName;
	boolean isReference;
	List<Table> sheets;
}

class Table {
	String sheetName;
	String innerSheetName;
	String tableName;
	HashMap<String, String> fields; // excel字段名称，内部字段名称
	
	
}

public class ExcelInfos {
	
	// 静态实例在类加载时就创建
    private static final ExcelInfos INSTANCE = new ExcelInfos();

    // 私有构造函数，防止外部实例化
    private ExcelInfos() {}

    public static ExcelInfos getInstance() {
        return INSTANCE;
    }
	
	private static int currentFileIndex = 0;
	private static int currentSheetIndex = 0;
	
	private String currentExcel = "";
	private String currentSheet = "";
	
	private HashMap<String, FileInfo> files = new HashMap<>(); // file name of excel, inner file name
	
	private void init_current_index(String excelPath, String sheetName)
	{
		if (currentExcel.isEmpty()) {
			currentExcel = excelPath;
			currentFileIndex = 0;
		}

		if (currentSheet.isEmpty()) {
			currentSheet = sheetName;
			currentSheetIndex = 0;
		}

		if (!currentExcel.equals(excelPath)) {
			currentFileIndex++;
		}

		if (!currentSheet.equals(sheetName)) {
			currentSheetIndex++;
		}
	}
	
	public void init_one_table(String excelPath, String sheetName, List<String> columns, boolean isReference)
	{
		excelPath = excelPath.trim();
		init_current_index(excelPath, sheetName);
		
		FileInfo fi = null;
		if (!files.containsKey(excelPath)) {
			fi = new FileInfo();
			fi.fileName = excelPath;
			fi.innerFileName = "FN" + currentFileIndex;
			fi.isReference = isReference;
			fi.sheets = new ArrayList<Table>();
			files.put(excelPath, fi);
		} 
		
		fi = files.get(excelPath);		
		
		Table t = new Table();
		t.sheetName = sheetName.trim();
		t.innerSheetName = "SN" + currentSheetIndex;
		t.tableName = fi.innerFileName + "_" + t.innerSheetName;
		t.fields = new HashMap<String, String>();
		int i = 0;
		for (String col : columns) {
			col = col.trim();
			t.fields.put(col, "FD" + i);
			i++;
		}
		fi.sheets.add(t);		
	}	
	
	public String get_table_name(String excelPath, String sheetName)
	{
		excelPath = excelPath.trim();
		sheetName = sheetName.trim();
		if (!files.containsKey(excelPath))
		{
			return "";
		}
		
		FileInfo fi = files.get(excelPath);
		for (Table t : fi.sheets)
		{
			if (t.sheetName.equals(sheetName))
			{
				return t.tableName;
			}
		}
		return "";
	}
	
	
	// 智能识别列：考虑到两张表列乱掉的情况
	public String get_columns(String excelPath, String sheetName, List<String> columns) 
	{
		excelPath = excelPath.trim();
		sheetName = sheetName.trim();
		String result = "";
		if (!files.containsKey(excelPath))
		{
			return "";
		}
		
		FileInfo fi = files.get(excelPath);
		for (Table t : fi.sheets)
		{
			if (t.sheetName.equals(sheetName))
			{
				for (String col : columns) {
					col = col.trim();
					if (!t.fields.containsKey(col))
					{
						return "";
					}
					result += t.fields.get(col) + ",";
				}
			}
		}
		
		if (!result.isEmpty())
		{
			result = result.substring(0, result.length()-1);// 去掉最后的逗号
		}
		return result;
	}
	
	public List<String> get_column_list(String excelPath, String sheetName)
	{
		excelPath = excelPath.trim();
		sheetName = sheetName.trim();
		if (!files.containsKey(excelPath))
		{
			return null;
		}
		
		FileInfo fi = files.get(excelPath);
		for (Table t : fi.sheets)
		{
			if (t.sheetName.equals(sheetName))
			{
				return new ArrayList<>(t.fields.keySet());
			}
		}		
		
		return null;
	}
	
	// 类似于："ID TEXT, NAME TEXT, AGE TEXT, ADDRESS TEXT, SALARY TEXT)"
	public String get_columns_type(String excelPath, String sheetName, List<String> columns) 
	{
		excelPath = excelPath.trim();
		sheetName = sheetName.trim();
		String result = "";
		if (!files.containsKey(excelPath))
		{
			return "";
		}
		
		FileInfo fi = files.get(excelPath);
		for (Table t : fi.sheets)
		{
			if (t.sheetName.equals(sheetName))
			{
				for (String col : columns) {
					col = col.trim();
					if (!t.fields.containsKey(col))
					{
						return "";
					}
					result += t.fields.get(col) + " TEXT,";
				}
			}
		}
		
		if (!result.isEmpty())
		{
			result = result.substring(0, result.length()-1);// 去掉最后的逗号
		}
		return result;
	}
	
	public static void main(String[] args) {
		
	}
}
