package com.aquaspoof.unified.toolkit.mcpe

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aquaspoof.unified.toolkit.mcpe.databinding.ScriptListItemBinding
import com.google.gson.Gson

class ConfigAdapter(private var configs: List<ConfigItem>) :
    RecyclerView.Adapter<ConfigAdapter.ConfigViewHolder>() {

    class ConfigViewHolder(val binding: ScriptListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigViewHolder {
        val binding = ScriptListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConfigViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConfigViewHolder, position: Int) {
        val config = configs[position]
        holder.binding.tvScriptName.text = config.name
        holder.binding.tvScriptDescription.text = config.description

        val context = holder.itemView.context

        val openDetails = {
            val intent = Intent(context, ConfigDetailActivity::class.java)
            val gson = Gson()
            val jsonString = gson.toJson(config)
            intent.putExtra("config_json_data", jsonString)

            context.startActivity(intent)
        }

        holder.binding.buttonDetails.setOnClickListener { openDetails() }
        holder.itemView.setOnClickListener { openDetails() }
    }

    override fun getItemCount() = configs.size

    fun updateData(newConfigs: List<ConfigItem>) {
        configs = newConfigs
        notifyDataSetChanged()
    }
}