import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Inventory {

    private final Map<Item, Integer> items;
    private final int capacity;

    public Inventory(int capacity) {
        this.items = new HashMap<>();
        this.capacity = capacity;
    }

    public Inventory() {
        this(20);
    }

    public boolean addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) return false;

        items.put(item, items.getOrDefault(item, 0) + quantity);
        return true;
    }

    public boolean removeItem(Item item, int quantity) {
        if (item == null || quantity <= 0) return false;

        Integer current = items.get(item);
        if (current == null || current < quantity) return false;

        if (current.equals(quantity)) {
            items.remove(item);
        } else {
            items.put(item, current - quantity);
        }
        return true;
    }

    public int getQuantity(Item item) {
        return items.getOrDefault(item, 0);
    }

    public Set<Item> getItems() {
        return items.keySet();
    }

    public Map<Item, Integer> getAllItemsWithQuantity() {
        return new HashMap<>(items);
    }

    public void clearInventory() {
        items.clear();
    }

    public String getInventoryString() {
        if (items.isEmpty()) return "가방이 비어있습니다.";

        StringBuilder sb = new StringBuilder("--- 가방 ---\n");
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            sb.append(entry.getKey().getName())
                    .append(": ")
                    .append(entry.getValue())
                    .append("개\n");
        }
        return sb.toString();
    }
}