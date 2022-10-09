package com.zestas.cryptmyfiles.helpers.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.zestas.cryptmyfiles.R

class SnackBarHelper {
    companion object {
        private lateinit var mActivity: AppCompatActivity

        fun init(activity: AppCompatActivity) {
            mActivity = activity
        }

        fun showSnackBarInfo(message: String, activity: AppCompatActivity = mActivity) {
            if (activity != mActivity) {
                Snackbar.make(activity.findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    .show()
            }
            else {
                Snackbar.make(activity.findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    .setAnchorView(R.id.bottom_menu)
                    .show()
            }
        }

        fun showSnackBarCheck(message: String, activity: AppCompatActivity = mActivity) {
            if (activity != mActivity) {
                Snackbar.make(activity.findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    .setBackgroundTint(ContextCompat.getColor(activity, R.color.green_active))
                    .show()
            }
            else {
                Snackbar.make(activity.findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_menu)
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    .setBackgroundTint(ContextCompat.getColor(activity, R.color.green_active))
                    .show()
            }
        }

        fun showSnackBarError(message: String, activity: AppCompatActivity = mActivity) {
            if (activity != mActivity) {
                Snackbar.make(activity.findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    .setBackgroundTint(ContextCompat.getColor(activity, R.color.red_active))
                    .show()
            }
            else {
                Snackbar.make(activity.findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.bottom_menu)
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    .setBackgroundTint(ContextCompat.getColor(activity, R.color.red_active))
                    .show()
            }
        }

        fun showSnackBarLove(message: String) {
            Snackbar.make(mActivity.findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.bottom_menu)
                .setBackgroundTint(ContextCompat.getColor(mActivity, R.color.colorAccent))
                .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                .show()
        }

        fun showSnackBarBuyPro() {
            Snackbar.make(mActivity.findViewById(R.id.content), "This is a PRO feature.", Snackbar.LENGTH_LONG)
                .setAction("BUY") {
                    val menu = mActivity.findViewById<ChipNavigationBar>(R.id.bottom_menu)
//                    FragmentHelper.replaceFragmentWithDelay(SettingsFragment(),0)
                    if (menu.getSelectedItemId() != R.id.settings)
                        menu.setItemSelected(R.id.settings)
                }
                .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                .setActionTextColor(ContextCompat.getColor(mActivity,R.color.white))
                .setAnchorView(R.id.bottom_menu)
                .setBackgroundTint(ContextCompat.getColor(mActivity, R.color.red_active))
                .show()
        }
    }
}
