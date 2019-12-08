package de.guntram.mcmod.durabilityviewer.mixin;

import de.guntram.mcmod.durabilityviewer.DurabilityViewer;
import de.guntram.mcmod.durabilityviewer.handler.ConfigurationHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.packet.GameJoinS2CPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ConnectMixin {

    @Inject(method="onGameJoin", at=@At("RETURN"))
    private void onConnectedToServerEvent(GameJoinS2CPacket packet, CallbackInfo cbi) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        MinecraftClient mc=MinecraftClient.getInstance();
        ServerInfo serverData = mc.getCurrentServerEntry();
        String serverName;
        if (serverData==null)
            serverName="local game";
        else
            serverName = serverData.name;
        if (serverName==null)
            serverName="unknown server";
        DurabilityViewer.setWindowTitle(mc.getSession().getUsername() + " on "+serverName);
    }
    
    @Inject(method="onDisconnected", at=@At("HEAD"))
    public void onDisconnectFromServerEvent(CallbackInfo cbi) {
        if (!ConfigurationHandler.showPlayerServerName())
            return;
        MinecraftClient mc=MinecraftClient.getInstance();
        DurabilityViewer.setWindowTitle(mc.getSession().getUsername() + " not connected");
    }
}
