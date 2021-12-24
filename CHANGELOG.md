# Changelog

## Release 3.9.3

* Fix - Placing Unfinished artwork back on the easel after it being broken off.
* Fix - Newer MC versions need a Title or the annoucement silently fails causing artmap warnings not to show.

## Release 3.9.2

* Add 3 new colors added in 1.18.

## Release 3.9.1

* 1.18.1 Support - Basic testing done so make sure you have backups.

## Release 3.9.0'

* Gitlab CI - Automates the releases and testing to save me some time.
* 3 JDK builds - Builds now available for JDK 8,16,17

## Release 3.8.1

* Fix 1.13 `/art menu` failing to open
* Attempt to fix AnvilGUI errors by avoiding a class conflict with other plugins.
* Paintbrush will no longer show in the recipe menu when disabled.
* Fix a possible NPE if a player is in a mneu when an `/art reload` is called.

## Release 3.8.0

* Removes `/art break`. Use shift right click to break an easel.
* When an easel is broken for any reason the art is no longer cleared.
* Maps that are broken off an easel should now be able to be replaced.
* New option on `/art give` `/art give <player_to_give> unsaved:<id>` to retrieve copy of unsaved artwork.
* Add support for the 5 new colors introduced in 1.16. (Must be running a 1.16 server to use.)
* To reset a canvas on an easel use /art clear.
* Restructure of plugin compatibility layer.
* Add AFK prevention with Essentials and CMI. Controlled by artmap.prevent.afk. (Off by default)
* Attempt to fix Factions UUID compatibility. (This and SabreFactions are going to be problematic as the use the same plugin name and versions)
* Fix an error that caused artwork to appear blank if it is loaded in the art menu before being seen in the world.
* Fix a bug that allowed removal items from the help menu.
* If artbrush is disabled it will no longer show in menu.
* Artists menu paging is now much quicker (especially with a large number of artists).
* A few more translation fixes. (Sponge and others)
* There is now a MENU_HEADER.  This can be translated but must exist and be unique or the Artmap menus will break.

## Release 3.7.2

* Fix 'by' in search not being able to be translated.
* Add basic GriefDefender protection support.
* Add translation support for Next and Prev buttons in artkit.

## Release 3.7.1

* /art help now prints something useful
* Add traslatable strings for:
  * "by" in search text
  * "of" in search text
  * Sponge color pickup message
  * Dyes for Painting help menu
* Fix adding of untranslated text to lang.yml
* Fix a bit of miscoloring in the search text
* Make unit tests a bit more consistent

## Release 3.7.0

* Add `/artmap search` command to do text based search of artwork.
* Search provides clickable links that can preview artwork and retreive it (for admins).
* Update AnvilGUI to support 1.16.4.
* Fix null in copied artwork artist.
* Fix handling of artist names for artists no longer in the local server cache.

## Release 3.6.7

* Fix artwork previews.
* Fix little bugs all over the gui menus.
* Move datatables into the repo so we don't need the external dependency anymore.
* Pull components into artmap from InvMenu so we don't need the external dependency anymore.

## Release 3.6.6

* Fix WorldGuard putting "+" in the dev version numbers

## Release 3.6.5

* Add repair command to be used to detect and fix blank artwork.
* Update blank.dat with one that works for 1.13 plus.

## Release 3.6.4

* Fix RedProtect Compatibility exception.
* Fix Factions Compatibility exception.

## Release 3.6.3

* Remove "hide prefix" option.  It was broken and a pain to mantain.
* Fix Towny dependency pom.
* Fix Towny compat loading error.
* Update AnvilGUI dependency for 1.16.2
* Remove admin skip of build check.  It doesn't work since the other plugins will still block the build.
* Attempt to fix UnsupportedOperationException from ProtocolLib.
* Fix PlotSquared placing signs on road when an easel place is denied.  (Added special check for Roads.)

## Release 3.6.2

* Fix compatibility version check.  Should stop PlotSquared4 from thorwing a ClassNotFoundException.

## Release 3.6.1

* Fix IllegalStateExcpetion with maps that do not have a mapview (likely another plugin).

## Release 3.6.0

* Major changes to the way region handlers are built and loaded.
* PlotSquared 5 support added.
* SabreFactions support added.
* Fix Residence compat
* Fix some lang strings not updated to /art break
* Fix import export of artist or title

## Release 3.5.7

* Add ability to disable paintbrush in the config.yml.
* Add lang.yml lines for paintbrush force and disable.
* Reduce log message amount about heads failing to be downloaded.
* Fix a creative inventory weirdness that caused an exception

## Release 3.5.6

