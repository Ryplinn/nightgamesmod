package nightgames.skills;

import java.util.ArrayList;

import nightgames.characters.Character;
import nightgames.characters.Emotion;
import nightgames.characters.Trait;
import nightgames.characters.body.Body;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Global;
import nightgames.status.addiction.Addiction;
import nightgames.status.addiction.AddictionType;

public class Masturbate extends Skill {
    public Masturbate(Character self) {
        super("Masturbate", self);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public boolean usable(Combat c, Character target) {
        return getSelf().canMasturbate() && c.getStance().mobile(getSelf())
                        && getTargetOrgan(c, getSelf()) != Body.nonePart;
    }

    @Override
    public float priorityMod(Combat c) {
        return -10.0f;
    }

    public BodyPart getSelfOrgan() {
        return getSelf().body.getRandom("hands");
    }

    public BodyPart getTargetOrgan(Combat c, Character target) {
        ArrayList<BodyPart> parts = new ArrayList<BodyPart>();
        BodyPart cock = target.body.getRandomCock();
        BodyPart pussy = target.body.getRandomPussy();
        BodyPart ass = target.body.getRandom("ass");
        if (cock != null && !c.getStance().inserted(target)) {
            parts.add(cock);
        }
        if (pussy != null && !c.getStance().vaginallyPenetrated(target)) {
            parts.add(pussy);
        }
        if ((parts.isEmpty() || getSelf().has(Trait.shameless)) && ass != null
                        && !c.getStance().anallyPenetrated(target)) {
            parts.add(ass);
        }
        if (parts.isEmpty()) {
            return Body.nonePart;
        }

        return parts.get(Global.random(parts.size()));
    }

    private BodyPart withO = Body.nonePart;
    private BodyPart targetO = Body.nonePart;

    @Override
    public int getMojoBuilt(Combat c) {
        return 25;
    }

    @Override
    public boolean resolve(Combat c, Character target) {
        withO = getSelfOrgan();
        targetO = getTargetOrgan(c, getSelf());

        if (getSelf().human()) {
            if (getSelf().getArousal().get() <= 15) {
                c.write(getSelf(), deal(c, 0, Result.weak, target));
            } else {
                c.write(getSelf(), deal(c, 0, Result.normal, target));
                if (Global.getPlayer().checkAddiction(AddictionType.MIND_CONTROL, target)) {
                    Global.getPlayer().unaddictCombat(AddictionType.MIND_CONTROL, 
                                    target, Addiction.MED_INCREASE, c);
                    c.write(getSelf(), "Touching yourself amuses Mara, reducing her control over you.");
                }
            }
        } else if (target.human()) {
            c.write(getSelf(), receive(c, 0, Result.normal, target));
        }
        int pleasure;

        pleasure = getSelf().body.pleasure(getSelf(), withO, targetO, 25, c, this);
        getSelf().emote(Emotion.horny, pleasure);
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new Masturbate(user);
    }

    @Override
    public Tactics type(Combat c) {
        return Tactics.misc;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character target) {
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
    public String receive(Combat c, int damage, Result modifier, Character target) {
        if (targetO == null) {
            return "She starts playing with herself, building up her own arousal.";
        }
        if (targetO.isType("cock")) {
            if (modifier == Result.weak) {
                return "She takes hold of her flaccid dick, tugging and rubbing it into a full erection.";
            } else {
                return "She jerks off, building up her own arousal.";
            }
        } else if (targetO.isType("pussy")) {
            return "She slowly teases her own labia and starts playing with herself.";
        } else if (targetO.isType("ass")) {
            return "She teases her own asshole and sticks a finger in.";
        } else {
            return "She starts playing with herself, building up her own arousal.";
        }
    }

    @Override
    public String describe(Combat c) {
        return "Raise your own arousal and boosts your mojo";
    }
}
