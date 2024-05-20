package com.ads.everywhere.ui.permissions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ads.everywhere.R
import com.ads.everywhere.data.models.Permission


class PermissionsAdapter(private val viewModel: PermissionViewModel) : RecyclerView.Adapter<PermissionsAdapter.PermissionVH>() {
    private val items = ArrayList<Permission>()
    fun updateList(newList: List<Permission>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class PermissionVH(private val root: View) : ViewHolder(root) {
        fun bind(item: Permission) {
            val res = root.context.resources
            val enabled = viewModel.isEnabled(item)
            root.findViewById<TextView>(R.id.text).text = res.getString(item.titleRes)
            root.findViewById<View>(R.id.allow).setOnClickListener { viewModel.allowClicked(item) }
            root.findViewById<CheckBox>(R.id.checkbox).visibility =
                if (enabled) View.VISIBLE else View.GONE
            root.findViewById<View>(R.id.allow).visibility =
                if (enabled) View.GONE else View.VISIBLE

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionVH {
        val inflater = LayoutInflater.from(parent.context)
        return PermissionVH(inflater.inflate(R.layout.item_permission, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PermissionVH, position: Int) =
        holder.bind(items[position])
}