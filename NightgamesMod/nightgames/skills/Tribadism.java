package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.stance.TribadismStance;

public class Tribadism extends Skill {

    public Tribadism(String name, int cooldown) {
        super(name, cooldown);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.fucking);
        addTag(SkillTag.petDisallowed);
    }

    public Tribadism() {
        this("Tribadism", 0);
    }

    public BodyPart getSelfOrgan(Character user) {
        return user.body.getRandomPussy();
    }

    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomPussy();
    }

    private boolean fuckable(Combat c, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        boolean possible = selfO != null && targetO != null;
        boolean stancePossible = false;
        if (possible) {
            stancePossible = true;
            if (selfO.isType("pussy")) {
                stancePossible = !c.getStance().vaginallyPenetrated(c, user);
            }
            if (targetO.isType("pussy")) {
                stancePossible &= !c.getStance().vaginallyPenetrated(c, target);
            }
        }
        stancePossible &= !c.getStance().havingSex(c);
        return possible && stancePossible && user.crotchAvailable() && target.crotchAvailable();
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target) && c.getStance().mobile(user) && !c.getStance().mobile(target)
                        && user.canAct() && c.getStance().en != Stance.trib;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        writeOutput(c, Result.normal, user, target);
        c.setStance(new TribadismStance(user.getType(), target.getType()), user, true);
        int otherm = 10;
        int m = 10;
        target.body.pleasure(user, selfO, targetO, m, c, new SkillUsage<>(this, user, target));
        user.body.pleasure(target, targetO, selfO, otherm, c, new SkillUsage<>(this, user, target));
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Tribadism();
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
                            "You grab {other:name-possessive} legs and push them apart. You then push your hot snatch across her pussy lips and grind down on it.",
                            user, target);
        }
        return "Bad stuff happened";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        if (modifier == Result.normal) {
            return String.format("%s grabs %s leg and slides her crotch against %s."
                            + " She then grinds her %s against %s wet %s.", user.subject(),
                            target.nameOrPossessivePronoun(), target.possessivePronoun(),
                            selfO.describe(user), target.possessiveAdjective(),
                            targetO.describe(user));
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
