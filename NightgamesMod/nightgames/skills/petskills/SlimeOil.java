package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Tactics;
import nightgames.status.Oiled;
import nightgames.status.Stsflag;

public class SlimeOil extends SimpleEnemySkill {
    public SlimeOil() {
        super("Slime Oil", 10);
        addTag(SkillTag.buff);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && !target.is(Stsflag.oiled);
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            c.write(user, Formatter.format("{self:SUBJECT} forms into a shape that's vaguely human and clearly female. "
                                        + "Somehow it manages to look cute and innocent while still being an animated blob of slime. "
                                        + "The slime suddenly pounces on {other:name-do} and wraps itself around {other:direct-object}. "
                                        + "It doesn't seem to be attacking {other:direct-object} as much as giving you a hug, "
                                        + "but it leaves {other:direct-object} covered in slimy residue", user, target));
            target.add(c, new Oiled(target.getType()));
        } else {
            c.write(user, Formatter.format("{self:SUBJECT} launches itself towards {other:name-do}, but {other:SUBJECT-ACTION:sidestep|sidesteps} it handily.",
                            user, target));
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }
}
