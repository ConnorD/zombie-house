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

  public void start(Stage stage) throws Exception
  {
    this.stage = stage;

    Button startButton = new Button("Start");
    startButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        stage.hide();
      }
    });

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
      stage.hide();
    }
  }
}

