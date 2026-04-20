package fr.seeeek.carpenterprotocol.common;

import java.awt.*;

public enum MessageType {

    INFO("[INFO]", Color.WHITE, false),
    DEBUG("[DEBUG]", Color.PINK, true),
    SUCCESS("[SUCCESS]", new Color(0, 200, 0), true),
    WARNING("[WARNING]", Color.ORANGE, true),
    ERROR("[ERROR]", new Color(255, 64, 64), true);

    private final String prefix;
    private final Color color;
    private final boolean bold;

    MessageType(String prefix, Color color, boolean bold) {
        this.prefix = prefix;
        this.color = color;
        this.bold = bold;
    }

    public String prefix() {
        return prefix;
    }

    public Color color() {
        return color;
    }

    public boolean bold() {
        return bold;
    }
}