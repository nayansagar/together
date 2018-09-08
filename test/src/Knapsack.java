class Item{
    private int weight, value;

    public Item(int weight, int value) {
        this.weight = weight;
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }
}
public class Knapsack {
    public static void main(String[] args) {
        Item[] items = {new Item(5,3), new Item(2,4), new Item(7,2), new Item(3,9), new Item(4,5)};
        int capacity = 12;
        Item[] ks = fillKnapsack(items, capacity);
    }

    private static Item[] fillKnapsack(Item[] items, int capacity) {

        return items;
    }


}
