package nightgames.start;

import nightgames.characters.Attribute;
import nightgames.characters.CharacterSex;
import nightgames.characters.TestAngel;
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
    StartConfiguration startConfig;
    NpcConfiguration angelConfig;

    @Before public void setUp() throws Exception {
        Path file = new File("NightgamesTests/nightgames/start/TestStartConfig.json").toPath();
        startConfig = StartConfiguration.parse(JsonUtils.rootJson(file).getAsJsonObject());
        angelConfig = startConfig.findNpcConfig("TestAngel")
                        .orElseThrow(() -> new NoSuchElementException("TestAngel not found in test config."));
    }

    @Test public void testConfigMerge() throws Exception {
        NpcConfiguration mergedConfig = new NpcConfiguration(angelConfig, startConfig.npcCommon);
        assertThat(mergedConfig.type, equalTo("TestAngel"));
        assertThat(mergedConfig.gender, is(Optional.empty()));
        assertThat(mergedConfig.attributes, allOf(IsMapContaining.hasEntry(Attribute.power, 13),
                        IsMapContaining.hasEntry(Attribute.seduction, 20),
                        IsMapContaining.hasEntry(Attribute.cunning, 15),
                        IsMapContaining.hasEntry(Attribute.divinity, 10),
                        IsMapContaining.hasEntry(Attribute.spellcasting, 2)));
        assertThat(mergedConfig.body.flatMap(body -> body.type),
                        equalTo(Optional.of(BodyConfiguration.Archetype.ANGEL)));
        assertThat(mergedConfig.xp.orElse(0), equalTo(50));
        assertThat(mergedConfig.level.orElse(0), equalTo(5));
        assertThat(mergedConfig.money.orElse(0), equalTo(5000));
    }

    @Test public void testNpcCreation() throws Exception {
        TestAngel angel = new TestAngel(Optional.of(angelConfig), Optional.of(startConfig.npcCommon));
        assertThat(angel.character.getType(), equalTo("TestAngel"));
        assertThat(angel.character.att, allOf(Arrays.asList(IsMapContaining.hasEntry(Attribute.power, 13),
                        IsMapContaining.hasEntry(Attribute.seduction, 20),
                        IsMapContaining.hasEntry(Attribute.cunning, 15),
                        IsMapContaining.hasEntry(Attribute.divinity, 10), IsMapContaining.hasEntry(Attribute.spellcasting, 2),
                        IsMapContaining.hasEntry(Attribute.perception, 6),
                        IsMapContaining.hasEntry(Attribute.speed, 5))));
        assertThat(angel.character.xp, equalTo(50));
        assertThat(angel.character.level, equalTo(5));
        assertThat(angel.character.money, equalTo(5000));
    }

    @Test public void testBodyMerge() throws Exception {
        TestAngel angel = new TestAngel(Optional.of(angelConfig), Optional.of(startConfig.npcCommon));

        // Starting stats should match config but breasts should be the same as base Angel if not overwritten in config.
        assertThat(angel.character.get(Attribute.seduction), equalTo(angelConfig.attributes.get(Attribute.seduction)));
        assertThat(angel.character.body.getLargestBreasts(),
                        equalTo(TestAngel.baseTestAngelChar.body.getLargestBreasts()));
        assertEquals(TestAngel.baseTestAngelChar.body.getLargestBreasts(),
                        angel.getCharacter().body.getLargestBreasts());
    }
    
    @Test public void testGenderChange() throws Exception {
        angelConfig.gender = Optional.of(CharacterSex.male);
        TestAngel angel = new TestAngel(Optional.of(angelConfig), Optional.of(startConfig.npcCommon));

        assertFalse(angel.character.body.has("pussy"));
        assertTrue(angel.character.body.has("cock"));
        // Changing gender should not change (e.g.) breast size.
        assertThat(angel.character.body.getLargestBreasts(),
                        equalTo(TestAngel.baseTestAngelChar.body.getLargestBreasts()));
    }

    @Test public void testClothing() throws Exception {
        NpcConfiguration mergedConfig = new NpcConfiguration(angelConfig, startConfig.npcCommon);
        TestAngel angel = new TestAngel(Optional.of(angelConfig), Optional.of(startConfig.npcCommon));
        Clothing[] expectedClothing = ClothingTable.getIDs(mergedConfig.clothing
                        .orElseThrow(() -> new AssertionError("Merged npc clothing config has no")))
                        .toArray(new Clothing[] {});
        assertThat(angel.character.outfit.getEquipped(), hasItems(expectedClothing));
    }
}
