package com.baobao.ui.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.baobao.R
import com.baobao.databinding.FragmentRecordBinding
import com.baobao.ui.viewmodel.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : Fragment(R.layout.fragment_record) {

    private val viewModel: RecordViewModel by viewModels()
    private lateinit var binding: FragmentRecordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecordBinding.bind(view)

        initUI()
        observeData()
    }

    private fun initUI() {
        // 拍照/录像按钮
        binding.cameraButton.setOnClickListener {
            // TODO: 打开相机
        }

        // 本地上传按钮
        binding.uploadButton.setOnClickListener {
            // TODO: 打开相册选择
        }

        // 快速记录按钮
        binding.logFeedingButton.setOnClickListener {
            // TODO: 打开喂养记录表单
        }

        binding.logPottyButton.setOnClickListener {
            // TODO: 打开排泄记录表单
        }

        binding.logSleepButton.setOnClickListener {
            // TODO: 打开睡眠记录表单
        }

        binding.logGrowthButton.setOnClickListener {
            // TODO: 打开身高体重记录表单
        }

        binding.logHealthButton.setOnClickListener {
            // TODO: 打开更多健康记录
        }
    }

    private fun observeData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.cameraButton.isEnabled = !isLoading
            binding.uploadButton.isEnabled = !isLoading
            binding.logFeedingButton.isEnabled = !isLoading
            binding.logPottyButton.isEnabled = !isLoading
            binding.logSleepButton.isEnabled = !isLoading
            binding.logGrowthButton.isEnabled = !isLoading
            binding.logHealthButton.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                // TODO: 显示错误提示
            }
        }

        viewModel.success.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                // TODO: 显示成功提示
            }
        }
    }
}
