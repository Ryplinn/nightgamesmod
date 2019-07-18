package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

public class ReversePetThreesome extends PetThreesome {
    ReversePetThreesome(String name, CharacterType self, int cooldown) {
        super(name, self, cooldown);
    }

    ReversePetThreesome(CharacterType self) {
        super("Reverse Threesome", self, 0);
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return super.usable(c, target);
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

    @Override
    public Skill copy(Character user) {
        return new ReversePetThreesome(user.getType());
    }
}
