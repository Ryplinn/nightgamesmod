package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.Emotion;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.items.Item;
import nightgames.stance.Anal;
import nightgames.stance.AnalProne;
import nightgames.stance.BehindFootjob;
import nightgames.stance.Stance;
import nightgames.status.*;

public class AssFuck extends Fuck {
    public AssFuck() {
        super("Ass Fuck", 0);
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        return 0.0f + (user.getMood() == Emotion.dominant ? 1.0f : 0);
    }

    @Override
    public BodyPart getTargetOrgan(Character target) {
        return target.body.getRandom("ass");
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return fuckable(c, user, target) && c.getStance().mobile(user)
                        && (c.getStance().behind(user)
                                        || (c.getStance().prone(target) && !c.getStance().mobile(target)))
                        && user.canAct()
                        && c.getStance().reachBottom(user)
                        && (getTargetOrgan(target).isReady(target) || target.has(Trait.buttslut) || user.has(Item.Lubricant)
                                        || user.getArousal().percent() > 50 || user.has(Trait.alwaysready)
                                        || user.has(Trait.assmaster))
                        && (!target.hasPussy() || PullOut.permittedByAddiction(user));
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        String premessage = premessage(c, user, target);
        if (!target.hasStatus(Stsflag.oiled) && user.getArousal().percent() > 50
                        || user.has(Trait.alwaysready) || user.has(Trait.assmaster)) {
            String fluids = target.hasDick() ? "copious pre-cum" : "own juices";
            if (premessage.isEmpty()) {
                premessage = "{self:subject-action:lube|lubes}";
            } else {
                premessage += "{self:action:lube|lubes}";
            }
            premessage += " up {other:possessive} ass with {self:possessive} " + fluids + ".";
            target.add(c, new Oiled(target.getType()));
        } else if (!target.hasStatus(Stsflag.oiled) && user.has(Item.Lubricant)) {
            if (premessage.isEmpty()) {
                premessage = "{self:subject-action:lube|lubes}";
            } else {
                premessage += "{self:action:lube|lubes}";
            }
            premessage += " up {other:possessive} ass.";
            target.add(c, new Oiled(target.getType()));
            user.consume(Item.Lubricant, 1);
        }
        c.write(user, Formatter.format(premessage, user, target));

        int m = Random.random(10, 15);
        if (user.has(Trait.strapped) && user.has(Item.Strapon2)) {
            m += 3;
        }
        if (user.human()) {
            c.write(user, deal(c, premessage.length(), Result.normal, user, target));
        } else if (target.human()) {
            if (!c.getStance().behind(user) && user.has(Trait.strapped)) {
                c.write(user, receive(c, premessage.length(), Result.upgrade, user, target));
            } else if (user.getType().equals(CharacterType.get("Eve")) && c.getStance().behind(user)) {
                m += 5;
                c.write(user, receive(c, premessage.length(), Result.special, user, target));
            } else {
                c.write(user, receive(c, premessage.length(), Result.normal, user, target));
            }
        } else if (c.isBeingObserved()) {
            if (!c.getStance().behind(user) && user.has(Trait.strapped)) {
                c.write(user, receive(c, premessage.length(), Result.upgrade, user, target));
            } else {
                c.write(user, receive(c, premessage.length(), Result.normal, user, target));
            }
        }

        boolean voluntary = user.canMakeOwnDecision();
        if (c.getStance().behind(user)) {
            if (user.getType().equals(CharacterType.get("Eve"))) {
                c.setStance(new AnalProne(user.getType(), target.getType()), user, voluntary);
            } else {
                if (c.getStance().enumerate() == Stance.behindfootjob) {
                    c.setStance(new BehindFootjob(user.getType(), target.getType()));}
                else {c.setStance(new Anal(user.getType(), target.getType()), user, voluntary);}
            }
        } else {
            c.setStance(new AnalProne(user.getType(), target.getType()), user, voluntary);
        }
        int otherm = m;
        if (user.has(Trait.insertion)) {
            otherm += Math.min(user.get(Attribute.seduction) / 4, 40);
        }
        target.body.pleasure(user, getSelfOrgan(user), getTargetOrgan(target), otherm, c, new SkillUsage<>(this, user, target));
        if (!user.has(Trait.strapped)) {
            user.body.pleasure(target, getTargetOrgan(target), getSelfOrgan(user), m / 2, c, new SkillUsage<>(this, user, target));
        }
        user.emote(Emotion.dominant, 100);
        if (!target.has(Trait.analTraining1) && !target.has(Trait.shameless)) {
            target.emote(Emotion.desperate, 50);
        } else {
            target.emote(Emotion.horny, 25);
        }
        if (!target.has(Trait.Unflappable)) {
            target.add(c, new Flatfooted(target.getType(), 1));
        }
        if (user.has(Trait.analFanatic) && user.hasDick()) {
            c.write(user,
                            String.format("Now with %s %s deeply embedded within %s ass,"
                                            + " %s mind clears itself of everything but fucking %s as hard as possible.",
                            user.possessiveAdjective(), user.body.getRandomCock().describe(user),
                            target.nameOrPossessivePronoun(), user.nameOrPossessivePronoun(),
                            target.directObject()));
            user.add(c, new Frenzied(user.getType(), 4));
            user.add(c, new IgnoreOrgasm(user.getType(), 4));
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 15 || target.has(Trait.buttslut);
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
        if (modifier == Result.normal) {
            return String.format(
                            (damage == 0 ? "You" : "After you")
                                            + " make sure %s ass is sufficiently lubricated, you push your %s into %s %s.",
                            target.nameOrPossessivePronoun(), getSelfOrgan(user).describe(user),
                            target.possessiveAdjective(), getTargetOrgan(target).describe(target));
        } else {
            return target.getName() + "'s ass is oiled up and ready to go, but you're still too soft to penetrate "+target.directObject()+".";
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if (modifier == Result.upgrade) {
            return String.format("%s %s legs apart and teasingly pokes the strap-on against %s anus."
                            + " %s %s to struggle away, but %s %s %s hips closer and slowly pushes the dildo"
                            + " inside %s ass.", user.subjectAction("spread"), target.nameOrPossessivePronoun(),
                            target.possessiveAdjective(),
                            Formatter.capitalizeFirstLetter(target.pronoun()), target.action("try", "tries"),
                            user.subject(), user.action("pull"), target.possessiveAdjective(),
                            target.possessiveAdjective());
        }
        if (modifier == Result.normal) {
            if (user.has(Trait.strapped)) {
                if (user.has(Item.Strapon2)) {
                    return String.format("%s %s strap-on behind %s and pushes it into %s lubricated ass. After pushing it"
                                    + " in completely, %s pushes a button on a controller which causes the dildo to vibrate in"
                                    + " %s ass, giving %s a slight shiver.", user.subjectAction("align"), user.possessiveAdjective(),
                                    target.nameDirectObject(), target.possessiveAdjective(), user.pronoun(), target.possessiveAdjective(),
                                    target.directObject());
                } else {
                    return String.format("%s lubes up %s strap-on, positions %s behind %s, and shoves it into %s ass.", 
                                    user.getName(), user.possessiveAdjective(), user.reflectivePronoun(),
                                    target.nameDirectObject(), target.possessiveAdjective());
                }
            } else {
                return String.format("%s rubs %s cock up and down %s ass before thrusting %s hips to penetrate %s.",
                                user.getName(), user.possessiveAdjective(), target.nameOrPossessivePronoun(),
                                user.possessiveAdjective(), target.directObject());
            }
        } else if (modifier == Result.special) {
            // Eve
            return String.format(
                            "While maintaining a firm grip, Eve runs her hands down your sides. <i>\"Are you ready for"
                                            + " me now, %s? Actually, I don't care if you are. It's not like you can stop me now.\"</i> There's only"
                                            + " one thing that could mean, and you don't want any part of it. You struggle in Eve's arms, trying to"
                                            + " get away as she is dryhumping your ass, getting it wet for her. Finally, you manage to stumble away,"
                                            + " but Eve trips you before you regain your balance. She follows you to the ground, rolling you onto your"
                                            + " back and lifting your legs. <i>\"Uh uh, you're not going anywhere, my little cumslut-to-be. Now just lay back and"
                                            + " take it.\"</i> Keeping your legs up with one arm, she uses the other to line up her %s with your hole. Then,"
                                            + " she brutally slams it all the way in in one go. Your screams and Eve's laughter fill the air as she starts"
                                            + " fucking you at a furious pace.",
                            target.getName(), user.body.getRandomCock().describe(user));
        } else {
            return String.format("%s rubs %s dick against %s ass, but it's still flaccid and can't actually penetrate %s.",
                            user.getName(), user.possessiveAdjective(), target.nameOrPossessivePronoun(), target.directObject());
        }
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Penetrate your opponent's ass.";
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
