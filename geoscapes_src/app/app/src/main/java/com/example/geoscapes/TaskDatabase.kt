package com.example.geoscapes

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.Upsert
import com.google.android.gms.maps.model.LatLng

@Entity
data class Task (
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val taskName: String = "", // Name of the task
    val taskDescription: String?, // Optional description of the task
    val taskCompletion: Float, // Percentage of steps completed
    val location: LatLng, // The location of the task - could move to task if needed
    val radius: Int = 100, // Radius user needs to be to start task
    val storyline: String?, // Optional story of the task
)
@Entity(
    primaryKeys = ["taskId", "stepId"],
    foreignKeys = [ForeignKey(
        entity = Task::class,
        parentColumns = ["taskId"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Step::class,
        parentColumns = ["stepId"],
        childColumns = ["stepId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskStepRelation(
    val taskId: Int,
    val stepId: Int
)

@Entity
data class Step (
    @PrimaryKey(autoGenerate = true) val stepId: Int = 0,
    val stepName: String, // Name of the step
    val stepDescription: String?, // Optional description of the step
    val stepCompletion: Boolean, // Whether the step has been completed
    val activityId: String?, // Optional activityId for the step
)

class Converters {
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }
    @TypeConverter
    fun toLatLng(latLngString: String): LatLng {
        val latLngArray = latLngString.split(",")
        return LatLng(latLngArray[0].toDouble(), latLngArray[1].toDouble())
    }
}

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsert(task: Task)

    @Query("SELECT * FROM Task")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM Task WHERE taskName = :taskName")
    suspend fun getTaskByName(taskName: String): Task?

    @Query("SELECT * FROM Task WHERE taskCompletion = 100")
    suspend fun getCompletedTasks(): List<Task>

    @Query("SELECT * FROM Task WHERE taskCompletion < 100")
    suspend fun getIncompleteTasks(): List<Task>

    @Query("""SELECT * From Task, Step, TaskStepRelation
                WHERE Task.taskId = :id
                  AND TaskStepRelation.taskId = Task.taskId
                  AND Step.stepId = TaskStepRelation.stepId"""
    )
    suspend fun getStepsFromTask(id: Int): List<Step>
}

@Dao
interface StepDao {
    @Query("SELECT * FROM step WHERE stepId = :id")
    suspend fun getById(id: Int): Step

    @Query("SELECT stepId FROM step WHERE rowid = :rowId")
    suspend fun getByRowId(rowId: Long): Int

    @Upsert(entity = Step::class)
    suspend fun upsert(step: Step): Long

    @Insert
    suspend fun insertRelation(stepAndTask: TaskStepRelation)

    @Transaction
    suspend fun upsertStep(step: Step, taskId: Int): Int {
        val rowId = upsert(step)
        if (step.stepId == 0) {
            val stepId = getByRowId(rowId)
            insertRelation(TaskStepRelation(taskId, stepId))
            return stepId
        }
        return step.stepId
    }

    @Query(
        """SELECT COUNT(*) FROM Task, Step, TaskStepRelation
            WHERE Task.taskId = :taskId
                AND TaskStepRelation.taskId = Task.taskId
                AND Step.stepId = TaskStepRelation.stepId"""
    )
    suspend fun taskStepCount(taskId: Int): Int
}

@Dao
interface DeleteDao {
    @Query("DELETE FROM task WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query(
        """SELECT Step.stepId FROM Task, Step, TaskStepRelation
                WHERE Task.taskId = :taskId
                    AND TaskStepRelation.taskId = Task.taskId
                    AND Step.stepId = TaskStepRelation.stepId"""
    )
    suspend fun getAllStepIdsByTask(taskId: Int): List<Int>

    @Query("DELETE FROM step WHERE stepId IN (:stepsIds)")
    suspend fun deleteSteps(stepsIds: List<Int>)

    // Deletes a task and all of its steps
    @Transaction
    suspend fun delete(taskId: Int) {
        deleteSteps(getAllStepIdsByTask(taskId))
        deleteTask(taskId)
    }
}

@Database(entities = [Task::class, Step::class, TaskStepRelation::class], version = 5)

@TypeConverters(Converters::class)
abstract  class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun stepDao(): StepDao
    abstract fun deleteDao(): DeleteDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    context.getString(R.string.task_database),
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}