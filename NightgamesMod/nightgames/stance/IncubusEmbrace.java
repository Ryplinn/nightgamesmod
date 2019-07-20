package nightgames.stance;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.characters.body.BodyPart;
import nightgames.combat.Combat;
import nightgames.skills.FondleBreasts;
import nightgames.skills.Skill;
import nightgames.status.Status;
import nightgames.status.Stsflag;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncubusEmbrace extends Position {

    private final Supplier<Status> statusBuilder;
    private final Stsflag flag;
    
    public IncubusEmbrace(CharacterType top, CharacterType bottom) {
        this(top, bottom, null, null);
    }

    public IncubusEmbrace(CharacterType top, CharacterType bottom, Supplier<Status> status, Stsflag flag) {
        super(top, bottom, Stance.incubusembrace);
        this.domType = DomType.MALEDOM;
        this.statusBuilder = status;
        this.flag = flag;
    }
    
    @Override
    public String describe(Combat c) {
        return null;
    }

    @Override
    public Optional<Position> checkOngoing(Combat c) {
        super.checkOngoing(c);
        if (c.getStance() != this)
            return Optional.empty();
        
        if (flag != null && statusBuilder != null && !getBottom().is(flag)) {
            getBottom().add(c, statusBuilder.get());
        } else if (flag == null && getBottom().hasBreasts()) {
            FondleBreasts fb = new FondleBreasts(top);
            if (Skill.skillIsUsable(c, fb, getBottom())) {
                fb.resolve(c, getBottom());
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<BodyPart> bottomParts() {
        return Stream.of(getBottom().body.getRandomAss()).filter(part -> part != null && part.present())
                        .collect(Collectors.toList());
    }
    
    @Override
    public int dominance() {
        return 4;
    }
    
    @Override
    public boolean mobile(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean kiss(Character c, Character target) {
        return c.getType() == top;
    }

    @Override
    public boolean dom(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean sub(Character c) {
        return c.getType() == bottom;
    }

    @Override
    public boolean reachTop(Character c) {
        return c.getType() == top;
    }

    @Override
    public boolean reachBottom(Character c) {
        return true;
    }

    @Override
    public boolean prone(Character c) {
        return false;
    }

    @Override
    public boolean behind(Character c) {
        return c.getType() == top;
    }

    @Override
    public String image() {
        return getTop().hasBreasts() ? "incubus_embrace.jpg" : "";
    }

}
