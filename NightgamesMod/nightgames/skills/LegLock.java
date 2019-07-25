package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.status.AttributeBuff;

public class LegLock extends Skill {

    public LegLock() {
        super("Leg Lock");
        // addTag(SkillTag.positioning); it's not, right?
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().dom(user) && c.getStance().reachBottom(user) && c.getStance().prone(target)
                        && user.canAct() && !c.getStance().connected(c);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, Result.normal, user, target);
            target.add(c, new AttributeBuff(target.getType(), Attribute.speed, -2, 5));
            target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random.random(10, 16)));
            target.emote(Emotion.angry, 15);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 24;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You grab " + target.getName() + "'s leg, but she kicks free.";
        } else {
            return "You take hold of " + target.getName() + "'s ankle and force her leg to extend painfully.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to put %s in a leglock, but %s %s away.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("slip"));
        } else {
            return String.format("%s pulls %s leg across %s body in a painful submission hold.",
                            user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "A submission hold on your opponent's leg";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
