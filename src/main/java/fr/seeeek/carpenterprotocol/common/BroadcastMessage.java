package fr.seeeek.carpenterprotocol.common;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;

import java.util.Objects;

/**
 * Utility class responsible for broadcasting text messages
 * to different scopes within a Hytale server environment.
 *
 * <p>This class centralizes message delivery logic so that:
 * <ul>
 *     <li>Message broadcasting behavior stays consistent</li>
 *     <li>Code duplication is avoided</li>
 *     <li>Future formatting or logging changes are applied globally</li>
 * </ul>
 *
 * <p>Supported broadcast scopes:
 * <ul>
 *     <li>Single Player</li>
 *     <li>Entire World</li>
 *     <li>Entire Universe</li>
 * </ul>
 *
 * <p>This class follows the utility pattern:
 * <ul>
 *     <li>It cannot be instantiated</li>
 *     <li>All methods are static</li>
 * </ul>
 *
 * @author SeeeeK
 * @since 1.0
 */
public class BroadcastMessage {
    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This class is designed as a static utility holder.
     */
    private BroadcastMessage() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Broadcasts a message to a single player.
     *
     * <p>This method sends a formatted message directly to the specified player.
     * It is typically used for:
     * <ul>
     *     <li>Private notifications</li>
     *     <li>Command feedback</li>
     *     <li>System alerts</li>
     * </ul>
     *
     * @param playerRef  the target player reference receiving the message (must not be null)
     * @param message the message to send (must not be null)
     * @param messageType  the messageType to stylize and add prefix to the message (must not be null)
     *
     * @throws NullPointerException if player or message is null
     */
    public static void toPlayer(PlayerRef playerRef, String message, MessageType messageType, Object... args){
        Objects.requireNonNull(playerRef, "Player cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(messageType, "MessageType cannot be null");

        Message formattedMessage = MessageFormatter.format(messageType, message, args);
        assert formattedMessage != null;

        playerRef.sendMessage(formattedMessage);
    }

    /**
     * Broadcasts a message to a single player.
     *
     * <p>This method sends a formatted message directly to the specified player.
     * It is typically used for:
     * <ul>
     *     <li>Private notifications</li>
     *     <li>Command feedback</li>
     *     <li>System alerts</li>
     * </ul>
     *
     * @param player  the target player receiving the message (must not be null)
     * @param message the message to send (must not be null)
     * @param messageType  the messageType to stylize and add prefix to the the message (must not be null)
     *
     * @throws NullPointerException if player or message is null
     */
    public static void toPlayer(Player player, String message, MessageType messageType, Object... args){
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(messageType, "MessageType cannot be null");

        Message formattedMessage = MessageFormatter.format(messageType, message, args);
        assert formattedMessage != null;

        player.sendMessage(formattedMessage);
    }

    /**
     * Broadcasts a message to all players within a specific world.
     *
     * <p>This method iterates through all players currently present in the world
     * and sends the given message individually.
     *
     * <p>Common use cases:
     * <ul>
     *     <li>World-specific events</li>
     *     <li>Region announcements</li>
     *     <li>World boss alerts</li>
     * </ul>
     *
     * @param world   the world containing the players (must not be null)
     * @param message the message to broadcast (must not be null)
     * @param messageType  the messageType to stylize and add prefix to the the message (must not be null)
     *
     * @throws NullPointerException if world or message is null
     */
    public static void toWorld(World world, String message, MessageType messageType, Object... args){
        Objects.requireNonNull(world, "World cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(messageType, "MessageType cannot be null");

        Message formattedMessage = MessageFormatter.format(messageType, message, args);

        if(formattedMessage != null)  world.sendMessage(formattedMessage);
    }

    /**
     * Broadcasts a message to all players across the entire universe.
     *
     * <p>This method iterates through every world within the universe
     * and broadcasts the message to all players in each world.
     *
     * <p>This is typically used for:
     * <ul>
     *     <li>Global announcements</li>
     *     <li>Server restart warnings</li>
     *     <li>Network-wide events</li>
     * </ul>
     *
     * @param universe the universe containing all worlds (must not be null)
     * @param message  the message to broadcast (must not be null)
     * @param messageType  the messageType to stylize and add prefix to the the message (must not be null)
     *
     * @throws NullPointerException if universe or message is null
     */
    public static void toUniverse(Universe universe, String message, MessageType messageType, Object... args) {
        Objects.requireNonNull(universe, "Universe cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(messageType, "MessageType cannot be null");

        Message formattedMessage = MessageFormatter.format(messageType, message, args);

        if(formattedMessage != null)  universe.sendMessage(formattedMessage);
    }
}