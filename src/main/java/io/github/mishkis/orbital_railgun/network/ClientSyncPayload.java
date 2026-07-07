package io.github.mishkis.orbital_railgun.network;

import io.github.mishkis.orbital_railgun.OrbitalRailgun;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * Server -> client: a strike began at {@code pos}; the client starts its strike shader.
 */
public record ClientSyncPayload(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<ClientSyncPayload> ID =
            new CustomPayload.Id<>(Identifier.of(OrbitalRailgun.MOD_ID, "client_synch_packet"));

    public static final PacketCodec<RegistryByteBuf, ClientSyncPayload> CODEC =
            PacketCodec.tuple(BlockPos.PACKET_CODEC, ClientSyncPayload::pos, ClientSyncPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
