package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.pet.Fairy;
import nightgames.pet.FairyFem;
import nightgames.pet.FairyMale;
import nightgames.pet.Ptype;

public class SpawnFaerie extends Skill {
    private Ptype gender;

    SpawnFaerie(Ptype gender) {
        super("Summon Faerie (" + gender.name() + ")");
        this.gender = gender;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.spellcasting) >= 3;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && c.getPetsFor(user).size() < user.getPetLimit();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return user.has(Trait.faefriend) ? 10 : 25;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Summon a Faerie familiar to support you: " + getMojoCost(c, user) + " Mojo";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int power = 5 + user.getAttribute(Attribute.spellcasting);
        int ac = 4 + user.getAttribute(Attribute.spellcasting) / 10;
        if (user.human()) {
            c.write(user, deal(c, 0, Result.normal, user, target));
            switch (gender) {
                case fairyfem:
                    c.addPet(user, new Fairy(user, Ptype.fairyfem, power, ac).getSelf());
                    break;
                case fairymale:
                    c.addPet(user, new Fairy(user, Ptype.fairymale, power, ac).getSelf());
                    break;
                case fairyherm:
                default:
                    c.addPet(user, new Fairy(user, Ptype.fairyherm, power, ac).getSelf());
            }
        } else {
            if (target.human()) {
                c.write(user, receive(c, 0, Result.normal, user, target));
            }
            if (gender == Ptype.fairyfem) {
                c.addPet(user, new FairyFem(user, power, ac).getSelf());
            } else {
                c.addPet(user, new FairyMale(user, power, ac).getSelf());
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
        if (gender == Ptype.fairyfem) {
            return "Faerie (female)";
        } else {
            return "Faerie (male)";
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (gender == Ptype.fairyfem) {
            return "You start a summoning chant and in your mind, seek out a familiar. A pretty little faerie girl appears in front of you and gives you a friendly wave before "
                            + "landing softly on your shoulder.";
        } else {
            return "You start a summoning chant and in your mind, seek out a familiar. A six inch tall faerie boy winks into existence in response to your call. The faerie "
                            + "hovers in the air on dragonfly wings.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
    	if (gender == Ptype.fairyfem) {
	        return String.format("%s casts a spell as %s extends %s hand. In a flash of magic,"
	                        + " a small, naked girl with butterfly wings appears in %s palm.",
	                        user.subject(), user.pronoun(), user.possessiveAdjective(),
	                        user.possessiveAdjective());
    	} else {
	        return String.format("%s casts a spell as %s extends %s hand. In a flash of magic,"
	                        + " a small, naked boy with butterfly wings appears in %s palm.",
	                        user.subject(), user.pronoun(), user.possessiveAdjective(),
	                        user.possessiveAdjective());
    	}
    }

}
