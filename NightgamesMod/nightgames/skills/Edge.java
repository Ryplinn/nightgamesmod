package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;

public class Edge extends Skill {

    public Edge() {
        super("Edge");
        addTag(SkillTag.usesHands);
        addTag(SkillTag.pleasure);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.has(Trait.edger);
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance().reachBottom(user)
                        && target.crotchAvailable()
                        && target.hasDick() && user.canAct()
                        && !c.getStance().havingSex(c);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        float mod = 0.f;
        if (user.has(Trait.dexterous) || user.has(Trait.defthands) ||
                        user.has(Trait.limbTraining1)) {
            mod += .5f;
        }
        if (c.getOpponent(user).getArousal().percent() >= 100
                        && c.getOpponent(user).getArousal().percent() < 300) {
            mod *= 2;
        }
        if (user.getArousal().percent() >= 80) {
            mod /= 3;
        }
        return mod;
    }
    
    @Override
    public String describe(Combat c, Character user) {
        return "Get your opponent close to the edge, without pushing them over.";
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 80;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        boolean hit = !target.canAct() || c.getStance().dom(user)
                        || rollSucceeded;
        if (!hit) {
            c.write(user, Formatter.format("{self:NAME-POSSESSIVE} hands descend towards"
                            + "{other:name-possessive} {other:body-part:cock}, but "
                            + "{other:pronoun} succeeds in keeping them well away.", user, target));
            return false;
        } else if (target.getArousal().percent() < 100) {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:jerk|jerks} {other:name-possessive}"
                            + " {other:body-part:cock} slowly yet deliberately with both hands.", user, target));
        } else {
            c.write(user, Formatter.format("{other:SUBJECT-ACTION:are|is} already so close to cumming, but"
                            + " {self:name-possessive} hands make such careful, calculated movements all over"
                            + " {other:possessive} {other:body-part:cock} that {other:pronoun-action:stay|stays}"
                            + " <i>just</i> away from that impending peak. "
                            + "{other:PRONOUN-ACTION:<i>do</i>|<i>does</i>} thrash around a lot, trying desperately"
                            + " to get that little bit of extra stimulation, and it's draining"
                            + " {other:possessive} energy quite rapidly.", user, target));
            target.weaken(c, Math.min(30, Random.random((target.getArousal().percent() - 100) / 10)));
        }
        target.temptWithSkill(c, user, user.body.getRandom("hands"), 20 + Random.random(8), this);
        target.emote(Emotion.horny, 30);
        user.emote(Emotion.confident, 15);
        user.emote(Emotion.dominant, 15);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return null;
    }

}
