package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.Body;
import nightgames.characters.body.CockMod;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Knotted;
import nightgames.status.Stsflag;

public class ToggleKnot extends Skill {

    public ToggleKnot() {
        super("Toggle Knot");
    }

    private boolean isActive(Character target) {
        return target.hasStatus(Stsflag.knotted);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.body.get("cock").stream().anyMatch(cock -> cock.moddedPartCountsAs(user, CockMod.primal));
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return (user.canRespond() && isActive(target)) || (user.canAct() && c.getStance().inserted(user));
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Inflate or deflate your knot.";
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (isActive(c.getOpponent(user))) {
            return "Deflate Knot";
        }
        return "Inflate Knot";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (isActive(target)) {
            if (user.human()) {
                c.write(user,
                                "Deciding she's had enough for now, you let your cock return to its regular shape, once again permitting movement.");
            } else if (c.shouldPrintReceive(target, c)) {
                String part = Random.pickRandom(c.getStance().getPartsFor(c, target, user)).orElse(Body.nonePart).describe(target);
                c.write(user, String.format("%s the intense pressure in %s %s "
                                + "recede as %s allows %s knot to deflate.", target.subjectAction("feel"),
                                target.possessiveAdjective(), part, user.subject(),
                                user.possessiveAdjective()));
            }
            target.removeStatus(Stsflag.knotted);
        } else {
            if (user.human()) {
                c.write(user,
                                "You'd like to stay inside " + target.getName() + " for a bit, so you "
                                                + (c.getStance().canthrust(c, user) ? "thrust" : "buck up")
                                                + " as deep inside of her as you can and send a mental command to the base of your cock, where your"
                                                + " knot soon swells up, locking you inside,");
            } else if (c.shouldPrintReceive(target, c)) {
                String firstPart;
                if (c.getStance().dom(user)) {
                    firstPart = String.format("%s bottoms out inside of %s, and something quickly feels off%s.",
                                    user.subject(), target.nameDirectObject(),
                                    c.isBeingObserved() ? " to " + target.directObject() : "");
                } else {
                    firstPart = String.format("%s pulls %s all the way onto %s cock. "
                                    + "As soon as %s pelvis touches %s, something starts happening.",
                                    user.subject(), target.nameDirectObject(),
                                    user.possessiveAdjective(), user.possessiveAdjective(),
                                    (target.human() || target.useFemalePronouns()) 
                                    ? target.possessiveAdjective() + "s" : "s");
                }
                c.write(user ,String.format("%s A ball swells up at the base of %s dick,"
                                + " growing to the size of a small apple. %s not"
                                                + " getting <i>that</i> out of %s any time soon...",
                                                firstPart, user.nameOrPossessivePronoun(),
                                                Formatter.capitalizeFirstLetter(target.subjectAction("are", "is")),
                                                target.reflectivePronoun()));
            }
            target.add(c, new Knotted(target.getType(), user.getType(), c.getStance().anallyPenetrated(c, target)));
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
