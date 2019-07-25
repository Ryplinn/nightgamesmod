package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.items.clothing.ClothingSlot;
import nightgames.stance.Position;

public class WingWrapped extends Status {

    private final CharacterType wrapper;
    private final int strength;
    private Position initialPosition;

    public WingWrapped(CharacterType affected, CharacterType wrapper) {
        super("Wing Wrapped", affected);
        this.wrapper = wrapper;
        this.strength = calcStrength(getWrapper());
        flag(Stsflag.wrapped);
    }
    
    private static int calcStrength(Character wrapper) {
        return wrapper.getAttribute(Attribute.power) / 4 + wrapper.getAttribute(Attribute.darkness) / 6;
    }

    private Character getWrapper() {
        return wrapper.fromPoolGuaranteed();
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        String msg = "{other:NAME-POSSESSIVE} powerful {other:body-part:wings} are holding"
                        + " {self:name-do} in place";
        if (getWrapper().has(Trait.VampireWings) && getAffected().outfit.slotEmpty(ClothingSlot.top)) {
            if (getWrapper().human()) {
                msg += ", and they are feeding you {self:possessive} power";
            } else {
                msg += ", and every bit of {self:possessive} skin they touch seems to go numb with weakness";
            }
        }
        return Formatter.format(msg + ".", getAffected(), getWrapper());
    }

    @Override
    public String describe(Combat c) {
        String msg = "{self:SUBJECT-ACTION:are|is} held tightly by {other:name-possessive} {other:body-part:wings}";
        if (getWrapper().has(Trait.VampireWings) && getAffected().outfit.slotEmpty(ClothingSlot.top)) {
            if (getWrapper().human()) {
                msg += ", and they are feeding you {self:possessive} power";
            } else {
                msg += ", and every bit of {self:possessive} skin they touch seems to go numb with weakness";
            }
        }
        return Formatter.format(msg + ".", getAffected(), getWrapper());
    }

    @Override
    public int mod(Attribute a) {
        if (a == Attribute.speed) {
            return -strength/4;
        }
        return 0;
    }

    @Override
    public void tick(Combat c) {
        if (initialPosition == null) {
            initialPosition = c.getStance();
        } else if (!c.getStance().equals(initialPosition) && !canPersist(c)) {
            c.write(getWrapper(), Formatter.format("Lacking sufficient purchase to keep"
                            + " {self:name-do} in check any longer, {other:name-possessive}"
                            + " {other:body-part:wings} return to their regular place behind"
                            + " {other:possessive} back.", getAffected(), getWrapper()));
            getAffected().removelist.add(this);
            return;
        }
        if (!getWrapper().body.has("wings")) {
            c.write(Formatter.format("Now that {other:name-possessive} wings are gone,"
                            + " they can no longer confine {self:name-do}.", getAffected(), getWrapper()));
            getAffected().removelist.add(this);
        } else if (getWrapper().has(Trait.VampireWings) && getAffected().outfit.slotEmpty(ClothingSlot.top)) {
            if (getAffected().getAttribute(Attribute.power) < 6) {
                c.write(getWrapper(), Formatter.format("{other:NAME-POSSESSIVE} {other:body-part:wings}, pressed"
                                + " against {self:name-possessive} bare skin, try to reel in"
                                + " {self:possessive} power, but they fail to draw on what little"
                                + " remains within {self:direct-object}.", getAffected(), getWrapper()));
            } else {
                c.write(getWrapper(), Formatter.format("{other:NAME-POSSESSIVE} {other:body-part:wings}, pressed"
                                + " against {self:name-possessive} bare skin, leech {self:possessive}"
                                + " power from {self:possessive} body, letting it flow back into"
                                + " {other:direct-object}.", getAffected(), getWrapper()));
                Drained.drain(c, getWrapper(), getAffected(), Attribute.power, 3, 20, true);
            }
        }
    }
    
    private boolean canPersist(Combat c) {
        return c.getStance().havingSex(c) || c.getStance().distance() < 2;
    }

    @Override
    public int regen(Combat c) {
        return 0;
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
        return -strength;
    }

    @Override
    public int escape(Character from) {
        return -strength;
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
        return -strength;
    }

    @Override
    public int value() {
        return -strength/5;
    }

    @Override
    public Status instance(Character newAffected, Character newOther) {
        return new WingWrapped(newAffected.getType(), newOther.getType());
    }

    @Override
    public JsonObject saveToJson() {
        return null;
    }

    @Override
    public Status loadFromJson(JsonObject obj) {
        return null;
    }

}
