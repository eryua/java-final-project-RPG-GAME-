import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.HashMap;

public class CharacterDisplayPanel extends JPanel {

    private IndividualCharacterPanel playerPanel;
    private IndividualCharacterPanel monsterPanel;
    private BufferedImage backgroundImage;
    private Player currentPlayerRef;
    private Monster currentMonsterRef;

    private Map<GameManager.Location, BufferedImage> backgroundImagesMap;

    public CharacterDisplayPanel(Player player, Monster monster) {
        this.currentPlayerRef = player;
        this.currentMonsterRef = monster;

        setLayout(new GridLayout(1, 2, 20, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        playerPanel = new IndividualCharacterPanel(true);
        monsterPanel = new IndividualCharacterPanel(false);

        backgroundImagesMap = new HashMap<>();
        preloadBackgroundImages();

        if (player != null) {
            updatePlayerDisplay(player);
        } else {
            playerPanel.clearDisplay("플레이어 정보 로드 중...");
        }

        if (monster != null) {
            updateMonsterDisplay(monster);
        } else {
            monsterPanel.clearDisplay("몬스터 없음");
        }

        playerPanel.setOpaque(false);
        monsterPanel.setOpaque(false);

        add(playerPanel);
        add(monsterPanel);
    }

    private void preloadBackgroundImages() {
        loadAndStoreBackgroundImage(GameManager.Location.TOWN, "/images/town_bg.png");
        loadAndStoreBackgroundImage(GameManager.Location.SLIME_CAVE, "/images/slime_cave_bg.png");
        loadAndStoreBackgroundImage(GameManager.Location.GOBLIN_FOREST, "/images/goblin_forest_bg.png");
        loadAndStoreBackgroundImage(GameManager.Location.DRAGON_LAIR, "/images/dragon_lair_bg.png");
    }

    private void loadAndStoreBackgroundImage(GameManager.Location location, String imagePath) {
        try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
            if (stream != null) {
                backgroundImagesMap.put(location, ImageIO.read(stream));
            }
        } catch (IOException e) {
            // 로딩 실패 시 무시
        }
    }

    public void updateBackground(GameManager.Location location) {
        this.backgroundImage = backgroundImagesMap.get(location);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void updatePlayerDisplay(Player player) {
        this.currentPlayerRef = player;
        if (player != null && playerPanel != null) {
            playerPanel.updateDisplay(
                    player.getName(),
                    player.getCurrentHp(), player.getMaxHp(),
                    player.getCurrentMp(), player.getMaxMp(),
                    player.getExp(), player.getExpToNextLevel(),
                    player.getImagePath()
            );
        } else if (playerPanel != null) {
            playerPanel.clearDisplay("플레이어 정보 없음");
        }
    }

    public void updateMonsterDisplay(Monster monster) {
        this.currentMonsterRef = monster;
        if (monster != null && monster.isAlive() && monsterPanel != null) {
            monsterPanel.updateDisplayForMonster(
                    monster.getName(),
                    monster.getHp(), monster.getMaxHp(),
                    monster.getImagePath()
            );
        } else if (monsterPanel != null) {
            monsterPanel.clearDisplay("몬스터 없음");
        }
    }

    public void setPlayerFrame(ImageIcon frameIcon) {
        if (playerPanel != null) {
            playerPanel.setImage(frameIcon);
        }
    }

}