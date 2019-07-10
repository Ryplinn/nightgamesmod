package nightgames.nskills.tags;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.BodyPart;
import nightgames.characters.trait.Trait;
import nightgames.nskills.struct.SkillResultStruct;
import nightgames.status.Stsflag;

import java.util.Set;

public class WorshipSkillTag extends SkillTag {
    private static final SkillRequirement requirement = new SkillRequirement() {
        @Override
        public boolean meets(SkillResultStruct results, double value) {
            Character self = results.getSelf().getCharacter();
            Character other = results.getOther().getCharacter();
            Set<BodyPart> otherParts = results.getOther().getParts();
            if (self.is(Stsflag.enthralled) || self.is(Stsflag.lovestruck) || self.is(Stsflag.charmed)) {
                if (!(value < results.getRoll() * 100)) {
                    //mental resist check failed
                    return true;
                }
            }
            if (other.has(Trait.objectOfWorship)) {
                // check roll for worship
                if (!(value + other.get(Attribute.divinity) < results.getRoll() * 100)) {
                    //mental resist check failed
                    return true;
                }
            }
            if (otherParts.stream().anyMatch(part -> self.body.getFetish(part.getType()).isPresent())) {
                // check for fetish on the part
                return true;
            }
            return false;
        }
    };

    @Override
    public SkillRequirement getRequirements() {
        return requirement;
    }

    @Override
    public SkillRequirement getUsableRequirements() {
        return requirement;
    }

    @Override
    public String getName() {
        return "Worship";
    }
}
