package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;

public class CommandOral extends PlayerCommand {

    CommandOral() {
        super("Force Oral");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return super.usable(c, user, target) && user.crotchAvailable() && c.getStance().oral(target, target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Force your opponent to go down on you.";
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 30;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        boolean silvertongue = target.has(Trait.silvertongue);
        boolean lowStart = user.getArousal().get() < 15;
        int m = (silvertongue ? 8 : 5) + Random.random(10);
        if (user.human()) {
            if (lowStart) {
                if (m < 8) {
                    c.write(user, deal(c, 0, Result.weak, user, target));
                } else {
                    c.write(user, deal(c, 0, Result.strong, user, target));
                }
            } else {
                c.write(user, deal(c, 0, Result.normal, user, target));
            }
        }
        user.body.pleasure(target, target.body.getRandom("mouth"), user.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int magnitude, Result modifier, Character user, Character target) {
        switch (modifier) {
            case normal:
                return target.getName() + " is ecstatic at being given the privilege of"
                                + " pleasuring you and does a fairly good job at it, too. She"
                                + " sucks your hard dick powerfully while massaging your balls.";
            case strong:
                return target.getName() + " seems delighted to 'help' you, and makes short work"
                                + " of taking your flaccid length into her mouth and getting it " + "nice and hard.";
            case weak:
                return target.getName() + " tries her very best to get you ready by running"
                                + " her tongue all over your groin, but even"
                                + " her psychically induced enthusiasm can't get you hard.";
            default:
                return "<<This should not be displayed, please inform The" + " Silver Bard: CommandOral-deal>>";
        }
    }

    @Override
    public String receive(Combat c, int magnitude, Result modifier, Character user, Character target) {
        return "<<This should not be displayed, please inform The" + " Silver Bard: CommandOral-receive>>";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
