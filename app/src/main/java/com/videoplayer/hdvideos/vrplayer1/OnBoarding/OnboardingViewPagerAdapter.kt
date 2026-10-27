//package com.videoplayer.hdvideos.vrplayer1.OnBoarding
//
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.viewpager2.adapter.FragmentStateAdapter
// import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
//
//class OnboardingViewPagerAdapter(
//    fragmentActivity: FragmentActivity
//) : FragmentStateAdapter(fragmentActivity) {
//
//    private val pages: List<OnboardingPage> = buildPages()
//
//    private fun buildPages(): List<OnboardingPage> {
//        val list = mutableListOf<OnboardingPage>()
//        list.add(OnboardingPage.Page1)
//        list.add(OnboardingPage.Page2)
//
//        if (PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreen) {
//            list.add(OnboardingPage.FullScreenAd) // 🔥 only added if enabled
//        }
//
//        list.add(OnboardingPage.Page3Content)
//        return list
//    }
//
//    override fun getItemCount(): Int = pages.size
//
//    override fun createFragment(position: Int): Fragment {
//        return when (pages[position]) {
//            OnboardingPage.Page1 -> OnboardingFragment1()
//            OnboardingPage.Page2 -> OnboardingFragment2()
//            OnboardingPage.FullScreenAd -> OnboardingFragment3() // full-screen native ad fragment
//            OnboardingPage.Page3Content -> OnboardingFragment4()
//        }
//    }
//
//    fun isFullScreenAdPage(position: Int): Boolean {
//        return pages.getOrNull(position) is OnboardingPage.FullScreenAd
//    }
//}


package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds

class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val pages: List<OnboardingPage> = buildPages()

    private fun buildPages(): List<OnboardingPage> {
        val list = mutableListOf<OnboardingPage>()
        list.add(OnboardingPage.Page1)

        if (PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreenafterfirst) {
            list.add(OnboardingPage.FullScreenAdAfterFirst)
        }

        list.add(OnboardingPage.Page2)

        if (PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreen) {
            list.add(OnboardingPage.FullScreenAd)
        }

        list.add(OnboardingPage.Page3Content)
        return list
    }

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return when (pages[position]) {
            OnboardingPage.Page1 -> OnboardingFragment1()
            OnboardingPage.FullScreenAdAfterFirst -> OnboardingFragment1Native()
            OnboardingPage.Page2 -> OnboardingFragment2()
            OnboardingPage.FullScreenAd -> OnboardingFragment3()
            OnboardingPage.Page3Content -> OnboardingFragment4()
        }
    }

    fun isFullScreenAdPage(position: Int): Boolean {
        val page = pages.getOrNull(position)
        return page is OnboardingPage.FullScreenAd || page is OnboardingPage.FullScreenAdAfterFirst
    }
}