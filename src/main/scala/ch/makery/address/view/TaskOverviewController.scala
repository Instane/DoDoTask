package ch.makery.address.view

import ch.makery.address.model.Task
import ch.makery.address.MainApp
import scalafx.scene.control.{Alert, ButtonType, CheckBox, Label, TableColumn, TableView, TextArea}
import scalafxml.core.macros.sfxml
import ch.makery.address.util.DateUtil._

import scala.util.{Failure, Success}
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType

@sfxml
class TaskOverviewController(
                                private val taskTable : TableView[Task],
                                private val titleColumn : TableColumn[Task, String],
                                private val dateColumn : TableColumn[Task, String],
                                private val statusCheck : CheckBox,
                                private val titleLabel : Label,
                                private val descriptionLabel : TextArea,
                                private val participantsLabel : Label,
                                private val dateLabel :  Label,

                              ) {
  taskTable.items = MainApp.taskData
  titleColumn.cellValueFactory = {_.value.tasktitle}
  dateColumn.cellValueFactory = {_.value.date.asString()}

  showTaskDetails(None);

  taskTable.selectionModel().selectedItem.onChange(
    (_, _, newValue) => showTaskDetails(Some(newValue))
  )

  private def showTaskDetails (task : Option[Task]) = {
    task match {
      case Some(task) =>
        titleLabel.text <== task.tasktitle
        descriptionLabel.text  <== task.description
        participantsLabel.text   <== task.participants
        if (task.status == true) {
          statusCheck.selected
        }
        else {
          !statusCheck.selected
        }
        dateLabel.text   = task.date.value.asString
      case None =>
        titleLabel.text = " "
        descriptionLabel.text  = " "
        participantsLabel.text    = " "
        dateLabel.text  = " "
    }}

  def onCheck(action: ActionEvent): Unit = {
    val selectedTask = taskTable.selectionModel().selectedItem.value
    if (selectedTask == null) {
      statusCheck.isSelected == false
    } else {
      if (statusCheck.isSelected) {
        selectedTask.status.value = true
      } else {
        selectedTask.status.value = false
      }
    }
    }

  def handleNewTask(action : ActionEvent) = {
    val task = new Task()
    val okClicked = MainApp.showTaskEditDialog(task);
    if (okClicked) {
      task.save() match {
        case Success(x) =>
            MainApp.taskData += task
        case Failure(e) =>
          val alert = new Alert(Alert.AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failed to Save"
            headerText = "Database Error"
            contentText = "Database problem filed to save changes"
          }.showAndWait()
      }
    }
  }

  def handleEditTask(action : ActionEvent) = {
    val selectedTask = taskTable.selectionModel().selectedItem.value
    if (selectedTask != null) {
      val okClicked = MainApp.showTaskEditDialog(selectedTask)
      if (okClicked) {
        selectedTask.save() match {
          case Success(x) =>
            showTaskDetails(Some(selectedTask))
          case Failure(e) =>
            val alert = new Alert(Alert.AlertType.Warning) {
              initOwner(MainApp.stage)
              title = "Failed to Save"
              headerText = "Database Error"
              contentText = "Database problem filed to save changes"
            }.showAndWait()
        }
      }

    } else {
      // Nothing selected.
      val alert = new Alert(Alert.AlertType.Warning){
        initOwner(MainApp.stage)
        title       = "No Selection"
        headerText  = "No Task Selected"
        contentText = "Please select a task in the table."
      }.showAndWait()
    }
  }
  def handleDeleteTask(action : ActionEvent) = {
    val selectedIndex = taskTable.selectionModel().selectedIndex.value
    val selectedtask = taskTable.selectionModel().selectedItem.value
    if (selectedIndex >= 0) {
      val alert = new Alert(AlertType.Confirmation) {
        initOwner(MainApp.stage)
        title = "Delete Task"
        headerText = "Would like to delete this task?"
        contentText = "Task details will be lost."
      }
      val result = alert.showAndWait()

      result match {
        case Some(ButtonType.OK) => println("Task is deleted")
      selectedtask.delete() match {
        case Success(x) =>
          taskTable.items().remove(selectedIndex);
        case Failure(e) =>
          val alert = new Alert(Alert.AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failed to Save"
            headerText = "Database Error"
            contentText = "Database problem filed to save changes"
          }.showAndWait()
      }
        case _ => println("Tasks not removed")
      }
    } else {
      // Nothing selected.
      val alert = new Alert(AlertType.Warning){
        initOwner(MainApp.stage)
        title       = "No Selection"
        headerText  = "No Task Selected"
        contentText = "Please select a task in the table."
      }.showAndWait()
    }
  }

  def deleteAllTasks(action: ActionEvent) = {
    val task = new Task()
    val alert = new Alert(Alert.AlertType.Confirmation) {
      initOwner(MainApp.stage)
      title = "Delete All?"
      headerText = "Are you sure you would like to delete all tasks?"
      contentText = "Once deleted, all data is lost"
    }
    val result = alert.showAndWait()

    result match {
      case Some(ButtonType.OK) => println("All tasks has been deleted")
        task.deleteAllDataFromTaskTable()
        MainApp.taskData.clear()
      case _                   => println("Tasks not removed")
    }
  }
}
