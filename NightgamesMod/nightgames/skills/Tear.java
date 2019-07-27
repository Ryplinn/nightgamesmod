package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.items.Item;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.items.clothing.ClothingTrait;
import nightgames.nskills.tags.SkillTag;

public class Tear extends Skill {

    public Tear() {
        super("Tear Clothes");
        addTag(SkillTag.stripping);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 32 || user.getAttribute(Attribute.animism) >= 12
                        || user.getAttribute(Attribute.medicine) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        boolean notMedical = user.getAttribute(Attribute.power) >= 32 || user.getAttribute(Attribute.animism) >= 12;
        return ((c.getStance().reachTop(user) && !target.breastsAvailable())
                        || ((c.getStance().reachBottom(user) && !target.crotchAvailable()))) && user.canAct()
                        && (notMedical || user.has(Item.MedicalSupplies, 1));
    }

    @Override
    public String describe(Combat c, Character user) {
        if (user.getAttribute(Attribute.medicine) >= 12 && user.has(Item.MedicalSupplies, 1)) {
            return "Dissect your opponent's clothing";
        }
        return "Rip off your opponent's clothes";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        boolean isMedical = (user.getAttribute(Attribute.medicine) >= 12 && user.has(Item.MedicalSupplies, 1));
        if (c.getStance().reachTop(user) && !target.getOutfit().slotEmpty(ClothingSlot.top)) {
            Clothing article = target.getOutfit().getTopOfSlot(ClothingSlot.top);
            if (isMedical && !article.is(ClothingTrait.indestructible)
                            && (((user.checkVsDc(Attribute.power,
                                            article.dc() + (target.getStamina().percent()
                                                            - (target.getArousal().percent()) / 4)
                                            + user.getAttribute(Attribute.medicine) * 4)) || !target.canAct()))) {
                if (user.human()) {
                    c.write(user,
                                    Formatter.format("Grabbing your scalpel, you jump forward. The sharp blade makes quick work of {other:possessive}} clothing and your skill with the blade allows you avoid harming them completely. {other:SUBJECT} can only look at you with shock as {other:possessive} shredded clothes float to the ground between you.",
                                                    user, target));
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user,
                                    Formatter.format("{self:SUBJECT} leaps forward. {self:POSSESSIVE} hand is a blur but {other:subject-action:spot|spots} the glint of steel in them. Reflexively, {other:pronoun-action:cover|covers} {other:reflective} with {other:possessive} arms to prevent as much damage as possible. When nothing happens {other:subject-action:open|opens} {other:possessive} eyes to see {self:subject} grinning at {other:direct-object}, a scalpel still in {self:possessive} hands. Looking down {other:pronoun-action:see|sees} that some of {other:possessive} clothes have been cut to ribbons!",
                                                    user, target));
                }
                target.shred(ClothingSlot.top);
                if (user.human() && target.mostlyNude()) {
                    c.write(user, target.nakedLiner(c, target));
                }
                user.consume(Item.MedicalSupplies, 1);
            } else if (!article.is(ClothingTrait.indestructible) && user.getAttribute(Attribute.animism) >= 12
                            && (user.checkVsDc(Attribute.power,
                                            article.dc() + (target.getStamina().percent()
                                                            - (target.getArousal().percent()) / 4)
                                            + user.getAttribute(Attribute.animism) * user.getArousal().percent() / 100)
                            || !target.canAct())) {
                if (user.human()) {
                    c.write(user, "You channel your animal spirit and shred " + target.getName() + "'s "
                                    + article.getName() + " with claws you don't actually have.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s lunges towards %s and rakes %s nails across %s %s, "
                                    + "shredding the garment. That shouldn't be possible. %s "
                                    + "nails are not that sharp, and if they were, %s surely wouldn't have gotten away unscathed.",
                                    user.subject(), target.nameDirectObject(), user.possessiveAdjective(),
                                    target.possessiveAdjective(), article.getName(),
                                    Formatter.capitalizeFirstLetter(user.pronoun()),
                                    target.nameDirectObject()));
                }
                target.shred(ClothingSlot.top);
                if (user.human() && target.mostlyNude()) {
                    c.write(user, target.nakedLiner(c, target));
                }
            } else if (!article.is(ClothingTrait.indestructible)
                            && user.checkVsDc(Attribute.power, article.dc()
                                            + (target.getStamina().percent() - target.getArousal().percent()) / 4)
                            || !target.canAct()) {
                if (user.human()) {
                    c.write(user, target.getName() + " yelps in surprise as you rip her " + article.getName()
                                    + " apart.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s violently rips %s %s off.",
                                    user.subject(), target.nameOrPossessivePronoun(), article.getName()));
                }
                target.shred(ClothingSlot.top);
                if (user.human() && target.mostlyNude()) {
                    c.write(target, target.nakedLiner(c, target));
                }
            } else if (isMedical) {
                if (user.human()) {
                    c.write(user, "You try to cut apart " + target.getName() + "'s " + article.getName()
                                    + ", but the material is more durable than you expected.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s tries to cut %s %s, but fails to remove them.",
                                    user.subject(), target.nameOrPossessivePronoun(), article.getName()));
                }
                user.consume(Item.MedicalSupplies, 1);
                return false;
            } else {
                if (user.human()) {
                    c.write(user, "You try to tear apart " + target.getName() + "'s " + article.getName()
                                    + ", but the material is more durable than you expected.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s yanks on %s %s, but fails to remove it.",
                                    user.subject(), target.nameOrPossessivePronoun(), article.getName()));
                }
            }
        } else if (!target.getOutfit().slotEmpty(ClothingSlot.bottom)) {
            Clothing article = target.getOutfit().getTopOfSlot(ClothingSlot.bottom);
            if (isMedical && !article.is(ClothingTrait.indestructible)
                          && ((user.checkVsDc(Attribute.power, article.dc() + (target.getStamina().percent() - (target.getArousal().percent()) / 4) + user.getAttribute(Attribute.medicine) * 4))
                            || !target.canAct())) {
                if (user.human()) {
                    c.write(user,
                                    Formatter.format("Grabbing your scalpel, you jump forward. The sharp blade makes quick work of {other:possessive} clothing and your skill with the blade allows you avoid harming them completely. {other:SUBJECT} can only look at you with shock as {other:possessive} shredded clothes float to the ground between you.",
                                                    user, target));
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user,
                                    Formatter.format("{self:SUBJECT} leaps forward. {self:possessive} hand is a blur but {other:subject-action:spot|spots} the glint of steel in them. Reflexively, {other:pronoun-action:cover|covers} {other:reflective} with {other:possessive} arms to prevent as much damage as possible. When nothing happens {other:subject-action:open|opens} {other:possessive} eyes to see {self:subject} grinning at {other:direct-object}, a scalpel still in {self:possessive} hands. Looking down {other:pronoun-action:see|sees} that some of {other:possessive} clothes have been cut to ribbons!",
                                                    user, target));
                }
                target.shred(ClothingSlot.bottom);
                if (user.human() && target.mostlyNude()) {
                    c.write(user, target.nakedLiner(c, target));
                }
                user.consume(Item.MedicalSupplies, 1);
            } else if (!article.is(ClothingTrait.indestructible) && user.getAttribute(Attribute.animism) >= 12
                            && (user.checkVsDc(Attribute.power,
                                            article.dc() + (target.getStamina().percent()
                                                            - (target.getArousal().percent()) / 4)
                                            + user.getAttribute(Attribute.animism) * user.getArousal().percent() / 100)
                            || !target.canAct())) {
                if (user.human()) {
                    c.write(user, "You channel your animal spirit and shred " + target.getName() + "'s "
                                    + article.getName() + " with claws you don't actually have.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s lunges towards %s and rakes %s nails across %s %s, "
                                    + "shredding the garment. That shouldn't be possible. %s "
                                    + "nails are not that sharp, and if they were, %s surely wouldn't have gotten away unscathed.",
                                    user.subject(), target.nameDirectObject(), user.possessiveAdjective(),
                                    target.possessiveAdjective(), article.getName(),
                                    Formatter.capitalizeFirstLetter(user.pronoun()),
                                    target.nameDirectObject()));
                }
                target.shred(ClothingSlot.bottom);
                if (user.human() && target.mostlyNude()) {
                    c.write(target, target.nakedLiner(c, target));
                }
                if (target.human() && target.crotchAvailable() && target.hasDick()) {
                    if (target.getArousal().get() >= 15) {
                        c.write(user, String.format("%s boner springs out, no longer restrained by %s pants.",
                                        target.nameOrPossessivePronoun(), target.possessiveAdjective()));
                    } else {
                        c.write(user, String.format("%s giggles as %s flaccid dick is exposed.",
                                        user.subject(), target.nameOrPossessivePronoun()));
                    }
                }
                target.emote(Emotion.nervous, 10);
            } else if (!article.is(ClothingTrait.indestructible)
                            && user.checkVsDc(Attribute.power, article.dc()
                                            + (target.getStamina().percent() - target.getArousal().percent()) / 4)
                            || !target.canAct()) {
                if (user.human()) {
                    c.write(user, target.getName() + " yelps in surprise as you rip her " + article.getName()
                                    + " apart.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s violently rips %s %s off.",
                                    user.subject(), target.nameOrPossessivePronoun(), article.getName()));
                }
                target.shred(ClothingSlot.bottom);
                if (user.human() && target.mostlyNude()) {
                    c.write(target, target.nakedLiner(c, target));
                }
                if (target.human() && target.crotchAvailable()) {
                    if (target.getArousal().get() >= 15) {
                        c.write(user, String.format("%s boner springs out, no longer restrained by %s pants.",
                                        target.nameOrPossessivePronoun(), target.possessiveAdjective()));
                    } else {
                        c.write(user, String.format("%s giggles as %s flaccid dick is exposed.",
                                        user.subject(), target.nameOrPossessivePronoun()));
                    }
                }
                target.emote(Emotion.nervous, 10);
            } else if (isMedical) {
                if (user.human()) {
                    c.write(user, "You try to cut apart " + target.getName() + "'s " + article.getName()
                                    + ", but the material is more durable than you expected.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s tries to cut %s %s, but fails to remove them.",
                                    user.subject(), target.nameOrPossessivePronoun(), article.getName()));
                }
                user.consume(Item.MedicalSupplies, 1);
                return false;
            } else {
                if (user.human()) {
                    c.write(user, "You try to tear apart " + target.getName() + "'s " + article.getName()
                                    + ", but the material is more durable than you expected.");
                } else if (c.shouldPrintReceive(target, c)) {
                    c.write(user, String.format("%s yanks on %s %s, but fails to remove it.",
                                    user.subject(), target.nameOrPossessivePronoun(), article.getName()));
                }
                return false;
            }
        }
        return true;
    }

    public String getLabel(Combat c, Character user) {
        if (user.getAttribute(Attribute.medicine) >= 12) {
            return "Clothing-ectomy";
        } else if (user.getAttribute(Attribute.animism) >= 12) {
            return "Shred Clothes";
        } else {
            return "Tear Clothes";
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.stripping;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return "";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
