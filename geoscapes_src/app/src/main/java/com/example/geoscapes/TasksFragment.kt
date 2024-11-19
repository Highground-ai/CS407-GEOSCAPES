package com.cs407.cs407_geoscapes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.geoscapes.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TasksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TasksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var job : Job? = null

    private lateinit var incompleteRecyclerView: RecyclerView
    private lateinit var completedRecyclerView: RecyclerView
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var taskDB: TaskDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        taskDB = TaskDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        completedRecyclerView = view.findViewById(R.id.completedRecyclerView)
        incompleteRecyclerView = view.findViewById(R.id.incompleteRecyclerView)

        job = CoroutineScope(Dispatchers.IO).launch {
            val incompleteTasks = taskDB.taskDao().getIncompleteTasks()
            val completedTasks = taskDB.taskDao().getCompletedTasks()

            withContext(Dispatchers.Main) {
                tasksAdapter = TasksAdapter(incompleteTasks, taskDB)
                incompleteRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = tasksAdapter
                }

                tasksAdapter = TasksAdapter(completedTasks, taskDB)
                completedRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = tasksAdapter
                }
            }

        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}