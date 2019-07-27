package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class SuckNeck extends Skill {

    SuckNeck() {
        super("Suck Neck");
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().kiss(user, target) && user.canAct();
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 7;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            if (user.getAttribute(Attribute.darkness) >= 1) {
                writeOutput(c, Result.special, user, target);
                int m = target.getStamina().max() / 8;
                target.drain(c, user,
                                (int) DamageType.drain.modifyDamage(user, target, m), Character.MeterType.STAMINA);
            } else {
                writeOutput(c, Result.normal, user, target);
            }
            int m = 1 + Random.random(8);
            target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandom("skin"), m, c, new SkillUsage<>(this, user, target));
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 12;
    }

    @Override
    public int speed(Character user) {
        return 5;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return c.getStance().dom(user) ? 100 : 70;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.getAttribute(Attribute.darkness) >= 1) {
            return "Drain energy";
        } else {
            return getName(c, user);
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You lean in to kiss " + target.getName() + "'s neck, but she slips away.";
        } else if (modifier == Result.special) {
            return "You draw close to " + target.getName()
                            + " as she's momentarily too captivated to resist. You run your tongue along her neck and bite gently. She shivers and you "
                            + "can feel the energy of her pleasure flow into you, giving you strength.";
        } else {
            return "You lick and suck " + target.getName() + "'s neck hard enough to leave a hickey.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s goes after %s neck, but %s %s %s back.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("push", "pushes"),
                            user.possessiveAdjective());
        } else if (modifier == Result.special) {
            return String.format("%s presses %s lips against %s neck. %s gives %s a "
                            + "hickey and %s knees start to go weak. It's like %s strength"
                            + " is being sucked out through "
                            + "%s skin.", user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), user.subject(),
                            target.directObject(), target.possessiveAdjective(), target.possessiveAdjective(),
                            target.possessiveAdjective());
        } else {
            return String.format("%s licks and sucks %s neck, biting lightly when %s %s expecting it.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("aren't", "isn't"));
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Suck on opponent's neck. Highly variable effectiveness";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FOREPLAY;
    }
}
