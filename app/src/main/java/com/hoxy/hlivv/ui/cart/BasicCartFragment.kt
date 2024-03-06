package com.hoxy.hlivv.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.apis.MemberControllerApi
import com.hoxy.hlivv.data.infrastructure.ClientException
import com.hoxy.hlivv.data.models.CartDto
import com.hoxy.hlivv.databinding.FragmentBasicCartBinding
import com.hoxy.hlivv.domain.Utils.showErrorDialog
import com.hoxy.hlivv.ui.cart.payment.PaymentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
class BasicCartFragment : Fragment(), OnSelectedItemsChanged {
    private var _binding: FragmentBasicCartBinding? = null
    private lateinit var cartAdapter: CartAdapter
    private lateinit var viewModel: PaymentViewModel
    private val memberControllerApi = MemberControllerApi()
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        viewModel.setSubTotalPrice(0L)
        viewModel.setDiscountTotalPrice(0L)
        viewModel.setOrderTotalPrice(0L)
        viewModel.setNumberOfProductTypes(0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicCartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(PaymentViewModel::class.java)
        val navController = findNavController()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val memberControllerApi = MemberControllerApi()

                // 전체 카트 리스트 get
                val cartList = memberControllerApi.getAllCarts().toMutableList()

                withContext(Dispatchers.Main) {
                    // RecyclerView를 설정하고 전체 카트 리스트를 어댑터에 전달
                    setupRecyclerView(cartList)
                }
            } catch (e: ClientException) {
                val response = JSONObject(e.message)
                val status = response.getInt("status")
                val message = response.getString("message")
                if (status == 401 || status == 400) {
                    try {
                        navController.navigate(R.id.navigation_login)
                    } catch (e: Exception) {
                        Log.d("Login", "ERROR", e)
                    }

                } else {
                    showErrorDialog(message, requireContext())
                }
                Log.d("Login", "ERROR", e)
            } catch (e: Exception) {
                showErrorDialog("잠시 후 다시 시도해주세요.", requireContext())
            }
        }

        // 전체 선택 체크박스에 대한 리스너를 설정
        binding.totalCheck.setOnClickListener {
            val isChecked = binding.totalCheck.isChecked
            cartAdapter.selectAllItems(isChecked)
        }

        binding.deleteList.setOnClickListener{
            cartAdapter.deleteCartList()
        }
    }

    private fun setupRecyclerView(cartList: MutableList<CartDto>) {
        // 카트 어댑터를 생성하고 RecyclerView에 설정
        val recyclerView: RecyclerView = binding.cartRecyclerView
        cartAdapter = CartAdapter(cartList, this@BasicCartFragment, viewModel)
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = cartAdapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSelectedItemsChanged(selectedItems: List<Long>) {


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cartDtos: Array<CartDto> =
                    memberControllerApi.getSelectedItems(selectedItems.toTypedArray())
                withContext(Dispatchers.Main) {
                    var numberOfArList = 0
                    var subTotal = 0L
                    var discountTotal = 0L
                    var deliveryTotal = 0L

                    cartDtos.forEach { item ->
                        val unitPrice = item.unitPrice ?: 0L
                        val discountUnitPrice = item.discountUnitPrice ?: 0L
                        val cartQty = item.cartQty ?: 0
                        if (item.arSupported == true) {
                            numberOfArList += 1
                        }

                        subTotal += cartQty * unitPrice
                        discountTotal += cartQty * discountUnitPrice
                    }

                    val count = cartDtos.size

                    viewModel.setNumberOfArProduct(numberOfArList)
                    viewModel.setSubTotalPrice(subTotal)
                    viewModel.setDiscountTotalPrice(subTotal - discountTotal)
                    viewModel.setOrderTotalPrice(discountTotal + deliveryTotal)
                    viewModel.setNumberOfProductTypes(count)
                }
            } catch (e: Exception) {
                // 예외 처리를 수행
            }
        }
    }
}
