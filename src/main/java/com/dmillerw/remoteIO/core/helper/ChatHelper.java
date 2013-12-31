package com.dmillerw.remoteIO.core.helper;

import com.dmillerw.remoteIO.lib.ModInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class ChatHelper {

	public static final String PREFIX = "[%s] ";
	
	public static void sendMessage(EnumChatFormatting prefixColor, String prefix, EntityPlayer player, String message) {
		ChatMessageComponent chat = new ChatMessageComponent().setColor(prefixColor).addText(String.format(PREFIX, prefix));
		chat.appendComponent(ChatMessageComponent.createFromTranslationKey(message).setColor(EnumChatFormatting.WHITE));
		player.sendChatToPlayer(chat);
	}
	
	public static void info(EntityPlayer player, String message) {
		sendMessage(EnumChatFormatting.GREEN, ModInfo.NAME, player, message);
	}
	
	public static void warn(EntityPlayer player, String message) {
		sendMessage(EnumChatFormatting.RED, ModInfo.NAME, player, message);
	}
	
}
