package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Behind;
import nightgames.stance.Position;
import nightgames.status.Primed;

public class CheapShot extends Skill {

    CheapShot() {
        super("Cheap Shot");
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.temporal) >= 2;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        Position s = c.getStance();
        return s.mobile(user) && !s.prone(user) && !s.prone(target) && !s.behind(user)
                        && user.canAct() && !s.penetrated(c, target) && !s.penetrated(c, user)
                        && Primed.isPrimed(user, 3);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Stop time long enough to get in an unsportsmanlike attack from behind: 3 charges";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.add(c, new Primed(user.getType(), -3));
        writeOutput(c, Result.normal, user, target);
        if (target.human() && Random.random(5) >= 3) {
            c.write(user, user.bbLiner(c, target));
        }
        c.setStance(new Behind(user.getType(), target.getType()), user, true);
        target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random.random(8, 20)));
        user.buildMojo(c, 10);

        user.emote(Emotion.confident, 15);
        user.emote(Emotion.dominant, 15);
        target.emote(Emotion.nervous, 10);
        target.emote(Emotion.angry, 20);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new CheapShot();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (target.mostlyNude()) {
            if (target.hasBalls()) {
                return String.format(
                                "You freeze time briefly, giving you a chance to circle around %s. When time resumes, %s looks around in "
                                                + "confusion, completely unguarded. You capitalize on your advantage by crouching behind %s and delivering a decisive "
                                                + "uppercut to %s dangling balls.",
                                target.getName(), target.pronoun(), target.directObject(), target.possessiveAdjective());
            } else {
                return String.format(
                                "You freeze time briefly, giving you a chance to circle around %s. When time resumes, %s looks around in "
                                                + "confusion, completely unguarded. You capitalize on your advantage by crouching behind %s and delivering a swift, but "
                                                + "painful cunt punt.",
                                target.getName(), target.pronoun(), target.directObject());
            }
        } else {
            return String.format(
                            "You freeze time briefly, giving you a chance to circle around %s. When time resumes, %s looks around in "
                                            + "confusion, completely unguarded. You capitalize on your advantage by crouching behind %s and delivering a decisive "
                                            + "uppercut to the groin.",
                            target.getName(), target.pronoun(), target.directObject());
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (target.mostlyNude()) {
            return String.format(
                            "%s suddenly vanishes right in front of %s eyes. That wasn't just fast, %s completely disappeared! Before "
                                            + "%s can react, %s %s hit from behind with a devastating punch to %s unprotected balls.",
                            user.getName(), target.nameOrPossessivePronoun(), user.pronoun(),
                            target.subject(), target.pronoun(), target.subjectAction("are", "is"), target.possessiveAdjective());
        } else {
            return String.format(
                            "%s suddenly vanishes right in front of %s eyes. That wasn't just fast, %s completely disappeared! %s something "
                                            + "that sounds like 'Za Warudo' before %s suffer a painful groin hit from behind.",
                            user.getName(), target.nameOrPossessivePronoun(), user.pronoun(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("hear")), target.pronoun());
        }
    }

}
