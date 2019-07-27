package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;

public class DarkTalisman extends Skill {

    DarkTalisman() {
        super("Dark Talisman");
        addTag(SkillTag.dark);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getRank() >= 1;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance()
                                      .mobile(user)
                        && !c.getStance()
                             .prone(user)
                        && !target.is(Stsflag.enthralled) && user.has(Item.Talisman);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Consume the mysterious talisman to control your opponent";
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return target.is(Stsflag.blinded) ? -100 : 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        Result result = target.is(Stsflag.blinded) ? Result.special
                        : rollSucceeded ? Result.normal : Result.miss;
        writeOutput(c, result, user, target);
        user.consume(Item.Talisman, 1);
        if (result == Result.normal) {
            target.add(c, new Enthralled(target.getType(), user.getType(), Random.random(3) + 1));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return "You brandish the dark talisman, which seems to glow with power. The trinket crumbles to dust, but you see the image remain in the reflection of "
                            + target.getName() + "'s eyes.";
        } else if (modifier == Result.special) {
            return "You hold the talisman in front of " + target.nameOrPossessivePronoun() + " head, but as "
                            + target.pronoun() + " is currently unable to see, it crumbles uselessly in your hands.";
        } else {
            return "You brandish the dark talisman, which seems to glow with power. The trinket crumbles to dust, with "
                            + target + " seemingly unimpressed.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return String.format("%s holds up a strange talisman. %s compelled to look at the thing, captivated by its unholy nature.",
                            user.getName(), Formatter.capitalizeFirstLetter(target.subjectAction("feel")));
        } else if (modifier == Result.special) {
            return String.format("%s something which sounds like sand spilling onto the floor and a cry of annoyed "
                            + "frustration from %s. What could it have been?", user.subjectAction("hear"),
                            target.nameDirectObject());
        } else {
            return String.format("%s holds up a strange talisman. %s a tiny tug on %s consciousness, but it doesn't really affect %s much.",
                            user.getName(), Formatter.capitalizeFirstLetter(target.subjectAction("feel")), target.possessiveAdjective(),
                            target.directObject());
        }
    }

}
