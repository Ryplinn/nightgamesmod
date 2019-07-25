package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.mods.ExtendedTonguedMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.ReverseMount;
import nightgames.stance.SixNine;

public class Blowjob extends Skill {
    public Blowjob(String name) {
        super(name);
        addTag(SkillTag.usesMouth);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
    }

    public Blowjob() {
        this("Blow");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean canUse = !c.getStance().isBeingFaceSatBy(user, target) && user.canAct();
        return ((target.crotchAvailable() && target.hasDick() && c.getStance().oral(user, target)
                        && c.getStance().front(user) && canUse)
                        || (user.canRespond() && isVaginal(c, user, target)));
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        float priority = 0;
        if (c.getStance().penetratedBy(c, user, c.getOpponent(user))) {
            priority += 1.0f;
        }
        if (user.has(Trait.silvertongue)) {
            priority += 1;
        }
        if (user.has(Trait.experttongue)) {
            priority += 1;
        }
        return priority;
    }

    private boolean isVaginal(Combat c, Character user, Character target) {
        return c.getStance().isPartFuckingPartInserted(c, target, target.body.getRandomCock(), user, user.body.getRandomPussy())
                        && !c.getOpponent(user).has(Trait.strapped) && user.body.getRandomPussy().moddedPartCountsAs(user, ExtendedTonguedMod.INSTANCE);
    }

