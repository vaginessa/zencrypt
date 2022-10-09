package com.zestas.cryptmyfiles.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.app.AlertDialog
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.adapters.DecryptedFilesExpandableRecyclerAdapter
import com.zestas.cryptmyfiles.adapters.EncryptedFilesExpandableRecyclerAdapter
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.dataItemModels.FileItem
import com.zestas.cryptmyfiles.databinding.FragmentDecryptedViewBinding
import com.zestas.cryptmyfiles.helpers.FileSearchHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class DecryptedViewFragment : Fragment(R.layout.fragment_decrypted_view) {
    //---
    private val binding by viewBinding(FragmentDecryptedViewBinding::bind)
    private lateinit var progressDialog: AlertDialog
    private lateinit var externalFilesDir: File
    private lateinit var data: ArrayList<FileItem>
    private lateinit var adapter: DecryptedFilesExpandableRecyclerAdapter
    //---

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalFilesDir = ZenCryptConstants.decryptedFilesDir(requireContext())
        if (!externalFilesDir.exists())
            externalFilesDir.mkdir()

        loadDataAndPopulateCardView()
        initToolbarMenu()
    }

    private fun loadDataAndPopulateCardView() {
        buildProgressDialog()
        progressDialog.show()
        lifecycleScope.launch {
            whenStarted {
                data = withContext(Dispatchers.IO) {
                    val decryptedFilesItems: ArrayList<FileItem> = ArrayList()
                    externalFilesDir.walkTopDown().filter { file -> !file.isDirectory }.sortedBy { it.name }.forEach { file ->
                        decryptedFilesItems.add(FileItem.create(file))
                    }
                    return@withContext decryptedFilesItems
                }

                val tvLocation = binding.tvLocation
                if (data.size == 0) {
                    val emptyListView = binding.emptyList
                    val infoIcon = binding.infoIcon
                    emptyListView.visibility = VISIBLE
                    tvLocation.visibility = GONE
                    infoIcon.visibility = VISIBLE
                }
                else {
                    tvLocation.visibility = VISIBLE
                    val recyclerView: RecyclerView = binding.cardListRecyclerView
                    recyclerView.setHasFixedSize(true)
                    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
                    recyclerView.layoutManager = layoutManager
                    adapter = DecryptedFilesExpandableRecyclerAdapter(data)
                    recyclerView.adapter = adapter
                }
            }
            progressDialog.dismiss()
        }
    }

    private fun initToolbarMenu() {
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener {
            val searchView: SearchView
            when(it.itemId) {
                R.id.search_files -> {
                    searchView = it.actionView as SearchView
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                        android.widget.SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(p0: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(msg: String): Boolean {
                            // inside on query text change method we are
                            // calling a method to filter our recycler view.
                            if (data.size != 0)
                                FileSearchHelper.filterResults(data, msg, adapter)
                            return false
                        }
                    })
                    true
                }
                else -> false
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