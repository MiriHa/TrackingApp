package com.example.trackingapp.activity.permissions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.R
import com.example.trackingapp.databinding.FragmentPermissionHolderBinding
import com.example.trackingapp.util.PermissionManager
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.SharedPrefManager
import com.example.trackingapp.util.navigate

class PermissionHolderFragment : Fragment() {

    private val TAG = "PermissionHolderFragment"

    private lateinit var binding: FragmentPermissionHolderBinding
    private lateinit var viewModel: PermissionHolderViewModel
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPermissionHolderBinding = FragmentPermissionHolderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, PermissionHolderViewModelFactory())[PermissionHolderViewModel::class.java]

        SharedPrefManager.init(mContext.applicationContext)
        getPermissionList()
        viewModel.initPermissionIterator(this.activity)

        binding.frameLayoutPermissionContainer.setOnClickListener {
            // This ClickListener catches clicks that are forwarded to the background
        }


        if (viewModel.currentPermission == null) {
            Log.d(TAG, "showNextPermissionFragment()")
            showNextPermissionFragment()
        }

        return binding.root
    }

    private fun getPermissionList() {
        viewModel.permissions = arrayOf(
            PermissionView.PERMISSIONS.takeIf { !PermissionManager.checkPermission(PermissionView.PERMISSIONS, this.activity) },
            PermissionView.ACCESSIBILITY_SERVICE.takeIf { !PermissionManager.checkPermission(PermissionView.ACCESSIBILITY_SERVICE, this.activity) },
            PermissionView.NOTIFICATION_LISTENER.takeIf { !PermissionManager.checkPermission(PermissionView.NOTIFICATION_LISTENER, this.activity) },
            PermissionView.USAGE_STATS.takeIf { !PermissionManager.checkPermission(PermissionView.USAGE_STATS, this.activity) }
        ).filterNotNull()
        Log.d(TAG, "PermissionListsize: ${viewModel.permissions.size}")
    }

    private fun showNextPermissionFragment() {
        val nextPermissionFragment = nextPermissionFragment() as? PermissionFragment
        Log.d(TAG, "showNextPermissionFragment")

        if (nextPermissionFragment != null) {
            swapContent(nextPermissionFragment)
        } else {
            //viewModel.userFinishedOnboarding()
            viewModel.reset()
            navigate(to = ScreenType.HomeScreen, from = ScreenType.Permission)
        }
    }

    private fun swapContent(nextPermissionFragment: PermissionFragment) {
        Log.d(TAG, "swap Content")
//        nextPermissionFragment.apply {
//            exitTransition = Fade().also {
//                it.duration = AnimationUtil.getScaledAnimationDuration(applicationContext, EXIT_ANIMATION_DURATION)
//            }
//
//            enterTransition = Fade().also {
//                it.duration = AnimationUtil.getScaledAnimationDuration(applicationContext, ENTER_ANIMATION_DURATION)
//                it.startDelay = 0
//            }
//        }

        childFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.frameLayout_permission_container, nextPermissionFragment)
        }
    }

    private fun nextPermissionFragment(): Fragment? {
        val permission = viewModel.nextPermissionToAsk()
        Log.d(TAG, "nextPermissionFragment ${permission?.name}")

        return permission?.let {
            PermissionFragment.newInstance(
                PermissionViewModel(viewModel, it) { showNextPermissionFragment() }
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}