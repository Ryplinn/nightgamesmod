package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;

import java.util.List;

public class Closet extends Activity {

    Closet() {
        super("Change Clothes");
    }

    @Override
    public boolean known() {
        return true;
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance)
                    throws InterruptedException {
        if (choice.equals("Start")) {
            GUI.gui.clearText();
            GUI.gui.clearCommand();
            GUI.gui.changeClothes(getPlayer());
            done(false, instance);
        }
    }

    @Override
    public void shop(NPC npc, int budget) {
        // TODO Auto-generated method stub

    }

}
