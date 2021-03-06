package nightgames.gui;

import nightgames.characters.Attribute;
import nightgames.characters.trait.Trait;
import nightgames.global.GameState;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

/**
 * Tests involving the CreationGUI.
 */
public class CreationGUITest {
    private TestGUI testGUI;
    @Before public void setUp() {
        testGUI = new TestGUI();
        testGUI.showGameCreation();
    }

    @Test public void testSelectPlayerStats() throws Exception {
        CreationGUI creationGUI = testGUI.creation;
        creationGUI.namefield.setText("TestPlayer");
        creationGUI.StrengthBox.setSelectedItem(Trait.romantic);
        creationGUI.WeaknessBox.setSelectedItem(Trait.insatiable);
        creationGUI.power = 5;
        creationGUI.seduction = 11;
        creationGUI.cunning = 9;
        creationGUI.makeGame(null);
        GameState gameState = testGUI.loadedState.take();
        assertThat(gameState.characterPool.human.att, allOf(hasEntry(Attribute.power, 5), hasEntry(Attribute.seduction, 11),
                        hasEntry(Attribute.cunning, 9)));
        assertThat(gameState.characterPool.human.getTraits(), IsCollectionContaining.hasItems(Trait.romantic, Trait.insatiable));
    }
}
