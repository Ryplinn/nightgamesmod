package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.status.Horny;
import nightgames.status.Stsflag;

public class LustAura extends Skill {

    LustAura() {
        super("Lust Aura");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.darkness) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canRespond() && c.getStance().mobile(user)
                        && !target.is(Stsflag.horny, user.nameOrPossessivePronoun() + " aura of lust");
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Inflicts arousal over time: 10 Arousal, 10 Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.arouse(10, c);
        writeOutput(c, Result.normal, user, target);
        target.add(c, Horny.getWithPsychologicalType(user, target, (float) (3 + 2 * user.getExposure()), 3 + Random
                                        .random(3),
                        user.nameOrPossessivePronoun() + " aura of lust"));
        target.emote(Emotion.horny, 10);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You allow the corruption in your libido to spread out from your body. " + target.getName()
                        + " flushes with arousal and presses her thighs together as the aura taints her.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s releases an aura of pure sex. %s %s body becoming hot just being near %s.",
                        user.subject(), Formatter.capitalizeFirstLetter(target.subjectAction("feel")),
                        target.possessiveAdjective(), user.directObject());
    }

}
