package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.global.Flag;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingTable;

import java.util.List;

public class ClothingStore extends Store {

    ClothingStore() {
        super("Clothing Store");
        ClothingTable.getAllBuyableFrom("ClothingStore").forEach(this::add);
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
                            "This is a normal retail clothing outlet. For obvious reasons, you'll need to buy anything you want to wear at night in bulk.");
            for (Clothing i : clothing().keySet()) {
                GUI.gui.message(i.getName() + ": " + i.getPrice() + (getPlayer().ownsClothing(i) ? " (Owned)" : ""));
            }
            GUI.gui.message("You have: $" + getPlayer().money + " available to spend.");
            displayGoods(nextChoices);
            choose("Leave", nextChoices);
        }
    }

    @Override
    public void shop(NPC npc, int budget) {
        // TODO Auto-generated method stub

    }

}
