
# Release 3.5.7
* Add ability to disable paintbrush in the config.yml.
* Add lang.yml lines for paintbrush force and disable.
* Reduce log message amount about heads failing to be downloaded.
* Fix a creative inventory weirdness that caused an exception

# Release 3.5.6
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
    - Those checks now run on map load so keep an eye on timings of MapInitializeEvent

## Release 3.3.2
* Fixed user disconnect on Dropper tool use.
* Removed NMS dependency which should make compiling a bit easier

## Release 3.3.1
* Artkit now saves hotbar during current login session.
    - This works across different easels.
    - Clears on logout or server restart in case something breaks.
* Eye Dropper now prints out base dye plus the byte code for easier shade matching on other eisels.
* Fixed mismatch by making Coarse Dirt -> Podzol.

## Release 3.3.0
### Major Changes
* Paint Bukkit is no longer craftable.  
    - Instead use a regular bukkit in the main hand the dye you want to fill with in the offhand.
    - There have been too many exploits with the crafting of paint bukkets and duplicating items this neatly removes that problem.
    - Now allows players in creative mode to more easily use paint buckets without leaving the easel to craft them.
* Added Eye Dropper Tool.
    - Using a sponge left click the colour you would like to pick up. Then right click to draw with the color.
    - Allows easy copy of shades.
    - Usable with the paint bucket to fill with a shade.
* Admin's can now right click dyes in the dye menu to receive a copy of the dye.
* Players can now obtain a copy of their own artwork by right clicking on it in the preview menu.  It cost them one empty map just like using a crafting table would.

### Minor Changes
* Lots of cleanup to the English Language file.
    - Try to make sure tooltips won't go off screen even on huge GUI configurations.
    - Made more text able to be changed via the language files.
    - If you are using a custom lang.yml I suggest comparing to the new lang.yml to pickup changes.
    - If anyone has updates to the other languages files please sumbit an issue and I will have the updated or added as soon as possible.
* Fixed help menu back buttons sometimes being invisible.
* Fixed a few duplication and stealing from artkit bugs.
* Add '/art break' if a player really wants to break and easel and reset the artwork.
    - Prevents accidental easel breaks.
