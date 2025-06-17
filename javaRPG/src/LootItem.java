public class LootItem extends Item {

    public LootItem(String name, String description, int value) {
        super(name, description, value, true); // 전리품은 겹쳐짐
    }

    @Override
    public String use(Player player, GameManager gameManager) {
        String message = name + " 아이템은 직접 사용할 수 없습니다. 상점에 판매하거나 재료로 사용하세요.";
        if (gameManager != null && gameManager.getLogPanel() != null) {
            gameManager.getLogPanel().addLog(message);
        }
        return message;
    }
}