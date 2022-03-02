package fr.gabrielmercier.skinchangerapi;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinChangerAPI extends JavaPlugin {

    /**
     * Change the player skin.
     *
     * @param player       The targeted player.
     * @param skinProperty The property of the new player's skin.
     */

    public static void ChangePlayerSkin(Player player, Property skinProperty) {

        //  Init the player's connection
        GameProfile profile = ((CraftPlayer)player).getHandle().getProfile();
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        //  Send the packets
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle()));
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", skinProperty);
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle()));
    }

    /**
     * Get the player's skin textures.
     *
     * @param playerName      The targeted player's name.
     *
     * @return playerTextures Return the player's skin textures.
     */

    public static Property GetOnlinePlayerSkin(String playerName){

        // Init the player's connection
        GameProfile profile = ((CraftPlayer)Bukkit.getPlayer(playerName)).getHandle().getProfile();

        // Get the player's textures
        Property playerTextures = profile.getProperties().get("textures").iterator().next();

        // Return the player's textures
        return playerTextures;
    }

}
