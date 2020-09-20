package com.safetymarcus.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.safetymarcus.portfolio.core.CoroutineFragment
import com.safetymarcus.portfolio.utils.IntentBuilder
import com.safetymarcus.portfolio.video.VideoCaptureActivity
import kotlinx.android.synthetic.main.main_fragment.*

/**
 * @author Marcus Hooper
 */
class MainFragment : CoroutineFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cards.setOnClickListener {
            view.findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToCardsFragment()
            )
        }
        record.setOnClickListener {
            VideoCaptureActivity.startVideoCapture(
                activity ?: return@setOnClickListener
            ) {}
        }
        settings.setOnClickListener {
            IntentBuilder.startActivity<SettingsActivity>(requireContext()) {}
        }
    }
}