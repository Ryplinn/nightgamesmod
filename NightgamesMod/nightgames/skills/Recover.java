package nightgames.skills;

import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Neutral;
import nightgames.status.Stsflag;

public class Recover extends Skill {

    public Recover() {
        super("Recover");
        addTag(SkillTag.positioning);
        addTag(SkillTag.escaping);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().prone(user) && c.getStance().mobile(user) && !(new StandUp())
                        .usable(c, user, target) && user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
        } else if (c.shouldPrintReceive(target, c)) {
            if (target.is(Stsflag.blinded))
                printBlinded(c, user);
            else
                c.write(user, receive(c, 0, Result.normal, user, target));
        }
        c.setStance(new Neutral(user.getType(), target.getType()), user, true);
        user.heal(c, Random.random(3));
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int speed(Character user) {
        return 0;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You pull yourself up, taking a deep breath to restore your focus.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.getName() + " scrambles back to "+user.possessiveAdjective()+" feet.";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Stand up";
    }
}
