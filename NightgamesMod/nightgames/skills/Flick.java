package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class Flick extends Skill {

    public Flick() {
        super("Flick", 2);
        addTag(SkillTag.mean);
        addTag(SkillTag.hurt);
        addTag(SkillTag.positioning);
        addTag(SkillTag.staminaDamage);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return target.crotchAvailable() && c.getStance().reachBottom(user) && user.canAct()
                        && !user.has(Trait.shy);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 10;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 5;
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 90;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            if (target.has(Trait.brassballs)) {
                writeOutput(c, Result.weak, user, target);
            } else {
                int mojoLost = 25;
                int m = Random.random(8) + 8;
                writeOutput(c, Result.normal, user, target);
                if (target.has(Trait.achilles)) {
                    m += 2 + Random.random(target.get(Attribute.perception) / 2);
                    mojoLost = 40;
                }
                target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, m));
                target.loseMojo(c, mojoLost);
                user.emote(Emotion.dominant, 10);
                target.emote(Emotion.angry, 15);
                target.emote(Emotion.nervous, 15);
            }
        } else {
            writeOutput(c, Result.miss, user, target);
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 17;
    }

    @Override
    public Skill copy(Character user) {
        return new Flick();
    }

    @Override
    public int speed(Character user) {
        return 6;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return "You flick your finger between " + target.getName() + "'s legs, but don't hit anything sensitive.";
        } else if (modifier == Result.weak) {
            return "You flick " + target.getName() + "'s balls, but " + target.pronoun() + " seems utterly unfazed.";
        } else {
            if (target.hasBalls()) {
                return "You use two fingers to simultaneously flick both of " + target.getName()
                                + " dangling balls. She tries to stifle a yelp and jerks her hips away reflexively. "
                                + "You feel a twinge of empathy, but she's done far worse.";
            } else {
                return "You flick your finger sharply across " + target.getName()
                                + "'s sensitive clit, causing her to yelp in surprise and pain. She quickly covers her girl parts "
                                + "and glares at you in indignation.";
            }
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.miss) {
            return String.format("%s flicks at %s balls, but hits only air.",
                            user.subject(), target.nameOrPossessivePronoun());
        } else if (modifier == Result.weak) {
            return String.format("%s flicks %s balls, but %s barely %s a thing.",
                            user.subject(), target.nameOrPossessivePronoun(),
                            target.pronoun(), target.action("feel"));
        } else {
            return String.format("%s gives %s a mischievous grin and flicks each of %s balls with %s finger. "
                            + "It startles %s more than anything, but it does hurt and "
                            + "%s seemingly carefree abuse of %s jewels destroys %s confidence.",
                            user.subject(), target.nameDirectObject(), target.possessiveAdjective(),
                            user.possessiveAdjective(), target.directObject(), user.nameOrPossessivePronoun(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Flick opponent's genitals, which is painful and embarrassing";
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
