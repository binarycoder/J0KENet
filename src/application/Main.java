package application;

import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
		  primaryStage.setTitle("J0KE.gg Stat Calculator");
      SplitPane myPane = (SplitPane) FXMLLoader.load(getClass().getClassLoader().getResource
   ("jokegg.fxml"));
      Scene myScene = new Scene(myPane);
      primaryStage.setScene(myScene);
      primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

  public static void main(String[] args) {
    System.out.printf("Current working directory: %s\n", Paths.get(".").toAbsolutePath().normalize().toString());

    launch(args);
  }
}