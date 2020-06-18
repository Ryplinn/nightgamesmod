package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.gui.GUI;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ThrowSlime extends Skill {
    public ThrowSlime() {
        super("Throw Slime", 4);
        addTag(SkillTag.knockdown);
    }

    @Override public Set<SkillTag> getTags(Combat c, Character user, Character target) {
        Set<SkillTag> tags = new HashSet<>(super.getTags(c, user, target));
        if (user.getAttribute(Attribute.slime) >= 12) {
            tags.add(SkillTag.mental);
        }
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.slime) > 0;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && (c.getStance().en == Stance.neutral
                        || (c.getStance().en == Stance.standingover && c.getStance().dom(user)))
                        && !user.is(Stsflag.charmed);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 9 + user.getAttribute(Attribute.slime);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Throw some globs of slime at your opponent. Unlocks more effects with higher Slime attribute.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (target.has(Trait.slime)) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:throw|throws} a glob of slime at"
                            + " {other:name-do}, but it is simply absorbed into {other:possessive}"
                            + " equally slimy body. That was rather underwhelming.", user, target));
            return false;
        } else {
            HitType type = decideEffect(c, user, target);
            c.write(Formatter.format("With a large movement of {self:possessive} arms, {self:subject-action:throw|throws}"
                            + " a big glob of viscous slime at {other:name-do}. ", user, target));
            type.message(c, user, target);
            if (type != HitType.NONE) {
                target.add(c, type.build(user, target));
                if (user.has(Trait.VolatileSubstrate)) {
                    target.add(c, new Slimed(target.getType(), user.getType(), Random.random(1, 11)));
                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    /*
     * Slime level:     Possible effects (cumulative): 
     * 1                Flatfooted (1 turn) 
     * 4                Weak Bound 
     * 8                Falling (if in neutral stance) OR 3-turn Flatfooted (if already standing over) 
     * 12               Low chance of Trance 
     * 16               Strong Bound 
     * 20               Somewhat higher chance of Trance OR Frenzied (50/50) 
     * 24               VERY low chance of Parasited (critical hit-style)
     * 
     * Accuracy increases with attribute level
     */

    public enum HitType {
        FLAT_1,
        BOUND_W,
        FALL,
        FLAT_3,
        TRANCE,
        BOUND_S,
        FRENZIED,
        PARASITED,
        NONE;

        public Status build(Character user, Character target) {
            switch (this) {
                case BOUND_S:
                    return new Bound(target.getType(), 55 + 4 * Math.sqrt(user.getAttribute(Attribute.slime)), "slime");
                case BOUND_W:
                    return new Bound(target.getType(), 20 + 2 * Math.sqrt(user.getAttribute(Attribute.slime)), "slime");
                case FALL:
                    return new Falling(target.getType());
                case FLAT_1:
                    return new Flatfooted(target.getType(), 1);
                case FLAT_3:
                    return new Flatfooted(target.getType(), 3);
                case FRENZIED:
                    return new Frenzied(target.getType(), 3);
                case PARASITED:
                    return new Parasited(target.getType(), user.getType());
                case TRANCE:
                    return new Trance(target.getType(), 3);
                default: // NONE or a stupid mistake
                    GUI.gui
                          .message("ERROR: Half-implemented HitType for ThrowSlime; "
                                          + "applying 1-turn Wary instead. Please report this."
                                          + " And be sure to laugh at my stupidity. (DNDW)");
                    return new Wary(target.getType(), 1);
            }
        }

        public void message(Combat c, Character self, Character target) {
            String msg = "";
            switch (this) {
                case BOUND_S:
                    msg += Formatter.format("While in the air, the mass of slime splits in two, and the remaining projectiles"
                                    + " impact both of {other:possessive} hands, binding them solidly to "
                                    + (c.getStance().en == Stance.neutral ? "one another." : "the ground.")
                                    , self, target);
                    break;
                case BOUND_W:
                    msg += Formatter.format("The slime impacts one of {other:possessive} hands, encasing it in a slimy"
                                    + " mitten. {other:PRONOUN-ACTION:are|is} going to have to get that off before"
                                    + " continuing.", self, target);
                    break;
                case FALL:
                    msg += Formatter.format("The glob impacts with a powerful <i>thud</i>, and it knocks"
                                    + " {other:subject} off {other:possessive} feet.", self, target);
                    break;
                case FLAT_1:
                    msg += Formatter.format("The slimy ball connects soundly with {other:possessive} head,"
                                    + " dazing {other:direct-object}.", self, target);
                    break;
                case FLAT_3:
                    msg += Formatter.format("The slime hits {other:possessive} already prone body with"
                                    + " substantial force, knocking the wind solidly out of {other:direct-object}."
                                    , self, target);
                    break;
                case TRANCE:
                    msg += Formatter.format("The glob catches on {other:possessive} arm, seemingly harmless. Then,"
                                    + " however, a flush spreads across {other:possessive} skin, radiating outward"
                                    + " from the slime. When the flush reaches {other:name-possessive} head,"
                                    + " {other:pronoun-action:fall|falls} straight into a deep trance."
                                    , self, target);
                    break;
                case NONE:
                    msg += Formatter.format("{other:PRONOUN}, however, "
                                    + "{other:action:manage|manages} to evade the onrushing slime.", self, target);
                    break;
                case PARASITED:
                    if (target.human()) {
                        msg += "You panic as the slime wraps itself around your head, completely engulfing it."
                                + " Your mood does not improve when you feel it seep into your ears, and"
                                + " beyond. It doesn't hurt, but the shock and the general weirdness of the"
                                + " situation get to you. You try to shake the slime off, but it just worms around"
                                + " inside of you while " + self.getName() + " observes, giggling."
                                + " When it's job - whatever it is - is finished, the slime dislodges from"
                                + " your head and falls to the ground in a harmless puddle. Not nearly as"
                                + " harmless, however, is the sensation of some left-over slime still "
                                + " working its magic inside your skull...";
                    } else {
                        msg += Formatter.format("You can't help but feel a little giddy as your risky move succeeds,"
                                        + " and the slime wraps around {other:name-possessive} head."
                                        + " You know it won't be long now. {other:PRONOUN} tries to throw and"
                                        + " claw the slime off, but you can already feel the connection forming."
                                        + " After only a few seconds, the slime falls away. Most of it, anyway."
                                        + " Time for a test run...", self, target);
                    }
                    break;
                case FRENZIED:
                    msg += Formatter.format("The glob catches on {other:possessive} arm, seemingly harmless. Then,"
                                    + " however, a flush spreads across {other:possessive} skin, radiating outward"
                                    + " from the slime. "
                                    + (target.human() ? "It feels warm, and when it reaches your head it fills your"
                                                    + " mind with an unquenchable thirst for sex. And you"
                                                    + " know just where to get some..."
                                                    : "When the flush reaches {other:name-possessive} head, {other:pronoun}"
                                                    + " suddenly stares straight at you, focused and intense"
                                                    + " with a not-so-subtle hint of sheer insanity.")
                                    , self, target);
                    break;
                default:
                    msg += "ERROR: Half-implemented HitType for ThrowSlime; "
                                    + "applying 1-turn Wary instead. Please report this."
                                    + " And be sure to laugh at my stupidity. (DNDW)";

            }
            c.write(self, msg);
        }
    }

    private int random(Combat c, Character target, int skill, int diff) {
        int r = Random.random(150);
        if (!c.getStance()
              .mobile(target) || !target.canRespond()) {
            r -= 50;
        } else {
            r += target.evasionBonus();
        }
       
        r -= Math.min(skill - diff, 20);

        return r;
    }

    public HitType decideEffect(Combat c, Character user, Character target) {
        int slime = user.getAttribute(Attribute.slime);
        int bonus = Math.min(slime, 40) - 20;

        if (!c.getStance().mobile(target) || !target.canRespond()) {
            bonus *= 2;
        }

        if (slime >= 24 && random(c, target, slime, 24) <= 2) {
            return HitType.PARASITED;
        }
        if (slime >= 20 && random(c, target, slime, 20) <= 15 + bonus) {
            return Random.random(2) == 0 ? HitType.TRANCE : HitType.FRENZIED;
        }
        if (slime >= 16 && random(c, target, slime, 16) <= 20 + bonus) {
            return HitType.BOUND_S;
        }
        if (slime >= 12 && random(c, target, slime, 12) <= 10 + bonus) {
            return HitType.TRANCE;
        }
        if (slime >= 8 && random(c, target, slime, 8) <= 25 + bonus) {
            return c.getStance().en == Stance.neutral ? HitType.FALL : HitType.FLAT_3;
        }
        if (slime >= 4 && random(c, target, slime, 4) <= 30 + bonus) {
            return HitType.BOUND_W;
        }
        if (random(c, target, slime, 1) <= 50 + bonus) {
            return HitType.FLAT_1;
        }
        return HitType.NONE;
    }
}
