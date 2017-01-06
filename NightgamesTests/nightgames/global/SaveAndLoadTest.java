package nightgames.global;

import com.google.gson.JsonObject;
import nightgames.characters.BlankPersonality;
import nightgames.characters.Character;
import nightgames.characters.Player;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests for saving and loading game data.
 */
public class SaveAndLoadTest {
    private Path savePath = new File("NightGamesTests/nightgames/global/test_save.ngs").toPath();

    @Before public void setUp() throws Exception {
        new TestGlobal();
    }

    @Test public void testLoadAndSave() throws Exception {
        Global.global.load(savePath.toFile());
        SaveData firstLoadData = Global.global.saveData();
        Path tempSave = Files.createTempFile("", "");
        Global.global.save(tempSave.toFile());
        Global.global.load(tempSave.toFile());
        SaveData reloadedData = Global.global.saveData();
        assertThat(reloadedData.players, equalTo(firstLoadData.players));
        for (Character player : firstLoadData.players) {
            Character reloaded = reloadedData.players.stream().filter(p -> p.equals(player)).findFirst()
                            .orElseThrow(AssertionError::new);
            assertThat(reloaded, CharacterStatMatcher.statsMatch(player));
        }
        assertThat(reloadedData, equalTo(firstLoadData));
    }

    @Test public void testSaveAndLoadAffection() throws Exception {
        BlankPersonality beforeNPC = new BlankPersonality("Affectionate", 1);
        Player human = new Player("testPlayer");
        beforeNPC.character.gainAffection(human, 10);
        JsonObject npcJson = beforeNPC.character.save();
        BlankPersonality afterNPC = new BlankPersonality("AffectionateLoad", 1);
        afterNPC.character.load(npcJson);
        assertThat(afterNPC.character.getAffections(), equalTo(beforeNPC.character.getAffections()));
    }

    private static class CharacterStatMatcher extends TypeSafeMatcher<Character> {
        private Character me;

        CharacterStatMatcher(Character me) {
            this.me = me;
        }

        @Override public boolean matchesSafely(Character other) {
            return me.hasSameStats(other);
        }

        @Override public void describeMismatchSafely(Character other, Description description) {
            description.appendText("was").appendValue(other.printStats());
        }

        @Override public void describeTo(Description description) {
            description.appendText(me.printStats());
        }

        @Factory static CharacterStatMatcher statsMatch(Character me) {
            return new CharacterStatMatcher(me);
        }
    }
}
