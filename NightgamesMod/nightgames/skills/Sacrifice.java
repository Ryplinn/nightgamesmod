package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;

public class Sacrifice extends Skill {

    public Sacrifice() {
        super("Sacrifice", 5);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.darkness) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && !c.getStance().sub(user) && user.getArousal().percent() >= 70;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 20;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Damage yourself to reduce arousal";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        user.pain(c, user, user.getStamina().max() / 3);
        user.calm(c, user.getArousal().max() / 3 + 20 + user.getAttribute(Attribute.darkness));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.calming;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You feed your own lifeforce and pleasure to the darkness inside you. Your legs threaten to give out, but you've regained some self control.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s pinches %s nipples hard while screaming in pain. %s %s "
                        + "stagger in exhaustion, but %s seems much less aroused.",
                        user.subject(), user.nameOrPossessivePronoun(),
                        Formatter.capitalizeFirstLetter(target.subjectAction("see")), user.directObject(),
                        user.pronoun());
    }
}
