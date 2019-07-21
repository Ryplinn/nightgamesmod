package nightgames.status.addiction;

import com.google.gson.JsonObject;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Anal;
import nightgames.stance.AnalCowgirl;
import nightgames.stance.Position;
import nightgames.status.Enthralled;
import nightgames.status.Status;
import nightgames.status.Stsflag;

import java.util.Optional;

public class MindControl extends Addiction {

    public MindControl(CharacterType afflicted, CharacterType cause, float magnitude) {
        super("Mind Control", afflicted, cause, magnitude);
        getAfflicted().add(Trait.mindcontrolresistance);
    }

    public MindControl(CharacterType afflicted, CharacterType cause) {
        this(afflicted, cause, .01f);
    }

    private class MindControlTrackerSymptom extends AddictionSymptom {
        MindControlTrackerSymptom(Addiction source, float initialMagnitude) {
            super(afflicted, "Cybernetic Compulsion", source, initialMagnitude);
        }

        @Override
        public void tick(Combat c) {
            super.tick(c);
            if (c != null && !getAffected().is(Stsflag.enthralled) && c.getOpponent(getAffected()).equals(source.getCause())
                            && Random.randomdouble() < magnitude / 3) {

                getAffected().addlist.add(new Enthralled(affected, cause, 3));
                Formatter.writeIfCombat(c, getCause(), getCause().getName()
                                + "'s constant urging overcomes your defences, washing away all of your resistance.");
            }
        }
    }

    @Override public Optional<Status> withdrawalEffects() {
        return Optional.of(new MindControlWithdrawal(afflicted));
    }

