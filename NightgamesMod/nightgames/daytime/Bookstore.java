package nightgames.daytime;

import java.util.List;
import java.util.Map;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.global.Flag;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;

public class Bookstore extends Store {
    public Bookstore() {
        super("Bookstore");
        add(Item.EnergyDrink);
        add(Item.ZipTie);
        add(Item.Phone);
    }

    @Override
    public boolean known() {
        return Flag.checkFlag(Flag.basicStores);
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance) {
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        if (choice.equals("Start")) {
            acted = false;
        } else if (choice.equals("Leave")) {
            done(acted, instance);
            return;
        } else {
            attemptBuy(choice);
        }
        if (getPlayer().human()) {
            GUI.gui.message(
                            "In addition to textbooks, the campus bookstore sells assorted items for everyday use.");
            Map<Item, Integer> MyInventory = this.getPlayer().getInventory();
            for (Item i : stock.keySet()) {
                if (MyInventory.get(i) == null || MyInventory.get(i) == 0) {
                    GUI.gui.message(i.getName() + ": $" + i.getPrice());
                } else {
                    GUI.gui.message(
                                    i.getName() + ": $" + i.getPrice() + " (you have: " + MyInventory.get(i) + ")");
                }
            }
            GUI.gui.message("You have : $" + getPlayer().money + " to spend.");

            displayGoods(nextChoices);
            choose("Leave", nextChoices);
        }
    }

    @Override
    public void shop(NPC npc, int budget) {
        int remaining = budget;
        int bored = 0;
        while (remaining > 25 && bored < 5) {
            for (Item i : stock.keySet()) {
                if (remaining > i.getPrice() && !npc.has(i, 10)) {
                    npc.gain(i);
                    npc.money -= i.getPrice();
                    remaining -= i.getPrice();
                } else {
                    bored++;
                }
            }
        }
    }
}
