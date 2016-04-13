package application;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;


public class FXMLController {
  static String APIKEY = "example"; //register an api key to make this work. Change this in GitGud as well.

  String playerName = "";
  int historySize = 0;

  String defaultConfigFile = String.format("files%sdefault.txt", File.separatorChar);

  List<Integer> killBanList = GitGud.loadBanList("KILLFILTER", defaultConfigFile, Arrays.asList(1));
  List<Integer> enemyKillBanList = GitGud.loadBanList("ENEMYKILLFILTER", defaultConfigFile, Arrays.asList(0));
  List<Integer> allowVehicles = GitGud.loadBanList("FRIEDLYVEHICLESALLOWED", defaultConfigFile, Arrays.asList(0));
  List<Integer> enemyAllowVehicles = GitGud.loadBanList("ENEMYVEHICLESALLOWED", defaultConfigFile, Arrays.asList(1));
  List<String> storedOutfitData = new ArrayList<String>();
  @FXML private Text FXPlayerName, FXBR, FXRKD, FXTKD, FXAcc, FXHSR, FXIVI, FXWeapon1Name, FXWeapon2Name, FXWeapon3Name,
                     FXOutputString, FXKillFilter, FXEnemyKillFilter, FXVehicles, FXEnemyVehicles, FXLoadedFilter, HistoryString,
                     HistoryStringBR, HistoryStringTKD, HistoryStringRKD, HistoryStringAcc, HistoryStringIVI, HistoryStringHSR, HistoryStringCOMP;
  @FXML private TextField NameEnterField, SampleEnterField, FXFilterName;
  @FXML private ImageView FXWeapon1, FXWeapon2, FXWeapon3;
  @FXML private ProgressBar progressBarParse;
  @FXML private AnchorPane historyGridBack;
  @FXML private Button gitGudButton;
  @FXML private ToggleGroup queryType;
  @FXML private RadioButton FXQueryPlayer, FXQueryOutfit;
  @FXML protected void EnterPlayerName(ActionEvent event) {

  }
  @FXML protected void FXFilterLoad(ActionEvent event) {
    String fileName = FXFilterName.getText();

    String path = String.format("files%s%s.txt", File.separatorChar, fileName);
    File f = new File(path);

    if(f.exists() && !f.isDirectory()) {
      killBanList = GitGud.loadBanList("KILLFILTER", path);
      enemyKillBanList = GitGud.loadBanList("ENEMYKILLFILTER", path);
      allowVehicles = GitGud.loadBanList("FRIEDLYVEHICLESALLOWED", path);
      enemyAllowVehicles = GitGud.loadBanList("ENEMYVEHICLESALLOWED", path);
      FXKillFilter.setText(Integer.toString(killBanList.size()));
      FXEnemyKillFilter.setText(Integer.toString(enemyKillBanList.size()));
      if (allowVehicles.get(0) == 1) {
        FXVehicles.setText("YES");
      } else {
        FXVehicles.setText("NO");
      }
      if (enemyAllowVehicles.get(0) == 1) {
        FXEnemyVehicles.setText("YES");
      } else {
        FXEnemyVehicles.setText("NO");
      }
      FXLoadedFilter.setText("Currently loaded: \"" + fileName + "\"");
    } else {
      FXLoadedFilter.setText("Error: File does not exist.");
    }
  }

  @FXML protected void CalculateButtonEnter(KeyEvent event) throws IOException, InterruptedException {
    if (event.getCode() == KeyCode.ENTER && gitGudButton.isDisable() == false) {
      CalculateButton(new ActionEvent());
    }
  }
  @FXML protected void filterPressEnter(KeyEvent event) throws IOException, InterruptedException {
    if (event.getCode() == KeyCode.ENTER && gitGudButton.isDisable() == false) {
      FXFilterLoad(new ActionEvent());
    }
  }

  @FXML protected void clearHistory(ActionEvent event) {
    HistoryString.setText("");
    HistoryStringBR.setText("");
    HistoryStringRKD.setText("");
    HistoryStringTKD.setText("");
    HistoryStringAcc.setText("");
    HistoryStringHSR.setText("");
    HistoryStringIVI.setText("");
    historySize = 0;
    historyGridBack.setPrefHeight(20);
    storedOutfitData.clear();
  }

  @FXML protected void exportCSV(ActionEvent event) throws FileNotFoundException, UnsupportedEncodingException {
    PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
    writer.println("Player Name,BR,Revive K/D,True K/D,Accuracy,HSR,IVI");
    for (int i = 0; i < storedOutfitData.size(); i++) {
      String player = storedOutfitData.get(i);
      writer.println(player);
    }
    writer.close();
  }

