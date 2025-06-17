import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterStatusDialog extends JDialog {

    private CharacterStatusPanel statusPanel;
    private Player player;

    public CharacterStatusDialog(JFrame owner, Player player) {
        super(owner, "캐릭터 정보", true);
        this.player = player;

        setSize(350, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusPanel = new CharacterStatusPanel(this.player);
        add(statusPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.add(closeButton);
        add(buttonPane, BorderLayout.SOUTH);
    }

    public void updateStatus() {
        if (statusPanel != null) {
            statusPanel.updateStatus();
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (statusPanel != null) {
            statusPanel.updateStatus();
        }
    }
}