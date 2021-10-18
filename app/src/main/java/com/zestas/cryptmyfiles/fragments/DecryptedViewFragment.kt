package com.zestas.cryptmyfiles.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.app.AlertDialog
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.adapters.DecryptedFilesExpandableRecyclerAdapter
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.dataItemModels.FileItem
import com.zestas.cryptmyfiles.databinding.FragmentDecryptedViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DecryptedViewFragment : Fragment(R.layout.fragment_decrypted_view) {
    //---
    private val binding by viewBinding(FragmentDecryptedViewBinding::bind)
    private lateinit var progressDialog: AlertDialog
    private lateinit var externalFilesDir: File
    //---

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalFilesDir = ZenCryptConstants.decryptedFilesDir(requireActivity())
        if (!externalFilesDir.exists())
            externalFilesDir.mkdir()
        loadDataAndPopulateCardView()
    }

    private fun loadDataAndPopulateCardView() {
        buildProgressDialog()
        lifecycleScope.launch {
            whenStarted {
                progressDialog.show()
                val data = withContext(Dispatchers.IO) {
                    val decryptedFilesItems: ArrayList<FileItem> = ArrayList()
                    externalFilesDir.walkTopDown().filter { file -> !file.isDirectory }.forEach { file ->
                        decryptedFilesItems.add(FileItem.create(file))
                    }
                    return@withContext decryptedFilesItems
                }

                if (data.size == 0) {
                    val emptyListView = binding.emptyList
                    val infoIcon = binding.infoIcon
                    emptyListView.visibility = VISIBLE
                    infoIcon.visibility = VISIBLE
                }
                else {
                    val recyclerView: RecyclerView = binding.cardListRecyclerView
                    recyclerView.setHasFixedSize(true)
                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.adapter = DecryptedFilesExpandableRecyclerAdapter(data)
                }
                progressDialog.dismiss()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

    private fun buildProgressDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false)

        builder.setView(R.layout.indeterminate_progress_circular)
        progressDialog = builder.create()
    }

}