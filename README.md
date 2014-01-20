[Jail 3.0](http://ci.graywolf336.com/job/Jail/)
====
This plugins adds Jail to your Minecraft server. Admins can define several jails and then jail/unjail people or jail them on time basis. Plugin also offers wide variety of protections, so players won't escape out of your jail.

[![Build Status](http://ci.graywolf336.com/job/Jail/badge/icon)](http://ci.graywolf336.com/job/Jail/)

Changes
===
With this being version 3 of this plugin, a couple things have changed but the core concept is still the exact same. Here are some drastic changes:
* Time can now be entered different formats, 3hours or 15mins or 4days
* New language system
* New config system (per jail configs are going to make a come back)
* Items in the config use item names now, **not** the ids

ToDo
===
* About everything

Done
===
* Muted Prisoners are now muted
* Implemented counting down time **always use the bukkit scheduler, unless your tick rate is slow as the other way we do it is not always stable**
* New command system, internally we handle commands a lot better
* Delete commands are now remove
* Language system (adding language strings as I use them, be patient with me)
* Handcuffs are now implemented
* Config value ``jailing.jail.defaultJail`` is now used
* Config value ``jailing.jail.defaultTime`` is now used
* The time passed can be represented by time shorthand, aka "3hours" or "15minutes" or etc (defaults to minutes)
* Config value ``jailing.jail.broadcastJailing`` is now used
* Config value ``jailing.jail.logToConsole`` is now used
* Config value ``jailing.jail.deleteInventory`` is now used
* Config value ``jailing.release.backToPreviousPosition`` is now used on jail and unjail
* Config value ``jailing.release.restorePreviousGameMode`` is now used
* Config value ``jailing.jail.gameMode`` is now used
* Config value ``jailing.during.ignoreSleeping`` is now used in one part
* Config value ``jailing.jail.commands`` is now used
* Config value ``jailing.during.maxFoodLevel`` is now used in one part
* Config value ``jailing.during.minFoodLevel`` is now used in one part
* Config value ``jailing.release.teleport`` is used in unjailing
* Config value ``jailing.release.commands`` is now used

[Jail 3.0 JavaDoc](http://ci.graywolf336.com/job/Jail/javadoc)
====
