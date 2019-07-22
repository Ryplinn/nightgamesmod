package nightgames.items;

import nightgames.characters.Character;
import nightgames.characters.CharacterType;
import nightgames.combat.Combat;
import nightgames.status.Status;

import java.util.function.BiFunction;

public class BuffEffect extends ItemEffect {
    private BiFunction<CharacterType, CharacterType, Status> buffSupplier;

    public BuffEffect(String verb, String otherverb, BiFunction<CharacterType, CharacterType, Status> buffSupplier) {
        super(verb, otherverb, true, true);
        this.buffSupplier = buffSupplier;
    }

    @Override
    public boolean use(Combat c, Character user, Character opponent, Item item) {
        user.add(c, buffSupplier.apply(user.getType(), opponent.getType()));
        return true;
    }
}
