package com.cs407.cs407_geoscapes

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
    private val taskList: List<Task>,
    private val taskDB: TaskDatabase,
) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksAdapter.TasksViewHolder, position: Int) {
        val taskItem = taskList[position]
        holder.bind(taskItem, taskDB)
    }


    override fun getItemCount(): Int = taskList.size

    inner class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.taskTextView)
        private val dropdownArrow: ImageView = itemView.findViewById(R.id.dropdownArrow)
        private val stepsRecyclerView: RecyclerView = itemView.findViewById(R.id.stepsRecyclerView)
        private val taskColor: View = itemView.findViewById(R.id.taskColor)
        private lateinit var stepsAdapter: StepsAdapter

        private var job : Job? = null

        fun bind(taskItem: Task, taskDB: TaskDatabase) {
            titleTextView.text = taskItem.taskName
            if (taskItem.taskCompletion == 100f) {
                taskColor.setBackgroundResource(R.color.complete)
            } else if (taskItem.taskCompletion > 0f) {
                taskColor.setBackgroundResource(R.color.inProgress)
            } else {
                taskColor.setBackgroundResource(R.color.incomplete)
            }

            job = CoroutineScope(Dispatchers.IO).launch {
                val steps = taskDB.taskDao().getStepsFromTask(taskItem.taskId)

                withContext(Dispatchers.Main) {
                    stepsAdapter = StepsAdapter(steps)
                    stepsRecyclerView.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = stepsAdapter
                    }
                }
            }

            titleTextView.setOnClickListener {
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
