package outskirts.util.registry;

import outskirts.util.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Registry<T> {

    //RegistryID always is String, but sometimes not, e.g Packet registerID is Integer, it needs more simple data
    protected HashMap<Object, T> map = new HashMap<>();

    public abstract T register(T entry);

    protected final T register(Object registryID, T entry) {
        Validate.notNull(entry, "Registration must not be null (%s)", registryID);
        Validate.notNull(registryID, "RegistryID must not be null (%s)", entry.getClass().toString());
        Validate.validState(!containsKey(registryID), "%s's registryID '%s' is already been registered.", entry.toString(), registryID);
        Validate.validState(getRegistryID(entry) == null, "Registration %s(registryID='%s') is already been registered.", entry.toString(), registryID);

        map.put(registryID, entry);

        return entry;
    }

    public int size() {
        return map.size();
    }

    public Collection<T> values() {
        return map.values();
    }

    public T get(Object registryID) {
        return map.get(registryID);
    }

    public boolean containsKey(Object registryID) {
        return map.containsKey(registryID);
    }

    /**
     * this'll useful when registration no storage registryID
     */
    public <I> I getRegistryID(T entry) {
        for (Map.Entry<Object, T> e : map.entrySet()) {
            if (e.getValue().equals(entry))
                return (I) e.getKey();
        }
        return null;
    }

    public static class ClassRegistry<T extends Class<? extends Registrable>> extends Registry<T> {
        @Override
        public T register(T entry) {
            try {
                return register(entry.newInstance().getRegistryID(), entry);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException("Failed to create new instance, require a Empty&Accessible constructor", ex);
            }
        }
    }

    public static class RegistrableRegistry<T extends Registrable> extends Registry<T> {
        @Override
        public T register(T entry) {
            return register(entry.getRegistryID(), entry);
        }
    }

}
