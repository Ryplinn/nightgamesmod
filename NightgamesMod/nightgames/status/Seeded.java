package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.GenericBodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;

public class Seeded extends Status implements InsertedStatus {
    private String target;
    private CharacterType other;
    private static final BodyPart SEED_PART = new GenericBodyPart("seed", 0.0, 1.0, 0.0, "seed", "a ");
    private double time;
    private int stage;

    public Seeded(CharacterType affected, CharacterType other, String hole) {
        super("seeded", affected);
        this.target = hole;
        this.other = other;
        this.stage = 0;
        this.time = 0;
        flag(Stsflag.seeded);
        flag(Stsflag.debuff);
        flag(Stsflag.purgable);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        BodyPart hole = getAffected().body.getRandom(target);
        if (hole == null) {
            return "";
        }
        return Formatter.capitalizeFirstLetter(String.format("%s planted a seed in %s %s\n",
                        getOther().subjectAction("have", "has"),
                        getAffected().nameOrPossessivePronoun(), hole.describe(getAffected())));
    }

    @Override
    public String describe(Combat c) {
        BodyPart hole = getAffected().body.getRandom(target);
        if (getAffected().human()) {
            if (stage > 4) {
                return Formatter.capitalizeFirstLetter(
                                String.format("A large white lilly grows from your %s\n", hole.describe(getAffected())));
            } else if (stage > 3) {
                return Formatter.capitalizeFirstLetter(
                                String.format("A small green bud peeks out from your %s\n", hole.describe(getAffected())));
            }
            return Formatter.capitalizeFirstLetter(
                            String.format("A lemon-sized seed is lodged firmly in your %s\n", hole.describe(getAffected())));
        } else {
            if (stage > 4) {
                return Formatter.capitalizeFirstLetter(String.format(
                                "A large white lilly grows from " + getAffected().possessiveAdjective() + " %s\n",
                                hole.describe(getAffected())));
            } else if (stage > 3) {
                return Formatter.capitalizeFirstLetter(String.format(
                                "A small green bud peeks out from " + getAffected().possessiveAdjective() + " %s\n",
                                hole.describe(getAffected())));
            }
            return Formatter.capitalizeFirstLetter(String.format(
                            "A lemon-sized seed is lodged firmly in " + getAffected().possessiveAdjective() + " %s\n",
                            hole.describe(getAffected())));
        }
    }

    @Override
    public float fitnessModifier() {
        return -10;
    }

    @Override
    public int mod(Attribute a) {
        return 0;
    }

