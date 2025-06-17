public class Equipment extends Item {
    private EquipmentSlot slot;
    private int attackBonus;
    private int defenseBonus;
    private int hpBonus;
    private int mpBonus;

    public Equipment(String name, String description, int value, EquipmentSlot slot,
                     int attackBonus, int defenseBonus, int hpBonus, int mpBonus) {
        super(name, description, value, false);
        this.slot = slot;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
        this.hpBonus = hpBonus;
        this.mpBonus = mpBonus;
    }

    public EquipmentSlot getSlot() { return slot; }
    public int getAttackBonus() { return attackBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public int getHpBonus() { return hpBonus; }
    public int getMpBonus() { return mpBonus; }

    @Override
    public String use(Player player, GameManager gameManager) {
        return player.equip(this, gameManager);
    }
}