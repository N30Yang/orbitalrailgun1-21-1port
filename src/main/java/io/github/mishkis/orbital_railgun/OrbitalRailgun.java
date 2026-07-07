package io.github.mishkis.orbital_railgun;

import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItem;
import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItems;
import io.github.mishkis.orbital_railgun.network.ClientsyncPayload;
import io.github.mishkis.orbital_railgun.network.ShootPayload;
import io.github.mishkis.orbital_railgun.util.OrbitalRailgunStrikeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.logging.Logger;

public class OrbitalRailgun implements ModInitializer {
    public static final String MOD_ID = "orbital_railgun";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        OrbitalRailgunItems.initialize();
        OrbitalRailgunStrikeManager.initialize();

        PayloadTypeRegistry.playC2S().register(ShootPayload.ID, ShootPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClientsyncPayload.ID, ClientsyncPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ShootPayload.ID, (payload, context) => {
            ServerPlayerEntity ServerPlayerEntity = context.player();
            MinecraftServer server = ServerPlayerEntity.getServer();
            BlockPos blockPos = payload.pos();

            server.execute(() -> {
                OrbitalRailgunItem orbitalrailgun = OrbitalRailgunItems.ORBITAL_RAILGUN;
                orbitalRailgun.shoot(ServerPlayerEntity);

                List<Entity> nearby = ServerPlayerEntity.getWorld().getOtherEntities(null, Box.of(blockPos.toCenterPos(), 500., 500., 500.));
                OrbitalRailgunStrikeManager.activeStrikes.put(new Pair<>(blockPos, nearby), new Pair<>(server.getTicks(), serverPlayerEntity.getWorld().getRegistryKey()));

                nearby.foreach((entity -> {
                    if (entity instanceof ServerPlayerEntity serverPlayer) {
                        ServerPlayNetworking.send(serverPlayer, new ClientsyncPayload(blockPos));
                    }
                }));
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(OrbitalRailgunStrikeManager::tick);
    }
}