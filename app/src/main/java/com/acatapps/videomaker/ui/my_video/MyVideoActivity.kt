package com.acatapps.videomaker.ui.my_video

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.acatapps.videomaker.R
import com.acatapps.videomaker.adapter.AllMyStudioAdapter
import com.acatapps.videomaker.base.BaseActivity
import com.acatapps.videomaker.models.MyStudioDataModel
import com.acatapps.videomaker.ui.edit_video.VideoEditSlideActivity
import com.acatapps.videomaker.ui.share_video.ShareVideoActivity
import com.acatapps.videomaker.ui.trim_video.TrimVideoActivity
import com.acatapps.videomaker.utils.*
import kotlinx.android.synthetic.main.activity_base_layout.*
import kotlinx.android.synthetic.main.activity_my_video.*
import kotlinx.android.synthetic.main.base_header_view.view.*
import java.io.File

class MyVideoActivity : BaseActivity() {
    override fun getContentResId(): Int = R.layout.activity_my_video

    private val mAllMyStudioAdapter = AllMyStudioAdapter()

    override fun initViews() {
        setRightButton(R.drawable.ic_delete_white) {
            Logger.e("delete")
            if (mAllMyStudioAdapter.getNumberItemSelected() < 1) {
                showToast(getString(R.string.nothing_item_selected))
                return@setRightButton
            }
            showYesNoDialog(getString(R.string.do_you_want_delete_items)) {
                deleteItemSelected()
            }
        }

        setSubRightButton(R.drawable.ic_check_all_none) {
            Logger.e("check all")
            var allItemChecked = true
            for (item in mAllMyStudioAdapter.itemList) {
                if (!item.checked && item.filePath.length > 5) {
                    allItemChecked = false
                    break
                }
            }
            Logger.e("allItemChecked = $allItemChecked")
            if (allItemChecked) {
                mAllMyStudioAdapter.setOffAll()
                headerView.subRightButton.setImageResource(R.drawable.ic_check_all_none)
            } else {
                selectAll()
                headerView.subRightButton.setImageResource(R.drawable.ic_check_all)
            }

        }

        hideButton()
        setScreenTitle(getString(R.string.my_studio))
        val colSize = 110 * Utils.density(this)
        val numberCols = Utils.screenWidth(this) / colSize
        allMyStudioListView.apply {
            adapter = mAllMyStudioAdapter
            layoutManager = GridLayoutManager(
                context,
                numberCols.toInt(),
                LinearLayoutManager.VERTICAL,
                false
            ).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (mAllMyStudioAdapter.getItemViewType(position) == R.layout.item_all_my_studio) {
                            1
                        } else {
                            numberCols.toInt()
                        }
                    }

                }
            }
        }
        //getAllMyStudioItem()
    }

    private var mSelectMode = false
    override fun initActions() {
        mAllMyStudioAdapter.onLongPress = {
            if (!mSelectMode) {
                openSelectMode()
            }
        }
        mAllMyStudioAdapter.onSelectChange = {
            Thread {

                val number = mAllMyStudioAdapter.getNumberItemSelected()
                val total = mAllMyStudioAdapter.getTotalItem()

                if (number == total) {
                    runOnUiThread {
                        headerView.subRightButton.setImageResource(R.drawable.ic_check_all)
                    }

                } else {
                    runOnUiThread {
                        headerView.subRightButton.setImageResource(R.drawable.ic_check_all_none)
                    }
                }
            }.start()
        }
        mAllMyStudioAdapter.onClickItem = {
            if (!mSelectMode)
                ShareVideoActivity.gotoActivity(this, it.filePath)
        }

        mAllMyStudioAdapter.onClickOpenMenu = { view, myStudioDataModel ->
            val popupMenu = PopupMenu(this@MyVideoActivity, view)
            popupMenu.menuInflater.inflate(R.menu.item_my_studio_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {

                    when (item?.itemId) {

                        R.id.delete -> {
                            onDeleteItem(myStudioDataModel.filePath)
                        }

                        R.id.edit -> {

                            val intent =
                                Intent(this@MyVideoActivity, VideoEditSlideActivity::class.java)
                            intent.putStringArrayListExtra(
                                "Video picked list",
                                arrayListOf(myStudioDataModel.filePath)
                            )
                            startActivity(intent)

                        }

                        R.id.trim -> {
                            TrimVideoActivity.gotoActivity(
                                this@MyVideoActivity,
                                myStudioDataModel.filePath
                            )


                        }
                        R.id.share -> {
                            shareVideoFile(myStudioDataModel.filePath)

                        }
                    }
                    popupMenu.dismiss()
                    return true
                }

            })
            popupMenu.show()
        }
    }

    private fun onDeleteItem(path: String) {
        showYesNoDialog(getString(R.string.do_you_want_delete_item)) {
            val file = File(path)
            if (file.exists()) {

                try {
                    file.delete()
                    mAllMyStudioAdapter.onDeleteItem(path)
                    updateEmptyIcon()
                    doSendBroadcast(path)
                } catch (e: Exception) {

                }


            }
        }

    }

    private fun openSelectMode() {
        mSelectMode = true
        mAllMyStudioAdapter.selectMode = true
        mAllMyStudioAdapter.notifyDataSetChanged()
        showButton()
    }

    private fun closeSelectMode() {
        mSelectMode = false
        mAllMyStudioAdapter.selectMode = false
        mAllMyStudioAdapter.notifyDataSetChanged()
        hideButton()
        mAllMyStudioAdapter.setOffAll()

    }

    private fun showButton() {
        showRightButton()
        showSubRightButton()
    }

    private fun hideButton() {
        hideRightButton()
        hideSubRightButton()
    }

    private fun selectAll() {
        mAllMyStudioAdapter.selectAll()
    }

    private fun deleteItemSelected() {
        showProgressDialog()
        Thread {
            val selectedItems = ArrayList<MyStudioDataModel>()
            for (item in mAllMyStudioAdapter.itemList) {
                if (item.checked && item.filePath.isNotEmpty()) {
                    selectedItems.add(item)
                }
            }
            for (item in selectedItems) {
                val file = File(item.filePath)
                file.delete()
                doSendBroadcast(item.filePath)
                runOnUiThread {
                    mAllMyStudioAdapter.onDeleteItem(item.filePath)
                }

            }

            runOnUiThread {
                updateEmptyIcon()
                closeSelectMode()
                dismissProgressDialog()
            }
        }.start()

    }

    private fun getAllMyStudioItem() {

        Thread {
            if (mAllMyStudioAdapter.itemCount > 0) {
                val deletePathList = ArrayList<String>()
                mAllMyStudioAdapter.itemList.forEachIndexed { index, myStudioDataModel ->

                    if (myStudioDataModel.filePath.length > 5 && !File(myStudioDataModel.filePath).exists()) {
                        deletePathList.add(myStudioDataModel.filePath)
                    }

                }

                runOnUiThread {
                    deletePathList.forEach {
                        mAllMyStudioAdapter.onDeleteItem(it)
                    }
                }

            } else {
                runOnUiThread {
                    showProgressDialog()
                }
                val folder = File(Utils.myStuioFolderPath)
                val myStudioDataList = ArrayList<MyStudioDataModel>()
                if (folder.exists() && folder.isDirectory) {
                    for (item in folder.listFiles()) {
                        try {
                            val duration = Utils.getVideoDuration(item.absolutePath)
                            if (item.exists())
                                myStudioDataList.add(
                                    MyStudioDataModel(
                                        item.absolutePath,
                                        item.lastModified(),
                                        duration
                                    )
                                )

                        } catch (e: java.lang.Exception) {
                            item.delete()
                            doSendBroadcast(item.absolutePath)
                            continue
                        }

                    }
                }
                myStudioDataList.sort()

                runOnUiThread {
                    mAllMyStudioAdapter.setItemList(myStudioDataList)
                    if (myStudioDataList.size > 0) {
                        mAllMyStudioAdapter.notifyDataSetChanged()
                        iconNoItem.visibility = View.GONE
                    } else {
                        iconNoItem.visibility = View.VISIBLE
                    }
                    dismissProgressDialog()
                }
            }


        }.start()


    }

    private fun updateEmptyIcon() {
        Thread {
            val total = mAllMyStudioAdapter.getTotalItem()
            if (total <= 0) {
                runOnUiThread {
                    iconNoItem.visibility = View.VISIBLE
                    allMyStudioListView.visibility = View.GONE
                }

            } else {
                runOnUiThread {
                    iconNoItem.visibility = View.GONE
                    allMyStudioListView.visibility = View.VISIBLE
                }

            }
        }.start()
    }

    override fun onBackPressed() {
        if (mYesNoDialogShowing) {
            dismissYesNoDialog()
            return
        }
        if (mSelectMode) {
            closeSelectMode()
        } else {
            super.onBackPressed()
        }
    }


    override fun onResume() {
        super.onResume()
        getAllMyStudioItem()
    }

}
