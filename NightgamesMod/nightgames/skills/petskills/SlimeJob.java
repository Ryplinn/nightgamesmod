package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Tactics;

public class SlimeJob extends SimpleEnemySkill {
    public SlimeJob() {
        super("Slime Job");
        addTag(SkillTag.pleasure);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return super.requirements(c, user, target);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            int m = (int) (Random.random(10, 16) + Math.sqrt(user.getLevel())/ 2);
            if (target.crotchAvailable() && !c.getStance().penisInserted(target) && target.hasDick()) {
                c.write(user, Formatter.format("{self:SUBJECT} forms into a humanoid shape and grabs {other:name-possessive} dick. "
                                + "A slimy vagina forms around {other:possessive} penis and rubs {other:direct-object} with a slippery pleasure.",
                                    user, target));
                target.body.pleasure(user, user.body.getRandomPussy(), target.body.getRandomCock(), m, c);
                return true;
            } else if (target.hasPussy() && !c.getStance().vaginallyPenetrated(c, target) && target.crotchAvailable() && user.hasDick()) {
                c.write(user, Formatter.format("Two long appendages extend from {self:name-do} and wrap around {other:name-possessive} legs. "
                                + "A third, phallic shaped appendage forms and penetrates {other:possessive} "
                                + "pussy. {self:PRONOUN} stifles a moan as the slimy phallus thrusts in and out of {other:direct-object}.",
                                user, target));
                target.body.pleasure(user, user.body.getRandomCock(), target.body.getRandomPussy(), m, c);
                return true;
            } else if (target.breastsAvailable()) {
                c.write(user, Formatter.format("{self:SUBJECT} grows two long slimy appendages which rises up and tweaks {other:name-possessive} "
                                + "sensitive nipples.",
                                user, target));
                target.body.pleasure(user, user.body.getRandom("tentacles"), target.body.getRandomBreasts(), m, c);
                return true;
            }
        }
        c.write(user, Formatter
                        .format("You see eyes form in {self:name-do} as it watches the fight curiously.", user, target));
        return false;
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
