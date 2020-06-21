package nightgames.characters;

import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.combat.Combat;
import nightgames.global.Match;
import nightgames.global.TestGameState;
import nightgames.modifier.standard.NoModifier;
import nightgames.status.Pheromones;
import nightgames.status.Status;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * TODO: Write class-level documentation.
 */
public class CharacterTest {
    private Character testCharacter;

    @Before public void setUp() throws Exception {
        testCharacter = new TestCharacter();
    }

    @Test public void spendXPDetectLevelup() {
        assertThat(testCharacter.levelsToGain, equalTo(0));
        testCharacter.xp = 0;
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(0));
        testCharacter.xp += testCharacter.getXPReqToNextLevel();
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(1));
    }
    // test spend xp only when it would leave 0 or more

    @Test public void spendXPOnlyWhenEnough() {
        int XPBefore = testCharacter.getXPReqToNextLevel() - 1;
        testCharacter.xp = XPBefore;
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(0));
        assertThat(testCharacter.xp, equalTo(XPBefore));
    }

    @Test public void spendMultipleLevelupXP() {
        int expectedLevelups = 100;
        testCharacter.xp = 0;
        testCharacter.levelsToGain = 0;
        int startLevel = testCharacter.level;
        for (int i = 0; i < expectedLevelups; i++) {
            testCharacter.xp += testCharacter.getXPReqToNextLevel(startLevel + i);
        }
        testCharacter.spendXP();
        assertThat(testCharacter.levelsToGain, equalTo(100));
        assertThat(testCharacter.xp, equalTo(0));
    }

    @Test public void cloneCharacterInSim() throws Exception {
        TestGameState gameState = new TestGameState();
        gameState.init();
        CharacterPool mainPool = new CharacterPool();
        CharacterType.usePool(mainPool);
        TestCharacter opponent = new TestCharacter("opponent");
        TestCharacter outside = new TestCharacter("outside");
        mainPool.putAll(testCharacter, opponent, outside);
        Match.match = new Match(Arrays.asList(testCharacter, opponent, outside), new NoModifier());
        Status lingering = Pheromones.getWith(outside, testCharacter, 0.5f, 10);
        Area testArea = new Area("TestArea", "TestArea description", Movement.beer);
        Combat mainCombat = new Combat(testCharacter, opponent, testArea);
        testCharacter.add(mainCombat, lingering);
        CharacterPool.SimPool simPool = new CharacterPool.SimPool(mainPool);
        CharacterType.usePool(simPool);
        Combat clonedCombat = mainCombat.clone();
        assertThat(clonedCombat.p1.getType(), equalTo(testCharacter.getType()));
        assertTrue(clonedCombat.p1.status.toString(), clonedCombat.p1.has(lingering));
    }

    @AfterClass public static void afterClass() throws Exception {
        CharacterType.usePool(null);
    }
}
