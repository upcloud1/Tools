package demo.frame.util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class ExcelStatusBar {
	
	public static JLabel statusLabel;
	public static JProgressBar progressBar;
	private static Timer timer;
	
	public static void hide()
	{
		statusLabel.setVisible(false);
		progressBar.setVisible(false);
	}
	
	private static void auto_hide(int durationMs)
	{
		if (timer != null) timer.stop();
        timer = new Timer(durationMs, e -> {
        	statusLabel.setVisible(false);
            timer.stop();
        });
        timer.setRepeats(false);
        timer.start();
	}
	
	public static void show_error(String msg, int durationMs)
	{
		statusLabel.setText(msg);
		statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		statusLabel.setForeground(Color.BLACK);
		statusLabel.setBackground(new Color(255, 204, 0)); // 黄色系背景
		statusLabel.setOpaque(true);
		//statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
		statusLabel.setVisible(true);
		progressBar.setVisible(false);
		
		// 启动定时器：自动隐藏
        auto_hide(durationMs);
	}
	
	public static void show_info(String msg)
	{
		statusLabel.setText(msg);
		statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		statusLabel.setForeground(Color.BLACK);
		//statusLabel.setBackground(new Color(220, 53, 69)); // 红色系背景
		statusLabel.setOpaque(true);
		//statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
		statusLabel.setVisible(true);
		//System.out.println("ExcelStatusBar.show_info");
	}
	
	public static void show_progress(int progress)
	{
		progressBar.setValue(progress);
		progressBar.setVisible(true);
		//System.out.println("ExcelStatusBar.show_progress");
	}
}
