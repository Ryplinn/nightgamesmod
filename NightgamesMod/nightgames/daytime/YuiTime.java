package nightgames.daytime;

import nightgames.characters.Attribute;
import nightgames.characters.Player;
import nightgames.global.Flag;
import nightgames.global.GameState;
import nightgames.gui.GUI;
import nightgames.gui.LabeledValue;
import nightgames.items.Item;

import java.util.Collections;
import java.util.List;

public class YuiTime extends BaseNPCTime {
    private boolean acted;

    YuiTime() {
        super(GameState.getGameState().characterPool.getNPC("Yui"));
        knownFlag = "YuiLoyalty";
        giftedString = "\"Thanks! You're a pretty nice you know?\"";
        giftString = "\"A present? I'm not going to go easy on you even if you bribe me, you know?\"";
        transformationOptionString = "Error";
        loveIntro = "[Placeholder] Yui jumps into your arms as you enter, kissing you forcefully.";
        advTrait = null;
        transformationFlag = "";
    }

    @Override
    public void buildTransformationPool() {
        options = Collections.emptyList();
    }

    @Override
    public void visit(String choice, int page, List<LabeledValue<String>> nextChoices, ActivityInstance instance) {
        GUI.gui.clearText();
        GUI.gui.clearCommand();
        if (choice.equals("Start")) {
            acted = false;
            if (Flag.checkFlag(Flag.YuiAvailable)) {
                GUI.gui.message("Yui greets you happily when you arrive at her hideout/shed. <i>\"Master! It's good to see you. I've collected some items "
                                      + "you may find useful.\"</i> She grins and hands you the items proudly. She's acting so much like an excited puppy that you end up patting "
                                      + "her on the head. She blushes, but seems to like it. <i>\"Would you like to spend some time training? Or is there something else I can do "
                                      + "to help you?\"</i><br/>");
                if (Flag.checkFlag(Flag.Yui)) {
                    GUI.gui.message("");
                } else {
                    GUI.gui.message("Hearing the offer from such an attractive blushing girl stirs up a natural temptation. She'd almost certainly agree to "
                                          + "any sexual request you could think of. However, knowing Yui's sincerity and "
                                          + "innocence, your conscience won't let you take advantage of her loyalty.");
                }
                choose("Train with Yui", nextChoices);
                npc.gainAffection(getPlayer(), Math.min(Math.max(0, 20 - npc.getAffection(getPlayer())), 5));
                getPlayer().gainAffection(npc, Math.min(Math.max(0, 20 - getPlayer().getAffection(npc)), 5));
                Flag.unflag(Flag.YuiAvailable);
                acted = true;
                getPlayer().gain(Item.Tripwire);
                getPlayer().gain(Item.Rope);
                if (getPlayer().getPure(Attribute.ninjutsu) >= 1) {
                    getPlayer().gain(Item.Needle, 3);
                }
                if (getPlayer().getPure(Attribute.ninjutsu) >= 3) {
                    getPlayer().gain(Item.SmokeBomb, 2);
                }
            } else {
                GUI.gui.message("You head to Yui's hideout, but find it empty. She must be out doing something. It would be a lot easier to track her down if she "
                                      + "had a phone.");
            }
            choose("Leave", nextChoices);
        } else if (choice.equals("Leave")) {
            done(acted, instance);
        } else if (choice.startsWith("Train")) {
            GUI.gui.message("Yui's skills at subterfuge turn out to be as strong as she claimed. She's also quite a good teacher. Apparently she helped train her "
                                  + "younger sister, so she's used to it. Nothing she teaches you is overtly sexual, but you can see some useful applications for the Games.");
            getPlayer().modAttributeDontSaveData(Attribute.ninjutsu, 1);
            Flag.flag("Trained" + Attribute.ninjutsu.name());
            choose("Leave", nextChoices);
        }
    }

    @Override
    public void subVisit(String choice, List<LabeledValue<String>> nextChoices) {

    }

    @Override
    public void subVisitIntro(String choice, List<LabeledValue<String>> nextChoices) {

    }

}
