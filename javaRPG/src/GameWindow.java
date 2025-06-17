import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class GameWindow extends JFrame {

    private GameManager gameManager;

    private CharacterDisplayPanel characterDisplayPanel;
    private LogPanel logPanel;
    private ButtonPanel buttonPanel;

    private ImageIcon playerStandIcon;
    private ImageIcon[] attackAnimationFrames;

    public GameWindow() {
        setTitle("미니 RPG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        gameManager = new GameManager();
        gameManager.setGameWindow(this);

        logPanel = new LogPanel();
        gameManager.setLogPanel(logPanel);

        Player player = gameManager.getPlayer();
        playerStandIcon = getScaledIcon(player.getImagePath(), 96, 96);
        if (playerStandIcon == null) {
            log("스탠딩 이미지 로드 실패: " + player.getImagePath());
            playerStandIcon = createFallbackIcon(96, 96, Color.GRAY, "용사");
        }

        characterDisplayPanel = new CharacterDisplayPanel(player, gameManager.getCurrentMonster());
        gameManager.setCharacterDisplayPanel(characterDisplayPanel);

        buttonPanel = new ButtonPanel(gameManager);
        gameManager.setButtonPanel(buttonPanel);

        loadAttackAnimationFrames();

        add(characterDisplayPanel, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(0, 5));
        bottom.add(logPanel, BorderLayout.CENTER);
        bottom.add(buttonPanel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(700, 550));
        setVisible(true);

        gameManager.startGame();
    }

    private void loadAttackAnimationFrames() {
        String[] paths = {
                "/images/attack1.png",
                "/images/attack2.png",
                "/images/attack3.png",
                "/images/attack4.png"
        };

        attackAnimationFrames = new ImageIcon[paths.length];

        for (int i = 0; i < paths.length; i++) {
            ImageIcon frame = getScaledIcon(paths[i], 96, 96);
            attackAnimationFrames[i] = (frame != null) ? frame :
                    (playerStandIcon != null ? playerStandIcon : createFallbackIcon(96, 96, Color.MAGENTA, "X"));
        }
    }

    public void playPlayerAttackAnimation() {
        if (characterDisplayPanel == null || attackAnimationFrames == null || attackAnimationFrames.length == 0) {
            gameManager.processActualAttack();
            return;
        }

        if (buttonPanel != null) {
            buttonPanel.updateButtonsForLocation(gameManager.getCurrentLocation(), false);
        }

        final Timer animationTimer = new Timer(120, null);
        final int[] index = {0};
        final int total = attackAnimationFrames.length;

        characterDisplayPanel.setPlayerFrame(attackAnimationFrames[0]);

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                index[0]++;
                if (index[0] < total) {
                    characterDisplayPanel.setPlayerFrame(attackAnimationFrames[index[0]]);
                } else {
                    animationTimer.stop();
                    if (playerStandIcon != null) {
                        characterDisplayPanel.setPlayerFrame(playerStandIcon);
                    }
                    gameManager.processActualAttack();
                }
            }
        };
        animationTimer.addActionListener(listener);
        animationTimer.setInitialDelay(0);
        animationTimer.start();
    }

    public ImageIcon getScaledIcon(String resourcePath, int width, int height) {
        try (InputStream stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream == null) return null;
            BufferedImage image = ImageIO.read(stream);
            if (image == null) return null;
            Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    private ImageIcon createFallbackIcon(int width, int height, Color color, String text) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);

        g.setColor(color);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);
        g.dispose();

        return new ImageIcon(img);
    }

    public void log(String message) {
        if (logPanel != null) logPanel.addLog(message);
    }
}