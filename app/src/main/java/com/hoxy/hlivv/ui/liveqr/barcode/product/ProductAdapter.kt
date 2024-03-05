package com.hoxy.hlivv.ui.liveqr.barcode.product

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hoxy.hlivv.R
import com.hoxy.hlivv.data.apis.CartControllerApi
import com.hoxy.hlivv.data.models.CartDto
import com.hoxy.hlivv.domain.Utils.setFormattedNumberToTextView
import com.hoxy.hlivv.ui.liveqr.dialog.CartCheckDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductAdapter(
    private val productList: List<Product>,
    private val listener: OnDialogConfirmedListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder private constructor(view: View, listener: OnDialogConfirmedListener) :
        RecyclerView.ViewHolder(view) {

        private val imageView: ImageView = view.findViewById(R.id.product_image)
        private val titleView: TextView = view.findViewById(R.id.product_title)
        private val priceView: TextView = view.findViewById(R.id.product_price)
        private val qtyView: TextView = view.findViewById(R.id.quantity_text)
        private val plusButton: TextView = view.findViewById(R.id.plus_button)
        private val minusButton: TextView = view.findViewById(R.id.minus_button)
        private val addCartButton: TextView = view.findViewById(R.id.add_cart)
        private val imageSize: Int =
            view.resources.getDimensionPixelOffset(R.dimen.product_item_image_size)

        @SuppressLint("CheckResult")
        fun bindProduct(product: Product, listener: OnDialogConfirmedListener) {
            imageView.setImageDrawable(null)
            if (!TextUtils.isEmpty(product.imageUrl)) {
                ImageDownloadTask(imageView, imageSize).downloadImage(product.imageUrl)
            } else {
                imageView.setImageResource(R.drawable.ic_cart_white)
            }
            titleView.text = product.title
            val unitPrice = product.unitPrice.toLong()
            var price = product.unitPrice.toLong()
            setFormattedNumberToTextView(price, priceView)
            var quantity = 1
            qtyView.text = quantity.toString()

            plusButton.setOnClickListener {
                val currentQuantity = qtyView.text.toString().toInt()
                quantity = currentQuantity + 1
                price += unitPrice
                setFormattedNumberToTextView(price, priceView)
                qtyView.text = quantity.toString()

            }

            minusButton.setOnClickListener {
                val currentQuantity = qtyView.text.toString().toInt()
                if (currentQuantity > 1) {
                    quantity = currentQuantity - 1
                    price -= unitPrice
                    setFormattedNumberToTextView(price, priceView)
                    qtyView.text = quantity.toString()
                }

            }

            addCartButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val cartControllerApi = CartControllerApi()
                        val cartDto: CartDto =
                            cartControllerApi.addProductToCart(product.productId, quantity)
                        showSuccessDialog("장바구니에 담았습니다.", listener)
                    } catch (e: Exception) {
                        Log.e("ADDCART", "${product.productId}, ${quantity}", e)
                    }
                }

            }
        }

        private fun showSuccessDialog(message: String, listener: OnDialogConfirmedListener) {
            Handler(Looper.getMainLooper()).post {
                val cartCheckDialog = CartCheckDialog(itemView.context)
                cartCheckDialog.listener = object : OnDialogConfirmedListener {
                    override fun onDialogConfirmed() {
                        listener.onDialogConfirmed()
                    }
                }
                cartCheckDialog.start()
            }
        }


//        private fun setFormattedNumberToTextView(number: Long, textView: TextView) {
//            val formattedNumber = NumberFormat.getNumberInstance(Locale.KOREA).format(number)
//            textView.text = formattedNumber
//        }


        companion object {
            fun create(parent: ViewGroup, listener: OnDialogConfirmedListener) =
                ProductViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.product_item, parent, false), listener
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder.create(parent, listener)

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindProduct(productList[position], listener)
    }

    override fun getItemCount(): Int = productList.size


}