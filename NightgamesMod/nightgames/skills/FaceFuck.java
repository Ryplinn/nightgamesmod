package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.status.BodyFetish;
import nightgames.status.Shamed;

public class FaceFuck extends Skill {

    FaceFuck() {
        super("Face Fuck");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.fetishism) >= 15;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().dom(user) && c.getStance().prone(target)
                        && (user.crotchAvailable() && user.hasDick() || user.has(Trait.strapped))
                        && !c.getStance().inserted(user) && c.getStance().front(user)
                        && !c.getStance().behind(target);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Force your opponent to orally pleasure you.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        Result res = Result.normal;
        int selfDamage = 4;
        int targetDamage = 0;
        BodyPart targetMouth = target.body.getRandom("mouth");
        if (target.has(Trait.silvertongue)) {
            res = Result.reverse;
            selfDamage *= 2;
        }
        if (user.has(Trait.strapped)) {
            if (user.has(Item.Strapon2)) {
                res = Result.upgrade;
                targetDamage += 6;
            } else {
                res = Result.strapon;
            }
            selfDamage = 0;
        }
        if (targetMouth.isErogenous()) {
            targetDamage += 10;
        }
        selfDamage += Random.random(selfDamage * 2 / 3);
        targetDamage += Random.random(targetDamage * 2 / 3);

        if (user.human()) {
            c.write(user, deal(c, 0, res, user, target));
        } else {
            c.write(user, receive(c, 0, res, user, target));
        }
        target.add(c, new Shamed(target.getType()));

        if (selfDamage > 0) {
            user.body.pleasure(target, targetMouth, user.body.getRandom("cock"), selfDamage, c, new SkillUsage<>(this, user, target));
        }
        if (targetDamage > 0) {
            target.body.pleasure(target, user.body.getRandomInsertable(), targetMouth, targetDamage, c, new SkillUsage<>(this, user, target));
        }
        if (Random.random(100) < 5 + 2 * user.get(Attribute.fetishism) && !user.has(Trait.strapped)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "cock", .25));
        }
        target.loseMojo(c, Random.random(10, 20));
        target.loseWillpower(c, 5);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.debuff;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        String m;
        if (modifier == Result.strapon || modifier == Result.upgrade) {
            m = "You grab hold of " + target.getName()
                            + "'s head and push your cock into her mouth. She flushes in shame and anger, but still dutifully services you with her lips "
                            + "and tongue while you thrust your hips.";
            if (modifier == Result.upgrade) {
                m += "<br/>Additionally, your upgraded vibrocock thoroughly stimulates her throat.";
            }
        } else {
            if (target.body.getRandom("mouth").isErogenous()) {
                m = "You grab hold of " + target.getName()
                                + "'s head and push your cock into her mouth. What you find inside is unexpected, though. "
                                + target.getName()
                                + " has transformed her mouth into a second female genitalia; its soft hot walls, its ridges and folds slide across your dick delightfully as you thrust into her.";
                if (modifier == Result.reverse) {
                    m += "<br/>Her skillful tongue works its magic on your cock while you're fucking her mouth pussy, and you find yourself on the verge of orgasm way quicker than you would like.";
                }
            } else {
                m = "You grab hold of " + target.getName()
                                + "'s head and push your cock into her mouth. She flushes in shame and anger, but still dutifully services you with her lips "
                                + "and tongue while you thrust your hips.";
                if (modifier == Result.reverse) {
                    m += "<br/>Her skillful tongue works its magic on your cock though, and you find yourself on the verge of orgasm way quicker than you would like.";
                }
            }
        }
        return m;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 25;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        String m;
        if (modifier == Result.strapon) {
            m = String.format("%s forces her strapon cock into %s mouth and fucks %s face with it. "
                            + "It's only rubber, but the position is still humiliating. %s not "
                            + "to gag on the artificial member while %s revels in her dominance.",
                            Formatter.capitalizeFirstLetter(user.subject()), target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            target.subjectAction("try", "tries"), user.subject());
        } else if (modifier == Result.upgrade) {
            m = String.format("%s moves slightly towards %s, pushing her strapon against %s lips. %s to keep %s"
                            + " mouth closed but %s pinches %s nose shut, "
                            + "and pushes in the rubbery invader as %s for air. After a few sucks, %s"
                            + " %s to break free, although %s %s still shivering "
                            + "with a mix of arousal and humiliation.", Formatter.capitalizeFirstLetter(user.subject()), target.nameDirectObject(),
                            target.possessiveAdjective(), target.subjectAction("try", "tries"), target.possessiveAdjective(),
                            user.subject(), target.possessiveAdjective(), target.subjectAction("gasp"),
                            target.pronoun(), target.action("manage"), target.pronoun(), target.action("are", "is"));
        } else if (target.body.getRandom("mouth").isErogenous()) {
            m = String.format("%s forces %s mouth open and shoves %s %s into it. %s "
                            + "momentarily overwhelmed by the strong, musky smell and the taste, but "
                            + "%s quickly starts moving %s hips, fucking %s mouth. However, %s "
                            + "modified oral orifice was literally designed to squeeze cum; soon %s finds "
                            + "%s ramming with little more than %s own enjoyment in mind.",
                            user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            user.body.getRandomCock().describe(user), target.subjectAction("are", "is"),
                            user.subject(), user.possessiveAdjective(), target.possessiveAdjective(),
                            target.nameOrPossessivePronoun(), user.subject(), user.reflectivePronoun(),
                            user.possessiveAdjective());
        } else {
            m = String.format("%s forces %s mouth open and shoves %s %s into it. %s "
                            + "momentarily overwhelmed by the strong, musky smell and the taste, but "
                            + "%s quickly starts moving %s hips, fucking %s mouth like a pussy. %s "
                            + "%s cheeks redden in shame, but %s still %s what you can to pleasure %s. "
                            + "%s may be using you like a sex toy, but %s going to try to scrounge "
                            + "whatever advantage %s can get.",
                            user.subject(), target.nameOrPossessivePronoun(), user.possessiveAdjective(),
                            user.body.getRandomCock().describe(user), target.subjectAction("are", "is"),
                            user.subject(), user.possessiveAdjective(), target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.subjectAction("feel")), target.possessiveAdjective(),
                            target.pronoun(), target.action("do", "does"), user.nameDirectObject(),
                            Formatter.capitalizeFirstLetter(user.subject()), target.subjectAction("are", "is"),
                            target.pronoun());
        }
        return m;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
    
    @Override
    public Stage getStage() {
        return Stage.FINISHER;
    }
}
