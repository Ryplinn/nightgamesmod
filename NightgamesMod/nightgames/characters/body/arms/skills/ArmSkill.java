package nightgames.characters.body.arms.skills;

import nightgames.characters.Character;
import nightgames.characters.body.arms.Arm;
import nightgames.combat.Combat;

public abstract class ArmSkill {
    
    private final String name;
    private final int level;
    
    public ArmSkill(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public boolean usable(Combat c, Arm arm, Character owner, Character target) {
        return owner.getLevel() >= level;
    }
    
    public final String getName() {
        return name;
    }
    
    public abstract boolean resolve(Combat c, Arm arm, Character owner, Character target);
    
    protected int bonusChance() {
        return 0;
    }
}
