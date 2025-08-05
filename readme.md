# BetterKeepInventory 2.0
[![Dynamic JSON Badge](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F93081&query=downloads&logo=spigotmc&label=Downloads&color=%23ED8106)](https://www.spigotmc.org/resources/betterkeepinventory.93081/)
[![Discord](https://img.shields.io/discord/850836877924761610?label=BeepSterr's%20Workshop&color=%239191c9)](https://leafcat.live/discord)

Take control of KeepInventory.
No longer shall your players jump into lava to get home. Weep they shall, for the mercy of their benevolent admins.

*(This plugin lets take fine grained control over KeepInventory, choosing to drop certain items, damage durability and many more effects)*

## Downloads
The plugin can be obtained here on GitHub or trough
- [Modrinth](https://modrinth.com/plugin/betterkeepinventory)
- [SpigotMC](https://www.spigotmc.org/resources/betterkeepinventory.93081/)

## Supported Versions
BetterKeepInventory supports the latest 3 major Minecraft versions, and is compiled using the native Java version of the oldest supported Minecraft version.

| Minecraft Version | Plugin Version | Java Version |
|-------------------|----------------|--------------|
| 1.16 and below    | 1.3            | 11           |
| 1.17, 1.18        | 1.6.2          | 16           |
| 1.19+             | Latest         | 17           |

### Stable Versions
The latest and stable version of BetterKeepInventory is not always the same version. The latest release will be posted to SpigotMC but the stable release is defined in [This File](versions/stable.txt).
Set your `notify_channel` to `stable` to receive notifications whenever the stable version is newer than the version you have installed.

### Snapshots
Snapshots are for those who like to live on the edge (or are waiting for specific changes)
They contain the latest changes and are not guaranteed to be stable. You can find snapshot builds [Here](https://github.com/BeepSterr/BetterKeepInventory/actions/workflows/build_snapshot.yml)

## Having Trouble?
Please either create a issue here on GitHub or join the [Discord](https://discord.gg/fFvFXPvtty) for chat support

## Developer API
BetterKeepInventory exposes a API to allow other plugins to extend its features.

### Installation
Follow the instructions [here](https://github.com/BeepSterr/BetterKeepInventory/packages/) to install the API package into your project

### Adding a Condition
A condition is a checked when it is applied to a rule, it allows end users to create their own rules
for when certain effects should be ran. some examples could be: if player is in a party, or the player has a hardcore difficulty
#### Definition
```java
// An example condition definition
public static class AlwaysTrueCondition implements Condition {
    public AlwaysTrueCondition(ConfigurationSection section) {
        // no config needed in this example
        // but here you can use standard bukkit config API to read your conditions values
    }

    @Override
    public boolean check(Player player, PlayerDeathEvent deathEvent, PlayerRespawnEvent respawnEvent) {
        return false;
    }
}
```

#### Register the condition
```java
BetterKeepInventoryAPI api = Bukkit.getServicesManager().load(BetterKeepInventoryAPI.class);
// don't forget null checks!
api.conditionRegistry().register(this, "always_true", AlwaysTrueCondition::new);
```
And your condition is now available under `always_true` and `plugin_name.always_true`

### Adding a Effect
a effect is a "thing" that happens whenever a rule is triggered.
Some examples could be: loss levels, temporary restrictions or anything your plugin can offer
#### Definition
```java
// An example condition definition
public static class BroadcastEffect implements Effect {
    public BroadcastEffect(ConfigurationSection section) {
        // no config needed in this example
        // but here you can use standard bukkit config API to read your conditions values
    }

    @Override
    public void onRespawn(Player player, PlayerRespawnEvent event) {
        // This effect doesn't do anything on respawn (yet)
    }

    @Override
    public void onDeath(Player ply, PlayerDeathEvent event) {
        plugin.getServer().broadcastMessage(ply.getName() + " died a gruesome death.");
    }
}
```
#### Register the effect
```java
BetterKeepInventoryAPI api = Bukkit.getServicesManager().load(BetterKeepInventoryAPI.class);
// don't forget null checks!
api.effectRegistry().register(this, "broadcast", BroadcastEffect::new);
```
