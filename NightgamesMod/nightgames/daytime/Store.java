package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;

import java.util.HashMap;

public abstract class Store extends Activity {
    protected HashMap<Item, Integer> stock;
    protected HashMap<Clothing, Integer> clothingstock;
    protected boolean acted;

    public Store(String name, Character player) {
        super(name, player);
        stock = new HashMap<Item, Integer>();
        clothingstock = new HashMap<Clothing, Integer>();
        acted = false;
    }

    @Override
    public abstract boolean available();

    @Override
    public abstract void start();

    public void add(Item item) {
        stock.put(item, item.getPrice());
    }

    public void add(Clothing item) {
        clothingstock.put(item, item.getPrice());
    }

    public HashMap<Item, Integer> stock() {
        return stock;
    }

    public HashMap<Clothing, Integer> clothing() {
        return clothingstock;
    }

    protected void displayClothes() {
        for (Clothing i : clothingstock.keySet()) {
            if (!player.hasClothing(i)) {
                Global.global.gui().sale(this, i);
            }
        }
    }

    protected void displayItems() {
        for (Item i : stock.keySet()) {
            Global.global.gui().sale(this, i);
        }
    }

    protected void displayGoods() {
        displayClothes();
        displayItems();
    }

    protected boolean checkSale(String name) {
        for (Item i : stock.keySet()) {
            if (name.equals(i.getName())) {
                buy(i);
                return true;
            }
        }
        for (Clothing i : clothingstock.keySet()) {
            if (name.equals(i.getName())) {
                buy(i);
                return true;
            }
        }
        return false;
    }

    public void buy(Item item) {
        int price = stock.getOrDefault(item, item.getPrice());
        if (player.money >= price) {
            player.modMoney(-price);
            player.gain(item);
            acted = true;
            Global.global.gui().refresh();
        } else {
            Global.global.gui().message("You don't have enough money to purchase that.");
        }
    }

    public void buy(Clothing item) {
        int price = clothingstock.getOrDefault(item, item.getPrice());
        if (player.money >= price) {
            player.modMoney(-price);
            player.gain(item);
            acted = true;
            Global.global.gui().refresh();
        } else {
            Global.global.gui().message("You don't have enough money to purchase that.");
        }

    }

}
