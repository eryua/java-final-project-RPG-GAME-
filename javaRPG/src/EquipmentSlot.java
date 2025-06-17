public enum EquipmentSlot {
    WEAPON("무기"),
    ARMOR_BODY("갑옷"),
    ARMOR_HEAD("투구"),
    ACCESSORY("장신구");

    private final String displayName;

    EquipmentSlot(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
