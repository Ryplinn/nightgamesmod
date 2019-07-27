package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class StripBottom extends Skill {

    private Clothing stripped, extra;

    StripBottom() {
        super("Strip Bottoms");

        addTag(SkillTag.positioning);
        addTag(SkillTag.stripping);
        addTag(SkillTag.weaken);
        addTag(SkillTag.staminaDamage);
    }

    @Override public boolean usable(Combat c, Character user, Character target) {
        return (c.getStance().oral(user, target) || c.getStance().reachBottom(user)) && !target.crotchAvailable()
                        && user.canAct() && !(target.has(ClothingTrait.harpoonDildo) || target.has(ClothingTrait.harpoonOnahole));
    }

    @Override public int getMojoCost(Combat c, Character user) {
        return c.getStance().dom(user) ? 2 : 10;
    }

    @Override public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        stripped = extra = null;
        int difficulty = target.getOutfit().getTopOfSlot(ClothingSlot.bottom).dc() + target.getLevel()
                        + (target.getStamina().percent() / 4 - target.getArousal().percent()) / 5 - (
                        !target.canAct() || c.getStance().sub(target) ? 20 : 0);
        if (user.checkVsDc(Attribute.cunning, difficulty) || !target.canAct()) {
            stripped = target.strip(ClothingSlot.bottom, c);
            boolean doubled = false;
            if (user.getAttribute(Attribute.cunning) >= 30 && !target.crotchAvailable() && user
                            .checkVsDc(Attribute.cunning, difficulty) || !target.canAct()) {
                extra = target.strip(ClothingSlot.bottom, c);
                doubled = true;
                writeOutput(c, Result.critical, user, target);
            } else {
                writeOutput(c, Result.normal, user, target);
            }
            if (user.human() && target.mostlyNude()) {
                c.write(target, target.nakedLiner(c, target));
            }
            if (target.human() && target.crotchAvailable() && target.hasDick()) {
                if (target.body.getRandomCock().isReady(target)) {
                    c.write(user, "Your boner springs out, no longer restrained by your pants.");
                } else {
                    c.write(user, user.getName() + " giggles as "+target.nameOrPossessivePronoun()
                    +" flaccid dick is exposed.");
                }
            }
            target.emote(Emotion.nervous, doubled ? 20 : 10);
        } else {
            stripped = target.outfit.getTopOfSlot(ClothingSlot.bottom);
            writeOutput(c, Result.miss, user, target);
            target.weaken(c, (int) DamageType.physical.modifyDamage(user, target, Random.random(8, 16)));
            return false;
        }
        return true;
    }

    @Override public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.cunning) >= 3;
    }

    @Override public int speed(Character user) {
        return 3;
    }

    @Override public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You grab " + target.getName() + "'s " + stripped.getName() + ", but " + target.pronoun()
                            + " scrambles away before you can strip " + target.directObject() + ".";
        } else {
            String msg = "After a brief struggle, you manage to pull off " + target.getName() + "'s " + stripped.getName()
                            + ".";
            if (modifier == Result.critical && extra != null) {
                msg += String.format(" Taking advantage of the situation, you also manage to snag %s %s!",
                                target.possessiveAdjective(), extra.getName());
            }
            return msg;
        }
    }

    @Override public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to pull down %s %s, but %s %s them up.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            stripped.getName(), target.pronoun(), target.action("hold"));
        } else {
            String msg = String.format("%s grabs the waistband of %s %s and pulls them down.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            stripped.getName());
            if (modifier == Result.critical && extra != null) {
                msg += String.format(" Before %s can react, %s also strips off %s %s!", target.subject(),
                                user.subject(), target.possessiveAdjective(), extra.getName());
            }
            return msg;
        }
    }

    @Override public String describe(Combat c, Character user) {
        return "Attempt to remove opponent's pants. More likely to succeed if she's weakened and aroused";
    }

    @Override public boolean makesContact() {
        return true;
    }
}
