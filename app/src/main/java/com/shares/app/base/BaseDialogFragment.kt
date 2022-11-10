package com.shares.app.base


import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.shares.app.extension.toast
import com.shares.app.network.Status



open class BaseDialogFragment : DialogFragment(),BaseOwner {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBaseModel()?.baseEvent?.observe(viewLifecycleOwner, { event ->
            when (event) {
                is BaseModel.BaseViewModelEvent.NetworkEvent->{
                    when(event.resource.status){
                        Status.LOADING->{

                        }
                        Status.ERROR->{
                            requireContext().toast(event.resource.message.orEmpty())
                        }
                    }
                }
                is BaseModel.BaseViewModelEvent.ToastIntEvent->{
                    requireContext().toast(resources.getString(event.id))
                }
                is BaseModel.BaseViewModelEvent.ToastStrEvent->{
                    requireContext().toast(event.str)
                }
                BaseModel.BaseViewModelEvent.NavigateUpEvent->{
                    navigateUp()
                }
            }
        })
    }



    override fun getBaseModel():BaseModel? {
        return null
    }

    override fun navigateUp() {

    }

    override fun navigate(id: Int) {

    }


}