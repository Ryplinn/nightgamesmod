package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class Nurple extends Skill {

    public Nurple() {
        super("Twist Nipples");
        addTag(SkillTag.hurt);
        addTag(SkillTag.mean);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.power) >= 13;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.breastsAvailable() && c.getStance().reachTop(user) && user.canAct();
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 10;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        double m = Random.random(4, 7);
        DamageType damageType = DamageType.physical;
        if (target.roll(user, accuracy(c, user, target))) {
            if (user.has(Item.ShockGlove) && user.has(Item.Battery, 2)) {
                writeOutput(c, Result.special, user, target);
                user.consume(Item.Battery, 2);
                damageType = DamageType.gadgets;
                m += Random.random(16, 30);
            } else {
                writeOutput(c, Result.normal, user, target);
            }
            target.pain(c, user, (int) damageType.modifyDamage(user, target, m));
            target.loseMojo(c, (int) DamageType.technique.modifyDamage(user, target, 5));
            target.emote(Emotion.angry, 15);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public int speed(Character user) {
        return 7;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.has(Item.ShockGlove)) {
            return "Shock breasts";
        } else {
            return getName(c, user);
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You grope at " + target.getName() + "'s breasts, but miss.";
        } else if (modifier == Result.special) {
            return "You grab " + target.getName() + "'s boob with your shock-gloved hand, painfully shocking her.";
        } else {
            return "You pinch and twist " + target.getName() + "'s nipples, causing her to yelp in surprise.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to grab %s nipples, but misses.",
                            user.subject(), target.nameOrPossessivePronoun());
        } else if (modifier == Result.special) {
            return String.format("%s touches %s nipple with %s glove and a jolt of electricity hits %s.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(), target.directObject());
        } else {
            return String.format("%s twists %s sensitive nipples, giving %s a jolt of pain.",
                            user.subject(), target.nameOrPossessivePronoun(), target.directObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Twist opponent's nipples painfully";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
