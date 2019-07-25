package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.stance.IncubusEmbrace;
import nightgames.stance.Position;
import nightgames.stance.Stance;
import nightgames.stance.SuccubusEmbrace;
import nightgames.status.Stsflag;
import nightgames.status.TailFucked;
import nightgames.status.TailSucked;

public class Embrace extends Skill {

    public Embrace() {
        super("Embrace", 6);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.SuccubusWarmth);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && validPosition(c.getStance(), c, user, target) && user.body.has("wings");
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 10.f; // objectively better than the positions it's available from
    }

    private boolean validPosition(Position stance, Combat c, Character user, Character target) {
        if (!stance.connected(c) || !stance.dom(user) || stance.anallyPenetratedBy(c, user, target)) {
            return false;
        }
        if (stance.en == Stance.succubusembrace || stance.en == Stance.upsidedownmaledom
                        || stance.en == Stance.upsidedownfemdom
                        || (stance.en == Stance.flying && !stance.penetrated(c, user))) {
            return false;
        }
        if (stance.penetrated(c, target) && stance.en == Stance.doggy || stance.en == Stance.anal) {
            return true;
        }
        return stance.vaginallyPenetrated(c, user) && stance.facing(target, user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Give your opponent a true demon's embrace";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {

        Position pos = c.getStance();
        Position next;
        boolean selfCatches = c.getStance().vaginallyPenetratedBy(c, user, target);

        String trans = transition(c, pos, user, target, selfCatches);

        if (selfCatches) {
            c.write(user,
                            Formatter.format("%s Now properly seated, {self:subject-action:continue|continues}"
                                            + " {self:possessive} bouncing movements while pressing {other:name-possessive}"
                                            + " head to {self:possessive} chest. Meanwhile, {self:possessive}"
                                            + " {self:body-part:wings} wrap around {other:direct-object}, holding"
                                            + " {other:direct-object} firmly in place.", user, target, trans));
            next = new SuccubusEmbrace(user.getType(), target.getType());
        } else if ((c.getStance().en == Stance.anal || c.getStance().en == Stance.doggy)
                        && c.getStance().penetratedBy(c, target, user)) {
            if (target.hasDick()) {
                next = new IncubusEmbrace(user.getType(), target.getType(), () -> {
                    c.write(user, Formatter.format("{self:NAME-POSSESSIVE} {self:body-part:tail}"
                                    + " reaches around and opens up in front of {other:name-possessive}"
                                    + " hard {other:body-part:cock}. In a quick motion, the turgid shaft"
                                    + " is swallowed up completely. The bulbous head at the end of the"
                                    + " tail flexes mightily, creating an intense suction for its"
                                    + " prisoner and drawing out {other:name-possessive} strength.", user, target));
                    return new TailSucked(target.getType(), user.getType(), 2);
                }, Stsflag.tailsucked);
            } else if (c.getStance().anallyPenetrated(c, target) && target.hasPussy()) {
                next = new IncubusEmbrace(user.getType(), target.getType(), () -> {
                    c.write(user, Formatter.format("{self:NAME-POSSESSIVE} prehensile"
                                    + " {self:body-part:tail} snakes around {other:name-possessive}"
                                    + " waist and then downward between {other:possessive} legs."
                                    + " Having quickly found its target and coated it in copious"
                                    + " amounts of lubricants, the tail shoots up {other:name-possessive}"
                                    + " {other:body-part:pussy} in a single, powerful thrust. The undulating"
                                    + " appendage does not stop, though, and keeps on pistoning in and out"
                                    + " at a speed which is leaving {other:name-do} even more breathless"
                                    + " than {other:pronoun} already {other:action:were|was}.", user, target));
                    return new TailFucked(user.getType(), target.getType(), "pussy");
                }, Stsflag.tailfucked);
            } else {
                next = new IncubusEmbrace(user.getType(), target.getType());
            }
            c.write(user, trans);
        } else {
            c.write("<u><b>Error: Unexpected stance for Embrace. Moving on.</b></u>");
            Thread.dumpStack();
            return false;
        }
 
        c.setStance(next, user, true);

        return false;
    }

    private String transition(Combat c, Position pos, Character user, Character target, boolean selfCatches) {
        switch (pos.en) {
            case cowgirl:
                assert selfCatches;
                return Formatter.format(
                                "{self:SUBJECT-ACTION:pull|pulls} {other:name-possessive}"
                                                + " upper body up to {self:possessive} own by {other:possessive}"
                                                + " shoulders. One hand slips behind {other:name-possessive} head and"
                                                + " pulls it towards {self:name-possessive} {self:body-part:breasts},"
                                                + " which are leaking a small bit of milk."
                                                + " <i>\"There, there, {other:name}. Just relax. Have a drink, if"
                                                + " you like.\"</i> All the while, {self:pronoun-action:keep|keeps}"
                                                + " rocking {self:possessive} hips against {other:direct-object}.",
                                user, target);
            case coiled:
                assert selfCatches;
                return Formatter.format(
                                "{self:NAME-POSSESSIVE} arms and legs pull even tighter around"
                                                + " {other:name-do}. Then, quickly and efficiently,"
                                                + " {self:pronoun-action:roll|rolls} %s over, seating"
                                                + " {self:reflective} in {other:possessive} lap. Before"
                                                + " {other:subject-action:have|has} a chance to respond,"
                                                + " {self:subject-action:shove|shoves} {other:possessive} head"
                                                + " into {self:possessive} milky cleavage.",
                                user, target, c.bothDirectObject(target));
            case flying:
                if (pos.penetrated(c, user)) {
                    return Formatter.format(
                                    "A powerful fear seizes {other:name-do} as"
                                                    + " {self:subject} suddenly {self:action:swoop|swoops} down to"
                                                    + " the ground. {self:PRONOUN-ACTION:are|is} skilled enough to"
                                                    + " keep %s from crashing, and instead {self:pronoun-action:deposit|deposits}"
                                                    + " {other:direct-object} safely on the ground, seating {self:reflective}"
                                                    + " in {other:possessive} lap. {self:PRONOUN} then hugs {other:direct-object}"
                                                    + " close, pushing {other:possessive} face into {self:possessive}"
                                                    + " lactating {self:body-part:breasts}.",
                                    user, target, c.bothDirectObject(target));
                } else {
                    return "";
                }
            case doggy:
            case anal:
                assert !selfCatches;
                return Formatter.format("{self:SUBJECT-ACTION:lean} forward and {self:action:grab}"
                                + " {other:subject} %s. {self:PRONOUN} then hoists %s back upright and"
                                + " {self:action:wrap} {self:possessive} wings around {other:name-do}, continuing"
                                + " {self:possessive} thrusts with new vigor.", user, target, target.hasBreasts() ?
                                                  "by {other:possessive} {other:body-part:breasts}"
                                                : "in a tight bear-hug", c.bothDirectObject(target));
            case missionary:
            default:
                Thread.dumpStack();
                return "<b><u>Unplanned transition in Embrace.</u></b>";
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
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
