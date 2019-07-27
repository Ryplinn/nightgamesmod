package nightgames.skills;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.body.Body;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

import java.util.ArrayList;

public class Masturbate extends Skill {
    public Masturbate() {
        super("Masturbate");
        addTag(SkillTag.pleasureSelf);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.canMasturbate() && !user.bound()
                        && getTargetOrgan(c, user, user) != Body.nonePart;
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return -10.0f;
    }

    public BodyPart getSelfOrgan(Character user) {
        return user.body.getRandom("hands");
    }

    public BodyPart getTargetOrgan(Combat c, Character user, Character target) {
        ArrayList<BodyPart> parts = new ArrayList<>();
        BodyPart cock = target.body.getRandomCock();
        BodyPart pussy = target.body.getRandomPussy();
        BodyPart ass = target.body.getRandom("ass");
        if (cock != null && !c.getStance().inserted(target)) {
            parts.add(cock);
        }
        if (pussy != null && !c.getStance().vaginallyPenetrated(c, target)) {
            parts.add(pussy);
        }
        if ((parts.isEmpty() || user.has(Trait.shameless)) && ass != null
                        && !c.getStance().anallyPenetrated(c, target)) {
            parts.add(ass);
        }
        if (parts.isEmpty()) {
            return Body.nonePart;
        }

        return parts.get(Random.random(parts.size()));
    }

    private BodyPart targetO = Body.nonePart;

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return user.canMakeOwnDecision() && user.canAct() ? 25 : 0;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        BodyPart withO = getSelfOrgan(user);
        targetO = getTargetOrgan(c, user, user);

        if (user.human()) {
            if (user.getArousal().get() <= 15) {
                c.write(user, deal(c, 0, Result.weak, user, target));
            } else {
                c.write(user, deal(c, 0, Result.normal, user, target));
            }
        } else if (c.shouldPrintReceive(target, c)) {
            c.write(user, receive(c, 0, Result.normal, user, target));
        }
        if (user.checkAddiction(AddictionType.MIND_CONTROL, target)) {
            user.unaddictCombat(AddictionType.MIND_CONTROL, target, Addiction.MED_INCREASE, c);
            c.write(user, "Touching yourself amuses Mara, reducing her control over you.");
        }
        int pleasure;

        pleasure = user.body.pleasure(user, withO, targetO, 25, c, new SkillUsage<>(this, user, target));
        user.emote(Emotion.horny, pleasure);
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if (targetO == null) {
            return "You play with yourself, building up your own arousal.";
        }
        if (targetO.isType("cock")) {
            if (modifier == Result.weak) {
                return "You take hold of your flaccid dick, tugging and rubbing it into a full erection.";
            } else {
                return "You jerk off, building up your own arousal.";
            }
        } else if (targetO.isType("pussy")) {
            return "You tease your own labia and finger yourself.";
        } else if (targetO.isType("ass")) {
            return "You tease your own asshole.";
        } else {
            return "You play with yourself, building up your own arousal.";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (targetO == null) {
            return String.format("%s starts playing with %s, building up %s own arousal.",
                            user.subject(), user.reflectivePronoun(),
                            user.possessiveAdjective());
        }
        if (targetO.isType("cock")) {
            if (modifier == Result.weak) {
                return String.format("%s takes hold of %s flaccid dick, tugging and rubbing it into a full erection.",
                                user.subject(), user.possessiveAdjective());
            } else {
                return String.format("%s jerks off, building up %s own arousal.",
                                user.subject(), user.possessiveAdjective());
            }
        } else if (targetO.isType("pussy")) {
            return String.format("%s slowly teases her own labia and starts playing with %s.",
                            user.subject(), user.reflectivePronoun());
        } else if (targetO.isType("ass")) {
            return String.format("%s teases %s own asshole and sticks a finger in.",
                            user.subject(), user.possessiveAdjective());
        } else {
            return String.format("%s starts playing with %s, building up %s own arousal.",
                            user.subject(), user.possessiveAdjective(),
                            user.reflectivePronoun());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Raise your own arousal and boosts your mojo";
    }
}
