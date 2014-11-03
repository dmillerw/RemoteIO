package dmillerw.remoteio.core;

import appeng.api.AEApi;
import appeng.api.networking.*;
import appeng.api.util.IReadOnlyCollection;
import appeng.util.ReadOnlyCollection;
import com.google.common.collect.Lists;
import dmillerw.remoteio.tile.TileRemoteInterface;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.List;

/**
 * @author dmillerw
 */
public class LinkedGridNode implements IGridNode {

    private final WeakReference<IGridNode> parentNode;
    private final WeakReference<IGridNode> ourNode;

    private final TileRemoteInterface parentInterface;

    public LinkedGridNode(IGridNode parentNode, TileRemoteInterface parentInterface) {
        this.parentNode = new WeakReference<IGridNode>(parentNode);
        this.ourNode = new WeakReference<IGridNode>(AEApi.instance().createGridNode(parentInterface));
        this.parentInterface = parentInterface;
    }

    private IGridNode getParentNode() {
        return parentNode != null ? parentNode.get() : null;
    }

    private IGridNode getOurNode() {
        return ourNode != null ? ourNode.get() : null;
    }

    @Override
    public void beginVisit(IGridVisitor g) {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        if (gridNode != null) gridNode.beginVisit(g); else if (ourNode != null) ourNode.beginVisit(g);
    }

    @Override
    public void updateState() {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        if (gridNode != null) gridNode.updateState();
        if (ourNode != null) ourNode.updateState();
    }

    @Override
    public IGridHost getMachine() {
        return parentInterface;
    }

    @Override
    public IGrid getGrid() {
        IGridNode gridNode = getParentNode();
        return gridNode != null ? gridNode.getGrid() : null;
    }

    @Override
    public void destroy() {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        if (gridNode != null) gridNode.destroy();
        if (ourNode != null) ourNode.destroy();
    }

    @Override
    public World getWorld() {
        return parentInterface.getWorldObj();
    }

    @Override
    public EnumSet<ForgeDirection> getConnectedSides() {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        EnumSet<ForgeDirection> enumSet = EnumSet.noneOf(ForgeDirection.class);
        if (gridNode != null) enumSet.addAll(gridNode.getConnectedSides());
        if (ourNode != null) enumSet.addAll(ourNode.getConnectedSides());
        return enumSet;
    }

    @Override
    public IReadOnlyCollection<IGridConnection> getConnections() {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        List<IGridConnection> list = Lists.newArrayList();
        if (gridNode != null) {
            for (IGridConnection gridConneciton : gridNode.getConnections()) {
                list.add(gridConneciton);
            }
        }
        if (ourNode != null) {
            for (IGridConnection gridConneciton : ourNode.getConnections()) {
                list.add(gridConneciton);
            }
        }
        return new ReadOnlyCollection<IGridConnection>(list);
    }

    @Override
    public IGridBlock getGridBlock() {
        return parentInterface;
    }

    @Override
    public boolean isActive() {
        return parentInterface.hasTransferChip(TransferType.NETWORK_AE);
    }

    @Override
    public void loadFromNBT(String name, NBTTagCompound nodeData) {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        if (gridNode != null) gridNode.loadFromNBT(name, nodeData);
        if (ourNode != null) ourNode.loadFromNBT(name, nodeData);
    }

    @Override
    public void saveToNBT(String name, NBTTagCompound nodeData) {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        if (gridNode != null) gridNode.saveToNBT(name, nodeData);
        if (ourNode != null) ourNode.saveToNBT(name, nodeData);
    }

    @Override
    public boolean meetsChannelRequirements() {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        return (gridNode != null && gridNode.meetsChannelRequirements()) || (ourNode != null && ourNode.meetsChannelRequirements());
    }

    @Override
    public boolean hasFlag(GridFlags flag) {
        IGridNode gridNode = getParentNode();
        IGridNode ourNode = getOurNode();
        return (gridNode != null && gridNode.hasFlag(flag)) || (ourNode != null && ourNode.hasFlag(flag));
    }

    @Override
    public void setPlayerID(int playerID) {
        IGridNode gridNode = getParentNode();
        if (gridNode != null) gridNode.setPlayerID(playerID);
    }

    @Override
    public int getPlayerID() {
        IGridNode gridNode = getParentNode();
        return gridNode != null ? gridNode.getPlayerID() : 0;
    }
}
