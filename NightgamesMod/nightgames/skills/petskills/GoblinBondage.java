package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;
import nightgames.status.BondageFetish;
import nightgames.status.Stsflag;

public class GoblinBondage extends SimpleEnemySkill {
    public GoblinBondage() {
        super("Goblin Bondage");
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
        c.write(user, Formatter.format("{self:SUBJECT} pulls the bondage straps tighter around herself. You can see the leather and latex digging into her skin as "
                        + "her bondage fascination begins to affect both of you.", user, target));
        user.pain(c, user, 10);
        c.p1.add(c, new BondageFetish(c.p1.getType()));
        c.p2.add(c, new BondageFetish(c.p2.getType()));
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new GoblinBondage();
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
