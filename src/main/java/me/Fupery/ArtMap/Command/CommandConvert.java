package me.Fupery.ArtMap.Command;

import org.bukkit.command.CommandSender;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Legacy.DatabaseConverter;
import me.Fupery.ArtMap.IO.Legacy.FlatDatabaseConverter;
import me.Fupery.ArtMap.IO.Legacy.V2DatabaseConverter;

class CommandConvert extends AsyncCommand {

    CommandConvert() {
		super("artmap.admin", "/art convert [--force]", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        boolean isForced = false;
        boolean conversionDone = false;
        if(args.length > 1 && args[1].equals("--force")) {
            isForced = true;
        }

        //figure out which conversion we are doing
        DatabaseConverter flat = new FlatDatabaseConverter(ArtMap.instance());
        DatabaseConverter v2 = new V2DatabaseConverter(ArtMap.instance());
        if(flat.isNeeded()) {
            sender.sendMessage("Artmap: Flat DB conversion needed.");
            conversionDone = flat.createExport(sender, false);
        }
        if(v2.isNeeded()) {
            sender.sendMessage("Artmap: V2 DB conversion needed.");
            conversionDone = v2.createExport(sender, false);
        }
        if(isForced && flat.canBeForced()) {
            sender.sendMessage("Artmap: Flat DB conversion being forced.");
            conversionDone = flat.createExport(sender, true);
        }
        if(isForced && v2.canBeForced()) {
            sender.sendMessage("Artmap: V2 DB conversion being forced.");
            conversionDone = v2.createExport(sender, true);
        }
        if(conversionDone) {
            sender.sendMessage("Artmap: Run '/artmap import -all <filename>' replace <filename> with the filename of the export printed above. ");
        } else {
            sender.sendMessage("Artmap: Conversion not needed or failed.");
        }
    }
}
