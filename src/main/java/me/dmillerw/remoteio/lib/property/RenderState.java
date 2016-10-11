package me.dmillerw.remoteio.lib.property;

import net.minecraft.block.state.IBlockState;

/**
 * Created by dmillerw
 */
public class RenderState {

    public static final RenderState BLANK = new RenderState(null, null, false, false);

    // Horrible, I know, but it works, I guess
    public IBlockState blockState;
    public IBlockState extendedBlockState;

    public boolean camouflage = false;
    public boolean tileRender = false;

    public RenderState() {

    }

    public RenderState(IBlockState blockState, IBlockState extendedBlockState, boolean camouflage, boolean tileRender) {
        this.blockState = blockState;
        this.extendedBlockState = extendedBlockState;
        this.camouflage = camouflage;
        this.tileRender = tileRender;
    }

    @Override
    public String toString() {
        return "{ " + blockState + " }";
    }
}
