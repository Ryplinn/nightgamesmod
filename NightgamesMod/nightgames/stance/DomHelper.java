package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;

import java.util.List;
import java.util.Optional;

/**
 * TODO: Write class-level documentation.
 */
public interface DomHelper {
    float priorityMod(Character self, Position position);

    Optional<Position> checkOngoing(Combat c, Position position);

    List<BodyPart> topParts(Position position);

    List<BodyPart> bottomParts(Position position);

    boolean inserted(Character c, Position position);

    boolean oral(Character c, Character target, Position position);

    boolean feet(Character c, Character target, Position position);

    double pheromoneMod(Character self, Position position);

    int distance(Position position);

    void struggle(Combat c, Character struggler, Position position);

    void escape(Combat c, Character escapee, Position position);
}
