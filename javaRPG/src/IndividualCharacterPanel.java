import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.IOException;

public class IndividualCharacterPanel extends JPanel {

    private JLabel nameLabel;
    private JLabel hpLabel;
    private JLabel mpLabel;
    private JLabel expLabel;
    private JLabel imageLabel;

    private boolean isPlayerPanel;

    public IndividualCharacterPanel(boolean isPlayer) {
        this.isPlayerPanel = isPlayer;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEtchedBorder());
        setOpaque(false);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(96, 96));

        JPanel statsPanel = new JPanel(new GridLayout(0, 1, 0, 2));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        nameLabel = new JLabel("이름: ");
        hpLabel = new JLabel("HP: ");
        statsPanel.add(nameLabel);
        statsPanel.add(hpLabel);

        if (isPlayerPanel) {
            mpLabel = new JLabel("MP: ");
            expLabel = new JLabel("EXP: ");
            statsPanel.add(mpLabel);
            statsPanel.add(expLabel);
        }

        add(imageLabel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }

    public void updateDisplay(String name, int hp, int maxHp, int mp, int maxMp, int exp, int expToNextLevel, String imagePath) {
        if (!isPlayerPanel) return;

        nameLabel.setText("이름: " + name);
        hpLabel.setText(String.format("HP: %d / %d", hp, maxHp));
        if (mpLabel != null) mpLabel.setText(String.format("MP: %d / %d", mp, maxMp));
        if (expLabel != null) expLabel.setText(String.format("EXP: %d / %d", exp, expToNextLevel));
        setImageByPath(imagePath);
    }

    public void updateDisplayForMonster(String name, int hp, int maxHp, String imagePath) {
        if (isPlayerPanel) return;

        nameLabel.setText("이름: " + name);
        hpLabel.setText(String.format("HP: %d / %d", hp, maxHp));
        setImageByPath(imagePath);
    }

    public void setImage(ImageIcon icon) {
        if (imageLabel == null) return;

        if (icon != null) {
            imageLabel.setIcon(icon);
            imageLabel.setText("");
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("이미지 없음");
        }
        repaint();
    }

    private void setImageByPath(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imageLabel.setIcon(null);
            imageLabel.setText("이미지 경로 없음");
            repaint();
            return;
        }

        try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
            if (stream != null) {
                Image img = ImageIO.read(stream);
                if (img != null) {
                    Image scaled = img.getScaledInstance(96, 96, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                    imageLabel.setText("");
                } else {
                    imageLabel.setIcon(null);
                    imageLabel.setText("이미지 오류");
                }
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText(imagePath.substring(imagePath.lastIndexOf('/') + 1) + " 없음");
            }
        } catch (IOException e) {
            imageLabel.setIcon(null);
            imageLabel.setText("로드 실패");
        }

        repaint();
    }

    public void clearDisplay(String message) {
        nameLabel.setText(message);
        hpLabel.setText("HP: - / -");
        if (isPlayerPanel) {
            if (mpLabel != null) mpLabel.setText("MP: - / -");
            if (expLabel != null) expLabel.setText("EXP: - / -");
        }
        imageLabel.setIcon(null);
        imageLabel.setText("");
        repaint();
    }
}