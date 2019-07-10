package nightgames.characters.body.arms;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.body.TentaclePart;
import nightgames.characters.body.arms.skills.ArmSkill;
import nightgames.characters.body.arms.skills.TentacleSuck;
import nightgames.characters.body.mods.PartMod;
import nightgames.combat.Combat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TentacleSucker extends TentacleArm {
    private TentaclePart part;

    public TentacleSucker(ArmManager manager, Optional<? extends PartMod> mod) {
        super(manager, ArmType.TENTACLE_SUCKER);
        part = new TentaclePart("tentacle sucker", "back", "slime", 0.0, 1.0, 0.0);
        if (mod.isPresent()) {
            part = (TentaclePart) part.applyMod(mod.get());
        }
    }

    @Override
    List<ArmSkill> getSkills(Combat c, Character owner, Character target) {
        return Collections.singletonList(new TentacleSuck());
    }

    @Override
    int attackOdds(Combat c, Character owner, Character target) {
        return (int) Math.min(60, 5 + owner.get(Attribute.slime) * .6);
    }

    @Override
    public TentaclePart getPart() {
        return part;
    }
}
