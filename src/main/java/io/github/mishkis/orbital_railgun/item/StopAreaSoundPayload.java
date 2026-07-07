package io.github.mishkis.orbital_railgun.network;

import io.github.mishkis.orbital_railgun.OrbitalRailgun; 
import net.minecraft.network.codec.PacketCodec; 
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier; 

public record StopAreaSoundPayload() implements CustomPayload { 
    public static final CustomPayload.Id<StopAreaSoundPayload> ID = 
            new CustomPayload.Id<>(Identifier.of(OrbitalRailgun.MOD_ID, "stop_area_sound")); 

    public static final PacketCodec<net.minecraft.network.RegistryByteBuf, StopAreaSoundPayload> CODEC =
            PacketCodec.unit(new StopAreaSoundPayload()); 

    @Override
    public Id<? extends CustomPayload> getId() { 
        return ID;
    } 
} 
