package com.aquaspoof.unified.toolkit.mcpe

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aquaspoof.unified.toolkit.mcpe.databinding.ScriptListItemBinding
import com.google.gson.Gson // Обязательно этот импорт

class ScriptAdapter(private var scripts: List<ScriptItem>) :
    RecyclerView.Adapter<ScriptAdapter.ScriptViewHolder>() {

    class ScriptViewHolder(val binding: ScriptListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScriptViewHolder {
        val binding = ScriptListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScriptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScriptViewHolder, position: Int) {
        val script = scripts[position]
        holder.binding.tvScriptName.text = script.name
        holder.binding.tvScriptDescription.text = script.description

        val context = holder.itemView.context

        val openDetails = {
            val intent = Intent(context, ScriptDetailActivity::class.java)
            val gson = Gson()
            val jsonString = gson.toJson(script)
            intent.putExtra("script_json_data", jsonString)

            context.startActivity(intent)
        }

        holder.binding.buttonDetails.setOnClickListener {
            openDetails()
        }

        holder.itemView.setOnClickListener {
            openDetails()
        }
    }

    override fun getItemCount() = scripts.size

    fun updateData(newScripts: List<ScriptItem>) {
        scripts = newScripts
        notifyDataSetChanged()
    }
}