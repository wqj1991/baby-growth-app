package com.baobao.ui.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.baobao.R
import com.baobao.databinding.FragmentHomeBinding
import com.baobao.ui.adapter.TimelineAdapter
import com.baobao.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        initUI()
        observeData()
    }

    private fun initUI() {
        // 初始化时间线列表
        timelineAdapter = TimelineAdapter()
        binding.timelineRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timelineAdapter
        }

        // TODO: 下拉刷新交互可在依赖稳定后恢复
    }

    private fun observeData() {
        viewModel.babyProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.babyNameText.text = profile.name
                binding.babyAgeText.text = viewModel.calculateAge(profile.birthday)
            }
        }

        viewModel.todayFeeding.observe(viewLifecycleOwner) {
            binding.feedingCountText.text = "今天 ${viewModel.getTodayFeedingCount()}次"
        }

        viewModel.todaySleep.observe(viewLifecycleOwner) {
            binding.sleepDurationText.text = "共 ${viewModel.getTodaySleepHours()}"
        }

        viewModel.todayPotty.observe(viewLifecycleOwner) {
            binding.diaperChangeCountText.text = "今天 ${viewModel.getTodayPottyCount()}次"
        }

        viewModel.timeline.observe(viewLifecycleOwner) { items ->
            timelineAdapter.submitList(items)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { _ -> }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                // 显示错误提示
            }
        }
    }
}
