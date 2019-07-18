package nightgames.global;

import nightgames.characters.CharacterType;

import java.util.*;

public enum Flag {
    metBroker,
    basicStores,
    blackMarket,
    meditation,
    girlAdvice,
    CassieKnown,
    CassieDate,
    Cassievg1,
    Cassievg2,
    CassieMaravg,
    CassieLoneliness,
    JewelKnown,
    JewelDate,
    AngelKnown,
    AngelDate,
    MaraKnown,
    MaraDate,
    Maravg1,
    maravg2,
    magicstore,
    metAisha,
    blackMarketPlus,
    metRin,
    dojo,
    metSuzume,
    workshop,
    metJett,
    Eve,
    Kat,
    Reyka,
    Airi,
    rank1,
    darkness,
    fetishism,
    furry,
    dumbmode,
    autosave,
    metLilly,
    noimage,
    noportraits,
    challengeAccepted,
    largefonts,
    CarolineAffection,
    metYui,
    YuiAffection,
    threesome,
    bodyShop,
    hardmode,
    systemMessages,
    malePref,
    masturbationSemen,
    AliceAvailable,
    metAlice,
    victory,
    catspirit,
    Clue1,
    Maya,
    FTC,
    didFTC, 
    AddictionAdvice,
    AiriDisabled,
    extendedLogs, 
    YuiLoyalty, 
    YuiAvailable, 
    Yui,
    YuiWalletReturned,
    YuiUnlocking,
    PCFemalePronounsOnly,
    NPCFemalePronounsOnly,
    skipMM,
    skipFF,
    hermHasBalls,
    shemaleNoBalls,
    femaleTGIntoHerm, 
    SuperTraitMode,
    AutoNext,
    NoFTC,
    LegacyCharAvailableSave
    ;

    static Set<String> flags = new HashSet<>();
    static Map<String, Float> counters;
    private static String DISABLED_FORMAT = "%sDisabled";

    // TODO: de-static this and make it part of the game state.
    static {
        resetCounters();
    }

    static void resetCounters() {
        counters = new HashMap<>();
        counters.put(malePref.name(), 0.f);
    }

    public static boolean exists(String flag) {
        return Arrays.stream(values()).anyMatch(f -> f.name().equals(flag));
    }

    public static void flag(String f) {
        flags.add(f);
    }

    public static void unflag(String f) {
        flags.remove(f);
    }

    public static void flag(Flag f) {
        flags.add(f.name());
    }

    public static void unflag(Flag f) {
        flags.remove(f.name());
    }

    public static void setFlag(String f, boolean value) {
        if (value) {
            flag(f);
        } else {
            unflag(f);
        }
    }

    public static void setFlag(Flag f, boolean value) {
        if (value) {
            flags.add(f.name());
        } else {
            flags.remove(f.name());
        }
    }

    public static boolean checkFlag(Flag f) {
        return flags.contains(f.name());
    }

    public static boolean checkFlag(String key) {
        return flags.contains(key);
    }

    public static float getValue(Flag f) {
        if (!counters.containsKey(f.name())) {
            return 0;
        } else {
            return counters.get(f.name());
        }
    }

    public static void modCounter(Flag f, float inc) {
        counters.put(f.name(), getValue(f) + inc);
    }

    public static void setCounter(Flag f, float val) {
        counters.put(f.name(), val);
    }

    public static boolean checkCharacterDisabledFlag(CharacterType type) {
        return checkFlag(String.format(DISABLED_FORMAT, type));
    }

    public static void setCharacterDisabledFlag(CharacterType type) {
        flag(String.format(DISABLED_FORMAT, type));
    }

    public static void unsetCharacterDisabledFlag(CharacterType type) {
        unflag(String.format(DISABLED_FORMAT, type));
    }
}
