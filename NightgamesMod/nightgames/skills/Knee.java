package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class Knee extends Skill {

    public Knee() {
        super("Knee");
        addTag(SkillTag.mean);
        addTag(SkillTag.hurt);
        addTag(SkillTag.positioning);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().mobile(user) && !c.getStance().prone(user) && user.canAct()
                        && c.getStance().front(target) && !c.getStance().connected(c);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            double m = Random.random(40, 60);
            if (user.human()) {
                c.write(user, deal(c, 0, Result.normal, user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                if (c.getStance().prone(target)) {
                    c.write(user, receive(c, 0, Result.special, user, target));
                } else {
                    c.write(user, receive(c, 0, Result.normal, user, target));
                }
                if (target.hasBalls() && Random.random(5) >= 3) {
                    c.write(user, user.bbLiner(c, target));
                }
            }
            if (target.has(Trait.achilles) && !target.has(ClothingTrait.armored)) {
                m += Random.random(16,20);
            }
            if (target.has(ClothingTrait.armored) || target.has(Trait.brassballs)) {
                m *= .75;
            }
            target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, m));

            target.emote(Emotion.angry, 20);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.power) >= 10;
    }

    @Override
    public int speed(Character user) {
        return 4;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return target.getName() + " blocks your knee strike.";
        }
        return "You deliver a powerful knee strike to " + target.getName()
                        + "'s delicate lady flower. She lets out a pained whimper and nurses her injured parts.";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String victim = target.hasBalls() ? "balls" : "crotch";
        if (modifier == Result.miss) {
            return String.format("%s tries to knee %s in the %s, but %s %s it.",
                            user.subject(), target.nameDirectObject(),
                            victim, target.pronoun(),
                                            target.action("block"));
        }
        if (modifier == Result.special) {
            return String.format("%s raises one leg into the air, then brings %s knee "
                            + "down like a hammer onto %s %s. %s"
                            + " out in pain and instinctively try "
                            + "to close %s legs, but %s holds them open.",
                            user.subject(), user.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), victim,
                            Formatter.capitalizeFirstLetter(target.subjectAction("cry", "cries")),
                            target.possessiveAdjective(), user.subject());
        } else {
            return String.format("%s steps in close and brings %s knee up between %s legs, "
                            + "crushing %s fragile balls. %s and nearly %s from the "
                            + "intense pain in %s abdomen.", user.subject(),
                            user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("groan")),
                            target.action("collapse"), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Knee opponent in the groin";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
