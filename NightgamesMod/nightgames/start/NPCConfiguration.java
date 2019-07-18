package nightgames.start;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nightgames.characters.CharacterSex;
import nightgames.characters.NPC;
import nightgames.json.JsonUtils;

import static nightgames.start.ConfigurationUtils.merge;

public class NPCConfiguration extends CharacterConfiguration {
    // Optional because NpcConfiguration is used for both NPCs and adjustments common to all NPCs
    protected String type;
    private Boolean isStartCharacter;

    private NPCConfiguration() {
        isStartCharacter = null;
    }

    /** Makes a new NpcConfiguration from merging two others.
     * @param primaryConfig Will override field values from secondaryConfig.
     * @param secondaryConfig Field values will be overridden by primaryConfig.
     */
    NPCConfiguration(NPCConfiguration primaryConfig, NPCConfiguration secondaryConfig) {
        super(primaryConfig, secondaryConfig);
        isStartCharacter = merge(primaryConfig.isStartCharacter, secondaryConfig.isStartCharacter);
        type = primaryConfig.type;
    }

    public static NPCConfiguration mergeNPCConfigs(NPCConfiguration primaryConfig,
                    NPCConfiguration secondaryConfig) {
        if (primaryConfig != null) {
            if (secondaryConfig != null) {
                return new NPCConfiguration(primaryConfig, secondaryConfig);
            } else {
                return primaryConfig;
            }
        } else {
            return secondaryConfig;
        }
    }

    public final void apply(NPC base) {
        if (gender != null) {
            CharacterSex sex = gender;
            base.initialGender = sex;
            // If gender is present in config, make genitals conform to it. This will be overridden if config also supplies genitals.
            if (!sex.hasCock()) {
                base.body.removeAll("cock");
            }
            if (!sex.hasPussy()) {
                base.body.removeAll("pussy");
            }
            base.body.makeGenitalOrgans(sex);
        } else {
            base.body.makeGenitalOrgans(base.initialGender);
        }
        super.apply(base);
        if (isStartCharacter != null) {
            base.isStartCharacter = isStartCharacter;
            base.available = isStartCharacter;
        }
    }

    /** Parse fields from the all_npcs section.
     * @param object The configuration from the JSON config file.
     * @return A new NpcConfiguration as specified in the config file.
     */
    static NPCConfiguration parseAllNPCs(JsonObject object) {
        NPCConfiguration config = new NPCConfiguration();
        config.isStartCharacter = JsonUtils.getOptional(object, "start").map(JsonElement::getAsBoolean).orElse(null);
        config.parseCommon(object);
        return config;
    }

    /** Parse a character-specific NPC config.
     * @param object The configuration from the JSON config file.
     * @return A new NpcConfiguration as specified in the config file.
     */
    public static NPCConfiguration parse(JsonObject object) {
        NPCConfiguration config = NPCConfiguration.parseAllNPCs(object);
        config.type = JsonUtils.getOptional(object, "type").map(JsonElement::getAsString)
                        .orElseThrow(() -> new RuntimeException("Tried parsing NPC without a type."));

        return config;
    }
}
