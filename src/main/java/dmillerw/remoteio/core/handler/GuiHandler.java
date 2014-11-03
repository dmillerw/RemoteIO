package dmillerw.remoteio.core.handler;

import cpw.mods.fml.common.network.IGuiHandler;
import dmillerw.remoteio.client.gui.GuiIntelligentWorkbench;
import dmillerw.remoteio.client.gui.GuiRFConfig;
import dmillerw.remoteio.client.gui.GuiRemoteInterface;
import dmillerw.remoteio.client.gui.GuiRemoteInventory;
import dmillerw.remoteio.inventory.container.ContainerIntelligentWorkbench;
import dmillerw.remoteio.inventory.container.ContainerNull;
import dmillerw.remoteio.inventory.container.ContainerRemoteInterface;
import dmillerw.remoteio.inventory.container.ContainerRemoteInventory;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.TileRemoteInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class GuiHandler implements IGuiHandler {

    public static final int GUI_REMOTE_INTERFACE = 0;
    public static final int GUI_RF_CONFIG = 1;
    public static final int GUI_REMOTE_INVENTORY = 2;
    public static final int GUI_INTELLIGENT_WORKBENCH = 3;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GUI_REMOTE_INTERFACE:
                return new ContainerRemoteInterface(player.inventory, (TileRemoteInterface) world.getTileEntity(x, y, z));

            case GUI_RF_CONFIG:
                return new ContainerNull();

            case GUI_REMOTE_INVENTORY:
                return new ContainerRemoteInventory(player.inventory, (TileRemoteInventory) world.getTileEntity(x, y, z));

            case GUI_INTELLIGENT_WORKBENCH:
                return new ContainerIntelligentWorkbench(player.inventory, world, x, y, z);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case GUI_REMOTE_INTERFACE:
                return new GuiRemoteInterface(player.inventory, (TileRemoteInterface) world.getTileEntity(x, y, z));

            case GUI_RF_CONFIG:
                return new GuiRFConfig(player.getHeldItem());

            case GUI_REMOTE_INVENTORY:
                return new GuiRemoteInventory(player.inventory, (TileRemoteInventory) world.getTileEntity(x, y, z));

            case GUI_INTELLIGENT_WORKBENCH:
                return new GuiIntelligentWorkbench(player.inventory, world, x, y, z);
        }

        return null;
    }
}