* Fix generic error message not being in the right file.
* Fix SQLException showing up instead of warning the player about duplicate title in the case of using a copied artwork.
* Dont kick the player off the easel if the save fails.
* Better catch exceptions in Compatibility handling so the plugin loads even with the compatibility hook failing to load.
* Fix null pointer on some head loading
* Only warn once about head cache not being fully loaded.

## Release 3.5.5

* Update AnvilGUI for 1.16.1 support
* Add Generic error message to the Lang file.
* Head loading error message cleared up.
* Handle some more Tuinity fork weirdness with events.
* More error catching around Artwork saving.
* Fix case were Artmap was passing null to AnvilGUI when it should not be.

## Release 3.5.2

* Better handle exceptions with play skin json.

## Release 3.5.1

* Fix some edge cases that could cause NPEs and other nastiness around the new Head Cache.

## Release 3.5.0

* Major changes around how player heads are loaded and cached to work with new Mojang API limits.
* Major internal refactoring
* Added unit tests, spotbugs, and code coverage reports to guide code quality improvements.
* Simplified protocol lib interaction.
* Removed a few compatibility hooks with plugins that never got upgraded to 1.13
* Remove a lot of legacy code around pre 1.13 support

## Release 3.4.3

* Fix Head retrieval.  The JSON response reading didn't handle newlines at all and mojang appears to have added one.

## Release 3.4.2

* Fix a major bug that would cause blank canvases when map restore would fail silently.
* Fix the logging so Artmap doesn't eat excpetions without telling anyone.

## Release 3.4.1

* Make import delay configurable.  Add importDelay to config.yml default is 100.
* Prevent /art break from clearing saved artwork.
  * This fixes a rare case where if there is a server crash after artwork is saved causing it to be placed back on the easel breaking it deleted (blanked) the saved artwork.
* Start adding some unit tests

## Release 3.4.0

* Rework of database conversion
* Internal command cleanup

## Release 3.3.11

* MarriageMaster integration - Prevent players from using gift wen in the Artkit.

## Release 3.3.10

* Prevent players in artkit from picking up items. Prevents loss of items thrown to a player while they are using artkit.

## Release 3.3.9

* Disable Map reuse as it might be causing map collisions and blank maps.
* Add some logging around map initialize to see if it is having problems.

## Release 3.3.8

* 1.15 support
* Fix compilation problem caused by protocol lib 4.4.0
* Update AnvilGui dependency for 1.15 support

## Release 3.3.7

* Fix cartography table integration

## Release 3.3.6

* Fix incorrect assumption that Denizen includes Depenizen classed causing a ClassNotFoundException on startup.

## Release 3.3.4

* Updated anvilgui - Brush for saving artwork will now work on 1.14.4

## Release 3.3.3

* Fixed an issue where a server with over 32768 maps would cause a short overflow and try and load negative map IDs which would fail.
* Removed initial artwork checks from startup as they were slow on large numbers of artwork
  * Those checks now run on map load so keep an eye on timings of MapInitializeEvent

## Release 3.3.2

* Fixed user disconnect on Dropper tool use.
* Removed NMS dependency which should make compiling a bit easier

## Release 3.3.1

* Artkit now saves hotbar during current login session.
  * This works across different easels.
  * Clears on logout or server restart in case something breaks.
* Eye Dropper now prints out base dye plus the byte code for easier shade matching on other eisels.
* Fixed mismatch by making Coarse Dirt -> Podzol.

## Release 3.3.0

### Major Changes

* Paint Bukkit is no longer craftable.  
  * Instead use a regular bukkit in the main hand the dye you want to fill with in the offhand.
  * There have been too many exploits with the crafting of paint bukkets and duplicating items this neatly removes that problem.
  * Now allows players in creative mode to more easily use paint buckets without leaving the easel to craft them.
* Added Eye Dropper Tool.
  * Using a sponge left click the colour you would like to pick up. Then right click to draw with the color.
  * Allows easy copy of shades.
  * Usable with the paint bucket to fill with a shade.
* Admin's can now right click dyes in the dye menu to receive a copy of the dye.
* Players can now obtain a copy of their own artwork by right clicking on it in the preview menu.  It cost them one empty map just like using a crafting table would.

### Minor Changes

* Lots of cleanup to the English Language file.
  * Try to make sure tooltips won't go off screen even on huge GUI configurations.
  * Made more text able to be changed via the language files.
  * If you are using a custom lang.yml I suggest comparing to the new lang.yml to pickup changes.
  * If anyone has updates to the other languages files please sumbit an issue and I will have the updated or added as soon as possible.
* Fixed help menu back buttons sometimes being invisible.
* Fixed a few duplication and stealing from artkit bugs.
* Add '/art break' if a player really wants to break and easel and reset the artwork.
  * Prevents accidental easel breaks.
