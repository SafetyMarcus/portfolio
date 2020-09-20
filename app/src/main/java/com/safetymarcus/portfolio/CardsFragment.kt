package com.safetymarcus.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.safetymarcus.portfolio.core.CoroutineFragment

/**
 * @author Marcus Hooper
 */
class CardsFragment : CoroutineFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.cards_fragment, container, false)
}