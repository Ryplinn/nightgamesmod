package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;

/**
 * Test position. Top character is dominant.
 */
public class TestPosition extends Position {
    private int dominance;

    public TestPosition(CharacterType top, CharacterType bottom, Stance stance, int dominance) {
        super(top, bottom, stance);
        this.dominance = dominance;
    }

    @Override public int dominance() {
        return dominance;
    }

    @Override public String describe(Combat c) {
        return null;
    }

    @Override public boolean mobile(Character c) {
        return false;
    }

    @Override public boolean kiss(Character c, Character target) {
        return false;
    }

    @Override public boolean dom(Character c) {
        return c.getType().equals(top);
    }

    @Override public boolean sub(Character c) {
        return c.getType().equals(bottom);
    }

    @Override public boolean reachTop(Character c) {
        return false;
    }

    @Override public boolean reachBottom(Character c) {
        return false;
    }

    @Override public boolean prone(Character c) {
        return false;
    }

    @Override public boolean feet(Character c, Character target) {
        return false;
    }

    @Override public boolean oral(Character c, Character target) {
        return false;
    }

    @Override public boolean behind(Character c) {
        return false;
    }

    @Override public boolean inserted(Character c) {
        return false;
    }

    @Override public String image() {
        return null;
    }

    @Override
    public int distance() {
        return 0;
    }

    @Override
    public void struggle(Combat c, Character struggler) {
        // TODO Auto-generated method stub
        
    }
}
