package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.stance.Position;
import nightgames.stance.Stance;
import nightgames.status.ArmLocked;
import nightgames.status.LegLocked;
import nightgames.status.Stsflag;

public class SuccubusSurprise extends Skill {

    SuccubusSurprise() {
        super("Succubus Surprise");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 15 || user.get(Attribute.cunning) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && !user.has(Trait.succubus) && user.has(Item.SuccubusDraft)
                        && c.getStance().inserted(target) && !c.getStance().anallyPenetrated(c)
                        && !BodyPart.hasOnlyType(c.getStance().topParts(), "strapon") && c.getStance().sub(user)
                        && user.canSpend(getMojoCost(c, user)) && !target.is(Stsflag.armlocked)
                        && !target.is(Stsflag.leglocked);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use a Succubus Draft and latch unto your opponent.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean oppHasBlessed = c.getStance().insertedPartFor(c, target).moddedPartCountsAs(target, CockMod.blessed);
        if (user.human()) {
            if (oppHasBlessed) {
                c.write(user, deal(c, 0, Result.weak, user, target));
            } else {
                c.write(user, deal(c, 0, Result.normal, user, target));
            }
        } else {
            if (oppHasBlessed) {
                c.write(user, receive(c, 0, Result.weak, user, target));
            } else {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
        }
        user.remove(Item.SuccubusDraft);
        Item.SuccubusDraft.getEffects().forEach(e -> e.use(c, user, target, Item.SuccubusDraft));
        if (isArmLock(c.getStance())) {
            target.add(c, new ArmLocked(target.getType(), 4 * user.get(Attribute.power)));
        } else {
            target.add(c, new LegLocked(target.getType(), 4 * user.get(Attribute.power)));
        }
        new Grind().resolve(c, user, target);

        if (!user.human() && target.human() && !oppHasBlessed
                        && user.getType().equals(CharacterType.get("CUSTOM_NPCSamantha"))) {
            c.write(user, "<br/><br/>\"<i>Do you like your surprise, " + target.getName() + "? I do.\"</i>");
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String result = String.format(
                        "You might be on the receiving end here, but that"
                                        + " doesn't mean you should just give up! You distract %s for a moment,"
                                        + " just long enough to bring a very special bottle to your lips. When %s"
                                        + " notices, %s tries and snatch it away, but you already had swallowed"
                                        + " enough. A sultry wave washes over you as the draft takes effect, and you ",
                        target.getName(), target.pronoun(), target.pronoun());
        if (isArmLock(c.getStance())) {
            result += String.format("grab %s hands and pull %s deeper into you. ", target.possessiveAdjective(),
                            target.directObject());
        } else {
            result += String.format("wrap your legs around %s, trapping %s within.", target.directObject(),
                            target.directObject());
        }
        if (modifier == Result.weak) {
            result += String.format(
                            "%s does not seem too worried, and you can see why when"
                                            + " your new succubus pussy fails to steal even the faintest wisp of energy.",
                            target.getName());
        } else {
            result += String.format("Realizing what is going on, %s frantically tries to pull out, "
                            + "but your hold is unrelenting. You grind against %s, and soon the "
                            + "energy starts flowing.", target.getName(), target.directObject());
        }
        return result;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String result = String.format("Despite %s dominant position, %s seems unfazed."
                        + " %s twists %s head to the side and %s %s gaze, fearing"
                        + " another competitor may be about to crash %s party. There's no one"
                        + " there, though, and when %s back at %s, %s has already downed"
                        + " a draft of some kind. %s grin widens as black wings and a tail form on %s back."
                        + " %s to pull out, but ", target.nameOrPossessivePronoun(),
                        user.getName(), user.subject(), user.possessiveAdjective(),
                        target.subjectAction("follow"), user.possessiveAdjective(),
                        target.possessiveAdjective(), user.getName(), user.directObject(),
                        user.pronoun(), Formatter.capitalizeFirstLetter(user.possessiveAdjective()),
                        user.possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(target.subjectAction("try", "tries")));
        if (isArmLock(c.getStance())) {
            result += String.format("%s grabs %s hands tightly to %s body, holding %s in place. ",
                            user.subject(), target.possessiveAdjective(),
                            user.possessiveAdjective(), target.directObject());
        } else {
            result += String.format("%s wraps %s lithe legs around %s waist, keeping %s inside.",
                            user.subject(), user.possessiveAdjective(),
                            target.possessiveAdjective(), target.directObject());
        }
        if (modifier == Result.weak) {
            result += String.format(" Luckily%s, the blessings on %s cock prevent any serious damage.",
                            target.human() ? "" : " for " + target.directObject(), 
                                            target.nameOrPossessivePronoun());
        } else {
            result += String.format(" %s fears are confirmed as %s %s a terrible suction starting "
                            + "on %s cock, drawing out %s strength.",
                            Formatter.capitalizeFirstLetter(target.nameOrPossessivePronoun()),
                            target.pronoun(), target.action("feel"),
                            target.possessiveAdjective(), target.possessiveAdjective());
        }
        return result;
    }

    private boolean isArmLock(Position p) {
        return p.en != Stance.missionary;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return Math.max(10, 50 - user.get(Attribute.technique));
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 0f;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
