package io.github.mishkis.orbital_railgun;

import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItem;
import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItems;
import io.github.mishkis.orbital_railgun.network.ClientSyncPayload;
import io.github.mishkis.orbital_railgun.network.ShootPayload;
import io.github.mishkis.orbital_railgun.network.StopAreaSoundPayload;
import io.github.mishkis.orbital_railgun.sound.OrbitalRailgunSounds;
import io.github.mishkis.orbital_railgun.sound.PlayerAreaListener;
import io.github.mishkis.orbital_railgun.SoundCommandRegistry;
import io.github.mishkis.orbital_railgun.SoundConfig;
import io.github.mishkis.orbital_railgun.util.OrbitalRailgunStrikeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.logging.Logger;

public class OrbitalRailgun implements ModInitializer {
    public static final String MOD_ID = "orbital_railgun";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    private static final long RAILGUN_SOUND_DURATION_MS = 52992L;

    @Override
    public void onInitialize() {
        OrbitalRailgunItems.initialize();
        OrbitalRailgunStrikeManager.initialize();
        SoundConfig.INSTANCE.loadConfig();
        OrbitalRailgunSounds.initialize();
        SoundCommandRegistry.registerSounds();

        PayloadTypeRegistry.playC2S().register(ShootPayload.ID, ShootPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientSyncPayload.ID, ClientSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StopAreaSoundPayload.ID, StopAreaSoundPayload.CODEC);

        PlayerAreaListener.setAreaChangeCallback(event -> handleAreaStateChange(event.player(), event.result()));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                PlayerAreaListener.clearPlayerState(handler.getPlayer().getUuid()));

        ServerPlayNetworking.registerGlobalReceiver(ShootPayload.ID, (payload, context) -> {
            ServerPlayerEntity serverPlayerEntity = context.player();
            MinecraftServer server = serverPlayerEntity.getServer();
            BlockPos blockPos = payload.pos();

            server.execute(() -> {
                OrbitalRailgunItem orbitalRailgun = OrbitalRailgunItems.ORBITAL_RAILGUN;
                orbitalRailgun.shoot(serverPlayerEntity);

                List<Entity> nearby = serverPlayerEntity.getWorld().getOtherEntities(null, Box.of(blockPos.toCenterPos(), 500., 500., 500.));
                OrbitalRailgunStrikeManager.activeStrikes.put(new Pair<>(blockPos, nearby), new Pair<>(server.getTicks(), serverPlayerEntity.getWorld().getRegistryKey()));

                nearby.foreach((entity -> {
                    if (entity instanceof ServerPlayerEntity serverPlayer) {
                        ServerPlayNetworking.send(serverPlayer, new ClientSyncPayload(blockPos));
                    }
                }));

                broadcastRailgunSound(server, blockPos);
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(OrbitalRailgunStrikeManager::tick);
        ServerTickEvents.End_server_TICK.register(server -> {
            if (server.getTicks() % 20 == 0 ) {
                server.getPlayerManager().getPlayerList().forEach(PlayerAreaListener::checkPlayerPosition);
            }
        });
    }

    private static void broadcastRailgunSound(MinecraftServer server, BlockPos blockpos) {
        double laserX = blockPos.getX() + 0.5;
        double laserZ = blockPos.getZ() + 0.5;
        long fireTimestamp = System.currentTimeMillis();

        server.getPlayerManager().getPlayerList().forEach(player -> {
            PlayerAreaListener.AreaCheckResult result =
                    PlayerAreaListener.handlePlayerAreaCheck(player, laserX, laserZ, fireTimestamp);
            
            if (result.isInside) {
                player.playSoundToPlayer(OrbitalRailgunSounds.RAILGUN_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }

            handleAreaStateChange(player, result);
        });
    }

    private static void handleAreaStateChange(serverPlayerEntity player, PlayerAreaListener.AreaCheckResult result) {
        if (result.hasEntered()) {
            long elapsedMs = System.currentTimeMillis() - result.fireTimestamp;
            if (elapsedMs <RAILGUN_SOUND_DURATION_MS) {
                player.playSoundToPlayer(OrbitalRailgunSounds.RAILGUN_SHOOT, SoundCategory.PLAYEERS, 1.0f, 1.0f);
            }
        } else if (result.hasLeft()) {
            ServerPlayNetworking.send(player, new StopAreaSoundPayload());
        }
    }
}