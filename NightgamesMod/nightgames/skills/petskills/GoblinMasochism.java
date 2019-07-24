package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.Masochistic;
import nightgames.status.Stsflag;

public class GoblinMasochism extends SimpleEnemySkill {
    public GoblinMasochism() {
        super("Goblin Masochism");
        addTag(SkillTag.debuff);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && !target.is(Stsflag.masochism);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.write(user, Formatter.format("{self:SUBJECT} draws a riding crop and hits her own balls with it. She shivers with delight at the pain and both of you can "
                        + "feel an aura of masochism radiate off her.", user, target));
        user.pain(c, user, 10);
        c.p1.add(c, new Masochistic(c.p1.getType()));
        c.p2.add(c, new Masochistic(c.p2.getType()));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new GoblinMasochism();
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public boolean makesContact() {
        return false;
    }
}
