package nightgames.status.addiction;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.stance.Behind;
import nightgames.stance.Mount;
import nightgames.stance.Position;
import nightgames.stance.Stance;
import nightgames.status.CrisisOfFaith;
import nightgames.status.DivineCharge;
import nightgames.status.Status;

import java.util.Optional;

public class ZealAddiction extends Addiction {

    ZealAddiction(CharacterType afflicted, CharacterType cause, float magnitude) {
        super("Zeal", afflicted, cause, magnitude);
    }

    private class ZealTrackingSymptom extends AddictionSymptom {
        ZealTrackingSymptom(ZealAddiction source, float initialMagnitude) {
            super(afflicted, "Zealous Rapture", source, initialMagnitude);
        }

        @Override
        public void tick(Combat c) {
            super.tick(c);
            if (c != null && (c.getStance().en == Stance.neutral || c.getStance().en == Stance.behind)
                            && Random.randomdouble() < Math.min(.5f, combatMagnitude / 2.0)) {
                c.write(getAffected(), "Overcome by your desire to serve " + getCause().getName() + ", you get on the ground "
                                + "and prostrate yourself in front of " + getCause().directObject() + ".");
                boolean behindPossible = getCause().hasDick();
                Position pos;
                if (!behindPossible || Random.random(2) == 0) {
                    pos = new Mount(cause, affected);
                    c.write(getCause(), String.format(
                                    "%s tells you to roll over, and once you have done so %s sets"
                                                    + " %s down on your stomach.",
                                    getCause().getName(), getCause().pronoun(), getCause().reflectivePronoun()));
                } else {
                    pos = new Behind(cause, affected);
                    c.write(getCause(), String.format("%s motions for you to get up and then casually walks around you"
                                    + ", grabbing you from behind.", getCause().getName()));
                }
                c.setStance(pos);
            }
        }

        @Override
        public int value() {
            return -3;
        }
    }


    @Override public Optional<Status> withdrawalEffects() {
        return Optional.of(new CrisisOfFaith(afflicted));
    }

    @Override
    public Optional<AddictionSymptom> startCombat(Combat c, Character opp) {
        Optional<AddictionSymptom> s = super.startCombat(c, opp);
        if (shouldApplyDivineCharge() && opp.equals(getCause())) {
            int sev = getSeverity().ordinal();
            opp.status.add(new DivineCharge(opp.getType(), sev * .75f));
        }
        return s;
    }

    private boolean shouldApplyDivineCharge() {
        return !isInWithdrawal() && isActive();
    }

    @Override public String describeIncrease() {
        switch (getSeverity()) {
            case HIGH:
                return getCause().getName()
                                + " demands worship! The holy aura only reinforces your faith and your desire to serve!";
            case LOW:
                return getCause().getName() + " shines brightly as you cum inside of " + getCause().directObject() + ". "
                                + "Maybe there is something to this whole divinity spiel?";
            case MED:
                return "You are now convinced " + getCause().getName()
                                + " is a higher power, and you feel a need to serve " + getCause().directObject() + " accordingly.";
            case NONE:
            default:
                return ""; // hide
        }
    }

    @Override public String describeDecrease() {
        switch (getSeverity()) {
            case LOW:
                return "Your faith is shaking. Is " + getCause().getName() + " really so divine?";
            case MED:
                return "Your faith in " + getCause().getName() + " has wavered a bit, but " + getCause().pronoun() + "'s still"
                                + " your goddess! Right?";
            case NONE:
                return "You don't know what possessed you before, but you no longer see " + getCause().getName()
                                + " as <i>actually</i> divine.";
            case HIGH:
            default:
                return ""; // hide
        }
    }

    @Override public String describeWithdrawal() {
        switch (getSeverity()) {
            case HIGH:
                return "<b>Your mind is completely preoccupied by " + getCause().getName() + ". You didn't worship today!"
                                + " Will " + getCause().directObject() + " be angry? What will you do if " + getCause().pronoun()
                                + " is? You aren't going to be able to focus on much else tonight.</b>";
            case MED:
                return "<b>You are terribly nervous at the thought of having to face " + getCause().getName()
                + " tonight after failing to pray to " + getCause().directObject() + " today. The rampaging"
                + " thoughts are throwing you off your game.</b>";
            case LOW:
                return "<b>You didn't pay your respects to " + getCause().getName() + " today... Is that bad? Or isn't it?"
                                + " You are confused, and will have less mojo tonight.</b>";
            case NONE:
                throw new IllegalStateException("Tried to describe withdrawal for an inactive zeal addiction.");
            default:
                return ""; // hide
        }
    }

    @Override public String describeCombatIncrease() {
        return "You feel an increasingly strong desire to do whatever " + getCause().getName()
                        + " wants. " + Formatter.capitalizeFirstLetter(getCause().pronoun()) + "'s a goddess, after all!";
    }

    @Override public String describeCombatDecrease() {
        return "Doing " + getCause().getName() + "'s bidding clears your mind a bit. Why are you really doing this?"
                        + " One look at her reaffirms " + getCause().directObject() + " divinity in your mind, though.";
    }

    @Override
    public String describeMorning() {
        return "An image of " + getCause().getName() + " in " + getCause().directObject() + " full angelic splendor is fixed in your"
                        + " mind as you get up. A growing part of you wants to pray to this new deity; to worship "
                        + getCause().directObject() + " and support you in the day to come.";
    }

    @Override public AddictionSymptom createTrackingSymptom(float initialCombatMagnitude) {
        return new ZealTrackingSymptom(this, initialCombatMagnitude);
    }

    @Override
    public AddictionType getType() {
        return AddictionType.ZEAL;
    }

    @Override
    public String initialMessage(Combat c, Status replacement) {
        if (inWithdrawal) {
            return "You tremble as " + getCause().getName()
                            + " steps into view. Will " + getCause().pronoun() + " punish you for not being pious enough?"
                            + " Perhaps you should beg forgiveness...";
        }
        return getCause().pronoun() + "'s here! The holy glow reveals " + getCause().directObject() + " presence even before you"
                        + " see " + getCause().directObject() + ", and you nearly drop to your knees where you are.";
    }

    @Override
    public String describe(Combat c, Severity severity) {
        switch (severity) {
            case HIGH:
                return "Your knees tremble with your desire to offer yourself to your goddess.";
            case MED:
                return "A part of you is screaming to kneel before " + getCause().getName()
                + ". Perhaps it's better to just give in?";
            case LOW:
                return getCause().getName() + " divine presence makes you wonder whether you should really be fighting "
                        + getCause().directObject() + ".";
            case NONE:
            default:
                return "";
        }
    }


    @Override
    public String informantsOverview() {
        return "You WHAT?! You actually think " + getCause().getName() + " is divine? As in god-like? Are you shitting me?"
                        + " Man, even I don't know where " + getCause().pronoun() + " got that pussy from, but it's certainly done"
                        + " a number on you. Well, I suppose that if you're busy worshipping " + getCause().directObject() + ", you'll"
                        + " be assuming some submissive positions in the process. That can't help your chances"
                        + " in a fight. And if you're really serious about this, the soul-searching required to"
                        + " get rid of it will probably throw you off your game for a while. Not good, man. I"
                        + " suppose you can draw some spiritual strength from your newfound faith, many people"
                        + " seem to do so, but is that worth it?";
    }

}
