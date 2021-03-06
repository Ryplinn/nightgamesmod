package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.custom.CharacterLine;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.Satiated;

public class LevelDrain extends Drain {
    public LevelDrain() {
        super("Level Drain");

        addTag(SkillTag.drain);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.fucking);
        addTag(SkillTag.dark);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.darkness) >= 20 && !user.has(Trait.leveldrainer);
        //The second clause may seem incorrect, but it isn't. Characters with this
        //trait drain levels passively and cannot also use this skill.
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().canthrust(c, user) && c.getStance().havingSexNoStrapped(c)
                        && user.getLevel() < 100 && user.getLevel() < target.getLevel();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Drain your opponent of their levels";
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 60;
    }

    private int stealXP(Character user, Character target) {
        int xpStolen = target.getXP();
        if (xpStolen <= 0) {
            return 0;
        }
        target.loseXP(xpStolen);
        user.gainXPPure(xpStolen);
        user.spendXP();
        return xpStolen;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 5.0f;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int type = Random.centeredrandom(2, user.getAttribute(Attribute.darkness) / 20.0f, 2);
        writeOutput(c, type, Result.normal, user, target);
        switch (type) {
            case 0:
                user.arouse(user.getArousal().max(), c);
                break;
            case 1:
                int stolen = stealXP(user, target);
                if (stolen > 0) {
                    user.add(c, new Satiated(user.getType(), stolen, 0));
                    if (user.human()) {
                        c.write(user, "You have absorbed " + stolen + " XP from " + target.getName() + "!\n");
                    } else {
                        c.write(user, user.getName() + " has absorbed " + stolen + " XP from you!\n");
                    }
                }
                break;
            case 2:
                int xpStolen = 95 + 5 * target.getLevel();
                user.add(c, new Satiated(user.getType(), xpStolen, 0));
                c.write(target, target.dong());
                if (user.human()) {
                    c.write(user, "You have stolen a level from " + target.getName() + "'s levels and absorbed it as " + xpStolen
                                    + " XP!\n");
                } else {
                    c.write(user, user.getName() + " has stolen a level from "+target.subject()+" and absorbed it as " + xpStolen
                                    + " XP!\n");
                }
                user.gainXPPure(xpStolen);
                user.spendXP();
                target.arouse(target.getArousal().max(), c);
                String levelDrainLine = user.getRandomLineFor(CharacterLine.LEVEL_DRAIN_LINER, c, target);
                if (!levelDrainLine.isEmpty()) {
                    c.write(user, levelDrainLine);
                }
                break;
            default:
                break;
        }
        return type != 0;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (user.hasPussy()) {
            String base = "You put your powerful vaginal muscles to work whilst" + " transfixing " + target.getName()
                            + "'s gaze with your own, goading his energy into his cock." + " Soon it erupts from him, ";
            switch (damage) {
                case 0:
                    return base + "but unfortunately you made a mistake, and the feedback leaves"
                                    + " you on the edge of climax!";
                case 1:
                    return base + "and you can feel his memories and experiences flow"
                                    + " into you, adding to your skill.";
                case 2:
                    return base + "far more powerfully than you even thought possible."
                                    + " You feel a fragment of his soul break away from him and"
                                    + " spew into you, taking with it a portion of his very being"
                                    + "and merging with your own. You have clearly"
                                    + " won this fight, and a lot more than that.";
                default:
                    // Should never happen
                    return " but nothing happens, you feel strangely impotent.";
            }
        } else {
            String base = "With your cock deep inside " + target.getName()
                            + ", you can feel the heat from her core. You draw the energy from her, mining her depths. ";
            switch (damage) {
                case 0:
                    return "You attempt to drain " + target.getName()
                                    + "'s energy through your intimate connection, but it goes wrong. You feel intense pleasure feeding "
                                    + "back into you and threatening to overwhelm you. You brink the spiritual link as fast as you can, but you're still left on the brink of "
                                    + "climax.";
                case 1:
                    return "You attempt to drain " + target.getName()
                                    + "'s energy through your intimate connection, taking a bit of her experience.";
                case 2:
                    return base + "You succeed in siphoning off a portion of her soul, stealing a portion of her very being. This energy permanently "
                                    + "settles within you!";
                default:
                    // Should never happen
                    return " but nothing happens, you feel strangely impotent.";
            }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String demon = user.useFemalePronouns() ? "succubus" : "incubus";
        
        String base = String.format("%s the %s' pussy suddenly tighten around %s. "
                        + "%s starts kneading %s dick, bringing %s immense pleasure and soon"
                        + " %s %s %s erupt into %s, but %s %s %s %s shooting"
                        + " something far more precious than semen into %s; as more of the ethereal"
                        + " fluid leaves %s, %s ",
                        target.subjectAction("feel"), demon, target.directObject(),
                        user.subject(), target.possessiveAdjective(), target.directObject(),
                        target.subject(), target.action("feel"), target.reflectivePronoun(),
                        user.directObject(), target.pronoun(), target.action("realize"),
                        target.pronoun(), target.action("are", "is"), user.nameDirectObject(),
                        target.directObject(), target.subjectAction("feel"));
        switch (damage) {
            case 0:
                return String.format("%s squeezes %s with %s pussy and starts to milk %s, "
                                + "but %s suddenly %s %s shudder and moan loudly. "
                                + "Looks like %s plan backfired.", user.subject(),
                                target.nameDirectObject(), user.possessiveAdjective(),
                                target.directObject(), target.pronoun(), target.action("feel"),
                                user.directObject(), user.possessiveAdjective());
            case 1:
                return base + String.format("%s experiences and memories escape %s mind and flowing into %s.",
                                target.possessiveAdjective(), target.possessiveAdjective(), user.directObject());
            case 2:
                return base + String.format("%s very being snap loose inside of %s and it seems to flow right "
                                + "through %s dick and into %s. When it is over %s... empty "
                                + "somehow. At the same time, %s seems radiant, looking more powerful,"
                                + " smarter and even more seductive than before. Through all of this,"
                                + " %s has kept on thrusting and %s right on the edge of climax."
                                + " %s defeat appears imminent, but %s %s already lost something"
                                + " far more valuable than a simple sex fight...",
                                target.possessiveAdjective(), target.directObject(), target.possessiveAdjective(),
                                user.subject(), target.subjectAction("feel"), user.subject(),
                                user.pronoun(), target.subjectAction("are", "is"),
                                Formatter.capitalizeFirstLetter(target.possessiveAdjective()),
                                target.pronoun(), target.action("have", "has"));
            default:
                // Should never happen
                return " nothing. You should be feeling something, but you're not.";
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
