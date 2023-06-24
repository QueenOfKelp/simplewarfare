package queenofkelp.simplewarfare.client;

import net.fabricmc.api.ClientModInitializer;
import queenofkelp.simplewarfare.networking.QPackets;
import queenofkelp.simplewarfare.registry.QEntities;


public class SimpleWarfareClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        QEntities.registerClient();

        QPackets.registerS2CPackets();

        QKeybinds.initialize();
    }

}
