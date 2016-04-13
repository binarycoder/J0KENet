package application;

///////////////////////////////////////////////////////////
//GitGud Stat Calculator v1.6
//By: Alek "binarycoder" Bollig - 2/1/2015
//Retrieves data from the Planetside 2 API, and calculates
//               various data.
///////////////////////////////////////////////////////////

import java.net.*;
import java.util.*;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GitGud {
  static String APIKEY = "example"; //register an api key to make this work. Change this in FXMLController as well.


  public static List<Integer> loadBanList(String searchValue, String filename) {
    return loadBanList(searchValue, filename, null);
  }

  public static List<Integer> loadBanList(String searchValue, String filename, List<Integer> defaultValue) {
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
      System.out.printf("File not found: %s\n", fileLocation.getAbsolutePath());
      return defaultValue;
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

  public static boolean antiCheese(int id, List<Integer> list) {
    for (int i = 0; i < list.size(); i++) {
      if (id == list.get(i)) {
        return false;
      }
    }
    return true;
  }

public static PlayerInfo getPlayerStats(String playerName, List<Integer> killBanList,
    List<Integer> enemyKillBanList, List<Integer> vehiclesAllowed,
    List<Integer> enemyVehiclesAllowed, int sampleSize) throws IOException {

  int SAMPLESIZE = sampleSize;
  String nameUpper = playerName;
  String detailedOut = "";
  int shotsFired = 0;
  int shotsHit = 0;
  boolean accuracyType = false; //false - default with no mods, true - support classes
  playerName = playerName.toLowerCase();
  int enemyAllowVehicles = enemyVehiclesAllowed.get(0);
  int allowVehicles = vehiclesAllowed.get(0);

  System.out.println("Fetching PID via API...");
  String playerId = censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/character/?name.first_lower=" + playerName);

  if (playerId.equals("{\"character_list\":[],\"returned\":0}")) {
    return null;
  } else {
    String playerBr = playerId;
    playerId = new JSONObject(playerId).getJSONArray("character_list").getJSONObject(0)
        .getString("character_id");
    detailedOut += "Player Id: " + playerId + "\n";
    final int battleRank = new JSONObject(playerBr).getJSONArray("character_list")
        .getJSONObject(0).getJSONObject("battle_rank").getInt("value");

    System.out.println("Fetching kill feed...");
    String killFeed = censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/characters_event/?character_id="
                                            + playerId + "&type=KILL&c:limit=" + SAMPLESIZE + "&c:join=loadout%5Eon:attacker_loadout_id%5Eto:loadout_id%5Einject_at:attacker_class");
    if (killFeed.equals("{\"characters_event_list\":[],\"returned\":0}")) {
      System.out.println("Received empty kill feed");
      return null;
    }

    System.out.println("Fetching death feed...");
    final String deathFeed = censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/characters_event/?character_id="
                                           + playerId + "&type=KILL,DEATH&c:limit=" + SAMPLESIZE);

    System.out.println("Fetching fake KD feed...");
    final String fakeKdString = censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/character/?character_id="
                                            + playerId + "&c:resolve=stat_history");

    JSONArray data = new JSONObject(killFeed).getJSONArray("characters_event_list");
    int killCount = 0;
    int headshotCount = 0;
    List<Integer> iviWeapons = new ArrayList<Integer>();
    List<Integer> iviWeaponsCount = new ArrayList<Integer>();
    List<Integer> classId = new ArrayList<Integer>();
    List<Integer> classCount = new ArrayList<Integer>();

    for (int i = 0; i < data.length(); i++) {
      int vehicleValue = data.getJSONObject(i).getInt("attacker_vehicle_id");
      if  (vehicleValue == 0 || allowVehicles == 1) { //remove vehicle kills
        int weaponValue = data.getJSONObject(i).getInt("attacker_weapon_id");
        if (antiCheese(weaponValue, killBanList)) { //remove non IVI
          killCount++;
          if (!iviWeapons.contains(weaponValue)) { //count most used weapons
            iviWeapons.add(weaponValue);
            iviWeaponsCount.add(1);
          } else {
            iviWeaponsCount.set(iviWeapons.indexOf(weaponValue),
                iviWeaponsCount.get(iviWeapons.indexOf(weaponValue)) + 1);
          }
          if (data.getJSONObject(i).has("attacker_class")) {
            int classValue = data.getJSONObject(i).getJSONObject("attacker_class").getInt("profile_id");
            if (!classId.contains(classValue)) { //count most used classes
              classId.add(classValue);
              classCount.add(1);
            } else {
              classCount.set(classId.indexOf(classValue),
                  classCount.get(classId.indexOf(classValue)) + 1);
            }
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
    
    //Sort class array
    for (int i = 1; i < classCount.size(); i++) {
      int temp = classCount.get(i);
      int tempId = classId.get(i);
      int sortValue;
      for (sortValue = i - 1; sortValue >= 0 && temp
          < classCount.get(sortValue); sortValue--) {
        classCount.set(sortValue + 1, classCount.get(sortValue));
        classId.set(sortValue + 1, classId.get(sortValue));
      }
      classCount.set(sortValue + 1, temp);
      classId.set(sortValue + 1, tempId);
    }
    //gather top 2-3 classes w / > 100 kills.
    if (classCount.size() != 0) {
      while ((classCount.size() > 3 || classCount.get(0) <= (SAMPLESIZE / 10))
        && classCount.size() > 1) {
      classCount.remove(0);
      classId.remove(0);
      }
    } else {
      return null;
    }
    //translate class types
    for (int i = 0; i< classId.size(); i++) {
      int replacement = new JSONObject(censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/profile?profile_id=" + classId.get(i)))
          .getJSONArray("profile_list").getJSONObject(0).getInt("profile_type_id");
      classId.set(i, replacement);
    }

    List<String> weaponNames = new ArrayList<String>();
    double backupAccuracy = 0;
    for (int i = 0; i < iviWeapons.size(); i++) {
      System.out.println("Fetching weapon data...");
      String weaponData = censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/"
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
      backupAccuracy += hit / fire;
    }
    backupAccuracy /= iviWeapons.size();
    
    double accuracy = 0;
    String accuracyList = "";
    for (int i = 0; i< classId.size(); i++) {
      accuracyList += classId.get(i) + ",";
    }
    accuracyList = accuracyList.substring(0,accuracyList.length() - 1);
    JSONObject accuracyData = new JSONObject(censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/characters_stat?character_id="+ playerId +"&stat_name=fire_count,hit_count&profile_id=" + accuracyList + "&c:limit=50"));
    double totalFireDaily = 0;
    double totalFireWeekly = 0;
    double totalFireMonthly = 0;
    double totalFireLife = 0;
    double totalHitDaily = 0;
    double totalHitWeekly = 0;
    double totalHitMonthly = 0;
    double totalHitLife = 0;
    
    JSONArray classStat = accuracyData.getJSONArray("characters_stat_list");
    for (int i = 0; i < classStat.length(); i++) {
      String stat_type = classStat.getJSONObject(i).getString("stat_name");
      JSONObject classData = classStat.getJSONObject(i);
      if (stat_type.equals("fire_count")) {
        double tempFireDaily = classData.getInt("value_daily");
        double tempFireWeekly = classData.getInt("value_weekly");
        double tempFireMonthly = classData.getInt("value_monthly");
        double tempFireLife = classData.getInt("value_forever");
        if (classData.getInt("profile_id") == 3) {
          accuracyType = true;
          System.out.println("Engineer calculation- applying HWA.");
          String repTool = censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/characters_weapon_stat?character_id=" + playerId + "&stat_name=weapon_fire_count&item_id=125,1256,1257,1258,1259,6011,126,1260,1261,1262,1263,6012,124,1252,1253,1254,1255,6010,803809,803810,803811,803812,803813,803814,803804,803803,803805,803807,803808,803806,803815,803816,803817,803818,803819,803820&c:resolve=item&c:limit=50&c:sort=value:-1");
          
          JSONArray toolList = new JSONObject(repTool).getJSONArray("characters_weapon_stat_list");
          double totalFiredShots = 0;
          for (int j = 0; j < toolList.length(); j++) {
            totalFiredShots += toolList.getJSONObject(j).getInt("value");
          }
          
          double fixedRatio = 1 - (totalFiredShots/tempFireLife);
          detailedOut += "Engineer HWA ratio: " + fixedRatio + "\n";
          
          tempFireDaily *= fixedRatio;
          tempFireWeekly *= fixedRatio;
          tempFireMonthly *= fixedRatio;
          tempFireLife *= fixedRatio;
          
          totalFireDaily += tempFireDaily;
          totalFireWeekly += tempFireWeekly;
          totalFireMonthly += tempFireMonthly;
          totalFireLife += tempFireLife;
        }
        else if (classData.getInt("profile_id") == 4) {
          accuracyType = true;
          System.out.println("Medic calculation- applying HWA.");
          String medTool = censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/characters_weapon_stat?character_id=" + playerId + "&stat_name=weapon_fire_count&item_id=1611,1610,1609,1608,803786,803792,803787,803793,803794,803795,803796,803797,803785,803798,803799,803800,803801,803802,94,1608,1609,1610,1611,1612,95,1617,1618,1619,1620,1621,93,1626,1627,1628,1628,1630&c:resolve=item&c:limit=50&c:sort=value:-1");
          JSONArray toolList = new JSONObject(medTool).getJSONArray("characters_weapon_stat_list");
          double totalFiredShots = 0;
          for (int j = 0; j < toolList.length(); j++) {
            totalFiredShots += toolList.getJSONObject(j).getInt("value");
          }
          
          double fixedRatio = 1 - (totalFiredShots/tempFireLife);
          detailedOut += "Medic HWA ratio: " + fixedRatio + "\n";

          tempFireDaily *= fixedRatio;
          tempFireWeekly *= fixedRatio;
          tempFireMonthly *= fixedRatio;
          tempFireLife *= fixedRatio;
          
          totalFireDaily += tempFireDaily;
          totalFireWeekly += tempFireWeekly;
          totalFireMonthly += tempFireMonthly;
          totalFireLife += tempFireLife;
        } else {
          totalFireDaily += classData.getInt("value_daily");
          totalFireWeekly += classData.getInt("value_weekly");
          totalFireMonthly += classData.getInt("value_monthly");
          totalFireLife += classData.getInt("value_forever");
        }
      } else if (stat_type.equals("hit_count")) {
        totalHitDaily += classData.getInt("value_daily");
        totalHitWeekly += classData.getInt("value_weekly");
        totalHitMonthly += classData.getInt("value_monthly");
        totalHitLife += classData.getInt("value_forever");
      }

    }
    if (totalFireDaily >= SAMPLESIZE) {
      accuracy = totalHitDaily/totalFireDaily;
      detailedOut += "Accuracy Taken from Daily Data-\n";
      detailedOut += "Shots Fired: " + totalFireDaily + "\n";
      detailedOut += "Shots Hit: " + totalHitDaily + "\n";
    } else if (totalFireWeekly >= SAMPLESIZE) {
      accuracy = totalHitWeekly/totalFireWeekly;
      detailedOut += "Accuracy Taken from Weekly Data-\n";
      detailedOut += "Shots Fired: " + totalFireWeekly + "\n";
      detailedOut += "Shots Hit: " + totalHitWeekly + "\n";
    } else if (totalFireMonthly >= SAMPLESIZE) {
      accuracy = totalHitMonthly/totalFireMonthly;
      detailedOut += "Accuracy Taken from Monthly Data-\n";
      detailedOut += "Shots Fired: " + totalFireMonthly + "\n";
      detailedOut += "Shots Hit: " + totalHitMonthly + "\n";
    } else if (totalFireLife >= SAMPLESIZE && totalFireLife < SAMPLESIZE * 3) {
      accuracy = totalHitLife/totalFireLife;
      detailedOut += "Accuracy Taken from Lifetime Data-\n";
      detailedOut += "Shots Fired: " + totalFireLife + "\n";
      detailedOut += "Shots Hit: " + totalHitLife + "\n";
    } else {
      System.out.println("Using backup accuracy" + totalFireLife);
      //accuracy = backupAccuracy;
      accuracy = totalHitLife/totalFireLife;
      detailedOut += "Accuracy Taken from Lifetime Data-\n";
      detailedOut += "(Due to a lack of historical stats)\n";
      detailedOut += "Shots Fired: " + shotsFired + "\n";
      detailedOut += "Shots Hit: " + shotsHit + "\n";
    }
    
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
    detailedOut += "Top Classes: " + classId.toString();
    
    
    if (accuracyType == true) {
      return new PlayerInfo(nameUpper + "*", battleRank, (double) fakeK / (double) fakeD, (double) killCountKd / (double) deathCountKd, accuracy, (double) headshotCount / (double) killCount, accuracy * (double) ((double) headshotCount
          / (double) killCount) * 10000, iviWeapons, weaponNames, detailedOut);
    } else {
      return new PlayerInfo(nameUpper, battleRank, (double) fakeK / (double) fakeD, (double) killCountKd / (double) deathCountKd, accuracy, (double) headshotCount / (double) killCount, accuracy * (double) ((double) headshotCount
        / (double) killCount) * 10000, iviWeapons, weaponNames, detailedOut);
    }
    
  }
}

  public static void main(String[] args) throws IOException {
    System.out.println("--GitGud v1.4--");
    System.out.println("_______________");
    System.out.println();
    Scanner kb = new Scanner(System.in);
    String playerName;
    List<Integer> killBanList = GitGud.loadBanList("KILLFILTER", "files\\default.txt");
    List<Integer> enemyKillBanList = GitGud.loadBanList("ENEMYKILLFILTER", "files\\default.txt");
    List<Integer> allowVehicles = GitGud.loadBanList("FRIEDLYVEHICLESALLOWED", "files\\default.txt");
    List<Integer> enemyAllowVehicles = GitGud.loadBanList("ENEMYVEHICLESALLOWED", "files\\default.txt");

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