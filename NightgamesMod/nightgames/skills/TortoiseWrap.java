package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Hypersensitive;
import nightgames.status.Stsflag;
import nightgames.status.Tied;

public class TortoiseWrap extends Skill {

    TortoiseWrap() {
        super("Tortoise Wrap");
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.fetishism) >= 24;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().reachTop(user) && !c.getStance().reachTop(target)
                        && user.has(Item.Rope) && c.getStance().dom(user) && !target.is(Stsflag.tied);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "User your bondage skills to wrap your opponent to increase her sensitivity";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        user.consume(Item.Rope, 1);
        writeOutput(c, Result.normal, user, target);
        target.add(c, new Tied(target.getType()));
        target.add(c, new Hypersensitive(target.getType()));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "You skillfully tie a rope around %s's torso "
                                        + "in a traditional bondage wrap. %s moans softly as the "
                                        + "rope digs into %s supple skin.",
                        target.getName(), Formatter.capitalizeFirstLetter(target.pronoun()),
                        target.possessiveAdjective());
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s ties %s up with a complex series of knots. "
                        + "Surprisingly, instead of completely incapacitating %s, "
                        + "%s wraps %s in a way that only "
                        + "slightly hinders %s movement. However, the discomfort of "
                        + "the rope wrapping around %s seems to make %s sense of touch more pronounced.",
                        user.getName(), target.nameDirectObject(), target.directObject(),
                        user.pronoun(), target.directObject(), target.possessiveAdjective(),
                        target.pronoun(), target.possessiveAdjective());
    }

}
