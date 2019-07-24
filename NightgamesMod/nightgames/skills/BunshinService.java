package nightgames.skills;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.combat.Result;
import nightgames.global.Random;

public class BunshinService extends Skill {

    BunshinService() {
        super("Bunshin Service");
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return user.getPure(Attribute.ninjutsu) >= 12;
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
                             .penetrated(c, user)
                        && target.mostlyNude();
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return numberOfClones(user) * 2;
    }

    @Override
    public String describe(Combat c, Character user) {
        return "Pleasure your opponent with shadow clones: 4 mojo per attack (min 2))";
    }

    private int numberOfClones(Character user) {
        return Math.min(Math.min(user.getMojo().get()/2, user.get(Attribute.ninjutsu)/2), 15);
    }

    @Override
    public int accuracy(Combat c, Character user, Character target) {
        return 25 + user.get(Attribute.speed) * 5;
    }

    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        int clones = numberOfClones(user);
        Result r;
        if(user.human()){
            c.write(user, String.format("You form %d shadow clones and rush forward.",clones));
        }
        else if(c.shouldPrintReceive(target, c)){
            c.write(user, String.format("%s moves in a blur and suddenly %s %d of %s approaching %s.",user.getName(),
                            target.subjectAction("see"),clones,user.pronoun(),target.reflectivePronoun()));
        }
        for (int i = 0; i < clones; i++) {
            if (target.roll(user, accuracy(c, user, target))) {
                switch (Random.random(4)) {
                    case 0:
                        r = Result.weak;
                        target.tempt(Random.random(3) + user.get(Attribute.seduction) / 4);
                        break;
                    case 1:
                        r = Result.normal;
                        target.body.pleasure(user,  user.body.getRandom("hands"),target.body.getRandomBreasts(),
                                        Random.random(3 + user.get(Attribute.seduction) / 2)
                                                        + target.get(Attribute.perception) / 2,
                                        c, new SkillUsage<>(this, user, target));
                        break;
                    case 2:
                        r = Result.strong;
                        BodyPart targetPart = target.body.has("cock") ? target.body.getRandomCock()
                                        : target.hasPussy() ? target.body.getRandomPussy()
                                                        : target.body.getRandomAss();
                        target.body.pleasure(user, user.body.getRandom("hands"),targetPart, Random.random(4 + user.get(Attribute.seduction))
                                                        + target.get(Attribute.perception) / 2,
                                        c, new SkillUsage<>(this, user, target));
                        break;
                    default:
                        r = Result.critical;
                        targetPart = target.body.has("cock") ? target.body.getRandomCock()
                                        : target.hasPussy() ? target.body.getRandomPussy()
                                                        : target.body.getRandomAss();
                        target.body.pleasure(user,user.body.getRandom("hands"), targetPart, Random.random(6)
                                        + user.get(Attribute.seduction) / 2f + target.get(Attribute.perception), c,
                                        new SkillUsage<>(this, user, target));
                        break;
                }
                writeOutput(c, r, user, target);
            } else {
                writeOutput(c, Result.miss, user, target);
            }
        }
        return true;
    }

    @Override
    public Skill copy(Character user) {
        return new BunshinService();
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
        if(modifier==Result.miss){
            return String.format("%s dodges your clone's groping hands.",target.getName());
        }else if(modifier==Result.weak){
            return String.format("Your clone darts close to %s and kisses %s on the lips.",target.getName(),target.directObject());
        }else if(modifier==Result.strong){
            if(target.hasDick()){
                return String.format("Your shadow clone grabs %s's dick and strokes it.",target.getName());
            }else{
                return String.format("Your shadow clone fingers and caresses %s's pussy lips.",target.getName());
            }
        }else if(modifier==Result.critical){
            if(target.hasDick()){
                return String.format("Your clone attacks %s's sensitive penis, rubbing and stroking %s glans.",target.getName(),target.possessiveAdjective());
            }else{
                return String.format("Your clone slips between %s's legs to lick and suck %s swollen clit.",target.getName(),target.possessiveAdjective());
            }
        }else{
            return String.format("A clone pinches and teases %s's nipples",target.getName());
        }
    }

    @Override
    public String receive(Combat c, int damage, Result modifier, Character user, Character target) {
        if(modifier==Result.miss){
            return String.format("%s to avoid one of the shadow clones.",
                            target.subjectAction("manage"));
        }else if(modifier==Result.weak){
            return String.format("One of the %ss grabs %s and kisses %s enthusiastically.",user.getName(),
                            target.subject(), target.directObject());
        }else if(modifier==Result.strong){
            if(target.hasBalls()){
                return String.format("A clone gently grasps and massages %s sensitive balls.",
                                target.nameOrPossessivePronoun());
            }else{
                return String.format("A clone teases and tickles %s inner thighs and labia.",
                                target.nameOrPossessivePronoun());
            }
        }else if(modifier==Result.critical){
            if(target.hasDick()){
                return String.format("One of the %s clones kneels between %s legs to lick and suck %s cock.",user.getName(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective());
            }else{
                return String.format("One of the %s clones kneels between %s legs to lick %s nether lips.",user.getName(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective());
            }
        }else{
            if(user.hasBreasts()){
                return String.format("A %s clone presses her boobs against %s and teases %s nipples.",user.getName(),
                                target.subject(), target.possessiveAdjective());
            }else{
                return String.format("A %s clone caresses %s chest and teases %s nipples.",user.getName(),
                                target.nameOrPossessivePronoun(), target.possessiveAdjective());
            }
            
        }
    }

}
