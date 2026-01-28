package com.v2ray.ang.ui

import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.net.VpnService
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityMainSimpleBinding
import com.v2ray.ang.extension.toast
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.handler.V2RayServiceManager
import com.v2ray.ang.dto.PermissionType
import com.v2ray.ang.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 简化版主界面
 * 设计原则：
 * 1. 极简设计，只保留核心功能
 * 2. 清晰的状态指示
 * 3. 简单的操作流程
 */
class MainActivitySimple : BaseActivity() {

    private val binding by lazy {
        ActivityMainSimpleBinding.inflate(layoutInflater)
    }

    val mainViewModel: MainViewModel by viewModels()
    private lateinit var serverAdapter: ServerSimpleAdapter
    private var addServerDialog: AlertDialog? = null

    private val requestVpnPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            startV2Ray()
        } else {
            updateConnectionState(false)
            toast(R.string.toast_permission_denied)
        }
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)
            addServerDialog?.dismiss()
        } else {
            toast(R.string.toast_permission_denied)
        }
    }

    private val scanQRCodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val result = it.data?.getStringExtra("SCAN_RESULT")
            if (!result.isNullOrEmpty()) {
                importBatchConfig(result)
                addServerDialog?.dismiss()
            }
        }
    }

    private val chooseFileForCustomConfig = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val uri = it.data?.data
        if (it.resultCode == RESULT_OK && uri != null) {
            readContentFromUri(uri)
            addServerDialog?.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initRecyclerView()
        setupViewModel()
        loadData()

        // 请求通知权限
        checkAndRequestPermission(PermissionType.POST_NOTIFICATIONS) {}
    }

    private fun initViews() {
        // 添加服务器按钮
        binding.btnAddServer.setOnClickListener {
            showAddServerDialog()
        }

        // 连接/断开按钮
        binding.btnConnect.setOnClickListener {
            toggleConnection()
        }

        // 空状态
        updateEmptyState()
    }

    private fun initRecyclerView() {
        serverAdapter = ServerSimpleAdapter(
            onServerClick = { server ->
                selectServer(server.guid)
            }
        )
        binding.recyclerViewServers.adapter = serverAdapter
    }

    private fun setupViewModel() {
        // 监听连接状态
        mainViewModel.isRunning.observe(this) { isRunning ->
            updateConnectionState(isRunning)
        }

        // 监听测试结果
        mainViewModel.updateTestResultAction.observe(this) { result ->
            updateSpeedDisplay(result)
        }

        // 监听服务器列表变化
        mainViewModel.serversCache.observe(this) { servers ->
            serverAdapter.updateServers(servers)
            updateEmptyState()

            // 更新当前服务器显示
            val selectedServer = MmkvManager.getSelectServer()
            if (!selectedServer.isNullOrEmpty()) {
                val server = servers.find { it.guid == selectedServer }
                binding.tvCurrentServer.text = server?.profile?.remarks ?: getString(R.string.simple_select_server)
            }
        }

        mainViewModel.startListenBroadcast()
        mainViewModel.initAssets(assets)
    }

    private fun loadData() {
        mainViewModel.reloadServerList()
    }

    private fun updateEmptyState() {
        val servers = mainViewModel.serversCache
        if (servers.isEmpty()) {
            binding.recyclerViewServers.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewServers.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE
        }
    }

    private fun updateConnectionState(isRunning: Boolean) {
        when {
            isRunning -> {
                // 已连接状态
                binding.statusIndicator.setCardBackgroundColor(ColorStateList.valueOf(getColor(R.color.color_fab_active)))
                binding.tvStatusText.text = getString(R.string.simple_status_connected)
                binding.tvStatusText.setTextColor(getColor(R.color.color_fab_active))
                binding.btnConnect.text = getString(R.string.simple_disconnect)
                binding.btnConnect.setIconResource(R.drawable.ic_stop_24dp)
                binding.btnConnect.backgroundTintList = ColorStateList.valueOf(getColor(R.color.color_fab_inactive))

                // 显示当前选中的服务器
                val selectedServer = MmkvManager.getSelectServer()
                if (!selectedServer.isNullOrEmpty()) {
                    val server = mainViewModel.serversCache.find { it.guid == selectedServer }
                    binding.tvCurrentServer.text = server?.remarks ?: getString(R.string.simple_select_server)
                }
            }
            else -> {
                // 未连接状态
                binding.statusIndicator.setCardBackgroundColor(ColorStateList.valueOf(getColor(android.R.color.darker_gray)))
                binding.tvStatusText.text = getString(R.string.simple_status_not_connected)
                binding.tvStatusText.setTextColor(getColor(android.R.color.darker_gray))
                binding.btnConnect.text = getString(R.string.simple_connect)
                binding.btnConnect.setIconResource(R.drawable.ic_play_arrow_24dp)
                binding.btnConnect.backgroundTintList = ColorStateList.valueOf(getColor(R.color.color_fab_active))

                // 显示当前选中的服务器
                val selectedServer = MmkvManager.getSelectServer()
                if (!selectedServer.isNullOrEmpty()) {
                    val server = mainViewModel.serversCache.find { it.guid == selectedServer }
                    binding.tvCurrentServer.text = server?.remarks ?: getString(R.string.simple_select_server)
                }
            }
        }
    }

    private fun updateSpeedDisplay(result: String?) {
        binding.tvSpeed.text = result ?: "0 KB/s"
    }

    private fun showAddServerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_server, null)

        addServerDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.cardScanQR).setOnClickListener {
            checkAndRequestPermission(PermissionType.CAMERA) {
                scanQRCodeLauncher.launch(Intent(this, ScannerActivity::class.java))
            }
        }

        dialogView.findViewById<View>(R.id.cardImportClipboard).setOnClickListener {
            importClipboard()
            addServerDialog?.dismiss()
        }

        dialogView.findViewById<View>(R.id.cardManualAdd).setOnClickListener {
            showManualAddOptions()
            addServerDialog?.dismiss()
        }

        dialogView.findViewById<View>(R.id.cardImportSubscription).setOnClickListener {
            startActivity(Intent(this, SubEditActivity::class.java))
            addServerDialog?.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            addServerDialog?.dismiss()
        }

        addServerDialog?.show()
    }

    private fun showManualAddOptions() {
        val options = arrayOf(
            "VMess",
            "VLESS",
            "Shadowsocks",
            "Trojan",
            "WireGuard",
            "Hysteria2"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.simple_manual_add)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> importManually(1) // VMess
                    1 -> importManually(2) // VLESS
                    2 -> importManually(3) // Shadowsocks
                    3 -> importManually(4) // Trojan
                    4 -> importManually(5) // WireGuard
                    5 -> importManually(6) // Hysteria2
                }
            }
            .show()
    }

    private fun importManually(createConfigType: Int) {
        startActivity(
            Intent()
                .putExtra("createConfigType", createConfigType)
                .putExtra("subscriptionId", mainViewModel.subscriptionId)
                .setClass(this, ServerActivity::class.java)
        )
    }

    private fun importClipboard(): Boolean {
        try {
            val clipboard = getClipboardContent()
            if (!clipboard.isNullOrEmpty()) {
                importBatchConfig(clipboard)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun getClipboardContent(): String? {
        val clipboard = getSystemService(android.content.ClipboardManager::class.java)
        return clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
    }

    private fun importBatchConfig(server: String?) {
        if (server.isNullOrEmpty()) return

        val result = AngConfigManager.importConfig(server)
        if (result) {
            toast(R.string.toast_success)
            mainViewModel.reloadServerList()
            updateEmptyState()
        } else {
            toast(R.string.toast_decoding_failed)
        }
    }

    private fun selectServer(guid: String) {
        MmkvManager.setSelectServer(guid)
        mainViewModel.reloadServerList()
        updateEmptyState()

        val server = mainViewModel.serversCache.find { it.guid == guid }
        binding.tvCurrentServer.text = server?.remarks ?: getString(R.string.simple_select_server)

        Toast.makeText(this, "已选择: ${server?.remarks}", Toast.LENGTH_SHORT).show()
    }

    private fun toggleConnection() {
        if (mainViewModel.isRunning.value == true) {
            // 断开连接
            V2RayServiceManager.stopVService(this)
        } else {
            // 开始连接
            if (MmkvManager.getSelectServer().isNullOrEmpty()) {
                toast(R.string.title_file_chooser)
                return
            }

            val intent = VpnService.prepare(this)
            if (intent == null) {
                startV2Ray()
            } else {
                requestVpnPermission.launch(intent)
            }
        }
    }

    private fun startV2Ray() {
        V2RayServiceManager.startVService(this)
    }

    private fun restartV2Ray() {
        if (mainViewModel.isRunning.value == true) {
            V2RayServiceManager.stopVService(this)
        }
        lifecycleScope.launch {
            delay(500)
            startV2Ray()
        }
    }

    private fun readContentFromUri(uri: android.net.Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                val content = input.bufferedReader().use { it.readText() }
                importBatchConfig(content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast(R.string.toast_config_file_invalid)
        }
    }

    override fun onResume() {
        super.onResume()
        // 刷新数据
        mainViewModel.reloadServerList()
        updateEmptyState()
        updateConnectionState(mainViewModel.isRunning.value == true)
    }
}
