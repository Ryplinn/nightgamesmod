package nightgames.characters;

import nightgames.skills.damage.DamageType;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests for loading and querying AttributeValues data store.
 */
public class AttributeValuesTest {
    private AttributeValues testValues = AttributeValues.loadAttributeValues(
                    Paths.get("NightGamesTests/nightgames/characters/attribute_values_test.json"));


    @Test public void getOffensivePower() {
        // existing value
        assertThat(testValues.getOffensivePower(Attribute.power, DamageType.physical), equalTo(2.0));
        // unspecified attribute
        assertThat(testValues.getOffensivePower(Attribute.temporal, DamageType.arcane), equalTo(0.0));
        // unspecified damage type
        assertThat(testValues.getOffensivePower(Attribute.medicine, DamageType.divine), equalTo(0.0));
    }

    @Test public void getDefensivePower() {
        // existing value
        assertThat(testValues.getDefensivePower(Attribute.cunning, DamageType.gadgets), equalTo(1.0));
        // unspecified attribute
        assertThat(testValues.getDefensivePower(Attribute.temporal, DamageType.physical), equalTo(0.0));
        // unspecified damage type
        assertThat(testValues.getDefensivePower(Attribute.cunning, DamageType.divine), equalTo(0.0));
    }

    @Test public void getProtectionPower() {
        // existing value
        assertThat(testValues.getProtectionPower(Attribute.slime, Character.MeterType.STAMINA), equalTo(3.0));
        // unspecified attribute
        assertThat(testValues.getProtectionPower(Attribute.temporal, Character.MeterType.AROUSAL), equalTo(0.0));
        // unspecified meter type
        assertThat(testValues.getProtectionPower(Attribute.seduction, Character.MeterType.MOJO), equalTo(0.0));
    }
}
