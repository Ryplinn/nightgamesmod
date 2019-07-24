package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.status.Drowsy;
import nightgames.status.Horny;

public class NeedleThrow extends Skill {

    NeedleThrow() {
        super("Needle Throw");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.ninjutsu) >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance()
                .mobile(user)
                        && !c.getStance()
                             .prone(user)
                        && user.canAct() && user.has(Item.Needle);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Throw a drugged needle at your opponent.";
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 70;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.consume(Item.Needle, 1);
        if (user.roll(user, accuracy(c, user, target))) {
            c.write(user, String.format(
                            "%s %s with one of %s drugged needles. "
                                            + "%s %s with arousal and %s it difficult to stay on %s feet.",
                            user.subjectAction("hit"), target.subject(), user.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.pronoun()), target.action("flush", "flushes"),
                            target.action("find", target.pronoun() + " is finding"), target.possessiveAdjective()));
            target.add(c, Horny.getWithBiologicalType(user, target, 3, 4, user.nameOrPossessivePronoun() + " drugged needle"));
            target.add(c, new Drowsy(target.getType()));
        } else {
            c.write(user,
                            String.format("%s a small, drugged needle at %s, but %s %s it.",
                                            user.subjectAction("throw"), target.subject(),
                                            target.pronoun(), target.action("dodge")));
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new NeedleThrow();
    }

    public int accuracy() {
        return 8;
    }

    public int speed(Character user) {
        return 9;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
