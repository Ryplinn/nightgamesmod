package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Flag;
import nightgames.skills.Skill;

public abstract class SimpleEnemySkill extends Skill {
    private int levelReq;
    public SimpleEnemySkill(String name) {
        this(name, 0);
    }
    public SimpleEnemySkill(String name, int levelReq) {
        super(name);
        this.levelReq = levelReq;
    }

    boolean gendersMatch(Character user, Character other) {
        if (other.useFemalePronouns() && user.useFemalePronouns() && Flag.checkFlag(Flag.skipFF)) {
            return false;
        }
        return other.useFemalePronouns() || user.useFemalePronouns() || !Flag.checkFlag(Flag.skipMM);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !user.isPetOf(target);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getLevel() >= levelReq;
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
}
