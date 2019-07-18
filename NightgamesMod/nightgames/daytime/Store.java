package nightgames.daytime;

import nightgames.characters.Player;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;
import nightgames.items.Loot;
import nightgames.items.clothing.Clothing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Store extends Activity {
    protected Map<Item, Integer> stock;
    protected Map<Clothing, Integer> clothingstock;
    protected boolean acted;

    public Store(String name) {
        super(name);
        stock = new HashMap<>();
        clothingstock = new HashMap<>();
        acted = false;
    }

    @Override
    public abstract boolean known();

    public void add(Item item) {
        stock.put(item, item.getPrice());
    }

    public void add(Clothing item) {
        clothingstock.put(item, item.getPrice());
    }

    public Map<Item, Integer> stock() {
        return stock;
    }

    public Map<Clothing, Integer> clothing() {
        return clothingstock;
    }

    List<LabeledValue<String>> displayClothes() {
        return clothingstock.keySet().stream().filter(clothing -> !getPlayer().has(clothing)).map(this::sale).collect(Collectors.toList());
    }

    protected List<LabeledValue<String>> displayItems() {
        return stock.keySet().stream().map(this::sale).collect(Collectors.toList());
    }

    void displayGoods(List<LabeledValue<String>> nextChoices) {
        nextChoices.addAll(displayClothes());
        nextChoices.addAll(displayItems());
    }

    void attemptBuy(String name) {
        for (Item i : stock.keySet()) {
            if (name.equals(i.getName())) {
                buy(i);
                return;
            }
        }
        for (Clothing i : clothingstock.keySet()) {
            if (name.equals(i.getName())) {
                buy(i);
                return;
            }
        }
        System.err.println(String.format("Could not find item %s in stock!", name));
    }

    public void buy(Item item) {
        int price = stock.getOrDefault(item, item.getPrice());
        if (getPlayer().money >= price) {
            getPlayer().modMoney(-price);
            getPlayer().gain(item);
            acted = true;
            GUI.gui.refresh();
        } else {
            GUI.gui.message("You don't have enough money to purchase that.");
        }
    }

    public void buy(Clothing item) {
        int price = clothingstock.getOrDefault(item, item.getPrice());
        if (getPlayer().money >= price) {
            getPlayer().modMoney(-price);
            getPlayer().gain(item);
            acted = true;
            GUI.gui.refresh();
        } else {
            GUI.gui.message("You don't have enough money to purchase that.");
        }

    }

    LabeledValue<String> sale(Loot i) {
        return new LabeledValue<>(i.getName(), i.getName(), i.getDesc());
    }
}
