package ch.makery.address

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import ch.makery.address.model.Task
import ch.makery.address.util.Database
import ch.makery.address.view.TaskEditController
import scalafx.stage.{Modality, Stage}

object MainApp extends JFXApp {

  Database.setupDB()
  val taskData = new ObservableBuffer[Task]()

  taskData ++= Task.getAllTask
  val rootResource = getClass.getResourceAsStream("view/RootLayout.fxml")
  val loader = new FXMLLoader(null, NoDependencyResolver)
  loader.load(rootResource)
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  stage = new PrimaryStage {
    title = "DoDoTask"
    scene = new Scene {
      root = roots
    }
  }

  def showTaskOverview() = {
    val resource = getClass.getResourceAsStream("view/TaskOverview.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showTaskEditDialog(task: Task): Boolean = {
    val resource = getClass.getResourceAsStream("view/TaskEditDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[TaskEditController#Controller]
    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      title = "DoDoTask"
      scene = new Scene {
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.task = task
    dialog.showAndWait()
    control.okClicked
  }

  showTaskOverview()
}
