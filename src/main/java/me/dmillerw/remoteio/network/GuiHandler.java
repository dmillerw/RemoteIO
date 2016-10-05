package me.dmillerw.remoteio.network;

import me.dmillerw.remoteio.client.gui.GuiFrequencyTemp;
import me.dmillerw.remoteio.core.frequency.IFrequencyProvider;
import me.dmillerw.remoteio.item.ItemPocketGadget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created by dmillerw
 */
public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (y < 0) {
            return new GuiFrequencyTemp(new IFrequencyProvider() {
                @Override
                public int getFrequency() {
                    return ItemPocketGadget.getFrequency(player.getActiveItemStack());
                }

                @Override
                public void setFrequency(int frequency) {

                }

                @Override
                public BlockPos getPosition() {
                    return null;
                }
            });
        } else {
            return new GuiFrequencyTemp((IFrequencyProvider) world.getTileEntity(new BlockPos(x, y, z)));
        }
    }
}
