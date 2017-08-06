package nightgames.daytime;

import nightgames.characters.Character;
import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.items.clothing.Clothing;

public class Boutique extends Store {
    public Boutique(Character player) {
        super("Boutique", player);
        Clothing.getAllBuyableFrom("Boutique").forEach(article -> add(article));
    }

    @Override
    public boolean known() {
        if (player.useFemalePronouns()) {
            return Global.checkFlag(Flag.basicStores);
        }
        return false;
    }

    @Override
    public void visit(String choice) {
        Global.gui().clearText();
        Global.gui().clearCommand();
        if (choice.equals("Start")) {
            acted = false;
        }
        if (choice.equals("Leave")) {
            done(acted);
            return;
        }
        checkSale(choice);
        if (player.human()) {
            Global.gui().message(
                            "This is a higher end store for women's clothing. Things may get a bit expensive here.");
            for (Clothing i : clothing().keySet()) {
                Global.gui().message(i.getName() + ": " + i.getPrice() + (player.has(i) ? " (Owned)" : ""));
            }
            Global.gui().message("You have: $" + player.money + " available to spend.");
            displayGoods();
            choose("Leave", Global.gui());
        }
    }

    @Override
    public void shop(Character npc, int budget) {}

}
