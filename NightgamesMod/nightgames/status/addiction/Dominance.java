package nightgames.status.addiction;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.status.Masochistic;
import nightgames.status.Status;
import nightgames.status.Stsflag;

import java.util.Optional;

public class Dominance extends Addiction {
    private Integer originalWill;

    public Dominance(CharacterType afflicted, CharacterType cause, float magnitude) {
        super("Dominance", afflicted, cause, magnitude);
    }

    public Dominance(CharacterType afflicted, CharacterType cause) {
        this(afflicted, cause, .01f);
    }

    private class DominanceTrackerSymptom extends AddictionSymptom {
        DominanceTrackerSymptom(Dominance source, float initialMagnitude) {
            super(source.afflicted, "Induced Submission", source, initialMagnitude);
            flags.add(Stsflag.victimComplex);
        }
    }


    public static boolean mojoIsBlocked(Character affected, Combat c) {
        if (c == null)
            return false;
        Character opp = c.getOpponent(affected);
        if (!affected.checkAddiction(AddictionType.DOMINANCE, opp))
            return false;
        int sev = affected.getAnyAddictionSeverity(AddictionType.DOMINANCE)
                        .ordinal();
        int dom = c.getStance().getDominanceOfStance(opp);

        return sev >= 5 - dom;
    }

    @Override public Optional<Status> withdrawalEffects() {
        if (originalWill == null) {
            // TODO: review these calculations
            double mod = Math.min(1.0, 1.0 / (double) getSeverity().ordinal() + .4);
            originalWill = getAfflicted().getWillpower()
                                    .max();
            getAfflicted().getWillpower().setTemporaryMax((int) (originalWill * mod));
        }
        return Optional.of(new Masochistic(afflicted));
    }

    @Override
    public void endNight() {
        super.endNight();

        getAfflicted().getWillpower().setTemporaryMax(originalWill);
        originalWill = null;
    }

    @Override public String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                return "Held down by " + getCause().getName() + ", you feel completely powerless to resist.";
            case LOW:
                return "You feel strangely weak in " + getCause().getName() + "'s powerful hold.";
            case MED:
                return "Something about the way " + getCause().getName() + " is holding on to you is causing your strength to seep away.";
            case NONE:
            default:
                return "";
        }
    }

    @Override public String describeDecrease() {
        switch (getSeverity()) {
            case LOW:
                return "More and more of your strength is returning since escaping from " + getCause().getName() + ". ";
            case MED:
                return "You find some of the strange weakness caused by " + getCause().getName() + "'s powerful hold"
                                + " fleeing your bones. ";
            case NONE:
                return "You have completely recovered from " + getCause().getName() + "'s hold. ";
            case HIGH:
            default:
                return "";
        }
    }

    @Override public String describeWithdrawal() {
        return "Your body longs for the exquisite pain and submission " + getCause().getName() + " can bring you,"
                        + " reducing your stamina and causing masochistic tendencies.";
    }

    @Override public String describeCombatIncrease() {
        return "Being hurt so well just makes you want to submit even more.";
    }

    @Override public String describeCombatDecrease() {
        return "Some of the submissiveness clears from your mind, allowing you to focus" + " more on the fight.";
    }

    @Override
    public String informantsOverview() {
        return "<i>\"Is that all? With all the weird shit going on around here, you're worried about a submissive"
                        + " streak? Well, sure, I can see how it would be a problem. Being held down does not"
                        + " help your chances in a fight, and if you actually enjoy it you are not at all"
                        + " likely to win. Basically, if " + getCause().pronoun() + " gets you down and tied up or something, you're going"
                        + " to lose, because you subconsciously don't actually want to win.\"</i> That does sound"
                        + " pretty bad... Any upsides? <i>\"Well, I suppose that being on the receiving end of such"
                        + " a powerful dominance, the stuff other people do won't make as much of an impression."
                        + " Personally, I wouldn't go for it, but if you like getting hurt and humiliated, go right"
                        + " ahead.\"";
    }

    @Override
    public String describeMorning() {
        return "";
    }

    @Override public AddictionSymptom createTrackingSymptom(float initialCombatMagnitude) {
        return new DominanceTrackerSymptom(this, initialCombatMagnitude);
    }

    @Override
    public AddictionType getType() {
        return AddictionType.DOMINANCE;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (inWithdrawal) {
            return getCause().getName() + " is looking meaner than ever after you neglected to visit today. Equal"
                            + " parts of fear and desire well up inside of you at the thought of what "
                            + getCause().pronoun() + " might do to you.";
        }
        return "You are conflicted at the sight of " + getCause().getName() + ". One part of you still remembers"
                        + " the pain and humiliation " + getCause().pronoun() + " can cause and"
                        + " is terrified because of it, the other part is getting excited"
                        + " for the very same reason.";
    }

    @Override
    public String describe(Combat c, Severity severity) {
        return "";
    }
}
