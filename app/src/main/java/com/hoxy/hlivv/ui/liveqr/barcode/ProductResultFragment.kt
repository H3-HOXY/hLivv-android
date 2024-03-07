package com.hoxy.hlivv.ui.liveqr.barcode

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hoxy.hlivv.R
import com.hoxy.hlivv.ui.liveqr.barcode.product.OnDialogConfirmedListener
import com.hoxy.hlivv.ui.liveqr.barcode.product.Product
import com.hoxy.hlivv.ui.liveqr.barcode.product.ProductAdapter
import com.hoxy.hlivv.ui.liveqr.camera.WorkflowModel

class ProductResultFragment(private val workflowModel: WorkflowModel) : BottomSheetDialogFragment(),
    OnDialogConfirmedListener {


    override fun onDialogConfirmed() {
        val navController = findNavController()
        navController.navigate(R.id.navigation_cart)
        dismiss()
    }

    override fun onDialogCancel() {
        dismiss()
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val view = layoutInflater.inflate(R.layout.product_bottom_sheet, viewGroup)
        //view.background = ContextCompat.getDrawable(requireContext(), R.drawable.bottom_product_background)

        val arguments = arguments
        val productList: ArrayList<Product> =
            if (arguments?.containsKey(ARG_PRODUCT_FIELD_LIST) == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arguments.getParcelableArrayList(ARG_PRODUCT_FIELD_LIST, Product::class.java)
                        ?: ArrayList()
                } else {
                    arguments.getParcelableArrayList(ARG_PRODUCT_FIELD_LIST) ?: ArrayList()
                }
            } else {
                ArrayList()
            }

        view.findViewById<RecyclerView>(R.id.product_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = ProductAdapter(productList, this@ProductResultFragment)
        }

        return view
    }


    override fun onDismiss(dialogInterface: DialogInterface) {
        Log.d("Fragment", "Setting WorkflowState to DETECTING")
        activity?.let {
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        }
        super.onDismiss(dialogInterface)
    }

    override fun onPause() {
        super.onPause()
        Log.d("Fragment", "onPause")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Fragment", "onDestroyView")
        dismiss()
    }

    companion object {

        private const val TAG = "ProductResultFragment"
        private const val ARG_PRODUCT_FIELD_LIST = "arg_product_field_list"

        fun show(
            fragmentManager: FragmentManager,
            workflowModel: WorkflowModel,
            productList: ArrayList<Product>
        ) {
            val productResultFragment = ProductResultFragment(workflowModel)
            productResultFragment.arguments = Bundle().apply {
                putParcelableArrayList(ARG_PRODUCT_FIELD_LIST, productList)
            }
            productResultFragment.show(fragmentManager, TAG)
        }

//        fun dismiss(fragmentManager: FragmentManager) {
//            (fragmentManager.findFragmentByTag(TAG) as BarcodeResultFragment?)?.dismiss()
//        }
    }
}