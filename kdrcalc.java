///////////////////////////////////////////////////////////
//J0KE.exe Stat Calculator v1.3
//By: Alek "binarycoder" Bollig - 1/27/2015
//Retrieves data from the Planetside 2 API, and calculates
//               Kill/Death Ratio and Head Shot percentage 
///////////////////////////////////////////////////////////

import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import org.json.JSONArray;

public class kdrcalc {
	
	public static ArrayList<Integer> loadBanList(String filename) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		File fileLocation = new File(filename);
		try {
			Scanner file = new Scanner(fileLocation);
			while (file.hasNextLine()) {
				output.add(Integer.parseInt(file.nextLine()));
			}
			file.close();
			return output;
		} catch (FileNotFoundException e) {
			System.out.println("null");
			return null;
		}
	}
	public static String censusFetch(String link) throws IOException {
		URL url = new URL(link);
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String output;
		output = in.readLine();
		in.close();
		return output;
	}
	public static boolean antiCheese(int id, ArrayList<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			if (id == list.get(i)) {
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args) throws IOException {
		System.out.println("--J0KE.exe v1.3--");
		System.out.println("_________________");
		System.out.println();
		Scanner kb = new Scanner(System.in);
		String playerName, killFilter = "killfilter", deathFilter = "deathfilter";
		
		if (args.length >= 1) {
			killFilter = args[0];
			System.out.println("Custom kill filter \"" + args[0] + "\" loaded.");
			if (args.length == 2) {
				deathFilter = args[1];
				System.out.println("Custom death filter \"" + args[1] + "\" loaded.");
			}
		}
		
		ArrayList<Integer> killBanList = loadBanList(("files\\" + killFilter + ".txt"));
		ArrayList<Integer> deathBanList = loadBanList("files\\" + deathFilter + ".txt");
		
		System.out.print("Enter player name to retrieve statistics(or \"exit\" to quit): ");
		playerName = kb.next().toLowerCase();
		while (!playerName.equals("exit")) {
			String playerId = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/character/?name.first_lower=" + playerName);
			while (playerId.equals("{\"character_list\":[],\"returned\":0}")) {
				System.out.println("Error: No character data. Check spelling twice, try again, then yell at binary.");
				System.out.print("Enter player name to retrieve statistics(or \"exit\" to quit): ");
				playerName = kb.next().toLowerCase();
				if (playerName.equals("exit")) {
					kb.close();
					System.out.println("Program Terminated.");
					return;
				} else {
					playerId = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/character/?name.first_lower=" + playerName);
				}
				
			}
			playerId = playerId.substring(playerId.indexOf("character_id") + 15, playerId.indexOf("name") - 3);
			String killFeed = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/characters_event/?character_id="
											+ playerId + "&type=KILL&c:limit=1000").substring(25);
			String deathFeed = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/characters_event/?character_id="
											+ playerId + "&type=KILL,DEATH&c:limit=1000").substring(25);
			
			JSONArray data = new JSONArray(killFeed);
			int killCount = 0, headshotCount = 0;
			for (int i = 0; i < data.length(); i++) {
				String kill = data.get(i).toString();
				int vehicleValue = Integer.parseInt(kill.substring(kill.indexOf("attacker_vehicle_id")+ 22, kill.indexOf("is_critical", (kill.indexOf("attacker_vehicle_id"))) - 3));
				if  (vehicleValue == 0) { //remove vehicle kills
					int weaponValue = Integer.parseInt(kill.substring(kill.indexOf("attacker_weapon_id") + 21, kill.indexOf("character_id", (kill.indexOf("attacker_weapon_id"))) - 3));
					if (antiCheese(weaponValue, killBanList)) { //remove non IVI
						killCount++;
						int headshotValue = Integer.parseInt(kill.substring(kill.indexOf("\"is_headshot\":\"") + 15, kill.indexOf("\",\"character_loadout_id\"")));
						if (headshotValue == 1) { //Mark Headshots
							headshotCount++;
						}
					}
				}
			}
			
			JSONArray deathData = new JSONArray(deathFeed);
			int killCountKD = 0, deathCountKD = 0;
			for (int i = 0; i < deathData.length(); i++) {
				String event = deathData.get(i).toString();
				if (event.contains("deaths")) {
					int weaponValue = Integer.parseInt(event.substring(event.indexOf("attacker_weapon_id") + 21, event.indexOf("character_id", (event.indexOf("attacker_weapon_id"))) - 3));
					if (antiCheese(weaponValue,deathBanList)) {
						deathCountKD++;
					}
				}
				else if (event.contains("kills")) {
					int vehicleValue = Integer.parseInt(event.substring(event.indexOf("attacker_vehicle_id")+ 22, event.indexOf("is_critical", (event.indexOf("attacker_vehicle_id"))) - 3));
					if  (vehicleValue == 0) { //remove vehicle kills
						int weaponValue = Integer.parseInt(event.substring(event.indexOf("attacker_weapon_id") + 21, event.indexOf("character_id", (event.indexOf("attacker_weapon_id"))) - 3));
						if (antiCheese(weaponValue, killBanList)) { //remove non IVI
							killCountKD++;
							}
						}
					}
				}
			System.out.println("Viable Kills (past 1000 kills): " + killCount + " | Headshot kills: " + headshotCount);
			System.out.println("Kills: " + killCountKD + " | Deaths: " + deathCountKD);
			System.out.println();
			System.out.println("-----------------");
			System.out.printf("HSR: %.2f%%%n", ((double) headshotCount / (double) killCount) * 100);
			System.out.printf("True KD: %.3f%n", (double) killCountKD / (double) deathCountKD);
			System.out.println("-----------------");
			System.out.println();
			System.out.print("Enter player name to retrieve statistics(or \"exit\" to quit): ");
			playerName = kb.next().toLowerCase();
		}
		kb.close();
		System.out.println("Program Terminated.");
	}
}
