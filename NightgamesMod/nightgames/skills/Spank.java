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
import nightgames.status.Shamed;
import nightgames.status.Stsflag;

public class Spank extends Skill {

    public Spank() {
        super("Spank");
        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return !c.getStance().prone(target) && c.getStance().distance() <= 1 && !c.getStance().sub(user) && c.getStance().reachBottom(user) && user.canAct();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        double m = Random.random(6, 13);
        if (user.has(Trait.disciplinarian)) {
            boolean shamed = Random.random(10) >= 5 || !target.is(Stsflag.shamed) && user.canSpend(5);
            if (shamed) {
                user.spendMojo(c, 5);
            }
            writeOutput(c, Result.special, user, target);
            if (shamed) {
                target.add(c, new Shamed(target.getType()));
                target.emote(Emotion.angry, 10);
                target.emote(Emotion.nervous, 15);
            }
            if (target.has(Trait.achilles)) {
                m += 10;
            } else {
                m += 5;
            }
        } else {
            writeOutput(c, Result.normal, user, target);
        }
        target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, m));

        target.emote(Emotion.angry, 25);
        target.emote(Emotion.nervous, 15);
        target.loseMojo(c, 10);
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 8;
    }

    @Override
    public Skill copy(Character user) {
        return new Spank();
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return c.getStance().dom(user) ? 100 : 65;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to spank " + target.getName() + ", but "+target.pronoun()+" dodges away.";
        }
        if (modifier == Result.special) {
            return "You bend " + target.getName()
                            + " over your knee and spank "+target.directObject()+", alternating between hitting "+target.possessiveAdjective()+" soft butt cheek and "+target.possessiveAdjective()+" sensitive pussy.";
        } else {
            return "You spank " + target.getName() + " on "+target.possessiveAdjective()+" naked butt cheek.";
        }

    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s aims a slap at %s ass, but %s %s it.", user.subject(),
                            target.nameOrPossessivePronoun(), target.pronoun(),
                            target.action("dodge"));
        }
        if (modifier == Result.special) {
            String victim = target.hasBalls() ? "balls" : "clit";
            String hood = target.hasBalls() ? "manhood" : "womanhood";
            return String.format("%s bends %s over like a misbehaving child and spanks %s"
                            + " ass twice. The third spank aims lower and connects solidly with %s %s, "
                            + "injuring %s %s along with %s pride.", user.subject(),
                            target.nameDirectObject(), target.possessiveAdjective(),
                            target.possessiveAdjective(), victim, target.possessiveAdjective(),
                            hood, target.possessiveAdjective());
        } else {
            return String.format("%s lands a stinging slap on %s bare ass.",
                            user.subject(), target.nameOrPossessivePronoun());
        }

    }

    @Override
    public String describe(Combat c, Character user) {
        return "Slap opponent on the ass. Lowers Mojo.";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
