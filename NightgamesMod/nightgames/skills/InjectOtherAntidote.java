package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;

public class InjectOtherAntidote extends Skill {
    private InjectOtherAntidote() {
        super("Inject Antidote (Other)");
    }

    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.medicine) >= 7;
    }

    public boolean usable(Combat c, Character user, Character target) {
        return (c.getStance().mobile(user)) && (user.canAct())
                        && user.has(Item.MedicalSupplies, 1)
                        && (!c.getStance().mobile(user) || !target.canAct());
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, user));
        }
        target.calm(c, target.getArousal().max() / 10);
        target.purge(c);
        user.consume(Item.MedicalSupplies, 1);

        return true;
    }

    public Skill copy(Character user) {
        return new InjectOtherAntidote();
    }

    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "Moving quickly you inject {other:name-do} with an antidote, removing any buffs or debuffs {other:possessive} had.",
                        user, target);
    }

    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return Formatter.format(
                        "{self:SUBJECT} quickly manages to stick {other:name-do} with a hypodermic needle. As the contents flood into {other:possessive} body, {other:pronoun-action:feel|feels} any temporary buffs or debuffs leave {other:direct-object}.",
                        user, target);
    }

    public String describe(Combat c, Character user) {
        return "Injects yourself with an pancea";
    }
}
