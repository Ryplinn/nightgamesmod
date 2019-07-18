package nightgames.start;

import nightgames.characters.Attribute;
import nightgames.characters.CharacterSex;
import nightgames.characters.Player;
import nightgames.characters.trait.Trait;
import nightgames.items.clothing.ClothingTable;
import nightgames.json.JsonUtils;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests involving starting player configuration.
 *
 */
public class PlayerConfigurationTest {
    private StartConfiguration startConfig;
    private PlayerConfiguration playerConfig;

    @Before public void setUp() throws Exception {
        Path file = new File("NightgamesTests/nightgames/start/TestStartConfig.json").toPath();
        startConfig = StartConfiguration.parse(JsonUtils.rootJson(file).getAsJsonObject());
        playerConfig = startConfig.player;
    }

    @Test public void testPlayerCreation() {
        Map<Attribute, Integer> chosenAttributes = new HashMap<>();
        List<Trait> pickedTraits = Arrays.asList(Trait.romantic, Trait.insatiable);
        chosenAttributes.put(Attribute.power, 5);
        chosenAttributes.put(Attribute.seduction, 6);
        chosenAttributes.put(Attribute.cunning, 7);
        Player malePlayer = new Player("dude", CharacterSex.male, playerConfig, pickedTraits,
                        chosenAttributes);
        assertEquals(5, malePlayer.level);
        assertEquals(15000, malePlayer.money);
        assertThat(malePlayer.getTraitsPure(), IsCollectionContaining
                        .hasItems(Trait.pussyhandler, Trait.dickhandler, Trait.limbTraining1, Trait.tongueTraining1,
                                        Trait.powerfulhips, Trait.romantic, Trait.insatiable));
        assertThat(malePlayer.outfit.getEquipped(),
                        IsCollectionContaining.hasItems(ClothingTable.getByID("gothshirt").get(), ClothingTable.getByID("gothpants").get()));
    }
}
