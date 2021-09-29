package ru.iwater.youwater.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import ru.iwater.youwater.R
import ru.iwater.youwater.base.BaseActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        addStartFragment()
    }

//    fun addStartFragment() {
//        if (supportFragmentManager.fragments.isEmpty()) {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.fragment_container, StartFragment.newInstance())
//                .commit()
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StartFragment.newInstance()).addToBackStack(null).commit()
//        }
//    }


    companion object {
        fun start(context: Context) {
            ContextCompat.startActivity(context, Intent(context, StartActivity::class.java), null)
        }
    }
}