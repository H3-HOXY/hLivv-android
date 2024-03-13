package com.hoxy.hlivv.ui.liveqr.barcode

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hoxy.hlivv.R
import com.hoxy.hlivv.ui.liveqr.camera.WorkflowModel
/**
 * @author 반정현
 */
class BarcodeResultFragment(private val workflowModel: WorkflowModel) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val view = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup)

        val arguments = arguments
        val barcodeFieldList: ArrayList<BarcodeField> =
            if (arguments?.containsKey(ARG_BARCODE_FIELD_LIST) == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arguments.getParcelableArrayList(
                        ARG_BARCODE_FIELD_LIST,
                        BarcodeField::class.java
                    ) ?: ArrayList()
                } else {
                    arguments.getParcelableArrayList(ARG_BARCODE_FIELD_LIST) ?: ArrayList()
                }
            } else {
                ArrayList()
            }

        view.findViewById<RecyclerView>(R.id.barcode_field_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = BarcodeFieldAdapter(barcodeFieldList)
        }

        return view
    }


    override fun onDismiss(dialogInterface: DialogInterface) {
        activity?.let {
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        }
        super.onDismiss(dialogInterface)
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {

        private const val TAG = "BarcodeResultFragment"
        private const val ARG_BARCODE_FIELD_LIST = "arg_barcode_field_list"

        fun show(
            fragmentManager: FragmentManager,
            workflowModel: WorkflowModel,
            barcodeFieldArrayList: ArrayList<BarcodeField>
        ) {
            val barcodeResultFragment = BarcodeResultFragment(workflowModel)
            barcodeResultFragment.arguments = Bundle().apply {
                putParcelableArrayList(ARG_BARCODE_FIELD_LIST, barcodeFieldArrayList)
            }
            barcodeResultFragment.show(fragmentManager, TAG)
        }

//        fun dismiss(fragmentManager: FragmentManager) {
//            (fragmentManager.findFragmentByTag(TAG) as BarcodeResultFragment?)?.dismiss()
//        }
    }
}