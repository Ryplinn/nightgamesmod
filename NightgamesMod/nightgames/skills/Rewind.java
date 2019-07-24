package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.status.Primed;

public class Rewind extends Skill {

    Rewind() {
        super("Rewind");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.temporal) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && !c.getStance().prone(user) && user.canAct() && Primed.isPrimed(user, 8);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Rewind your personal time to undo all damage you've taken: 8 charges";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.add(c, new Primed(user.getType(), -8));
        user.getArousal()
                 .empty();
        user.getStamina()
                 .fill();
        user.clearStatus();
        writeOutput(c, Result.normal, user, target);
        user.emote(Emotion.confident, 25);
        user.emote(Emotion.dominant, 20);
        target.emote(Emotion.nervous, 10);
        target.emote(Emotion.desperate, 10);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Rewind();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "It takes a lot of time energy, but you manage to rewind your physical condition back to the very start "
                        + "of the match, removing all damage you've taken.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "%s hits a button on %s wristband and suddenly seems to completely recover. It's like nothing "
                                        + "%s done even happened.",
                        user.getName(), user.possessiveAdjective(),
                        target.subjectAction("have", "has"));
    }

}
