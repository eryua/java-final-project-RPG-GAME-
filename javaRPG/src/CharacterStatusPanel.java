import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CharacterStatusPanel extends JPanel {

    private Player player;

    private JLabel nameLabelValue;
    private JLabel levelLabelValue;
    private JLabel hpLabelValue;
    private JLabel mpLabelValue;
    private JLabel expLabelValue;
    private JLabel goldLabelValue;
    private JLabel attackBaseLabelValue;
    private JLabel attackTotalLabelValue;
    private JLabel defenseBaseLabelValue;
    private JLabel defenseTotalLabelValue;

    private JPanel equipmentDisplayPanel;

    public CharacterStatusPanel(Player player) {
        this.player = player;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel statsGridPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        statsGridPanel.setBorder(BorderFactory.createTitledBorder("기본 정보"));

        statsGridPanel.add(new JLabel("이름:"));
        nameLabelValue = new JLabel();
        statsGridPanel.add(nameLabelValue);

        statsGridPanel.add(new JLabel("레벨:"));
        levelLabelValue = new JLabel();
        statsGridPanel.add(levelLabelValue);

        statsGridPanel.add(new JLabel("HP:"));
        hpLabelValue = new JLabel();
        statsGridPanel.add(hpLabelValue);

        statsGridPanel.add(new JLabel("MP:"));
        mpLabelValue = new JLabel();
        statsGridPanel.add(mpLabelValue);

        statsGridPanel.add(new JLabel("EXP:"));
        expLabelValue = new JLabel();
        statsGridPanel.add(expLabelValue);

        statsGridPanel.add(new JLabel("골드:"));
        goldLabelValue = new JLabel();
        statsGridPanel.add(goldLabelValue);

        statsGridPanel.add(new JLabel("기본 공격력:"));
        attackBaseLabelValue = new JLabel();
        statsGridPanel.add(attackBaseLabelValue);

        statsGridPanel.add(new JLabel("총 공격력:"));
        attackTotalLabelValue = new JLabel();
        statsGridPanel.add(attackTotalLabelValue);

        statsGridPanel.add(new JLabel("기본 방어력:"));
        defenseBaseLabelValue = new JLabel();
        statsGridPanel.add(defenseBaseLabelValue);

        statsGridPanel.add(new JLabel("총 방어력:"));
        defenseTotalLabelValue = new JLabel();
        statsGridPanel.add(defenseTotalLabelValue);

        add(statsGridPanel, BorderLayout.NORTH);

        equipmentDisplayPanel = new JPanel();
        equipmentDisplayPanel.setLayout(new BoxLayout(equipmentDisplayPanel, BoxLayout.Y_AXIS));
        equipmentDisplayPanel.setBorder(BorderFactory.createTitledBorder("장착 장비"));

        JScrollPane equipmentScrollPane = new JScrollPane(equipmentDisplayPanel);
        equipmentScrollPane.setPreferredSize(new Dimension(280, 120));

        add(equipmentScrollPane, BorderLayout.CENTER);

        updateStatus();
    }

    public void updateStatus() {
        if (player == null) return;

        nameLabelValue.setText(player.getName());
        levelLabelValue.setText(String.valueOf(player.getLevel()));
        hpLabelValue.setText(player.getCurrentHp() + " / " + player.getMaxHp());
        mpLabelValue.setText(player.getCurrentMp() + " / " + player.getMaxMp());
        expLabelValue.setText(player.getExp() + " / " + player.getExpToNextLevel());
        goldLabelValue.setText(String.valueOf(player.getGold()));
        attackBaseLabelValue.setText(String.valueOf(player.getAttackPowerBase()));
        attackTotalLabelValue.setText(String.valueOf(player.getTotalAttack()));
        defenseBaseLabelValue.setText(String.valueOf(player.getDefensePowerBase()));
        defenseTotalLabelValue.setText(String.valueOf(player.getTotalDefense()));

        equipmentDisplayPanel.removeAll();
        Map<EquipmentSlot, Equipment> equippedItems = player.getEquippedItems();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Equipment item = equippedItems.get(slot);
            String text = slot.getDisplayName() + ": " + (item != null ? item.getName() : "없음");
            JLabel equipLabel = new JLabel(text);
            equipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            equipmentDisplayPanel.add(equipLabel);
        }

        equipmentDisplayPanel.revalidate();
        equipmentDisplayPanel.repaint();
    }
}