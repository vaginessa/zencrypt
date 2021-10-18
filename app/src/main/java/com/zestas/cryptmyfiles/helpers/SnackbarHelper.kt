package com.zestas.cryptmyfiles.helpers

import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.pd.chocobar.ChocoBar
import com.zestas.cryptmyfiles.R

class SnackBarHelper {
    companion object {
        private lateinit var mActivity: AppCompatActivity

        fun init(activity: AppCompatActivity) {
            mActivity = activity
        }

        fun showSnackBarInfo(message: String, activity: AppCompatActivity = mActivity) {
            val chocoBar = ChocoBar.builder().setActivity(activity)
/*                .setActionText("ACTION")
                .setActionClickListener {
                    Toast.makeText(activity, "You clicked", Toast.LENGTH_LONG)
                        .show()
                }*/
                .setText(message)
                .setBackgroundColor(ContextCompat.getColor(mActivity,R.color.blue_grey_active_darker))
                .setTextColor(ContextCompat.getColor(mActivity,R.color.white))
                .setIcon(R.drawable.info_outline)
                .setDuration(ChocoBar.LENGTH_LONG)
                .build()
            val view = chocoBar.view
            if (activity == mActivity) {
                val params:
                        CoordinatorLayout.LayoutParams =
                    view.layoutParams as CoordinatorLayout.LayoutParams
                params.gravity = Gravity.TOP

                view.layoutParams = params
            }
            chocoBar.show()
        }

        fun showSnackBarCheck(message: String, activity: AppCompatActivity = mActivity) {
            val chocoBar = ChocoBar.builder().setActivity(activity)
                .setText(message)
                .setIcon(R.drawable.check_all)
                .setTextColor(ContextCompat.getColor(mActivity,R.color.white))
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.green_active))
                .setDuration(ChocoBar.LENGTH_LONG)
                .build()
            val view = chocoBar.view
            if (activity == mActivity) {
                val params:
                        CoordinatorLayout.LayoutParams =
                    view.layoutParams as CoordinatorLayout.LayoutParams
                params.gravity = Gravity.TOP

                view.layoutParams = params
            }
            chocoBar.show()
        }

        fun showSnackBarError(message: String, activity: AppCompatActivity = mActivity) {
            val chocoBar = ChocoBar.builder().setActivity(activity)
                .setText(message)
                .setIcon(R.drawable.close_circle_outline)
                .setTextColor(ContextCompat.getColor(mActivity,R.color.white))
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.red_active))
                .setDuration(ChocoBar.LENGTH_LONG)
                .build()
            val view = chocoBar.view
            if (activity == mActivity) {
                val params:
                        CoordinatorLayout.LayoutParams =
                    view.layoutParams as CoordinatorLayout.LayoutParams
                params.gravity = Gravity.TOP

                view.layoutParams = params
            }

/*            params.anchorId = R.id.bottom_menu
            params.anchorGravity = Gravity.TOP*/

            chocoBar.show()
        }

        fun showSnackBarLove(message: String, activity: AppCompatActivity = mActivity) {
            val chocoBar = ChocoBar.builder().setActivity(activity)
                .setText(message)
                .setIcon(R.drawable.heart_multiple_outline)
                .setTextColor(ContextCompat.getColor(mActivity,R.color.white))
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent))
                .setDuration(ChocoBar.LENGTH_LONG)
                .build()
            val view = chocoBar.view
            if (activity == mActivity) {
                val params:
                        CoordinatorLayout.LayoutParams =
                    view.layoutParams as CoordinatorLayout.LayoutParams
                params.gravity = Gravity.TOP

                view.layoutParams = params
            }

/*            params.anchorId = R.id.bottom_menu
            params.anchorGravity = Gravity.TOP*/

            chocoBar.show()
        }
    }
}
