package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.stance.Stance;
import nightgames.status.Abuff;
import nightgames.status.Stsflag;
import nightgames.status.TailSucked;

public class TailSuck extends Skill {

    public TailSuck(Character self) {
        super("Tail Suck", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.Seduction) >= 20 && user.get(Attribute.Dark) >= 15 && user.has(Trait.energydrain)
                        && user.body.get("tail").size() > 0;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canAct() && target.hasDick() && target.body.getRandomCock().isReady(target)
                        && target.crotchAvailable() && c.getStance().mobile(getSelf()) && !c.getStance().mobile(target)
                        && !c.getStance().inserted(target);
    }

    @Override
    public String describe(Combat c) {
        return "Use your tail to draw in your target's energies";
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        if (target.is(Stsflag.tailsucked)) {
            if (getSelf().human()) {
                c.write(getSelf(), deal(c, 0, Result.special, target));
            } else if (target.human()) {
                c.write(getSelf(), receive(c, 0, Result.special, target));
            }
            target.body.pleasure(getSelf(), getSelf().body.getRandom("tail"), target.body.getRandomCock(),
                            Global.random(10) + 10, c, this);
            drain(c, target);
        } else if (getSelf().roll(this, c, accuracy(c))) {
            Result res = c.getStance().en == Stance.facesitting && c.getStance().dom(getSelf()) ? Result.critical
                            : Result.normal;
            if (getSelf().human()) {
                c.write(getSelf(), deal(c, 0, res, target));
            } else if (target.human()) {
                c.write(getSelf(), receive(c, 0, res, target));
            }
            target.body.pleasure(getSelf(), getSelf().body.getRandom("tail"), target.body.getRandomCock(),
                            Global.random(10) + 10, c, this);
            drain(c, target);
            target.add(c, new TailSucked(target, getSelf(), power()));
        } else if (target.hasBalls()) {
            if (getSelf().human()) {
                c.write(getSelf(), deal(c, 0, Result.weak, target));
            } else if (target.human()) {
                c.write(getSelf(), receive(c, 0, Result.weak, target));
            }
            target.body.pleasure(getSelf(), getSelf().body.getRandom("tail"), target.body.getRandom("balls"),
                            Global.random(5) + 5, c, this);
            return true;
        } else {
            if (getSelf().human()) {
                c.write(getSelf(), deal(c, 0, Result.miss, target));
            } else if (target.human()) {
                c.write(getSelf(), receive(c, 0, Result.miss, target));
            }
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new TailSuck(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.special) {
            return String.format(
                            "Flexing a few choice muscles, you provide extra stimulation"
                                            + " to %s trapped %s, drawing in further gouts of %s energy.",
                            target.nameOrPossessivePronoun(), target.body.getRandomCock().describe(target),
                            target.possessivePronoun());
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
                            target.nameOrPossessivePronoun(), target.possessivePronoun(),
                            target.body.getRandomCock().describe(target), target.subject(), target.possessivePronoun(),
                            target.pronoun(), target.possessivePronoun());
        } else if (modifier == Result.weak) {
            return String.format(
                            "You shoot out your tail towards %s unprotected groin, but %s"
                                            + " twists away slightly causing you to just miss %s %s. Instead, your tail"
                                            + " latches onto %s balls. You can't do much with those in this way, so"
                                            + " after a little fondling you let go.",
                            target.nameOrPossessivePronoun(), target.pronoun(), target.possessivePronoun(),
                            target.body.getRandomCock().describe(target), target.possessivePronoun());
        } else {
            return String.format(
                            "You shoot out your tail towards %s unprotected groin, but %s"
                                            + " twists away slightly causing you to just miss %s %s.",
                            target.nameOrPossessivePronoun(), target.pronoun(), target.possessivePronoun(),
                            target.body.getRandomCock().describe(target));
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (modifier == Result.special) {
            return String.format(
                            "%s twists and turns %s tail with renewed vigor,"
                                            + " stealing more of your energy in the process.",
                            getSelf().name(), getSelf().possessivePronoun());
        } else if (modifier == Result.normal) {
            return String.format(
                            "%s grabs %s tail with both hands and aims it at"
                                            + " your groin. The tip opens up like a flower, revealing a hollow"
                                            + " inside shaped suspiciously like a pussy. Leaving you no chance"
                                            + " to ponder this curiosity, the tail suddenly flies at you. The opening"
                                            + ", which does indeed <i>feel</i> like a pussy as well, engulfs your %s"
                                            + " completely. You feel as if you are slowly getting weaker the more it"
                                            + " sucks on you. That is not good.",
                            getSelf().name(), getSelf().possessivePronoun(),
                            target.body.getRandomCock().describe(target));
        } else if (modifier == Result.critical) {
            return String.format(
                            "With your nose between %s asscheeks as it is, you feel some muscles at the base "
                                            + "of %s spine tense up. You aren't sure what's going on, but not long after you"
                                            + " feel your %s being swallowed up in a warm sheath. If %s %s weren't in your face, you'd"
                                            + " think %s were fucking you. Suddenly, the slick canal contracts around your dick, and"
                                            + " you feel some of your strength flowing out of you and into it. That is not good.",
                            getSelf().nameOrPossessivePronoun(), getSelf().possessivePronoun(),
                            target.body.getRandomCock().describe(target), getSelf().possessivePronoun(),
                            user().body.getRandomPussy().describe(getSelf()), getSelf().pronoun());
        } else if (modifier == Result.weak) {
            return String.format(
                            "%s grabs %s tail with both hands and aims it at"
                                            + " your groin. The tip opens up like a flower, revealing a hollow"
                                            + " inside shaped suspiciously like a pussy. That cannot be good, so"
                                            + " you twist your hips just in time to evade the tail as it suddenly"
                                            + " launches forward. Evade may be too strong a term, though, as it"
                                            + " misses your %s but finds your balls instead. %s does not seem"
                                            + " to interested in them, though, and leaves them alone after"
                                            + " massaging them a bit.",
                            getSelf().name(), getSelf().possessivePronoun(),
                            target.body.getRandomCock().describe(target), getSelf().name());
        } else {
            return String.format("%s grabs %s tail with both hands and aims it at"
                            + " your groin. The tip opens up like a flower, revealing a hollow"
                            + " inside shaped suspiciously like a pussy. That cannot be good, so"
                            + " you twist your hips just in time to evade the tail as it suddenly"
                            + " launches forward..", getSelf().name(), getSelf().possessivePronoun());
        }
    }

    private void drain(Combat c, Character target) {
        Attribute toDrain = Global.pickRandom(target.att.entrySet().stream().filter(e -> e.getValue() != 0)
                        .map(e -> e.getKey()).toArray(Attribute[]::new));
        target.add(c, new Abuff(target, toDrain, -power(), 20));
        getSelf().add(c, new Abuff(getSelf(), toDrain, power(), 20));
        target.drain(c, getSelf(), 1 + Global.random(power() * 3));
        target.drainMojo(c, getSelf(), 1 + Global.random(power() * 3));
        target.emote(Emotion.desperate, 5);
        getSelf().emote(Emotion.confident, 5);
    }

    private int power() {
        return (int) (1 + getSelf().get(Attribute.Dark) / 20.0);
    }

}
