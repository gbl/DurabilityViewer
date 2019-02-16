package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketJoinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public abstract class ConnectMixin {

    @Inject(method="handleJoinGame", at=@At("RETURN"))
    private void onConnectedToServerEvent(SPacketJoinGame packet, CallbackInfo cbi) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        Minecraft mc=Minecraft.getInstance();
        ServerData serverData = mc.getCurrentServerData();
        String serverName;
        if (serverData==null)
            serverName="local game";
        else
            serverName = serverData.serverName;
        if (serverName==null)
            serverName="unknown server";
        DurabilityViewer.setWindowTitle(mc.getSession().getUsername() + " on "+serverName);
    }
    
    @Inject(method="onDisconnect", at=@At("HEAD"))
    public void onDisconnectFromServerEvent(CallbackInfo cbi) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        Minecraft mc=Minecraft.getInstance();
        DurabilityViewer.setWindowTitle(mc.getSession().getUsername() + " not connected");
    }
}
