package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Stance;

public class VibroTease extends Skill {

    VibroTease() {
        super("Vibro-Tease");
        addTag(SkillTag.usesToy);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Item.Strapon2);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().dom(user) && c.getStance().en == Stance.anal
                        && user.has(Trait.strapped) && c.getStance().inserted(user)
                        && user.has(Item.Strapon2);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Turn up the strapon vibration";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            if (target.human()) {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
        }
        int m = 10 + Random.random(5);
        target.body.pleasure(user, null, target.body.getRandom("ass"), DamageType.gadgets
                        .modifyDamage(user, target, m), c, new SkillUsage<>(this, user, target));
        user.arouse(2, c);
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 15;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s cranks up the vibration to maximum level which stirs up %s insides. "
                        + "%s teasingly pokes the tip against %s %s which causes %s limbs to get shaky from the pleasure.",
                        user.subject(), target.nameOrPossessivePronoun(),
                        Formatter.capitalizeFirstLetter(user.pronoun()), target.possessiveAdjective(),
                        target.hasBalls() ? "prostate" : "sensitive insides", target.possessiveAdjective());
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
