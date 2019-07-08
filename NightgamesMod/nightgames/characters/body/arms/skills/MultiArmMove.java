package nightgames.characters.body.arms.skills;

import nightgames.characters.Character;
import nightgames.characters.body.arms.Arm;
import nightgames.combat.Combat;
import nightgames.global.Random;

import java.util.List;
import java.util.Optional;

public abstract class MultiArmMove {
    @SuppressWarnings("unused")
    private final String name;

    MultiArmMove(String name) {
        this.name = name;
    }

    public abstract Optional<List<Arm>> getInvolvedArms(Combat c, Character owner, Character target,
                    List<Arm> available);
    
    public abstract void execute(Combat c, Character owner, Character target, List<Arm> arms);
    
    public boolean shouldExecute() {
        return Random.random(100) < 20;
    }
}
