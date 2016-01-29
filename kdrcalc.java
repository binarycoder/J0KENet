///////////////////////////////////////////////////////////
//J0KE.exe Stat Calculator v1.2
//By: Alek "binarycoder" Bollig - 1/27/2015
//Retrieves data from the Planetside 2 API, and calculates
//               Kill/Death Ratio and Head Shot percentage 
///////////////////////////////////////////////////////////

import java.net.*;
import java.util.Scanner;
import java.io.*;
import org.json.JSONArray;

public class kdrcalc {
	
	public static String censusFetch(String link) throws IOException {
		URL url = new URL(link);
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String output;
		output = in.readLine();
		in.close();
		return output;
	}
	public static boolean antiCheese(int id) {
		if (!(id == 0 ||
				id == 1 || //NC Knife
				id == 13 || //TR Knife
				id == 19 || //VS Knife
				id == 2 || //NC Pistol
				id == 15 || //TR Pistol
				id == 21 || //VS Pistol
				id == 44505 || // Grenade
				id == 432 || // C4
				id == 880 || // Sticky Grenades
				id == 429 || // Claymore
				id == 1044 || //Bettys
				id == 1045 || //Proxy Mine
				id == 650 || // Tank Mine
				id == 800625 || // Aurax Chainblade
				id == 802315 || // Hexedge
				id == 285 || // Ripper
				id == 800624 || // Aurax MagCutter
				id == 802316 || // Hardlight dagger
				id == 271 || // Carver
				id == 800626 || // Aurax Force blade
				id == 802317 || // Damascus edge
				id == 286 || // Lumine Edge
				id == 802903 || // NS CAmpion
				id == 802902 || // Harrower
				id == 804113 || // Victory
				id == 800627 || // Autoblade
				id == 801969 || // Slasher
				id == 802025 || // Aurax Slasher
				id == 802584 || // Icykill
				id == 802904 || // Knife 6
				id == 1082 || // Max Punch
				id == 7400 || // TX2 Emperor
				id == 7401 || // TS2 Inquisitor
				id == 7402 || // T4 AMP
				id == 1954 || // The President
				id == 7388 || // LA8 Rebel
				id == 7389 || // LA3 Desperado
				id == 7390 || // Mag Scatter
				id == 1889 || // The Executive
				id == 7412 || // Manticore SX40
				id == 7413 || // Cerberus
				id == 7414 || // Spiker
				id == 1959 || // The immortal
				id == 75071 || // 	NS-357 Underboss
				id == 75063 || // NS-44 Commissioner
				id == 802733 || // NS-44L Blackhand
				id == 2308 || // Hunter QCX
				id == 76358 || // NS Deep Freeze
				id == 75516 || // NS Patriot Flare Gun
				id == 30 || // Shotguns.
				id == 39000 || // Too.
				id == 7432 || // Fucking.
				id == 39001 || // Lazy.
				id == 39002 || //
				id == 1934 || //
				id == 128 || //
				id == 40000 || //
				id == 7423 || //
				id == 40001 || //
				id == 40002 || //
				id == 1884 || //
				id == 129 || //
				id == 41000 || //
				id == 41001 || //
				id == 41002 || //
				id == 1939 || //
				id == 75085 || //
				id == 75083 || //
				id == 75084 || //
				id == 802322 || //
				id == 1097 || //
				id == 1849 || //
				id == 7513 || // MAXes
				id == 130 || //
				id == 7505 || //
				id == 7507 || //
				id == 7508 || //
				id == 7519 || //
				id == 7520 || //
				id == 15000 || //
				id == 15001 || //
				id == 15004 || //
				id == 15005 || //
				id == 15009 || //
				id == 15012 || //
				id == 15013 || //
				id == 15015 || //
				id == 15016 || //
				id == 15017 || //
				id == 15021 || //
				id == 15024 || //
				id == 15025 || //
				id == 15030 || //
				id == 16000 || //
				id == 16001 || //
				id == 16004 || //
				id == 16005 || //
				id == 16009 || //
				id == 16012 || //
				id == 16013 || //
				id == 16016 || //
				id == 16017 || //
				id == 16021 || //
				id == 16024 || //
				id == 16025 || //
				id == 16026 || //
				id == 16028 ||
				id == 16029 ||
				id == 16030 ||
				id == 16031 ||
				id == 16032 ||
				id == 16033 ||
				id == 17000 ||
				id == 17001 ||
				id == 17004 ||
				id == 17005 ||
				id == 17011 ||
				id == 17012 ||
				id == 17013 ||
				id == 17016 ||
				id == 17017 ||
				id == 17023 ||
				id == 17024 ||
				id == 17025 ||
				id == 17026 ||
				id == 17030 ||
				id == 17149 ||
				id == 17150 ||
				id == 17151 ||
				id == 17152 ||
				id == 17153 ||
				id == 17154 ||
				id == 7512 ||
				id == 7518 ||
				id == 7506 ||
				id == 7507 ||
				id == 7505 ||
				id == 7525 ||
				id == 7519 ||
				id == 802781 || // Patchwork
				id == 802782 ||
				id == 803007 ||
				id == 803008 ||
				id == 803009 ||
				id == 803824 ||
				id == 803825 ||
				id == 803826 ||
				id == 2001 ||
				id == 2309 ||
				id == 2310 ||
				id == 2317 ||
				id == 2318 ||
				id == 7379 ||
				id == 7380 ||
				id == 7388 ||
				id == 7391 ||
				id == 7392 ||
				id == 7400 ||
				id == 7403 ||
				id == 7404 ||
				id == 87 ||
				id == 88 ||
				id == 89 ||
				id == 1974 ||
				id == 1979 ||
				id == 7301 ||
				id == 7316 ||
				id == 7337 ||
				id == 24000 ||
				id == 24001 ||
				id == 24002 ||
				id == 24003 ||
				id == 24004 ||
				id == 25000 ||
				id == 25001 ||
				id == 25002 ||
				id == 25003 ||
				id == 25004 ||
				id == 26000 ||
				id == 26001 ||
				id == 26002 ||
				id == 26003 ||
				id == 26004)) {
			return true;
		} else {
			return false;
		}
		
	}
	public static void main(String[] args) throws IOException {
		System.out.println("--J0KE.exe v1.2--");
		System.out.println("_________________");
		System.out.println();
		Scanner kb = new Scanner(System.in);
		String playerName;
		
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
					if (antiCheese(weaponValue)) { //remove non IVI
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
					deathCountKD++;
				}
				else if (event.contains("kills")) {
					int vehicleValue = Integer.parseInt(event.substring(event.indexOf("attacker_vehicle_id")+ 22, event.indexOf("is_critical", (event.indexOf("attacker_vehicle_id"))) - 3));
					if  (vehicleValue == 0) { //remove vehicle kills
						int weaponValue = Integer.parseInt(event.substring(event.indexOf("attacker_weapon_id") + 21, event.indexOf("character_id", (event.indexOf("attacker_weapon_id"))) - 3));
						if (antiCheese(weaponValue)) { //remove non IVI
							killCountKD++;
							}
						}
					}
				}
			System.out.println("Viable Kills (past 1000 kills): " + killCount + " Headshot kills: " + headshotCount);
			System.out.println("Kills: " + killCountKD + " Deaths: " + deathCountKD);
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
