package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.Skill;
import nightgames.skills.Tactics;

public abstract class SimpleMasterSkill extends Skill {
    private int levelReq;

    SimpleMasterSkill(String name, CharacterType self) {
        this(name, self, 0);
    }
    SimpleMasterSkill(String name, CharacterType self, int levelReq) {
        super(name, self);
        this.levelReq = levelReq;
        addTag(SkillTag.helping);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().isPetOf(target);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return getSelf().getLevel() >= this.levelReq;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
        return "<ERROR>";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character target) {
        return "<ERROR>";
    }

    @Override
    public String describe(Combat c) {
        return "";
    }
    
    @Override
    public Tactics type(Combat c) {
        return Tactics.recovery;
    }
}
