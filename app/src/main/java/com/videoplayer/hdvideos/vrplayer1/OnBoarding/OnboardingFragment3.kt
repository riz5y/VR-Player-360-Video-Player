package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorLoadAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorMyAppNativeAds
import com.videoplayer.hdvideos.vrplayer1.ads.PhoneLocatorShowAds


import com.videoplayer.hdvideos.vrplayer1.databinding.FragmentOnboarding3Binding

class OnboardingFragment3 : Fragment() {
    private var _binding: FragmentOnboarding3Binding? = null
    private val binding get() = _binding!!
    private var shimmerAnimator: ObjectAnimator? = null
    private var shimmerView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.cross.visibility = View.GONE
        binding.cross.postDelayed({
            if (_binding != null) {
                binding.cross.visibility = View.VISIBLE
            }
        }, 3000)

        binding.cross.setOnClickListener {
            PhoneLocatorShowAds.logAnalyticsForClicks("Onboarding Screen 3 Cross Clicked", requireContext())
            val activity = requireActivity() as? OnboardingActivity
            activity?.goToNextPage()
        }

        if (PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreen) {
            startShimmer()
            loadFullScreenNativeAd()
        } else {
            binding.adViewUnified.visibility = View.GONE
        }
    }

    // --- Shimmer helpers ---

    private fun startShimmer() {
        val inflater = LayoutInflater.from(requireContext())
        val shimmerLayout = inflater.inflate(R.layout.shimmer_placeholder, null)
        shimmerView = shimmerLayout.findViewById(R.id.shimmer_view)

        binding.adViewUnified.removeAllViews()
        binding.adViewUnified.addView(shimmerLayout)
        binding.adViewUnified.visibility = View.VISIBLE

        // Animate the shimmer overlay from left to right
        val layoutWidth = binding.adViewUnified.width.toFloat()
        shimmerAnimator = ObjectAnimator.ofFloat(
            shimmerView!!,
            "translationX",
            -layoutWidth,   // start off-screen left
            layoutWidth     // end off-screen right
        ).apply {
            duration = 1500L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    fun stopShimmer() {
        shimmerAnimator?.cancel()
        shimmerAnimator = null
        shimmerView = null
    }


    private fun loadFullScreenNativeAd() {
        try {
            PhoneLocatorMyAppNativeAds.adsNativeOnBoardingFullScreen(
                requireActivity(),
                binding.adViewUnified
            )
        } catch (e: Exception) {
            Log.e("OnboardingFragment3", "Error loading native ad", e)
            stopShimmer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopShimmer()
        _binding = null
    }
}
