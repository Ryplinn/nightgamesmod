package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;

public class Defabricator extends Skill {

    public Defabricator() {
        super("Defabricator");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.science) >= 18;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && !target.mostlyNude() && user.has(Item.Battery, 8);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Does what it says on the tin.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.consume(Item.Battery, 8);
        writeOutput(c, Result.normal, user, target);
        if (user.human() || c.isBeingObserved())
            c.write(target, target.nakedLiner(c, target));
        target.nudify();
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You charge up your Defabricator and point it in " + target.getName()
                        + "'s general direction. A bright light engulfs her and her clothes are disintegrated in moment.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s points a device at %s and light shines from it like it's a simple flashlight. "
                        + "The device's function is immediately revealed as %s clothes just vanish "
                        + "in the light. %s left naked in seconds.", user.subject(),
                        target.nameDirectObject(), target.possessiveAdjective(), 
                        Formatter.capitalizeFirstLetter(target.subjectAction("are", "is")));
    }

}
