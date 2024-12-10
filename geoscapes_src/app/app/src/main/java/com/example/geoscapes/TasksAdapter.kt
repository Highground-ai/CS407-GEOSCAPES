package com.example.geoscapes

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Adapter for the tasks RecyclerView.
 * Takes in a list of TaskItem and a callback for when the switch is toggled.
 */
class TasksAdapter(
    private var taskList: List<Task>,
    private val taskDB: TaskDatabase,
    private val currentTask: SharedPreferences
) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {
    private var activeTaskPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksAdapter.TasksViewHolder, position: Int) {
        val taskItem = taskList[position]
        holder.itemView.isClickable = true
        holder.itemView.isLongClickable = true
        // Switches task to active/inactive if the task is not completed
        holder.itemView.setOnLongClickListener {
            if (taskItem.taskCompletion != 100f) {
                if (currentTask.getInt("taskID", -1) == taskItem.taskId) {
                    currentTask.edit().remove("taskID").apply()
                } else {
                    currentTask.edit().putInt("taskID", taskItem.taskId).apply()
                }
                notifyDataSetChanged() // Refresh the RecyclerView
            }
            true
        }
        holder.bind(taskItem, taskDB, currentTask.getInt("taskID", -1) == taskItem.taskId)
    }


    override fun getItemCount(): Int = taskList.size

    fun submitList(newTasks: List<Task>) {
        taskList = newTasks
        notifyItemRangeInserted(0, newTasks.size) // Or use more specific notify methods for better performance
    }

    inner class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.taskTextView)
        private val dropdownArrow: ImageView = itemView.findViewById(R.id.dropdownArrow)
        private val stepsRecyclerView: RecyclerView = itemView.findViewById(R.id.stepsRecyclerView)
        private val activeBorder: View = itemView.findViewById(R.id.activeBorder)
        private val taskColor: View = itemView.findViewById(R.id.taskColor)
        private lateinit var stepsAdapter: StepsAdapter

        private var job : Job? = null

        fun bind(taskItem: Task, taskDB: TaskDatabase, isActive: Boolean) {

            titleTextView.text = taskItem.taskName
            if (taskItem.taskCompletion == 100f) {
                taskColor.setBackgroundResource(R.color.complete)
            } else if (taskItem.taskCompletion > 0f) {
                taskColor.setBackgroundResource(R.color.inProgress)
            } else {
                taskColor.setBackgroundResource(R.color.incomplete)
            }

            if (isActive) {
                activeBorder.visibility = View.VISIBLE
            } else {
                activeBorder.visibility = View.INVISIBLE
            }

            stepsAdapter = StepsAdapter(emptyList())
            stepsRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = stepsAdapter
            }

            job = CoroutineScope(Dispatchers.IO).launch {
                val steps = taskDB.taskDao().getStepsFromTask(taskItem.taskId)

                withContext(Dispatchers.Main) {
                    stepsAdapter.submitList(steps)
                }
            }

            itemView.setOnClickListener {
                if (stepsRecyclerView.visibility == View.VISIBLE) {
                    stepsRecyclerView.visibility = View.GONE
                    dropdownArrow.rotation = -90f
                } else {
                    stepsRecyclerView.visibility = View.VISIBLE
                    dropdownArrow.rotation = 0f
                }
            }


        }
    }
}
