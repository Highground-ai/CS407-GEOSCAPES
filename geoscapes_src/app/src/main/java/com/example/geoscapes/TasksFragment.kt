package com.example.geoscapes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoscapes.databinding.FragmentTasksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private var job : Job? = null

    private lateinit var incompleteRecyclerView: RecyclerView
    private lateinit var completedRecyclerView: RecyclerView
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var taskDB: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskDB = TaskDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater,container,false)

        completedRecyclerView = _binding!!.completedRecyclerView
        incompleteRecyclerView = _binding!!.incompleteRecyclerView

        // Create adapter instances early
        val incompleteTasksAdapter = TasksAdapter(emptyList(), taskDB) // Initialize with empty list
        val completedTasksAdapter = TasksAdapter(emptyList(), taskDB) // Initialize with empty list

        incompleteRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = incompleteTasksAdapter
        }

        completedRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = completedTasksAdapter
        }


        job = CoroutineScope(Dispatchers.IO).launch {
            val incompleteTasks = taskDB.taskDao().getIncompleteTasks()
            val completedTasks = taskDB.taskDao().getCompletedTasks()

            withContext(Dispatchers.Main) {
                incompleteTasksAdapter.submitList(incompleteTasks) // Update data and notify
                completedTasksAdapter.submitList(completedTasks) // Update data and notify
            }
        }

        return binding.root
    }
}