package nightgames.daytime;

import nightgames.characters.NPC;
import nightgames.characters.Player;
import nightgames.global.Flag;
import nightgames.global.GameState;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.status.addiction.Addiction;

import java.util.List;
import java.util.Optional;

public class AddictionRemoval extends Activity {

    private static final String UNSAFE_OPT = "Overload it: $5000";
    private static final String SAFE_OPT = "Safe but expensive: $15000";
    private static final String NO_ADDICTION = "The woman refuses your money. \"You clearly don't have any addictions. "
                    + "Would you like a referral to counseling instead?\"";

    AddictionRemoval() {
        super("Addiction Removal");
    }

    @Override
    public boolean known() {
        return Flag.checkFlag(Flag.AddictionAdvice) && GameState.getGameState().characterPool.getPlayer().checkAnyAddiction();
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance) {
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        Optional<Addiction> addiction = getPlayer().getStrongestAddiction();
        switch (choice) {
            case "Start":
                GUI.gui.message("You walk to the place Aesop told you about "
                                + "where you're supposed to be able to get rid of your addictions."
                                + " An imperious-looking woman in a lab coat is there to meet you, and explains"
                                + " that you have two options. The cheap options will overload the addiction,"
                                + " basically making it enormously strong for one night and then snuffing it out"
                                + " completely. The other, more expensive choice will reduce or eliminate the addiction"
                                + " without side-effects, although very strong addictions may require several treatments."
                                + "\n\n(this is a placeholder -- note that these treatments only affect your current"
                                + " strongest addiction)");
                if (getPlayer().money >= 5000) {
                    choose(UNSAFE_OPT, nextChoices);
                    if (getPlayer().money >= 15000) {
                        choose(SAFE_OPT, nextChoices);
                    } else {
                        GUI.gui.message("\n\nA quick look at your finances reveal that only the risky option is"
                                        + " affordable for you right now. That may be a problem.");
                    }
                } else {
                    GUI.gui.message("\n\nUnfortunately, you don't have the cash for either option right now.");
                }
                break;
            case UNSAFE_OPT:
                if (addiction.isPresent()) {
                    getPlayer().money -= 5000;
                    GUI.gui.message("Nervously, you handed over the money for the overload treatment. You don't "
                                    + "remember what happened next, but you do know that now your addiction is far "
                                    + "stronger than before. Let's hope this works.");
                    addiction.get().overload();
                } else {
                    GUI.gui.message(NO_ADDICTION);
                }
                break;
            case SAFE_OPT:
                if (addiction.isPresent()) {
                    getPlayer().money -= 15000;
                    GUI.gui.message("You dole out the mountain of cash and are taken to the back for your treatment."
                                    + " When you emerge, you are completely free of your addiction.");
                    getPlayer().removeAddiction(addiction.get());
                } else {
                    GUI.gui.message(NO_ADDICTION);
                }
                break;
            case "Leave":
                done(true, instance);
                break;
        }
        choose("Leave", nextChoices);
    }

    @Override
    public void shop(NPC npc, int budget) {}

}
