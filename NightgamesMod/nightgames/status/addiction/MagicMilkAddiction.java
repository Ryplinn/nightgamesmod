package nightgames.status.addiction;

import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Status;
import nightgames.status.Stsflag;

import java.util.Optional;

import static nightgames.status.addiction.Addiction.Severity.*;

public class MagicMilkAddiction extends Addiction {
    private int originalMaxWill;

    public MagicMilkAddiction(CharacterType afflicted, CharacterType cause, float magnitude) {
        super("Magic Milk Addiction", afflicted, cause, magnitude);
    }

    public MagicMilkAddiction(CharacterType afflicted, CharacterType cause) {
        this(afflicted, cause, .01f);
    }

    private class MagicMilkTrackingSymptom extends AddictionSymptom {
        MagicMilkTrackingSymptom(MagicMilkAddiction source, float initialMagnitude) {
            super(afflicted, "Milk Cravings", source, initialMagnitude);
            flag(Stsflag.magicmilkcraving);
            flag(Stsflag.tolerance); // immune to regular addiction
        }

        @Override
        public int tempted(Combat c, int x) {
            return (int) (x * (combatAtLeast(MED) ? combatAtLeast(HIGH) ? 1.5 : 1.25 : 1));
        }

        @Override
        public void tick(Combat c) {
            super.tick(c);
            if (combatAtLeast(LOW)) {
                int loss;
                switch (getCombatSeverity()) {
                    case LOW:
                        loss = Random.random(1, 3);
                        break;
                    case MED:
                        loss = Random.random(3, 6);
                        break;
                    case HIGH:
                        loss = Random.random(5, 8);
                        break;
                    default:
                        throw new IllegalStateException();
                }
                getAfflicted().loseWillpower(c, loss, 0, false, " due to your Milk Addiction");
            }
        }
    }

    @Override public Optional<Status> withdrawalEffects() {
        double mod = 1.0 / (double) getSeverity().ordinal();
        originalMaxWill = getAfflicted().getWillpower().max();
        getAfflicted().getWillpower().setTemporaryMax((int) (originalMaxWill * mod));
        return Optional.empty();
    }

    @Override
    public void endNight() {
        super.endNight();
        getAfflicted().getWillpower().setTemporaryMax(originalMaxWill);
    }

