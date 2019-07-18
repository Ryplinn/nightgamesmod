package nightgames.start;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterSex;
import nightgames.characters.Growth;
import nightgames.characters.trait.Trait;
import nightgames.global.Flag;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingTable;
import nightgames.items.clothing.OutfitPlan;
import nightgames.json.JsonUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import static nightgames.start.ConfigurationUtils.*;

public abstract class CharacterConfiguration {

    protected String name;
    protected CharacterSex gender;
    protected Map<Attribute, Integer> attributes;
    protected Integer money;
    protected Integer level;
    protected Integer xp;
    protected Collection<Trait> traits;
    protected BodyConfiguration body;
    protected Collection<String> clothing;

    CharacterConfiguration() {

    }

    /**
     * Merges the fields of two CharacterConfigurations into the a new CharacterConfiguration.
     *
     * @param primaryConfig   The primary configuration.
     * @param secondaryConfig The secondary configuration. Field values will be overridden by values in primaryConfig. return
     */
    CharacterConfiguration(CharacterConfiguration primaryConfig, CharacterConfiguration secondaryConfig) {
        name = merge(primaryConfig.name, secondaryConfig.name);
        gender = merge(primaryConfig.gender, secondaryConfig.gender);
        attributes = mergeMaps(primaryConfig.attributes, secondaryConfig.attributes);
        money = merge(primaryConfig.money, secondaryConfig.money);
        level = merge(primaryConfig.level, secondaryConfig.level);
        xp = merge(primaryConfig.xp, secondaryConfig.xp);
        clothing = merge(primaryConfig.clothing, secondaryConfig.clothing);
        traits = mergeCollections(primaryConfig.traits, secondaryConfig.traits);
        body = BodyConfiguration.merge(primaryConfig.body, secondaryConfig.body);
    }

    private Map<Integer, Map<Attribute, Integer>> calculateAttributeLevelPlan(Character base, int desiredLevel, Map<Attribute, Integer> desiredFinalAttributes) {
        Map<Attribute, Integer> deltaAtts = desiredFinalAttributes.keySet()
                        .stream()
                        .collect(Collectors.toMap(Function.identity(), key -> desiredFinalAttributes.get(key) - base.att.getOrDefault(key, 0)));
        Map<Integer, Map<Attribute, Integer>> attributeLevelPlan = new HashMap<>();
        // k this is some terrible code but what it's doing is trying to simulate level ups for a character based on the number of levels
        // it gets and what final attributes it has
        for (int i = base.level + 1; i <= desiredLevel; i++) {
            // calculates how many more attributes it needs to add
            int attsLeftToAdd = deltaAtts.values().stream().mapToInt(Integer::intValue).sum();
            // calculates how many more levels left to distribute points (counting the current level)
            int levelsLeft = desiredLevel - i + 1;
            // calculates how many points to add for this particular level
            int attsToAdd = attsLeftToAdd / levelsLeft;
            Map<Attribute, Integer> attsForLevel = new HashMap<>();
            attributeLevelPlan.put(i, attsForLevel);
            for (int j = 0; j < attsToAdd; j++) {
                // randomly pick an attribute to train out of the ones that needs to be trained.
                List<Attribute> attsToTrain = deltaAtts.entrySet().stream()
                                .filter(entry -> entry.getValue() > 0)
                                .map(Entry::getKey)
                                .collect(Collectors.toList());
                Optional<Attribute> attToTrain = Random.pickRandom(attsToTrain);
                // put it into the level plan.
                attToTrain.ifPresent(att -> {
                    attsForLevel.compute(att, (key, old) -> old == null ? 1 : old + 1);
                    deltaAtts.compute(att, (key, old) -> old == null ? 0 : old - 1);
                });
            }
        }
        return attributeLevelPlan;
    }

    protected final void apply(Character base) {
        if (name != null) {
            base.setName(name);
        }
        if (money != null) {
            base.money = money;
        }
        if (traits != null) {
            base.clearTraits();
            traits.forEach(base::addTraitDontSaveData);
            traits.forEach(trait -> base.getGrowth().addTrait(0, trait));
        }
        if (level != null) {
            Map<Integer, Map<Attribute, Integer>> attributeLevelPlan =
                            calculateAttributeLevelPlan(base, level, attributes);
                System.out.println(attributeLevelPlan);
                while (base.level < level) {
                    base.level += 1;
                    modMetersOnce(base); // multiplication to compensate for missed daytime gains
                    attributeLevelPlan.get(base.level).forEach((a, val) -> {
                        if (val > 0) {
                            base.mod(a, val, true);
                        }
                    });
                    base.getGrowth().addOrRemoveTraits(base);
                }
        }
        if (attributes != null) {
            base.att.putAll(attributes);
        }
        if (xp != null) {
            base.gainXPPure(xp);
        }
        if (clothing != null) {
            List<Clothing> clothes = ClothingTable.getIDs(clothing);
            base.outfitPlan = new OutfitPlan(clothes);
            base.closet = new HashSet<>(clothes);
            base.change();
        }
        if (body != null) {
            body.apply(base.body);
        }
        base.spendXP();
    }

    /**
     * Parses fields common to PlayerConfiguration and NpcConfigurations.
     *
     * @param object The configuration read from the JSON config file.
     */
    void parseCommon(JsonObject object) {
        name = JsonUtils.getOptional(object, "name").map(JsonElement::getAsString).orElse(null);
        gender = JsonUtils.getOptional(object, "gender").map(JsonElement::getAsString).map(String::toLowerCase)
                        .map(CharacterSex::valueOf).orElse(null);
        traits = JsonUtils.getOptionalArray(object, "traits")
                        .map(array -> JsonUtils.collectionFromJson(array, Trait.class)).orElse(null);
        body = JsonUtils.getOptionalObject(object, "body").map(BodyConfiguration::parse).orElse(null);
        clothing = JsonUtils.getOptionalArray(object, "clothing").map(JsonUtils::stringsFromJson).orElse(null);
        money = JsonUtils.getOptional(object, "money").map(JsonElement::getAsInt).orElse(null);
        level = JsonUtils.getOptional(object, "level").map(JsonElement::getAsInt).orElse(null);
        xp = JsonUtils.getOptional(object, "xp").map(JsonElement::getAsInt).orElse(null);
        attributes = JsonUtils.getOptionalObject(object, "attributes")
                        .map(obj -> JsonUtils.mapFromJson(obj, Attribute.class, Integer.class)).orElse(null);
    }

    private static void modMetersOnce(Character character) {
        Growth growth = character.getGrowth();
        boolean hard = Flag.checkFlag(Flag.hardmode);
        character.getStamina().gain(growth.stamina);
        character.getArousal().gain(growth.arousal);
        character.getWillpower().gain(growth.willpower);
        if (hard) {
            character.getStamina().gain(growth.bonusStamina);
            character.getArousal().gain(growth.bonusArousal);
            character.getWillpower().gain(growth.bonusWillpower);
        }
    }
}
