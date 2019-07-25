package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class StripTop extends Skill {

    private Clothing stripped, extra;
    
    StripTop() {
        super("Strip Top");

        addTag(SkillTag.positioning);
        addTag(SkillTag.stripping);
        addTag(SkillTag.weaken);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().reachTop(user) && !target.breastsAvailable() && user.canAct();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return c.getStance().dom(user) ? 2 : 10;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int difficulty = target.getOutfit().getTopOfSlot(ClothingSlot.top).dc() + target.getLevel()
                        + (target.getStamina().percent() / 5 - target.getArousal().percent()) / 4
                        - (!target.canAct() || c.getStance().sub(target) ? 20 : 0);
        if (user.checkVsDc(Attribute.cunning, difficulty) || !target.canAct()) {
            stripped = target.strip(ClothingSlot.top, c);
            boolean doubled = false;
            if (user.getAttribute(Attribute.cunning) >= 30 && !target.breastsAvailable()
                            && user.checkVsDc(Attribute.cunning, difficulty) || !target.canAct()) {
                extra = target.strip(ClothingSlot.top, c);
                doubled = true;
                writeOutput(c, Result.critical, user, target);
            } else {
                writeOutput(c, Result.normal, user, target);
            }
            if (user.human() && target.mostlyNude()) {
                c.write(target, target.nakedLiner(c, target));
            }
            target.emote(Emotion.nervous, doubled ? 20 : 10);
        } else {
            stripped = target.outfit.getTopOfSlot(ClothingSlot.top);
            writeOutput(c, Result.miss, user, target);
            target.weaken(c, (int) DamageType.physical.modifyDamage(user, target, Random.random(8, 16)));
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.cunning) >= 3;
    }

    @Override
    public int speed(Character user) {
        return 3;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You attempt to strip off " + target.getName() + "'s "
                            + stripped.getName()
                            + ", but she shoves you away.";
        } else {
            String msg = "After a brief struggle, you manage to pull off " + target.getName() + "'s "
                            + stripped.getName() + ".";
            if (modifier == Result.critical && extra != null) {
                msg += String.format(" Taking advantage of the situation, you also"
                                + " manage to snag %s %s!", target.possessiveAdjective(), extra.getName());
            }
            return msg;
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to yank off %s %s, but %s %s to hang onto it.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            stripped.getName(), target.pronoun(), target.action("manage"));
        } else {
            String msg = String.format("%s grabs a hold of %s %s and yanks it off before %s can stop %s.",
                            user.subject(), target.nameOrPossessivePronoun(), stripped.getName(),
                            target.pronoun(), user.directObject());
            if (modifier == Result.critical && extra != null) {
                msg += String.format(" Before %s can react, %s also strips off %s %s!", 
                                target.subject(), user.subject(),
                                target.possessiveAdjective(), extra.getName());
            }
            return msg;
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Attempt to remove opponent's top. More likely to succeed if she's weakened and aroused";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
