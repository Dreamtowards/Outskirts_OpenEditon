package outskirts.network;

import outskirts.event.Event;
import outskirts.util.registry.Registry;

public abstract class Packet extends Event {

    public static final Registry<Class<? extends Packet>> REGISTRY = new Registry<Class<? extends Packet>>() {
        @Override
        public Class<? extends Packet> register(Class<? extends Packet> entry) {
            return register(map.size(), entry);
        }
    };

    public static Packet createPacket(int packetID) {
        try {
            return REGISTRY.get(packetID).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to createPacket.", ex);
        }
    }

    public abstract void write(PacketBuffer buf);

    public abstract void read(PacketBuffer buf);

}
