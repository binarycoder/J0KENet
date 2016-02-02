# J0KENet
Planetside 2 stat calculator

To use: Download zip file, run the "runStatCalc.bat" file, or run "J0KENetStatCalc.jar" through the command line.

Algorythim Explanation:

 * BR: Just pulled from the chacter stats. 
 * Revive KD: Pulled fom weekly historical stats until there are more than 1000 points of data.
 * True KD: Pulls the past 1000 kills/death events, then records all kills except the following: kills in a vehicle, suicides (self kills. weird.), and kills with knives, consumable explosives, pistols, shotguns, and MAX weapons. It then records all deaths, and computes a ratio of kills / deaths.
 * Accuracy: Calulates most recently used weapons from killboard, keeping the top 1-3 with more than 100 kills. Then pulls and avertages total shots registered vs shots fired.
 * HSR: Pulls the last 1000 kills only, and applies the same restrictions as above. Then records the number of kills from that set that were accomplished with a headshot, and then computes the ratio of headshot kills / total kills.
 * IVI: HSR * Accuracy.



If Java is not installed, it can be installed here: https://www.java.com/en/download/


Filter Lists:
Note: please use an advanced text editor such as notpad++ to edit the list files!
Included in the "files" directory (please keep them in the folder) are the weapon filter files. The weapon ids listed in these files are removed when statistics are calculated. They are simple text files with a single numeric id on each line.
Included is the "J0KE Standard" settings, which are run by default. There is also a configuration for bolter stats (standard munis sniper rifles), and a reference file that lists all character weapon ids sorted by type.
Custom lists can be loaded on runtime via comand line. This can be done by editing the .bat file, as seen in the "runStatCalcModified.bat," or through the command line, with the first argument being the kill filter, and the second being the death filter.