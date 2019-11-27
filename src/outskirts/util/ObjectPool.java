package outskirts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectPool<T> {

    private Class<T> clazz;

    private List<T> list = new ArrayList<>();

    private ObjectPool(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T get() {
        if (list.isEmpty()) {
            return create();
        } else {
            return list.remove(list.size() - 1);
        }
    }

    public void release(T obj) {
        list.add(obj);
    }

    private T create() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }





    private static final ThreadLocal<Map<Class, ObjectPool>> THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    @SuppressWarnings("unchecked")
    public static <T> ObjectPool<T> get(Class<T> clazz) {
        Map<Class, ObjectPool> localMap = THREAD_LOCAL.get();

        if (!localMap.containsKey(clazz)) {
            localMap.put(clazz, new ObjectPool<>(clazz));
        }

        return (ObjectPool<T>) localMap.get(clazz);
    }
}
