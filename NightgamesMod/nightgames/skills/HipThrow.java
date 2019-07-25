package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.status.Falling;

public class HipThrow extends Skill {

    HipThrow() {
        super("Hip Throw");
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.judonovice);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !target.wary() && c.getStance().mobile(user) && c.getStance().mobile(target)
                        && !c.getStance().prone(user) && !c.getStance().prone(target) && user.canAct()
                        && !c.getStance().connected(c);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (user.checkVsDc(Attribute.power, target.knockdownDC() - target.getAttribute(Attribute.cunning) / 2)) {
            writeOutput(c, Result.normal, user, target);
            target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random.random(10, 16)));
            target.add(c, new Falling(target.getType()));
            target.emote(Emotion.angry, 5);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return target.getName()
                            + " rushes toward you, but you step in close and pull her towards you, using her momentum to throw her across your hip and onto the floor.";
        } else {
            return "As " + target.getName()
                            + " advances, you pull her towards you and attempt to throw her over your hip, but she steps away from the throw and manages to keep her footing.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.normal) {
            return String.format("%s a momentary weakness in %s guard and %s toward %s to "
                            + "take advantage of it. The next thing %s, %s %s "
                            + "hitting the floor behind %s.",
                            user.subjectAction("see"), target.nameOrPossessivePronoun(),
                            user.action("lunge"), target.directObject(),
                            user.subjectAction("know"), user.pronoun(),
                            user.action("are", "is"), target.directObject());
        } else {
            return String.format("%s grabs %s arm and pulls %s off balance, but %s %s"
                            + " to plant %s foot behind %s leg sweep. This gives %s a more"
                            + " stable stance than %s and %s has "
                            + "to break away to stay on %s feet.", user.subject(),
                            target.nameOrPossessivePronoun(), target.directObject(),
                            target.pronoun(), target.action("manage"), target.possessiveAdjective(),
                            user.possessiveAdjective(), target.nameDirectObject(),
                            user.nameDirectObject(), user.pronoun(),
                            user.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Throw your opponent to the ground, dealing some damage: 10 Mojo";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
