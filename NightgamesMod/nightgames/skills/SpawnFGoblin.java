package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.pet.FGoblin;
import nightgames.pet.Ptype;

public class SpawnFGoblin extends Skill {

    private final Ptype gender;
    
    SpawnFGoblin(Ptype gender) {
        super("Spawn Fetish Goblin");
        this.gender = gender;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.fetishism) >= 9;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance()
                                      .mobile(user)
                        && !c.getStance().prone(user) && user.getArousal().get() >= 25
                             && c.getPetsFor(user).size() < user.getPetLimit();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Summons a hermaphroditic goblin embodying multiple fetishes: Arousal at least 25";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int power = 3 + user.get(Attribute.fetishism);
        int ac = 2 + user.get(Attribute.fetishism);
        writeOutput(c, Result.normal, user, target);
        c.addPet(user, new FGoblin(user, power, ac).getSelf());
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new SpawnFGoblin(gender);
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.summoning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You channel all the fetishes in your twisted libido into a single form. The creature is about 4 feet tall and has a shapely female body covered "
                        + "with bandage gear. Her face is completely obscured by a latex mask, but her big tits and her crotch are completely exposed. She has a large cock, "
                        + "which looks ready to burst if it wasn't tightly bound at the base. Past her heavy sack, you can see sex toys sticking out of both her pussy and ass.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format(
                        "%s shivers and moans as %s sinks into %s darkest fantasies. Something dangerous is coming. Sure enough a short feminine figure in bondage gear appears "
                                        + "before %s. Her face is completely obscured by a latex mask, but her big tits and her crotch are completely exposed. She has a large cock, "
                                        + "which looks ready to burst if it wasn't tightly bound at the base. Past her heavy sack, %s can see sex toys sticking out of both her pussy and ass.",
                        user.getName(), user.pronoun(), user.possessiveAdjective(),
                        target.nameDirectObject(), target.subject());
    }

}
