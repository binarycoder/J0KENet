///////////////////////////////////////////////////////////
//J0KE.exe Stat Calculator v1.4
//By: Alek "binarycoder" Bollig - 2/1/2015
//Retrieves data from the Planetside 2 API, and calculates
//               Kill/Death Ratio and Head Shot percentage 
///////////////////////////////////////////////////////////

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class kdrcalc {
  private static final int SAMPLESIZE = 1000;
  
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
    System.out.println("--J0KE.exe v1.4--");
    System.out.println("_________________");
    System.out.println();
    Scanner kb = new Scanner(System.in);
    String playerName;
    String killFilter = "killfilter";
    String deathFilter = "deathfilter";
          
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
      System.out.print(". ");
      while (playerId.equals("{\"character_list\":[],\"returned\":0}")) {
        System.out.println("Error: No character data. Check spelling twice,"
            + "try again, then yell at binary.");
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
    
      String playerBr = playerId;
      playerId = new JSONObject(playerId).getJSONArray("character_list").getJSONObject(0)
          .getString("character_id");
      final int battleRank = new JSONObject(playerBr).getJSONArray("character_list")
          .getJSONObject(0).getJSONObject("battle_rank").getInt("value");
      String killFeed = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/characters_event/?character_id="
                                              + playerId + "&type=KILL&c:limit=" + SAMPLESIZE);
      System.out.print(". ");
      final String deathFeed = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/characters_event/?character_id="
                                             + playerId + "&type=KILL,DEATH&c:limit=" + SAMPLESIZE);
      System.out.print(". ");
      final String fakeKdString = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/character/?character_id="
                                              + playerId + "&c:resolve=stat_history");
      System.out.print(". ");
   
      JSONArray data = new JSONObject(killFeed).getJSONArray("characters_event_list");
      int killCount = 0;
      int headshotCount = 0;
      ArrayList<Integer> iviWeapons = new ArrayList<Integer>();
      ArrayList<Integer> iviWeaponsCount = new ArrayList<Integer>();
    
    
      for (int i = 0; i < data.length(); i++) {
        int vehicleValue = data.getJSONObject(i).getInt("attacker_vehicle_id");
        if  (vehicleValue == 0) { //remove vehicle kills
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
      while ((iviWeaponsCount.size() > 3 || iviWeaponsCount.get(0) <= (SAMPLESIZE / 10))
          && iviWeaponsCount.size() > 1) {
        iviWeaponsCount.remove(0);
        iviWeapons.remove(0);
      }
      String weaponNames = " - ";
      double accuracy = 0;
      for (int i = 0; i < iviWeapons.size(); i++) {
        String weaponData = censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/"
            + "characters_weapon_stat?character_id="
            + playerId + "&item_id=" + iviWeapons.get(i)
            + "&c:resolve=item&c:limit=50&c:sort=value:-1");
        System.out.print(". ");
        JSONArray weaponDataArray = new JSONObject(weaponData)
            .getJSONArray("characters_weapon_stat_list");
           
        weaponNames += weaponDataArray.getJSONObject(0)
            .getJSONObject("item").getJSONObject("name").getString("en") + " - ";
        double fire = weaponDataArray.getJSONObject(2).getDouble("value");
        double hit = weaponDataArray.getJSONObject(3).getDouble("value");
        accuracy += hit / fire;
      }
      accuracy /= iviWeapons.size();
      
      JSONArray deathData = new JSONObject(deathFeed).getJSONArray("characters_event_list");
      int killCountKd = 0;
      int deathCountKd = 0;
      for (int i = 0; i < deathData.length(); i++) {
        String event = deathData.getJSONObject(i).getString("table_type");
        if (event.equals("deaths")) {
          int weaponValue = deathData.getJSONObject(i).getInt("attacker_weapon_id");
          if (antiCheese(weaponValue,deathBanList)) {
            deathCountKd++;
          }
        } else if (event.equals("kills")) {
          int vehicleValue = deathData.getJSONObject(i).getInt("attacker_vehicle_id");
          if  (vehicleValue == 0) { //remove vehicle kills
            int weaponValue = deathData.getJSONObject(i).getInt("attacker_weapon_id");
            if (antiCheese(weaponValue, killBanList)) { //remove non IVI
              killCountKd++;
            }
          }
        }
      }
      JSONArray reviveKdArray = new JSONObject(fakeKdString).getJSONArray("character_list")
          .getJSONObject(0).getJSONObject("stats").getJSONArray("stat_history");
      int kdIncrement = 0;
      int fakeK = 0;
      int fakeD = 0;
      int week = 1; 
      while (kdIncrement < SAMPLESIZE && week <= 9) {
        fakeD += reviveKdArray.getJSONObject(2).getJSONObject("week").getInt("w0" + week);
        fakeK += reviveKdArray.getJSONObject(5).getJSONObject("week").getInt("w0" + week);
        week++;
        kdIncrement += (fakeK + fakeD);
      }
      System.out.println();
      if (kdIncrement < SAMPLESIZE) {
        System.out.println("!Results lower than Sample Size! Affects Fake KD,"
            + " probably Accuracy and weapon selection as well.");
      }
      
      System.out.println("Viable Kills (past " + SAMPLESIZE + " kills): "
          + killCount + " | Headshot kills: " + headshotCount);
      System.out.println("Kills: " + killCountKd + " | Deaths: " + deathCountKd);
      System.out.println("IVI Calculated from the following weapon(s): " + weaponNames);
      System.out.println();
      System.out.println("-----------------");
      System.out.printf("BR: %d%n", battleRank);
      System.out.printf("Revive K/D: %.3f%n", (double) fakeK / (double) fakeD);
      System.out.printf("True KD: %.3f%n", (double) killCountKd / (double) deathCountKd);
      System.out.printf("Accuracy: %.2f%%%n", accuracy * 100);
      System.out.printf("HSR: %.2f%%%n", ((double) headshotCount / (double) killCount) * 100);
      System.out.printf("IVI: %.2f%n", accuracy * (double) ((double) headshotCount
          / (double) killCount) * 10000);
      System.out.println("-----------------");
      System.out.println();
      System.out.print("Enter player name to retrieve statistics(or \"exit\" to quit): ");
      playerName = kb.next().toLowerCase();
    }
    kb.close();
    System.out.println("Program Terminated.");
  }
}