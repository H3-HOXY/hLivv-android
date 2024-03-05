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

//class CartFragment : Fragment(),OnSelectedItemsChanged {
//    private var _binding: FragmentCartBinding? = null
//    private lateinit var cartAdapter: CartAdapter
//    private var currentPage = 0
//    private val pageSize = 2
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentCartBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        loadNextPage()
//        cartAdapter = CartAdapter(mutableListOf(),this@CartFragment)
//        val recyclerView: RecyclerView=binding.cartRecyclerView
//        recyclerView.layoutManager=LinearLayoutManager(context)
//        recyclerView.adapter = cartAdapter
//        binding.totalCheck.setOnClickListener{
//            val isChecked=binding.totalCheck.isChecked
//            cartAdapter.selectAllItems(isChecked)
//        }
//
////        CoroutineScope(Dispatchers.IO).launch {
////            try{
////                val memberControllerApi=MemberControllerApi()
////                val cartList=memberControllerApi.getAllCarts().toList()
////                withContext(Dispatchers.Main) {
////                    val cartAdapter=CartAdapter(cartList)
////                    recyclerView.adapter=cartAdapter
////                }
////            }
////            catch (e:Exception){
////
////            }
////        }
//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                val visibleItemCount = layoutManager.childCount
//                val totalItemCount = layoutManager.itemCount
//                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
//
//                if (visibleItemCount + firstVisibleItem >= totalItemCount && firstVisibleItem >= 0) {
//                    loadNextPage()
//                }
//            }
//        })
//    }
//
//    private fun loadNextPage() {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val memberControllerApi = MemberControllerApi()
//                val pageCartDto = memberControllerApi.getCarts(currentPage, pageSize)
//                withContext(Dispatchers.Main) {
//                    val newItems = pageCartDto.content ?: emptyArray()
//                    cartAdapter.addItems(newItems.toList())
//                    currentPage++
//                }
//            } catch (e:Exception){
//
//            }
//        }
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    override fun onSelectedItemsChanged(selectedItems: List<Long>) {
//        TODO("Not yet implemented")
//    }
//}
//class CartFragment : Fragment(), OnSelectedItemsChanged {
//    private var _binding: FragmentCartBinding? = null
//    private lateinit var cartAdapter: CartAdapter
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentCartBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val memberControllerApi = MemberControllerApi()
//
//                // 전체 카트 리스트 get
//                val cartList = memberControllerApi.getAllCarts().toList()
//
//                withContext(Dispatchers.Main) {
//                    // RecyclerView를 설정하고 전체 카트 리스트를 어댑터에 전달
//                    setupRecyclerView(cartList)
//                }
//            } catch (e: Exception) {
//                // 예외 처리를 여기에 추가하세요.
//            }
//        }
//    }
//
//    private fun setupRecyclerView(cartList: List<CartDto>) {
//        // 카트 어댑터를 생성하고 RecyclerView에 설정
//        cartAdapter = CartAdapter(cartList, this@CartFragment)
//        val recyclerView: RecyclerView = binding.cartRecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = cartAdapter
//
//        // 전체 선택 체크박스에 대한 리스너를 설정
//        binding.totalCheck.setOnClickListener {
//            val isChecked = binding.totalCheck.isChecked
//            cartAdapter.selectAllItems(isChecked)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    override fun onSelectedItemsChanged(selectedItems: List<CartDto>) {
//        Log.d("CartList","$selectedItems")
//    }
//}

class CartFragment : Fragment(), OnPaymentButtonListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private var listener: OnPaymentButtonListener? = null

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