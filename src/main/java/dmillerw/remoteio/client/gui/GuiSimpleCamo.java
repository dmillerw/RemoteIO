package dmillerw.remoteio.client.gui;

import dmillerw.remoteio.inventory.InventoryItem;
import dmillerw.remoteio.inventory.container.ContainerSimpleCamo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author dmillerw
 */
public class GuiSimpleCamo extends GuiContainer {

	public GuiSimpleCamo(EntityPlayer player, InventoryItem inventory) {
		super(new ContainerSimpleCamo(player, inventory));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

	}

}
