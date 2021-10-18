package com.zestas.cryptmyfiles.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pvryan.easycrypt.symmetric.ECPasswordAnalyzer
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.databinding.FragmentEncryptedViewBinding
import com.zestas.cryptmyfiles.databinding.FragmentPasswordAnalyzerBinding
import java.util.*

class PasswordAnalyzerFragment : Fragment(R.layout.fragment_password_analyzer) {
    private val binding by viewBinding(FragmentPasswordAnalyzerBinding::bind)

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentPasswordAnalyzerBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_password_analyzer, container, false)

        return binding.root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edPasswordP.addTextChangedListener(object : TextWatcher {

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(newText: Editable?) {

                newText?.toString()?.let {
                    val analysis = ECPasswordAnalyzer.analyze(it)
                    val animation = ObjectAnimator.ofInt(
                        binding.progressBarP, "progress",
                        analysis.strength.value * 100)
                    animation.duration = 500 // 0.5 second
                    animation.interpolator = DecelerateInterpolator()
                    animation.start()

                    binding.tvGuesses.text = String.format(Locale.CANADA, "%.4f", analysis.guesses)
                    binding.tvGuessesLog10.text = String.format(Locale.CANADA, "%.4f", analysis.guessesLog10)
                    binding.tvCalcTime.text = analysis.calcTime.toString() + " ms"
                    binding.tvOnlineBFTime.text = String.format(
                        Locale.CANADA, "%.4f",
                        analysis.crackTimeSeconds.onlineThrottling100perHour) +
                            " secs" + " (" + analysis.crackTimesDisplay.onlineThrottling100perHour + ")"
                    binding.tvOfflineBFTime.text = String.format(
                        Locale.CANADA, "%.4f",
                        analysis.crackTimeSeconds.offlineFastHashing1e10PerSecond) +
                            " secs" + " (" + analysis.crackTimesDisplay.offlineFastHashing1e10PerSecond + ")"
                    binding.tvWarning.text = analysis.feedback.warning
                }
            }

            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }
}