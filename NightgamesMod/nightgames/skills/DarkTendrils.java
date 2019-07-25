package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Bound;
import nightgames.status.Falling;

public class DarkTendrils extends Skill {

    DarkTendrils() {
        super("Dark Tendrils", 4);
        addTag(SkillTag.positioning);
        addTag(SkillTag.knockdown);
        addTag(SkillTag.dark);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.darkness) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && !c.getStance().sub(user) && !c.getStance().prone(user)
                        && !c.getStance().prone(target) && user.canAct();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Summon shadowy tentacles to grab or trip your opponent: 20% Arousal";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.arouse((int) (user.getArousal().max() * .20), c);
        if (target.roll(user, accuracy(c, user, target))) {
            if (Random.random(2) == 1) {
                writeOutput(c, Result.normal, user, target);
                target.add(c, new Bound(target.getType(), 35 + 2 * Math.sqrt(user.getAttribute(Attribute.darkness)), "shadows"));
                target.add(c, new Falling(target.getType()));
            } else if (user.checkVsDc(Attribute.darkness, target.knockdownDC() - user.getMojo().get())) {
                writeOutput(c, Result.weak, user, target);
                target.add(c, new Falling(target.getType()));
            } else {
                writeOutput(c, Result.miss, user, target);
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 75;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You summon dark tentacles to hold " + target.getName() + ", but she twists away.";
        } else if (modifier == Result.weak) {
            return "You summon dark tentacles that take " + target.getName() + " feet out from under her.";
        } else {
            return "You summon a mass of shadow tendrils that entangle " + target.getName()
                            + " and pin her arms in place.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s makes a gesture and evil looking tentacles pop up around %s. %s %s out of the way as they try to grab %s.",
                            user.subject(), target.subject(), Formatter.capitalizeFirstLetter(target.pronoun()),
                            target.action("dive"), target.directObject());
        } else if (modifier == Result.weak) {
            return String.format("%s shadow seems to come to life as dark tendrils wrap around %s legs and bring %s to the floor.",
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(), target.directObject());
        } else {
            return String.format("%s summons shadowy tentacles which snare %s arms and hold %s in place.", 
                            user.subject(), target.nameOrPossessivePronoun(), target.directObject());
        }
    }

}
