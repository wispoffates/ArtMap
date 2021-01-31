package me.Fupery.ArtMap.api.Config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Fupery.ArtMap.api.IArtMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.annotation.Nonnull;

class LangLoader {
    private IArtMap plugin;
    private FileConfiguration defaults;
    private FileConfiguration lang;
    private boolean usingCustomLang = false;
    private HashMap<String, String> missingStrings = new HashMap<>();

    LangLoader(IArtMap plugin, Configuration configuration) {
        this.plugin = plugin;
        String language = configuration.LANGUAGE;
        plugin.getLogger().info(String.format("Loading '%s' language file", language.toLowerCase()));
        defaults = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));
        lang = null;

        if (language.equalsIgnoreCase("custom")) {
            usingCustomLang = true;
            File customLang = getCustomLangFile();
            lang = YamlConfiguration.loadConfiguration(customLang);

        } else if (!language.equalsIgnoreCase("english")) {
            String languageFileName = String.format("lang%s.yml", File.separator + language);
            Reader langReader = plugin.getTextResourceFile(languageFileName);
            if (langReader != null) {
                lang = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile(languageFileName));
            } else {
                logLangError(String.format("Error loading lang.yml! '%s' is not a valid language.", language));
            }
        }
        if (lang == null) {
            lang = defaults;
            usingCustomLang = false;
        }
    }

    private File getCustomLangFile() {
        File customLang = new File(plugin.getDataFolder(), "lang.yml");
        if (!customLang.exists()) plugin.writeResource("lang.yml", customLang);
        return customLang;
    }

    void save() {
        if (!usingCustomLang) return;
        File langFile = getCustomLangFile();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(langFile, true));
            for (Entry<String,String> ent : missingStrings.entrySet()) {
                writer.newLine();
                writer.write(ent.getKey() + ": \"" + ent.getValue() + "\"");
            }
            writer.close();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot save default keys to lang,yml.",e);
        }
    }

    String loadString(String key) {
        if (!lang.contains(key)) {
            logLangError(String.format("Error loading key from lang.yml: '%s'", key));
            if (defaults == null || !defaults.contains(key)) return null;
            String defaultString = defaults.getString(key);
            missingStrings.put(key, defaultString);
            return defaultString;
        }
        return lang.getString(key);
    }

    @Nonnull
    String[] loadArray(String key) {
        List<String> messages = lang.getStringList(key);
        if (messages.isEmpty()) {
            //if loading as List fails try loading as String an making a list
            messages = Collections.singletonList(lang.getString(key));
            if(messages.isEmpty()) {
                logLangError(String.format("Error loading key from lang.yml: '%s'", key));
                if (defaults == null || !defaults.contains(key)) return new String[]{"[" + key + "] NOT FOUND"};
                messages = defaults.getStringList(key);
            }
        }
        return messages.toArray(new String[messages.size()]);
    }

    String[] loadRegex(String key) {
        List<String> msg = lang.getStringList(key);
        return msg.toArray(new String[msg.size()]);
    }

    private void logLangError(String reason) {
        plugin.getLogger().warning(reason + " Default values will be used.");
    }
}
