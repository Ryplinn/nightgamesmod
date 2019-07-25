package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BreastsPart;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.stance.Stance;
import nightgames.status.BodyFetish;
import nightgames.status.Stsflag;


public class Paizuri extends Skill {
    Paizuri(String name) {
        super(name);
        addTag(SkillTag.positioning);
        addTag(SkillTag.pleasure);
        addTag(SkillTag.oral);
        addTag(SkillTag.foreplay);
        addTag(SkillTag.usesBreasts);
    }
    
    Paizuri() {
        this("Titfuck");
    }

    static int MIN_REQUIRED_BREAST_SIZE = 3;

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return user.hasBreasts()
                        && user.body.getLargestBreasts().getSize() >= MIN_REQUIRED_BREAST_SIZE
                        && target.hasDick() && user.breastsAvailable() && target.crotchAvailable()
                        && c.getStance().paizuri(user, target)
                        && c.getStance().front(user) && user.canAct()
                        && c.getStance().en != Stance.oralpin;
    }

    @Override
    public int getMojoBuilt(Combat c, Character user) {
        return 15;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        BreastsPart breasts = user.body.getLargestBreasts();
        // try to find a set of breasts large enough, if none, default to
        // largest.
        for (int i = 0; i < 3; i++) {
            BreastsPart otherbreasts = user.body.getRandomBreasts();
            if (otherbreasts.getSize() > MIN_REQUIRED_BREAST_SIZE) {
                breasts = otherbreasts;
                break;
            }
        }
        
        int fetishChance = 7 + breasts.getSize() + user.get(Attribute.fetishism) / 2;

        int m = 5 + Random.random(5) + breasts.getSize();
        
        if(user.is(Stsflag.oiled)) {
            m += Random.random(2, 5);
        }
        
        if( user.has(Trait.lactating)) {
            m += Random.random(3, 5);
            fetishChance += 5;
        }
        
        if (user.has(Trait.temptingtits)) {
            
            m += Random.random(4, 8);
            fetishChance += 10;
        }
        
        if (user.has(Trait.beguilingbreasts)) {
            m *= 1.5;            
            fetishChance *= 2;
        }

        if (target.human()) {
            c.write(user, receive(user, breasts));
        } else {
            c.write(user, deal(c, 0, Result.normal, user, target));
        }
        target.body.pleasure(user, user.body.getRandom("breasts"), target.body.getRandom("cock"), m, c, new SkillUsage<>(this, user, target));
        if (Random.random(100) < fetishChance) {
            target.add(c, new BodyFetish(target.getType(), user.getType(), BreastsPart.a.getType(), .05 + (0.01 * breasts.getSize()) + user.get(Attribute.fetishism) * .01));
        }
        if (user.has(Trait.temptingtits)) {
            target.temptWithSkill(c, user, user.body.getRandom("breasts"), m/5, this);
        }
        return true;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.get(Attribute.seduction) >= 20 && user.hasBreasts();
    }

    @Override
    public int speed(Character user) {
        return 4;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        StringBuilder b = new StringBuilder();
        b.append("You squeeze ");
        b.append(target.possessivePronoun());
        b.append(" dick between your ");
        
        b.append(user.body.getRandomBreasts().describe(user));
        if( user.has(Trait.lactating))
        {
            b.append(" and milk squirts from your lactating teats");
        }
        b.append(". ");       
        
        if(user.is(Stsflag.oiled)){
            b.append("You rub your oiled tits up and down ");
            b.append(target.possessivePronoun()) ;
            b.append(" shaft and teasingly lick the tip.");
        }else{
            b.append("You rub them up and down ");
            b.append(target.possessivePronoun());
            b.append(" shaft and teasingly lick the tip.");
        }
        
        if (user.has(Trait.temptingtits)) {
            b.append(" Upon seeing your perfect tits around ");
            b.append(target.possessivePronoun());
            b.append(" cock, ");
            b.append(target.getName());
            b.append(" shudders with lust");
       
            if (user.has(Trait.beguilingbreasts)) {
                b.append(" and due to your beguiling nature, ").append(target.possessiveAdjective())
                                .append(" can't help drooling at the show.");
            }
            else  {
                b.append(".");
            }
        }
        
        return b.toString();
    }

    public String receive(Character user, BreastsPart breasts) {
        StringBuilder b = new StringBuilder();
        b.append(user.getName()).append(" squeezes your dick between her ");
        b.append(breasts.describe(user));
        if( user.has(Trait.lactating))
        {
            b.append(" and milk squirts from her lactating teats");
        }
        b.append(". ");       
        
        if(user.is(Stsflag.oiled)){
            b.append("She rubs her oiled tits up and down your shaft and teasingly licks your tip.");
        }
        else{
            b.append("She rubs them up and down your shaft and teasingly licks your tip.");
        }
        
        if (user.has(Trait.temptingtits)) {
            b.append(" The sight of those perfect tits around your cock causes you to shudder with lust");
            
            if (user.has(Trait.beguilingbreasts)) {
                b.append(" and due to ");
                b.append(user.getName()) ;
                b.append("'s breasts beguiling nature, you can't help but enjoy the show.");
            }
            else  {
                b.append(".");
            }
        }
        
        return b.toString();
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Rub your opponent's dick between your boobs";
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
