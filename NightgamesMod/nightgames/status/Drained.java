package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.pet.PetCharacter;

import java.util.Arrays;
import java.util.List;

public class Drained extends AttributeBuff {
    public static void drain(Combat c, Character drainer, Character drained, 
                    Attribute att, int value, int duration, boolean write) {
        if (drainer.has(Trait.WillingSacrifice) && drained.is(Stsflag.charmed)) {
            value *= 1.5;
        }
        if (drainer.has(Trait.Greedy)) {
            duration *= 1.5;
        }
        int realValue = Math.min(drained.getPure(att) - 
                        (Attribute.isBasic(att) ? 3 : 0), value);
        if (realValue > 0) {
            drainer.add(c, new Drained(drainer.getType(), drained.getType(), att, realValue, duration));
            while (drainer instanceof PetCharacter) {
                drainer = ((PetCharacter) drainer).getSelf().owner();
            }
            drained.add(c, new Drained(drained.getType(), drainer.getType(), att, -realValue, duration));
            if (write) {
                if (drainer.has(Trait.WillingSacrifice) && drained.is(Stsflag.charmed)) {
                    Formatter.writeIfCombat(c, drainer, Formatter.format("With {other:name-possessive} mental defences lowered as they are,"
                                    + " {self:subject-action:are|is} able to draw in more of {other:possessive} %s than"
                                    + " normal."
                                    , drainer, drained, att.toString()));
                }
                if (drainer.has(Trait.Greedy)) {
                    Formatter.writeIfCombat(c, drainer, Formatter.format("{self:SUBJECT-ACTION:suck|sucks} {other:name-possessive} %s"
                                    + " deeply into {self:reflective}, holding onto it for longer than usual."
                                    , drainer, drained, att.toString()));
                }
                if (drainer.has(Trait.RaptorMentis)) {
                    Formatter.writeIfCombat(c, drainer, Formatter.format("Additionally, the draining leaves a profound emptiness in its"
                                    + " wake, sapping {other:name-possessive} confidence.", drainer, drained));
                }
            }
            if (drainer.has(Trait.RaptorMentis)) {
                drained.drain(c, drainer, Math.max(5, realValue), Character.MeterType.MOJO);
            }
        } else {
            Formatter.writeIfCombat(c, drainer, Formatter.format("{self:subject-action:try} to drain {other:name-possessive} %s but {self:action:find} that there's nothing left to take.",
                            drainer, drained, att.getDrainedDO()));
        }
    }

    private final CharacterType drainer;

    public Drained(CharacterType affected, CharacterType drainer, Attribute att, int value, int duration) {
        super(affected, att, value, duration);
        this.drainer = drainer;
        unflag(Stsflag.purgable);
        if (value < 0) {
            flag(Stsflag.debuff);
        }
    }

    public Character getDrainer() {
        return drainer.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        int newValue;
        if (replacement != null) {
            newValue = ((Drained)replacement).value;
        } else {
            newValue = this.value;
        }
        if (newValue < 0) {
            return "";
        } else {
            String message;
            List<String> stolenSynonyms = Arrays.asList("stolen", "robbed", "plundered", "hijacked", "drained", "diverted");
            List<String> boostingSynonyms = Arrays.asList("augmenting", "boosting", "bolstering", "reinforcing", "strengthening", "improving");
            if (newValue <= 2) {
                // small
                message = "{self:subject-action:have} %s a bit of {other:name-possessive} %s, slightly %s {self:possessive} %s.";
            } else if (newValue <= 4) {
                // medium
                message = "{self:subject-action:have} %s some of {other:name-possessive} %s, %s {self:possessive} %s.";
            } else {
                // large
                message = "{self:subject-action:have} %s some of {other:name-possessive} %s, greatly %s {self:possessive} %s.";
            }
            return Formatter.format(message, getAffected(), getDrainer(), Random.pickRandomGuaranteed(stolenSynonyms), modded.getDrainedDO(),
                            Random.pickRandomGuaranteed(boostingSynonyms), modded.getDrainerOwnDO());
        }
    }

    @Override
    public String describe(Combat c) {
        String person, adjective, modification;

        if (getAffected().human()) {
            person = "You feel your";
        } else {
            person = getAffected().getName() + "'s";
        }
        if (Math.abs(value) > 5) {
            adjective = "greatly";
        } else {
            adjective = "";
        }
        if (value > 0) {
            modification = "augmented.";
        } else {
            modification = "sapped.";
        }

        return String.format("%s %s is %s %s\n", person, modded, adjective, modification);
    }

    @Override
    public int mod(Attribute a) {
        if (a == modded) {
            return value;
        }
        return 0;
    }

    @Override
    public String getVariant() {
        return "DRAINED:" + drainer + ":" + modded.toString();
    }

    @Override
    public boolean overrides(Status s) {
        return false;
    }

    @Override
    public void replace(Status s) {
        assert s instanceof Drained;
        Drained other = (Drained) s;
        assert other.modded == modded;
        setDuration(Math.max(other.getDuration(), getDuration()));
        value += other.value;
        name = String.format("%s %+d", modded.displayName(), value);
    }

    @Override
    public int escape(Character from) {
        return from != null && from.has(Trait.SpecificSapping) && value < 0 ? Math.max(-10, -value) : 0;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new Drained(newAffected.getType(), drainer, modded, value, getDuration());
    }

    @Override
    public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("modded", modded.name());
        obj.addProperty("value", value);
        obj.addProperty("duration", getDuration());
        return obj;
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return new Drained(null, null, Attribute.valueOf(obj.get("modded")
                                                    .getAsString()),
                        obj.get("value")
                           .getAsInt(),
                        obj.get("duration")
                           .getAsInt());
    }
}
