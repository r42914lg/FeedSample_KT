package com.r42914lg.tutukt.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.r42914lg.tutukt.R
import com.r42914lg.tutukt.domain.Category
import com.r42914lg.tutukt.domain.CategoryDetailed
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.MessageFormat


class TuTuViewModel(app: Application) : AndroidViewModel(app) {

    private val categoryListLiveData: MutableLiveData<List<Category>> = MutableLiveData()
    private val categoryDetailedLiveData: MutableLiveData<CategoryDetailed> = MutableLiveData()
    private val progressBarFlagLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val liveToolBarTitle: MutableLiveData<String> = MutableLiveData()
    private val showFabLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val toastLiveData: MutableLiveData<String> = MutableLiveData()
    private val terminateDialogEventMutableLiveData: MutableLiveData<TerminateDialogText> = MutableLiveData()
    private val navigateToDetailedViewLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private var categoryId = 0
    private var offlineCategoriesInFeed = false

    private var isOnline = false
    val checkIfOnline: Boolean
        get() {
            return isOnline
        }

    private val repository = RepoWrapper(this, APIRepository(),LocalRepository(app))

    init {
        liveToolBarTitle.value = MessageFormat.format(
            "{0}{1}{2}",
            getApplication<Application>().applicationContext.getString(R.string.app_name),
            " - ",
            getApplication<Application>().applicationContext.getString(R.string.status_offline)
        )
    }

    fun setCategoryId(positionInAdapter: Int) {
        categoryId = categoryListLiveData.value?.get(positionInAdapter)?.id ?:0
    }

    fun requestFeedUpdate(requestNewItems: Boolean) {
        val feedPopulated: Boolean = categoryListLiveData.value?.isNotEmpty() ?:false
        if (!requestNewItems && feedPopulated) {
            return
        }
        doRequestFeedUpdate()
    }

    private fun doRequestFeedUpdate() {
        progressBarFlagLiveData.value = true
        viewModelScope.launch {
            val categoryList = repository.getCategories()

            categoryList?.apply {
                offlineCategoriesInFeed = false
                pushCategoriesToUI(this)
                repository.saveCategoriesToFile(this)
            }

            if (categoryList.isNullOrEmpty()) {
                showErrorMsgOnUI()
            }
        }
    }

    fun requestDetailsUpdate() {
        viewModelScope.launch {
            val categoryDetailed = repository.getCategoryDetails(categoryId)

            if (categoryDetailed != null && categoryDetailed.id == categoryId) {
                repository.saveDetailsToFile(categoryDetailed)
                categoryDetailedLiveData.value = categoryDetailed
                navigateToDetailedViewLiveData.value = true
                progressBarFlagLiveData.setValue(false)
            } else
                showErrorMsgOnUI()
        }
    }

    private fun showErrorMsgOnUI() {
        val text = getApplication<Application>().applicationContext
            .getString(
                if (isOnline) R.string.retrofit_error else R.string.local_storage_error)

        toastLiveData.value = text
        progressBarFlagLiveData.value = false
    }

    private fun pushCategoriesToUI(categoryList: List<Category>) {
        categoryListLiveData.value = categoryList
        progressBarFlagLiveData.value = false
    }

    fun onPermissionsCheckFailed() {
        terminateDialogEventMutableLiveData.value = TerminateDialogText(
            getApplication<Application>().getString(R.string.dialog_terminate_no_permissions_title),
            getApplication<Application>().getString(R.string.dialog_terminate_no_permissions_text)
        )
    }

    fun setNetworkStatus(_isOnline: Boolean) {
        isOnline = _isOnline
        liveToolBarTitle.postValue(
            MessageFormat.format(
                "{0}{1}{2}",
                getApplication<Application>().applicationContext.getString(R.string.app_name),
                " - ",
                getApplication<Application>().applicationContext
                    .getString(if (isOnline) R.string.status_online else R.string.status_offline)
            )
        )
        if (isOnline && offlineCategoriesInFeed) {
            doRequestFeedUpdate()
        }
    }

    fun showFab(flag: Boolean) {
        showFabLiveData.value = flag
    }

    fun getLiveToolBarTitle() = liveToolBarTitle
    fun getShowFabLiveData() = showFabLiveData
    fun getToastLiveData() = toastLiveData
    fun getTerminateDialogEventMutableLiveData() = terminateDialogEventMutableLiveData
    fun getProgressBarFlagLiveData() = progressBarFlagLiveData
    fun getCategoryDetailedLiveData() = categoryDetailedLiveData
    fun getCategoriesLiveData() = categoryListLiveData
    fun getNavigateToDetailedViewLiveData() = navigateToDetailedViewLiveData
}