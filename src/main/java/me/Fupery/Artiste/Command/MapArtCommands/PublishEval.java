package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.MapArt.PrivateMap;
import me.Fupery.Artiste.Command.Utils.Error;
import me.Fupery.Artiste.MapArt.PublicMap;

public class PublishEval extends MapArtCommand {

	private boolean approve;

	public void initialize() {

		usage = "<approve|deny> <title>";
		adminRequired = true;
	}

	public boolean run() {

		if (!getCmd())
			return false;

		PrivateMap map = (PrivateMap) art;

		if (approve && !map.isDenied())

			new PublicMap(sender, map);

		else

			map.deny();

		return true;
	}

	@Override
	public String conditions() {

		switch (type) {

		case PRIVATE:

			error = String.format(Error.notQueued, args[1]);
			break;

		case QUEUED:

			return null;

		case PUBLIC:

			error = String.format(Error.alreadyPub, args[1]);

		default:
			error = usage;
		}

		return error;
	}

	private boolean getCmd() {
		switch (args[0].toLowerCase()) {
		case "approve":
			approve = true;
			break;
		case "deny":
			approve = false;
			break;
		default:
			return false;
		}
		return true;
	}

}
