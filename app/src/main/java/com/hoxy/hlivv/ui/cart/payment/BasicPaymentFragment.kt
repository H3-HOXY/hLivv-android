package com.hoxy.hlivv.ui.cart.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hoxy.hlivv.databinding.BasicPaymentBottomSheetBinding
import com.hoxy.hlivv.domain.Utils.setFormattedIntToTextView
import com.hoxy.hlivv.domain.Utils.setFormattedNumberToTextView

/**
 * @author 반정현
 */
class BasicPaymentFragment : Fragment() {
    private var _binding: BasicPaymentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PaymentViewModel
    lateinit var listener: OnPaymentButtonListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BasicPaymentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(PaymentViewModel::class.java)

        viewModel.orderTotalPrice.observe(viewLifecycleOwner) { total ->
            setFormattedNumberToTextView(total, binding.finalPaymentPrice)
        }

        viewModel.numberOfProductTypes.observe(viewLifecycleOwner) { cnt ->
            binding.gotoOrder.isEnabled = cnt != 0
            setFormattedIntToTextView(cnt, binding.numberOfOrderProduct)
        }


        binding.buttonUp.setOnClickListener {
            (parentFragment as? OnPaymentButtonListener)?.onPaymentExpanded()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}