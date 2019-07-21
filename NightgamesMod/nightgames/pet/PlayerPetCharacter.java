package nightgames.pet;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.combat.Combat;
import nightgames.items.clothing.Outfit;
import nightgames.skills.SkillPool;

import java.util.HashMap;

public class PlayerPetCharacter extends PetCharacter {

    PlayerPetCharacter(String name, Pet self, Player prototypeCharacter, int level) throws CloneNotSupportedException {
        super(self, name, prototypeCharacter.getType() + "Pet", prototypeCharacter.getGrowth(), 1);
        Player prototype = (Player) prototypeCharacter.clone();
        prototype.applyBasicStats(this);
        for (int i = 1; i < level; i++) {
            getGrowth().levelUp(this);
            prototype.getLevelUpFor(i).apply(this);
            this.level += 1;
        }
        this.att = new HashMap<>(prototype.att);
        this.clearTraits();
        prototype.getTraitsPure().forEach(this::addTraitDontSaveData);
        this.getSkills().clear();
        this.body = prototypeCharacter.body.clone(this);
        this.outfit = new Outfit(prototypeCharacter.outfit);
        this.mojo.empty();
        this.arousal.empty();
        this.stamina.fill();
        SkillPool.learnSkills(this);
    }

    @Override
    public String bbLiner(Combat c, Character target) {
        return "";
    }

    @Override
    public String nakedLiner(Combat c, Character target) {
        return "";
    }

    @Override
    public String stunLiner(Combat c, Character target) {
        return "";
    }

    @Override
    public String taunt(Combat c, Character target) {
        return "";
    }

    @Override
    public String temptLiner(Combat c, Character target) {
        return "";
    }

    @Override
    public String challenge(Character other) {
        return "";
    }

    @Override
    public String getPortrait(Combat c) {
        return "";
    }
}
