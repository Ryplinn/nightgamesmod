package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.StraponPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.BodyFetish;

public class Frottage extends Skill {

    public Frottage() {
        super("Frottage");
        addTag(SkillTag.pleasureSelf);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getAttribute(Attribute.seduction) >= 26;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canAct() && c.getStance().mobile(user) && !c.getStance().sub(user)
                        && !c.getStance().havingSex(c) && target.crotchAvailable()
                        && (user.hasDick() && user.crotchAvailable() || user.has(Trait.strapped))
                        && c.getStance().reachBottom(user);
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Rub yourself against your opponent";
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 10;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int m = 6 + Random.random(8);
        BodyPart receiver = target.hasDick() ? target.body.getRandomCock() : target.body.getRandomPussy();
        BodyPart dealer = user.hasDick() ? user.body.getRandomCock() : user.has(Trait.strapped) ? StraponPart.generic : user.body.getRandomPussy();
        if (user.human()) {
            if (target.hasDick()) {
                c.write(user, deal(c, m, Result.special, user, target));
            } else {
                c.write(user, deal(c, m, Result.normal, user, target));
            }
        } else if (user.has(Trait.strapped)) {
            if (target.human()) {
                c.write(user, receive(c, m, Result.special, user, target));
            }
            target.loseMojo(c, 10);
            dealer = null;
        } else {
            c.write(user, receive(c, m, Result.normal, user, target));
        }

        if (dealer != null) {
            user.body.pleasure(target, receiver, dealer, m / 2, c, new SkillUsage<>(this, user, target));
        }
        target.body.pleasure(user, dealer, receiver, m, c, new SkillUsage<>(this, user, target));
        if (Random.random(100) < 15 + 2 * user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "cock", .25));
        }
        user.emote(Emotion.horny, 15);
        target.emote(Emotion.horny, 15);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return "You tease " + target.getName() + "'s penis with your own, dueling her like a pair of fencers.";
        } else {
            return "You press your hips against " + target.getName()
                            + "'s legs, rubbing her nether lips and clit with your dick.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.special) {
            return String.format("%s thrusts %s hips to prod %s delicate jewels with %s strapon dildo. "
                            + "As %s and %s %s hips back, %s presses the toy against %s cock, "
                            + "teasing %s sensitive parts.",
                            user.subject(), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                            user.possessiveAdjective(),
                            target.subjectAction("flinch", "flinches"), target.action("pull"), target.possessiveAdjective(),
                            user.subject(), target.possessiveAdjective(), target.possessiveAdjective());
        } else if (user.hasDick() && target.hasDick()) {
            return String.format("%s pushes %s %s against the sensitive head of %s member, "
                            + "dominating %s manhood.", user.subject(), user.possessiveAdjective(),
                            user.body.getRandomCock().describe(user), target.nameOrPossessivePronoun(),
                            target.possessiveAdjective());
        } else {
            return String.format("%s pushes %s cock against her soft thighs, rubbing %s shaft up"
                            + " against %s nether lips.", user.subject(),
                            target.nameOrPossessivePronoun(), target.possessiveAdjective(),
                            user.possessiveAdjective());
        }
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
