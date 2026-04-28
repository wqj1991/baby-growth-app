package com.baobao.ui.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.baobao.R
import com.baobao.databinding.FragmentStatsBinding
import com.baobao.ui.viewmodel.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    private val viewModel: StatsViewModel by viewModels()
    private lateinit var binding: FragmentStatsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStatsBinding.bind(view)

        // TODO: 设置宝宝 ID
        val babyId = "baby_id_placeholder"
        viewModel.setBabyId(babyId)

        observeData()
    }

    private fun observeData() {
        viewModel.feedingLogs.observe(viewLifecycleOwner) { _ -> }
        viewModel.sleepLogs.observe(viewLifecycleOwner) { _ -> }
        viewModel.pottyLogs.observe(viewLifecycleOwner) { _ -> }
        viewModel.growthLogs.observe(viewLifecycleOwner) { _ -> }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.calendarButton.isEnabled = !isLoading
            binding.dateRangeButton.isEnabled = !isLoading
        }
    }
}
