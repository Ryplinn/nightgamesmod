package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.StandingOver;

public class Dominate extends Skill {

    public Dominate() {
        super("Dominate", 3);
        addTag(SkillTag.positioning);
        addTag(SkillTag.knockdown);
        addTag(SkillTag.dark);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.darkness) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && !c.getStance().sub(user) && !c.getStance().prone(user)
                        && !c.getStance().prone(target) && !c.getStance().sub(target) && user.canAct() && !user.has(Trait.submissive);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Overwhelm your opponent to force her to lie down: 30% Arousal";
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        user.arouse((int) (user.getArousal().max() * .30), c);
        writeOutput(c, Result.normal, user, target);
        c.setStance(new StandingOver(user.getType(), target.getType()), target, false);
        user.emote(Emotion.dominant, 20);
        target.emote(Emotion.nervous, 20);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You take a deep breathe, gathering dark energy into your lungs. You expend the power to command "
                        + target.getName() + " to submit. The demonic command renders her "
                        + "unable to resist and she drops to floor, spreading her legs open to you. As you approach, she comes to her senses and quickly closes her legs. Looks like her "
                        + "will is still intact.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s forcefully orders %s to \"Kneel!\" %s body complies without waiting for"
                        + " %s brain and %s %s to %s knees in front of %s. %s smiles and "
                        + "pushes %s onto %s back. By the time %s free of %s suggestion, %s %s"
                        + " flat on the floor with %s foot planted on %s chest.", user.subject(),
                        target.subject(), Formatter.capitalizeFirstLetter(target.pronoun()),
                        target.possessiveAdjective(), target.pronoun(), target.action("drop"),
                        target.possessiveAdjective(), user.directObject(),
                        user.getName(), target.nameDirectObject(), target.possessiveAdjective(),
                        target.subjectAction("break"), user.possessiveAdjective(), target.pronoun(),
                        target.action("are", "is"), user.nameOrPossessivePronoun(), target.possessiveAdjective());
    }

}