    private boolean isFacesitting(Combat c, Character user, Character target) {
        return c.getStance().isBeingFaceSatBy(user, target);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        if (isVaginal(c, user, c.getOpponent(user))) {
            return 10;
        } else if (c.getStance().isBeingFaceSatBy(user, c.getOpponent(user))) {
            return 0;
        } else {
            return 5;
        }
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = 10 + Random.random(8);
        boolean facesitting = isFacesitting(c, user, target);
        if (user.has(Trait.silvertongue)) {
            m += 4;
        }
        if (isVaginal(c, user, target)) {
            m += 4;
            writeOutput(c, m, Result.intercourse, user, target);
            target.body.pleasure(user, user.body.getRandom("pussy"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
        } else if (facesitting) {
            writeOutput(c, m, Result.reverse, user, target);
            target.body.pleasure(user, user.body.getRandom("mouth"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
            target.buildMojo(c, 10);
        } else if (target.roll(user, accuracy(c, user, target))) {
            writeOutput(c, m, user.has(Trait.silvertongue) ? Result.special : Result.normal, user, target);
            BodyPart mouth = user.body.getRandom("mouth");
            BodyPart cock = target.body.getRandom("cock");
            target.body.pleasure(user, mouth, cock, m, c, new SkillUsage<>(this, user, target));
            if (mouth.isErogenous()) {
                user.body.pleasure(target, cock, mouth, m, c, new SkillUsage<>(this, user, target));
            }

            if (c.getStance() instanceof ReverseMount) {
                c.setStance(new SixNine(user.getType(), target.getType()), user, true);
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 10 && !user.has(Trait.temptress);
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return isVaginal(c, user, target) || isFacesitting(c, user, target) || !c.getStance().reachTop(target)? 200 : 75;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        if (user != null && isVaginal(c, user, c.getStance().getPartner(c, user))) {
            return Tactics.fucking;
        } else {
            return Tactics.pleasure;
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String m;
        if (modifier == Result.miss) {
            m = "You try to take " + target.getName() + "'s penis into your mouth, but she manages to pull away.";
        } else if (target.getArousal().get() < 15) {
            m = "You suck on " + target.getName()
                            + " flaccid little penis until it grows into an intimidating large erection.";
        } else if (target.getArousal().percent() >= 90) {
            m = target.getName()
                            + "'s girl-cock seems ready to burst, so you suck on it strongly and attack the glans with your tongue fiercely.";
        } else if (modifier == Result.special) {
            m = "You put your skilled tongue to good use tormenting and teasing her unnatural member.";
        } else if (modifier == Result.reverse) {
            m = "With " + target.getName() + " sitting over your face, you have no choice but to try to suck her off.";
        } else {
            m = "You feel a bit odd, faced with " + target.getName()
                            + "'s rigid cock, but as you lick and suck on it, you discover the taste is quite palatable. Besides, "
                            + "making " + target.getName() + " squirm and moan in pleasure is well worth it.";
        }
        if (modifier != Result.miss && user.body.getRandom("mouth").isErogenous()) {
            m += "<br/>Unfortunately for you, your sensitive modified mouth pussy sends spasms of pleasure into you too as you mouth fuck "
                            + target.possessiveAdjective() + " cock.";
        }
        return m;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String m = "";
        if (modifier == Result.miss) {
            m += String.format("%s tries to suck %s cock, but %s %s %s hips back to avoid %s.",
                            user.getName(), target.nameOrPossessivePronoun(), target.pronoun(),
                            target.action("pull"), target.possessiveAdjective(), user.directObject());
        } else if (modifier == Result.special) {
            m += String.format("%s soft lips and talented tongue work over %s dick, drawing out"
                            + " dangerously irresistible pleasure with each touch.", 
                            user.nameOrPossessivePronoun(), target.nameOrPossessivePronoun());
        } else if (modifier == Result.intercourse) {
            m += String.format("%s pussy lips suddenly quiver and %s a long sinuous object wrap around %s cock. "
                            + "%s realize she's controlling her vaginal tongue to blow %s with her pussy! "
                            + "Her lower tongue runs up and down %s shaft causing %s to shudder with arousal.",
                            user.nameOrPossessivePronoun(), target.subjectAction("feel"),
                            target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.pronoun()), target.directObject(),
                            target.possessiveAdjective(), target.directObject());
        } else if (modifier == Result.reverse) {
            m += String.format("Faced with %s dick sitting squarely in front of %s face, %s"
                            + " obediently tongues %s cock in defeat.", target.nameOrPossessivePronoun(),
                            user.nameOrPossessivePronoun(), user.pronoun(), target.possessiveAdjective());
        } else if (target.getArousal().get() < 15) {
            m += String.format("%s %s soft penis into %s mouth and sucks on it until it hardens.",
                            user.subjectAction("take"), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective());
        } else if (target.getArousal().percent() >= 90) {
            m += String.format("%s up the precum leaking from %s cock and %s the entire length into %s mouth, sucking relentlessly.",
                            user.subjectAction("lap"), target.nameOrPossessivePronoun(), user.action("take"),
                            user.possessiveAdjective());
        } else {
            int r = Random.random(4);
            if (r == 0) {
                m += String.format("%s %s tongue up the length of %s dick, sending a jolt of pleasure up %s spine. "
                                + "%s slowly wraps %s lips around %s dick and sucks.",
                                user.subjectAction("run"), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                                target.possessiveAdjective(), Formatter.capitalizeFirstLetter(user.pronoun()),
                                user.possessiveAdjective(), target.nameOrPossessivePronoun());
            } else if (r == 1) {
                m += String.format("%s on the head of %s cock while %s hand strokes the shaft.",
                                user.subjectAction("suck"), target.nameOrPossessivePronoun(), user.possessiveAdjective());
            } else if (r == 2) {
                m += String.format("%s %s way down to the base of %s cock and gently sucks on %s balls.",
                                user.subjectAction("lick"), user.possessiveAdjective(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective());
            } else {
                m += String.format("%s %s tongue around the glans of %s penis and teases %s urethra.",
                                user.subjectAction("run"), user.possessiveAdjective(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective());
            }
        }

        if (modifier != Result.miss && user.body.getRandom("mouth").isErogenous()) {
            m += String.format("<br/>Unfortunately for %s, as %s mouth fucks %s cock %s sensitive"
                            + " modifier mouth pussy sends spasms of pleasure into %s as well.", 
                            user.directObject(), user.subject(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(), user.reflectivePronoun());
        }
        return m;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Lick and suck your opponent's dick";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
