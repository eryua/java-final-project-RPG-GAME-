import java.util.*;

public class Player {
    private String name;
    private int maxHpBase;
    private int currentHp;
    private int maxMpBase;
    private int currentMp;
    private int attackPowerBase;
    private int defensePowerBase;
    private int experience;
    private int level;
    private String imagePath;
    private int expToNextLevel;

    private Inventory inventory;
    private Map<EquipmentSlot, Equipment> equippedItems;
    private List<Quest> activeQuests;
    private int gold;

    private static final Random random = new Random();

    public Player(String name, int maxHp, int maxMp, int attackPower) {
        this.name = name;
        this.maxHpBase = maxHp;
        this.currentHp = maxHp;
        this.maxMpBase = maxMp;
        this.currentMp = maxMp;
        this.attackPowerBase = attackPower;
        this.defensePowerBase = 0;
        this.experience = 0;
        this.level = 1;
        this.imagePath = "/images/hero_stand.png";
        this.expToNextLevel = calculateExpToNextLevel(level);
        this.inventory = new Inventory(20);
        this.equippedItems = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            equippedItems.put(slot, null);
        }
        this.activeQuests = new ArrayList<>();
        this.gold = 100;
    }

    public Player(String name, int maxHp, int maxMp, int attackPower, String imagePath) {
        this(name, maxHp, maxMp, attackPower);
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() {
        int bonus = equippedItems.values().stream().filter(Objects::nonNull).mapToInt(Equipment::getHpBonus).sum();
        return maxHpBase + bonus;
    }
    public int getCurrentMp() { return currentMp; }
    public int getMaxMp() {
        int bonus = equippedItems.values().stream().filter(Objects::nonNull).mapToInt(Equipment::getMpBonus).sum();
        return maxMpBase + bonus;
    }
    public int getAttackPowerBase() { return attackPowerBase; }
    public int getTotalAttack() {
        int bonus = equippedItems.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .mapToInt(e -> e.getValue().getAttackBonus()).sum();
        return attackPowerBase + bonus;
    }
    public int getDefensePowerBase() { return defensePowerBase; }
    public int getTotalDefense() {
        int bonus = equippedItems.values().stream().filter(Objects::nonNull).mapToInt(Equipment::getDefenseBonus).sum();
        return defensePowerBase + bonus;
    }
    public int getLevel() { return level; }
    public int getExp() { return experience; }
    public String getImagePath() { return imagePath; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public Inventory getInventory() { return inventory; }
    public Map<EquipmentSlot, Equipment> getEquippedItems() { return new HashMap<>(equippedItems); }
    public Equipment getEquippedItem(EquipmentSlot slot) { return equippedItems.get(slot); }
    public List<Quest> getActiveQuests() { return new ArrayList<>(activeQuests); }
    public int getGold() { return gold; }

    public boolean isAlive() {
        return currentHp > 0;
    }

    public void takeDamage(int incomingDamage) {
        int finalDamage = Math.max(0, incomingDamage - getTotalDefense());
        currentHp = Math.max(0, currentHp - finalDamage);
    }

    public void healHp(int amount) {
        currentHp = Math.min(getMaxHp(), currentHp + amount);
    }

    public boolean useMp(int amount) {
        if (currentMp >= amount) {
            currentMp -= amount;
            return true;
        }
        return false;
    }

    public void gainExp(int exp) {
        if (!isAlive()) return;
        experience += exp;
        while (experience >= expToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        experience -= expToNextLevel;
        expToNextLevel = calculateExpToNextLevel(level);

        maxHpBase += 15 + random.nextInt(11);
        maxMpBase += 8 + random.nextInt(8);
        attackPowerBase += 2 + random.nextInt(4);
        defensePowerBase += 1 + random.nextInt(2);

        currentHp = getMaxHp();
        currentMp = getMaxMp();
    }

    private int calculateExpToNextLevel(int currentLevel) {
        return 100 * currentLevel + (currentLevel * currentLevel * 10);
    }

    public void restFully() {
        currentHp = getMaxHp();
        currentMp = getMaxMp();
    }

    public String equip(Equipment newEquipment, GameManager gameManager) {
        if (newEquipment == null) return "장착할 아이템이 없습니다.";
        if (!inventory.removeItem(newEquipment, 1)) {
            String msg = newEquipment.getName() + " 아이템이 가방에 없어 장착할 수 없습니다.";
            if (gameManager != null) gameManager.log(msg);
            return msg;
        }

        EquipmentSlot slot = newEquipment.getSlot();
        Equipment oldEquipment = equippedItems.get(slot);
        if (oldEquipment != null) {
            inventory.addItem(oldEquipment, 1);
            if (gameManager != null) gameManager.log(oldEquipment.getName() + " 아이템을 가방에 넣었습니다.");
        }

        equippedItems.put(slot, newEquipment);
        currentHp = Math.min(currentHp, getMaxHp());
        currentMp = Math.min(currentMp, getMaxMp());

        String message = newEquipment.getName() + " 을(를) " + slot.getDisplayName() + "에 장착했습니다.";
        if (gameManager != null) {
            gameManager.log(message);
            gameManager.updateUI();
        }
        return message;
    }

    public String unequip(EquipmentSlot slot, GameManager gameManager) {
        Equipment oldEquipment = equippedItems.put(slot, null);
        if (oldEquipment != null) {
            inventory.addItem(oldEquipment, 1);
            currentHp = Math.min(currentHp, getMaxHp());
            currentMp = Math.min(currentMp, getMaxMp());

            String msg = oldEquipment.getName() + " 아이템 장착을 해제했습니다.";
            if (gameManager != null) {
                gameManager.log(msg);
                gameManager.updateUI();
            }
            return msg;
        }
        String msg = slot.getDisplayName() + "에 장착된 아이템이 없습니다.";
        if (gameManager != null) gameManager.log(msg);
        return msg;
    }

    public void addQuest(Quest quest) {
        if (quest != null && quest.getStatus() == QuestStatus.ACTIVE && !activeQuests.contains(quest)) {
            activeQuests.add(quest);
        }
    }

    public void addGold(int amount) {
        if (amount > 0) gold += amount;
    }

    public boolean spendGold(int amount) {
        if (amount > 0 && gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }
}