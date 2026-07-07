package io.github.mishkis.orbital_railgun.client;

import io.github.mishkis.orbital_railgun.client.item.OrbitalRailgunRenderer;
import io.github.mishkis.orbital_railgun.client.rendering.OrbitalRailgunGuiShader;
import io.github.mishkis.orbital_railgun.client.rendering.OrbitalRailgunShader;
import io.github.mishkis.orbital_railgun.client.sound.ClinetSoundConfig;
import io.github.mishkis.orbital_railgun.client.sound.SoundsHandler;
import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItems;
import io.github.mishkis.orbital_railgun.network.ClientSyncPayload;
import io.github.mishkis.orbital_railgun.netowrk.StopAreaSoundPayload;
import io.github.mishkis.orbital_railgun.sound.OrbitalRailgunSounds;
import org.ladysnake.satin.api.event.PostWorldRenderCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.sound.SoundCategory;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public class OrbitalRailgunClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OrbitalRailgunItems.ORBITAL_RAILGUN.renderProviderHolder.setValue(new GeoRenderProvider() {
            private OrbitalRailgunRenderer renderer;

            @Override
            public BuiltinModelItemRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new OrbitalRailgunRenderer();
                }

                return this.renderer;
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientSyncPayload.ID, ((payload, context) -> {
            var blockPos = payload.pos();

            context.client().execute(() -> {
                OrbitalRailgunShader.INSTANCE.BlockPosition = blockPos.toCenterPos().toVector3f();
                OrbitalRailgunShader.INSTANCE.Dimension = context.client().world.getRegistryKey();
            });
        }));

        ClientTickEvents.END_CLIENT_TICK.register(OrbitalRailgunGuiShader.INSTANCE);
        PostWorldRenderCallback.EVENT.register(OrbitalRailgunGuiShader.INSTANCE);

        ClientTickEvents.END_CLIENT_TICK.register(OrbitalRailgunShader.INSTANCE);
        PostWorldRenderCallback.EVENT.register(OrbitalRailgunShader.INSTANCE);

        ClinetSoundConfig.INSTANCE.load();
        SoundsHandler soundsHandler = new SoundsHandler();
        OrbitalRailgunItems.ORBITAL_RAILGUN.onScopeStart = user -> soundsHandler.startScope(MinecraftClient.getInstance());
        OrbitalRailgunItems.ORBITAL_RAILGUN.onScopeStop = user -> soundsHandler.stopScope(MinecraftClient.getInstance());
        ClientTickEvents.END_CLIENT_TICK.register(soundsHandler::onEndTick);

        ClientPlayNetworking.registerGlobalReceiver(StopAreaSoundPayload.ID, (payload, context) ->
                contest.client().execute(() -> 
                        MinecraftClient.getInstance().getSoundManager() 
                                .stopSounds(OrbitalRailgunSounds.RAILGUN_SHOTT_ID, SoundCategory.PLAYERS)));
    }
}
