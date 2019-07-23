![ArtMap](http://puu.sh/kRWAF/2c81256338.jpg)

## Spigot 1.14.3
PLEASE HAVE BACKUPS! 1.14 support required some changes around how Art is stored so please, please have Art.db backed up.
Spigot and Paper 1.14.3 are now supported by the Master Branch (Version 3.2.0+) along with continued 1.13 support.

## Warning
* Artmap is not compatible with 1.14.4 yet!
    - Everything looks to work but saving with the paint brush will crash the server!
    - I will make another release when anvil gui fixes compatibility.

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

## Move to Gitlab
I've moved this project to gitlab https://gitlab.com/BlockStack/ArtMap.  Mirroring has been setup so all commits should be available to fork on github.  But please submit issues on Gitlab.

### Attribution
This is not my original work and belongs to Fupery.  I continue maintenance of this plugin in his absence for my personal use.  As he had the code freely available I have continued that with my updates.  I ask that anyone that uses this plugin have either previously bought it from spigot.  Or please do so in the future if Fupery returns.

Bukkit plugin - allows players to draw directly onto minecraft maps. 
User guide at [ArtMap Wiki](https://gitlab.com/BlockStack/ArtMap/wikis/home).

### Features
* Custom easel entity
* Basic filter for artwork titles
* Asynchronous protocol handling
* List & preview system to view artworks

### Supported Versions:
* Spigot 1.14.3, 1.13.2  - Master Branch
* Spigot 1.8.8 - 1.12.2 - 1.12 Branch

### Permissions Nodes
* artmap.artist - allows players to use artmap
* artmap.admin - grants administrative override/deletion priveleges
* artmap.artkit - Will give players access to all dyes when seated at an easel and the config option forceartkit is set to true.

### NOTE
Use a plugin manager to reload your server whenever you update ArtMap - the Spigot reload command will freak out and throw a bunch of exceptions.
