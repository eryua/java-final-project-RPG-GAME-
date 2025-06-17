import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ShopDialog extends JDialog {

    private GameManager gameManager;
    private Player player;

    private JTable shopItemTable;
    private JTable playerInventoryTable;
    private DefaultTableModel shopTableModel;
    private DefaultTableModel playerInventoryTableModel;

    private JLabel playerGoldLabel;
    private JButton buyButton;
    private JButton sellButton;
    private JButton closeButton;

    public ShopDialog(Frame owner, GameManager gameManager) {
        super(owner, "상점", true);
        this.gameManager = gameManager;
        this.player = gameManager.getPlayer();

        initializeUI();
        setupEvents();
        refreshAll();
    }

    private void initializeUI() {
        setSize(600, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        // 골드 표시
        playerGoldLabel = new JLabel("", SwingConstants.CENTER);
        add(playerGoldLabel, BorderLayout.NORTH);

        // 테이블 설정
        shopTableModel = new DefaultTableModel(new Object[]{"아이템", "가격", "설명"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        shopItemTable = new JTable(shopTableModel);
        JScrollPane shopScrollPane = new JScrollPane(shopItemTable);

        playerInventoryTableModel = new DefaultTableModel(new Object[]{"아이템", "수량", "판매가"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        playerInventoryTable = new JTable(playerInventoryTableModel);
        JScrollPane inventoryScrollPane = new JScrollPane(playerInventoryTable);

        // 상점/인벤토리 패널
        JPanel shopPanel = new JPanel(new BorderLayout());
        shopPanel.setBorder(BorderFactory.createTitledBorder("상점 판매 목록"));
        shopPanel.add(shopScrollPane, BorderLayout.CENTER);
        buyButton = new JButton("구매");
        shopPanel.add(buyButton, BorderLayout.SOUTH);

        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("내 가방 (판매)"));
        inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);
        sellButton = new JButton("판매");
        inventoryPanel.add(sellButton, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.add(shopPanel);
        centerPanel.add(inventoryPanel);
        add(centerPanel, BorderLayout.CENTER);

        // 닫기 버튼
        closeButton = new JButton("나가기");
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(closeButton);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        buyButton.addActionListener(e -> handleBuy());
        sellButton.addActionListener(e -> handleSell());
        closeButton.addActionListener(e -> setVisible(false));
    }

    private void refreshAll() {
        updateGoldLabel();
        loadShopItems();
        loadInventoryItems();
    }

    private void updateGoldLabel() {
        playerGoldLabel.setText("소지 골드: " + player.getGold());
    }

    private void loadShopItems() {
        shopTableModel.setRowCount(0);
        for (Item item : gameManager.getShopInventory()) {
            shopTableModel.addRow(new Object[]{item, item.getValue(), item.getDescription()});
        }
    }

    private void loadInventoryItems() {
        playerInventoryTableModel.setRowCount(0);
        for (Map.Entry<Item, Integer> entry : player.getInventory().getAllItemsWithQuantity().entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();
            int sellPrice = Math.max(1, item.getValue() / 2);
            playerInventoryTableModel.addRow(new Object[]{item, quantity, sellPrice});
        }
    }

    private void handleBuy() {
        int selected = shopItemTable.getSelectedRow();
        if (selected == -1) {
            showMessage("구매할 아이템을 선택하세요.");
            return;
        }
        Item item = (Item) shopTableModel.getValueAt(selected, 0);
        boolean success = gameManager.buyItemFromShop(item.getName());
        if (!success) {
            showMessage("골드가 부족하거나 구매할 수 없습니다.");
        }
        refreshAll();
    }

    private void handleSell() {
        int selected = playerInventoryTable.getSelectedRow();
        if (selected == -1) {
            showMessage("판매할 아이템을 선택하세요.");
            return;
        }

        Item item = (Item) playerInventoryTableModel.getValueAt(selected, 0);
        int maxQty = player.getInventory().getQuantity(item);

        String input = JOptionPane.showInputDialog(this, "판매할 수량 (1~" + maxQty + "):", "수량 입력", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        try {
            int qty = Integer.parseInt(input.trim());
            if (qty <= 0 || qty > maxQty) {
                showMessage("잘못된 수량입니다.");
                return;
            }
            gameManager.sellItemToShop(item, qty);
            refreshAll();
        } catch (NumberFormatException ex) {
            showMessage("숫자를 입력해주세요.");
        }
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "알림", JOptionPane.WARNING_MESSAGE);
    }
}