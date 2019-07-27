package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class Squeeze extends Skill {

    public Squeeze() {
        super("Squeeze Balls");
        addTag(SkillTag.mean);
        addTag(SkillTag.hurt);
        addTag(SkillTag.positioning);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.hasBalls() && c.getStance().reachBottom(user) && user.canAct()
                        && !user.has(Trait.shy);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            double m = Random.random(10, 20);
            DamageType type = DamageType.physical;
            if (target.has(Trait.brassballs)) {
                if (target.human()) {
                    c.write(user, receive(c, 0, Result.weak2, user, target));
                } else if (user.human()) {
                    c.write(user, deal(c, 0, Result.weak2, user, target));
                }
                m = 0;
            } else if (target.crotchAvailable()) {
                if (user.has(Item.ShockGlove) && user.has(Item.Battery, 2)) {
                    user.consume(Item.Battery, 2);
                    if (target.human()) {
                        c.write(user, receive(c, 0, Result.special, user, target));
                    } else if (user.human()) {
                        c.write(user, deal(c, 0, Result.special, user, target));
                    }
                    type = DamageType.gadgets;
                    m += 15;
                    if (target.has(Trait.achilles)) {
                        m += 5;
                    }
                } else if (target.has(ClothingTrait.armored)) {
                    if (target.human()) {
                        c.write(user, receive(c, 0, Result.item, user, target));
                    } else if (user.human()) {
                        c.write(user, deal(c, 0, Result.item, user, target));
                    }
                    m *= .5;
                } else {
                    if (target.human()) {
                        c.write(user, receive(c, 0, Result.normal, user, target));
                    } else if (user.human()) {
                        c.write(user, deal(c, 0, Result.normal, user, target));
                    }
                    if (target.has(Trait.achilles)) {
                        m += 5;
                    }
                }
            } else {
                if (target.human()) {
                    c.write(user, receive(c, 0, Result.weak, user, target));
                } else if (user.human()) {
                    c.write(user, deal(c, 0, Result.weak, user, target));
                }
                m *= target.getExposure(ClothingSlot.bottom);
            }
            target.pain(c, user, (int) type.modifyDamage(user, target, m));

            target.emote(Emotion.angry, 15);
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 9 && user.getAttribute(Attribute.seduction) >= 9;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You try to grab " + target.getName() + "'s balls, but she avoids it.";
        } else if (modifier == Result.special) {
            return "You use your shock glove to deliver a painful jolt directly into " + target.getName()
                            + "'s testicles.";
        } else if (modifier == Result.weak) {
            return "You grab the bulge in " + target.getName() + "'s "
                            + target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName() + " and squeeze.";
        } else if (modifier == Result.weak2) {
            return "You grab " + target.getName() + "by the balls and squeeze hard, but" + target.pronoun()
                            + " does not flinch at all.";
        } else if (modifier == Result.item) {
            return "You grab the bulge in " + target.getName() + "'s "
                            + target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName()
                            + ", but find it solidly protected.";
        } else {
            return "You manage to grab " + target.getName()
                            + "'s balls and squeeze them hard. You feel a twinge of empathy when she cries out in pain, but you maintain your grip.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s grabs at %s balls, but misses.",
                            user.subject(), target.nameOrPossessivePronoun());
        } else if (modifier == Result.special) {
            return String.format("%s grabs %s naked balls roughly in %s gloved hand. A painful jolt "
                            + "of electricity shoots through %s groin, sapping %s will to fight.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(), target.possessiveAdjective(),
                            target.possessiveAdjective());
        } else if (modifier == Result.weak) {
            return String.format("%s grabs %s balls through %s %s and squeezes hard.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.possessiveAdjective(), 
                            target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName());
        } else if (modifier == Result.weak2) {
            return String.format("%s grins menacingly and firmly grabs %s nuts. %s squeezes as hard as "
                            + "%s can, but %s hardly %s it.", user.subject(),
                            target.nameOrPossessivePronoun(),
                            Formatter.capitalizeFirstLetter(user.subject()),
                            user.pronoun(), target.pronoun(), target.action("feel"));
        } else if (modifier == Result.item) {
            return String.format("%s grabs %s crotch through %s %s, but %s can barely feel it.",
                            user.subject(), target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            target.getOutfit().getTopOfSlot(ClothingSlot.bottom).getName(),
                            target.pronoun());
        } else {
            return String.format("%s reaches between %s legs and grabs %s exposed balls. %s "
                            + "in pain as %s pulls and squeezes them.", user.subject(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("writhe")),
                            user.subject());
        }
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.has(Item.ShockGlove)) {
            return "Shock Balls";
        } else {
            return getName(c, user);
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Grab opponent's groin; deals more damage if she's naked";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
