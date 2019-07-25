package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;

public class PerfectTouch extends Skill {

    PerfectTouch() {
        super("Sleight of Hand");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && !target.torsoNude() && !c.getStance().prone(user)
                        && user.canAct() && !c.getStance().connected(c);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.normal, user, target));
                c.write(target, target.nakedLiner(c, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
            target.undress(c);
            target.emote(Emotion.nervous, 10);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.cunning) >= 18;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return Math.round(Math.max(Math.min(150,
                        2.5f * (user.get(Attribute.cunning) - c.getOpponent(user).get(Attribute.cunning)) + 65),
                        40));
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to steal " + target.getName() + "'s clothes off of her, but she catches you.";
        } else {
            return "You feint to the left while your right hand makes quick work of " + target.getName()
                            + "'s clothes. By the time she realizes what's happening, you've "
                            + "already stripped all her clothes off.";
        }

    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s lunges toward %s, but %s %s %s hands"
                            + " before %s can get ahold of %s clothes.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("catch"),
                            target.possessiveAdjective(), user.pronoun(),
                            target.possessiveAdjective());
        } else {
            return String.format("%s lunges towards %s, but dodges away without hitting %s. "
                            + "%s tosses aside a handful of clothes, "
                            + "at which point %s %s %s "
                            + "naked. How the hell did %s manage that?",
                            user.subject(), target.nameDirectObject(), target.directObject(),
                            user.subject(), target.subjectAction("realize"), target.pronoun(),
                            target.action("are", "is"), user.pronoun());
        }

    }

    @Override
    public String describe(Combat c, Character user) {
        return "Strips opponent completely: 25 Mojo";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
