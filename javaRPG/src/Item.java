public class Item {
    protected String name;
    protected String description;
    protected int value;
    protected boolean stackable;

    public Item(String name, String description, int value, boolean stackable) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.stackable = stackable;
    }

    public Item(String name, String description, int value) {
        this(name, description, value, false);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    public boolean isStackable() {
        return stackable;
    }

    public String use(Player player, GameManager gameManager) {
        String message = name + " 아이템은 직접 사용할 수 없습니다.";
        if (gameManager != null && gameManager.getLogPanel() != null) {
            gameManager.getLogPanel().addLog(message);
        }
        return message;
    }

    @Override
    public String toString() {
        return (description != null && !description.isEmpty())
                ? name + " (" + description + ")"
                : name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}