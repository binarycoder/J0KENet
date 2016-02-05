package application;

import java.util.ArrayList;

public class PlayerInfo {
  static String playerName;
  static int BR;
  static double RKD;
  static double TKD;
  static double Acc;
  static double HSR;
  static double IVI;
  static ArrayList<Integer> weapons;
  static ArrayList<String> weaponString;
  static String detailedOutput;
  public PlayerInfo (String playerNameIn, int BRIn, double RKDIn, double TKDIn, double AccIn, double HSRIn, double IVIIn, ArrayList<Integer> weaponsIn, ArrayList<String> weaponStringIn, String detailedOutputIn) {
    playerName = playerNameIn;
    BR = BRIn;
    RKD = RKDIn;
    TKD = TKDIn;
    Acc = AccIn;
    HSR = HSRIn;
    IVI = IVIIn;
    weapons = weaponsIn;
    weaponString = weaponStringIn;
    detailedOutput = detailedOutputIn;
    
  }
  public String getPlayerName() {
    return playerName;
  }
  public int getBR() {
    return BR;
  }
  public double getTKD() {
    return TKD;
  }
  public double getRKD() {
    return RKD;
  }
  public double getAcc() {
    return Acc;
  }
  public double getHSR() {
    return HSR;
  }
  public double getIVI() {
    return IVI;
  }
  public ArrayList<Integer> getWeapons() {
    return weapons;
  }
  public ArrayList<String> getWeaponString() {
    return weaponString;
  }
  public String getDetailedOutput() {
    return detailedOutput;
  }
}

