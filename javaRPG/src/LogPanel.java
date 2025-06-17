import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {

    private final JTextArea logArea;

    public LogPanel() {
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setPreferredSize(new Dimension(getWidth(), 150));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clearLog() {
        logArea.setText("");
    }
}