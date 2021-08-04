![ArtMap](http://puu.sh/kRWAF/2c81256338.jpg)
### 1.17.1 Support
We did a very quick test and it looks like everything works.  But as always keep backups.

### Supported Versions:
* Spigot/Paper 1.17.1, 1.16.1,  1.15.2, 1.14.4, 1.13.2 - Master Branch
* Spigot/Paper 1.8.8 - 1.12.2 - 1.12 Branch

### Features
* Custom easel entity
* Basic filter for artwork titles
* Asynchronous protocol handling
* List & preview system to view artworks

### Permissions Nodes
* artmap.artist - allows players to use artmap
* artmap.admin - grants administrative override/deletion priveleges
* artmap.artkit - Will give players access to all dyes when seated at an easel and the config option forceartkit is set to true.

## Move to Gitlab
I've moved this project to gitlab https://gitlab.com/BlockStack/ArtMap.  Mirroring has been setup so all commits should be available to fork on github.  But please submit issues on Gitlab.

### Attribution
This is not my original work and belongs to Fupery.  I continue maintenance of this plugin in his absence for my personal use.  As he had the code freely available I have continued that with my updates.

Bukkit plugin - allows players to draw directly onto minecraft maps. 
User guide at [ArtMap Wiki](https://gitlab.com/BlockStack/ArtMap/wikis/home).

### NOTE
Use a plugin manager to reload your server whenever you update ArtMap - the Spigot reload command will freak out and throw a bunch of exceptions.
