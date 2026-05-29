package demo.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SqliteOperation {
	
	private Connection c = null;
	
	public void create_connection()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:excels.db");
		} 
		catch (Exception e) 
		{
			// TODO 这里补充下日志
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		//System.out.println("Opened database successfully");
	}
	
	public void close_connection()
	{
		try {
			if (c != null && !c.isClosed()) {
				c.close();
				c = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void close_statement(Statement stmt)
	{
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public void create_table(String sql)
	{
		if (c == null) {
			create_connection();
		}

		Statement stmt = null;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();			
			stmt.executeUpdate(sql);
			c.commit();			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			try {
				c.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			close_statement(stmt);
			close_connection();
		}
		//System.out.println("Records created successfully");
	}
	
	public void insert(List<String> sqls)
	{
		if (c == null) {
			create_connection();
		}

		Statement stmt = null;
		try {
			c.setAutoCommit(false);
			stmt = c.createStatement();
			for (String sql : sqls) {
				stmt.addBatch(sql);
			}
			stmt.executeBatch();
			c.commit();			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			try {
				c.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			close_statement(stmt);
			close_connection();
		}
		//System.out.println("Records created successfully");	  
	}
	
	public List<Map<String, String>> query(String sql)
	{
		if (c == null) {
			create_connection();
		}

		List<Map<String, String>> result = new ArrayList<>();
		Statement stmt = null;
		try {
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			while (rs.next()) {
				Map<String, String> row = new HashMap<>();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					String value = rs.getObject(i).toString();
					row.put(columnName, value);
				}
				result.add(row);
			}
			rs.close();
			close_statement(stmt);
			close_connection();
			return result;

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} 
		System.out.println("Operation done successfully");
		return result;
	  
	}
	
	public void deleteDatabase() {
	    // 先关闭所有数据库连接（如果有的话）
	    try {
	        if (c != null && !c.isClosed()) {
	            c.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // 再删除文件
	    File dbDir = new File("./excels.db");
	    if (dbDir.exists() && dbDir.isFile()) {
	    	dbDir.delete();
	    }
	}

	public static void main(String[] args) {
		
		List<String> sqls = new ArrayList<>();
		String sql = "CREATE TABLE COMPANY " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " + 
                " AGE            INT     NOT NULL, " + 
                " ADDRESS        CHAR(50), " + 
                " SALARY         REAL)"; 		
		SqliteOperation sqlop = new SqliteOperation();
		sqlop.create_connection();
		sqlop.create_table(sql);
		
		sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (1, 'Paul', 32, 'California', 20000.00 );"; 
		sqls.add(sql);
		sql = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
	            "VALUES (2, 'Allen', 25, 'Texas', 15000.00 );"; 
		sqls.add(sql);
		sqlop.insert(sqls);
		sql = "select * from COMPANY";
		List<Map<String, String > > list = sqlop.query(sql);
		
		for(Map<String, String> m : list)
		{
			for (Entry<String, String> entry : m.entrySet())
			{
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			System.out.println();
		}
		sqlop.close_connection();
	}

}
