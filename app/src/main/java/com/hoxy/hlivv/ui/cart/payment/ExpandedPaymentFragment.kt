package com.hoxy.hlivv.ui.cart.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hoxy.hlivv.databinding.PaymentBottomSheetBinding
import com.hoxy.hlivv.domain.Utils.setFormattedIntToTextView
import com.hoxy.hlivv.domain.Utils.setFormattedNumberToTextView

class ExpandedPaymentFragment : Fragment() {
    private var _binding: PaymentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PaymentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(PaymentViewModel::class.java)

        viewModel.orderTotalPrice.observe(viewLifecycleOwner) { total ->
            setFormattedNumberToTextView(total, binding.finalPaymentPrice)
        }

        viewModel.subTotalPrice.observe(viewLifecycleOwner) { total ->
            setFormattedNumberToTextView(total, binding.totalPaymentPrice)
        }

        viewModel.discountTotalPrice.observe(viewLifecycleOwner) { total ->
            setFormattedNumberToTextView(total, binding.discountPaymentPrice)
        }

        viewModel.numberOfProductTypes.observe(viewLifecycleOwner) { cnt ->
            binding.gotoOrder.isEnabled = cnt != 0
            setFormattedIntToTextView(cnt, binding.numberOfOrderProduct)
        }

        viewModel.numberOfArProduct.observe(viewLifecycleOwner) { cnt ->
            binding.gotoAr.isEnabled = cnt > 0
        }

        binding.buttonDown.setOnClickListener {
            (parentFragment as? OnPaymentButtonListener)?.onPaymentCollapsed()
        }

        /**
         * "gotoAr" 버튼에 대한 클릭 리스너를 설정하여 Unity로 이동
         */

        binding.gotoAr.setOnClickListener{
            // 여기에 currentSelectedItems를 유니티로 전달하는 코드를 작성해주세요
            val currentSelectedItems=viewModel.selectedItems.value
            Log.d("ClickedItems", currentSelectedItems.toString())
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}