//package com.videoplayer.hdvideos.vrplayer1.OnBoarding
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.videoplayer.hdvideos.vrplayer1.R
//
//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [OnboardingFragment1Native.newInstance] factory method to
// * create an instance of this fragment.
// */
//class OnboardingFragment1Native : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_onboarding_fragment1_native, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment OnboardingFragment1Native.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            OnboardingFragment1Native().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}


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

class OnboardingFragment1Native : Fragment() {
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
            PhoneLocatorShowAds.logAnalyticsForClicks("Onboarding NativeAfterFirst Cross Clicked", requireContext())
            val activity = requireActivity() as? OnboardingActivity
            activity?.goToNextPage()
        }

        if (PhoneLocatorLoadAds.canShowNativeOnBoardingFullScreenafterfirst) {
            startShimmer()
            loadFullScreenNativeAd()
        } else {
            binding.adViewUnified.visibility = View.GONE
        }
    }

    private fun startShimmer() {
        val inflater = LayoutInflater.from(requireContext())
        val shimmerLayout = inflater.inflate(R.layout.shimmer_placeholder, null)
        shimmerView = shimmerLayout.findViewById(R.id.shimmer_view)

        binding.adViewUnified.removeAllViews()
        binding.adViewUnified.addView(shimmerLayout)
        binding.adViewUnified.visibility = View.VISIBLE

        val layoutWidth = binding.adViewUnified.width.toFloat()
        shimmerAnimator = ObjectAnimator.ofFloat(
            shimmerView!!,
            "translationX",
            -layoutWidth,
            layoutWidth
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
            PhoneLocatorMyAppNativeAds.adsNativeOnBoardingFullScreenAfterFirst(
                requireActivity(),
                binding.adViewUnified
            )
        } catch (e: Exception) {
            Log.e("OnboardingFragment1Native", "Error loading native ad", e)
            stopShimmer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopShimmer()
        _binding = null
    }
}