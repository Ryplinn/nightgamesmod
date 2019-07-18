package nightgames.characters;

import nightgames.global.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Identifier for a character.
 */
public final class CharacterType {
    // If you're wondering why this exists as well as the CharacterPool, it's because they serve different purposes:
    // The CharacterPool provides semantic information about what characters are available in the game. This cache, on
    // the other hand, is an implementation detail that keeps track of which character types the code has looked for,
    // and has nothing to do with the game.
    private static final Map<String, CharacterType> typeCache = new HashMap<>();
    private static CharacterPool lastUsedPool;
    private final String type;

    // To generate an instance of CharacterType, use CharacterType.get().
    private CharacterType(String type) {
        this.type = type;
        typeCache.put(type, this);
    }

    boolean hasType(String otherTypeName) {
        return type.equals(otherTypeName);
    }

    public static CharacterType get(String typeName) {
        return typeCache.getOrDefault(typeName, new CharacterType(typeName));
    }

    private Optional<Character> fromPool(CharacterPool pool) {
        lastUsedPool = pool;
        return Optional.ofNullable(pool.getCharacterByType(this));
    }

    /**
     * Queries the character pool for a character of this type.
     *
     * @return An optional containing a character of this type, if available.
     */
    public Optional<Character> fromPool() {
        if (lastUsedPool == null) {
            lastUsedPool = GameState.getGameState().characterPool;
        }
        return fromPool(lastUsedPool);
    }

    /**
     * As fromPool(), but for when you know for sure that this character is in the pool.
     *
     * @return The character of this type.
     */
    public Character fromPoolGuaranteed() {
        return this.fromPool().orElseThrow(() -> new CharacterPool.CharacterNotFoundException(this));
    }

    public static void usePool(CharacterPool pool) {
        lastUsedPool = pool;
    }

    @Override public String toString() {
        return type;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof String) {
            return type.equals(o);
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        CharacterType that = (CharacterType) o;
        return type.equals(that.type);
    }

    @Override public int hashCode() {
        return type.hashCode();
    }
}
