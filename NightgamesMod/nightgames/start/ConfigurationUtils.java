package nightgames.start;

import java.util.*;

/**
 *
 */
class ConfigurationUtils {
    static <T> T merge(T primary, T secondary) {
        if (primary != null) {
            return primary;
        } else {
            return secondary;
        }
    }

    static <T> Collection<T> mergeCollections(Collection<T> primary, Collection<T> secondary) {
        if (primary == null && secondary == null) {
            return null;
        }
        Collection<T> list = new ArrayList<>();
        if (primary != null) {
            list.addAll(primary);
        }
        if (secondary != null) {
            list.addAll(secondary);
        }
        return list;
    }

    static <K, V> Map<K, V> mergeMaps(Map<K, ? extends V> primary, Map<K, ? extends V> secondary) {
        if (primary == null && secondary == null) {
            return null;
        }
        Map<K, V> map = new HashMap<>();
        if (primary != null) {
            map.putAll(primary);
        }
        if (secondary != null) {
            map.putAll(secondary);
        }
        return map;
    }
}
