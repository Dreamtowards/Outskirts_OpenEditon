package outskirts.init;

import ext.testing.CPacketStrMsg;
import outskirts.network.Packet;

public class Bootstrap {

    private static void registerPackets() {

        Packet.REGISTRY.register(CPacketStrMsg.class);

    }


    //use event to register/release..?
    public static void register() {

        registerPackets();



        BlockTextures.initAndBuild();

    }

}
