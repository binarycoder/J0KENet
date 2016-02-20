# J0KE.GG Stat Calc
Planetside 2 Statistics Calculator

To use: Download zip file, run "Joke_gg.jar."

Outfit warning! This takes forever. Only use sparingly, and preferably on smaller outfits. 

Algorithm Explanation:

 * BR: Just pulled from the character stats. 
 * Revive KD: Pulled from weekly historical stats until there are more than 1000 points of data.
 * True KD: Pulls the past 1000 kills/death events, then records all kills except the following: kills in a vehicle, suicides (self kills. weird.), and kills with knives, consumable explosives, pistols, shotguns, and MAX weapons. It then records all deaths, and computes a ratio of kills / deaths.
 * Accuracy: Calculates most recently used weapons from kill-board, keeping the top 1-3 with more than 100 kills. Then pulls and averages total shots registered vs shots fired.
 * HSR: Pulls the last 1000 kills only, and applies the same restrictions as above. Then records the number of kills from that set that were accomplished with a head-shot, and then computes the ratio of head-shot kills / total kills.
 * IVI: HSR * Accuracy.



If Java is not installed, it can be installed here: https://www.java.com/en/download/


Filter Lists:
Note: please use an advanced text editor such as notepad++ to edit the list files!
Included in the "files" directory (please keep them in the folder) are the weapon filter files. The weapon ids listed in these files are removed when statistics are calculated. They are simple text files with a single numeric id on each line. The default settings can be defined by editing the "default.txt" file. You can also save configurations that can be loaded in the menu when running.
