package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;
import nightgames.status.Drained;
import nightgames.status.TailSucked;

import java.util.Map;
import java.util.Optional;

public class TailSuck extends Skill {

    public TailSuck() {
        super("Tail Suck");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 20 && user.getAttribute(Attribute.darkness) >= 15 && user.has(Trait.energydrain)
                        && user.body.get("tail").size() > 0;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && target.hasDick() && target.body.getRandomCock().isReady(target)
                        && target.crotchAvailable() && c.getStance().mobile(user) && !c.getStance().mobile(target)
                        && !c.getStance().inserted(target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Use your tail to draw in your target's energies";
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return c.getStance().isPartFuckingPartInserted(c, target, target.body.getRandomCock(), user, user.body.getRandom("tail")) ? 200 : 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (c.getStance().isPartFuckingPartInserted(c, target, target.body.getRandomCock(), user, user.body.getRandom("tail"))) {
            writeOutput(c, Result.special, user, target);
            target.body.pleasure(user, user.body.getRandom("tail"), target.body.getRandomCock(),
                            Random.random(10) + 10, c, new SkillUsage<>(this, user, target));
            drain(c, user, target);
        } else if (rollSucceeded) {
            Result res = c.getStance().isBeingFaceSatBy(target, user) ? Result.critical
                            : Result.normal;
            writeOutput(c, res, user, target);
            target.body.pleasure(user, user.body.getRandom("tail"), target.body.getRandomCock(),
                            Random.random(10) + 10, c, new SkillUsage<>(this, user, target));
            drain(c, user, target);
            target.add(c, new TailSucked(target.getType(), user.getType(), power(user)));
        } else if (target.hasBalls()) {
            writeOutput(c, Result.weak, user, target);
            target.body.pleasure(user, user.body.getRandom("tail"), target.body.getRandom("balls"),
                            Random.random(5) + 5, c, new SkillUsage<>(this, user, target));
            return true;
        } else {
            writeOutput(c, Result.miss, user, target);
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return String.format(
                            "Flexing a few choice muscles, you provide extra stimulation"
                                            + " to %s trapped %s, drawing in further gouts of %s energy.",
                            target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                            target.possessiveAdjective());
        } else if (modifier == Result.normal) {
            return String.format(
                            "You open up the special mouth at the end of your"
                                            + " tail and aim it at %s %s. Flashing %s a confident smile, you launch"
                                            + " it forward, engulfing the shaft completely. You take a long, deep breath,"
                                            + " and you feel life flowing in from your tail as well as through"
                                            + " your nose.",
                            target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                            target.directObject());
        } else if (modifier == Result.critical) {
            return String.format(
                            "Making sure %s view is blocked, you swing your tail out in front of you, hovering over"
                                            + " %s %s. Then, you open up the mouth at its tip and carefully lower it over the hard shaft."
                                            + " Amusingly, %s does not seem to understand %s predicament, but as soon as you <i>breathe</i>"
                                            + " in %s quickly catches on. The flow of energy through your tail makes you shudder atop"
                                            + " %s face.",
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            target.body.getRandomCock().describe(target), target.subject(), target.possessiveAdjective(),
                            target.pronoun(), target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format(
                            "You shoot out your tail towards %s unprotected groin, but %s"
                                            + " twists away slightly causing you to just miss %s %s. Instead, your tail"
                                            + " latches onto %s balls. You can't do much with those in this way, so"
                                            + " after a little fondling you let go.",
                            target.nameOrPossessivePronoun(), target.pronoun(), target.possessiveAdjective(),
                            target.body.getRandomCock().describe(target), target.possessiveAdjective());
        } else {
            return String.format(
                            "You shoot out your tail towards %s unprotected groin, but %s"
                                            + " twists away slightly causing you to just miss %s %s.",
                            target.nameOrPossessivePronoun(), target.pronoun(), target.possessiveAdjective(),
                            target.body.getRandomCock().describe(target));
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return String.format(
                            "%s twists and turns %s tail with renewed vigor,"
                                            + " stealing more of %s energy in the process.",
                            user.getName(), user.possessiveAdjective(), target.nameOrPossessivePronoun());
        } else if (modifier == Result.normal) {
            return String.format(
                            "%s grabs %s tail with both hands and aims it at"
                                            + " %s groin. The tip opens up like a flower, revealing a hollow"
                                            + " inside shaped suspiciously like a pussy. Leaving %s no chance"
                                            + " to ponder this curiosity, the tail suddenly flies at %s. The opening"
                                            + ", which does indeed <i>feel</i> like a pussy as well, engulfs %s %s"
                                            + " completely. %s as if %s %s slowly getting weaker the more it"
                                            + " sucks on %s. That is not good.",
                            user.getName(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), target.directObject(),target.directObject(),
                            target.possessiveAdjective(), target.body.getRandomCock().describe(target),
                            Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                            target.pronoun(), target.action("are", "is"), target.directObject());
        } else if (modifier == Result.critical) {
            return String.format(
                            "With %s nose between %s asscheeks as it is, %s some muscles at the base "
                                            + "of %s spine tense up. %s %sn't sure what's going on, but not long after, %s"
                                            + " %s %s %s being swallowed up in a warm sheath. If %s %s weren't in %s face, you would"
                                            + " think %s were fucking %s. Suddenly, the slick canal contracts around %s dick, and"
                                            + " %s %s some of %s strength flowing out of %s and into it. That is not good.",
                            target.possessiveAdjective(), user.nameOrPossessivePronoun(), target.subjectAction("feel"),
                            user.possessiveAdjective(), Formatter.capitalizeFirstLetter(target.pronoun()),
                            target.action("are", "is"), target.pronoun(), target.action("feel"),
                            target.possessiveAdjective(), target.body.getRandomCock().describe(target), 
                            user.possessiveAdjective(), user.body.getRandomPussy().describe(user),
                            target.possessiveAdjective(),
                            user.subject(), target.directObject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("feel"), target.possessiveAdjective(), target.directObject());
        } else if (modifier == Result.weak) {
            return String.format(
                            "%s grabs %s tail with both hands and aims it at"
                                            + " %s groin. The tip opens up like a flower, revealing a hollow"
                                            + " inside shaped suspiciously like a pussy. That cannot be good, so"
                                            + " %s %s hips just in time to evade the tail as it suddenly"
                                            + " launches forward. Evade may be too strong a term, though, as it"
                                            + " misses %s %s but finds %s balls instead. %s does not seem"
                                            + " too interested in them, though, and leaves them alone after"
                                            + " massaging them a bit.",
                            user.getName(), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            target.subjectAction("twist"), target.possessiveAdjective(), target.possessiveAdjective(),
                            target.body.getRandomCock().describe(target), target.possessiveAdjective(), user.getName());
        } else {
            return String.format("%s grabs %s tail with both hands and aims it at"
                            + " %s groin. The tip opens up like a flower, revealing a hollow"
                            + " inside shaped suspiciously like a pussy. That cannot be good, so"
                            + " %s %s hips just in time to evade the tail as it suddenly"
                            + " launches forward..", user.getName(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(),
                            target.subjectAction("twist"), target.possessiveAdjective());
        }
    }

    private void drain(Combat c, Character user, Character target) {
        Optional<Attribute> pickToDrain = Random.pickRandom(target.att.entrySet().stream().filter(e -> e.getValue() != 0)
                        .map(Map.Entry::getKey).toArray(Attribute[]::new));
        if (!pickToDrain.isPresent()) {
            return;
        }
        Attribute toDrain = pickToDrain.get();
        Drained.drain(c, user, target, toDrain, power(user), 20, true);
        target.drain(c, user, (int) DamageType.drain.modifyDamage(user, target, 10), Character.MeterType.STAMINA);
        target.drain(c, user, 1 + Random.random(power(user) * 3), Character.MeterType.MOJO);
        target.emote(Emotion.desperate, 5);
        user.emote(Emotion.confident, 5);
    }

    private int power(Character user) {
        return (int) (1 + user.getAttribute(Attribute.darkness) / 20.0);
    }

}
