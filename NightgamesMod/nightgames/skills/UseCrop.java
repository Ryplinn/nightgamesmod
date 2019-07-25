package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class UseCrop extends Skill {

    UseCrop() {
        super(Item.Crop.getName());
        addTag(SkillTag.usesToy);
        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return (user.has(Item.Crop) || user.has(Item.Crop2)) && user.canAct()
                        && c.getStance().mobile(user)
                        && (c.getStance().reachTop(user) || c.getStance().reachBottom(user));
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 10;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            double m = Random.random(12, 18);
            if (target.crotchAvailable() && c.getStance().reachBottom(user)) {
                if (user.has(Item.Crop2) && Random.random(10) > 7 && !target.has(Trait.brassballs)) {
                    writeOutput(c, Result.critical, user, target);
                    if (target.has(Trait.achilles)) {
                        m += 6;
                    }
                    target.emote(Emotion.angry, 10);
                    m += 8;
                } else {
                    writeOutput(c, Result.normal, user, target);
                    target.pain(c, user, 5 + Random.random(12) + target.get(Attribute.perception) / 2);
                }
            } else {
                writeOutput(c, Result.weak, user, target);
                m -= Random.random(2, 6);
                target.pain(c, user, 5 + Random.random(12));
            }
            target.pain(c, user, (int) DamageType.gadgets.modifyDamage(user, target, m));
            target.emote(Emotion.angry, 15);
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
        if (modifier == Result.miss) {
            if (!target.has(Item.Crop)) {
                return "You lash out with your riding crop, but it fails to connect.";
            } else {
                return "You try to hit " + target.getName() + " with your riding crop, but she deflects it with her own.";
            }
        } else if (modifier == Result.critical) {
            if (target.hasBalls()) {
                return "You strike " + target.getName()
                                + "'s bare ass with your crop and the 'Treasure Hunter' attachment slips between her legs, hitting one of her hanging testicles "
                                + "squarely. She lets out a shriek and clutches her sore nut";
            } else {
                return "You strike " + target.getName()
                                + "'s bare ass with your crop and the 'Treasure Hunter' attachment slips between her legs, impacting on her sensitive pearl. She "
                                + "lets out a high pitched yelp and clutches her injured anatomy.";
            }
        } else if (modifier == Result.weak) {
            return "You hit " + target.getName() + " with your riding crop.";
        } else {
            return "You strike " + target.getName()
                            + "'s soft, bare skin with your riding crop, leaving a visible red mark.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            if (!target.has(Item.Crop)) {
                return String.format("%s out of the way, as %s swings %s riding crop at %s.",
                                target.subjectAction("duck"), user.subject(),
                                user.possessiveAdjective(), target.directObject());
            } else {
                return String.format("%s swings %s riding crop, but %s %s own crop and %s it.",
                                user.subject(), user.possessiveAdjective(),
                                target.subjectAction("draw"), target.possessiveAdjective(),
                                target.action("parry", "parries"));
            }
        } else if (modifier == Result.critical) {
            return String.format("%s hits %s on the ass with %s riding crop. "
                            + "The attachment on the end delivers a painful sting to "
                            + "%s jewels. %s in pain and %s the urge to "
                            + "curl up in the fetal position.", user.subject(),
                            target.nameDirectObject(), user.possessiveAdjective(),
                            target.possessiveAdjective(), target.subjectAction("groan"),
                            target.action("fight"));
        } else if (modifier == Result.weak) {
            return String.format("%s strikes %s with a riding crop.",
                            user.subject(), target.nameDirectObject());
        } else {
            return String.format("%s hits %s bare ass with a riding crop hard enough to leave a painful welt.",
                            user.subject(), target.nameOrPossessivePronoun());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Strike your opponent with riding crop. More effective if she's naked";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
