package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Behind;
import nightgames.stance.Stance;

public class Turnover extends Skill {

    Turnover() {
        super("Turn Over");
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.power) >= 4;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().enumerate() == Stance.standingover && c.getStance().dom(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Turn your opponent over and get behind her";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        writeOutput(c, Result.normal, user, target);
        c.setStance(new Behind(user.getType(), target.getType()), user, true);
        target.emote(Emotion.dominant, 20);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You turn " + target.getName() + " onto her hands and knees. You move behind her while she slowly gets up.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s rolls %s onto %s stomach. %s %s back "
                        + "up, but %s takes the opportunity to get behind %s.", user.subject(),
                        target.nameDirectObject(), target.possessiveAdjective(),
                        Formatter.capitalizeFirstLetter(target.subjectAction("push", "pushes")),
                        target.reflectivePronoun(), user.subject(), target.directObject());
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
