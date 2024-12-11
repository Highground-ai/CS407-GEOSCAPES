package com.example.geoscapes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for the steps RecyclerView.
 * Takes in a list of StepItem and a callback for when the switch is toggled.
 */
class StepsAdapter(
    private var stepList: List<Step>
) : RecyclerView.Adapter<StepsAdapter.StepsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_step, parent, false)
        return StepsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepsAdapter.StepsViewHolder, position: Int) {
        val stepItem = stepList[position]
        holder.bind(stepItem)
    }


    override fun getItemCount(): Int = stepList.size

    fun submitList(newSteps: List<Step>) {
        stepList = newSteps
        notifyItemRangeInserted(0, newSteps.size)
    }

    inner class StepsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.stepTextView)
        private val stepColor: View = itemView.findViewById(R.id.stepColor)

        fun bind(stepItem: Step) {
            titleTextView.text = stepItem.stepName
            Log.d("TasksAdapter", "Binding task: ${stepItem.stepName}")
            if (stepItem.stepCompletion) {
                stepColor.setBackgroundResource(R.color.complete)
            } else {
                stepColor.setBackgroundResource(R.color.incomplete)
            }
        }
    }
}
