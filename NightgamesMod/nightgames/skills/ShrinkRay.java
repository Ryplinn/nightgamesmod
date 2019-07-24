package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.body.CockPart;
import nightgames.characters.body.mods.SizeMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.Shamed;

@SuppressWarnings("unused")
public class ShrinkRay extends Skill {

    ShrinkRay() {
        super("Shrink Ray");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.science) >= 12;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().prone(user)
                        && target.mostlyNude() && user.has(Item.Battery, 2);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 2.f;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Shrink your opponent's 'assets' to damage her ego: 2 Batteries";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        user.consume(Item.Battery, 2);
        boolean permanent = Random.random(20) == 0 && (user.human() || target.human())
                        && !target.has(Trait.stableform);
        if (user.human()) {
            if (target.hasDick()) {
                c.write(user, deal(c, permanent ? 1 : 0, Result.special, user, target));
            } else {
                c.write(user, deal(c, permanent ? 1 : 0, Result.normal, user, target));
            }
        } else if (c.shouldPrintReceive(target, c)) {
            if (target.hasDick()) {
                c.write(user, receive(c, permanent ? 1 : 0, Result.special, user, target));
            } else {
                c.write(user, receive(c, permanent ? 1 : 0, Result.normal, user, target));
            }
        }
        target.add(c, new Shamed(target.getType()));
        if (permanent) {
            if (target.hasDick()) {
                CockPart part = target.body.getCockAbove(SizeMod.getMinimumSize("cock"));
                if (part != null) {
                    target.body.addReplace(part.downgrade(), 1);
                } else {
                    target.body.remove(target.body.getRandomCock());
                }
            } else {
                BreastsPart part = target.body.getBreastsAbove(BreastsPart.flat.getSize());
                if (part != null) {
                    target.body.addReplace(part.downgrade(), 1);
                }
            }
        } else {
            if (target.hasDick()) {
                CockPart part = target.body.getCockAbove(SizeMod.getMinimumSize("cock"));
                if (part != null) {
                    target.body.temporaryAddOrReplacePartWithType(part.downgrade(), part, 10);
                } else {
                    target.body.temporaryRemovePart(target.body.getRandom("cock"), 10);
                }
            } else {
                BreastsPart part = target.body.getBreastsAbove(BreastsPart.flat.getSize());
                if (part != null) {
                    target.body.temporaryAddOrReplacePartWithType(part.downgrade(), part, 10);
                }
            }
        }
        target.loseMojo(c, 50);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new ShrinkRay();
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        if (modifier == Result.special) {
            message = "You aim your shrink ray at " + target.getName()
                            + "'s cock, shrinking her male anatomy. She turns red and glares at you in humiliation.";
        } else {
            message = "You point your shrink ray to turn " + target.getName()
                            + "'s breasts. She whimpers and covers her chest in shame.";
        }
        if (damage > 0) {
            message += " She glares at you angrily when she realizes the effects are permanent!";
        }
        return message;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String message;
        if (modifier == Result.special) {
            message = String.format("%s points a device at %s groin and giggles as %s genitals "
                            + "shrink. %s in shame and %s %s.", user.subject(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("flush", "flushes")),
                            target.action("cover"), target.reflectivePronoun());
        } else {
            message = String.format("%s points a device at %s chest and giggles as %s %s"
                            + " shrink. %s in shame and %s %s.", user.subject(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            user.body.getRandomBreasts().describe(user),
                            Formatter.capitalizeFirstLetter(target.subjectAction("flush", "flushes")),
                            target.action("cover"), target.reflectivePronoun());
        }
        if (damage == 0) {
            message += String.format(" The effect wears off quickly, but the"
                            + " damage to %s dignity lasts much longer.", target.nameOrPossessivePronoun());
        } else {
            message += " You realize the effects are permanent!";
        }
        return message;
    }

}
