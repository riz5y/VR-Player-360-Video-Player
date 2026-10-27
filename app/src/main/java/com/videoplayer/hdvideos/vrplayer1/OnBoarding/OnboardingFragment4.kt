package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.databinding.FragmentOnboarding4Binding

class OnboardingFragment4 : Fragment() {
    private var _binding: FragmentOnboarding4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            onboardingImage.setImageResource(R.drawable.oonboard3)
            onboardingImage.topCrop()

            val titleText = "Explore\nTrending\nVR Videos"
            val spannable = android.text.SpannableString(titleText)
            val colorStart = titleText.indexOf("VR Videos")
            spannable.setSpan(
                android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#DAE639")),
                colorStart,
                colorStart + "VR Videos".length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                colorStart,
                colorStart + "VR Videos".length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            onboardingTitle.text = spannable

            onboardingDescription.text = "Find popular and category-based VR videos curated for you and start watching in seconds."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}