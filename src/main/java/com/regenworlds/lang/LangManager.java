package com.regenworlds.lang;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LangManager {
    private final Map<String, YamlConfiguration> langs = new HashMap<>();
    private final String defaultLang;

    public LangManager(JavaPlugin plugin, String defaultLang) {
        this.defaultLang = defaultLang;
        loadLang(plugin, "en");
        loadLang(plugin, "ru");
    }

    private void loadLang(JavaPlugin plugin, String code) {
        var stream = plugin.getResource("lang_" + code + ".yml");
        if (stream != null)
            langs.put(code, YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8)));
    }

    public String get(Player player, String key, String... placeholders) {
        String locale = player != null ? player.getLocale().substring(0, 2).toLowerCase() : defaultLang;
        YamlConfiguration config = langs.getOrDefault(locale, langs.get(defaultLang));
        String msg = config != null ? config.getString(key, key) : key;
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        for (int i = 0; i + 1 < placeholders.length; i += 2)
            msg = msg.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
        return msg;
    }
}
