package io.github.mishkis.orbital_railgun.network;

import io.github.mishkis.orbital_railgun.OrbitalRailgun;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record ShootPayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<ShootPayload> ID =
            new CustomPayload.Id<>(Identifier.of(OrbitalRailgun.MOD_ID, "shoot_packet"));

    public static final PacketCodec<RegistryByteBuf, ShootPayload> CODEC =
            PacketCodec.tuple(BlockPos.PACKET_CODEC, ShootPayload::pos, ShootPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}