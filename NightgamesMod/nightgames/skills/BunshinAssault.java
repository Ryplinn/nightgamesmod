package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.skills.damage.DamageType;

public class BunshinAssault extends Skill {

    BunshinAssault() {
        super("Bunshin Assault");
        addTag(SkillTag.hurt);
        addTag(SkillTag.staminaDamage);
        addTag(SkillTag.positioning);
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.ninjutsu) >= 6;
    }

    @Override
    public boolean usable(Combat c, Character user, Character target) {
        return c.getStance()
                .mobile(user)
                        && !c.getStance()
                             .prone(user)
                        && user.canAct() && !c.getStance()
                                                   .behind(target)
                        && !c.getStance()
                             .penetrated(c, target)
                        && !c.getStance()
                             .penetrated(c, user);
    }

    private int numberOfClones(Character user) {
        return Math.min(Math.min(user.getMojo().get()/2, user.getAttribute(Attribute.ninjutsu)/2), 15);
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return numberOfClones(user) * 2;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Attack your opponent with shadow clones: 2 Mojo per attack (min 2)";
    }

    @Override
    public int baseAccuracy(Combat c, Character user, Character target) {
        return 25 + user.getAttribute(Attribute.speed) * 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target, boolean rollSucceeded) {
        int clones = numberOfClones(user);
        Result r;
        if(user.human()){
            c.write(user, String.format("You form %d shadow clones and rush forward.",clones));
        }
        else if(c.shouldPrintReceive(target, c)){
            c.write(user, String.format("%s moves in a blur and suddenly %s %d of %s approaching %s.",user.getName(),
                            target.subjectAction("see"),clones,user.pronoun(),target.reflectivePronoun()));
        }
        for(int i=0;i<clones;i++){
            if(rollSucceeded) {
                switch(Random.random(4)){
                case 0:
                    r=Result.weak;
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random
                                    .random(1, 4)));
                    break;
                case 1:
                    r=Result.normal;
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random
                                    .random(2, 5)));
                    break;
                case 2:
                    r=Result.strong;
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random
                                    .random(6, 9)));
                    break;
                default:
                    r=Result.critical;
                    target.pain(c, user, (int) DamageType.physical.modifyDamage(user, target, Random
                                    .random(10, 14)));
                    break;
                }
                writeOutput(c, r, user, target);
            }else{

                writeOutput(c, Result.miss, user, target);
            }
        }
        return true;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.damage;
    }

    
    @Override
    public int speed(Character user) {
        return 4;
    }
    
    @Override
    public String deal(Combat c, int damage, Result modifier, Character user, Character target) {
        if(modifier==Result.miss){
            return String.format("%s dodges one of your shadow clones.",target.getName());
        }else if(modifier==Result.weak){
            return String.format("Your shadow clone gets behind %s and slaps %s hard on the ass.",target.getName(),target.directObject());
        }else if(modifier==Result.strong){
            if(target.hasBalls()){
                return String.format("One of your clones gets grabs and squeezes %s's balls.",target.getName());
            }else{
                return String.format("One of your clones hits %s on %s sensitive tit.",target.getName(),target.possessiveAdjective());
            }
        }else if(modifier==Result.critical){
            if(target.hasBalls()){
                return String.format("One lucky clone manages to deliver a clean kick to %s's fragile balls.",target.getName());
            }else{
                return String.format("One lucky clone manages to deliver a clean kick to %s's sensitive vulva.",target.getName());
            }
        }else{
            return String.format("One of your shadow clones lunges forward and strikes %s in the stomach.",target.getName());
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if(modifier==Result.miss){
            return String.format("%s quickly %s a shadow clone's attack.",
                            target.subject(), target.action("dodge"));
        }else if(modifier==Result.weak){
            return String.format("%s sight of one of the clones until %s %s a sharp spank on %s ass cheek.",
                            target.subjectAction("lose"), target.pronoun(), target.action("feel"),
                            target.possessiveAdjective());
        }else if(modifier==Result.strong){
            if(target.hasBalls()){
                return String.format("A %s clone gets a hold of %s balls and squeezes them painfully.",user.getName(),
                                target.nameOrPossessivePronoun());
            }else{
                return String.format("A %s clone unleashes a quick roundhouse kick that hits %s sensitive boobs.",user.getName(),
                                target.nameOrPossessivePronoun());
            }
        }else if(modifier==Result.critical){
            if(target.hasBalls()){
                return String.format("One lucky %s clone manages to land a snap-kick squarely on %s unguarded jewels.",user.getName(),
                                target.nameOrPossessivePronoun());
            }else{
                return String.format("One %s clone hits %s between the legs with a fierce cunt-punt.",user.getName(),
                                target.nameOrPossessivePronoun());
            }
        }else{
            return String.format("One of %s clones delivers a swift punch to %s solar plexus.",user.possessiveAdjective(),
                            target.nameOrPossessivePronoun());
        }
    }

}
