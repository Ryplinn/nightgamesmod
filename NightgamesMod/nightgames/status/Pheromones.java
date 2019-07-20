package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Character;
import nightgames.characters.*;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.skills.damage.DamageType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Pheromones extends Horny {
    private static List<Attribute> NON_DEBUFFABLE_ATTS = Arrays.asList(
                    Attribute.speed,
                    Attribute.animism,
                    Attribute.nymphomania,
                    Attribute.willpower
    );

    public static Pheromones getWith(Character from, Character target, float magnitude, int duration) {
        return getWith(from, target, magnitude, duration, " pheromones");
    }

    public static Pheromones getWith(Character from, Character target, float magnitude, int duration, String sourceSuffix) {
        return new Pheromones(target.getType(), from.getType(), (float) DamageType.biological.modifyDamage(from, target, magnitude), duration, sourceSuffix);
    }

    private int stacks;
    private CharacterType source;

    public Pheromones(CharacterType affected, CharacterType other, float magnitude, int duration, String sourceSuffix) {
        super(affected, magnitude, duration, sourceSuffix);
        this.source = other;
        this.stacks = 1;
        this.sourceSuffix = getSource().nameOrPossessivePronoun() + sourceSuffix;
        flag(Stsflag.horny);
        flag(Stsflag.pheromones);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
        if (getSource().has(Trait.PiercingOdor)) {
            flag(Stsflag.piercingOdor);
            if (!getAffected().has(Trait.calm)) {
                setMagnitude(getMagnitude() * 1.25f);
            }
        }
    }

    private Character getSource() {
        return source.fromPoolGuaranteed();
    }

    @Override
    public void tick(Combat c) {
        // only use secondary effects for normal pheromones
        if (sourceSuffix.endsWith(" pheromones")) {
            if (getSource().has(Trait.BefuddlingFragrance)) {
                List<Attribute> debuffable = Arrays.stream(Attribute.values())
                                  .filter(att -> !NON_DEBUFFABLE_ATTS.contains(att))
                                  .filter(att -> getAffected().get(att) > 0)
                                  .collect(Collectors.toList());
                Attribute att = Random.pickRandomGuaranteed(debuffable);
                String message = Formatter.format("{other:NAME-POSSESSIVE} intoxicating aroma is messing with {self:name-possessive} head, "
                                + "{self:pronoun-action:feel|seems} %s than before.", getAffected(), getSource(), att.getLowerPhrase());
                if (c != null) {
                    c.write(getAffected(), message);
                } else {
                    GUI.gui.message(message);
                }
                getAffected().add(c, new AttributeBuff(affected, att, -1, 10));
            }
            if (c != null && getSource().has(Trait.FrenzyScent)) {
                if (Random.random(13 - stacks) == 0) {
                    String message;
                    if (getAffected().human()) {
                        message = Formatter.format("The heady obscene scent clinging to you is too much. You can't help it any more, you NEED to fuck something right this second!", getAffected(),
                                        getSource());
                    } else {
                        message = Formatter.format("The heady obscene scent clinging to {self:name-do} is clearly overwhelming {self:direct-object}. "
                                        + "Groaning with animal passion, {self:subject} is descends into a frenzy!", getAffected(),
                                        getSource());
                    }
                    c.write(getAffected(), message);
                    getAffected().add(c, new Frenzied(affected, 3));
                }
            }
        }
        getAffected().arouse(Math.round(getMagnitude()), c, " (" + sourceSuffix + ")");
        getAffected().emote(Emotion.horny, Math.round(getMagnitude()) / 2);
    }

    private int getMaxStacks() {
        if (getSource().has(Trait.ComplexAroma)) {
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
        return new Pheromones(newAffected.getType(), newOther.getType(), getMagnitude(), getDuration(), sourceSuffix);
    }

    @Override  public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("magnitude", getMagnitude());
        obj.addProperty("source", sourceSuffix);
        obj.addProperty("stacks", stacks);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        Pheromones status = new Pheromones(NPC.noneCharacter().getType(), NPC.noneCharacter().getType(), obj.get("magnitude").getAsFloat(), obj.get("duration").getAsInt(), obj.get("source").getAsString());
        status.stacks = obj.get("stacks").getAsInt();
        return status;
    }

}
