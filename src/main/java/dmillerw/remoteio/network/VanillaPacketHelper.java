package dmillerw.remoteio.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/**
 * @author dmillerw
 */
public class VanillaPacketHelper {

    public static void sendToAllWatchingTile(TileEntity tile, Packet packet) {
        if (!tile.hasWorldObj()) {
            return;
        }

        sendToAllWatchingChunk(tile.getWorldObj().getChunkFromBlockCoords(tile.xCoord, tile.zCoord), packet);
    }

    /**
     * Sends the specified packet to all players either in specified chunk, or at least have that chunk loaded
     */
    public static void sendToAllWatchingChunk(Chunk chunk, Packet packet) {
        ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
        World world = chunk.worldObj;

        if (world instanceof WorldServer) {
            PlayerManager playerManager = ((WorldServer) world).getPlayerManager();
            for (Object obj : manager.playerEntityList) {
                EntityPlayerMP player = (EntityPlayerMP) obj;

                if (playerManager.isPlayerWatchingChunk(player, chunk.xPosition, chunk.zPosition)) {
//					if (!player.loadedChunks.contains(new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition))) {
                    player.playerNetServerHandler.sendPacket(packet);
//					}
                }
            }
        }
    }

    public static void sendToAllInDimension(int dimension, Packet packet) {
        ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();

        for (Object obj : manager.playerEntityList) {
            EntityPlayerMP player = (EntityPlayerMP) obj;

            if (player.getEntityWorld().provider.dimensionId == dimension) {
                player.playerNetServerHandler.sendPacket(packet);
            }
        }
    }

    public static void sendToAllInRange(int dimension, int x, int y, int z, int range, Packet packet) {
        ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();

        for (Object obj : manager.playerEntityList) {
            EntityPlayerMP player = (EntityPlayerMP) obj;

            if (player.getEntityWorld().provider.dimensionId == dimension && player.getDistance(x, y, z) <= range) {
                player.playerNetServerHandler.sendPacket(packet);
            }
        }
    }
}
