package ch.makery.address.model

import scalafx.beans.property.{BooleanProperty, ObjectProperty, StringProperty}

import java.time.LocalDate
import ch.makery.address.util.Database
import ch.makery.address.util.DateUtil._
import scalikejdbc._
import scala.util.{Failure, Success, Try}

class Task(val title: String) extends Database {
  def this() = this(null)

  var tasktitle = new StringProperty(title)
  var description = new StringProperty()
  var participants = new StringProperty()
  var status = new BooleanProperty
  var date = ObjectProperty[LocalDate](LocalDate.of(2023,12,21))


  def save(): Try[Int] = {
    if (!isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
          insert into task (tasktitle, description,
            participants, status, date) values
            (${tasktitle.value}, ${description.value}, ${participants.value},
              ${status.value}, ${date.value.asString})
        """.update.apply()
      })
    } else {
      Try(DB autoCommit { implicit session =>
        sql"""
        update task
        set
        tasktitle  = ${tasktitle.value} ,
        dsecription   = ${description.value},
        participants     = ${participants.value},
        status = ${status.value},
        date       = ${date.value.asString}
         where tasktitle = ${tasktitle.value}
        """.update.apply()
      })
    }

  }

  def delete(): Try[Int] = {
    if (isExist) {
      Try(DB autoCommit { implicit session =>
        sql"""
        delete from task where
          tasktitle = ${tasktitle.value}
        """.update.apply()
      })
    } else
      throw new Exception("Task does not exist in Database")
  }

  def deleteAllDataFromTaskTable(): Int = {
    (DB autoCommit { implicit session =>
        sql"""
           delete from task
           """.update().apply()
  })}

  private def isExist: Boolean = {
    DB readOnly { implicit session =>
      sql"""
        select * from task where
        tasktitle = ${tasktitle.value}
      """.map(rs => rs.string("tasktitle")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }

  }
}

object Task extends Database {
  def apply(
             tasktitleS: String,
             descriptionS: String,
             participantS: String,
             statuS: Boolean,
             dateS: String
           ): Task = {

    new Task(tasktitleS) {
      description.value = descriptionS
      participants.value = participantS
      status.value = statuS
      date.value = dateS.parseLocalDate
    }

  }

  def initializeTable() = {
    DB autoCommit { implicit session =>
      sql"""
      create table task (
        id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
        tasktitle varchar(64),
        description varchar(500),
        participants varchar(100),
        status varchar(20),
        date varchar(64)
      )
      """.execute.apply()
    }
  }

  def getAllTask: List[Task] = {
    DB readOnly { implicit session =>
      sql"select * from task".map(rs => Task(rs.string("tasktitle"),
        rs.string("description"), rs.string("participants"),
        rs.boolean("status"), rs.string("date"))).list.apply()
    }
  }
}
