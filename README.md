# TASmod OG
TASmod for Minecraft version 1.0.0-1.2.5

## What does this do?
TASmod is a Mod for Minecraft that allows recording and playing back a set of Inputs like in any other TAS Editor.

## Credits
Authors: Pancake (MCPfannkuchenYT), Scribble (ScribbleLP)
Huge thanks to Scribble who did most of the work, I ported most of his Code from 1.12 to older minecraft versions.

## Installation
Download the Mod Jar from the Releases Page and double click it.
 
## How to use.
From the Main Menu you can select "Record TAS" and create a world. If the World Name is the one from an existing File, that map will be loaded instead
As soon as the Map loads your Recording will be running. From the Main Menu you can playback a TAS or download one via Link. Server-support will probably never arrive

### Contributing
[Wiki available](https://github.com/MCPfannkuchenYT/TASmod-1.0-OG/wiki).
Works as a Gradle Project. Please use the gradle wrapper already provided, and do not change it.
When fast-forwarding/cloning a new commit, run the task `decommit` to regenerate the Minecraft Sourcecode. And run `commit` when commiting.
You will need to install the windows subsystem for linux, and then run: `sudo apt-get update -y && sudo apt-get install dos2unix git diffutils`.
To run you will have to create a new run configuration for `Start.java` in the default package. Edit it, and add `-Djava.library.path=build/natives`