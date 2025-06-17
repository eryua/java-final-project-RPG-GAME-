public class ItemQuantity {
    private Item item;
    private int quantity;

    public ItemQuantity(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }
}