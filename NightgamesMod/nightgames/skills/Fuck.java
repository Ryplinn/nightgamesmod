package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.CockMod;
import nightgames.characters.body.StraponPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.clothing.Clothing;
import nightgames.items.clothing.ClothingSlot;
import nightgames.nskills.tags.SkillTag;

public class Fuck extends Skill {

    public Fuck(String name, int cooldown) {
        super(name, cooldown);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.fucking);
        addTag(SkillTag.petDisallowed);
    }

    public Fuck() {
        this("Fuck", 0);
    }

    public BodyPart getSelfOrgan(Character user) {
        BodyPart res = user.body.getRandomCock();
        if (res == null && user.has(Trait.strapped)) {
            res = StraponPart.generic;
        }
        return res;
    }

    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandomPussy();
    }

    boolean fuckable(Combat c, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        boolean possible = selfO != null && targetO != null;
        boolean ready = possible && selfO.isReady(user);
        boolean stancePossible = false;
        if (ready) {
            stancePossible = true;
            if (selfO.isType("cock")) {
                stancePossible = !c.getStance().inserted(user);
            }
            if (selfO.isType("pussy")) {
                stancePossible &= !c.getStance().vaginallyPenetrated(c, user);
            }
            if (selfO.isType("ass")) {
                stancePossible &= !c.getStance().anallyPenetrated(c, user);
            }
            if (targetO.isType("cock")) {
                stancePossible &= !c.getStance().inserted(target);
            }
            if (targetO.isType("pussy")) {
                stancePossible &= !c.getStance().vaginallyPenetrated(c, target);
            }
            if (targetO.isType("ass")) {
                stancePossible &= !c.getStance().anallyPenetrated(c, target);
            }
        }
        stancePossible &= !c.getStance().havingSex(c) && !c.getStance().isFaceSitting(user);
        return possible && ready && stancePossible && user.clothingFuckable(selfO) && canGetToCrotch(user, target);
    }

    private boolean canGetToCrotch(Character user, Character target) {
        if (target.crotchAvailable())
            return true;
        if (!getSelfOrgan(user).moddedPartCountsAs(user, CockMod.slimy))
            return false;
        return target.outfit.getTopOfSlot(ClothingSlot.bottom).getLayer() == 0;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target)
                        && (c.getStance().insert(c, user, user).isPresent()
                                        || c.getStance().insert(c, target, user).isPresent())
                        && c.getStance().mobile(user) && !c.getStance().mobile(target) && user.canAct();
    }

    String premessage(Combat c, Character user, Character target) {
        String premessage = "";
        Clothing underwear = user.getOutfit().getSlotAt(ClothingSlot.bottom, 0);
        Clothing bottom = user.getOutfit().getSlotAt(ClothingSlot.bottom, 1);
        String bottomMessage;

        if (underwear != null && bottom != null) {
            bottomMessage = underwear.getName() + " and " + bottom.getName();
        } else if (underwear != null) {
            bottomMessage = underwear.getName();
        } else if (bottom != null) {
            bottomMessage = bottom.getName();
        } else {
            bottomMessage = "";
        }

        if (getSelfOrgan(user) != null) {
            if (!bottomMessage.isEmpty() && getSelfOrgan(user).isType("cock")) {
                premessage = String.format("{self:SUBJECT-ACTION:pull|pulls} down {self:possessive} %s halfway and ",
                                bottomMessage);
            } else if (!bottomMessage.isEmpty() && getSelfOrgan(user).isType("pussy")) {
                premessage = String.format("{self:SUBJECT-ACTION:pull|pulls} {self:possessive} %s to the side and ",
                                bottomMessage);
            }

            if (!target.crotchAvailable() && getSelfOrgan(user).moddedPartCountsAs(user, CockMod.slimy)) {
                Clothing destroyed = target.strip(ClothingSlot.bottom, c);
                assert target.outfit.slotEmpty(ClothingSlot.bottom);
                String start;
                if (premessage.isEmpty()) {
                    start = "{self:SUBJECT-ACTION:place|places}";
                } else {
                    start = "{self:action:place|places} ";
                }
                premessage += start + " the head of {self:possessive} {self:body-part:cock}"
                                + " against {other:name-possessive} " + destroyed.getName() + ". The corrosive slime burns"
                                + " right through them, but leaves the skin beneath untouched. Then, ";
            }
        }

        return Formatter.format(premessage, user, target);
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        String premessage = premessage(c, user, target);
        int m = Random.random(10, 15);
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        if (selfO != null && targetO != null && selfO.isReady(user) && targetO.isReady(target)) {
            if (targetO.isType("pussy") && target.has(Trait.temptingass) && new AssFuck().usable(c, user, target)
                && Random.random(3) == 1) {
                
                c.write(user, Formatter.format("%s{self:subject-action:line|lines}"
                                + " {self:possessive} {self:body-part:cock} up with {other:name-possessive}"
                                + " {other:body-part:pussy}. At the last moment before thrusting in, however,"
                                + " {self:pronoun-action:shift|shifts} to the tantalizing hole next door,"
                                + " and {self:action:sink|sinks} the hard rod into {other:name-possessive}"
                                + " hot ass instead.<br/>", user, target, premessage));
                new AssFuck().resolve(c, user, target, true);
                
                return true;
            }
            if (user.human()) {
                c.write(user, premessage + deal(c, premessage.length(), Result.normal, user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, premessage + receive(c, premessage.length(), Result.normal, user, target));
            }
            if (selfO.isType("pussy")) {
                c.getStance().insert(c, target, user).ifPresent(newStance -> c
                                .setStance(newStance, user, user.canMakeOwnDecision()));
            } else {
                c.getStance().insert(c, user, user).ifPresent(newStance -> c
                                .setStance(newStance, user, user.canMakeOwnDecision()));
            }
            int otherm = m;
            if (user.has(Trait.insertion)) {
                otherm += Math.min(user.getAttribute(Attribute.seduction) / 4, 40);
            }
            target.body.pleasure(user, selfO, targetO, otherm, c, new SkillUsage<>(this, user, target));
            user.body.pleasure(target, targetO, selfO, m, c, new SkillUsage<>(this, user, target));
        } else {
            if (user.human()) {
                c.write(user, premessage + deal(c, premessage.length(), Result.miss, user, target));
            } else if (c.shouldPrintReceive(target, c)) {
                c.write(user, premessage + receive(c, premessage.length(), Result.miss, user, target));
            }
            target.body.pleasure(user, selfO, targetO, 5, c, new SkillUsage<>(this, user, target));
            user.body.pleasure(target, targetO, selfO, 5, c, new SkillUsage<>(this, user, target));
            return false;
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return true;
    }

    @Override
    public int speed(Character user) {
        return 2;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.fucking;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        if (modifier == Result.normal) {
            return "you rub the head of your " + selfO.describe(user) + " around " + target.getName()
                            + "'s entrance, causing "+target.directObject()+" to shiver with anticipation. Once you're sufficiently lubricated "
                            + "with "+target.possessiveAdjective()+" wetness, you thrust into "+target.possessiveAdjective()+" " + targetO.describe(target)
                            + ". " + target.getName()
                            + " tries to stifle "+target.possessiveAdjective()+" pleasured moan as you fill "+target.possessiveAdjective()+" in an instant.";
        } else if (modifier == Result.miss) {
            if (!selfO.isReady(user) && !targetO.isReady(target)) {
                return "you're in a good position to fuck " + target.getName()
                                + ", but neither of you are aroused enough to follow through.";
            } else if (!targetO.isReady(target)) {
                return "you position your " + selfO.describe(user) + " at the entrance to " + target.getName()
                                + ", but find that "+target.pronoun()+"'s not nearly wet enough to allow a comfortable insertion. You'll need "
                                + "to arouse "+target.directObject()+" more or you'll risk hurting "+target.directObject()+".";
            } else if (!selfO.isReady(user)) {
                return "you're ready and willing to claim " + target.getName() + "'s eager "
                                + targetO.describe(target) + ", but your shriveled "
                                + selfO.describe(user)
                                + " isn't cooperating. Maybe your self-control training has become too effective.";
            }
            return "you managed to miss the mark.";
        }
        return "Bad stuff happened";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        BodyPart selfO = getSelfOrgan(user);
        BodyPart targetO = getTargetOrgan(target);
        if (modifier == Result.normal) {
            return String.format("%s rubs %s %s against %s wet snatch. " +
                                 "%s slowly but steadily pushes in, forcing %s length into %s hot, wet pussy.", 
                            user.getName(), user.possessiveAdjective(), selfO.describe(user),
                            target.nameOrPossessivePronoun(),
                            Formatter.capitalizeFirstLetter(user.pronoun()), user.possessiveAdjective(),
                            target.possessiveAdjective());
        } else if (modifier == Result.miss) {
            String subject = (damage == 0 ? user.getName() + " " : "");
            if (!selfO.isReady(user) || !targetO.isReady(target)) {
                String indicative = target.human() ? "yours" : target.nameOrPossessivePronoun();
                return String.format("%sgrinds %s privates against %ss, but since neither of %s are"
                                + " very turned on yet, it doesn't accomplish much.",
                                subject, user.possessiveAdjective(), indicative,
                                c.bothDirectObject(target));
            } else if (!targetO.isReady(target)) {
                return String.format("%stries to push %s %s inside %s pussy, but %s %s not wet enough. "
                                + "%s simply not horny enough for effective penetration yet.",
                                subject, user.possessiveAdjective(), selfO.describe(user),
                                target.nameOrPossessivePronoun(), target.pronoun(),
                                target.action("are", "is"),
                                Formatter.capitalizeFirstLetter(target.subjectAction("are", "is")));
            } else {
                return String.format("%stries to push %s %s into %s ready pussy, but %s is still limp.",
                                subject, user.possessiveAdjective(), selfO.describe(user),
                                target.nameOrPossessivePronoun(), user.pronoun());
            }
        }
        return "Bad stuff happened";
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Penetrate your opponent, switching to a sex position";
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
