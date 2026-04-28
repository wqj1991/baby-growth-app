package com.baobao.ui.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.baobao.R
import com.baobao.databinding.FragmentProfileBinding
import com.baobao.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        initUI()
        observeData()
    }

    private fun initUI() {
        // 编辑宝宝档案
        binding.editBabyProfileButton.setOnClickListener {
            // TODO: 打开宝宝档案编辑对话框
        }

        // 清空缓存
        binding.cacheManagementButton.setOnClickListener {
            viewModel.clearCache()
        }

        // 家庭成员管理
        binding.familySpaceButton.setOnClickListener {
            // TODO: 打开家庭成员管理页面
        }

        // 系统设置入口暂作为刷新触发
        binding.systemSettingsButton.setOnClickListener {
            viewModel.loadData()
        }
    }

    private fun observeData() {
        viewModel.babyProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.userName.text = profile.name
            }
        }

        viewModel.appConfig.observe(viewLifecycleOwner) { config ->
            val networkText = if (config.wifiOnlyUpload) "仅 WiFi" else "全网络"
            binding.systemSettingsButton.text = "系统设置（同步 ${config.syncIntervalMinutes} 分钟，$networkText）"
        }

        viewModel.cacheSize.observe(viewLifecycleOwner) { size ->
            binding.cacheManagementButton.text = "缓存管理（${viewModel.formatCacheSize(size)}）"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.editBabyProfileButton.isEnabled = !isLoading
            binding.familySpaceButton.isEnabled = !isLoading
            binding.cacheManagementButton.isEnabled = !isLoading
            binding.systemSettingsButton.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                // 显示错误提示
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                // 显示成功提示
            }
        }
    }
}
