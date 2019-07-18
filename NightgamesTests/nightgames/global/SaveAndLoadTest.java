package nightgames.global;

import com.google.gson.JsonObject;
import nightgames.characters.*;
import nightgames.characters.Character;
import nightgames.gui.TestGUI;
import nightgames.skills.SkillPool;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Tests for saving and loading game data.
 */
public class SaveAndLoadTest {

    @BeforeClass public static void setUpSaveAndLoadTest() {
        Main.initialize();
        new TestGUI();
    }

    @After public void tearDown() {
        CharacterType.usePool(null);
    }

    @Test public void testLoadAndSave() throws Exception {
        Path savePath = new File("NightGamesTests/nightgames/global/test_save.ngs").toPath();
        GameState gameState = new GameState(SaveFile.load(savePath.toFile()));
        SaveData firstLoadData = new SaveData(gameState);
        Path tempSave = Files.createTempFile("", "");
        SaveFile.save(tempSave.toFile(), firstLoadData);
        gameState = new GameState(SaveFile.load(tempSave.toFile()));
        CharacterType.usePool(gameState.characterPool);
        SaveData reloadedData = new SaveData(gameState);
        assertThat(reloadedData.npcs, equalTo(firstLoadData.npcs));
        for (NPC firstLoadNpc : firstLoadData.npcs) {
            NPC reloadedNpc = reloadedData.npcs.stream().filter(p -> p.equals(firstLoadNpc)).findFirst()
                            .orElseThrow(AssertionError::new);
            assertThat(reloadedNpc, CharacterStatMatcher.statsMatch(firstLoadNpc));
            assertThat(reloadedNpc.status, equalTo(firstLoadNpc.status));
        }
        assertThat(reloadedData.player, equalTo(firstLoadData.player));
        assertThat(reloadedData.player.status, equalTo(firstLoadData.player.status));
        assertThat(new HashSet<>(reloadedData.player.getAddictions()),
                        equalTo(new HashSet<>(firstLoadData.player.getAddictions())));
        assertThat(reloadedData.player, CharacterStatMatcher.statsMatch(firstLoadData.player));
        assertThat(reloadedData, equalTo(firstLoadData));
        Optional<Addiction> magicMilkReloaded = reloadedData.player.getAddiction(AddictionType.MAGIC_MILK, "Cassie");
        Optional<Addiction> magicMilkFirstLoad = firstLoadData.player.getAddiction(AddictionType.MAGIC_MILK, "Cassie");
        assertThat(magicMilkReloaded.isPresent(), is(true));
        assertThat(magicMilkFirstLoad.isPresent(), is(true));
        assertThat(magicMilkReloaded.map(Addiction::getMagnitude).orElse(-1f),
                        equalTo(magicMilkFirstLoad.map(Addiction::getMagnitude).orElse(-2f)));
        assertThat(magicMilkReloaded.map(Addiction::didDaytime).orElse(null),
                        equalTo(magicMilkFirstLoad.map(Addiction::didDaytime).orElse(null)));
    }

    @Test public void testSaveAndLoadAffection() {
        NPC beforeNPC = new NPC("Affectionate");
        SkillPool.learnSkills(beforeNPC);
        Player human = new Player("testPlayer");
        beforeNPC.gainAffection(human, 10);
        JsonObject npcJson = beforeNPC.save();
        NPC afterNPC = new NPC("AffectionateLoad");
        SkillPool.learnSkills(afterNPC);
        afterNPC.load(npcJson);
        assertThat(afterNPC.getAffections(), equalTo(beforeNPC.getAffections()));
    }

    @Test public void testNPCAvailability() throws Exception {
        File saveFile = new File("NightGamesTests/nightgames/global/test_save.ngs");
        SaveData data = SaveFile.load(saveFile);
        for (NPC npc : data.npcs) {
            if (npc.getType().equals(CharacterType.get("Reyka"))) {
                assertThat("Reyka should not be available", npc.available, equalTo(false));
            }
        }
    }

    /**
     * Makes sure older save files are properly updated on load.
     */
    @Test public void testLoadLegacySave() throws Exception {
        Path savePath = new File("NightGamesTests/nightgames/global/test_save_legacy.ngs").toPath();
        GameState gameState = new GameState(SaveFile.load(savePath.toFile()));
        SaveData firstLoadData = new SaveData(gameState);
        Path tempSave = Files.createTempFile("", "");
        SaveFile.save(tempSave.toFile(), firstLoadData);
        gameState = new GameState(SaveFile.load(tempSave.toFile()));
        CharacterType.usePool(gameState.characterPool);
        SaveData reloadedData = new SaveData(gameState);
        assertThat(reloadedData.npcs, equalTo(firstLoadData.npcs));
        for (NPC firstLoadNpc : firstLoadData.npcs) {
            NPC reloadedNpc = reloadedData.npcs.stream().filter(p -> p.equals(firstLoadNpc)).findFirst()
                            .orElseThrow(AssertionError::new);
            assertThat(reloadedNpc, CharacterStatMatcher.statsMatch(firstLoadNpc));
            assertThat(reloadedNpc.status, equalTo(firstLoadNpc.status));
        }
        assertThat(reloadedData.player, equalTo(firstLoadData.player));
        assertThat(reloadedData.player.status, equalTo(firstLoadData.player.status));
        assertThat(reloadedData.player, CharacterStatMatcher.statsMatch(firstLoadData.player));
        assertThat(reloadedData, equalTo(firstLoadData));
        Optional<Addiction> magicMilkReloaded = reloadedData.player.getAddiction(AddictionType.MAGIC_MILK, "Cassie");
        Optional<Addiction> magicMilkFirstLoad = firstLoadData.player.getAddiction(AddictionType.MAGIC_MILK, "Cassie");
        assertThat(magicMilkReloaded.isPresent(), is(true));
        assertThat(magicMilkFirstLoad.isPresent(), is(true));
        assertThat(magicMilkReloaded.map(Addiction::getMagnitude).orElse(-1f),
                        equalTo(magicMilkFirstLoad.map(Addiction::getMagnitude).orElse(-2f)));
        assertThat(magicMilkReloaded.map(Addiction::didDaytime).orElse(null),
                        equalTo(magicMilkFirstLoad.map(Addiction::didDaytime).orElse(null)));
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
