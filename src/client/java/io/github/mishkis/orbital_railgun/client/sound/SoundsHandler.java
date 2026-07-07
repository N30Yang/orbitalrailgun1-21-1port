package io.github.mishkis.orbital_railgun.client.sound;

import io.github.mishkis.orbital_railgun.item.OrbitalRailgunItems;
import io.github.mishkis.orbital_railgun.sound.OrbitalRailgunSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;

public class SoundsHandler {
    private int lastSelectedSlot = -1;
    private PositionedSoundInstance scopeSoundInstance;

    public void startScope(MinecraftClient client) {
        if (!ClientSoundConfig.INSTANCE.enableScopeSound) {
            return;
        }

        float volume = (float) ClientSoundConfig.INSTANCE.scopeVolume;
        scopeSoundInstance = new PositionedSoundInstance(
                OrbitalRailgunSounds.SCOPE_ON.getId(),
                SoundCategory.MASTER,
                volume,
                1.0f,
                SoundInstance.createRandom(),
                false,
                0,
                SoundInstance.AttenuationType.NONE,
                0.0, 0.0, 0.0,
                true
        );
        client.getSoundManager().play(scopeSoundInstance);
    }

    public void stopScope(MinecraftClient client) {
        if (scopeSoundInstance != null) {
            client.getSoundManager().stop(scopeSoundInstance);
            scopeSoundInstance = null;
        }
    }

    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        Item railgunItem = OrbitalRailgunItems.ORBITAL_RAILGUN;
        int selected = player.getInventory().selectedSlot;
        if (lastSelectedSlot != selected) {
            if (player.getMainHandStack().getItem() == railgunItem && ClientSoundConfig.INSTANCE.enableEquipSound) {
                player.playSound(OrbitalRailgunSounds.EQUIP, (float) ClientSoundConfig.INSTANCE.equipVolume, 1.0f);
            }
            lastSelectedSlot = selected;
        }
    }
}
