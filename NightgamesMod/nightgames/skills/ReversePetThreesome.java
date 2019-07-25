package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class ReversePetThreesome extends PetThreesome {
    ReversePetThreesome(String name, int cooldown) {
        super(name, cooldown);
    }

    ReversePetThreesome() {
        this("Reverse Threesome", 0);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target);
    }

    public BodyPart getSelfOrgan(Character fucker, Combat c) {
        return fucker.body.getRandomPussy();
    }

    public BodyPart getTargetOrgan(Character target) {
        if (target.hasDick()) {
            return target.body.getRandomCock();            
        }
        return target.body.getRandomPussy();
    }

}
