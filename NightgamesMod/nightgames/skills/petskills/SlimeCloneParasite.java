package nightgames.skills.petskills;

import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.body.mods.ParasitedMod;
import nightgames.characters.trait.Trait;
import nightgames.combat.Combat;
import nightgames.global.Formatter;
import nightgames.global.Random;
import nightgames.nskills.tags.SkillTag;
import nightgames.pet.PetCharacter;
import nightgames.skills.Tactics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SlimeCloneParasite extends SimpleEnemySkill {
    public SlimeCloneParasite() {
        super("Parasitism");
        addTag(SkillTag.debuff);
    }

    public float priorityMod(Combat c, Character user) {
        return 10.0f;
    }

    @Override
    public int getMojoCost(Combat c, Character user) {
        return 25;
    }

    @Override
    public boolean requirements(Combat c, Character user, Character target) {
        return super.requirements(c, user, target) && user instanceof PetCharacter && ((PetCharacter)user).getSelf().owner().has(Trait.MimicBodyPart);
    }

    private final static List<String> PARASITEABLE_PARTS = Arrays.asList("cock", "pussy", "ass", "mouth");
    @Override
    public boolean resolve(Combat c, Character user, Character target) {
        if (target.roll(user, accuracy(c, user, target))) {
            List<BodyPart> possibleTargets = new ArrayList<>();
            for (String type : PARASITEABLE_PARTS) {
                if (!target.body.getRandom(type).moddedPartCountsAs(target, ParasitedMod.INSTANCE)) {
                    possibleTargets.add(target.body.getRandom(type));
                }
            }
            Optional<BodyPart> result = Random.pickRandom(possibleTargets);
            if (result.isPresent()) {
                BodyPart targetPart = result.get();
                c.write(user, Formatter.format("{self:SUBJECT-ACTION:launch} {self:reflective} at {other:name-possessive} %s. "
                                + "{other:PRONOUN-ACTION:try} to dodge out of the way, but it's no use. "
                                + "{self:NAME-POSSESSIVE} gelatinous body has deformed around {other:possessive} %s and manages "
                                + "to crawl inside {other:possessive} body somehow!",
                                user, target, targetPart.describe(target), targetPart.getType()));
                target.body.temporaryAddPartMod(targetPart.getType(), ParasitedMod.INSTANCE, 10);
            }
            return true;
        }
        c.write(user, Formatter.format("{self:SUBJECT-ACTION:launch} {self:reflective} at {other:name-do}, but {other:pronoun-action:dodge} away in time.", user, target));
        return false;
    }

    @Override
    public int speed(Character user) {
        return 8;
    }

    @Override
    public Tactics type(Combat c, Character user) {
        return Tactics.pleasure;
    }

    @Override
    public boolean makesContact() {
        return true;
    }
}
