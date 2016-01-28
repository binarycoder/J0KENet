# J0KENet
Planetside 2 stat calculator

To use: Download zip file, run the "runStatCalc.bat" file, or run "J0KENetStatCalc.jar" through the command line.

Algorythim Explanation:
K/D ratio: Pulls the past 1000 kills/death events, then records all kills except the following: kills in a vehicle, suicides (self kills. weird.), and kills with knives, consumable explosives, pistols, shotguns, and MAX weapons. It then records all deaths, and computes a ratio of kills / deaths.

HSR: Pulls the last 1000 kills only, and applies the same restrictions as above. Then records the number of kills from that set that were accomplished with a headshot, and then computes the ratio of headshot kills / total kills.
