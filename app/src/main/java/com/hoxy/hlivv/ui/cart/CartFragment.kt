package com.hoxy.hlivv.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hoxy.hlivv.R
import com.hoxy.hlivv.databinding.FragmentCartBinding
import com.hoxy.hlivv.ui.cart.payment.BasicPaymentFragment
import com.hoxy.hlivv.ui.cart.payment.ExpandedPaymentFragment
import com.hoxy.hlivv.ui.cart.payment.OnPaymentButtonListener

/**
 * @author 반정현
 */
class CartFragment : Fragment(), OnPaymentButtonListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .add(R.id.cart_container, BasicCartFragment())
            .add(R.id.payment_container, BasicPaymentFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onPaymentExpanded() {
        childFragmentManager.beginTransaction()
            .replace(R.id.payment_container, ExpandedPaymentFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onPaymentCollapsed() {
        childFragmentManager.beginTransaction()
            .replace(R.id.payment_container, BasicPaymentFragment())
            .addToBackStack(null)
            .commit()
    }


}