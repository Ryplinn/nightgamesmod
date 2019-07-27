package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

public class PussyGrind extends Skill {

    private PussyGrind(String name, int cooldown) {
        super(name, cooldown);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.fucking);
        addTag(SkillTag.petDisallowed);
    }

    public PussyGrind() {
        this("Pussy Grind", 0);
    }

    public BodyPart getSelfOrgan(Character user) {
        return user.body.getRandomPussy();
    }

    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomPussy();
    }

    private boolean fuckable(Combat c, Character user, Character target) {
        return BodyPart.hasType(c.getStance().getPartsFor(c, user, target), "pussy")
                        && BodyPart.hasType(c.getStance().getPartsFor(c, target, user), "pussy");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target) && c.getStance().mobile(user) && user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        writeOutput(c, Result.normal, user, target);
        int m = 10 + Random.random(10);
        int otherm = 5 + Random.random(6);
        target.body.pleasure(user, selfO, targetO, m, c, new SkillUsage<>(this, user, target));
        user.body.pleasure(target, targetO, selfO, otherm, c, new SkillUsage<>(this, user, target));
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return Formatter.format(
                            "You rock your tangled bodies back and forth, grinding your loins into hers. {other:subject} passionately gasps as the stimulation overwhelms her. "
                                            + "Soon the floor is drenched with the fruits of your combined labor.",
                            user, target);
        }
        return "Bad stuff happened";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return Formatter.format(
                            "{self:SUBJECT} rocks {other:name-possessive} tangled bodies back and forth, grinding {self:possessive}"
                            + " crotch into %s. {other:SUBJECT-ACTION:moan|moans} passionately as the stimulation overwhelms {other:direct-object}. "
                                            + "Soon the floor is drenched with the fruits of %s combined labor.",
                            user, target, target.human() ? "yours" : target.useFemalePronouns() ? "hers" : "his",
                                            c.bothPossessive(target));
        }
        return "Bad stuff happened";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Grinds your pussy against your opponent's.";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
