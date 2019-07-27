package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.pet.ImpFem;
import nightgames.pet.ImpMale;
import nightgames.pet.Ptype;

public class SpawnImp extends Skill {
    private Ptype gender;

    SpawnImp(Ptype gender) {
        super("Summon Imp (" + gender.name() + ")");
        this.gender = gender;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.darkness) >= 6;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && c.getPetsFor(user).size() < user.getPetLimit();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Summon a demonic Imp: 10 mojo, 5 arousal";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        user.arouse(5, c);
        int power = 5 + user.getAttribute(Attribute.darkness);
        int ac = 2 + user.getAttribute(Attribute.darkness) / 10;
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
            if (gender == Ptype.impfem) {
                c.addPet(user, new ImpFem(user, power, ac).getSelf());
            } else {
                c.addPet(user, new ImpMale(user, power, ac).getSelf());
            }
        } else {
            if (target.human()) {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
            if (gender == Ptype.impfem) {
                c.addPet(user, new ImpFem(user, power, ac).getSelf());
            } else {
                c.addPet(user, new ImpMale(user, power, ac).getSelf());
            }
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.summoning;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (gender == Ptype.impfem) {
            return "Imp (female)";
        } else {
            return "Imp (male)";
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (gender == Ptype.impfem) {
            return "You focus your dark energy and summon a minion to fight for you. A naked, waist high, female imp steps out of a small burst of flame. She stirs up her honey "
                            + "pot and despite yourself, you're slightly affected by the pheromones she's releasing.";
        } else {
            return "You focus your dark energy and summon a minion to fight for you. A brief burst of flame reveals a naked imp. He looks at "
                            + target.getName() + " with hungry eyes "
                            + "and a constant stream of pre-cum leaks from his large, obscene cock.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
    	if (gender == Ptype.impfem) {
	        return String.format("%s spreads out %s dark aura and a demonic imp appears next to %s"
	                        + " in a burst of flame. The imp stands about waist height, with bright red hair, "
	                        + "silver skin and a long flexible tail. It's naked, clearly female, and "
	                        + "surprisingly attractive given its inhuman features.",
	                        user.subject(), user.possessiveAdjective(), user.directObject());
    	} else {
	        return String.format("%s spreads out %s dark aura and a demonic imp appears next to %s"
	                        + " in a burst of flame. The imp stands about waist height, with bright red hair, "
	                        + "silver skin and a long flexible tail. It's naked, clearly male, and "
	                        + "surprisingly attractive given its inhuman features.",
	                        user.subject(), user.possessiveAdjective(), user.directObject());
    	}
    }
}
