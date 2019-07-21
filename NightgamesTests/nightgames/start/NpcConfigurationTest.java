package nightgames.start;

import nightgames.characters.*;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingTable;
import nightgames.json.JsonUtils;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 */
public class NpcConfigurationTest {
    private StartConfiguration startConfig;
    private NPCConfiguration angelConfig;

    @Before public void setUp() throws Exception {
        Path file = new File("NightgamesTests/nightgames/start/TestStartConfig.json").toPath();
        startConfig = StartConfiguration.parse(JsonUtils.rootJson(file).getAsJsonObject());
        angelConfig = startConfig.findNpcConfig("TestAngel")
                        .orElseThrow(() -> new NoSuchElementException("TestAngel not found in test config."));
    }

    @Test public void testConfigMerge() {
        NPCConfiguration mergedConfig = new NPCConfiguration(angelConfig, startConfig.npcCommon);
        assertThat(mergedConfig.type, equalTo("TestAngel"));
        assertThat(mergedConfig.gender, is(Optional.empty()));
        assertThat(mergedConfig.attributes, allOf(IsMapContaining.hasEntry(Attribute.power, 13),
                        IsMapContaining.hasEntry(Attribute.seduction, 20),
                        IsMapContaining.hasEntry(Attribute.cunning, 15),
                        IsMapContaining.hasEntry(Attribute.divinity, 10),
                        IsMapContaining.hasEntry(Attribute.spellcasting, 2)));
        assertThat(mergedConfig.body.type,
                        equalTo(Optional.of(BodyConfiguration.Archetype.ANGEL)));
        assertThat(mergedConfig.xp, equalTo(50));
        assertThat(mergedConfig.level, equalTo(5));
        assertThat(mergedConfig.money, equalTo(5000));
    }

    @Test public void testNpcCreation() {
        NPC angel = new NPC(CharacterType.get("TestAngel"), new TestAngel(), startConfig);
        assertThat(angel.getType(), equalTo("TestAngel"));
        assertThat(angel.att, allOf(Arrays.asList(IsMapContaining.hasEntry(Attribute.power, 13),
                        IsMapContaining.hasEntry(Attribute.seduction, 20),
                        IsMapContaining.hasEntry(Attribute.cunning, 15),
                        IsMapContaining.hasEntry(Attribute.divinity, 10), IsMapContaining.hasEntry(Attribute.spellcasting, 2),
                        IsMapContaining.hasEntry(Attribute.perception, 6),
                        IsMapContaining.hasEntry(Attribute.speed, 5))));
        assertThat(angel.xp, equalTo(50));
        assertThat(angel.level, equalTo(5));
        assertThat(angel.money, equalTo(5000));
    }

    @Test public void testBodyMerge() {
        NPC angel = new NPC(CharacterType.get("TestAngel"), new TestAngel(), startConfig);

        // Starting stats should match config but breasts should be the same as base Angel if not overwritten in config.
        assertThat(angel.get(Attribute.seduction), equalTo(angelConfig.attributes.get(Attribute.seduction)));
        assertThat(angel.body.getLargestBreasts(),
                        equalTo(TestAngel.baseTestAngelChar.body.getLargestBreasts()));
        assertEquals(TestAngel.baseTestAngelChar.body.getLargestBreasts(),
                        angel.body.getLargestBreasts());
    }
    
    @Test public void testGenderChange() {
        angelConfig.gender = CharacterSex.male;
        NPC angel = new NPC(CharacterType.get("TestAngel"), new TestAngel(), angelConfig, startConfig.npcCommon);

        assertFalse(angel.body.has("pussy"));
        assertTrue(angel.body.has("cock"));
        // Changing gender should not change (e.g.) breast size.
        assertThat(angel.body.getLargestBreasts(),
                        equalTo(TestAngel.baseTestAngelChar.body.getLargestBreasts()));
    }

    @Test public void testClothing() {
        NPCConfiguration mergedConfig = new NPCConfiguration(angelConfig, startConfig.npcCommon);
        NPC angel = new NPC(CharacterType.get("TestAngel"), new TestAngel(), angelConfig, startConfig.npcCommon);
        Clothing[] expectedClothing = ClothingTable.getIDs(mergedConfig.clothing).toArray(new Clothing[] {});
        assertThat(angel.outfit.getEquipped(), hasItems(expectedClothing));
    }
}
