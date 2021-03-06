package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Mount;
import nightgames.stance.Stance;

public class Blindside extends Skill {

    public Blindside() {
        super("Blindside", 2);
        addTag(SkillTag.positioning);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.temptress) && user.getAttribute(Attribute.technique) >= 10;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && c.getStance()
                                  .enumerate() == Stance.neutral;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Distract your opponent and take them down.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(new Mount(user.getType(), target.getType()), user, true);
        user.emote(Emotion.confident, 15);
        user.emote(Emotion.dominant, 15);
        target.emote(Emotion.nervous, 10);
        return false;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "You move up to %s and kiss %s strongly. "
                                        + "While %s is distracted, you throw %s down and plant "
                                        + "yourself on top of %s.",
                        target.getName(), target.directObject(), target.pronoun(), target.directObject(),
                        target.directObject());
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "Seductively swaying %s hips, %s sashays over to %s. "
                                        + "%s eyes fix %s in place as %s leans in and firmly kisses %s, shoving %s tongue down"
                                        + " %s mouth. %s are so absorbed in kissing back, that %s only notice %s ulterior motive"
                                        + " once %s has already swept %s legs out from under %s and %s has landed on top of %s.",
                        user.possessiveAdjective(), user.getName(), target.subject(),
                        Formatter.capitalizeFirstLetter(user.possessiveAdjective()), target.directObject(),
                        user.pronoun(), target.directObject(), user.possessiveAdjective(),
                        target.possessiveAdjective(), Formatter.capitalizeFirstLetter(target.pronoun()), target.pronoun(),
                        user.possessiveAdjective(), user.pronoun(), target.possessiveAdjective(),
                        target.directObject(), user.pronoun(), target.directObject());
    }

}
