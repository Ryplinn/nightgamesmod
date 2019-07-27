package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.arms.skills.Grab;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Neutral;
import nightgames.stance.Position;
import nightgames.stance.Stance;
import nightgames.status.*;
import nightgames.status.Compulsive.Situation;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class Struggle extends Skill {

    public Struggle() {
        super("Struggle");
        addTag(SkillTag.positioning);
        addTag(SkillTag.escaping);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        if (!user.canRespond()) {
            return false;
        }
        if (target.hasStatus(Stsflag.cockbound) || target.hasStatus(Stsflag.knotted)) {
            return false;
        }
        if (user.hasStatus(Stsflag.cockbound) || user.hasStatus(Stsflag.knotted)) {
            return user.canRespond();
        }
        return ((!c.getStance().mobile(user) && !c.getStance().dom(user) || user.bound()
                        || (user.is(Stsflag.maglocked) && !user.is(Stsflag.hogtied)))
                        || hasSingleGrabber(c, user))
                        && user.canRespond();
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (blockedByCollar(c, user)) {
            return false;
        }
        if (user.is(Stsflag.maglocked)) {
            return struggleMagLock(c, user, target);
        } else if (hasSingleGrabber(c, user)) {
            return struggleGrabber(c, user, target);
        } else if (user.bound()) {
            return struggleBound(c, user, target);
        } else if (c.getStance().havingSex(c)) {
            boolean knotted = user.hasStatus(Stsflag.knotted);
            if (c.getStance().enumerate() == Stance.anal) {
                return struggleAnal(c, user, target, knotted);
            } else {
                return struggleVaginal(c, user, knotted);
            }
        } else {
            return struggleRegular(c, user, target);
        }
    }
    
    private boolean hasSingleGrabber(Combat c, Character user) {
        return c.getCombatantData(user).getIntegerFlag(Grab.FLAG) == 1;
    }
    
    private boolean blockedByCollar(Combat c, Character user) {
        Optional<String> compulsion = Compulsive.describe(c, user, Situation.PREVENT_STRUGGLE);
        if (compulsion.isPresent()) {
            c.write(user, compulsion.get());
            user.pain(c, null, 20 + Random.random(40));
            Compulsive.doPostCompulsion(c, user, Situation.PREVENT_STRUGGLE);
            return true;
        }
        return false;
    }

    private boolean struggleBound(Combat c, Character user, Character target) {
        Bound status = (Bound) target.getStatus(Stsflag.bound);
        if (user.checkVsDc(Attribute.power, -user.getEscape(c, target))) {
            if (user.human()) {
                if (status != null) {
                    c.write(user, "You manage to break free from the " + status + ".");
                } else {
                    c.write(user, "You manage to snap the restraints that are binding your hands.");
                }
            } else if (c.shouldPrintReceive(target, c)) {
                if (status != null) {
                    c.write(user, user.getName() + " slips free from the " + status + ".");
                } else {
                    c.write(user, user.getName() + " breaks free.");
                }
            }
            user.free();
            c.getCombatantData(target).setIntegerFlag(Grab.FLAG, 0);
            return true;
        } else {
            if (user.human()) {
                if (status != null) {
                    c.write(user, "You struggle against the " + status + ", but can't get free.");
                } else {
                    c.write(user, "You struggle against your restraints, but can't get free.");
                }
            } else if (c.shouldPrintReceive(target, c)) {
                if (status != null) {
                    c.write(user, user.getName() + " struggles against the " + status
                                    + ", but can't free her hands.");
                } else {
                    c.write(user, user.getName() + " struggles, but can't free her hands.");
                }
            }
            user.struggle();
            return false;
        }
    }

    private boolean struggleAnal(Combat c, Character user, Character target, boolean knotted) {
        int diffMod = knotted ? 50 : 0;
        if (target.has(Trait.grappler))
            diffMod += 15;
        if (user.checkVsDc(Attribute.power,
                        target.getStamina().get() / 2 - user.getStamina().get() / 2
                                        + target.getAttribute(Attribute.power) - user.getAttribute(Attribute.power)
                                        - user.getEscape(c, target) + diffMod)) {
            if (c.getStance().reversable(c)) {
                c.setStance(c.getStance().reverse(c, true));
            } else if (user.human()) {
                if (knotted) {
                    c.write(user, "With a herculean effort, you painfully force "
                                    + target.possessiveAdjective()
                                    + " knot through your asshole, and the rest of her dick soon follows.");
                    user.removeStatus(Stsflag.knotted);
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, 10));
                } else {
                    c.write(user, "You manage to break away from " + target.getName() + ".");
                }
                c.setStance(new Neutral(user.getType(), c.getOpponent(user).getType()));
            } else if (c.shouldPrintReceive(target, c)) {
                if (knotted) {
                    c.write(user, String.format("%s roughly pulls away from %s, groaning loudly"
                                    + " as the knot in %s dick pops free of %s ass.", user.subject(),
                                    target.nameDirectObject(), target.possessiveAdjective(),
                                    user.possessiveAdjective()));
                    user.removeStatus(Stsflag.knotted);
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, 10));
                } else {
                    c.write(user, String.format("%s pulls away from %s and"
                                    + " %s dick slides out of %s butt.",
                                    user.subject(), target.nameDirectObject(),
                                    target.possessiveAdjective(), user.possessiveAdjective()));
                }
                c.setStance(new Neutral(user.getType(), c.getOpponent(user).getType()));
            }
            return true;
        } else {
            user.struggle();
            c.struggle(user, c.getStance());
            return false;
        }
    }

    private boolean struggleVaginal(Combat c, Character user, boolean knotted) {
        int diffMod = 0;
        Character partner;
        if (c.getStance().sub(user)) {
            partner = c.getStance().getDomSexCharacter();
        } else {
            partner = c.getStance().getBottom();
        }
        Character target = partner;
        if (c.getStance().insertedPartFor(c, target).moddedPartCountsAs(target, CockMod.enlightened)) {
            diffMod = 15;
        } else if (c.getStance().insertedPartFor(c, user).moddedPartCountsAs(user, CockMod.enlightened)) {
            diffMod = -15;
        }
        if (target.has(Trait.grappler)) {
            diffMod += 15;
        }
        if (user.checkVsDc(Attribute.power,
                        target.getStamina().get() / 2 - user.getStamina().get() / 2
                                        + target.getAttribute(Attribute.power) - user.getAttribute(Attribute.power)
                                        - user.getEscape(c, target) + diffMod)) {
            if (user.hasStatus(Stsflag.cockbound)) {
                CockBound s = (CockBound) user.getStatus(Stsflag.cockbound);
                c.write(user,
                                Formatter.format("With a strong pull, {self:subject} somehow managed to wiggle out of {other:possessive} iron grip on {self:possessive} dick. "
                                                + "However the sensations of " + s.binding
                                                + " sliding against {self:possessive} cockskin leaves {self:direct-object} gasping.",
                                user, target));
                int m = 15;
                user.body.pleasure(target, target.body.getRandom("pussy"),
                                user.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
                user.removeStatus(Stsflag.cockbound);
            }
            if (knotted) {
                c.write(user,
                                Formatter.format("{self:subject} somehow {self:SUBJECT-ACTION:manage|manages} to force {other:possessive} knot through {self:possessive} tight opening, stretching it painfully in the process.",
                                                user, target));
                user.removeStatus(Stsflag.knotted);
                user.pain(c, user, 10);
            }
            boolean reverseStrapped = BodyPart.hasOnlyType(c.getStance().getPartsFor(c, target, user), "strapon");
            boolean reversedStance = false;
            if (!reverseStrapped) {
                Position reversed = c.getStance().reverse(c, true);
                if (reversed != c.getStance()) {
                    c.setStance(reversed);
                    reversedStance = true;
                }
            }
            if (!reversedStance) {
                c.write(user,
                                Formatter.format("{self:SUBJECT-ACTION:manage|manages} to shake {other:direct-object} off.",
                                                user, target));
                c.setStance(new Neutral(user.getType(), c.getOpponent(user).getType()));
            }
            return true;
        } else {
            user.struggle();
            c.struggle(user, c.getStance());
            return false;
        }
    }

    private boolean struggleRegular(Combat c, Character user, Character target) {
        int difficulty = target.getStamina().get() / 2 - user.getStamina().get() / 2
                        + target.getAttribute(Attribute.power) - user.getAttribute(Attribute.power)
                        - user.getEscape(c, target);
        if (target.has(Trait.powerfulcheeks)) {
            difficulty += 5;
        }
        if (target.has(Trait.bewitchingbottom)) {
            difficulty += 5;
        }
        if (user.checkVsDc(Attribute.power, difficulty)
                        && (!target.has(Trait.grappler) || Random.random(10) >= 2)) {
            if (user.human()) {
                c.write(user, "You manage to scrabble out of " + target.getName() + "'s grip.");
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, user.getName() + " squirms out from under "+target.nameDirectObject()+".");
            }
            c.setStance(new Neutral(user.getType(), c.getOpponent(user).getType()));
            return true;
        } else {
            user.struggle();
            c.struggle(user, c.getStance());
            return false;
        }
    }
    
    private boolean struggleMagLock(Combat c, Character user, Character target) {
        MagLocked stat = (MagLocked) user.getStatus(Stsflag.maglocked);
        
        Attribute highestAdvancedAttr = null;
        int attrLevel = 0;
        for (Map.Entry<Attribute, Integer> ent : user.att.entrySet()) {
            Attribute attr = ent.getKey();
            if (attr == Attribute.power || attr == Attribute.seduction || attr == Attribute.cunning) {
                continue;
            }
            if (ent.getValue() > attrLevel) {
                highestAdvancedAttr = attr;
                attrLevel = ent.getValue();
            }
        }
        
        boolean basic = highestAdvancedAttr == null;
        int dc;
        
        if (basic) {
           attrLevel = Math.max(user.getAttribute(Attribute.power),
                           Math.max(user.getAttribute(Attribute.seduction),
                                           user.getAttribute(Attribute.cunning))) / 2;
        }
        dc = attrLevel + Random.random(-10, 20);
        
        // One MagLock, pretty easy to remove
        if (stat.getCount() == 1) {
            if (target.checkVsDc(Attribute.science, dc / 2)) {
                c.write(user, Formatter.format("Still having one hand completely free, it's not to"
                            + " difficult for {self:subject} to remove the lone MagLock"
                            + " {other:subject} had placed around {self:possessive} wrist.", user, target));
                user.removeStatus(stat);
                return true;
            } else {
                c.write(user, Formatter.format("{self:SUBJECT-ACTION:pull|pulls} at the MagLock around"
                                + " {self:possessive} wrist, but it's not budging.", user, target));
            }
        } else {
            if (stat.getCount() != 2) {
                // Three MagLocks? Shouldn't be able to struggle if that's the case...
                c.write("ERROR: Something went wrong with the MagLocks...");
                return false;
            }
            // Two MagLocks, difficult to remove
            if (target.checkVsDc(Attribute.science, dc)) {
                String msg = "{self:SUBJECT-ACTION:struggle|struggles} against the powerful"
                                + " MagLocks locked around {self:possessive} wrists by ";
                if (Arrays.asList(Attribute.darkness, Attribute.spellcasting, Attribute.temporal, Attribute.divinity)
                                .contains(highestAdvancedAttr)) {
                    msg += "trying to pry them of with {self:possessive} magic";
                } else if (Arrays.asList(Attribute.power,
                                Attribute.ki, Attribute.ninjutsu, Attribute.animism, Attribute.nymphomania)
                                .contains(highestAdvancedAttr)) {
                    msg += "applying brute force with {self:possessive} powerful muscles";
                } else if (Arrays.asList(Attribute.cunning, Attribute.science, Attribute.hypnotism)
                                .contains(highestAdvancedAttr)) {
                    msg += "finding and exploiting a weakness in their design";
                } else {
                    msg += "twisting and turning {self:possessive} hands as much as possible"
                                    + " while attempting to force them apart";
                }
                msg += ", and eventually succeeds. The two bands drop to the ground and power down.";
                if (!target.human()) {
                    msg += " {other:SUBJECT} seems very surprised that {self:subject} was able to escape.";
                }
                c.write(user, Formatter.format(msg, user, target));
                user.removeStatus(stat);
                return true;
            } else {
                c.write(user, Formatter.format("{self:SUBJECT-ACTION:struggle|struggles} against the"
                                + " MagLocks around {self:possessive} wrist, but {self:action:prove|proves}"
                                + " no match for their insanely strong attraction.", user, target));
            }
        }
        return false;
    }
    
    private boolean struggleGrabber(Combat c, Character user, Character target) {
        int baseResist = Math.min(90, 40 + target.getAttribute(Attribute.science));
        int trueResist = Math.max(20, baseResist) - user.getAttribute(Attribute.science) / 2
                                                  - user.getAttribute(Attribute.power) / 3
                                                  - user.getAttribute(Attribute.cunning) / 3;
        if (Random.random(100) > trueResist) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:wrench|wrenches}"
                            + " {other:name-possessive} Grabber off {self:possessive}"
                            + " wrist without too much trouble.", user, target));
            c.getCombatantData(user).setIntegerFlag(Grab.FLAG, 0);
            return true;
        } else {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:pull|pulls} mightily"
                            + " on the Grabber around {self:possessive} wrist, but"
                            + " {self:action:fail|fails} to remove it.", user, target));
        }
        return false;
    }
    
    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 3;
    }

    @Override
    public int speed(Character user) {
        return 0;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.positioning;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Attempt to escape a submissive position using Power";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
