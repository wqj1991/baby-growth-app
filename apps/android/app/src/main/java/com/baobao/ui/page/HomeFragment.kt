package com.baobao.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baobao.R
import com.baobao.databinding.FragmentHomeBinding
import com.baobao.ui.home.TimelineAdapter
import com.baobao.ui.home.TimelineItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimeline()
    }

    private fun setupTimeline() {
        val timelineItems = listOf(
            TimelineItem.Photo(
                id = "1",
                userName = "妈妈",
                userAvatar = R.drawable.mom_avatar,
                postTime = "2小时前",
                message = "今天天气真好，带宝宝去公园玩啦！☀️",
                imageUrl = R.drawable.baby_park,
                likeCount = 12,
                commentCount = 3
            ),
            TimelineItem.Video(
                id = "2",
                userName = "爸爸",
                userAvatar = R.drawable.dad_avatar,
                postTime = "5小时前",
                message = "宝宝第一次自己走路，必须记录下来！",
                thumbnailUrl = R.drawable.baby_walking,
                likeCount = 25,
                commentCount = 8
            ),
            TimelineItem.Log(
                id = "3",
                userName = "妈妈",
                userAvatar = R.drawable.mom_avatar,
                postTime = "8小时前",
                logType = "喂奶记录",
                logDetails = "奶粉 180ml",
                logIcon = R.drawable.ic_bottle
            )
        )

        binding.timelineRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TimelineAdapter(timelineItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
