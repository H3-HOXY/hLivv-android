package com.hoxy.hlivv.ui.cart

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.apis.CartControllerApi
import com.hoxy.hlivv.data.apis.ProductControllerApi
import com.hoxy.hlivv.data.models.CartDto
import com.hoxy.hlivv.data.models.ProductDto
import com.hoxy.hlivv.data.models.ProductImageDto
import com.hoxy.hlivv.ui.cart.payment.PaymentViewModel
import com.hoxy.hlivv.ui.liveqr.barcode.product.ImageDownloadTask
import com.hoxy.hlivv.ui.liveqr.barcode.product.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.EOFException
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val cartList: MutableList<CartDto>,
    private val callback: OnSelectedItemsChanged,
    private val viewModel: PaymentViewModel
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    var isAllSelected = false
    private val selectedItems = mutableListOf<Long>()
    private val cartControllerApi = CartControllerApi()

    inner class CartViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.cart_product_image)
        private val titleView: TextView = itemView.findViewById(R.id.cart_product_title)
        val checkBox: CheckBox = itemView.findViewById(R.id.item_check)
        private val arSupported: ImageView = itemView.findViewById(R.id.ar_supported)
        private val unitPriceView: TextView =
            itemView.findViewById(R.id.cart_product_unit_price)
        private val priceView: TextView = itemView.findViewById(R.id.cart_product_price)
        private val qtyView: TextView = itemView.findViewById(R.id.cart_quantity_text)
        private val plusButton: TextView = itemView.findViewById(R.id.cart_plus_button)
        private val minusButton: TextView = itemView.findViewById(R.id.cart_minus_button)
        private val deleteButton: ImageView = itemView.findViewById(R.id.cart_delete_btn)
        private val imageSize: Int =
            itemView.resources.getDimensionPixelOffset(R.dimen.cart_item_image_size)


        fun bindCartProduct(cartDto: CartDto) {
            imageView.setImageDrawable(null)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val productApi = ProductControllerApi()
                    val productDto: ProductDto =
                        productApi.getProduct1(cartDto.productId!!.toLong())
                    val cartProduct =
                        convertProductDtoToProduct(cartDto.productId, productDto)
                    if (cartDto.arSupported!!) {
                        arSupported.visibility = View.VISIBLE
                    } else {
                        arSupported.visibility = View.GONE
                    }
                    checkBox.isChecked = isAllSelected
                    Log.d("TotalCheckBox", "$isAllSelected")
                    checkBox.setOnClickListener {
                        val isChecked = checkBox.isChecked
                        Log.d("isChecked", "$checkBox.isChecked, $adapterPosition")
                        if (isChecked) {
                            selectedItems.add(cartDto.productId)
                        } else {
                            selectedItems.remove(cartDto.productId)
                        }
                        callback.onSelectedItemsChanged(selectedItems)
                    }
                    if (!TextUtils.isEmpty(cartProduct.imageUrl)) {
                        ImageDownloadTask(
                            imageView,
                            imageSize
                        ).downloadImage(cartProduct.imageUrl)
                    }
                    withContext(Dispatchers.Main) {
                        titleView.text = cartProduct.title
                        val unitPrice = cartProduct.unitPrice.toLong()
                        setFormattedNumberToTextView(unitPrice, unitPriceView)

                        var quantity = cartDto.cartQty
                        var price = unitPrice * quantity!!
                        qtyView.text = quantity.toString()
                        setFormattedNumberToTextView(price, priceView)

                        // 누르면 업데이트 연결
                        plusButton.setOnClickListener {
                            val currentQuantity = qtyView.text.toString().toInt()
                            quantity = currentQuantity + 1
                            price += unitPrice

                            setFormattedNumberToTextView(price, priceView)
                            qtyView.text = quantity.toString()
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val cartDto: CartDto = cartControllerApi.updateCart(
                                        cartProduct.productId,
                                        quantity!!.toInt()
                                    )
                                    withContext(Dispatchers.Main) {
                                        if (cartProduct.productId in selectedItems) {
                                            viewModel.addSubTotalPrice(cartDto.unitPrice!!)
                                            viewModel.addDiscountTotalPrice(cartDto.discountUnitPrice!!)
                                            viewModel.addOrderTotalPrice(cartDto.discountUnitPrice)
                                        }
                                    }
                                } catch (e: Exception) {
                                    //  예외 처리---> 다이얼로그로

                                }
                            }

                        }

                        // 누르면 업데이트 연결
                        minusButton.setOnClickListener {
                            val currentQuantity = qtyView.text.toString().toInt()
                            if (currentQuantity > 1) {
                                quantity = currentQuantity - 1
                                price -= unitPrice
                                setFormattedNumberToTextView(price, priceView)
                                qtyView.text = quantity.toString()
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val cartDto: CartDto = cartControllerApi.updateCart(
                                            cartProduct.productId,
                                            quantity!!.toInt()
                                        )
                                        withContext(Dispatchers.Main) {
                                            if (cartProduct.productId in selectedItems) {
                                                viewModel.subtractSubTotalPrice(cartDto.unitPrice!!)
                                                viewModel.subtractDiscountTotalPrice(cartDto.discountUnitPrice!!)
                                                viewModel.subtractOrderTotalPrice(cartDto.discountUnitPrice)
                                            }
                                        }

                                    } catch (e: Exception) {
                                        //  예외 처리

                                    }
                                }
                            }

                        }

                        deleteButton.setOnClickListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    cartControllerApi.deleteFromCart(cartProduct.productId)
                                } catch (e: EOFException) {
                                    withContext(Dispatchers.Main) {
                                        removeItem(adapterPosition)
                                        selectedItems.remove(cartProduct.productId)
                                        callback.onSelectedItemsChanged(selectedItems)
                                    }
                                } catch (e: Exception) {
                                    Log.d("CartDto", "API ERROR", e)
                                }
                            }
                        }

                    }
                } catch (e: Exception) {

                }

            }
        }

        private fun setFormattedNumberToTextView(number: Long, textView: TextView) {
            val formattedNumber = NumberFormat.getNumberInstance(Locale.KOREA).format(number)
            textView.text = formattedNumber
        }

        private fun convertProductDtoToProduct(productId: Long, productDto: ProductDto): Product {
            val imageUrl = getProductImageUrl(productDto.productImages)
            val title = productDto.name ?: ""
            val price = productDto.price?.toString() ?: "0"

            return Product(
                productId,
                imageUrl,
                title,
                price,
                productDto.arSupported!!,
                productDto.qrSupported!!,
                productDto.eco!!
            )
        }

        private fun getProductImageUrl(productImages: Array<ProductImageDto>?): String {
            return productImages?.firstOrNull()?.imageUrl ?: ""
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder =
        create(parent)

    private fun create(parent: ViewGroup): CartViewHolder {
        return CartViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bindCartProduct(cartList[position])
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty() && payloads[0] is Payload) {
            val isChecked = (payloads[0] as Payload).isChecked
            holder.checkBox.isChecked = isChecked
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }



    fun selectAllItems(isChecked: Boolean) {
        isAllSelected = isChecked
        selectedItems.clear()
        if (isChecked) {
            selectedItems.addAll(cartList.map { it.productId!! })
        }
        for (position in 0 until itemCount) {
            notifyItemChanged(position, Payload(isChecked))
        }
        callback.onSelectedItemsChanged(selectedItems)


    }

    fun removeItem(position: Int) {
        cartList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int =
        cartList.size

    fun deleteCartList() {
        val deletedItems = mutableListOf<CartDto>()

        for (productId in selectedItems) {
            val position = cartList.indexOfFirst { it.productId == productId }
            if (position != -1) {
                // 선택된 아이템을 삭제하고 삭제된 아이템 리스트에 추가
                val deletedItem = cartList.removeAt(position)
                deletedItems.add(deletedItem)
                notifyItemRemoved(position)
            }
        }

        // 삭제된 아이템들 서버에서 삭제
        for (deletedItem in deletedItems) {
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    cartControllerApi.deleteFromCart(deletedItem.productId!!)
                } catch (e:Exception){
                    Log.d("CartViewHolder","ERROR!",e)
                }

            }
        }

        // 선택된 아이템 목록 초기화
        selectedItems.clear()
        callback.onSelectedItemsChanged(selectedItems)
    }

}