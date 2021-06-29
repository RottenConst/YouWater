package ru.iwater.youwater.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.start_fragment.*
import kotlinx.android.synthetic.main.start_fragment.view.*
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseFragment

class StartFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLayout =  inflater.inflate(R.layout.start_fragment, container, false)

        val navController = NavHostFragment.findNavController(this)

        fragmentLayout.btn_start.setOnClickListener { navController.navigate(R.id.loginFragment) }

        return fragmentLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }
}