package nightgames.modifier;

import com.google.gson.JsonObject;
import nightgames.actions.Locate;
import nightgames.characters.Player;
import nightgames.json.JsonUtils;
import nightgames.modifier.status.StatusModifier;
import nightgames.skills.*;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests for loading custom modifier setups.
 */
public class CustomModifierLoaderTest {
    private JsonObject modJSON;
    private static Player player = new Player("player");


    @BeforeClass public static void setUpClass() {
        SkillPool.buildSkillPool();
    }

    @Before public void setUp() throws Exception {
        Path file = new File("NightgamesTests/nightgames/modifier/test_modifier.json").toPath();
        modJSON = JsonUtils.rootJson(file).getAsJsonObject();
    }

    @Test public void testReadModifier() {
        BaseModifier mod = (BaseModifier) CustomModifierLoader.readModifier(modJSON);
        // TODO: Modifiers constructed via allOf() and combine() only have the top-most attributes directly accessible. Figure out a way to merge attributes.
        assertThat(mod.clothing.allowedLayers(), IsCollectionContaining.hasItem(0));
        assertThat(mod.clothing.forcedItems(), IsCollectionContaining.hasItem("jacket"));
        assertThat(mod.clothing.toString(), equalTo("underwear-only, Forced:[jacket]"));
        assertThat(mod.status, equalTo(StatusModifier.combiner.nullModifier()));
        assertThat(mod.skills.bannedTactics(), IsCollectionContaining.hasItem(Tactics.fucking));
        assertThat(mod.skills.encouragedSkills(), allOf(IsMapContaining.hasEntry(new Blowjob(player.getType()), 1.0),
                        IsMapContaining.hasEntry((Skill) new Kick(player.getType()), -2.0)));
        assertThat(mod.skills.toString(), equalTo("Banned:[fucking], Encouraged:{Blow=1.0, Kick=-2.0}"));
        assertThat(mod.actions.bannedActions(), IsCollectionContaining.hasItem(new Locate()));
        assertThat(mod.custom, equalTo(BaseModifier.EMPTY_CONSUMER));
    }

}
