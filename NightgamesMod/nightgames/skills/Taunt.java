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
import nightgames.status.Shamed;
import nightgames.status.Stsflag;

public class Taunt extends Skill {

    public Taunt() {
        super("Taunt");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.mostlyNude() && !c.getStance().sub(user) && user.canAct() && !user.has(Trait.shy);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 10;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        double m = (6 + Random.random(4) + user.body.getHotness(target)) / 3
                        * Math.min(2, 1 + target.getExposure());
        if (target.has(Trait.imagination)) {
            m += 4;
            // chance += .25;
        }
        target.temptNoSource(c, user, (int) Math.round(m), this);
        statusCheck(new Shamed(target.getType()), c, user, target, .25).ifPresent(status -> target.add(c, status));
        if (user.has(Trait.commandingvoice) && Random.random(3) == 0) {
            c.write(user, Formatter.format("{other:SUBJECT-ACTION:speak|speaks} with such unquestionable"
                            + " authority that {self:subject-action:don't|doesn't} even consider not obeying."
                            , user, target));
            target.add(c, new Enthralled(target.getType(), user.getType(), 1, false));
        } else if (user.has(Trait.MelodiousInflection) && !target.is(Stsflag.charmed) && Random.random(3) == 0) {
            c.write(user, Formatter.format("Something about {self:name-possessive} words, the"
                            + " way {self:possessive} voice rises and falls, {self:possessive}"
                            + " pauses and pitch... {other:SUBJECT} soon {other:action:find|finds}"
                            + " {other:reflective} utterly hooked.", user, target));
            target.add(c, new Charmed(target.getType(), 2).withFlagRemoved(Stsflag.mindgames));
        }
        target.emote(Emotion.angry, 30);
        target.emote(Emotion.nervous, 15);
        user.emote(Emotion.dominant, 20);
        target.loseMojo(c, 5);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.cunning) >= 8 || user.getAttribute(Attribute.power) >= 15;
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
        return "You tell " + target.getName()
                        + " that if she's so eager to be fucked senseless, you're available during off hours.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.taunt(c, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Embarrass your opponent. Lowers Mojo, may inflict Shamed";
    }
}
