package de.diemex.scoreboardnotifier.message;

import org.bukkit.ChatColor;

/**
 * Settings for a Popup to be displayed, internal holder class
 */
public class MsgSettings {
    private final String uniqueIdentifier;
    private final int length;
    private final ChatColor titleColor;
    private ChatColor textColor;

    public MsgSettings(int length) {
        this(null, length, null, null);
    }

    public MsgSettings(int length, ChatColor titleColor) {
        this(null, length, titleColor, null);
    }

    public MsgSettings(int length, ChatColor titleColor, ChatColor textColor) {
        this(null, length, titleColor, textColor);
    }

    public MsgSettings(String uniqueIdentifier, int length, ChatColor titleColor, ChatColor textColor) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.length = length;
        this.titleColor = titleColor;
        this.textColor = textColor;
    }

    public boolean hasUniqueIdentifier() {
        return uniqueIdentifier != null && !uniqueIdentifier.isEmpty();
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public int getLength() {
        return length;
    }

    public ChatColor getTitleColor() {
        return titleColor;
    }

    public ChatColor getTextColor() {
        return textColor;
    }
}
