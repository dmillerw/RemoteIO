package remoteio.common.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import remoteio.common.lib.DimensionalCoords;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author dmillerw
 */
public class ChannelRegistry {

    private Map<Integer, DimensionalCoords> channelDataMap;
    private Set<Integer> dirtyChannels;

    public ChannelRegistry() {
        channelDataMap = Maps.newHashMap();
        dirtyChannels = Sets.newHashSet();

        MinecraftForge.EVENT_BUS.register(this);
    }

    public DimensionalCoords getChannelData(int channel) {
        return channelDataMap.get(channel);
    }

    public void setChannelData(int channel, DimensionalCoords coords) {
        channelDataMap.put(channel, coords);
        dirtyChannels.add(channel);
    }

    public boolean pollDirty(int channel) {
        if (dirtyChannels.contains(channel)) {
            dirtyChannels.remove(channel);
            return true;
        }
        return false;
    }

    private File getFile(ISaveHandler saveHandler) {
        return new File(saveHandler.getWorldDirectory(), "channels.rio");
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.provider.dimensionId != 0)
            return;

        File file = getFile(event.world.getSaveHandler());

        if (!file.exists())
            return;

        channelDataMap.clear();
        dirtyChannels.clear();

        try {
            NBTTagCompound nbtTagCompound = CompressedStreamTools.read(file);

            if (nbtTagCompound != null) {
                NBTTagList nbtTagList = nbtTagCompound.getTagList("data", Constants.NBT.TAG_COMPOUND);

                for (int i=0; i<nbtTagList.tagCount(); i++) {
                    NBTTagCompound dataTag = nbtTagList.getCompoundTagAt(i);
                    channelDataMap.put(dataTag.getInteger("channel"), DimensionalCoords.fromNBT(dataTag.getCompoundTag("coords")));
                    dirtyChannels.add(dataTag.getInteger("channel"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (event.world.provider.dimensionId != 0)
            return;

        File file = getFile(event.world.getSaveHandler());

        try {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            NBTTagList nbtTagList = new NBTTagList();

            for (Map.Entry<Integer, DimensionalCoords> entry : channelDataMap.entrySet()) {
                NBTTagCompound dataTag = new NBTTagCompound();
                dataTag.setInteger("channel", entry.getKey());
                NBTTagCompound coordTag = new NBTTagCompound();
                entry.getValue().writeToNBT(coordTag);
                dataTag.setTag("coords", coordTag);
                nbtTagList.appendTag(dataTag);
            }

            nbtTagCompound.setTag("data", nbtTagList);

            CompressedStreamTools.write(nbtTagCompound, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
