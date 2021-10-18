package com.zestas.cryptmyfiles.helpers

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.zestas.cryptmyfiles.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentHelper {
    companion object {
        private lateinit var mActivity: AppCompatActivity

        fun init(activity: AppCompatActivity) {
            mActivity = activity
        }

        fun replaceFragmentWithDelay(fragment: Fragment, timeMillis: Long = 350) {
            // very important to use launchWhenStarted {...} here. If we were to use launch {...}
            // the isStateSaved variable will be false and the transaction is not safe to commit,
            // resulting in app crash due to illegalStateException. launchWhenStarted ensures that the
            // state is saved and the transaction is safe to commit!
            mActivity.lifecycleScope.launchWhenStarted {
                delay(timeMillis)
                val backStateName = fragment.javaClass.name
                val fragmentPopped = mActivity.supportFragmentManager.popBackStackImmediate (backStateName, 0)

                if (!fragmentPopped && mActivity.supportFragmentManager.findFragmentByTag(backStateName) == null) { // fragment not in back stack, create it.
                    val fragmentTransaction = mActivity.supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.addToBackStack(backStateName);
                    fragmentTransaction.commit();
                }
            }
        }
    }
}