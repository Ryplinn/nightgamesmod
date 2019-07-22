package nightgames.modifier.status;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.modifier.ModifierCategory;
import nightgames.modifier.ModifierComponent;
import nightgames.status.Status;

import java.util.function.Function;

public class StatusModifier implements ModifierCategory<StatusModifier>, ModifierComponent {
    public static final StatusModifierCombiner combiner = new StatusModifierCombiner();

    private String name;
    private final Function<CharacterType, Status> statusBuilder;
    private final boolean playerOnly;

    public StatusModifier(Function<CharacterType, Status> statusBuilder, String name, boolean playerOnly) {
        this.statusBuilder = statusBuilder;
        this.name = name;
        this.playerOnly = playerOnly;
    }

    StatusModifier() {
        statusBuilder = null;
        playerOnly = true;
    }

    public void apply(Character c) {
        if (statusBuilder != null && (!playerOnly || c.human())) {
            Status status = statusBuilder.apply(c.getType());
            if (status != null) {
                c.addNonCombat(status);
            }
        }
    }

    public StatusModifier combine(StatusModifier next) {
        StatusModifier first = this;
        return new StatusModifier() {
            @Override
            public void apply(Character c) {
                first.apply(c);
                next.apply(c);
            }

            @Override public String toString() {
                return first.toString() + " and " + next.toString();
            }
        };
    }

    @Override
    public String toString() {
        if (statusBuilder == null) {
            return "null StatusModifier";
        }
        return name;
    }

    @Override public String name() {
        assert statusBuilder != null;
        return "status-modifier-" + name;
    }
}
