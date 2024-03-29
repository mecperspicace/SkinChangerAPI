package fr.mecperspicace.skinchangerapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinChangerAPI {
    private static final Property DEFAULT_SKIN = new Property("textures", "eyJ0aW1lc3RhbXAiOjE1MTgzNDg5NzE5NjAsInByb2ZpbGVJZCI6IjcwOTU2NDU0NTJkOTRiYTI5YzcwZDFmYTY3YjhkYTQyIiwicHJvZmlsZU5hbWUiOiJIaWRkdXMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIxNTA3NjM2NDJmZjVjNmEzZjNhYzViNDlkYTZmN2NhNGQzZDliMTlmZjg2MjFkMTIwYTI1NWY4OWM5OTRiIn19fQ==", "OC0mYKDPjcVl6mynbt2r6PRsOotVpuOYHN/ovyALXkMIFnEqBpnhzQG2i+SZcFGUA+TXFbJ58QzaqOdEc3AUiRI3HRQ27amfpIq07gwxpwzhvk1BBTNxsP2Mw/5jw/+ttA0QDavm9OYid1aOzQLQ7WzgiWSUUucbTDyfu7pKSceERBvezRAqknZ6BiUgpCx4ahbiDbbHwDIA4N2HXEtK7TeqOuK2pyeICqopcu9d7W5ZUIw/tgUB04wq68FDV7kCrhAmlyl8Yn/PzkGeCk9dNbcuJVRRIu537FE+N/BrCmtNVqH7eQ6w1534rDVI7Q3jNDazT5HYeTNN9opkaMM02e9r/Nj7t3ZxNjhqCvRTVBr+IvrUMI/D164wei3iFVIjyFDNg9Pn5YsyW0sLibsDU0PJxU+SOsqDsJa9gATlxbx4eANBf9NF1O+J3j+Cob+00XdWlTMO/6+wQNDOoW9as9dKoSsxAzIZt/5U6iFioCoI8Lumy5DivBFZecUfCqxP9Fffal38uWlz5B2tkQTx3FFsROhpsAQcF6athaOA2nAfmuZhfe7Uik+ER13T8xWFPvfqjueC68pqDK4JIxNL0BwqdJb1UuZm1hFjdwDMuxsXfZcT4BZe/09Oja1rwChBFDYIMatg8to7u9yN+vqo3C9KmTDJgeIZ+VOjOFWBFzE=");


    /**
     * Change the player skin.
     *
     * @param player       The targeted player.
     * @param skinProperty The property of the new player's skin.
     */

    public static void change(Player player, Property skinProperty) {

        //  Init the player's connection
        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        //  Send the packets
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", skinProperty);
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
        updateSkinWhitRelog(player);
        for (Player on : Bukkit.getOnlinePlayers()) {
            on.hidePlayer(player);
            on.showPlayer(player);
        }
    }

    public static void updateSkinWhitRelog(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        CraftWorld cw = (CraftWorld) player.getWorld();
        WorldServer ws = cw.getHandle();
        int dimension = ws.dimension;
        ((CraftServer) Bukkit.getServer()).getHandle().moveToWorld(ep, dimension, true, player.getLocation(), true);
    }

    /**
     * Get the player skin.
     *
     * @param uuid The targeted player's uuid.
     * @return playertextures The property of the player's skin.
     */

    public static Property getByUUID(String uuid) {
        // Do the request
        try {

            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                return DEFAULT_SKIN;
            }

            InputStream input = connection.getInputStream();
            InputStreamReader reponse = new InputStreamReader(input);
            if (reponse == null) {
                return DEFAULT_SKIN;
            }
            JsonObject jsonproperty = new JsonParser().parse(reponse).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = jsonproperty.get("value").getAsString();
            String signature = jsonproperty.get("signature").getAsString();

            reponse.close();
            input.close();
            connection.disconnect();

            return new Property("textures", texture, signature);

        } catch (IOException exception) {

            System.err.println(exception);
            return DEFAULT_SKIN;
        }

    }


    /**
     * Get the player skin.
     *
     * @param name The targeted player's name.
     * @return playertextures The property of the player's skin.
     */

    public static Property getByName(String name) {

        String uuid = "";

        // Do the first request
        try {
            URL url1 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) {
                return DEFAULT_SKIN;
            }

            InputStreamReader reponse1 = new InputStreamReader(connection.getInputStream());
            uuid = new JsonParser().parse(reponse1).getAsJsonObject().get("id").getAsString();
            reponse1.close();
        } catch (IOException exception) {
            System.err.println(exception);
            return DEFAULT_SKIN;
        }

        // Do the second request
        try {

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            if (connection.getResponseCode() != 200) {
                return DEFAULT_SKIN;
            }
            InputStreamReader reponse2 = new InputStreamReader(connection.getInputStream());
            JsonObject jsonproperty = new JsonParser().parse(reponse2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = jsonproperty.get("value").getAsString();
            String signature = jsonproperty.get("signature").getAsString();

            return new Property("textures", texture, signature);

        } catch (IOException exception) {

            System.err.println(exception);
            return DEFAULT_SKIN;

        }

    }


    /**
     * Get the player skin.
     *
     * @param player The targeted player's uuid.
     * @return playertextures The property of the player's skin.
     */

    public static Property getByObject(Player player) {

        // Init the player's connection
        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();

        // Get the skin's textures
        Property property = profile.getProperties().get("textures").iterator().next();

        return new Property("textures", property.getValue(), property.getSignature());
    }
}