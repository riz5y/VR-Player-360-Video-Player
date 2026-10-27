package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.databinding.FragmentOnboarding2Binding


class OnboardingFragment2 : Fragment() {
    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }



    private fun setupUI() {
        binding.apply {
            onboardingImage.setImageResource(R.drawable.oonboard2)
            onboardingImage.topCrop()

            val titleText = "Watch VR\nMade Easy"
            val spannable = android.text.SpannableString(titleText)
            val colorStart = titleText.indexOf("Easy")
            spannable.setSpan(
                android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#DAE639")),
                colorStart,
                colorStart + "Easy".length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                colorStart,
                colorStart + "Easy".length,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            onboardingTitle.text = spannable

            onboardingDescription.text = "Enjoy smooth, high-quality VR videos in just one tap."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}