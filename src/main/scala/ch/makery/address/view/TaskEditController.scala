package ch.makery.address.view

import ch.makery.address.model.Task
import ch.makery.address.util.DateUtil.DateFormater
import scalafx.scene.control.{Alert, DatePicker, TextArea, TextField}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.event.ActionEvent

@sfxml
class TaskEditController (
                                   private val  tasktitle : TextField,
                                   private val  description : TextArea,
                                   private val  participants : TextField,
                                   private val  date : DatePicker
                                 ){
  var         dialogStage : Stage  = null
  var _task     : Task = null
  var         okClicked            = false

  def task = _task
  def task_=(x : Task) {
    _task = x

    tasktitle.text = _task.tasktitle.value
    description.text  = _task.description.value
    participants.text    = _task.participants.value
    date.value = _task.date.value
    date.setPromptText("dd/mm/yyyy");
  }

  def handleOk(action :ActionEvent){

    if (isInputValid()) {
      _task.tasktitle <== tasktitle.text
      _task.description  <== description.text
      _task.participants    <== participants.text
      _task.date.value  = date.getValue

      okClicked = true;
      dialogStage.close()
    }
  }

  def handleCancel(action :ActionEvent) {
    dialogStage.close();
  }
  def nullChecking (x : String) = x == null || x.length == 0

  def isInputValid() : Boolean = {
    var errorMessage = ""

    if (nullChecking(tasktitle.text.value))
      errorMessage += "No valid title!\n"
    if (nullChecking(description.text.value))
      description.text = " - "
    if (nullChecking(participants.text.value))
      participants.text = " - "
    else {

    }
    if (nullChecking(date.getValue.asString))
      errorMessage += "No valid date!\n"


    if (errorMessage.length() == 0) {
      return true;
    } else {
      // Show the error message.
      val alert = new Alert(Alert.AlertType.Error){
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()

      return false;
    }
  }
}
