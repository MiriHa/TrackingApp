package com.example.trackingapp.activity.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.trackingapp.databinding.FragmentPermissionBinding
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.PermissionManager

class PermissionFragment() : Fragment() {

    private val TAG = "PERMISSIONFRAGMENT"

    private lateinit var binding: FragmentPermissionBinding
    lateinit var viewModel: PermissionViewModel
    private lateinit var mContext: Context

    lateinit var permissionView: PermissionView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)

        permissionView = viewModel.permission

        binding.imageviewPermissionIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, permissionView.backgroundResourceId, null))
        binding.textViewPermissionFragmentTitle.text = mContext.getString(permissionView.titleLabelResourceId)
        binding.textViewPermissionFragmentDescription.text = mContext.getString(permissionView.descriptionLabelResourceId)
        binding.buttonPermissionFragent.text = mContext.getString(permissionView.primaryButtonLabelResourceId)

        binding.buttonPermissionFragent.setOnClickListener {
            continueClicked()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsOrProceed()
    }

    private fun checkPermissionsOrProceed(){
        Log.d(TAG, "checkPermissionOrProceed")
        this.activity?.let {
            val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)
            when (viewModel.permission) {
                PermissionView.PERMISSIONS -> {
                    if(managePermissions.arePermissionsGranted() == PackageManager.PERMISSION_GRANTED){
                        viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                    }
                    //TODO check what happens when not?
                }
                PermissionView.NOTIFICATION_LISTENER -> {
                    if (Settings.Secure.getString(it.contentResolver, "enabled_notification_listeners")
                            .contains(it.applicationContext.packageName)){
                        viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                    }
                }
                PermissionView.ACCESSIBILITY_SERVICE -> {
                    if(managePermissions.accessibilityServiceEnabled() == 1){
                        viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                    }
                }
            }
        }
    }

    private fun continueClicked() {
        Log.d(TAG, "continueClicked")
        this.activity?.let {
            val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)

            when (viewModel.permission) {
                PermissionView.PERMISSIONS -> {
                  //  managePermissions.checkPermissions()
                    if(managePermissions.checkPermissions()){
                        checkPermissionsOrProceed()
                    }
                }
                PermissionView.NOTIFICATION_LISTENER -> {
                    //managePermissions.checkForNotificationListenerPermissionEnabled()
                    if(managePermissions.checkForNotificationListenerPermissionEnabled()) {
                        checkPermissionsOrProceed()
                    }
                }
                PermissionView.ACCESSIBILITY_SERVICE -> {
                    //managePermissions.checkAccessibilityPermission()
                   if(managePermissions.checkAccessibilityPermission()){
                       checkPermissionsOrProceed()
                   }
                }
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        fun newInstance(viewModel: PermissionViewModel): PermissionFragment {
            return PermissionFragment().apply { this.viewModel = viewModel }
        }
    }
}