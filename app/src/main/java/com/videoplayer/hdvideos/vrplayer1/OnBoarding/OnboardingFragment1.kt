package com.videoplayer.hdvideos.vrplayer1.OnBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
 import com.videoplayer.hdvideos.vrplayer1.R
import com.videoplayer.hdvideos.vrplayer1.databinding.FragmentOnboarding1Binding

class OnboardingFragment1 : Fragment() {
    private var _binding: FragmentOnboarding1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }


private fun setupUI() {
    binding.apply {
        onboardingImage.setImageResource(R.drawable.oonboard1)
        onboardingImage.topCrop()

        val titleText = "Enter the\nWorld of VR"
        val spannable = android.text.SpannableString(titleText)
        val colorStart = titleText.indexOf("of VR")
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor("#DAE639")),
            colorStart,
            colorStart + "of VR".length,
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            colorStart,
            colorStart + "of VR".length,
            android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        onboardingTitle.text = spannable

        onboardingDescription.text = "Watch VR videos easily with smooth playback and a fully engaging viewing experience."
    }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}