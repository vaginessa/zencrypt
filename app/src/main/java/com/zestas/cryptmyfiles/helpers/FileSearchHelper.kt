package com.zestas.cryptmyfiles.helpers

import com.zestas.cryptmyfiles.adapters.DecryptedFilesExpandableRecyclerAdapter
import com.zestas.cryptmyfiles.adapters.EncryptedFilesExpandableRecyclerAdapter
import com.zestas.cryptmyfiles.dataItemModels.FileItem
import java.util.*
import kotlin.collections.ArrayList

class FileSearchHelper {

    companion object {
        fun filterResults(data: ArrayList<FileItem>, filterText: String, adapter: EncryptedFilesExpandableRecyclerAdapter) {
            // creating a new array list to filter our data.
            val filteredList: ArrayList<FileItem> = ArrayList()

            //check if empty first
            if (data.size == 0) return

            // running a for loop to compare elements.
            for (item in data) {
                // checking if the entered string matched with any item of our recycler view.
                if (item.getName().lowercase(Locale.ROOT).contains(filterText.lowercase(Locale.ROOT))) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredList.add(item)
                }
            }
            //if (filteredList.isEmpty()) { ... }
            //...
            //else
            adapter.filterFileList(filteredList)
        }

        fun filterResults(data: ArrayList<FileItem>, filterText: String, adapter: DecryptedFilesExpandableRecyclerAdapter) {
            // creating a new array list to filter our data.
            val filteredList: ArrayList<FileItem> = ArrayList()

            // running a for loop to compare elements.
            for (item in data) {
                // checking if the entered string matched with any item of our recycler view.
                if (item.getName().lowercase(Locale.ROOT).contains(filterText.lowercase(Locale.ROOT))) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    filteredList.add(item)
                }
            }
            //if (filteredList.isEmpty()) { ... }
            //...
            //else
            adapter.filterFileList(filteredList)
        }
    }
}