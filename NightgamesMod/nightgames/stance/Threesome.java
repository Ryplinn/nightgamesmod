package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;

import java.util.List;
import java.util.Optional;

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
    public Optional<Position> checkOngoing(Combat c) {
        if (!c.otherCombatantsContains(getDomSexCharacter())) {
            c.write(getBottom(), Formatter.format("With the disappearance of {self:name-do}, {other:subject-action:manage|manages} to escape.", getDomSexCharacter(), getBottom()));
            return Optional.of(new Neutral(c.p1.getType(), c.p2.getType()));
        }
        return super.checkOngoing(c);
    }

    @Override
    public abstract List<Character> getAllPartners(Combat c, Character self);
}
