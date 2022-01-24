package com.zestas.cryptmyfiles.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.zestas.cryptmyfiles.databinding.FragmentEncryptedViewBinding
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.adapters.EncryptedFilesExpandableRecyclerAdapter
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.dataItemModels.FileItem
import com.zestas.cryptmyfiles.dataItemModels.ZenCryptSettingsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class EncryptedViewFragment : Fragment(R.layout.fragment_encrypted_view) {
    //---
    private val binding by viewBinding(FragmentEncryptedViewBinding::bind)
    private lateinit var progressDialog: AlertDialog
    private lateinit var externalFilesDir: File
    //---


/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = FragmentEncryptedViewBinding.inflate(layoutInflater)

    }*/

/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_password_analyzer, container, false)

        return binding.root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalFilesDir = ZenCryptConstants.encryptedFilesDir(requireActivity())
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
                    val encryptedFileItems: ArrayList<FileItem> = ArrayList()
                    externalFilesDir.walkTopDown().filter { file -> !file.isDirectory }.sortedBy { it.name }.forEach { file ->
                        if (file.name.endsWith(ZenCryptSettingsModel.extension.value))
                            encryptedFileItems.add(FileItem.create(file))
                    }
                    return@withContext encryptedFileItems
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
                    recyclerView.adapter = EncryptedFilesExpandableRecyclerAdapter(data)
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