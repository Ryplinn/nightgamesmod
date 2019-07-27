package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Stance;

public class UseOnahole extends Skill {

    UseOnahole() {
        super(Item.Onahole.getName());
        addTag(SkillTag.usesToy);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return c.getStance().en == Stance.neutral ? 50 : 100;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return (user.has(Item.Onahole) || user.has(Item.Onahole2)) && user.canAct() && target.hasDick()
                        && c.getStance().reachBottom(user) && target.crotchAvailable()
                        && !c.getStance().inserted(target);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int m = 5 + Random.random(10);

        if (rollSucceeded) {
            if (user.has(Item.Onahole2)) {
                m += 5;
                writeOutput(c, Result.upgrade, user, target);
            } else {
                writeOutput(c, Result.normal, user, target);
            }
            m = (int) DamageType.gadgets.modifyDamage(user, target, m);
            target.body.pleasure(user, null, target.body.getRandomCock(), m, c, new SkillUsage<>(this, user, target));
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to stick your onahole onto " + target.getName() + "'s dick, but she manages to avoid it.";
        } else if (modifier == Result.upgrade) {
            return "You slide your onahole over " + target.getName()
                            + "'s dick. The well-lubricated toy covers her member with minimal resistance. As you pump it, she moans in "
                            + "pleasure and her hips buck involuntarily.";
        } else {
            return "You stick your cocksleeve onto " + target.getName()
                            + "'s erection and rapidly pump it. She squirms a bit at the sensation and can't quite stifle a moan.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to stick a cocksleeve on %s dick, but %s %s to avoid it.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("manage"));
        } else if (modifier == Result.upgrade) {
            return String.format("%s slides %s cocksleeve over %s dick and starts pumping it. "
                            + "The sensation is the same as if %s was riding %s, but %s %s the only "
                            + "one who's feeling anything.", user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), user.subject(), target.directObject(),
                            target.pronoun(), target.action("are", "is"));
        } else {
            return String.format("%s forces a cocksleeve over %s erection and begins to pump it. "
                            + "At first the feeling is strange and a little bit uncomfortable, but the "
                            + "discomfort gradually becomes pleasure.", user.subject(),
                            target.nameOrPossessivePronoun());
        }

    }

    @Override
    public String describe(Combat c, Character user) {
        return "Pleasure opponent with an Onahole";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
