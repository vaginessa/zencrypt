package com.zestas.cryptmyfiles.adapters

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.zestas.cryptmyfiles.BuildConfig
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.activities.ActionActivity
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.dataItemModels.FileItem
import com.zestas.cryptmyfiles.helpers.FileActionsHelper
import com.zestas.cryptmyfiles.helpers.ui.SnackBarHelper


class EncryptedFilesExpandableRecyclerAdapter(private var data: List<FileItem>) :
    RecyclerView.Adapter<EncryptedFilesExpandableRecyclerAdapter.ViewHolder>() {
    private val expandState: SparseBooleanArray = SparseBooleanArray()
    private var context: Context? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        context = viewGroup.context
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.encrypted_file_item_cardview, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.setIsRecyclable(false)
        viewHolder.tvFileTitle.text = data[position].getName()
        viewHolder.tvFileSubtitle1.text = data[position].getDateTime()
        viewHolder.tvFileSubtitle2.text = data[position].getSize()

        //check if view is expanded
        val isExpanded: Boolean = expandState.get(position)
        viewHolder.expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE
        viewHolder.buttonLayout.rotation = if (expandState.get(position)) 180f else 0f
        //---- Listener for expanding/collapsing
        viewHolder.relativeLayoutContainer.setOnClickListener {
            onClickButton(
                viewHolder.expandableLayout,
                viewHolder.buttonLayout,
                viewHolder.bindingAdapterPosition
            )
        }
        //---- Button listeners
        // Delete button
        viewHolder.expandableLayout.findViewById<Button>(R.id.button_delete).setOnClickListener {
            FileActionsHelper.showFileDeleteConfirmDialog(data[position].getFile(), ZenCryptConstants.REPLACE_WITH_ENCRYPTED)
        }
        // Rename button
        viewHolder.expandableLayout.findViewById<Button>(R.id.button_rename).setOnClickListener {
            FileActionsHelper.showFileRenameDialog(data[position].getFile(), ZenCryptConstants.REPLACE_WITH_ENCRYPTED)
        }

        // Decrypt button
        viewHolder.expandableLayout.findViewById<Button>(R.id.button_decrypt).setOnClickListener {
            val intent = Intent(context, ActionActivity::class.java)
            intent.putExtra(ZenCryptConstants.REQUEST_CODE, ZenCryptConstants.FROM_CARD_VIEW)
            intent.putExtra(ZenCryptConstants.FILE, data[position].getFile())
            intent.putExtra(ZenCryptConstants.ACTION_CODE, ZenCryptConstants.ACTION_DECRYPT)
            context?.startActivity(intent)
        }
        // Share button
        viewHolder.expandableLayout.findViewById<Button>(R.id.button_share).setOnClickListener {
            val uri = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", data[position].getFile())
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.type = "*/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
            try {
                context!!.startActivity(shareIntent)
            } catch (e: ActivityNotFoundException) {
                SnackBarHelper.showSnackBarError("No activity found.")
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFileTitle: TextView = view.findViewById(R.id.encrypted_file_title) as TextView
        val tvFileSubtitle1: TextView = view.findViewById(R.id.encrypted_file_subtitle1) as TextView
        val tvFileSubtitle2: TextView = view.findViewById(R.id.encrypted_file_subtitle2) as TextView
        var buttonLayout: RelativeLayout = view.findViewById(R.id.button) as RelativeLayout
        var relativeLayoutContainer: RelativeLayout = view.findViewById(R.id.relative_layout_container) as RelativeLayout
        var expandableLayout: LinearLayout = view.findViewById(R.id.expandableLayout) as LinearLayout

    }

    private fun onClickButton(
        expandableLayout: LinearLayout,
        buttonLayout: RelativeLayout,
        i: Int
    ) {

        //Simply set View to Gone if not expanded
        //Not necessary but I put simple rotation on button layout
        if (expandableLayout.visibility == View.VISIBLE) {
            createRotateAnimator(buttonLayout, 180f, 0f).start()
            expandableLayout.visibility = View.GONE
            expandState.put(i, false)
        } else {
            createRotateAnimator(buttonLayout, 0f, 180f).start()
            expandableLayout.visibility = View.VISIBLE
            expandState.put(i, true)
        }
    }

    //Code to rotate button
    private fun createRotateAnimator(target: View, from: Float, to: Float): ObjectAnimator {
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(target, "rotation", from, to)
        animator.duration = 300
        animator.interpolator = LinearInterpolator()
        return animator
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterFileList(filterList: List<FileItem>) {
        data = filterList
        notifyDataSetChanged()
    }

    init {
        //set initial expanded state to false
        for (i in data.indices) {
            expandState.append(i, false)
        }
    }
}