package com.dmillerw.remoteIO.inventory.gui.tab;

import com.dmillerw.remoteIO.block.tile.TileIOCore;
import com.dmillerw.remoteIO.core.handler.IconHandler;
import net.minecraft.util.Icon;

/**
 * Created by Dylan Miller on 1/17/14
 */
public class TabWarning extends TabText {

    public TabWarning(TileIOCore.Warning warning) {
        super("warning_" + warning, warning.getTooltip(), warning.getDescription());

        this.overlayColor = 0xBB0000;
    }

    @Override
    public Icon getIcon() {
        return IconHandler.INSTANCE.errorIcon;
    }

}
