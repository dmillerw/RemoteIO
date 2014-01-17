package com.dmillerw.remoteIO.inventory.gui.tab;

/**
 * Created by Dylan Miller on 1/17/14
 */
public interface ITabbedGUI {

    public int getXSize();
    public int getYSize();
    public int getWidth();
    public int getHeight();

    public void drawGradient(int x, int y, int w, int h, int c1, int c2);

}
