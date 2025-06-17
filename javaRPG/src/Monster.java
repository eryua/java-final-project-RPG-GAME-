import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Monster {
    private String name;
    private int maxHp;
    private int currentHp;
    private int attackPower;
    private int expGiven;
    private String imagePath;
    private List<ItemQuantity> lootTable;

    private static final Random random = new Random();

    public Monster(String name, int maxHp, int attackPower, int expGiven, String imagePath) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attackPower = attackPower;
        this.expGiven = expGiven;
        this.imagePath = imagePath;
        this.lootTable = new ArrayList<>();
        initializeLootTableByName();
    }

    public String getName() { return name; }
    public int getHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attackPower; }
    public int getExpGiven() { return expGiven; }
    public String getImagePath() { return imagePath; }

    public boolean isAlive() { return currentHp > 0; }

    public void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp < 0) currentHp = 0;
    }

    private void initializeLootTableByName() {
        if ("슬라임".equals(name)) {
            lootTable.add(new ItemQuantity(
                    new LootItem("끈적한 점액", "슬라임의 끈적한 잔해.", 2), 1 + random.nextInt(2)));
        } else if ("고블린".equals(name)) {
            lootTable.add(new ItemQuantity(
                    new LootItem("고블린 천조각", "고블린이 입던 누더기 천.", 5), 1));
            if (random.nextDouble() < 0.3) {
                lootTable.add(new ItemQuantity(
                        new LootItem("낡은 동전", "오래된 동전.", 1), 1 + random.nextInt(5)));
            }
        } else if ("드래곤".equals(name)) {
            lootTable.add(new ItemQuantity(
                    new LootItem("드래곤 비늘", "단단한 드래곤의 비늘.", 50), 1));
            if (random.nextDouble() < 0.1) {
                // 희귀 아이템 예시: 데이터베이스 연동 필요
                // lootTable.add(new ItemQuantity(...));
            }
        }
    }

    public List<ItemQuantity> dropLoot() {
        List<ItemQuantity> droppedItems = new ArrayList<>();
        if (!isAlive()) {
            for (ItemQuantity drop : lootTable) {
                droppedItems.add(new ItemQuantity(drop.getItem(), drop.getQuantity()));
            }
        }
        return droppedItems;
    }
}