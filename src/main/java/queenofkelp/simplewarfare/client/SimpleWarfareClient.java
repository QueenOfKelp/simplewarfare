package queenofkelp.simplewarfare.client;

import net.fabricmc.api.ClientModInitializer;
import queenofkelp.simplewarfare.client.keybinds.QKeybinds;
import queenofkelp.simplewarfare.networking.QPackets;


public class SimpleWarfareClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        QPackets.registerS2CPackets();

        QKeybinds.initialize();
    }

}
