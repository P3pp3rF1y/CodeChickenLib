package codechicken.lib.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache.ProfileEntry;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by covers1624 on 22/10/2016.
 */
public class ServerUtils {
    public static MinecraftServer mc() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public static EntityPlayerMP getPlayer(String playername) {
        return mc().getPlayerList().getPlayerByUsername(playername);
    }

    public static List<EntityPlayerMP> getPlayers() {
        return mc().getPlayerList().getPlayerList();
    }

    public static ArrayList<EntityPlayer> getPlayersInDimension(int dimension) {
        ArrayList<EntityPlayer> players = new ArrayList<EntityPlayer>();
        for (EntityPlayer p : getPlayers()) {
            if (p.dimension == dimension) {
                players.add(p);
            }
        }

        return players;
    }

    public static boolean isPlayerLoadingChunk(EntityPlayerMP player, ChunkPos chunk){
        return player.getServerWorld().getPlayerChunkMap().getEntry(chunk.chunkXPos, chunk.chunkZPos) != null;
    }

    public static GameProfile getGameProfile(String username) {
        EntityPlayer player = getPlayer(username);
        if (player != null) {
            return player.getGameProfile();
        }

        //try and access it in the cache without forcing a save
        username = username.toLowerCase(Locale.ROOT);
        ProfileEntry cachedEntry = mc().getPlayerProfileCache().usernameToProfileEntryMap.get(username);
        if (cachedEntry != null) {
            return cachedEntry.getGameProfile();
        }

        //load it from the cache
        return mc().getPlayerProfileCache().getGameProfileForUsername(username);
    }

    public static boolean isPlayerOP(String username) {
        GameProfile prof = getGameProfile(username);
        return prof != null && mc().getPlayerList().canSendCommands(prof);
    }

    public static boolean isPlayerOwner(String username) {
        return mc().isSinglePlayer() && mc().getServerOwner().equalsIgnoreCase(username);
    }

    public static void sendChatToAll(ITextComponent msg) {
        for (EntityPlayer p : getPlayers()) {
            p.addChatComponentMessage(msg);
        }
    }
}
