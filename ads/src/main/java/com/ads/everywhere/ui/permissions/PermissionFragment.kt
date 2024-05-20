package com.ads.everywhere.ui.permissions

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.Keep
import androidx.recyclerview.widget.RecyclerView
import com.ads.everywhere.R
import com.ads.everywhere.ui.base.BaseFragment
import com.ads.everywhere.util.ext.safeNavigate
import org.koin.androidx.viewmodel.ext.android.viewModel

@Keep
class PermissionFragment : BaseFragment(R.layout.fragment_permissions) {



    private val viewModel by viewModel<PermissionViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val status:TextView = view.findViewById(R.id.status)
        val rvPermissions:RecyclerView = view.findViewById(R.id.rvPermissions)
        val adapter = PermissionsAdapter(viewModel)
        rvPermissions.adapter = adapter

        viewModel.finish.observe(viewLifecycleOwner){
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
        }
        viewModel.statusText.observe(viewLifecycleOwner){
            status.text = getString(it.res, it.current , it.total)
        }
        viewModel.showDestination.observe(viewLifecycleOwner){
            safeNavigate(it)
        }
        viewModel.statusVisible.observe(viewLifecycleOwner){
            status.visibility = if(it) View.VISIBLE else View.GONE
        }
        viewModel.permissionList.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }










}