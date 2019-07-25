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
import nightgames.stance.BreastSmothering;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;
import nightgames.status.Charmed;

public class BreastSmother extends Skill {
    public BreastSmother() {
        super("Breast Smother");
        addTag(SkillTag.dominant);
        addTag(SkillTag.usesBreasts);
        addTag(SkillTag.weaken);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getLevel() >= 15 ||user.getAttribute(Attribute.seduction) >= 30 && user.hasBreasts();
    }

    @Override
    public float priorityMod(Combat c, Character user) {
        if (c.getStance().havingSex(c)) {
            return 1; 
        } else {
            return 3;
        }
    }

    private static final int MIN_REQUIRED_BREAST_SIZE = 4;
    
    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.breastsAvailable()
                        && c.getStance().reachTop(user)
                        && c.getStance().front(user)
                        && user.body.getLargestBreasts().getSize() >= MIN_REQUIRED_BREAST_SIZE
                        && c.getStance().mobile(user)
                        && (!c.getStance().mobile(target) || c.getStance().prone(target)) && user.canAct();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Shove your opponent's face between your tits to crush her resistance.";
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        boolean special = c.getStance().en != Stance.breastsmothering && !c.getStance().havingSex(c);        
        writeOutput(c, special ? Result.special : Result.normal, user, target);

        double n = 10 + Random.random(5) + user.body.getLargestBreasts().getSize();

        if (target.has(Trait.temptingtits)) {
            n += Random.random(5, 10);
        }
        if (target.has(Trait.beguilingbreasts)) {
            n *= 1.5;
            target.add(c, new Charmed(target.getType()));
        }
        if (target.has(Trait.imagination)) {
            n *= 1.5;
        }

        target.temptWithSkill(c, user, user.body.getRandom("breasts"), (int) Math.round(n / 2), this);
        target.weaken(c, (int) DamageType.physical.modifyDamage(user, target, Random.random(5, 15)));

        target.loseWillpower(c, Math.min(5, target.getWillpower().max() * 10 / 100 ));     

        if (special) {
            c.setStance(new BreastSmothering(user.getType(), target.getType()), user, true);
            user.emote(Emotion.dominant, 20);
        } else {
            user.emote(Emotion.dominant, 10);
        }
        if (Random.random(100) < 15 + 2 * user.getAttribute(Attribute.fetishism)) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), "breasts", .25));
        }
        return true;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 0;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        if (c.getStance().enumerate() != Stance.breastsmothering) {
            return Tactics.positioning;
        } else {
            return Tactics.pleasure;
        }
    }

    @Override
    public String getLabel(Combat c, Character user) {
        return "Breast Smother";
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        StringBuilder b = new StringBuilder();
        
        if (modifier == Result.special) {
            b.append("You quickly wrap up ").append(target.getName()).append("'s head in your arms and press your ")
                            .append(user.body.getRandomBreasts().fullDescribe(user)).append(" into ")
                            .append(target.nameOrPossessivePronoun()).append(" face. ");
        }
        else {
            b.append("You rock ").append(target.getName()).append("'s head between your ")
                            .append(user.body.getRandomBreasts().fullDescribe(user))
                            .append(" trying to force ").append(target.directObject()).append(" to gasp.");
        }
        
        if (user.has(Trait.temptingtits)) {
            b.append(Formatter.capitalizeFirstLetter(target.possessiveAdjective()))
                            .append(" can't help but groan in pleasure from having ")
                            .append(target.possessiveAdjective()).append(" face stuck between your perfect tits");
            if (user.has(Trait.beguilingbreasts)) {
                b.append(", and you smile as ").append(target.pronoun()).append(" snuggles deeper into your cleavage");
            } 
            b.append(".");
            
        } else{
            b.append(" ").append(target.getName()).append(" muffles something in confusion into your breasts before ")
                            .append(target.pronoun()).append(" begins to panic as ").append(target.pronoun())
                            .append(" realizes ").append(target.pronoun()).append(" cannot breathe!");
        }   
        return b.toString();
}

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        StringBuilder b = new StringBuilder();
        if (modifier == Result.special) {
            b.append(user.subject()).append(" quickly wraps up your head between ")
                            .append(user.possessiveAdjective()).append(" ")
                            .append(user.body.getRandomBreasts().fullDescribe(user))
                            .append(", filling your vision instantly with them. ");
        } else {
            b.append(user.subject()).append(" rocks your head between ").append(user.possessiveAdjective())
                            .append(" ").append(user.body.getRandomBreasts().fullDescribe(user))
                            .append(" trying to force you to gasp for air. ");
        }
        
        if (user.has(Trait.temptingtits)) {
            b.append("You can't help but groan in pleasure from having your face stuck between ");
            b.append(user.possessiveAdjective());
            b.append(" perfect tits as they take your breath away");             
            if (user.has(Trait.beguilingbreasts)) {
                b.append(", and due to their beguiling nature you can't help but want to stay there as long as possible");
            }
            b.append(".");
        } else {
            b.append(" You let out a few panicked sounds muffled by the breasts now covering your face as you realize you cannot breathe!");
        }

        return b.toString();
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
