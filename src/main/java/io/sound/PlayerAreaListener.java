package io.github.mishkis.orbital_railgun.sound;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import io.github.mishkis.orbital_railgun.OrbitalRailgun;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerAreaListener {
    private static final Map<UUID, AreaState> playerStates = new ConcurrentHashMap<>();
    private static Consumer<AreaChangeEvent> areaChangeCallback = null;

    private static class AreaState {
        boolean isInside;
        double lastLaserX;
        double lastLaserZ;
        long fireTimestamp;

        AreaState(boolean isInside, double laserX, double laserZ, long fireTimestamp) {
            this.isInside = isInside;
            this.lastLaserX = laserX;
            this.lastLaserZ = laserZ;
            this.fireTimestamp = fireTimestamp;
        }
    }

    public static boolean isPlayerInRange(ServerPlayerEntity player, double laserX, double laserZ) {
        double soundRange = SoundConfig.INSTANCE.getSoundRange();

        double dx = player.getX() - laserX;
        double dz = player.getZ() - laserZ;
        double distanceSquared = dx * dx + dz * dz;
        double rangeSquared = soundRange * soundRange;

        return distanceSquared <= rangeSquared;
    }

    public static AreaCheckResult handlePlayerAreaCheck(ServerPlayerEntity player, double laserX, double laserZ) {
        return handlePlayerAreaCheck(player, laserX, laserZ, System.currentTimeMillis());
    }

    public static AreaCheckResult handlePlayerAreaCheck(ServerPlayerEntity player, double laserX, double laserZ, long fireTimestamp) {
        UUID playerId = player.getUuid();
        boolean currentlyInside = isPlayerInRange(player, laserX, laserZ);

        AreaState previousState = playerStates.get(playerId);
        boolean wasInside = previousState != null && previousState.isInside;

        boolean isNewLocation = previousState == null
                || previousState.lastLaserX != laserX
                || previousState.lastLaserZ != laserZ;

        long timestamp = isNewLocation ? fireTimestamp : previousState.fireTimestamp;

        playerStates.put(playerId, new AreaState(currentlyInside, laserX, laserZ, timestamp));

        AreaCheckResult result = new AreaCheckResult();
        result.isInside = currentlyInside;
        result.wasInside = wasInside;
        result.isNewLocation = isNewLocation;
        result.fireTimestamp = timestamp;

        if (SoundConfig.INSTANCE.isDebugMode() && !wasInside && currentlyInside) {
            OrbitalRailgun.LOGGER.info("Player " + player.getName().getString() + " entered sound range at (" + laserX + ", " + laserZ + ")");
        }

        return result;
    }

    public static void clearPlayerState(UUID playerId) {
        playerStates.remove(playerId);
    }

    public static void setAreaChangeCallback(Consumer<AreaChangeEvent> callback) {
        areaChangeCallback = callback;
    }

    public static void checkPlayerPosition(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        AreaState state = playerStates.get(playerId);

        if (state == null) {
            return;
        }

        boolean currentlyInside = isPlayerInRange(player, state.lastLaserX, state.lastLaserZ);

        if (state.isInside != currentlyInside) {
            AreaCheckResult result = handlePlayerAreaCheck(player, state.lastLaserX, state.lastLaserZ);

            if (areaChangeCallback != null && result.hasStateChanged()) {
                areaChangeCallback.accept(new AreaChangeEvent(player, result, state.lastLaserX, state.lastLaserZ));
            }
        }
    }

    public record AreaChangeEvent(ServerPlayerEntity player, AreaCheckResult result, double laserX, double laserZ) {
    }

    public static class AreaCheckResult {
        public boolean isInside;
        public boolean wasInside;
        public boolean isNewLocation;
        public long fireTimestamp;

        public boolean hasEntered() {
            return isInside && !wasInside;
        }

        public boolean hasLeft() {
            return !isInside && wasInside;
        }

        public boolean hasStateChanged() {
            return hasEntered() || hasLeft();
        }
    }
}
