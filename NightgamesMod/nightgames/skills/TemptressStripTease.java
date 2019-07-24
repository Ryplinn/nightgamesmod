package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.Stance;
import nightgames.status.Alluring;
import nightgames.status.Charmed;

public class TemptressStripTease extends StripTease {

    TemptressStripTease() {
        super("Skillful Strip Tease");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.temptress) && user.get(Attribute.technique) >= 8;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return isDance(c, user) ? 0 : super.getMojoBuilt(c, user);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return isDance(c, user) ? super.getMojoBuilt(c, user) : super.getMojoCost(c, user);
    }

    private boolean canStrip(Combat c, Character user, Character target) {
        boolean sexydance = c.getStance().enumerate() == Stance.neutral && user.canAct() && user.mostlyNude();
        boolean normalstrip = !user.mostlyNude();
        return user.stripDifficulty(target) == 0 && (sexydance || normalstrip);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return canStrip(c, user, target) && user.canAct() && c.getStance().mobile(user)
                        && !c.getStance().prone(user);
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return isDance(c, user) ? "Sexy Dance" : super.getLabel(c, user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return isDance(c, user) ? "Do a slow, titillating dance to charm your opponent."
                        : "Shed your clothes seductively, charming your opponent.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int technique = user.get(Attribute.technique);
        //assert technique > 0;

        if (isDance(c, user)) {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.weak, user, target));
            } else {
                c.write(user, receive(c, 0, Result.weak, user, target));
            }
            target.temptNoSource(c, user, 10 + Random.random(Math.max(5, technique)), this);
            if (Random.random(2) == 0) {
                target.add(c, new Charmed(target.getType(), Random.random(Math.min(3, technique))));
            }
            user.add(c, new Alluring(user.getType(), 3));
        } else {
            if (user.human()) {
                c.write(user, deal(c, 0, Result.normal, user, target));
            } else {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }

            target.temptNoSource(c, user, 15 + Random.random(Math.max(10, technique)), this);
            target.add(c, new Charmed(target.getType(), Random.random(Math.min(5, technique))));
            user.add(c, new Alluring(user.getType(), 5));
            user.undress(c);
        }
        target.emote(Emotion.horny, 30);
        user.emote(Emotion.confident, 15);
        user.emote(Emotion.dominant, 15);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new TemptressStripTease();
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (isDance(c, user)) {
            return String.format("%s backs up a little and starts swinging"
                            + " her hips side to side. Curious as to what's going on, %s"
                            + " %s attacks and watch as she bends and curves, putting"
                            + " on a slow dance that would be very arousing even if she weren't"
                            + " naked. Now, without a stitch of clothing to obscure %s view,"
                            + " the sight stirs %s imagination. %s shocked out of %s"
                            + " reverie when she plants a soft kiss on %s lips, and %s dreamily"
                            + " %s into her eyes as she gets back into a fighting stance.",
                            user.subject(), target.subjectAction("cease"),
                            target.possessiveAdjective(), target.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.subjectAction("are", "is"),
                            target.possessiveAdjective(), target.possessiveAdjective(), target.pronoun(),
                            target.action("gaze"));
        } else {
            return String.format("%s takes a few steps back and starts "
                            + "moving sinuously. She sensually runs her hands over her body, "
                            + "undoing straps and buttons where she encounters them, and starts"
                            + " peeling her clothes off slowly, never breaking eye contact."
                            + " %s can only gawk in amazement as her perfect body is revealed bit"
                            + " by bit, and the thought of doing anything to blemish such"
                            + " perfection seems very unpleasant indeed.", user.subject(),
                            Formatter.capitalizeFirstLetter(target.subject()));
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (isDance(c, user)) {
            return "You slowly dance for " + target.getName() + ", showing off" + " your naked body.";
        } else {
            return "You seductively perform a short dance, shedding clothes as you do so. " + target.getName()
                            + " seems quite taken with it, as " + target.pronoun()
                            + " is practically drooling onto the ground.";
        }
    }

    private boolean isDance(Combat c, Character user) {
        return !super.usable(c, user, c.getOpponent(user)) && usable(c, user, c.getOpponent(user));
    }
}
