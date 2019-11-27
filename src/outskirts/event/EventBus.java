package outskirts.event;

import outskirts.util.Validate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

public class EventBus {

    private static class Handler implements Comparable<Handler> {
        private int priority;
        private Object method; // Method / Consumer
        private Object owner;  // only for reflect method.invoke() require.
        private boolean ignoreCancelled;

        private Handler(Object method, Object owner, int priority, boolean ignoreCancelled) {
            Validate.isTrue(method instanceof Method || method instanceof Consumer, "method only support 'java.lang.reflect.Method' or 'java.util.function.Consumer'");
            this.method = method;
            this.owner = owner;
            this.priority = priority;
            this.ignoreCancelled = ignoreCancelled;
        }

        @SuppressWarnings("unchecked")
        private void invoke(boolean cancelled, Event event) {
            if (cancelled && !ignoreCancelled) {
                return;
            }
            try {
                if (method instanceof Method) {
                    ((Method)method).invoke(owner, event);
                } else {
                    ((Consumer)method).accept(event);
                }
            } catch (Throwable t) {
                throw new RuntimeException("An exception occurred on EventHandler execution", t);
            }
        }

        @Override
        public int compareTo(Handler o) {
            return this.priority - o.priority;
        }
    }

    //          Event  Handler(s)
    private Map<Class, List<Handler>> eventHandlers = new HashMap<>();


    //should support static class/methods? f support, that may have some problem about complexity
    //e.g does static-listener using non-static-method..? instanced-listener using static-method..?
    /**
     * register each method in the listener as EventHandler which passed following several condition:
     * 1.method have @EventHandler annotation
     * 2.method is non-static
     * 3.method have only one param and the param is extends Event.class class
     * that method will be register. When event happen, that EventHandler(method) will be call
     *
     * EventHandler(method) support not public(you can public/private/protected/friendly)
     *
     * @param listener instanced listener
     */
    public void register(Object listener) {
        Validate.isTrue(!(listener instanceof Class), "Class-Static-Listener is Unsupported.");
        for (Method method : listener.getClass().getDeclaredMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Validate.isTrue(!Modifier.isStatic(method.getModifiers()), "EventHandler is unsupported static method. (method: %s)", method.getName());
                Validate.isTrue(method.getParameterCount() == 1 &&
                        Event.class.isAssignableFrom(method.getParameterTypes()[0]),
                        "EventHandler method require only-one <? extends Event> parameter (method: %s)", method.getName());

                //EventHandler info
                Class<?> eventClass = method.getParameterTypes()[0];
                int priority = annotation.priority();
                boolean ignoreCancelled = annotation.ignoreCancelled();

                method.setAccessible(true);

                registerHandler(eventClass, new Handler(method, listener, priority, ignoreCancelled));
            }
        }
    }

    public <T extends Event> void register(Class<T> eventClass, Consumer<T> functionHandler, int priority, boolean ignoreCancelled) {

        registerHandler(eventClass, new Handler(functionHandler, null, priority, ignoreCancelled));
    }

    public <T extends Event> void register(Class<T> eventClass, Consumer<T> functionHandler) {
        register(eventClass, functionHandler, EventHandler.DEFAULT_PRIORITY, EventHandler.DEFAULT_IGNORE_CANCELLED);
    }

    /**
     * @param listenerOrHandler Listener-Instance / Consumer-Object (FunctionHandler)
     */
    public void unregister(Object listenerOrHandler) {
        Validate.notNull(listenerOrHandler, "null removal condition.");
        for (List<Handler> handlers : eventHandlers.values()) {
            handlers.removeIf(handler ->
                    handler.owner == listenerOrHandler || handler.method == listenerOrHandler
            );
        }
    }

    private void registerHandler(Class eventClass, Handler handler) {
        if (!eventHandlers.containsKey(eventClass)) {
            eventHandlers.put(eventClass, new ArrayList<>());
        }
        List<Handler> handlers = eventHandlers.get(eventClass);

        handlers.add(handler);

        //f last 2 handler had different priority
        if (handlers.size() >= 2 && handlers.get(handlers.size() - 2).priority != handler.priority) {
            handlers.sort(Comparator.reverseOrder()); //a big operation
        }
    }

    /**
     * perform a Event, the EventHandler(s) will be call which belong the Event and registered in this EventBus's instance
     * @return if true, the event has be cancelled. (only probably return true when the Event implements Cancellable)
     */
    public boolean post(Event event) { //should name as execute..?
        boolean cancelled = false;
        boolean isCancellable = event instanceof Cancellable;

        List<Handler> handlers = eventHandlers.get(event.getClass());
        if (handlers == null) //if the event not have handlers
            return false;

        //execute every EventHandler
        for (Handler handler : handlers) {

            handler.invoke(cancelled, event);

            if (isCancellable) {
                cancelled = ((Cancellable)event).isCancelled();
            }
        }

        return cancelled;
    }

}
