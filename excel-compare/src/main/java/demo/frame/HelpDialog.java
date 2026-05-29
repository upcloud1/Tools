package demo.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class HelpDialog extends JDialog {

    public HelpDialog(JFrame parent) {
        super(parent, "帮助说明", true);
        setSize(650, 500);
        setLocationRelativeTo(parent);

        // 读取 Markdown 内容
        String markdown = loadMarkdownFromResource("help.md");

        // 转换为 HTML（使用 Jsoup + 简易渲染）
        String html = convertMarkdownToHTML(markdown);
        System.out.println(html);

        // 显示在 JEditorPane（支持基本富文本）
        JEditorPane editorPane = new JEditorPane("text/html", html);
        editorPane.setEditable(false);
        editorPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        editorPane.setBackground(Color.WHITE);

        // 添加滚动条
        JScrollPane scrollPane = new JScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);

        // 添加关闭按钮
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 从 resources 读取 Markdown
    private String loadMarkdownFromResource(String path) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("找不到资源文件: " + path);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "加载帮助文件失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
        return sb.toString();
    }

    // 将 Markdown 转为简易 HTML（支持标题、列表、加粗、斜体、图片）
    private String convertMarkdownToHTML(String markdown) {
        StringBuilder html = new StringBuilder();

        // 行分割
        String[] lines = markdown.split("\n");

        boolean inList = false;
        boolean inCodeBlock = false;

        for (String line : lines) {
            line = line.trim();

            // 处理标题（# 或 ##）
            if (line.startsWith("#")) {
                int level = 0;
                while (level < line.length() && line.charAt(level) == '#') level++;
                if (level > 6) level = 6;
                String title = line.substring(level).trim();
                html.append("<h").append(level).append(">").append(title).append("</h").append(level).append(">");
                inList = false;
                continue;
            }

            // 处理列表项（- 或 *）
            if (line.startsWith("- ") || line.startsWith("* ")) {
                if (!inList) {
                    html.append("<ul>");
                    inList = true;
                }
                String item = line.substring(2).trim();
                html.append("<li>").append(item).append("</li>");
                continue;
            }

            // 列表结束
            if (inList && !line.startsWith("- ") && !line.startsWith("* ")) {
                html.append("</ul>");
                inList = false;
            }

            // 处理加粗和斜体
            line = line.replace("**", "<strong>").replace("__", "<strong>")
                       .replace("*", "<em>").replace("_", "<em>");

            // 处理代码块（反引号）
            if (line.startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                if (inCodeBlock) {
                    html.append("<pre><code>");
                } else {
                    html.append("</code></pre>");
                }
                continue;
            }

            if (inCodeBlock) {
                html.append("<br>").append(line);
                continue;
            }

            // 处理图片
            if (line.contains("![") && line.contains("](")) {
                int start = line.indexOf("!["), end = line.indexOf("]");
                String alt = line.substring(start + 2, end);
                int urlStart = line.indexOf("(", end + 1), urlEnd = line.indexOf(")", urlStart + 1);
                String src = line.substring(urlStart + 1, urlEnd);
                // 替换为相对路径（确保能加载）
                html.append("<p><img src=\"").append(src).append("\" alt=\"").append(alt).append("\" style=\"max-width:100%;height:auto;\" /></p>");
                continue;
            }

            // 普通段落
            if (!line.isEmpty()) {
                html.append("<p>").append(line).append("</p>");
            }
        }

        // 闭合未完成的标签
        if (inList) html.append("</ul>");
        if (inCodeBlock) html.append("</code></pre>");

        return "<html><head>" +
               "<style>" +
               "body { font-family: 'Microsoft YaHei', sans-serif; margin: 15px; font-size: 13px; }" +
               "h1, h2, h3, h4, h5, h6 { color: #2c3e50; }" +
               "ul { margin-left: 20px; }" +
               "li { margin-bottom: 5px; }" +
               "code { background: #f0f0f0; padding: 2px 5px; border-radius: 3px; font-family: monospace; }" +
               "pre { background: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto; }" +
               "</style>" +
               "</head><body>" + html.toString() + "</body></html>";
    }

    // 启动方法（测试用）
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("主界面");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);

            JButton helpBtn = new JButton("帮助说明");
            helpBtn.addActionListener(e -> new HelpDialog(frame).setVisible(true));

            frame.add(helpBtn, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}