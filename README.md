# TASmod-1.0-OG
TASmod for Minecraft version 1.0.0-1.2.5

## What does this do?
TASmod is a Mod for Minecraft that allows recording and playing back a set of Inputs like in any other TAS Editor.

## Credits
Authors: Pancake (MCPfannkuchenYT), Scribble (ScribbleLP)
Huge thanks to Scribble who did most of the work, I ported most of his Code from 1.12 to 1.0.

## Installation
! This Project is still in early development and does not feature an installer yet !
 Download the Mod Jar and MCLoader Jar from the Releases Page, open Minecraft 1.0 (UNMODIFIED!)
 and after Minecraft has launched, you can open the MCLoader and select the Mod File. Your Game will automatically be patched and TASmod will be installed
 
 ## How to use.
 From the Main Menu you can select "Record TAS" and create a world. If the World Name is the one from an existing File, that map will be loaded instead (TODO).
 As soon as the Map loads your Recording will be running. Press `k` to stop the Recording at any Point. From the Main Menu you can playback a TAS or download one via Link. Server-support will probably never arrive

### Contributing
First run regenerateSources.sh in WSL after converting the File to unix format
Open 'mcp/eclipse' in Eclipse.
After editing, run regeneratePatches.sh in WSL.
Once opened in eclipse, you will need to delete the 'Server' from the disk. and change the JDK to 1.8 or higher
#### DO NOT USE MULTIPLE MCP BRANCHES!
