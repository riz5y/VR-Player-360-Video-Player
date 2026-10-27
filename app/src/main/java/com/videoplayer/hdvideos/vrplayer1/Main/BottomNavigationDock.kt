package com.videoplayer.hdvideos.vrplayer1.Main

import android.content.res.ColorStateList
import android.graphics.Color
import com.videoplayer.hdvideos.vrplayer1.databinding.LayoutBottomNavDockBinding

enum class MainNavigationTab {
    HOME,
    MEDITATION,
    ROLLER_COASTER,
    JURASSIC
}

object BottomNavigationDock {
    fun bind(
        binding: LayoutBottomNavDockBinding,
        selectedTab: MainNavigationTab?,
        onTabSelected: (MainNavigationTab) -> Unit
    ) {
        setSelectedTab(binding, selectedTab)
        binding.navHome.setOnClickListener { onTabSelected(MainNavigationTab.HOME) }
        binding.navMeditation.setOnClickListener { onTabSelected(MainNavigationTab.MEDITATION) }
        binding.navRollerCoaster.setOnClickListener {
            onTabSelected(MainNavigationTab.ROLLER_COASTER)
        }
        binding.navJurassic.setOnClickListener { onTabSelected(MainNavigationTab.JURASSIC) }
    }

    fun setSelectedTab(
        binding: LayoutBottomNavDockBinding,
        selectedTab: MainNavigationTab?
    ) {
        val activeColor = ColorStateList.valueOf(Color.WHITE)
        val inactiveColor = ColorStateList.valueOf(Color.parseColor("#C1B3B3"))

        binding.ivNavHome.imageTintList =
            if (selectedTab == MainNavigationTab.HOME) activeColor else inactiveColor
        binding.tvNavHome.setTextColor(
            if (selectedTab == MainNavigationTab.HOME) Color.WHITE else inactiveColor.defaultColor
        )

        binding.ivNavMeditation.imageTintList =
            if (selectedTab == MainNavigationTab.MEDITATION) activeColor else inactiveColor
        binding.tvNavMeditation.setTextColor(
            if (selectedTab == MainNavigationTab.MEDITATION) Color.WHITE else inactiveColor.defaultColor
        )

        binding.ivNavRollerCoaster.imageTintList =
            if (selectedTab == MainNavigationTab.ROLLER_COASTER) activeColor else inactiveColor
        binding.tvNavRollerCoaster.setTextColor(
            if (selectedTab == MainNavigationTab.ROLLER_COASTER) Color.WHITE else inactiveColor.defaultColor
        )

        binding.ivNavJurassic.imageTintList =
            if (selectedTab == MainNavigationTab.JURASSIC) activeColor else inactiveColor
        binding.tvNavJurassic.setTextColor(
            if (selectedTab == MainNavigationTab.JURASSIC) Color.WHITE else inactiveColor.defaultColor
        )
    }
}
