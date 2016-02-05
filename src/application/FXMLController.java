package application;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;


public class FXMLController {
  String playerName = "";
  ArrayList<Integer> killBanList = GitGud.loadBanList("KILLFILTER", "files\\default.txt");
  ArrayList<Integer> enemyKillBanList = GitGud.loadBanList("ENEMYKILLFILTER", "files\\default.txt");
  ArrayList<Integer> allowVehicles = GitGud.loadBanList("FRIEDLYVEHICLESALLOWED", "files\\default.txt");
  ArrayList<Integer> enemyAllowVehicles = GitGud.loadBanList("ENEMYVEHICLESALLOWED", "files\\default.txt");
  @FXML private Text FXPlayerName, FXBR, FXRKD, FXTKD, FXAcc, FXHSR, FXIVI, FXWeapon1Name, FXWeapon2Name, FXWeapon3Name,
                     FXOutputString, FXKillFilter, FXEnemyKillFilter, FXVehicles, FXEnemyVehicles, FXLoadedFilter;
  @FXML private TextField NameEnterField, SampleEnterField, FXFilterName;
  @FXML private ImageView FXWeapon1, FXWeapon2, FXWeapon3;
  @FXML protected void EnterPlayerName(ActionEvent event) {
    
  }
  @FXML protected void FXFilterLoad(ActionEvent event) {
    String fileName = FXFilterName.getText();
    killBanList = GitGud.loadBanList("KILLFILTER", "files\\"+ fileName +".txt");
    enemyKillBanList = GitGud.loadBanList("ENEMYKILLFILTER", "files\\"+ fileName +".txt");
    allowVehicles = GitGud.loadBanList("FRIEDLYVEHICLESALLOWED", "files\\"+ fileName +".txt");
    enemyAllowVehicles = GitGud.loadBanList("ENEMYVEHICLESALLOWED", "files\\"+ fileName +".txt");
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
  }
  @FXML protected void CalculateButton(ActionEvent event) throws IOException, InterruptedException {
    playerName = NameEnterField.getText();
    int sampleSize = Integer.parseInt(SampleEnterField.getText());
    PlayerInfo data = GitGud.getPlayerStats(playerName, killBanList, killBanList, allowVehicles, enemyAllowVehicles, sampleSize);
    if (data == null) {
      FXPlayerName.setText("ERROR: Check Spelling");
    } else {
      FXPlayerName.setText(data.getPlayerName());
      FXBR.setText(String.format("%d", data.getBR()).toString());
      FXRKD.setText(String.format("%.3f", data.getRKD()).toString());
      FXTKD.setText(String.format("%.3f", data.getTKD()).toString());
      FXAcc.setText(String.format("%.2f%%", data.getAcc() * 100).toString());
      FXHSR.setText(String.format("%.2f%%", data.getHSR() * 100).toString());
      FXIVI.setText(String.format("%.2f", data.getIVI()).toString());
      FXOutputString.setText(data.getDetailedOutput());
      
      ImageView[] imagePositionArray = {FXWeapon1,FXWeapon2,FXWeapon3};
      Text[] imageNameArray = {FXWeapon1Name,FXWeapon2Name,FXWeapon3Name};
      
      ArrayList<Integer> weaponIds = data.getWeapons();
      ArrayList<String> weaponNames = data.getWeaponString();
      ArrayList<Integer> weaponImages = new ArrayList<Integer>();
      for (int i = 0; i < 3; i ++) {
        imagePositionArray[i].setImage(null);
        imageNameArray[i].setOpacity(0);
      }
      for (int i = weaponIds.size() - 1; i >= 0 ; i--) {
        int weaponId = new JSONObject(GitGud.censusFetch("https://census.daybreakgames.com/get/ps2/item?item_id=" + weaponIds.get(i))).getJSONArray("item_list").getJSONObject(0).getInt("image_id");
        Image weapon = new Image("https://census.daybreakgames.com/files/ps2/images/static/" + weaponId + ".png");
        imageNameArray[i].setText(weaponNames.get(i));
        imagePositionArray[i].setImage((Image) weapon);
        imageNameArray[i].setOpacity(1);
      }
    }
  }
}
