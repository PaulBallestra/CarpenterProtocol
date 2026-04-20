package fr.seeeek.carpenterprotocol.common;

import com.hypixel.hytale.server.core.Message;

import java.util.Objects;

/**
 * Responsible for building and styling {@link Message} objects
 * before they are sent to players.
 *
 * <p>This class centralizes:
 * <ul>
 *     <li>Prefix handling</li>
 *     <li>Color styling</li>
 *     <li>Bold styling</li>
 *     <li>Optional server-wide prefix</li>
 *     <li>Placeholder formatting</li>
 * </ul>
 *
 * <p>This class does NOT send messages.
 * It only builds them.
 *
 * @author SeeeeK
 * @since 1.0
 */
public class MessageFormatter {
    /**
     * Global server prefix.
     */
    private static final String SERVER_PREFIX = "";

    /**
     * Whether debug messages should be shown.
     */
    private static boolean debugEnabled = true;

    private MessageFormatter() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Builds a formatted {@link Message} using the specified type and content.
     *
     * @param messageType    the message type (defines prefix, color, bold style)
     * @param content the raw message content
     * @param args    optional formatting arguments (String.format style)
     * @return formatted Message ready to send
     */
    public static Message format(MessageType messageType, String content, Object... args) {
        Objects.requireNonNull(messageType, "MessageType cannot be null");
        Objects.requireNonNull(content, "Message content cannot be null");

        if (messageType == MessageType.DEBUG && !debugEnabled) {
            return null; // Caller should handle null safely
        }

        String formattedContent = args.length > 0
                ? String.format(content, args)
                : content;

        String finalText = SERVER_PREFIX
                + messageType.prefix()
                + " "
                + formattedContent;

        return Message.raw(finalText)
                .color(messageType.color())
                .bold(messageType.bold());
    }

    /**
     * Enables or disables DEBUG messages globally.
     *
     * @param enabled true to enable debug messages
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
}