    @Override public String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                return "You feel empty without " + getCause().getName() + "'s milk flowing down your throat. You need more!";
            case LOW:
                return "You feel a strange yearning for more of " + getCause().getName() + "'s milk.";
            case MED:
                return "You know it's bad for you, but you really want more of that milk.";
            case NONE:
            default:
                return ""; // hide
        }
    }

    @Override public String describeDecrease() {
        switch (getSeverity()) {
            case LOW:
                return "The desire for " + getCause().getName() + "'s milk has calmed down somewhat.";
            case MED:
                return "You still know you're addicted to " + getCause().getName() + "'s milk, but you"
                                + " can control yourself a little better now.";
            case NONE:
                return "Finally, you feel the last remnants of the unnatural thirst for milk leave you.";
            case HIGH:
            default:
                return ""; // hide
        }
    }

    @Override public String describeWithdrawal() {
        switch (getSeverity()) {
            case HIGH:
                return "<b>You haven't had any of " + getCause().getName() + "'s milk today, and the thirst threatens"
                                + " to overwhelm you. You won't last long in fights tonight...</b>";
            case LOW:
                return "<b>You feel a little uneasy going without " + getCause().getName() + "'s milk for a whole day,"
                                + " and it's distracting you to the point where it lowers your willpower!</b>";
            case MED:
                return "<b>The thirst for " + getCause().getName() + "'s milk burns within you, "
                                + "scorching away such useless things as a strong will.</b>";
            case NONE:
                throw new IllegalStateException("Tried to describe withdrawal for an inactive milk addiction.");
            default:
                return ""; // hide
        }
    }

    @Override public String describeCombatIncrease() {
        return "The swaying of " + getCause().getName() + "'s breasts causes "
                        + "you to remember vividly the taste of " + getCause().directObject() + " milk. You know you want more.";
    }

    @Override public String describeCombatDecrease() {
        return "Having drank some of " + getCause().getName() + "'s sweet nectar, the thirst fades into the background. A part"
                        + " of you is already looking forward to more, though.";
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (inWithdrawal) {
            return "The burning thirst wells up at the sight of " + getCause().getName() + ". It would be so easy to subdue,"
                        + " just a little sip...";
        }
        return "\"Milk\" is the first thing you think of when you see " + getCause().getName() + ". "
                        + "You won't be able to ignore your urges for long...";
    }

    @Override
    public String describe(Combat c, Severity severity) {
        switch (severity) {
            case HIGH:
                return "You are desperate for more milk and can't even think of resisting " + getCause().directObject() + ".";
            case LOW:
                return "You are distracted by the lingering sweetness of " + getCause().getName() + "'s milk, "
                                + "and it's sapping your will to resist.";
            case MED:
                return "You thirst for more of " + getCause().getName() + "'s milk and are struggling to keep your mind in the game.";
            case NONE:
            default:
                return "";

        }
    }

    @Override
    public AddictionType getType() {
        return AddictionType.MAGIC_MILK;
    }

    @Override
    public String describeMorning() {
        switch (getSeverity()) {
            case HIGH:
                aggravate(null, Addiction.MED_INCREASE);
                flagDaytime();
                return "You wake up in the morning with a burning need for " + getCause().getName() + "'s milk. The thought of resisting the urge doesn't even enter your mind. You quickly whip out your cellphone and dial " + getCause()
                                .getName() + "'s number. "
                                + "Moments later, an amused voice answers. You sob into the phone, begging for " + getCause()
                                .getName() + "'s milk. Luckily, " + getCause().getName() + " doesn't seem to want to tease you today, and readily agrees to drop by. "
                                + "Fifteen agonizing minutes later, the doorbell rings and you rush to answer. Giving you a quick and dirty kiss at the door way, " + getCause()
                                .getName() + " enters your room and sits down on your bed. "
                                + Formatter.capitalizeFirstLetter(getCause().pronoun()) + " pats " + getCause().directObject() + " lap and motions for you to strip and lie down. You quickly comply and lay in " + getCause()
                                .directObject() + " lap facing the ceiling, giddy for more milk. <br/>"
                                + "With a coy grin, " + getCause().getName() + " strips off " + getCause().directObject() + " top and lets " + getCause()
                                .directObject() + " bountiful breasts bounce free of " + getCause().directObject() + " bra. Your eyes immediately zeroes into " + getCause()
                                .directObject() + " nipples, already dripping with opalescent white fluids. "
                                + getCause().getName() + " lowers " + getCause().directObject() + " breasts into your face, and you happily start drinking " + getCause()
                                .directObject() + " mindbending milk. Seconds turn into minutes and minutes turn into hours. "
                                + "You don't know how long your were nursing at " + getCause().directObject() + " teats, but you seemed to have dozed off in the middle of it. You find yourself on the bed by yourself, with a blanket covering you. "
                                + getCause().getName() + " has already left, but left a note on the kitchen table, <br/><i>Hey hun, unfortunately I have to get to class. I made you some lunch that I put in the fridge, and left you a bottle of milk in case the cravings come back. I'll see you tonight at the games okay? Love you baby.</i><br/><br/>";
            case MED:
                return "When you wake up in the morning, the first thing you think of is " + getCause().getName() + "'s breasts. And the second. And the third. In fact, you realize that's all you can think of right now. "
                                + "You sigh and attempt to take a cold shower to tear your mind from " + getCause().directObject() + " sinfully sweet milk. Unfortunately, it does you little good. You will have to make a choice between toughing it out, or caving and calling " + getCause()
                                .getName() + " for a helping of " + getCause().directObject() + " addictive cream.<br/><br/>";
            case LOW:
                return "You wake up in the morning with damp underwear. You realize that you've been dreaming of " + getCause()
                                .getName() + "'s milk the entire night. This can't be healthy... <br/>"
                + "You want to immediately head over to " + getCause().getName() + "'s and ask for another helping, but quickly realize that will just feed the addiction. "
                + "However, at this rate, you will be thinking of " + getCause().directObject() + " the entire day, and affect your willpower. You will have to make a decision to tough it out or call " + getCause()
                                .directObject() + " up and ask for more.<br/><br/>";
            case NONE:
            default:
                return "You wake up in the morning with your throat feeling strangely parched. You step into the kitchen and take out a carton of milk to attempt to slake your thirst. "
                                + "Five minutes and a empty carton later, you still don't feel much better. You decide to ignore it and head to class.<br/><br/>";
        }
    }

    @Override public AddictionSymptom createTrackingSymptom(float initialCombatMagnitude) {
        return new MagicMilkTrackingSymptom(this, initialCombatMagnitude);
    }

    @Override
    public String informantsOverview() {
        return "You let " + getCause().getName() + "'s milk get to you? I know those new and improved"
                        + " boobs are great to look at, but couldn't you be a bit more careful?"
                        + " Now I'm not at liberty to tell you how I know this, but you'll have noticed that "
                        + "you're likely to find yourself unwilling to keep fighting if you allow this thing"
                        + " to grow too bad. At first it'll only be when directly confronted with " + getCause().directObject() + " directly,"
                        + " but if you don't drink for long enough, well, you won't do very well. That said, it"
                        + " does at least mean you won't be affected by any weaker addictive substances, so you've"
                        + " got that going for you.";
    }

}
