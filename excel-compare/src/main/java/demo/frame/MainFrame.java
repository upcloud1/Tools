package demo.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

import demo.ExcelInfos;
import demo.easyexcel.StreamReader;
import demo.frame.util.CheckableTreeCellRenderer;
import demo.frame.util.CheckableTreeNode;
import demo.frame.util.ExcelStatusBar;
import demo.sqlite.SqliteOperation;

class SheetInfo
{
	String excelName;
	String sheetName;
}

public class MainFrame extends JFrame {
	
	private List<String> filesImported = new ArrayList<String>();
	private int selectedSheetNum = 0;
	private List<SheetInfo> selectedSheets = new ArrayList<SheetInfo>();
	private SqliteOperation so = new SqliteOperation();
	
	private JLabel lblTableInfo = new JLabel();
	
	// 创建表格
	private JTable table1 = new JTable();
	private JTable table2 = new JTable();
	
	private int dbOffset = 0;
	private String currentSql1 = "";
	private String currentSql2 = "";
	
	
	DefaultTableModel model1 = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    };
    
    DefaultTableModel model2 = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    };
	
	public MainFrame() {
		setTitle("Excel对比工具");
		
		try {
		    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		    SwingUtilities.updateComponentTreeUI(this); // 刷新界面
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		setSize(1292, 800);
        setLocationRelativeTo(null);
        
		// 添加窗口关闭监听器
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				SqliteOperation so = new SqliteOperation();
				so.deleteDatabase();
				System.exit(0); // 可选：退出程序
			}
		});
        
		// 1. 创建根节点（最顶层）
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Excel 文件列表");

		// 2. 创建模型，绑定根节点
		DefaultTreeModel model = new DefaultTreeModel(root);

		// 3. 创建 JTree 并设置模型
		JTree tree = new JTree(model);
		// 设置自定义渲染器
		//tree.setCellRenderer(new CheckableTreeCellRenderer());

		getContentPane().add(tree, BorderLayout.WEST);
		tree.setCellRenderer(new CheckableTreeCellRenderer());
		// 可选：设置点击行为为“点击复选框不展开”
		tree.setToggleClickCount(1); // 点击一次就触发选中/取消
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu file = new JMenu("文件");
		menuBar.add(file);
		
		JMenuItem importExcel = new JMenuItem("导入文件");
		importExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFileChooser(model);
			}
		});
		file.add(importExcel);
		
		JMenuItem exportReport = new JMenuItem("导出报告");
		exportReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 启动 SwingWorker 执行后台任务
		        new SwingWorker<Void, String>() {

					@Override
					protected Void doInBackground() throws Exception {
						int idx = 0;
						String tableName1 = "", tableName2 = "", sheetName1 = "", sheetName2 = "";
						List<String> columnNames1 = null, columnNames2 = null;
						String columns1 = "", columns2 = "";				
						//String[] dbColumns1, dbColumns2;
						
						if (selectedSheets.size() < 2) 
						{
							ExcelStatusBar.show_error("未选择两个表格进行比较，无需导出报告", 10000);
							return null;
						}
						publish("开始导出比较结果...");
						//ExcelStatusBar.show_info("开始导出比较结果...");
						for (SheetInfo sheet : selectedSheets) {
							if (idx == 0) {
								tableName1 = ExcelInfos.getInstance().get_table_name(sheet.excelName, sheet.sheetName);
								sheetName1 = sheet.excelName + "->" + sheet.sheetName;
								columnNames1 = ExcelInfos.getInstance().get_column_list(sheet.excelName, sheet.sheetName);
								columns1 = ExcelInfos.getInstance().get_columns(sheet.excelName, sheet.sheetName, columnNames1);
								idx++;
								continue;
							}
							tableName2 = ExcelInfos.getInstance().get_table_name(sheet.excelName, sheet.sheetName);
							sheetName2 = sheet.excelName + "->" + sheet.sheetName;
							columnNames2 = ExcelInfos.getInstance().get_column_list(sheet.excelName, sheet.sheetName);
							columns2 = ExcelInfos.getInstance().get_columns(sheet.excelName, sheet.sheetName, columnNames2);
						}
						
						// cursor.execute("SELECT * FROM FN0_SN0 EXCEPT SELECT * FROM FN1_SN0;")
						String sql1 = "SELECT " + columns1 + " FROM " + tableName1 + " EXCEPT SELECT " + columns2 + " FROM " + tableName2 + ";";
						String sql2 = "SELECT " + columns2 + " FROM " + tableName2 + " EXCEPT SELECT " + columns1 + " FROM " + tableName1 + ";";
						
						// Compared to Table 2, the differing rows in Table 1 are
						String out = "Compared to " +  sheetName2 + ", the different rows in " + sheetName1 + " are: \n";
						for(String col : columnNames1)
						{
							out += col + ",";
						}
						out += "\n";
						publish(String.valueOf(10));
						List<Map<String, String>> resultSet1 =  so.query(sql1);
						String[] dbcols = columns1.split(",");
						for (Map<String, String> row : resultSet1) {
							for (int i = 0; i < dbcols.length; i++) {
			                    out += row.get(dbcols[i]) + ",";
			                }
							out += "\n";
						}
						publish(String.valueOf(50));
						out += "\n";
						out += "Compared to " +  sheetName1 + ", the different rows in " + sheetName2 + " are: \n";
						List<Map<String, String>> resultSet2 =  so.query(sql2);
						for (Map<String, String> row : resultSet2) {
							for (int i = 0; i < dbcols.length; i++) {
			                    out += row.get(dbcols[i]) + ",";
			                }
							out += "\n";
						}				
						out += "\n";
						
						try (BufferedWriter writer = new BufferedWriter(new FileWriter("./result_compare.txt"))) {
							writer.write(out);
						} catch (IOException e1) {
							ExcelStatusBar.show_error("写入文件时发生错误：" + e1.getMessage(), 10000);
						}
						
						publish(String.valueOf(100));
						return null;
					}
					
					@Override
		            protected void process(java.util.List<String> chunks) {
		                // 在这里更新进度信息（如状态栏、日志框）
		                for (String message : chunks) {
		                	if (isInteger(message))
		                	{
		                		ExcelStatusBar.show_progress(Integer.parseInt(message));
		                		continue;
		                	}
		                	ExcelStatusBar.show_info(message);
		                }
		            }
					
					@Override
		            protected void done() {
		                // 任务完成后的回调，用于提示成功或失败
		                try {
		                    get(); // 确保异常被捕获
		                    ExcelStatusBar.show_progress(100);
							ExcelStatusBar.show_info("导出结束！请在result_compare.txt中查看！");
		                } catch (Exception e) {	                    
		                    ExcelStatusBar.show_progress(100);
							ExcelStatusBar.show_error("导出失败：" + e.getMessage(),10000);
		                }
		            }
		        
		        }.execute();
								
			}
		});
		file.add(exportReport);
		
		JMenu mnOp = new JMenu("处理");
		menuBar.add(mnOp);
		
		JMenuItem mntmCompare = new JMenuItem("开始比较");
		mntmCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compare_table();
			}
		});
		mnOp.add(mntmCompare);
		
		JMenu mnHelp = new JMenu("帮助");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelp = new JMenuItem("帮助说明");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				File helpFile = new File("help.html");
				if (!helpFile.exists())
				{
					ExcelStatusBar.show_error("找不到帮助说明文件", 10000);
					return;
				}
				
				try
				{
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().browse(helpFile.toURI());
					}
					return;
				} catch (Exception e1)
				{
					ExcelStatusBar.show_error("无法打开帮助说明文件", 10000);
					e1.printStackTrace();
				}
