package application;

///////////////////////////////////////////////////////////
//GitGud Stat Calculator v1.4
//By: Alek "binarycoder" Bollig - 2/1/2015
//Retrieves data from the Planetside 2 API, and calculates
//               various data. 
///////////////////////////////////////////////////////////

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.control.ProgressBar;
import javafx.concurrent.Task;

public class GitGud {
  public static ArrayList<Integer> loadBanList(String searchValue, String filename) {
    ArrayList<Integer> output = new ArrayList<Integer>();
    File fileLocation = new File(filename);
    try {
      Scanner file = new Scanner(fileLocation);
      boolean endLine = false;
      while (file.hasNextLine() && endLine == false) {
        String nextValue = file.nextLine();
        while (!nextValue.equals(searchValue)) {
          nextValue = file.nextLine();
        }
        while (file.hasNextInt()) {
          output.add(file.nextInt());
        }
        endLine = true;
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
  
public static PlayerInfo getPlayerStats(String playerName, ArrayList<Integer> killBanList,
    ArrayList<Integer> enemyKillBanList, ArrayList<Integer> vehiclesAllowed,
    ArrayList<Integer> enemyVehiclesAllowed, int sampleSize) throws IOException {
  int SAMPLESIZE = sampleSize;
  String nameUpper = playerName;
  String detailedOut = "";
  int shotsFired = 0;
  int shotsHit = 0;
  playerName = playerName.toLowerCase();
  int enemyAllowVehicles = enemyVehiclesAllowed.get(0);
  int allowVehicles = vehiclesAllowed.get(0);

  String playerId = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/character/?name.first_lower=" + playerName);
  
  if (playerId.equals("{\"character_list\":[],\"returned\":0}")) {
    return null;
  } else {
    String playerBr = playerId;
    playerId = new JSONObject(playerId).getJSONArray("character_list").getJSONObject(0)
        .getString("character_id");
    detailedOut += "Player Id: " + playerId + "\n";
    final int battleRank = new JSONObject(playerBr).getJSONArray("character_list")
        .getJSONObject(0).getJSONObject("battle_rank").getInt("value");
    String killFeed = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/characters_event/?character_id="
                                            + playerId + "&type=KILL&c:limit=" + SAMPLESIZE);
    if (killFeed.equals("{\"characters_event_list\":[],\"returned\":0}")) {
      return null;
    }
    final String deathFeed = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/characters_event/?character_id="
                                           + playerId + "&type=KILL,DEATH&c:limit=" + SAMPLESIZE);
    final String fakeKdString = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/character/?character_id="
                                            + playerId + "&c:resolve=stat_history");
 
    JSONArray data = new JSONObject(killFeed).getJSONArray("characters_event_list");
    int killCount = 0;
    int headshotCount = 0;
    ArrayList<Integer> iviWeapons = new ArrayList<Integer>();
    ArrayList<Integer> iviWeaponsCount = new ArrayList<Integer>();
  
    for (int i = 0; i < data.length(); i++) {
      int vehicleValue = data.getJSONObject(i).getInt("attacker_vehicle_id");
      if  (vehicleValue == 0 || allowVehicles == 1) { //remove vehicle kills
        int weaponValue = data.getJSONObject(i).getInt("attacker_weapon_id");
        if (antiCheese(weaponValue, killBanList)) { //remove non IVI
          killCount++;
          if (!iviWeapons.contains(weaponValue)) {
            iviWeapons.add(weaponValue);
            iviWeaponsCount.add(1);
          } else {
            iviWeaponsCount.set(iviWeapons.indexOf(weaponValue),
                iviWeaponsCount.get(iviWeapons.indexOf(weaponValue)) + 1);
          }
          int headshotValue = data.getJSONObject(i).getInt("is_headshot");
          if (headshotValue == 1) { //Mark Headshots
            headshotCount++;
          }
        }
      }
    }
      //Sort array 
    for (int i = 1; i < iviWeaponsCount.size(); i++) {
      int temp = iviWeaponsCount.get(i);
      int tempId = iviWeapons.get(i);
      int sortValue;
      for (sortValue = i - 1; sortValue >= 0 && temp
          < iviWeaponsCount.get(sortValue); sortValue--) {
        iviWeaponsCount.set(sortValue + 1, iviWeaponsCount.get(sortValue));
        iviWeapons.set(sortValue + 1, iviWeapons.get(sortValue));
      }
      iviWeaponsCount.set(sortValue + 1, temp);
      iviWeapons.set(sortValue + 1, tempId);
    }
    //gather top 2-3 weapons w / > 100 kills.
    if (iviWeaponsCount.size() != 0) {
      while ((iviWeaponsCount.size() > 3 || iviWeaponsCount.get(0) <= (SAMPLESIZE / 10))
        && iviWeaponsCount.size() > 1) {
      iviWeaponsCount.remove(0);
      iviWeapons.remove(0);
      }
    } else {
      return null;
    }
    
    ArrayList<String> weaponNames = new ArrayList<String>();
    double accuracy = 0;
    for (int i = 0; i < iviWeapons.size(); i++) {
      String weaponData = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/"
          + "characters_weapon_stat?character_id="
          + playerId + "&item_id=" + iviWeapons.get(i)
          + "&c:resolve=item&c:limit=50&c:sort=value:-1");
      JSONArray weaponDataArray = new JSONObject(weaponData)
          .getJSONArray("characters_weapon_stat_list");
      int weaponDataSize = new JSONObject(weaponData).getInt("returned");
      if (weaponDataSize != 5) {
        return null;
      }
      weaponNames.add(weaponDataArray.getJSONObject(0)
          .getJSONObject("item").getJSONObject("name").getString("en"));
      double fire = weaponDataArray.getJSONObject(2).getDouble("value");
      double hit = weaponDataArray.getJSONObject(3).getDouble("value");
      shotsFired += fire;
      shotsHit += hit;
      accuracy += hit / fire;
    }
    accuracy /= iviWeapons.size();
    
    JSONArray deathData = new JSONObject(deathFeed).getJSONArray("characters_event_list");
    int killCountKd = 0;
    int deathCountKd = 0;
    for (int i = 0; i < deathData.length(); i++) {
      String event = deathData.getJSONObject(i).getString("table_type");
      if (event.equals("deaths")) {
        int vehicleValue = deathData.getJSONObject(i).getInt("attacker_vehicle_id");
        if  (vehicleValue == 0 || enemyAllowVehicles == 1) { //remove vehicle kills 
          int weaponValue = deathData.getJSONObject(i).getInt("attacker_weapon_id");
          if (antiCheese(weaponValue,enemyKillBanList)) {
            deathCountKd++;
          }
        }
        
      } else if (event.equals("kills")) {
        int vehicleValue = deathData.getJSONObject(i).getInt("attacker_vehicle_id");
        if  (vehicleValue == 0 || allowVehicles == 1) { //remove vehicle kills
          int weaponValue = deathData.getJSONObject(i).getInt("attacker_weapon_id");
          if (antiCheese(weaponValue, killBanList)) { //remove non IVI
            killCountKd++;
          }
        }
      }
    }
    if (deathCountKd == 0) {
      deathCountKd = 1;
    }
    JSONArray reviveKdArray = new JSONObject(fakeKdString).getJSONArray("character_list")
        .getJSONObject(0).getJSONObject("stats").getJSONArray("stat_history");
    int kdIncrement = 0;
    int fakeK = 0;
    int fakeD = 1;
    int week = 1; 
    while (kdIncrement < SAMPLESIZE && week <= 9) {
      fakeD += reviveKdArray.getJSONObject(2).getJSONObject("week").getInt("w0" + week);
      fakeK += reviveKdArray.getJSONObject(5).getJSONObject("week").getInt("w0" + week);
      week++;
      kdIncrement += (fakeK + fakeD);
    }
    if (fakeD == 0) {
      fakeD = 1;
    }
    detailedOut += "Revive Kills: " + fakeK + "\n";
    detailedOut += "Revive Deaths: " + fakeD + "\n";
    detailedOut += "# Weeks parsed: " + week + "\n";
    detailedOut += "Filtered Kills: " + killCountKd + "\n";
    detailedOut += "Filtered Deaths: " + deathCountKd + "\n";
    detailedOut += "Filterd Kills for HSR: " + killCount + "\n";
    detailedOut += "Headshot Kills: " + killCountKd + "\n";
    detailedOut += "Shots Fired: " + shotsFired + "\n";
    detailedOut += "Shots Hit: " + shotsHit + "\n";
    return new PlayerInfo(nameUpper, battleRank, (double) fakeK / (double) fakeD, (double) killCountKd / (double) deathCountKd, accuracy, (double) headshotCount / (double) killCount, accuracy * (double) ((double) headshotCount
        / (double) killCount) * 10000, iviWeapons, weaponNames, detailedOut);
  }
}
  
  public static void main(String[] args) throws IOException {
    System.out.println("--GitGud v1.4--");
    System.out.println("_______________");
    System.out.println();
    Scanner kb = new Scanner(System.in);
    String playerName;
    String killFilter = "killfilter";
    String deathFilter = "deathfilter";
    
    ArrayList<Integer> killBanList = GitGud.loadBanList("KILLFILTER", "files\\default.txt");
    ArrayList<Integer> enemyKillBanList = GitGud.loadBanList("ENEMYKILLFILTER", "files\\default.txt");
    ArrayList<Integer> allowVehicles = GitGud.loadBanList("FRIEDLYVEHICLESALLOWED", "files\\default.txt");
    ArrayList<Integer> enemyAllowVehicles = GitGud.loadBanList("ENEMYVEHICLESALLOWED", "files\\default.txt");
          
    System.out.print("Enter player name to retrieve statistics(or \"exit\" to quit): ");
    playerName = kb.next().toLowerCase();
    while (!playerName.equals("exit")) {
      PlayerInfo data = getPlayerStats(playerName, killBanList, enemyKillBanList, allowVehicles, enemyAllowVehicles, 1000);
      while (data == null) {
        System.out.println("Error: No character data. Check spelling twice,"
            + "try again, then yell at binary.");
        System.out.print("Enter player name to retrieve statistics(or \"exit\" to quit): ");
        playerName = kb.next().toLowerCase();
        if (playerName.equals("exit")) {
          kb.close();
          System.out.println("Program Terminated.");
          return;
        } else {
          data = getPlayerStats(playerName, killBanList, enemyKillBanList, allowVehicles, enemyAllowVehicles, 1000);
          
          
        }
      }
      System.out.println("IVI Calculated from the following weapon(s): " + data.getWeaponString().toString());
      System.out.println();
      System.out.println("-----------------");
      System.out.printf("BR: %d%n", data.getBR());
      System.out.printf("Revive K/D: %.3f%n", data.getRKD());
      System.out.printf("True KD: %.3f%n", data.getTKD());
      System.out.printf("Accuracy: %.2f%%%n", data.getAcc());
      System.out.printf("HSR: %.2f%%%n", data.getHSR() * 100);
      System.out.printf("IVI: %.2f%n", data.getIVI());
      System.out.println("-----------------");
      System.out.println();
      System.out.print("Enter player name to retrieve statistics(or \"exit\" to quit): ");
      playerName = kb.next().toLowerCase();
    }
    kb.close();
    System.out.println("Program Terminated.");
  }
}