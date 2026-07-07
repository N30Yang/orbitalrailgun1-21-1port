package io.github.mishkis.orbital_railgun.sound;

import io.github.mishkis.orbital_railgun.OrbitalRailgun;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class OrbitalRailgunSounds {
    public static final Identifier RAILGUN_SHOOT_ID = Identifier.of(OrbitalRailgun.MOD_ID, "railgun_shoot");
    public static final Identifier SCOPE_ON_ID = Identifier.of(OrbitalRailgun.MOD_ID, "scope_on");
    public static final Identifier EQUIP_ID = Identifier.of(OrbitalRailgun.MOD_ID, "equip");

    public static final SoundEvent RAILGUN_SHOOT = register(RAILGUN_SHOOT_ID);
    public static final SoundEvent SCOPE_ON = register(SCOPE_ON_ID);
    public static final SoundEvent EQUIP = register(EQUIP_ID);

    private static SoundEvent register(Identifier id) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {
    }
}
