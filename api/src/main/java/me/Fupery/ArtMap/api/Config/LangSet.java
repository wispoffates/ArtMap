package me.Fupery.ArtMap.api.Config;

import org.bukkit.command.CommandSender;

interface LangSet<T> {
    void send(CommandSender sender);

    T get();
}
