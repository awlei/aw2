package com.v2ray.ang.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ItemServerSimpleBinding
import com.v2ray.ang.dto.ProfileItem
import com.v2ray.ang.dto.ServersCache
import com.v2ray.ang.handler.MmkvManager

/**
 * 简化版服务器列表适配器
 * 设计原则：
 * 1. 只显示必要信息
 * 2. 清晰的选中状态
 * 3. 简单的交互
 */
class ServerSimpleAdapter(
    private val onServerClick: (ServersCache) -> Unit
) : RecyclerView.Adapter<ServerSimpleAdapter.ViewHolder>() {

    private var servers: List<ServersCache> = emptyList()
    private var selectedServerId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServerSimpleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val server = servers[position]
        holder.bind(server, server.guid == selectedServerId)
    }

    override fun getItemCount(): Int = servers.size

    fun updateServers(newServers: List<ServersCache>) {
        servers = newServers
        selectedServerId = MmkvManager.getSelectServer()
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemServerSimpleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(server: ServersCache, isSelected: Boolean) {
            val profile = server.profile
            val context = binding.root.context

            // 服务器名称
            binding.tvName.text = profile.remarks ?: "未知服务器"

            // 协议类型
            binding.tvType.text = profile.configType.toString()

            // 测试结果
            val aff = MmkvManager.decodeServerAffiliationInfo(server.guid)
            val testResult = aff?.getTestDelayString() ?: context.getString(R.string.simple_not_tested)
            binding.tvTestResult.text = testResult

            // 根据测试结果设置颜色
            if (aff?.testDelayMillis ?: 0L < 0L) {
                binding.tvTestResult.setTextColor(ContextCompat.getColor(context, R.color.colorPingRed))
            } else {
                binding.tvTestResult.setTextColor(ContextCompat.getColor(context, R.color.colorPing))
            }

            // 选中状态
            if (isSelected) {
                binding.selectedIndicator.visibility = View.VISIBLE
                binding.cardServer.strokeWidth = 2
                binding.cardServer.strokeColor = ContextCompat.getColor(context, R.color.color_fab_active)
                binding.cardServer.cardBackgroundColor = ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
            } else {
                binding.selectedIndicator.visibility = View.GONE
                binding.cardServer.strokeWidth = 0
                binding.cardServer.cardBackgroundColor = ColorStateList.valueOf(Color.WHITE)
            }

            // 订阅标签
            val subRemarks = if (profile.subscriptionId.isNotEmpty()) {
                MmkvManager.decodeSubscription(profile.subscriptionId)?.remarks?.firstOrNull()
            } else {
                null
            }

            if (subRemarks != null) {
                binding.tvSubscription.text = subRemarks
                binding.tvSubscription.visibility = View.VISIBLE
            } else {
                binding.tvSubscription.visibility = View.GONE
            }

            // 点击事件
            binding.itemContainer.setOnClickListener {
                onServerClick(server)
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        // 清理资源
        holder.binding.cardServer.strokeWidth = 0
    }
}
