package nightgames.characters;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import nightgames.json.JsonUtils;
import nightgames.skills.damage.DamageType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Loads and stores offensive and defensive values associated with attributes.
 */
public final class AttributeValues {
    private static final Path DATA_PATH = Paths.get("NightgamesMod/nightgames/characters/AttributeValues.json");
    private static final AttributeValues VALUES = loadAttributeValues(DATA_PATH);

    public enum PowerType {
        OFFENSE("offense"),
        DEFENSE("defense"),
        PROTECT("protection"),
        ;

        public final String key;

        PowerType(String key) {
            this.key = key;
        }
    }

    private final Map<Attribute, Map<DamageType, Double>> offensivePower;
    private final Map<Attribute, Map<DamageType, Double>> defensivePower;
    private final Map<Attribute, Map<Character.MeterType, Double>> protectionPower;

    private AttributeValues(Map<Attribute, Map<DamageType, Double>> offensivePower,
                    Map<Attribute, Map<DamageType, Double>> defensivePower,
                    Map<Attribute, Map<Character.MeterType, Double>> protectionPower) {
        this.offensivePower = Collections.unmodifiableMap(offensivePower);
        this.defensivePower = Collections.unmodifiableMap(defensivePower);
        this.protectionPower = Collections.unmodifiableMap(protectionPower);
    }

    Double getOffensivePower(Attribute attribute, DamageType damageType) {
        return offensivePower.getOrDefault(attribute, Collections.emptyMap()).getOrDefault(damageType, 0.0);
    }

    Double getDefensivePower(Attribute attribute, DamageType damageType) {
        return defensivePower.getOrDefault(attribute, Collections.emptyMap()).getOrDefault(damageType, 0.0);
    }

    Double getProtectionPower(Attribute attribute, Character.MeterType meterType) {
        return protectionPower.getOrDefault(attribute, Collections.emptyMap()).getOrDefault(meterType, 0.0);
    }

    public Double getPower(PowerType type, Attribute attribute, DamageType damageType) {
        switch (type) {
            case DEFENSE:
                return getDefensivePower(attribute, damageType);
            case OFFENSE:
                return getOffensivePower(attribute, damageType);
            default:
                throw new AttributeValueException("Power type " + type + " does not map to a damage type.");
        }
    }

    public Double getPower(PowerType type, Attribute attribute, Character.MeterType meterType) {
        if (type == PowerType.PROTECT) {
            return getProtectionPower(attribute, meterType);
        }
        throw new AttributeValueException("Power type" + type + " does not map to a meter type.");
    }

    public static Double getOffensivePowerForAttribute(Attribute attribute, DamageType damageType) {
        return VALUES.getOffensivePower(attribute, damageType);
    }

    public static Double getDefensivePowerForAttribute(Attribute attribute, DamageType damageType) {
        return VALUES.getDefensivePower(attribute, damageType);
    }

    public static Double getProtectionPowerForAttribute(Attribute attribute, Character.MeterType meterType) {
        return VALUES.getProtectionPower(attribute, meterType);
    }

    static AttributeValues loadAttributeValues(Path dataPath) {
        Map<Attribute, Map<DamageType, Double>> offensivePowerMap = new EnumMap<>(Attribute.class);
        Map<Attribute, Map<DamageType, Double>> defensivePowerMap = new EnumMap<>(Attribute.class);
        Map<Attribute, Map<Character.MeterType, Double>> protectionPowerMap = new EnumMap<>(Attribute.class);

        JsonObject attributesObj;

        try {
            attributesObj = JsonUtils.rootJson(dataPath).getAsJsonObject();

            Stream.of(Attribute.values()).forEach(attribute -> {
                Optional<JsonObject> attributeObj = JsonUtils.getOptionalObject(attributesObj, attribute.name());
                attributeObj.ifPresent(obj -> loadAttribute(obj, attribute, offensivePowerMap, defensivePowerMap, protectionPowerMap));
            });
            return new AttributeValues(offensivePowerMap, defensivePowerMap, protectionPowerMap);
        } catch (IOException | JsonParseException e) {
            // these are kind of important
            throw new AttributeValueException("Could not load attribute values!");
        }
    }

    private static void loadAttribute(JsonObject attributeObj, Attribute attribute,
                    Map<Attribute, Map<DamageType, Double>> offensive,
                    Map<Attribute, Map<DamageType, Double>> defensive,
                    Map<Attribute, Map<Character.MeterType, Double>> protection) {
        Optional<JsonObject> offObj = JsonUtils.getOptionalObject(attributeObj, PowerType.OFFENSE.key);
        Optional<JsonObject> defObj = JsonUtils.getOptionalObject(attributeObj, PowerType.DEFENSE.key);
        Optional<JsonObject> protObj = JsonUtils.getOptionalObject(attributeObj, PowerType.PROTECT.key);

        Map<DamageType, Double> offMap = offObj.map(AttributeValues::loadDamageTypeMap).orElse(Collections.emptyMap());
        offensive.put(attribute, Collections.unmodifiableMap(offMap));

        Map<DamageType, Double> defMap = defObj.map(AttributeValues::loadDamageTypeMap).orElse(Collections.emptyMap());
        defensive.put(attribute, Collections.unmodifiableMap(defMap));

        Map<Character.MeterType, Double> protMap = protObj.map(AttributeValues::loadMeterTypeMap).orElse(Collections.emptyMap());
        protection.put(attribute, Collections.unmodifiableMap(protMap));
    }

    private static <K extends Enum<K>> Map<K, Double> loadPowerMap(JsonObject object, Class<K> keyClazz) {
        return JsonUtils.enumMapFromJson(object, keyClazz, Double.class);
    }

    private static Map<Character.MeterType, Double> loadMeterTypeMap(JsonObject jsonMap) {
        return loadPowerMap(jsonMap, Character.MeterType.class);
    }

    private static Map<DamageType, Double> loadDamageTypeMap(JsonObject jsonMap) {
        return loadPowerMap(jsonMap, DamageType.class);
    }

    private static class AttributeValueException extends RuntimeException {
        private static final long serialVersionUID = 7855388691632492651L;

        AttributeValueException(String message) {
            super(message);
        }
    }
}
