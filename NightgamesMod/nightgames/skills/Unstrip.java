package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Primed;

public class Unstrip extends Skill {

    Unstrip() {
        super("Unstrip");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.temporal) >= 8;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && !c.getStance().prone(user) && user.canAct() && Primed.isPrimed(user, 6);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Rewinds your clothing's time to when you were still wearing it: 6 charges";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.outfit.dress(user.outfitPlan);
        user.add(c, new Primed(user.getType(), -6));
        writeOutput(c, Result.normal, user, target);
        user.emote(Emotion.confident, 20);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "It's tricky, but with some clever calculations, you restore the state of your outfit. Your outfit from the "
                        + "start of the night reappears on your body.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "%s sight of %s for just a moment and almost %s a double-take "
                        + "when %s %s %s again, fully dressed. "
                                        + "In the second %s looked away, how did %s "
                                        + "find the time to put %s clothes on?!",
                        target.subjectAction("lose"), user.getName(), target.action("do", "does"),
                        target.pronoun(), target.action("see"), user.directObject(),
                        target.pronoun(), user.pronoun(), user.possessiveAdjective());
    }

}
