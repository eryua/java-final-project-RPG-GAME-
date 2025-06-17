import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class InventoryDialog extends JDialog {
    private final Player player;
    private final GameManager gameManager;

    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JButton useButton;
    private JButton equipButton;
    private JTextArea itemDescriptionArea;

    public InventoryDialog(Frame owner, Player player, GameManager gameManager) {
        super(owner, "가방", true);
        this.player = player;
        this.gameManager = gameManager;

        setSize(500, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        inventoryTableModel = new DefaultTableModel(new Object[]{"아이템", "수량"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        itemDescriptionArea = new JTextArea(3, 20);
        itemDescriptionArea.setEditable(false);
        itemDescriptionArea.setLineWrap(true);
        itemDescriptionArea.setWrapStyleWord(true);
        itemDescriptionArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        southPanel.add(new JScrollPane(itemDescriptionArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        useButton = new JButton("사용");
        equipButton = new JButton("장착");
        equipButton.setVisible(false);
        JButton closeButton = new JButton("닫기");

        buttonPanel.add(useButton);
        buttonPanel.add(equipButton);
        buttonPanel.add(closeButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateSelectedItemDetails();
        });

        useButton.addActionListener(e -> useSelectedItem());
        equipButton.addActionListener(e -> equipSelectedItem());
        closeButton.addActionListener(e -> setVisible(false));

        loadInventoryItems();
    }

    private void loadInventoryItems() {
        inventoryTableModel.setRowCount(0);
        for (Map.Entry<Item, Integer> entry : player.getInventory().getAllItemsWithQuantity().entrySet()) {
            inventoryTableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
        updateSelectedItemDetails();
    }

    private void updateSelectedItemDetails() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            Item selectedItem = (Item) inventoryTableModel.getValueAt(selectedRow, 0);
            itemDescriptionArea.setText(selectedItem.getDescription());

            if (selectedItem instanceof Equipment) {
                useButton.setVisible(false);
                equipButton.setVisible(true);
                equipButton.setText(selectedItem.getName() + " 장착");
            } else {
                useButton.setVisible(true);
                equipButton.setVisible(false);
                useButton.setText(selectedItem.getName() + " 사용");
            }
        } else {
            itemDescriptionArea.setText("아이템을 선택하세요.");
            useButton.setVisible(true);
            useButton.setText("사용");
            equipButton.setVisible(false);
        }
    }

    private void useSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "사용할 아이템을 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item item = (Item) inventoryTableModel.getValueAt(selectedRow, 0);
        item.use(player, gameManager);

        loadInventoryItems();
        if (gameManager != null) gameManager.updateUI();
    }

    private void equipSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "장착할 아이템을 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Item item = (Item) inventoryTableModel.getValueAt(selectedRow, 0);
        if (item instanceof Equipment) {
            player.equip((Equipment) item, gameManager);
            loadInventoryItems();
        } else {
            JOptionPane.showMessageDialog(this, "이 아이템은 장착할 수 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
        }
    }
}