    @Override public String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                return getCause().getName() + " has you completely in " + getCause().directObject() + " grasp. Your body moves "
                        + "automatically to obey " + getCause().directObject() + " commands, now.";
            case LOW:
                return "You feel a tug on your mind every time " + getCause().getName() + " speaks, pushing you to do as "
                        + getCause().pronoun() + " says.";
            case MED:
                return "You find your body moving to " + getCause().getName() + "'s words without any input from your mind.";
            case NONE:
            default:
                return ""; // hide
        }
    }

    @Override public String describeDecrease() {
        switch (getSeverity()) {
            case LOW:
                return getCause().getName() + "'s control is weakening, and only " + getCause().directObject() + " strongest commands"
                        + " have a noticeable effect.";
            case MED:
                return "You feel as if " + getCause().getName() + "'s words do not bury themselves as deeply into your psyche as before."
                        + " Can you resist " + getCause().directObject() + "?";
            case NONE:
                return "At last that invisible string tying you to " + getCause().getName() + " snaps, and you are back in control"
                        + " of your mind.";
            case HIGH:
            default:
                return ""; // hide
        }
    }

    @Override public String describeWithdrawal() {
        switch (getSeverity()) {
            case HIGH:
                return "<b>You are now constantly fighting your own body to keep from doing " + getCause().getName() + "'s will.</b>";
            case LOW:
                return "<b>Your body tries to steer you towards " + getCause().getName() + " all the time, and it's taking"
                        + " serious effort to resist.</b>";
            case MED:
                return "<b>Keeping your body in line and away from " + getCause().getName() + " is getting really difficult know,"
                        + " and it's a severe strain on your stamina.</b>";
            case NONE:
            default:
                return "";
        }
    }

    @Override public String describeCombatIncrease() {
        return getCause().getName() + "'s words weigh increasingly heavily on you, and it's getting harder to resist.";
    }

    @Override public String describeCombatDecrease() {
        return "Doing " + getCause().getName() + "'s bidding relieves some of the pressure in your mind.";
    }

    @Override
    public String informantsOverview() {
        return "Oh, that is just nasty. You've got to hand it to " + getCause()
                        .directObject() + ", though, " + getCause().pronoun()
                + " got you good. It looks like " + getCause().directObject() + " control somehow bypasses your mind and goes"
                + " straight to your motor functions. That's a special kind of mean, because you'll be entirely"
                + " conscious for the whole thing, not turned into some kind of willing slave. There's"
                + " two ways you can go about this: You can do what " + getCause().pronoun() + " wants you to do, but on your"
                + " terms, or you can try to defy " + getCause().directObject() + " as long as you can and beat "
                + getCause().directObject() + " quickly. If you play along, by laying down or whacking off or something, then"
                + " that will obviously be bad for you but it would also mean you stay more or less in control of it all."
                + " If you fight " + getCause().directObject() + " control, you'll be able to function normally for a while,"
                + " but you will eventually break. When you do, " + getCause().pronoun() + "'ll have total control until you"
                + " recover, which would be far worse. Resisting " + getCause().directObject() + " commands will take"
                + " some serious effort, so it would probably leave you quite tired. So my advice is: don't cum inside"
                + " of " + getCause().directObject() + " again while " + getCause().pronoun() + " can look you in the eyes. It's that simple.";
    }

    @Override public void removeImmediately() {
        super.removeImmediately();
        // If this is the last Mind Control addiction, remove the resistance it grants.
        if (getAfflicted().getAllAddictions(AddictionType.MIND_CONTROL).count() > 1) {
            getAfflicted().remove(Trait.mindcontrolresistance);
        }
    }

    @Override
    public String describeMorning() {
        return "Your hand shoots to your hardening dick as soon as you wake up. You have know idea how,"
                + " but you somehow know it's what " + getCause().getName() + " wants you to do, and your body is responding"
                + " accordingly. You force your hand to your side and awkwardly get dressed. Whenever you're"
                + " not paying attention, it shoots back and rubs your crotch again, though. Perhaps you"
                + " can persuade " + getCause().getName() + " to go a little easier on you? Then again, maybe not.";
    }

    @Override public AddictionSymptom createTrackingSymptom(float initialCombatMagnitude) {
        return new MindControlTrackerSymptom(this, initialCombatMagnitude);
    }

    @Override
    public AddictionType getType() {
        return AddictionType.MIND_CONTROL;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (inWithdrawal) {
            return "There " + getCause().pronoun() + " is! " + getCause().getName() + " does not look pleased after you haven't visited "
                    + getCause().directObject() + " all day.";
        }
        return "Your breathing accelerates when you see " + getCause().getName() + "; you know what power " + getCause()
                        .pronoun()
                + " has over you...";
    }

    @Override
    public String describe(Combat c, Severity severity) {
        switch (severity) {
            case HIGH:
                return "Every word " + getCause().getName() + " speaks rings of truth to you, even though " + getCause()
                                .pronoun() + "'s"
                        + " telling you to submit to " + getCause().directObject() + ". Your body trembles, and you will soon"
                        + " be forced to obey.";
            case LOW:
                return getCause().getName() + " keeps saying things for you to do, and you don't know how"
                                + " long you'll be able to resist " + getCause().directObject() + ".";
            case MED:
                return getCause().getName() + "'s words are starting to have a greater pull on you. You won't hold out much longer.";
            case NONE:
            default:
                return "";

        }
    }

    public static class Result {
        private boolean succeeded;
        private String description;

        public Result(Character affected, Character controller, Position pos) {
            if (affected.is(Stsflag.blinded)) {
                succeeded = false;
                description = "Since you can't see, you are protected from " + controller.getName() + "'s controlling gaze.";
            } else
                switch (pos.en) {
                    case cowgirl:
                        succeeded = true;
                        description = "You turn your head away as you feel your orgasm coming on, wary of " + controller.getName() + "'s"
                                        + " hypnotic eyes. " + Formatter.capitalizeFirstLetter(controller.pronoun()) + "'s"
                                        + " not having it, though. " + controller.pronoun() + " grabs your head"
                                        + " and forces your eyelids open with " + controller.directObject()+ " thumbs. ";
                        break;
                    case anal:
                        if (pos instanceof AnalCowgirl) {
                            succeeded = true;
                            description = "You turn your head away as you feel your orgasm coming on, wary of " + controller.getName() + "'s"
                                            + " hypnotic eyes. " + Formatter.capitalizeFirstLetter(controller.pronoun()) + "'s"
                                            + " not having it, though. " + controller.pronoun() + " grabs your head"
                                            + " and forces your eyelids open with " + controller.directObject() + " thumbs. ";
                            break;
                        } else if (pos instanceof Anal) {
                            succeeded = false;
                            description = "Since you're not facing " + controller.getName() + ", " + controller.directObject()
                                            + " hypnotic eyes cannot affect you.";
                            break;
                        }
                        // Fall-through intentional -- AnalProne
                    case mount:
                    case missionary:
                    case flying:
                        succeeded = true;
                        if (pos.dom(controller)) {
                            description = "You turn your head away as you feel your orgasm coming on, wary of " + controller.getName() + "'s"
                                            + " hypnotic eyes. " + Formatter.capitalizeFirstLetter(controller.pronoun()) + "'s"
                                            + " not having it, though. " + controller.pronoun() + " twists your head back"
                                            + " and forces your eyelids open with " + controller.directObject() + " thumbs. ";
                        } else {
                            description = "At the moment of your orgasm, " + controller.getName() + " pulls herself up by"
                                            + " your neck and touches " + controller.directObject() + " nose to yours."
                                            + " So close to cumming, you can't bring yourself to look away. ";
                        }
                        break;
                    case pin:
                        if (pos.dom(controller)) {
                            succeeded = true;
                            description = "You turn your head away as you feel your orgasm coming on, wary of " + controller.getName() + "'s"
                                            + " hypnotic eyes. " + Formatter.capitalizeFirstLetter(controller.pronoun()) + "'s"
                                            + " not having it, though. " + controller.pronoun() + " grabs your head"
                                            + " and forces your eyelids open with " + controller.directObject() + " thumbs. ";
                        } else {
                            succeeded = false;
                            description = "With " + controller.getName() + " pinned beneath you as " + controller.pronoun()
                                            + " is, it's not hard for you to keep your eyes from meeting hers as you "
                                            + "launch into your orgasm.";
                        }
                        break;
                    case oralpin:
                        if (pos.dom(controller)) {
                            succeeded = true;
                            description = controller.getName() + " grabs a fistful of your hair and pulls your head downwards. There is"
                                            + " nothing you can do to evade " + controller.directObject() + " hypnotic"
                                            + " gaze as you erupt into " + controller.directObject() + " sucking mouth. ";
                        } else {
                            // probably extremely rare
                            succeeded = false;
                            description = "You close your eyes to make certain you're not going to be affected by "
                                            + controller.getName() + "'s eyes as you cum.";
                        }
                        break;
                    case neutral:
                    case standingover:
                        if (affected.canAct()) {
                            succeeded = false;
                            description = "With the freedom of movement you have at the moment, turning your gaze away"
                                            + " from " + controller.getName() + "'s hypnotic eyes is quite easy even with "
                                            + "your impending orgasm.";
                        } else {
                            succeeded = true;
                            description = "Immobilized as you are, you can't keep " + controller.getName() + " from gazing "
                                            + "deeply into your eyes as your orgasm begins to wash over you.";
                        }
                        break;
                    default:
                        if (pos.facing(affected, controller)) {
                            succeeded = true;
                            description = controller.getName() + " gazes into your eyes as " + controller.pronoun() 
                                            + " pushes you over the edge. ";
                        } else {
                            succeeded = false;
                            description = "Since you're not facing " + controller.getName() + ", "
                                            + controller.directObject() + " hypnotic eyes cannot affect you.";
                        }
                }
        }

        public boolean hasSucceeded() {
            return succeeded;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Result [succeeded=" + succeeded + ", description=" + description + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((description == null) ? 0 : description.hashCode());
            result = prime * result + (succeeded ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Result other = (Result) obj;
            if (description == null) {
                if (other.description != null)
                    return false;
            } else if (!description.equals(other.description))
                return false;
            return succeeded == other.succeeded;
        }
    }

    public class MindControlWithdrawal extends Status {
        MindControlWithdrawal(CharacterType affected) {
            super("Mind Control Withdrawal", affected);
        }

        @Override
        public void tick(Combat c) {
            if (getAffected().getStamina()
                        .percent() > 5) {
                int amt = getSeverity().ordinal() * (Random.random(6) + 1);
                getAffected().weaken(c, (int) DamageType.temptation.modifyDamage(getCause(), getAffected(), amt));
                Formatter.writeIfCombat(c, getAffected(), "You keep fighting your own body to do as you want, and it's tiring you rapidly.");
            }
        }

        @Override
        public String initialMessage(Combat c, Status replacement) {
            return ""; // handled by withdrawal message
        }

        @Override
        public String describe(Combat c) {
            return "";
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

        @Override
        public int value() {
            return 0;
        }

        @Override
        public Status instance(Character newAffected, Character newOther) {
            return new MindControlWithdrawal(newAffected.getType());
        }

        @Override public JsonObject saveToJson() {
            return null;
        }

        @Override public Status loadFromJson(JsonObject obj) {
            return null;
        }

    }
}
