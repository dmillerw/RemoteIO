package dmillerw.remoteio.core.handler;

import cpw.mods.fml.common.network.IGuiHandler;
import dmillerw.remoteio.client.gui.*;
import dmillerw.remoteio.inventory.InventoryItem;
import dmillerw.remoteio.inventory.container.*;
import dmillerw.remoteio.tile.TileRemoteInterface;
import dmillerw.remoteio.tile.TileRemoteInventory;
import dmillerw.remoteio.tile.TileTransceiver;
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
    public static final int GUI_SIMPLE_CAMO = 4;
    public static final int GUI_SET_CHANNEL = 5;
    public static final int GUI_PDA = 6;

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

            case GUI_SIMPLE_CAMO:
                return new ContainerSimpleCamo(player, new InventoryItem(player.getCurrentEquippedItem(), 1));

            case GUI_SET_CHANNEL:
                return new ContainerNull();

            case GUI_PDA:
                return null;
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

            case GUI_SIMPLE_CAMO:
                return new GuiSimpleCamo(player, new InventoryItem(player.getCurrentEquippedItem(), 1));

            case GUI_SET_CHANNEL: {
                if (y > 0)
                    return new GuiTileSetChannel((TileTransceiver) world.getTileEntity(x, y, z));
                else
                    return new GuiItemSetChannel(player.getHeldItem());
            }

            case GUI_PDA:
                return new GuiDocumentation();
        }

        return null;
    }
}
