package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Behind;
import nightgames.status.Primed;

public class EmergencyJump extends Skill {

    EmergencyJump() {
        super("Emergency Jump");
        addTag(SkillTag.positioning);
        addTag(SkillTag.escaping);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.temporal) >= 4;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return ((c.getStance()
                  .sub(user)
                        && !c.getStance()
                             .mobile(user)
                        && !c.getStance()
                             .penetrated(c, user)
                        && !c.getStance()
                             .penetrated(c, target))
                        || user.bound()) && !user.stunned() && !user.distracted()
                        && Primed.isPrimed(user, 2);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Escape from a disadvantageous position and/or bind: 2 charges";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.add(c, new Primed(user.getType(),-2));
        user.free();
        c.setStance(new Behind(user.getType(), target.getType()), user, true);
        if(user.human()){
            c.write(user,deal(c,0,Result.normal, user, target));
        }
        else if(target.human()){
            c.write(user,receive(c,0,Result.normal, user, target));
        }
        user.emote(Emotion.confident, 15);
        target.emote(Emotion.nervous, 15);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You're in trouble for a moment, so you trigger your temporal manipulator and stop time just long "
                        + "enough to free yourself.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "%s thought %s had %s right where %s wanted %s, but %s seems to vanish completely and escape.",
                        target.subject(), target.pronoun(), user.getName(),
                        target.pronoun(), user.directObject(), user.pronoun());
    }

}
