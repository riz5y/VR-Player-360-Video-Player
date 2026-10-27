package com.videoplayer.hdvideos.vrplayer1.OnBoarding

//sealed class OnboardingPage {
//    object Page1 : OnboardingPage()
//    object Page2 : OnboardingPage()
//    object Page3Content : OnboardingPage() // your normal content page (old Fragment4)
//    object FullScreenAd : OnboardingPage()
//}


sealed class OnboardingPage {
    object Page1 : OnboardingPage()
    object Page2 : OnboardingPage()
    object Page3Content : OnboardingPage()
    object FullScreenAd : OnboardingPage()
    object FullScreenAdAfterFirst : OnboardingPage() // new
}