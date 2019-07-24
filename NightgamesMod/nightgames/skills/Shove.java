package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Mount;
import nightgames.stance.Neutral;
import nightgames.stance.ReverseMount;
import nightgames.status.Falling;
import nightgames.status.Stsflag;

public class Shove extends Skill {
    public Shove() {
        super("Shove");
        addTag(SkillTag.positioning);
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.knockdown);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        if (target.hasStatus(Stsflag.cockbound)) {
            return false;
        }
        return !c.getStance().dom(user) && !c.getStance().prone(target) && c.getStance().reachTop(user)
                        && user.canAct() && !c.getStance().havingSex(c);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean success = true;
        if (user.get(Attribute.ki) >= 1 && target.getOutfit().slotShreddable(ClothingSlot.top)
                        && user.canSpend(5)) {
            writeOutput(c, Result.special, user, target);
            target.shred(ClothingSlot.top);
            target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random.random(10, 25)));
            if (user.checkVsDc(Attribute.power, target.knockdownDC() - user.get(Attribute.ki))) {
                c.setStance(new Neutral(user.getType(), c.getOpponent(user).getType()), user, true);
            }
        } else if (c.getStance().getClass() == Mount.class || c.getStance().getClass() == ReverseMount.class) {
            if (user.checkVsDc(Attribute.power, target.knockdownDC() + 5)) {
                if (user.human()) {
                    c.write(user, "You shove " + target.getName()
                                    + " off of you and get to your feet before she can retaliate.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s shoves %s hard enough to free %s and jump up.",
                                    user.subject(), target.nameDirectObject(), user.reflectivePronoun()));
                }
                c.setStance(new Neutral(user.getType(), c.getOpponent(user).getType()), user, true);
            } else {
                if (user.human()) {
                    c.write(user, "You push " + target.getName() + ", but you're unable to dislodge her.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s shoves %s weakly.", user.subject(),
                                    target.nameDirectObject()));
                }
                success = false;
            }
            target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random.random(8, 20)));
        } else {
            if (user.checkVsDc(Attribute.power, target.knockdownDC())) {
                if (user.human()) {
                    c.write(user, "You shove " + target.getName() + " hard enough to knock her flat on her back.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s knocks %s off balance and %s %s at her feet.",
                                    user.subject(), target.nameDirectObject(),
                                    target.pronoun(), target.action("fall")));
                }
                target.add(c, new Falling(target.getType()));
            } else {
                if (user.human()) {
                    c.write(user, "You shove " + target.getName() + " back a step, but she keeps her footing.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s pushes %s back, but %s %s able to maintain %s balance.",
                                    user.subject(), target.nameDirectObject(), target.pronoun(),
                                    target.action("are", "is"), target.possessiveAdjective()));
                }
                success = false;
            }
            target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random.random(16, 25)));
        }
        return success;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.power) >= 5;
    }

    @Override
    public Skill copy(Character user) {
        return new Shove();
    }

    @Override
    public int speed(Character user) {
        return 7;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (user.get(Attribute.ki) >= 1) {
            return "Shredding Palm";
        } else {
            return getName(c, user);
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You channel your ki into your hands and strike " + target.getName() + " in the chest, destroying her "
                        + target.getOutfit().getTopOfSlot(ClothingSlot.top).getName() + ".";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return String.format("%s strikes %s in the chest with %s palm, staggering %s footing. Suddenly %s "
                        + "%s tears and falls off %s in tatters.", user.subject(),
                        target.nameDirectObject(), user.possessiveAdjective(),
                        target.possessiveAdjective(), target.nameOrPossessivePronoun(),
                        target.getOutfit().getTopOfSlot(ClothingSlot.top).getName(),
                        target.directObject());
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Slightly damage opponent and try to knock her down";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
