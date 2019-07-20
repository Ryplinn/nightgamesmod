package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * TODO: Write class-level documentation.
 */
public class NeutraldomSexStance implements DomHelper {
    @Override public float priorityMod(Character self, Position position) {
        return 0;
    }

    @Override public Optional<Position> checkOngoing(Combat c, Position position) {
        return Optional.empty();
    }

    @Override public List<BodyPart> topParts(Position position) {
        if (position.inserted()) {
            throw new UnsupportedOperationException(
                            "Attempted to get topPart in position " + position.getClass().getSimpleName()
                                            + ", but that position does not override the appropriate method.");
        }
        return Collections.emptyList();
    }

    @Override public List<BodyPart> bottomParts(Position position) {
        if (position.inserted()) {
            throw new UnsupportedOperationException(
                            "Attempted to get bottomPart in position " + getClass().getSimpleName()
                                            + ", but that position does not override the appropriate method.");
        }
        return Collections.emptyList();
    }

    @Override public boolean inserted(Character c, Position position) {
        return false;
    }

    @Override public boolean oral(Character c, Character target, Position position) {
        return false;
    }

    @Override public boolean feet(Character c, Character target, Position position) {
        return false;
    }

    @Override public double pheromoneMod(Character self, Position position) {
        return 1;
    }

    @Override public int distance(Position position) {
        return 3;
    }

    @Override public void struggle(Combat c, Character struggler, Position position) {

    }

    @Override public void escape(Combat c, Character escapee, Position position) {

    }
}
