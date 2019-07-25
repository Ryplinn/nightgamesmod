package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.items.Item;
import nightgames.status.Bound;
import nightgames.status.Stsflag;

public class Tie extends Skill {

    Tie() {
        super("Bind");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && user.canAct() && c.getStance().reachTop(user)
                        && (user.has(Item.ZipTie) || user.has(Item.Handcuffs))
                        && c.getStance().dom(user)
                        && !target.is(Stsflag.bound)
                        && !target.is(Stsflag.maglocked);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.has(Item.Handcuffs, 1)) {
            user.consume(Item.Handcuffs, 1);
            writeOutput(c, Result.special, user, target);
            target.add(c, new Bound(target.getType(), (40 + 3 * Math.sqrt(user.get(Attribute.cunning))), "handcuffs"));
        } else {
            user.consume(Item.ZipTie, 1);
            if (target.roll(user, accuracy(c, user, target))) {
                writeOutput(c, Result.normal, user, target);
                target.add(c, new Bound(target.getType(), (25 + 3 * Math.sqrt(user.get(Attribute.cunning))), "ziptie"));
            } else {
                writeOutput(c, Result.miss, user, target);
                return false;
            }
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to catch " + target.getName() + "'s hands, but she squirms too much to keep your grip on her.";
        } else if (modifier == Result.special) {
            return "You catch " + target.getName() + "'s wrists and slap a pair of cuffs on her.";
        } else {
            return "You catch both of " + target.getName() + " hands and wrap a ziptie around her wrists.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to tie %s down, but %s %s %s arms free.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("keep"), target.possessiveAdjective());
        } else if (modifier == Result.special) {
            return String.format("%s restrains %s with a pair of handcuffs.",
                            user.subject(), target.nameDirectObject());
        } else {
            return String.format("%s secures %s hands with a ziptie.",
                            user.subject(), target.nameOrPossessivePronoun());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Tie up your opponent's hands with a ziptie";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
