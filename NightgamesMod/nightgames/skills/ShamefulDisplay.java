package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.status.Horny;
import nightgames.status.Shamed;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class ShamefulDisplay extends Skill {

    ShamefulDisplay() {
        super("Shameful Display");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.submission) >= 15 && !user.has(Trait.shameless);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && user.crotchAvailable()
                        && (user.hasDick() || user.hasPussy());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Degrade yourself to entice your opponent";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (user.checkAddiction(AddictionType.MIND_CONTROL, target)) {
            user.unaddictCombat(AddictionType.MIND_CONTROL,
                            target, Addiction.LOW_INCREASE, c);
            c.write(user, "Acting submissively voluntarily reduces Mara's control over " + user.nameDirectObject());
        }
        user.add(c, new Shamed(user.getType()));
        int divisor = target.getMood() == Emotion.dominant ? 3 : 4;
        target.add(c, Horny.getWithPsychologicalType(user, target, user.getAttribute(Attribute.submission) / divisor, 2, " (Dominant Thrill)"));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (user.hasDick()) {
            return String.format(
                            "You spread your legs, exposing your naked %s and balls, and thrust your hips"
                                            + " out in a show of submission. %s practically drools at the sight, "
                                            + "while you struggle to bear the shame.",
                            user.body.getRandomCock().describe(user),
                            Formatter.capitalizeFirstLetter(target.pronoun()));
        } else {
            return String.format(
                            "You spread your legs and dip a hand between them. You stare lustfully"
                                            + " at %s while you finger yourself, breathing heavily. You feel a thrill"
                                            + " at opening yourself up like this, and %s seems quite taken with the sight.",
                            target.getName(), target.pronoun());
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (user.hasDick()) {
            return String.format(
                            "%s fondles %s %s while looking at %s with an almost daring look."
                                            + " %s seems to find the situation arousing, and so %s %s.",
                            user.getName(), user.possessiveAdjective(),
                            user.body.getRandomCock().describe(user),
                            target.nameDirectObject(),
                            Formatter.capitalizeFirstLetter(user.pronoun()),
                            target.action("do", "does"), target.subject());
        } else {
            return String.format(
                            "%s lifts %s hips and spreads %s pussy lips open. %s's "
                                            + "bright red with shame, but the sight is lewd enough to drive %s wild.",
                            user.getName(), user.possessiveAdjective(), user.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(user.pronoun()),
                            target.nameDirectObject());
        }
    }

}
