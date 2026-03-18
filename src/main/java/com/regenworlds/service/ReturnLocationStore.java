package com.regenworlds.service;

import com.regenworlds.model.PlayerReturn;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReturnLocationStore {
    private final File file;
    private final Map<UUID, PlayerReturn> store = new HashMap<>();

    public ReturnLocationStore(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "return_locations.dat");
        load();
    }

    public void set(UUID player, PlayerReturn loc) {
        store.put(player, loc);
        save();
    }

    public PlayerReturn get(UUID player) {
        return store.get(player);
    }

    public void remove(UUID player) {
        store.remove(player);
        save();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!file.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Map<UUID, PlayerReturn> loaded = (Map<UUID, PlayerReturn>) in.readObject();
            store.putAll(loaded);
        } catch (Exception ignored) {}
    }

    private void save() {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(store);
        } catch (Exception ignored) {}
    }
}
