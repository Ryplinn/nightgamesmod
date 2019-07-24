package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.*;

public class PetThreesome extends Skill {
    PetThreesome(String name, int cooldown) {
        super(name, cooldown);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.fucking);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 6.0f;
    }

    PetThreesome() {
        this("Threesome", 0);
    }

    public BodyPart getSelfOrgan(Character fucker, Combat c) {
        return fucker.body.getRandomCock();
    }

    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomPussy();
    }

    private boolean fuckable(Combat c, Character user, Character target) {
        Character fucker = getFucker(c, user);
        if (fucker == null) {
            return false;
        }

        BodyPart selfO = getSelfOrgan(fucker, c);
        BodyPart targetO = getTargetOrgan(target);
        // You can't really have a threesome with a fairy... or can you?
        boolean possible = fucker.body.getHeight() > 70 && selfO != null && targetO != null;
        boolean stancePossible = !c.getStance().havingSex(c);
        return possible && stancePossible && canGetToCrotch(target);
    }

    private boolean canGetToCrotch(Character target) {
        return target.crotchAvailable();
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target) && c.getStance().mobile(user) && (!c.getStance().mobile(target) || c.getStance().prone(target)) && user.canAct();
    }

    protected Character getFucker(Combat c, Character user) {
        return Random.pickRandom(c.getPetsFor(user)).orElse(null);
    }

    protected Character getMaster(Combat c, Character user) {
        return user;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int m = 5 + Random.random(5);
        int otherm = m;
        Character fucker = getFucker(c, user);
        Character master = getMaster(c, user);
        BodyPart selfO = getSelfOrgan(fucker, c);
        BodyPart targetO = getTargetOrgan(target);
        if (selfO == null || targetO == null) {
            c.write("Something really weird happened here, [ERROR]");
            return false;
        }
        for (int i = 0; i < 10; i++) {
            if (fucker.clothingFuckable(selfO) || fucker.strip(ClothingSlot.bottom, c) == null) {
                break;
            }
        }
        if (targetO.isReady(target)) {
            Result result = Random.random(3) == 0 ? Result.critical : Result.normal;
            if (selfO.isType("pussy") && targetO.isType("cock") && target.hasPussy() && master.hasDick()) {
                c.write(user, Formatter.format("While {self:subject} is holding {other:name-do} down, "
                                + "{master:subject-action:move|moves} behind {other:direct-object} and {master:action:pierce|pierces} "
                                + "{other:direct-object} with {master:possessive} cock. "
                                + "Taking advantage of {other:possessive} surprise "
                                + "{self:subject-action:slip|slips} {other:name-possessive} "
                                + "hard cock into {self:reflective}, ending up in a erotic daisy-chain.", fucker, 
                                target));
                c.setStance(new ReverseXHFDaisyChainThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
                target.body.pleasure(master, master.body.getRandomCock(), target.body.getRandomPussy(), otherm, 0, c, false, new SkillUsage<>(this, user, target));
                master.body.pleasure(target, target.body.getRandomPussy(), master.body.getRandomCock(), m, 0, c, false, new SkillUsage<>(this, user, target));
            } else if (selfO.isType("pussy") && targetO.isType("pussy")) {
                c.write(user, Formatter.format("While {master:subject:are|is} holding {other:name-do} down, "
                                + "{self:subject-action:mount|mounts} {other:direct-object} and {self:action:press|presses} "
                                + "{self:possessive} own pussy against {other:possessive}s.", fucker, 
                                target));
                c.setStance(new FFXTribThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
                target.body.pleasure(master, master.body.getRandomCock(), target.body.getRandomPussy(), otherm, 0, c, false, new SkillUsage<>(this, user, target));
                master.body.pleasure(target, target.body.getRandomPussy(), master.body.getRandomCock(), m, 0, c, false, new SkillUsage<>(this, user, target));
            } else if (selfO.isType("pussy")) {
                if (result == Result.critical && master.useFemalePronouns()) {
                    c.write(user, Formatter.format("While %s holding {other:name-do} down with %s ass, "
                                    + "{self:subject} mounts {other:direct-object} and pierces "
                                    + "{self:reflective} with {other:possessive} cock.", fucker, 
                                    target, master.subjectAction("are", "is"), master.possessiveAdjective()));
                    c.setStance(new FFMFacesittingThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
                } else {
                    c.write(user, Formatter.format("While %s holding {other:name-do} down, "
                                    + "{self:subject} mounts {other:direct-object} and pierces "
                                    + "{self:reflective} with {other:possessive} cock.", fucker, 
                                    target, master.subjectAction("are", "is")));
                    c.setStance(new FFMCowgirlThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
                }
            } else if (selfO.isType("cock") && master.hasPussy() && target.hasDick()) {
                c.write(user, Formatter.format("While %s holding {other:name-do} down, "
                                + "{self:subject} moves behind {other:direct-object} and pierces "
                                + "{other:direct-object} with {self:possessive} cock. "
                                + "Taking advantage of {other:possessive} surprise %s {other:name-possessive} "
                                + "hard cock into %s, ending up in a erotic daisy-chain.", fucker, 
                                target, master.subjectAction("are", "is"), master.subjectAction("slip"),
                                master.reflectivePronoun()));
                c.setStance(new XHFDaisyChainThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
                target.body.pleasure(master, master.body.getRandomPussy(), target.body.getRandomCock(), otherm, 0, c, false, new SkillUsage<>(this, user, target));
                master.body.pleasure(target, target.body.getRandomCock(), master.body.getRandomPussy(), m, 0, c, false, new SkillUsage<>(this, user, target));
            } else if (selfO.isType("cock") && !master.hasDick()) {
                c.write(user, Formatter.format("While %s holding {other:name-do} down, "
                                + "{self:subject} mounts {other:direct-object} and pierces "
                                + "{other:direct-object} with {self:possessive} cock in the missionary position.", fucker, 
                                target, master.subjectAction("are", "is")));
                c.setStance(new MFFMissionaryThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
            } else if (selfO.isType("cock")) {
                if (result == Result.critical) {
                    c.write(user, Formatter.format("While %s holding {other:name-do} from behind, "
                                    + "{self:subject} mounts {other:direct-object} and pierces "
                                    + "{other:direct-object} with {self:possessive} cock in the missionary position. "
                                    + "It does not end there however, as %s {other:possessive} remaining hole, "
                                    + "leaving {other:direct-object} completely stuffed front and back.", fucker, 
                                    target, master.subjectAction("are", "is"), master.pronoun() + master.action(" grin and take", " grins and takes")));
                    c.setStance(new MFMDoublePenThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
                    target.body.pleasure(master, master.body.getRandomCock(), target.body.getRandomAss(), otherm, 0, c, false, new SkillUsage<>(this, user, target));
                    master.body.pleasure(target, target.body.getRandomAss(), master.body.getRandomCock(), m, 0, c, false, new SkillUsage<>(this, user, target));
                } else {
                    c.write(user, Formatter.format("While %s holding {other:name-possessive} head, "
                                    + "{self:subject} gets behind {other:direct-object} and pierces "
                                    + "{other:direct-object} with {self:possessive} cock. "
                                    + "It does not end there however, as %s {other:direct-object} %s cock, "
                                    + "leaving the poor {other:girl} spit-roasted.", fucker, 
                                    target, master.subjectAction("are", "is"), master.pronoun() + master.action(" feed", " feeds"), master.possessiveAdjective()));
                    c.setStance(new MFMSpitroastThreesome(fucker.getType(), master.getType(), target.getType()), user, true);
                }
            }
            if (fucker.has(Trait.insertion)) {
                otherm += Math.min(fucker.get(Attribute.seduction) / 4, 40);
            }
            target.body.pleasure(fucker, selfO, targetO, otherm, c, new SkillUsage<>(this, user, target));
            fucker.body.pleasure(target, targetO, selfO, m, c, new SkillUsage<>(this, user, target));
        } else {
            c.write(user, Formatter.format("{self:SUBJECT-ACTION:try|tries} to pull {other:name-do} into a threesome but {other:pronoun-action:are|is} not aroused enough yet.",
                            user, target));
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new PetThreesome();
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        return "You bowl your opponent over and pin her down while your pet fucks her [PLACEHOLDER]";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        return user.subject() + " pins you down while her pet fucks you [PLACEHOLDER]";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Holds your opponent down and have your pet fuck her.";
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
