/**
 * @author Connor Denman
 */
package gamePackage.mainPackage;

import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class GamePreloader extends Preloader
{
  ProgressBar bar;
  Stage stage;

//  private Scene createPreloaderScene()
//  {
////    bar = new ProgressBar();
//    BorderPane p = new BorderPane();
//    p.setCenter(bar);
//    return new Scene(p, 300, 150);
//  }

  public void start(Stage stage) throws Exception
  {
    this.stage = stage;

    Button startButton = new Button("Start");
    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        stage.hide();
      }
    });
//
//    Button settingsButton = new Button("Settings");
//    Button quitButton = new Button("Quit");
//    bar = new ProgressBar();
    BorderPane pane = new BorderPane();
    pane.setCenter(startButton);
    Scene preloaderScene = new Scene(pane, 400, 300);
    stage.setScene(preloaderScene);
    stage.setTitle("Zombie House");
    stage.show();
  }

//  @Override
//  public void handleProgressNotification(ProgressNotification pn)
//  {
////    bar.setProgress(pn.getProgress());
//  }

  @Override
  public void handleStateChangeNotification(StateChangeNotification evt)
  {
    if (evt.getType() == StateChangeNotification.Type.BEFORE_START)
    {
//      stage.toFront();
      stage.hide();
    }
  }
}

