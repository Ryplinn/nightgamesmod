package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

public abstract class SimpleMasterSkill extends Skill {
    private int levelReq;

    SimpleMasterSkill(String name) {
        this(name, 0);
    }
    SimpleMasterSkill(String name, int levelReq) {
        super(name);
        this.levelReq = levelReq;
        addTag(SkillTag.helping);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.isPetOf(target);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getLevel() >= this.levelReq;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "<ERROR>";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return "<ERROR>";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "";
    }
    
    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.recovery;
    }
}
