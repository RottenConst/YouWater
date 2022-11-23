package ru.iwater.youwater.screen.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.iwater.youwater.R

class DeliverySuburbsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delivery_suburbs, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DeliverySuburbsFragment()
    }
}