package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Random;

import java.util.Optional;

public class Neutral extends Position {

    public Neutral(CharacterType top, CharacterType bottom) {
        super(top, bottom, Stance.neutral);
    }

    @Override
    public String describe(Combat c) {
        if (getTop().human()) {
            return "You and " + getBottom().getName() + " circle each other cautiously";
        } else {
            return String.format("%s and %s circle each other cautiously",
                            getTop().subject(), getBottom().subject());
        }
    }

    @Override
    public String image() {
        return "neutral.jpg";
    }

    @Override
    public boolean inserted(Character c) {
        return false;
    }

    @Override
    public boolean mobile(Character c) {
        return true;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return false;
    }

    @Override
    public boolean dom(Character c) {
        return false;
    }

    @Override
    public boolean sub(Character c) {
        return false;
    }

    @Override
    public boolean reachTop(Character c) {
        return true;
    }

    @Override
    public boolean reachBottom(Character c) {
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return false;
    }

    @Override
    public boolean feet(Character c, Character target) {
        return true;
    }

    @Override
    public boolean oral(Character c, Character target) {
        return false;
    }

    @Override
    public boolean behind(Character c) {
        return false;
    }

    @Override
    public Optional<Position> insertRandomDom(Combat c, Character dom) {
        Character other = getPartner(c, dom);
        boolean fuckPossible = dom.hasDick() && other.hasPussy();
        boolean reversePossible = other.hasDick() && dom.hasPussy();
        if (fuckPossible && reversePossible) {
            if (Random.random(2) == 0) {
                return Optional.of(new Standing(dom.getType(), other.getType()));
            } else {
                return Optional.of(new Jumped(dom.getType(), other.getType()));
            }
        } else if (fuckPossible) {
            return Optional.of(new Standing(dom.getType(), other.getType()));
        } else if (reversePossible) {
            return Optional.of(new Jumped(dom.getType(), other.getType()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Position> insert(Combat c, Character pitcher, Character dom) {
        Character catcher = getPartner(c, pitcher);
        Character sub = getPartner(c, pitcher);
        if (pitcher.body.getRandomInsertable() == null || !catcher.hasPussy()) {
            // invalid
            return Optional.empty();
        }
        if (pitcher == dom) {
            // guy is holding girl down, and is the dominant one in the new
            // stance
            return Optional.of(new Standing(pitcher.getType(), catcher.getType()));
        }
        if (pitcher == sub) {
            // guy is holding girl down, and is the submissive one in the new
            // stance
            return Optional.of(new Jumped(catcher.getType(), pitcher.getType()));
        }
        return Optional.empty();
    }

    @Override
    public double pheromoneMod(Character self) {
        return .5;
    }
    
    @Override
    public int distance() {
        return 3;
    }
}
