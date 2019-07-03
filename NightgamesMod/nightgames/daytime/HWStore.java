package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.characters.trait.Trait;
import nightgames.global.Flag;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HWStore extends Store {
    HWStore(Player player) {
        super("Hardware Store", player);
        add(Item.Tripwire);
        add(Item.Rope);
        add(Item.Spring);
        add(Item.Sprayer);
        add(Item.EmptyBottle);
    }

    @Override
    public boolean known() {
        return Flag.checkFlag(Flag.basicStores);
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance) {
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        if (choice.equals("Leave")) {
            done(acted, instance);
        } else if (choice.equals("Start")) {
            acted = false;
        } else {
            attemptBuy(choice);
        }
        if (player.human()) {
            GUI.gui.message(
                            "Nothing at the hardware store is designed for the sort of activities you have in mind, but there are components you could use to make some "
                                            + "effective traps.");
            Map<Item, Integer> MyInventory = this.player.getInventory();
            for (Item i : stock.keySet()) {
                if (MyInventory.get(i) == null || MyInventory.get(i) == 0) {
                    GUI.gui.message(i.getName() + ": $" + i.getPrice());
                } else {
                    GUI.gui.message(
                                    i.getName() + ": $" + i.getPrice() + " (you have: " + MyInventory.get(i) + ")");
                }
            }
            GUI.gui.message("You have : $" + player.money + " to spend.");
            displayGoods(nextChoices);
            choose("Leave", nextChoices);
        }
    }

    @Override
    protected List<LabeledValue<String>> displayItems() {
        // Empty bottles are only purchaseable if the player's rank is at least 1.
        return stock.keySet().stream().filter(item -> !(item == Item.EmptyBottle && player.getRank() < 1)).map(this::sale).collect(
                        Collectors.toList());
    }

    @Override
    public void shop(NPC npc, int budget) {
        int remaining = budget;
        int bored = 0;
        while (remaining > 10 && bored < 10) {
            for (Item i : stock.keySet()) {
                boolean emptyBottleCheck = npc.has(Trait.madscientist) || i != Item.EmptyBottle;
                if (remaining > i.getPrice() && !npc.has(i, 20) && emptyBottleCheck) {
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