  @FXML protected void CalculateButton(ActionEvent event) throws IOException, InterruptedException {
    Task<Void> task = new Task<Void>() {

      @Override
      protected Void call() throws Exception {
        try {
          gitGudButton.setDisable(true);
          playerName = NameEnterField.getText();
          int sampleSize = Integer.parseInt(SampleEnterField.getText());
          updateProgress(3, 10);
          PlayerInfo data = GitGud.getPlayerStats(playerName, killBanList, killBanList, allowVehicles, enemyAllowVehicles, sampleSize);
          if (data == null) {
            FXPlayerName.setText("Error: Check spelling");
            gitGudButton.setDisable(false);
            updateProgress(0, 10);
            return null;
          } else {
            updateProgress(4, 10);
            FXPlayerName.setText(data.getPlayerName());
            FXBR.setText(String.format("%d", data.getBR()).toString());
            FXRKD.setText(String.format("%.3f", data.getRKD()).toString());
            FXTKD.setText(String.format("%.3f", data.getTKD()).toString());
            FXAcc.setText(String.format("%.2f%%", data.getAcc() * 100).toString());
            FXHSR.setText(String.format("%.2f%%", data.getHSR() * 100).toString());
            FXIVI.setText(String.format("%.2f", data.getIVI()).toString());
            FXOutputString.setText(data.getDetailedOutput());
            updateProgress(6, 10);
            ImageView[] imagePositionArray = {FXWeapon1,FXWeapon2,FXWeapon3};
            Text[] imageNameArray = {FXWeapon1Name,FXWeapon2Name,FXWeapon3Name};


            List<Integer> weaponIds = data.getWeapons();
            List<String> weaponNames = data.getWeaponString();
            for (int i = 0; i < 3; i ++) {
              imagePositionArray[i].setImage(null);
              imageNameArray[i].setOpacity(0);
            }
            updateProgress(7, 10);
            for (int i = weaponIds.size() - 1; i >= 0 ; i--) {
              int weaponId = new JSONObject(GitGud.censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2/item?item_id=" + weaponIds.get(i))).getJSONArray("item_list").getJSONObject(0).getInt("image_id");
              Image weapon = new Image("https://census.daybreakgames.com/files/ps2/images/static/" + weaponId + ".png");
              imageNameArray[i].setText(weaponNames.get(i));
              imagePositionArray[i].setImage((Image) weapon);
              imageNameArray[i].setOpacity(1);
            }
            updateProgress(8, 10);

            //load into gridpane
            HistoryString.setText(HistoryString.getText() + FXPlayerName.getText() + "\n");
            HistoryStringBR.setText(HistoryStringBR.getText() + FXBR.getText() + "\n");
            HistoryStringRKD.setText(HistoryStringRKD.getText() + FXRKD.getText() + "\n");
            HistoryStringTKD.setText(HistoryStringTKD.getText() + FXTKD.getText() + "\n");
            HistoryStringAcc.setText(HistoryStringAcc.getText() + FXAcc.getText() + "\n");
            HistoryStringHSR.setText(HistoryStringHSR.getText() + FXHSR.getText() + "\n");
            HistoryStringIVI.setText(HistoryStringIVI.getText() + FXIVI.getText() + "\n");
            historySize++;
            historyGridBack.setPrefHeight(historyGridBack.getPrefHeight() + 16);
            updateProgress(0, 10);
            gitGudButton.setDisable(false);
            return null;
          }
        } catch(Exception e) {
          e.printStackTrace();
          return null;
        }
      }

    };
    Task<Void> taskOutfit = new Task<Void>() {

      @Override
      protected Void call() throws Exception {
        storedOutfitData.clear();
        HistoryString.setText("");
        HistoryStringBR.setText("");
        HistoryStringRKD.setText("");
        HistoryStringTKD.setText("");
        HistoryStringAcc.setText("");
        HistoryStringHSR.setText("");
        HistoryStringIVI.setText("");
        FXOutputString.setText("");
        historySize = 0;
        historyGridBack.setPrefHeight(20);
        ImageView[] imagePositionArray = {FXWeapon1,FXWeapon2,FXWeapon3};
        Text[] imageNameArray = {FXWeapon1Name,FXWeapon2Name,FXWeapon3Name};
        for (int i = 0; i < 3; i ++) {
          imagePositionArray[i].setImage(null);
          imageNameArray[i].setOpacity(0);
        }
        double avgRKD = 0, avgTKD = 0, avgBR = 0, avgAcc = 0, avgHSR = 0, avgIVI = 0;
        int numberPassed = 0;
        gitGudButton.setDisable(true);
        int sampleSize = Integer.parseInt(SampleEnterField.getText());
        String outfitName;
        outfitName = NameEnterField.getText();
        String checkOutfit = GitGud.censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/outfit/?alias=" + outfitName);
        if (checkOutfit.equals("{\"outfit_list\":[],\"returned\":0}")) {
          FXPlayerName.setText("Error: Check spelling");
          gitGudButton.setDisable(false);
          updateProgress(0, 10);
          return null;
        }
        String outfitId = new JSONObject(GitGud.censusFetch("https://census.daybreakgames.com/s:jokeNet/get/ps2:v2/outfit/?alias=" + outfitName))
            .getJSONArray("outfit_list").getJSONObject(0).getString("outfit_id");
        String outfitData = GitGud.censusFetch("https://census.daybreakgames.com/s:" + APIKEY + "/get/ps2:v2/outfit/?outfit_id="
            + outfitId + "&c:resolve=member_character(name)");
        int numberMembers = new JSONObject(outfitData).getJSONArray("outfit_list").getJSONObject(0).getInt("member_count");

        updateProgress(0, numberMembers);
        JSONArray playerData = new JSONObject(outfitData).getJSONArray("outfit_list").getJSONObject(0).getJSONArray("members");
        for (int i = 0; i < numberMembers; i++) {
          if (playerData.getJSONObject(i).has("name") == true) {
            String id = playerData.getJSONObject(i).getJSONObject("name").getString("first");
            PlayerInfo player = GitGud.getPlayerStats(id, killBanList, killBanList, allowVehicles, enemyAllowVehicles, sampleSize);
            if (player != null) {

              avgBR += player.getBR();
              avgTKD += player.getTKD();
              avgRKD += player.getRKD();
              avgAcc += player.getAcc();
              avgHSR += player.getHSR();
              avgIVI += player.getIVI();

              String output = player.getPlayerName() + "," +
                  String.format("%d", player.getBR()).toString() + "," +
                  String.format("%.3f", player.getRKD()).toString() + "," +
                  String.format("%.3f", player.getTKD()).toString() + "," +
                  String.format("%.2f%%", player.getAcc() * 100).toString() + "," +
                  String.format("%.2f%%", player.getHSR() * 100).toString() + "," +
                  String.format("%.2f", player.getIVI()).toString();
              storedOutfitData.add(output);

              HistoryString.setText(HistoryString.getText() + player.getPlayerName() + "\n");
              HistoryStringBR.setText(HistoryStringBR.getText() + String.format("%d", player.getBR()).toString() + "\n");
              HistoryStringRKD.setText(HistoryStringRKD.getText() + String.format("%.3f", player.getRKD()).toString() + "\n");
              HistoryStringTKD.setText(HistoryStringTKD.getText() + String.format("%.3f", player.getTKD()).toString() + "\n");
              HistoryStringAcc.setText(HistoryStringAcc.getText() + String.format("%.2f%%", player.getAcc() * 100).toString() + "\n");
              HistoryStringHSR.setText(HistoryStringHSR.getText() + String.format("%.2f%%", player.getHSR() * 100).toString() + "\n");
              HistoryStringIVI.setText(HistoryStringIVI.getText() + String.format("%.2f", player.getIVI()).toString() + "\n");
              historySize++;
              historyGridBack.setPrefHeight(historyGridBack.getPrefHeight() + 16);
              numberPassed++;
            }
          }
          updateProgress(i, numberMembers);
        }
        avgBR /= numberPassed;
        avgRKD /= numberPassed;
        avgTKD /= numberPassed;
        avgAcc /= numberPassed;
        avgHSR /= numberPassed;
        avgIVI /= numberPassed;


        FXPlayerName.setText("[" + outfitName + "]");
        FXBR.setText(String.format("%.0f", avgBR));
        FXRKD.setText(String.format("%.3f", avgRKD));
        FXTKD.setText(String.format("%.3f", avgTKD));
        FXAcc.setText(String.format("%.2f%%", avgAcc * 100));
        FXHSR.setText(String.format("%.2f%%", avgHSR * 100));
        FXIVI.setText(String.format("%.2f", avgIVI));
        updateProgress(0, numberMembers);
        gitGudButton.setDisable(false);
        return null;
      }

    };

    if (queryType.getSelectedToggle() == FXQueryPlayer) {
      FXPlayerName.setText("Loading Player Data...");
      progressBarParse.progressProperty().bind(task.progressProperty());
      new Thread(task).start();
    } else {
      FXPlayerName.setText("Loading Outfit Data...");
      progressBarParse.progressProperty().bind(taskOutfit.progressProperty());
      new Thread(taskOutfit).start();
    }
  }
}