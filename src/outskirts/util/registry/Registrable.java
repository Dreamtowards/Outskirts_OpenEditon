package outskirts.util.registry;

import outskirts.util.CollectionUtils;
import outskirts.util.ReflectionUtils;
import outskirts.util.ResourceLocation;

import java.lang.reflect.Field;

public interface Registrable {

    String getRegistryID();

    //TODO: should force impl?
    default <T extends Registrable> T setRegistryID(String registryID) {
        try {
            Field field = ReflectionUtils.getField(getClass(), "registryID", true, (clazz) -> CollectionUtils.contains(clazz.getInterfaces(), Registrable.class));
            field.setAccessible(true);

            if (field.get(this) != null) {
                throw new IllegalStateException("registryID already been initialized, can't be change again");
            }

            field.set(this, new ResourceLocation(registryID).toString());

            return (T) this;
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("A impossible exception occurred in Registrable::setRegistry() default impl", ex);
        }
    }
}
