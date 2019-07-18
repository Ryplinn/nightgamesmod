package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.global.GameState;
import nightgames.gui.LabeledValue;

import java.util.ArrayList;
import java.util.List;

public abstract class Activity {
    protected String name;
    protected int time;

    public Activity(String name) {
        this.name = name;
        time = 1;
    }

    Player getPlayer() {
        return GameState.getGameState().characterPool.getPlayer();
    }

    public abstract boolean known();

    public abstract void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance)
                    throws InterruptedException;

    public int time() {
        return time;
    }

    public void done(boolean acted, ActivityInstance instance) {
        instance.finished = true;
        instance.acted = acted;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract void shop(NPC npc, int budget);

    public void choose(String choice, String tooltip, List<LabeledValue<String>> choices) {
        choices.add(new LabeledValue<>(choice, choice, tooltip));
    }

    public void choose(String choice, List<LabeledValue<String>> choices) {
        choose(choice, null, choices);
    }

    public static class ActivityInstance {
        public final Activity activity;
        public boolean finished = false;
        public boolean acted = false;
        String currentChoice = "Start";
        private int page = 0;

        ActivityInstance(Activity activity) {
            this.activity = activity;
        }

        public void next() {
            page++;
        }

        List<LabeledValue<String>> visit() throws InterruptedException {
            List<LabeledValue<String>> nextChoices = new ArrayList<>();
            activity.visit(currentChoice, page, nextChoices, this);
            return nextChoices;
        }
    }
}
