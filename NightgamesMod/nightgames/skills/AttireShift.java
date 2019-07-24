package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Primed;

public class AttireShift extends Skill {

    AttireShift() {
        super("Attire Shift");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.temporal) >= 6;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && !target.outfit.isNude()
                        && Primed.isPrimed(user, 2);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Seperate your opponent from her clothes: 2 charges";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.add(c, new Primed(user.getType(),-2));
        target.nudify();
        writeOutput(c, Result.normal, user, target);
        user.emote(Emotion.dominant, 15);
        target.emote(Emotion.nervous, 10);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new AttireShift();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("You trigger a small temporal disturbance, sending %s's clothes a couple seconds back in time. "
                        + "Due to the speed and rotation of the Earth, they probably ended up somewhere over the Pacific Ocean.", target.getName());
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s triggers a device on her arm and %s clothes suddenly vanish. "
                        + "What the fuck did %s just do?",user.getName(), target.nameOrPossessivePronoun(),
                        user.pronoun());
    }

}
