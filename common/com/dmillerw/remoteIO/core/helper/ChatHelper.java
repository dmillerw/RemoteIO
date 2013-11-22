package com.dmillerw.remoteIO.core.helper;

import com.dmillerw.remoteIO.lib.ModInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class ChatHelper {

	public static final String PREFIX = "[%s] ";
	
	public static void sendMessage(EnumChatFormatting prefixColor, String prefix, EntityPlayer player, String message) {
		player.addChatMessage(prefixColor + String.format(PREFIX, prefix) + EnumChatFormatting.RESET + message);
	}
	
	public static void info(EntityPlayer player, String message) {
		sendMessage(EnumChatFormatting.GREEN, ModInfo.NAME, player, message);
	}
	
	public static void warn(EntityPlayer player, String message) {
		sendMessage(EnumChatFormatting.RED, ModInfo.NAME, player, message);
	}
	
}
