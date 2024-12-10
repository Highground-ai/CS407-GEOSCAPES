package com.example.geoscapes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.geoscapes.databinding.FragmentLandingPageBinding
import com.example.geoscapes.databinding.FragmentSettingsBinding
import com.example.geoscapes.databinding.FragmentTasksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandingPage : Fragment() {
    private var _binding: FragmentLandingPageBinding? = null
    private val binding get() = _binding!!
    private var job : Job? = null

    private lateinit var goButton: Button
    private lateinit var setTaskTextView: TextView
    private lateinit var taskTextView: TextView
    private lateinit var currentTaskTextView: TextView
    private lateinit var storyTextView: TextView

    private lateinit var currentTask: SharedPreferences // Used to display location of active
    private lateinit var taskDB: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskDB = TaskDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLandingPageBinding.inflate(inflater,container,false)

        currentTask = activity?.getSharedPreferences(
            getString(R.string.currentTaskKey), Context.MODE_PRIVATE)!! // in taskId, if -1, then no active task

        goButton = _binding!!.goButton
        setTaskTextView = _binding!!.setTaskTextView
        taskTextView = _binding!!.taskTextView
        currentTaskTextView = _binding!!.currentTaskTextView
        storyTextView = _binding!!.storyTextView

        goButton.setOnClickListener {
            if (currentTask.getInt("taskID", -1) != -1) {
                findNavController().navigate(R.id.action_landingPageFragment_to_mapsFragment)
            } else {
                findNavController().navigate(R.id.action_landingPageFragment_to_tasksFragment)
            }

        }
        // TODO: Inflate storyline text

        if (currentTask.getInt("taskID", -1) != -1) {
            // Get current task and display it
            job = CoroutineScope(Dispatchers.IO).launch {
                val task = taskDB.taskDao().getTaskById(currentTask.getInt("taskID", -1))
                val tasks = taskDB.taskDao().getAllTasks()
                withContext(Dispatchers.Main) {
                    setTaskTextView.text = "Go to Task"
                    currentTaskTextView.text = task?.taskName
                    if (task?.taskDescription == null) {
                        taskTextView.text = "No Task Description"
                    } else {
                        taskTextView.text = task.taskDescription
                    }
                    for (t in tasks) {
                        if (t.taskCompletion > 0f) {
                            storyTextView.append(t.storyline + "\n\n")
                        }
                    }
                }
            }
        } else {
            // If no task, display set task
            setTaskTextView.text = "Set Task"
            currentTaskTextView.text = "No Active Task:"
            taskTextView.text = "Go to Tasks to set a task"
        }



        return binding.root
    }

}