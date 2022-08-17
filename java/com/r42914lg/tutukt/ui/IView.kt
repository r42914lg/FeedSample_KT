package com.r42914lg.tutukt.ui

import com.r42914lg.tutukt.domain.Category
import com.r42914lg.tutukt.domain.CategoryDetailed
import com.r42914lg.tutukt.model.TerminateDialogText

interface ICoreView {
    fun showNetworkStatus(text: String)
    fun showFabIcon(flag: Boolean)
    fun showToast(msg: String)
    fun startProgressOverlay()
    fun stopProgressOverlay()
    fun showTerminateDialog(terminateDialogText: TerminateDialogText)
}

interface IDetailsView {
    fun showDetails(categoryDetailed: CategoryDetailed)
}

interface IFeedView {
    fun showFeed(clues: List<Category>)
}