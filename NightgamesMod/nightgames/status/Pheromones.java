package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.NPC;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.skills.damage.DamageType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Pheromones extends Horny {
    static List<Attribute> NON_DEBUFFABLE_ATTS = Arrays.asList(
                    Attribute.speed,
                    Attribute.animism,
                    Attribute.nymphomania,
                    Attribute.willpower
    );

    private Character sourceCharacter;

    public static Pheromones getWith(Character from, Character target, float magnitude, int duration) {
        return getWith(from, target, magnitude, duration, " pheromones");
    }

    public static Pheromones getWith(Character from, Character target, float magnitude, int duration, String source) {
        return new Pheromones(target, from, (float) DamageType.biological.modifyDamage(from, target, magnitude), duration, source);
    }

    private int stacks;

    public Pheromones(Character affected, Character other, float magnitude, int duration, String source) {
        super(affected, magnitude, duration, other.nameOrPossessivePronoun() + source);
        this.sourceCharacter = other;
        this.stacks = 1;
        flag(Stsflag.horny);
        flag(Stsflag.pheromones);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
        if (sourceCharacter.has(Trait.PiercingOdor)) {
            flag(Stsflag.piercingOdor);
            if (!affected.has(Trait.calm)) {
                setMagnitude(getMagnitude() * 1.25f);
            }
        }
    }

    @Override
    public void tick(Combat c) {
        // only use secondary effects for normal pheromones
        if (source.endsWith(" pheromones")) {
            if (sourceCharacter.has(Trait.BefuddlingFragrance)) {
                List<Attribute> debuffable = Arrays.stream(Attribute.values())
                                  .filter(att -> !NON_DEBUFFABLE_ATTS.contains(att))
                                  .filter(att -> affected.get(att) > 0)
                                  .collect(Collectors.toList());
                Optional<Attribute> att = Random.pickRandom(debuffable);
                String message = Formatter.format("{other:NAME-POSSESSIVE} intoxicating aroma is messing with {self:name-possessive} head, "
                                + "{self:pronoun-action:feel|seems} %s than before.", affected, sourceCharacter, att.get().getLowerPhrase());
                if (c != null && att.isPresent()) {
                    c.write(affected, message);
                } else {
                    GUI.gui.message(message);
                }
                affected.add(c, new AttributeBuff(affected, att.get(), -1, 10));
            }
            if (c != null && sourceCharacter.has(Trait.FrenzyScent)) {
                if (Random.random(13 - stacks) == 0) {
                    String message;
                    if (affected.human()) {
                        message = Formatter.format("The heady obscene scent clinging to you is too much. You can't help it any more, you NEED to fuck something right this second!", affected, sourceCharacter);
                    } else {
                        message = Formatter.format("The heady obscene scent clinging to {self:name-do} is clearly overwhelming {self:direct-object}. "
                                        + "Groaning with animal passion, {self:subject} is descends into a frenzy!", affected, sourceCharacter);
                    }
                    c.write(affected, message);
                    affected.add(c, new Frenzied(affected, 3));
                }
            }
        }
        affected.arouse(Math.round(getMagnitude()), c, " (" + source + ")");
        affected.emote(Emotion.horny, Math.round(getMagnitude()) / 2);
    }

    private int getMaxStacks() {
        if (sourceCharacter.has(Trait.ComplexAroma)) {
            return 10;
        }
        return 5;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof Pheromones;
        Pheromones other = (Pheromones) s;
        int maxStacks = getMaxStacks();
        if (stacks < maxStacks) {
            // if it's below max stacks, add the arousal over time additively
            setDuration(Math.max(other.getDuration(), getDuration()));
            setMagnitude(getMagnitude() + other.getMagnitude());
            stacks += 1;
        } else {
            // otherwise it will effectively "replace" one of the stacks in magnitude
            setDuration(Math.max(other.getDuration(), getDuration()));
            setMagnitude((getMagnitude() * (maxStacks - 1)+ other.getMagnitude()) / maxStacks);
        }
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Pheromones(newAffected, newOther, getMagnitude(), getDuration(), source);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("magnitude", getMagnitude());
        obj.addProperty("source", source);
        obj.addProperty("stacks", stacks);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        Pheromones status = new Pheromones(null, NPC.noneCharacter(), obj.get("magnitude").getAsFloat(), obj.get("duration").getAsInt(), obj.get("source").getAsString());
        status.stacks = obj.get("stacks").getAsInt();
        return status;
    }

}
