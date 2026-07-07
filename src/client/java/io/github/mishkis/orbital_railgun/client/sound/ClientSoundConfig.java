package io.github.mishkis.orbital_railgun.client.sound;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.mishkis.orbital_railgun.OrbitalRailgun;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ClientSoundConfig {
    private static final File CONFIG_FILE = new File("config/orbital-railgun-sounds-client-config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ClientSoundConfig INSTANCE = new ClientSoundConfig();

    public double scopeVolume = 1.0;
    public double equipVolume = 1.0;
    public boolean enableScopeSound = true;
    public boolean enableEquipSound = true;

    public void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ClientSoundConfig loaded = GSON.fromJson(reader, ClientSoundConfig.class);
            if (loaded != null) {
                this.scopeVolume = loaded.scopeVolume;
                this.equipVolume = loaded.equipVolume;
                this.enableScopeSound = loaded.enableScopeSound;
                this.enableEquipSound = loaded.enableEquipSound;
            }
        } catch (IOException e) {
            OrbitalRailgun.LOGGER.warning("Failed to load client sound config: " + e.getMessage());
        }
    }

    private void save() {
        try {
            File parentDir = CONFIG_FILE.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            OrbitalRailgun.LOGGER.warning("Failed to save client sound config: " + e.getMessage());
        }
    }
}