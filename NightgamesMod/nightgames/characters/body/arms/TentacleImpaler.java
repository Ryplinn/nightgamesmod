package nightgames.characters.body.arms;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.TentaclePart;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.TentacleFuck;
import nightgames.characters.body.mods.PartMod;
import nightgames.combat.Combat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TentacleImpaler extends TentacleArm {
    public TentaclePart part;

    public TentacleImpaler(ArmManager manager, Optional<? extends PartMod> mod) {
        super(manager, ArmType.TENTACLE_IMPALER);
        part = new TentaclePart("impaler tentacle", "back", "slime", 0.0, 1.0, 0.0);
        if (mod.isPresent()) {
            part = (TentaclePart) part.applyMod(mod.get());
        }
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Collections.singletonList(new TentacleFuck());
    }

    @Override
    int attackOdds(Combat c, Character owner, Character target) {
        return (int) Math.min(60, 5 + owner.getAttribute(Attribute.slime) * .6);
    }

    @Override
    public TentaclePart getPart() {
        return part;
    }
}
