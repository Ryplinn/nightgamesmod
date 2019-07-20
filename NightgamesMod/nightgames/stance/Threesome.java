package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;

import java.util.List;

/**
 * TODO: Write class-level documentation.
 */
public abstract class Threesome extends Position {
    CharacterType domSexCharacter;

    Threesome(CharacterType domSexCharacter, CharacterType top, CharacterType bottom, Stance stance) {
        super(top, bottom, stance);
        this.domSexCharacter = domSexCharacter;
    }

    @Override public Character getDomSexCharacter() {
        return domSexCharacter.fromPoolGuaranteed();
    }

    // TODO: Review whether this method makes sense
    public void setOtherCombatants(List<? extends Character> others) {
        for (Character other : others) {
            if (other.getType().equals(domSexCharacter)) {
                domSexCharacter = other.getType();
            }
        }
    }

    @Override
    public abstract List<Character> getAllPartners(Combat c, Character self);
}
