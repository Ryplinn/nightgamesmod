package nightgames.modifier.skill;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.modifier.ModifierComponentLoader;
import nightgames.skills.Skill;
import nightgames.skills.SkillPool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EncourageSkillsModifier extends SkillModifier implements ModifierComponentLoader<SkillModifier> {
    private static final String name = "encourage-skills";

    private final Map<Skill, Double> absolutes;
    private final Map<Skill, BiFunction<Character, Combat, Double>> variables;

    EncourageSkillsModifier() {
        absolutes = null;
        variables = null;
    }

    public EncourageSkillsModifier(Skill s, double encouragement) {
        absolutes = Collections.unmodifiableMap(Collections.singletonMap(s, encouragement));
        variables = Collections.emptyMap();
    }

    public EncourageSkillsModifier(Skill s, BiFunction<Character, Combat, Double> encouragementFunc) {
        absolutes = Collections.emptyMap();
        variables = Collections.unmodifiableMap(Collections.singletonMap(s, encouragementFunc));
    }

    public EncourageSkillsModifier(Map<Skill, Double> encs) {
        absolutes = Collections.unmodifiableMap(encs);
        variables = Collections.emptyMap();
    }

    public EncourageSkillsModifier(Map<Skill, Double> absolutes,
                    Map<Skill, BiFunction<Character, Combat, Double>> variables) {
        this.absolutes = Collections.unmodifiableMap(absolutes);
        this.variables = Collections.unmodifiableMap(variables);
    }

    @Override
    public Map<Skill, Double> encouragedSkills() {
        return absolutes;
    }

    @Override
    public double encouragement(Skill skill, Combat c, Character user) {
        return absolutes.getOrDefault(skill, 0.0) + variables.getOrDefault(skill, (ch, com) -> 0.0).apply(user, c);
    }

    // This applies only to npcs anyway
    @Override
    public final boolean playerOnly() {
        return false;
    }

    @Override
    public String name() {
        return name;
    }

    private Stream<Skill> getSkillsFromPool() {
        return SkillPool.skillPool.stream().map(Supplier::get);
    }

    @Override public EncourageSkillsModifier instance(JsonObject object) {
        if (object.has("list")) {
            JsonArray arr = (JsonArray) object.get("list");
            Map<Skill, Double> encs = new HashMap<>();
            for (Object raw : arr) {
                JsonObject jobj = (JsonObject) raw;
                if (!(jobj.has("skill") && jobj.has("weight"))) {
                    throw new IllegalArgumentException("All encouraged skills need a 'skill' and a 'weight'");
                }
                String name = jobj.get("skill").getAsString();
                Skill skill = getSkillsFromPool().filter(s -> s.getName().equals(name)).findAny()
                                .orElseThrow(() -> new IllegalArgumentException("No such skill: " + name));
                double weight = jobj.get("weight").getAsFloat();
                encs.put(skill, weight);
            }
            return new EncourageSkillsModifier(encs);
        } else if (object.has("skill") && object.has("weight")) {
            String name = object.get("skill").getAsString();
            Skill skill = getSkillsFromPool().filter(s -> s.getName().equals(name)).findAny()
                            .orElseThrow(() -> new IllegalArgumentException("No such skill: " + name));
            double weight = object.get("weight").getAsFloat();
            return new EncourageSkillsModifier(skill, weight);
        }
        throw new IllegalArgumentException("'encourage-skills' must have either 'list' or 'skill' and 'weight'");
    }

    @Override
    public String toString() {
        return "Encouraged:" + absolutes.toString();
    }
}
