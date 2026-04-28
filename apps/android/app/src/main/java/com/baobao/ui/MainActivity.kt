package com.baobao.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.baobao.R
import com.baobao.databinding.ActivityMainBinding
import com.baobao.ui.page.HomeFragment
import com.baobao.ui.page.ProfileFragment
import com.baobao.ui.page.RecordFragment
import com.baobao.ui.page.StatsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            switchPage(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchPage(HomeFragment())
                R.id.nav_record -> switchPage(RecordFragment())
                R.id.nav_stats -> switchPage(StatsFragment())
                R.id.nav_profile -> switchPage(ProfileFragment())
                else -> false
            }
        }
    }

    private fun switchPage(fragment: Fragment): Boolean {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
        return true
    }
}