    @Override
    public void tick(Combat c) {
        BodyPart hole = getAffected().body.getRandom(target);
        GenericBodyPart seed = new GenericBodyPart("seedling", 1.0, 1.0, 1.0, "seedling", "a");
        if (hole == null) {
            getAffected().removelist.add(this);
            return;
        }

        if (time >= 3) {
            if (stage < 3) {
                stage = 3;
                if (!c.shouldAutoresolve())
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("{other:name-possessive} seedling has finally flowered. A brilliant white lilly now covers {self:name-possessive} %s, displaying {self:possessive} verdant submission for everyone to see. "
                                                    + "While the little seedling has finally stopped sapping your vitality, the now-matured root network has somehow integrated with your nervous system and bloodsteam. As pulses of chemical and electrical obedience wrack {self:possessive} body, "
                                                    + "{self:subject-action:know|knows} that {self:pronoun} {self:action:have|has} lost this fight.",
                                    getAffected(), getOther(), hole.describe(getAffected()), hole.describe(getAffected())));
            }
            if (!c.shouldAutoresolve())
                GUI.gui.message(c, getAffected(),
                                Formatter.format("The seedling churns against {self:possessive} inner walls, while sending a chemical cocktail of aphrodisiacs and narcotics directly into {self:possessive} bloodstream. "
                                                + "{self:possessive} mind blanks out as every thought is replaced with a feral need to mate.",
                                getAffected(), getOther(), hole.describe(getAffected())));
            getAffected().heal(c, 100, " (Seedling)");
            getAffected().arouse(Math.max(Random.random(50, 100), getAffected().getArousal().max() / 4), c,
                            getOther().nameOrPossessivePronoun() + " seedling");
            getAffected().body.pleasure(getOther(), seed, hole, Random.random(10, 20) + getOther().get(Attribute.bio) / 2, c);
            getAffected().add(c, new Frenzied(other, 1000));
        } else if (time >= 2) {
            if (stage < 2) {
                stage = 2;
                if (!c.shouldAutoresolve())
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("Having drained enough of {self:name-possessive} essence, the seed shows yet more changes. "
                                                    + "The roots growth thicker and more active, now constantly grinding against {self:possessive} walls. "
                                                    + "On the other side, a small green bud has poked its head out from inside {self:possessive} %s. "
                                                    + "{self:SUBJECT-ACTION:worry|worries} about its implications, but the constant piston motion from your %s is making it hard to concentrate.",
                                    getAffected(), getOther(), hole.describe(getAffected()), hole.describe(getAffected())));
            }
            if (!c.shouldAutoresolve())
                GUI.gui.message(c, getAffected(),
                                Formatter.format("The thick tuber-like roots inside {self:direct-object} constantly shift and scrape against {self:possessive} %s, leaving {self:direct-object} both horny and lenthargic at the same time.",
                                                getAffected(), getOther(), hole.describe(getAffected())));
            getAffected().drain(c, getOther(), Random.random(5, 11), Character.MeterType.STAMINA, Character.MeterType.MOJO, 1.0f);
            getAffected().body.pleasure(getOther(), seed, hole, Random.random(10, 20) + getOther().get(Attribute.bio) / 2, c);
        } else if (time >= 1) {
            if (stage < 1) {
                stage = 1;
                if (!c.shouldAutoresolve())
                    GUI.gui.message(c, getAffected(),
                                    Formatter.format("With a quiet rumble, the seed burried inside {self:name-possessive} %s sprouts thin spindly roots that reach into {self:possessive} innards.",
                                                    getAffected(), getOther(), hole.describe(getAffected())));
            }
            if (!c.shouldAutoresolve())
                GUI.gui.message(c, getAffected(),
                                Formatter.format("{self:SUBJECT-ACTION:feel|feels} slow as the thin threadlike roots latch onto your inner walls and seem to leech your vigor.",
                                                getAffected(), getOther(), hole.describe(getAffected())));
            getAffected().drain(c, getOther(), Random.random(2, 6), Character.MeterType.STAMINA, Character.MeterType.MOJO, 1.0f);
        } else {
            if (!c.shouldAutoresolve())
                GUI.gui.message(c, getAffected(), Formatter.format("The seed sits uncomfortably in {self:possessive} %s.",
                                getAffected(), getOther(), hole.describe(getAffected())));
            getAffected().pain(c, getOther(), 1, false, false);
        }

        getAffected().emote(Emotion.desperate, 10);
        getAffected().emote(Emotion.nervous, 10);
        time += .25;
    }

    @Override
    public int damage(Combat c, int x) {
        return 0;
    }

    @Override
    public double pleasure(Combat c, BodyPart withPart, BodyPart targetPart, double x) {
        return 0;
    }

    @Override
    public int weakened(Combat c, int x) {
        return 0;
    }

    @Override
    public int tempted(Combat c, int x) {
        return 0;
    }

    @Override
    public int evade() {
        return -5;
    }

    @Override
    public int escape() {
        return 0;
    }

    @Override
    public int gainmojo(int x) {
        return 0;
    }

    @Override
    public int spendmojo(int x) {
        return 0;
    }

    @Override
    public int counter() {
        return 0;
    }

    public String toString() {
        return "Seeded";
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new Seeded(newAffected.getType(), newOther.getType(), target);
    }

     public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        obj.addProperty("target", target);
        return obj;
    }

    public Status loadFromJson(JsonObject obj) {
        return new Seeded(null, null, obj.get("target").getAsString());
    }

    @Override
    public int regen(Combat c) {
        return 0;
    }

    @Override
    public BodyPart getHolePart() {
        return getAffected().body.getRandom(target);
    }

    @Override
    public Character getReceiver() {
        return getAffected();
    }

    @Override
    public BodyPart getStickPart() {
        return SEED_PART;
    }

    @Override
    public Character getPitcher() {
        return getOther();
    }

    public Character getOther() {
        return other.fromPoolGuaranteed();
    }
}
