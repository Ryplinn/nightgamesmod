package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;
import nightgames.stance.Stance;
import nightgames.stance.StandingOver;
import nightgames.status.Slimed;

public class Slap extends Skill {

    public Slap() {
        super("Slap");
        addTag(SkillTag.hurt);
        addTag(SkillTag.positioning);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().reachTop(user) && user.canAct() && c.getStance().front(user);
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return user.has(Trait.pimphand) ? 15 : 5;
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        if (rollSucceeded) {
            if (isSlime(user)) {
                writeOutput(c, Result.critical, user, target);
                target.pain(c, user, Math.min(80, Random.random(10) + user.getAttribute(Attribute.slime) + user.getAttribute(Attribute.power) / 2));
                if (c.getStance().en == Stance.neutral && Random.random(5) == 0) {
                    c.setStance(new StandingOver(user.getType(), target.getType()), user, true);
                    c.write(user,
                                    Formatter.format("{self:SUBJECT-ACTION:slap|slaps} {other:direct-object} hard"
                                                    + " enough to throw {other:pronoun} to the ground.", user,
                                    target));
                }
                if (user.has(Trait.VolatileSubstrate)) {
                    target.add(c, new Slimed(target.getType(), user.getType(), Random.random(2, 4)));
                }
                target.emote(Emotion.nervous, 40);
                target.emote(Emotion.angry, 30);
            } else if (user.getAttribute(Attribute.animism) >= 8) {
                writeOutput(c, Result.special, user, target);
                if (user.has(Trait.pimphand)) {
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target,
                                    Random.random(35, 50) * (25 + user.getArousal().percent()) / 100));
                    target.emote(Emotion.nervous, 40);
                    target.emote(Emotion.angry, 30);
                } else {
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target,
                                    Random.random(25, 45) * (25 + user.getArousal().percent()) / 100));
                    target.emote(Emotion.nervous, 25);
                    target.emote(Emotion.angry, 30);
                }
            } else {
                writeOutput(c, Result.normal, user, target);
                if (user.has(Trait.pimphand)) {
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random
                                    .random(7, 15)));
                    target.emote(Emotion.nervous, 20);
                    target.emote(Emotion.angry, 30);
                } else {
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random
                                    .random(5, 10)));
                    target.emote(Emotion.nervous, 10);
                    target.emote(Emotion.angry, 30);
                }
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.power) >= 5;
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }
    
    private boolean isSlime(Character user) {
        return user.getAttribute(Attribute.slime) > 4;
    }

    @Override
    public String getLabel(Combat c, Character user) {
        if (isSlime(user)) {
            return "Clobber";
        } else if (user.getAttribute(Attribute.animism) >= 8) {
            return "Tiger Claw";
        } else {
            return "Slap";
        }
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return target.getName() + " avoids your slap.";
        } else if (modifier == Result.special) {
            return "You channel your bestial power and strike " + target.getName() + " with a solid open hand strike.";
        } else if (modifier == Result.critical) {
            return "You let more of your slime flow to your hand, tripling it in size. Then, you lash out and slam "
                            + target.getName() + " in the face.";
        } else {
            return "You slap " + target.getName()
                            + "'s cheek; not hard enough to really hurt her, but enough to break her concentration.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s tries to slap %s but %s %s %s wrist.",
                            user.subject(), target.nameDirectObject(),
                            target.pronoun(), target.action("catch", "catches"),
                            user.possessiveAdjective());
        } else if (modifier == Result.special) {
            return String.format("%s palm hits %s in a savage strike that makes %s head ring.",
                            user.nameOrPossessivePronoun(), target.nameDirectObject(),
                            target.possessiveAdjective());
        } else if (modifier == Result.critical) {
            return String.format("%s hand grows significantly, and then %s swings it powerfully into %s face.",
                            user.nameOrPossessivePronoun(), user.pronoun(),
                            target.nameOrPossessivePronoun());
        } else {
            return String.format("%s slaps %s across the face, leaving a stinging heat on %s cheek.",
                            user.subject(), target.nameDirectObject(), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Slap opponent across the face";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
