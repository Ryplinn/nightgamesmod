package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Charmed;
import nightgames.status.Enthralled;
import nightgames.status.Stsflag;
import nightgames.status.Trance;

public class Tempt extends Skill {

    public Tempt() {
        super("Tempt");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        double m = 4 + Random.random(4);

        if (c.getStance().front(user)) {
            // opponent can see self
            m += user.body.getHotness(target);
        }

        if (target.has(Trait.imagination)) {
            m *= 1.5;
        }

        int n = (int) Math.round(m);

        boolean tempted = Random.random(5) == 0;
        if (user.has(Trait.darkpromises) && tempted && !target.wary() && user.canSpend(15)) {
            user.spendMojo(c, 15);
            c.write(user,
                            Formatter.format("{self:NAME-POSSESSIVE} words fall on fertile grounds. {other:NAME-POSSESSIVE} will to resist crumbles in light of {self:possessive} temptation.",
                                            user, target));
            target.add(c, new Enthralled(target.getType(), user.getType(), 3));
        } else if (user.has(Trait.commandingvoice) && Random.random(3) == 0) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:speak|speaks} with such unquestionable"
                            + " authority that {other:subject-action:don't|doesn't} even consider disobeying."
                            , user, target));
            target.add(c, new Trance(target.getType(), 1, false));
        } else if (user.has(Trait.MelodiousInflection) && !target.is(Stsflag.charmed) && Random.random(3) == 0) {
            c.write(user, Formatter.format("Something about {self:name-possessive} words, the"
                            + " way {self:possessive} voice rises and falls, {self:possessive}"
                            + " pauses and pitch... {other:SUBJECT} soon {other:action:find|finds}"
                            + " {other:reflective} utterly hooked.", user, target));
            target.add(c, new Charmed(target.getType(), 2).withFlagRemoved(Stsflag.mindgames));
        }

        target.temptNoSource(c, user, n, this);
        target.emote(Emotion.horny, 10);
        user.emote(Emotion.confident, 10);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 15;
    }

    @Override
    public int speed(Character user) {
        return 9;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.temptLiner(c, target);
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.temptLiner(c, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Tempts your opponent. More effective if they can see you.";
    }
}
