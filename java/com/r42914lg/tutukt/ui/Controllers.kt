package com.r42914lg.tutukt.ui

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.r42914lg.tutukt.R
import com.r42914lg.tutukt.model.TuTuViewModel
import com.r42914lg.tutukt.utils.NetworkTracker
import com.r42914lg.tutukt.utils.PermissionChecker

class CoreController(private val vm: TuTuViewModel, private val activity: MainActivity) {
    private val networkTracker = NetworkTracker(activity, vm)
    private val permissionChecker = PermissionChecker(activity, vm)

    fun initCoreView(iCoreView: ICoreView) {

        vm.getLiveToolBarTitle().observe(activity, iCoreView::showNetworkStatus)
        vm.getShowFabLiveData().observe(activity, iCoreView::showFabIcon)
        vm.getToastLiveData().observe(activity, iCoreView::showToast)
        vm.getTerminateDialogEventMutableLiveData().observe(activity, iCoreView::showTerminateDialog)

        vm.getProgressBarFlagLiveData().observe(activity) { aBoolean ->
            when {
                aBoolean -> iCoreView.startProgressOverlay()
                else -> iCoreView.stopProgressOverlay()
            }
        }
    }

    fun registerNetworkTracker() = networkTracker.register()
    fun unregisterNetworkTracker() = networkTracker.unregister()
}

class DetailsController(private val vm: TuTuViewModel, private val fragment: Fragment) {
    fun initDetailsView(iDetailsView: IDetailsView) {
        vm.showFab(false)
        vm.getCategoryDetailedLiveData().observe(fragment) { iDetailsView.showDetails(it) }
    }
}

class FeedController(private val vm: TuTuViewModel, private val fragment: Fragment) {
    fun initFeedView(iFeedView: IFeedView) {
        vm.showFab(true)
        vm.getCategoriesLiveData().observe(fragment) {
            iFeedView.showFeed(it)
        }
        vm.getNavigateToDetailedViewLiveData().observe(fragment) {
            if (it) {
                vm.getNavigateToDetailedViewLiveData().value = false
                NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment)
            }
        }
        vm.requestFeedUpdate(false)
    }

    fun onDetailsRequested(positionInAdapter: Int) {
        vm.setCategoryId(positionInAdapter)
        vm.requestDetailsUpdate()
    }
}
