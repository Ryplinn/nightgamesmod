package nightgames.global;

import nightgames.characters.Character;
import nightgames.characters.CharacterSex;
import nightgames.characters.CharacterType;
import nightgames.modifier.standard.NoModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Test version of GameState that exposes more handles for easier testing.
 */
public class TestGameState extends GameState {
    public TestGameState() {
        super("TestPlayer", null, new ArrayList<>(), CharacterSex.male, new HashMap<>());
    }

    @Override public void init() {
        GameState.setGameState(this);
        super.init();
        CharacterType.usePool(this.characterPool);
    }

    @Override public void closeGame() {
        super.closeGame();
    }

    public Match makeMatch(List<Character> participants) {
        return new Match(participants, new NoModifier());
    }
}
