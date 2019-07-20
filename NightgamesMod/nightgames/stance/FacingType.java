package nightgames.stance;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.global.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO: Write class-level documentation.
 */
public enum FacingType {
    NONE,
    FACING,
    BEHIND
    ;

    public Optional<Position> insertRandomDom(Position currentStance, Combat c, Character dom) {
        List<Position> possibleResults = new ArrayList<>();
        Character sub = currentStance.getPartner(c, dom);
        switch (this) {
            case FACING:
            case BEHIND:
                if (dom.hasInsertable() && sub.hasPussy()) {
                    Optional<Position> newPos = currentStance.insert(c, dom, dom);
                    newPos.ifPresent(possibleResults::add);
                }
                if (dom.hasPussy() && sub.hasInsertable()) {
                    Optional<Position> newPos = currentStance.insert(c, sub, dom);
                    newPos.ifPresent(possibleResults::add);
                }
                return Random.pickRandom(possibleResults);
            case NONE:
            default:
                return Optional.empty();
        }
    }

    public Optional<Position> insert(Position currentStance, Combat c, Character pitcher, Character dom) {
        Character catcher = currentStance.getPartner(c, pitcher);
        Character sub = currentStance.getPartner(c, dom);
        switch (this) {
            case FACING:
                if (pitcher.body.getRandomInsertable() == null || !catcher.hasPussy()) {
                    // invalid
                    return Optional.empty();
                }
                if (pitcher == dom && pitcher.getType() == currentStance.top) {
                    // guy is holding girl down, and is the dominant one in the new
                    // stance
                    return Optional.of(Missionary.similarInstance(pitcher, catcher));
                }
                if (pitcher == sub && pitcher.getType() == currentStance.top) {
                    // guy is holding girl down, and is the submissive one in the new
                    // stance
                    return Optional.of(new CoiledSex(catcher.getType(), pitcher.getType()));
                }
                if (pitcher == dom && pitcher.getType() == currentStance.bottom) {
                    // girl is holding guy down, and is the submissive one in the new
                    // stance
                    return Optional.of(Missionary.similarInstance(pitcher, catcher));
                }
                if (pitcher == sub && pitcher.getType() == currentStance.bottom) {
                    // girl is holding guy down, and is the dominant one in the new
                    // stance
                    return Optional.of(Cowgirl.similarInstance(catcher, pitcher));
                }
                return Optional.empty();
            case BEHIND:
                if (pitcher.body.getRandomInsertable() == null || !catcher.hasPussy()) {
                    // invalid
                    return Optional.empty();
                }
                if (pitcher == dom && pitcher.getType() == currentStance.top) {
                    // guy is holding girl from behind, and is the dominant one in the
                    // new stance
                    return Optional.of(new Doggy(pitcher.getType(), catcher.getType()));
                }
                if (pitcher == sub && pitcher.getType() == currentStance.top) {
                    // guy is holding girl from behind, and is the submissive one in the
                    // new stance
                    return Optional.of(new ReverseCowgirl(catcher.getType(), pitcher.getType()));
                }
                if (pitcher == dom && pitcher.getType() == currentStance.bottom) {
                    // girl is holding guy from behind, and is the submissive one in the
                    // new stance
                    return Optional.of(new UpsideDownMaledom(pitcher.getType(), catcher.getType()));
                }
                if (pitcher == sub && pitcher.getType() == currentStance.bottom) {
                    // girl is holding guy from behind, and is the dominant one in the
                    // new stance
                    return Optional.of(new ReverseCowgirl(catcher.getType(), pitcher.getType()));
                }
                return Optional.empty();
            case NONE:
            default:
                return Optional.empty();
        }
    }
}
