import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanel extends JPanel implements ActionListener {

    private JButton btnSlot1, btnSlot2, btnSlot3;
    private JButton btnTownAction1, btnTownAction2, btnTownAction3, btnTownAction4, btnTownAction5;

    private GameManager gameManager;

    public ButtonPanel(GameManager manager) {
        this.gameManager = manager;
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        btnSlot1 = new JButton();
        btnSlot2 = new JButton();
        btnSlot3 = new JButton();
        btnTownAction1 = new JButton();
        btnTownAction2 = new JButton();
        btnTownAction3 = new JButton();
        btnTownAction4 = new JButton();
        btnTownAction5 = new JButton();

        JButton[] allButtons = {
                btnSlot1, btnSlot2, btnSlot3,
                btnTownAction1, btnTownAction2, btnTownAction3, btnTownAction4, btnTownAction5
        };
        for (JButton button : allButtons) {
            button.addActionListener(this);
        }
    }

    public void updateButtonsForLocation(GameManager.Location location, boolean isPlayerAlive) {
        removeAll();

        if (!isPlayerAlive && location != GameManager.Location.TOWN) {
            configureAndAddButton(btnSlot1, "마을로 귀환", GameManager.PlayerAction.RETURN_TO_TOWN.name(), true);
        } else {
            switch (location) {
                case TOWN:
                    configureAndAddButton(btnSlot1, "슬라임 동굴", GameManager.PlayerAction.GO_TO_SLIME_CAVE.name(), isPlayerAlive);
                    configureAndAddButton(btnSlot2, "고블린 숲", GameManager.PlayerAction.GO_TO_GOBLIN_FOREST.name(), isPlayerAlive);
                    configureAndAddButton(btnSlot3, "드래곤 둥지", GameManager.PlayerAction.GO_TO_DRAGON_LAIR.name(), isPlayerAlive);

                    configureAndAddButton(btnTownAction1, "휴식", GameManager.PlayerAction.REST_IN_TOWN.name(), isPlayerAlive);
                    configureAndAddButton(btnTownAction2, "상점", GameManager.PlayerAction.OPEN_SHOP.name(), isPlayerAlive);
                    configureAndAddButton(btnTownAction3, "퀘스트", GameManager.PlayerAction.OPEN_QUEST_LOG.name(), isPlayerAlive);
                    configureAndAddButton(btnTownAction4, "가방", GameManager.PlayerAction.OPEN_INVENTORY.name(), isPlayerAlive);
                    configureAndAddButton(btnTownAction5, "캐릭터 정보", GameManager.PlayerAction.OPEN_STATUS.name(), isPlayerAlive);
                    break;

                case SLIME_CAVE:
                case GOBLIN_FOREST:
                case DRAGON_LAIR:
                    configureAndAddButton(btnSlot1, "공격", GameManager.PlayerAction.ATTACK.name(), isPlayerAlive);
                    configureAndAddButton(btnSlot2, "회복 (MP 10)", GameManager.PlayerAction.HEAL.name(), isPlayerAlive);
                    configureAndAddButton(btnSlot3, "마을로 귀환", GameManager.PlayerAction.RETURN_TO_TOWN.name(), true);
                    break;

                default:
                    break;
            }
        }

        revalidate();
        repaint();
    }

    private void configureAndAddButton(JButton button, String text, String actionCommand, boolean enabled) {
        button.setText(text);
        button.setActionCommand(actionCommand);
        button.setEnabled(enabled);
        add(button);
    }

    public void updateButtonStatesForDefeat() {
        removeAll();
        configureAndAddButton(btnSlot1, "마을로 귀환", GameManager.PlayerAction.RETURN_TO_TOWN.name(), true);
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command != null && gameManager != null) {
            try {
                GameManager.PlayerAction action = GameManager.PlayerAction.valueOf(command);
                gameManager.handlePlayerAction(action);
            } catch (IllegalArgumentException ignored) {
                // 무시: 정의되지 않은 액션 명령
            }
        }
    }
}