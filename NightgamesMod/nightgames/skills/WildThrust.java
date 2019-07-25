package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

import java.util.Optional;

public class WildThrust extends Thrust {
    public WildThrust() {
        super("Wild Thrust");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.animism) > 1 || user.checkAnyAddiction(AddictionType.BREEDER);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return havingSex(c, user, target);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean effective = super.resolve(c, user, target);
        if (effective && c.getStance().sub(user) && user.has(Trait.Untamed) && Random.random(4) == 0 ) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:fuck|fucks} {other:name-do} with such abandon that it leaves {other:direct-object} "
                            + "momentarily dazed. {self:SUBJECT-ACTION:do|does} not let this chance slip and {self:action:rotate|rotates} {self:possessive} body so that {self:pronoun-action:are|is} on top!", user, target));
            c.setStance(c.getStance().reverse(c, false));
        }
        if (effective && user.has(Trait.breeder) && c.getStance().vaginallyPenetratedBy(c, user, target)
                         && target.human()) {
            c.write(user, Formatter.format("The sheer ferocity of {self:name-possessive} movements"
                            + " fill you with an unnatural desire to sate {self:possessive} thirst with"
                            + " your cum.", user, target));
            target.addict(c, AddictionType.BREEDER, user, Addiction.LOW_INCREASE);
        }
        return effective;
    }

    @Override
    public int[] getDamage(Combat c, Character user, Character target) {
        int[] results = new int[2];

        int m = 5 + Random.random(20) + Math
                        .min(user.getAttribute(Attribute.animism), user.getArousal().getReal() / 30);
        int mt = 5 + Random.random(20);
        mt = Math.max(1, mt);

        results[0] = m;
        results[1] = mt;
        modBreeder(c, user, target, results);

        return results;
    }

    private void modBreeder(Combat c, Character p, Character target, int[] results) {
        Optional<Addiction> addiction = p.getStrongestAddiction(AddictionType.BREEDER);
        if (!addiction.isPresent()) {
            return;
        }

        Addiction add = addiction.get();
        if (add.wasCausedBy(target)) {
            //Increased recoil vs Kat
            results[1] *= 1 + ((float) add.getSeverity().ordinal() / 3.f);
            p.addict(c, AddictionType.BREEDER, target, Addiction.LOW_INCREASE);
        } else {
            //Increased damage vs everyone else
            results[0] *= 1 + ((float) add.getSeverity().ordinal() / 3.f);
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal || modifier == Result.upgrade) {
            return "You wildly pound " + target.getName()
                            + " in the ass with no regard to technique. She whimpers in pleasure and can barely summon the strength to hold herself off the floor.";
        } else if (modifier == Result.reverse) {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:%s {other:name-possessive} cock with no regard to technique, relentlessly driving you both towards orgasm.",
                            user, target, c.getStance().sub(user) ? "grind} against" : "bounce} on");
        } else {
            return "You wildly pound your dick into " + target.getName()
                            + "'s pussy with no regard to technique. Her pleasure filled cries are proof that you're having an effect, but you're feeling it "
                            + "as much as she is.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.anal) {
            return String.format("%s passionately pegs %s in the ass as %s %s and %s to endure the sensation.",
                            user.subject(), target.nameDirectObject(), target.pronoun(),
                            target.action("groan"), target.action("try", "tries"));
        } else if (modifier == Result.upgrade) {
            return String.format("%s pistons wildly into %s while pushing %s shoulders on the ground; %s tits "
                            + "are shaking above %s head while %s strapon stimulates %s %s.", user.subject(),
                            target.nameDirectObject(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(user.possessiveAdjective()), target.possessiveAdjective(),
                            user.possessiveAdjective(), target.possessiveAdjective(),
                            target.hasBalls() ? "prostate" : "insides");
        } else if (modifier == Result.reverse) {
            return String.format("%s frenziedly %s %s cock, relentlessly driving %s both toward orgasm.",
                            user.subject(), target.nameOrPossessivePronoun(), c.bothDirectObject(target), c.getStance().sub(user) ? "grinds against" : "bounces on");
        } else {
            return Formatter.format(
                            "{self:SUBJECT-ACTION:rapidly pound|rapidly pounds} {self:possessive} {self:body-part:cock} into {other:possessive} {other:body-part:pussy}, "
                                            + "relentlessly driving %s both toward orgasm",
                            user, target, c.bothDirectObject(target));
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Fucks opponent without holding back. Extremely random large damage.";
    }

    @Override
    public String getName(Combat c, Character user) {
        if (c.getStance().penetratedBy(c, c.getStance().getPartner(c, user), user)) {
            return "Wild Thrust";
        } else if (c.getStance().sub(user)) {
            return "Wild Grind";
        } else {
            return "Wild Ride";
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