//				try {
//			        // 1. 从 resources 读取 HTML 内容
//			        String htmlPath = "./help.html"; // 放在 resources 目录下
//			        URL resource = getClass().getClassLoader().getResource(htmlPath);
//
//			        if (resource == null) {
//			            ExcelStatusBar.show_error("找不到帮助说明文件", 10000);
//			            return;
//			        }
//
//			        // 2. 转为 File 路径（用于打开浏览器）
//			        File file = new File(resource.toURI());
//
//			        // 3. 用系统默认浏览器打开
//			        Desktop.getDesktop().browse(file.toURI());
//
//			    } catch (Exception ex) {
//			        ex.printStackTrace();
//			        ExcelStatusBar.show_error("无法打开帮助说明文件", 10000);
//			    }			
			}
		});
		mntmHelp.setSelected(true);
		mnHelp.add(mntmHelp);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("导入文件");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFileChooser(model);
			}
		});
		toolBar.add(btnNewButton);
		
		JButton btnCompare = new JButton("开始比较");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compare_table();
			}			
		});
		toolBar.add(btnCompare);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		

        // 设置表格属性（可选）
        table1.setFillsViewportHeight(true);
        table2.setFillsViewportHeight(true);

        // 为两个表格创建滚动面板
        JScrollPane scrollPane1 = new JScrollPane(table1);
        JScrollPane scrollPane2 = new JScrollPane(table2);      
        
        // 同步滚动：共享垂直滚动条
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // 将两个滚动面板的垂直滚动条绑定到同一个模型
        JScrollBar verticalScrollBar = scrollPane1.getVerticalScrollBar();
        //scrollPane2.getVerticalScrollBar().getModel().setValueModel(verticalScrollBar.getModel());
		JScrollBar otherScrollBar = scrollPane2.getVerticalScrollBar();

		// 绑定滚动条值变化
		otherScrollBar.getModel().addChangeListener(e -> {
			if (!otherScrollBar.getValueIsAdjusting()) {
				verticalScrollBar.setValue(otherScrollBar.getValue());
			}
		});

		verticalScrollBar.getModel().addChangeListener(e -> {
			if (!verticalScrollBar.getValueIsAdjusting()) {
				otherScrollBar.setValue(verticalScrollBar.getValue());
			}
		});
			
		verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int value = e.getValue();                  // 当前位置
                int max = e.getAdjustable().getMaximum(); // 最大值（总高度）
                int extent = e.getAdjustable().getVisibleAmount(); // 可见区域大小

                if (value == 0) { // 滚动到顶部
                    if (dbOffset == 0)
                    {
                    	return;
                    }
                    
                    model1.setRowCount(0);
                    model2.setRowCount(0);
                    dbOffset--;                    
                    updateTableModel(currentSql1, dbOffset, model1, table1);
                    updateTableModel(currentSql2, dbOffset, model2, table2);                    
                }
                if (value >= max - extent) { // 滚动到底部
                	model1.setRowCount(0);
                    model2.setRowCount(0);
                	dbOffset++;                    
                    updateTableModel(currentSql1, dbOffset, model1, table1);
                    updateTableModel(currentSql2, dbOffset, model2, table2);
                    
                    int rowCount = model1.getRowCount();
                    if (rowCount > 0) {
                        int lastRow = rowCount - 50;// 将滚动条置于中间
                        table1.scrollRectToVisible(table1.getCellRect(lastRow, 0, true));
                        table2.scrollRectToVisible(table2.getCellRect(lastRow, 0, true));
                    }
                }
            }
        });
		
		// 在 TableComparisonFrame 构造函数中添加以下代码
		table2.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
		    @Override
		    public Component getTableCellRendererComponent(JTable table, Object value,
		                                                  boolean isSelected, boolean hasFocus, int row, int column) {
		        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		        // 获取 table1 中对应位置的值
				try {
					Object val1 = table1.getValueAt(row, column);
					if (!Objects.equals(val1, value)) {
						c.setBackground(Color.RED); // 差异用红色
						c.setForeground(Color.WHITE);
					} else {
						c.setBackground(Color.WHITE);
						c.setForeground(Color.BLACK);
					}

				} catch (Exception e) {
					c.setBackground(Color.RED); // 差异用红色
					c.setForeground(Color.WHITE);
				}

		        return c;
		    }
		});
        
        panel.setLayout(new BorderLayout(0, 0));        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(scrollPane1);
        splitPane.setRightComponent(scrollPane2); // 或放其他内容
        splitPane.setDividerLocation(0.5); // 左右各一半
        splitPane.setResizeWeight(0.5);    // 平均分割
        panel.add(splitPane, BorderLayout.CENTER);
        
        // 必须先设置父窗口
        //ToastNotifier.setParentFrame(this);
		
		tree.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mousePressed(MouseEvent e) {
		        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
		        if (path == null) return;

		        Object node = path.getLastPathComponent();
		        if (!(node instanceof CheckableTreeNode)) return;

		        CheckableTreeNode treeNode = (CheckableTreeNode) node;
		        
		        if (selectedSheetNum == 2 && !treeNode.isChecked())
		        {
		        	ExcelStatusBar.show_error("注意：只能勾选两个Sheet页！", 10000);
		        	return;
		        }
		        
		        treeNode.setChecked(!treeNode.isChecked());
		        

		        // 刷新该节点（否则视觉不更新）
		        model.nodeChanged(treeNode); // tree.repaint(path.getPath()); // 或者 model.nodeChanged(treeNode)
		        //updateChildStates((CheckableTreeNode)treeNode.getParent());

		        // 可选：打印当前选中的 Sheet
		        //System.out.println("当前选中的 Sheet：");
		        selectedSheetNum = collectSelectedSheets((DefaultMutableTreeNode) model.getRoot());
		    }
		});
		
		// 增加状态栏和进度条
		JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ExcelStatusBar.statusLabel = new JLabel("状态：准备就绪");
		ExcelStatusBar.progressBar = new JProgressBar(0, 100);
		ExcelStatusBar.progressBar.setValue(0);
		ExcelStatusBar.progressBar.setStringPainted(true); // 显示百分比
		statusBar.add(ExcelStatusBar.statusLabel);
		statusBar.add(ExcelStatusBar.progressBar);
		ExcelStatusBar.hide();
		
		lblTableInfo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		lblTableInfo.setForeground(Color.BLACK);
		lblTableInfo.setOpaque(true);
		lblTableInfo.setVisible(true);
		panel.add(lblTableInfo, BorderLayout.NORTH);
		panel.add(statusBar, BorderLayout.SOUTH);		
		
	}
	
	private boolean isInteger(String str) {
	    if (str == null || str.isEmpty()) {
	        return false;
	    }
	    // 匹配可选符号 + 一个或多个数字
	    return str.matches("^[-+]?\\d+$");
	}
	
	private void compare_table() {
		model1.setRowCount(0);
		model2.setRowCount(0);
		
		String sql = "";
		int idx=0;				
		
		String tableinfo = "当前比对的数据是：| ";
		for(SheetInfo sheet : selectedSheets)
		{
			String tableName = ExcelInfos.getInstance().get_table_name(sheet.excelName, sheet.sheetName);
			tableinfo += sheet.excelName + "->" + sheet.sheetName + " | ";
			List<String> columnNames = new ArrayList<>(ExcelInfos.getInstance().get_column_list(sheet.excelName, sheet.sheetName));
			String columns = ExcelInfos.getInstance().get_columns(sheet.excelName, sheet.sheetName, columnNames);
			String[] dbColumns = columns.split(",");
			String currentSql = "select " + columns + " from " + tableName + " limit 100 ";
			sql = currentSql + " offset " + dbOffset * 100;
			List<Map<String, String>> resultSet = so.query(sql);
			
			List<Object[]> tableData = new ArrayList<>();
            for (Map<String, String> row : resultSet) {
                Object[] rowData = new Object[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    rowData[i] = row.get(dbColumns[i]);
                }
                tableData.add(rowData);
            }
            
            //System.out.println(sheet.sheetName + ": " + tableData.size());
            
			if (idx == 0) {
				currentSql1 = currentSql;
				model1.setColumnIdentifiers(columnNames.toArray());
				for (Object[] row : tableData) {
					model1.addRow(row);
				}
			} else {
				currentSql2 = currentSql;
				model2.setColumnIdentifiers(columnNames.toArray());
				for (Object[] row : tableData) {
					model2.addRow(row);
				}
			}
			idx++;
		}
		
		lblTableInfo.setText(tableinfo);
		
		table1.setModel(model1);
		table2.setModel(model2);
	}
	
	private String[] get_sql_columns(String str) {
	    String[] parts = str.split(" ", 3); // 限制分割为最多3段
	    if (parts.length < 3) return null; // 不足两个空格
	    
	    String[] cols = parts[1].split(",");
	    return cols; // 第一个空格后、第二个空格前的部分
	}
	
	private void updateTableModel(String sqlinfo, int offset, DefaultTableModel model, JTable table)
	{
		String sql = sqlinfo + " offset " + offset * 100 + ";";
		List<Map<String, String>> resultSet = so.query(sql);
		if (resultSet.size() == 0) // 后面没有数据了，将不再继续更新
		{
			return;
		}

		int colCnt = model.getColumnCount();
		String colNames[] = new String[colCnt];
		for (int i = 0; i < colCnt; i++) {
			colNames[i] = model.getColumnName(i);
		}
		String[] dbColumns = get_sql_columns(sql);

		List<Object[]> tableData = new ArrayList<>();
		for (Map<String, String> row : resultSet) {

			Object[] rowData = new Object[colCnt];
			for (int i = 0; i < colCnt; i++) {
				rowData[i] = row.get(dbColumns[i]);
			}
			tableData.add(rowData);
		}

		model.setColumnIdentifiers(colNames);
		for (Object[] row : tableData) {
			model.addRow(row);
		}
		
		table.setModel(model);
	}
	
	private int collectSelectedSheets(DefaultMutableTreeNode root) {
		
		int checkedCnt = 0;
		selectedSheets.clear();
	    for (int i = 0; i < root.getChildCount(); i++) {
	        DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) root.getChildAt(i);
	        String fileName = fileNode.getUserObject().toString();
	        
	        for (int j = 0; j < fileNode.getChildCount(); j++) {
	            CheckableTreeNode sheetNode = (CheckableTreeNode) fileNode.getChildAt(j);
	            if (sheetNode.isChecked()) {
	            	checkedCnt++;	            	
	            	SheetInfo si = new SheetInfo();
	            	si.excelName = fileName;
	            	si.sheetName = sheetNode.toString();
	            	selectedSheets.add(si);
	                //System.out.println("✅ " + fileName + " → " + sheetNode.toString());
	            }
	        }
	    }
	    
	    return checkedCnt;
	}
	
	public void addExcelFileToTree(String fileName, List<String> sheetNames, DefaultTreeModel model) {
	    CheckableTreeNode fileNode = new CheckableTreeNode(fileName);
	    
	    for (String sheetName : sheetNames) {
	        CheckableTreeNode sheetNode = new CheckableTreeNode(sheetName);
	        sheetNode.setChecked(false); // 默认未选中
	        fileNode.add(sheetNode);
	    }

	    ((DefaultMutableTreeNode) model.getRoot()).add(fileNode);
	    model.reload();
	}
	
	private void showFileChooser(DefaultTreeModel model) {
	    JFileChooser fileChooser = new JFileChooser();

	    // 设置为多选模式
	    fileChooser.setMultiSelectionEnabled(true);

	    // 仅显示 Excel 文件
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "Excel Files (*.xlsx, *.xls)", "xlsx", "xls"
	    );
	    fileChooser.setFileFilter(filter);

	    // 设置初始目录（可选）
	    fileChooser.setCurrentDirectory(new File(".")); // 当前项目目录

	    // 弹出对话框
	    int result = fileChooser.showOpenDialog(null); // null 表示无父窗口

	    if (result == JFileChooser.APPROVE_OPTION) {
	        File[] selectedFiles = fileChooser.getSelectedFiles();

	        // 处理选中的文件，已导入的文件直接过滤
	        for (File file : selectedFiles) {
	        	if (filesImported.contains(file.getAbsolutePath()))
	        	{
	        		continue; // 已导入的文件直接过滤
	        	}
	        	filesImported.add(file.getAbsolutePath());
	            loadExcelFile(file, model);
	            new Thread(() -> {
	            	StreamReader.getInstance().read(file.getAbsolutePath());
	            }).start();
	        }
	    } else {
	        System.out.println("用户取消了选择");
	    }
	}
	
	
	private void loadExcelFile(File file, DefaultTreeModel model) {

		List<String> sheetNames = getSheetNames(file.getAbsolutePath());

		// 通知界面添加这个文件和它的 Sheet
		addExcelFileToTree(file.getAbsolutePath(), sheetNames, model);
	}
	
	public static List<String> getSheetNames(String filePath) {
		List<String> sheetNames = new ArrayList<String>();
		// 使用EasyExcel的ExcelReader读取Excel文件
        try (ExcelReader excelReader = EasyExcel.read(filePath).build()) {
            // 获取Excel文件的sheet元数据列表
            List<ReadSheet> sheetList = excelReader.excelExecutor().sheetList();
            for (ReadSheet sheet : sheetList) {
                //System.out.println("Sheet Name: " + sheet.getSheetName()); // 打印sheet名称
                sheetNames.add(sheet.getSheetName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return sheetNames;
	}
	

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
	}

}
