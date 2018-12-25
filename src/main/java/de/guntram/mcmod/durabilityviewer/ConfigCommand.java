/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.durabilityviewer;

import com.mojang.brigadier.CommandDispatcher;
import de.guntram.mcmod.durabilityviewer.client.gui.GuiConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import static net.minecraft.command.Commands.literal;

/**
 *
 * @author gbl
 */
class ConfigCommand {

    static void register(CommandDispatcher<CommandSource> cd) {
        cd.register(
            literal("configdurabilityviewer")
                .executes (c -> {
                    Minecraft.getInstance().displayGuiScreen(new GuiConfig());
                    return 1;
            })
        );
    }
}
