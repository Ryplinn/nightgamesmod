package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Frenzied;

public class SweetScent extends Skill {
    SweetScent() {
        super("Sweet Scent", 5);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !target.wary();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 30;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Result res = target.roll(user, accuracy(c, user, target)) ? Result.normal : Result.miss;

        writeOutput(c, res, user, target);
        if (res != Result.miss) {
            target.arouse(25, c);
            target.emote(Emotion.horny, 100);
            target.add(c, new Frenzied(target.getType(), 8));
        }
        return res != Result.miss;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.bio) >= 5;
    }

    @Override
    public int speed(Character user) {
        return 9;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier != Result.miss) {
            return "You breathe out a dizzying pink gas which spreads through the area. " + target.getName()
                            + " quickly succumbs to the cloying scent as her whole body flushes with arousal.";
        } else {
            return "You breathe out a dizzying pink gas, but " + target.getName()
                            + " covers her face and dodges out of the cloud.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier != Result.miss) {
            return String.format("%s breathes out a dizzying pink gas which spreads through the area. "
                            + "%s quickly %s to the cloying scent as %s whole"
                            + " body flushes with arousal.", user.subject(),
                            Formatter.capitalizeFirstLetter(target.subject()),
                            target.action("succumb"), target.possessiveAdjective());
        } else {
            return String.format("%s breathes out a dizzying pink gas, but %s to cover"
                            + " %s face and dodge out of the cloud.", user.subject(),
                            target.subjectAction("manage"), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Breathe out a sweet scent to send your opponent into a frenzy.";
    }
}
