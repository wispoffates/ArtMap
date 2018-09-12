![ArtMap](http://puu.sh/kRWAF/2c81256338.jpg)

## Spigot 1.13
PLEASE HAVE BACKUPS! There are major bugs!
1.13 update is in progress. The 1.13 release will not work with 1.12. I have created a 1.12 branch to continue maintenance of that release. The master branch will be 1.13 only.

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
* Spigot 1.13  - Master Branch
* Spigot 1.8.8 - 1.12 - 1.12 Branch

### Permissions Nodes
* artmap.artist - allows players to use artmap
* artmap.admin - grants administrative override/deletion priveleges

### Config
The following values are configurable
* Canvas resolution

### NOTE
Use a plugin manager to reload your server whenever you update ArtMap - the Spigot reload command will freak out and throw a bunch of exceptions.
