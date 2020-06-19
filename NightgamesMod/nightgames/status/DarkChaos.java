package nightgames.status;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionSymptom;
import nightgames.status.addiction.AddictionType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DarkChaos extends Status {

    public DarkChaos(CharacterType affected) {
        super("Dark Chaos", affected);
        flag(Stsflag.debuff);
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        return ""; // explained in withdrawal message
    }

    @Override
    public String describe(Combat c) {
        if (getAffected().human()) {
            return "The blackness coursing through your soul is looking for ways to hinder you.";
        } else {
            return Formatter.format("{self:subject-action:are} visibly distracted, trying to fight the corruption in {self:reflective}.", getAffected(), getAffected());
        }
    }

    @Override
    public void tick(Combat c) {
        if (c == null)
            return;
        float odds = getAffected().getAnyAddiction(AddictionType.CORRUPTION).flatMap(Addiction::activeTracker)
                        .map(AddictionSymptom::getCombatMagnitude).orElse(0f) / 4;
        if (odds > Math.random()) {
            Optional<Effect> e = Effect.pick(c, getAffected());
            if (e.isPresent()) {
                e.get().execute(c, getAffected());
            } else {
                System.err.println("No usable Dark Chaos effects!");
            }
        }
    }

    @Override
    public int mod(Attribute a) {
        return 0;
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
        return 0;
    }

    @Override
    public int escape(Character from) {
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

    @Override
    public int value() {
        return -10;
    }

    @Override
    public Status instance(Character newAffected, Character opponent) {
        return new DarkChaos(newAffected.getType());
    }

     @Override public JsonObject saveToJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", getClass().getSimpleName());
        return obj;
    }

    @Override public Status loadFromJson(JsonObject obj) {
        return new DarkChaos(null);
    }

    private enum Effect {
        HORNY((affected) -> new Horny(affected, 3.f, 5, "Reyka's Corruption"),
                        "The corruption settles into {self:possessive} genitals, inflaming {self:possessive} lusts."),
        HYPER(Hypersensitive::new,
                        "The corruption flows across {self:possessive} skin, leaving it much more sensitive."),
        CHARMED(Charmed::new,
                        "The blackness subverts {self:possessive} mind, making it unthinkable for {self:pronoun} to harm {self:possessive} opponent."),
        SHAMED(Shamed::new, "The blackness plants a deep sense of shame in {self:possessive} mind."),
        FALLING(Falling::new,
                        "The darkness interferes with {self:possessive} balance, sending {self:pronoun} falling to the ground."),
        FLATFOOTED((affected) -> new Flatfooted(affected, 1),
                        "The darkness clouds {self:possessive} mind, distracting {self:direct-object} from the fight."),
        FRENZIED((affected) -> new Frenzied(affected, 3), "The corruption senses what {self:subject-action:are} doing, and is compelling"
                        + " {self:pronoun} to fuck as hard as {self:pronoun} can.");

        private final Function<CharacterType, ? extends Status> effect;
        private final String message;

        Effect(Function<CharacterType, ? extends Status> supplier, String message) {
            this.effect = supplier;
            this.message = message;
        }

        boolean possible(Combat c) {
            if (this == FALLING)
                return c.getStance().en == Stance.neutral;
            return true;
        }

        void execute(Combat c, Character affected) {
            Character partner = c.getStance().getPartner(c, affected);
            if (this == FALLING)
                c.setStance(new StandingOver(c.getOpponent(affected).getType(), affected.getType()));
            else
                affected.addlist.add(effect.apply(affected.getType()));
            c.write(affected, Formatter.format(message, affected, partner));
        }

        static Optional<Effect> pick(Combat c, Character affected) {
            if (c.getStance().havingSex(c, affected))
                return Optional.of(FRENZIED);
            Optional<Effect> picked;
            List<Effect> values = Arrays.asList(values());
            do {
                picked = Random.pickRandom(values);
            } while (picked.isPresent() && picked.get().possible(c));
            return picked;
        }
    }
}
