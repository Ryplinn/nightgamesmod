package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Behind;
import nightgames.stance.Stance;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Cowardice extends Skill {

    public Cowardice() {
        super("Cowardice");
        addTag(SkillTag.suicidal);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.submission) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && target.canAct() && c.getStance().en == Stance.neutral;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Turning your back to an opponent will likely get you attacked from behind.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        c.setStance(new Behind(target.getType(), user.getType()), target, true);
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (user.checkAddiction(AddictionType.MIND_CONTROL, target)) {
            user.unaddictCombat(AddictionType.MIND_CONTROL,
                            target, Addiction.LOW_INCREASE, c);
            c.write(user, "Acting submissively voluntarily reduces Mara's control over " + user.nameDirectObject());
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Cowardice();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You try to run away, but " + target.getName() + " catches you and grabs you from behind.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s tries to sprint away, but %s quickly %s %s from behind before %s can escape", 
                            user.subject(), target.subject(), target.action("grab"),
                            user.directObject(), user.pronoun());
    }

}
