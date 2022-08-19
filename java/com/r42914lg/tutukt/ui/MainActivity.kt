package com.r42914lg.tutukt.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.r42914lg.tutukt.R
import com.r42914lg.tutukt.databinding.ActivityMainBinding
import com.r42914lg.tutukt.model.TerminateDialogText
import com.r42914lg.tutukt.model.TuTuViewModel

class MainActivity : AppCompatActivity(), ICoreView {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var progressOverlay: View
    private lateinit var networkStatus: TextView

    private val tuTuViewModel: TuTuViewModel by viewModels()
    private val controller: CoreController by lazy { CoreController(tuTuViewModel, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        controller.initCoreView(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        progressOverlay = findViewById(R.id.progress_overlay)
        networkStatus = findViewById(R.id.status)

        binding.fab.setOnClickListener {
            tuTuViewModel.requestFeedUpdate(true)
        }
    }

    override fun onResume() {
        super.onResume()
        controller.registerNetworkTracker()
    }

    override fun onPause() {
        super.onPause()
        controller.unregisterNetworkTracker()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun showNetworkStatus(text: String) {
        networkStatus.text = text
    }

    override fun showFabIcon(flag: Boolean) {
        binding.fab.visibility = if (flag) View.VISIBLE else View.INVISIBLE
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun startProgressOverlay() {
//        animateView(progressOverlay, View.VISIBLE, 0.6f)
    }

    override fun stopProgressOverlay() {
//        animateView(progressOverlay, View.GONE, 0f)
    }

    override fun showTerminateDialog(terminateDialogText: TerminateDialogText) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(terminateDialogText.title)
        dialog.setMessage(terminateDialogText.text)
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { it, _ ->
            finish()
            it.cancel()
        }
        dialog.setOnDismissListener {
            finish()
            it.cancel()
        }
        dialog.show()
    }

    private fun animateView(view: View, toVisibility: Int, toAlpha: Float) {
        val show = toVisibility == View.VISIBLE
        if (show) {
            view.alpha = 0f
        }

        view.visibility = View.VISIBLE
        view.animate()
            .setDuration(200)
            .alpha(if (show) toAlpha else 0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = toVisibility
                }
            })
    }
}