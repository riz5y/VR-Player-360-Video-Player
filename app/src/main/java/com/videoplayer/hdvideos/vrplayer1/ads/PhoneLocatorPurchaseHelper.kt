package com.videoplayer.hdvideos.vrplayer1.ads

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.android.billingclient.api.*
import com.videoplayer.hdvideos.vrplayer1.ads.utlites.ContentsLocator

class PhoneLocatorPurchaseHelper(private val activityContext: Context,  private val onPricesLoaded: (() -> Unit)? = null ) :
    PurchasesUpdatedListener {
    private lateinit var googleBillingAIGpsNavClient: BillingClient
    private val TAG = "BillingLogger:"
    private val listAvailAIGpsNavPurchases = ArrayList<ProductDetails>()
    private val listAvailAIGpsNavSub = ArrayList<ProductDetails>()
    private lateinit var billingAIGpsNavPreferences: SharedPreferences

    init {
        initMyBillingClientAIGpsNav()
    }

    private fun initMyBillingClientAIGpsNav() {
        try {
            billingAIGpsNavPreferences =
                activityContext.getSharedPreferences("PurchasePrefs", Context.MODE_PRIVATE)
            googleBillingAIGpsNavClient = BillingClient
                .newBuilder(activityContext)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .build()
                )
                .setListener(this)
                .build()
            googleBillingAIGpsNavClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Google Billing is Connected")
                        fetchAIGpsNavAllInAppsFromConsole() /*available on console*/
                        fetchAIGpsNavAllSubFromConsole() /*available on sub console*/
                        fetchAIGpsNavPurchasedInAppsFromConsole()  /*user has purchased*/
                        fetchAIGpsNavPurchasedSubFromConsole()  /*user has sub purchased*/
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(TAG, "Google Billing is  Disconnected")
                }
            })
        } catch (e: Exception) {
        }
    }

//    private fun fetchAIGpsNavAllInAppsFromConsole() {
//        val productList = ArrayList<QueryProductDetailsParams.Product>()
//        productList.add(
//            QueryProductDetailsParams.Product.newBuilder()
//                .setProductId("one_annually")
//                .setProductType(BillingClient.ProductType.INAPP)
//                .build()
//        )
//        val params = QueryProductDetailsParams.newBuilder()
//            .setProductList(productList)
//            .build()
//
//        googleBillingAIGpsNavClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                listAvailAIGpsNavPurchases.clear() // Clear previous list
//                for (productDetails in productDetailsList.productDetailsList) {
//                    listAvailAIGpsNavPurchases.add(productDetails)
//                    Log.i(TAG, productDetails.toString())
//                    val adsPurchaseDetails = productDetails.productId == " one_annually"
//                    if (adsPurchaseDetails) {
//                        productDetails.oneTimePurchaseOfferDetails?.let { offerDetails ->
//                            val price = offerDetails.formattedPrice
//                            Log.i(TAG + "billingLife", price)
//                            // ConstantsVd.oneTimePrice = price
//                        }
//                    }
//                }
//            } else {
//                Log.e(TAG, "Error querying in-app product details: ${billingResult.debugMessage}")
//            }
//        }
//    }
private fun fetchAIGpsNavAllInAppsFromConsole() {
    val productList = ArrayList<QueryProductDetailsParams.Product>()
    productList.add(
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId("one_annually")
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
    )
    val params = QueryProductDetailsParams.newBuilder()
        .setProductList(productList)
        .build()

    googleBillingAIGpsNavClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            listAvailAIGpsNavPurchases.clear()
            for (productDetails in productDetailsList.productDetailsList) {
                listAvailAIGpsNavPurchases.add(productDetails)
                Log.i(TAG, productDetails.toString())
                if (productDetails.productId == "one_annually") {
                    productDetails.oneTimePurchaseOfferDetails?.let { offerDetails ->
                        val price = offerDetails.formattedPrice
                        Log.i(TAG + "billingLife", price)
                        ContentsLocator.oneTimePrice = price
                    }
                }
            }
            (activityContext as? Activity)?.runOnUiThread {
                onPricesLoaded?.invoke()
            }
        } else {
            Log.e(TAG, "Error querying in-app product details: ${billingResult.debugMessage}")
        }
    }
}
    fun purchasedOneTime() {
        Log.d(TAG, "Going to purchase  one_annually")
        if (listAvailAIGpsNavPurchases.isNotEmpty()) {
            Log.e(TAG, "one_annually not found on Play Console - product list is empty")

            try {
                val adsProduct = listAvailAIGpsNavPurchases.find { it.productId == "one_annually" }
                if (adsProduct != null) {
                    launchBillingFlowForInApp(adsProduct)
                } else {
                    Log.d(TAG, " one_annually product not found in available products.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting purchase flow: ${e.message}")
            }
        } else {
            Log.d(TAG, "one_annually price: ${ContentsLocator.oneTimePrice}")

            Log.d(TAG, "Nothing to purchase for Google billing")
        }
    }


    private fun fetchAIGpsNavAllSubFromConsole() {
        val productList = ArrayList<QueryProductDetailsParams.Product>()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("one_annual")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("one_monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("one_weekly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        googleBillingAIGpsNavClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                listAvailAIGpsNavSub.clear() // Clear previous list
                for (productDetails in productDetailsList.productDetailsList) {
                    listAvailAIGpsNavSub.add(productDetails)
                    Log.i(TAG, productDetails.toString())
                    productDetails.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()
                        ?.let { pricingPhase ->
                            val price = pricingPhase.formattedPrice
                            val oneAnnuallyDetails = productDetails.productId == "one_annual"
                            val oneMonthlyDetails = productDetails.productId == "one_monthly"
                            val oneWeeklyDetails = productDetails.productId == "one_weekly"
                            if (oneAnnuallyDetails) {
                                ContentsLocator.yearlyPrice = price

                            } else if (oneMonthlyDetails) {
                                ContentsLocator.monthlyPrice = price
                            } else if (oneWeeklyDetails) {
                                ContentsLocator.WeeklyPrice = price
                            }
                        }
                }
                (activityContext as? Activity)?.runOnUiThread {
                    onPricesLoaded?.invoke()
                }
            } else {
                Log.e(TAG, "Error querying in-app product details: ${billingResult.debugMessage}")
            }
        }
    }

    fun fetchAIGpsNavPurchasedInAppsFromConsole() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        googleBillingAIGpsNavClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.isNotEmpty()) {
                    for (singlePurchase in purchasesList) {
                        // Billing Library 8 uses `getSkus()` which returns a List<String>
                        val productId =
                            singlePurchase.skus.firstOrNull() // Get the first product ID
                        if (productId != null) {
                            if (singlePurchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                Log.d(TAG, "Product Purchased: $productId")
                                billingAIGpsNavPreferences.edit { putBoolean(productId, true) }
                            } else {
                                billingAIGpsNavPreferences.edit { putBoolean(productId, false) }
                                Log.d(TAG, "Product Not Purchased: $productId")
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "No in-app purchases found.")
                }
            } else {
                Log.e(TAG, "Failed to query in-app purchases: ${billingResult.debugMessage}")
            }
        }

    }

    fun fetchAIGpsNavPurchasedSubFromConsole() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        googleBillingAIGpsNavClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.isNotEmpty()) {
                    for (singlePurchase in purchasesList) {
                        // Billing Library 8 uses `getSkus()` which returns a List<String>
                        val productId =
                            singlePurchase.skus.firstOrNull() // Get the first product ID
                        if (productId != null) {
                            if (singlePurchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                Log.d(TAG, "Subscription Purchased: $productId")
                                billingAIGpsNavPreferences.edit { putBoolean(productId, true) }
                            } else {
                                billingAIGpsNavPreferences.edit { putBoolean(productId, false) }
                                Log.d(TAG, "Subscription Not Purchased: $productId")
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "No subscriptions found.")
                }
            } else {
                Log.e(TAG, "Failed to query subscriptions: ${billingResult.debugMessage}")
            }
        }

    }

    fun purchaseAIGpsNavMonthlySub() {
        Log.d(TAG, "Going to purchase purchaseAIGpsNavMonthlySub subscription")
        if (listAvailAIGpsNavSub.size > 0) {
            val oneMonthlySubProduct = listAvailAIGpsNavSub.find { it.productId == "one_monthly" }
            if (oneMonthlySubProduct != null) {
                oneMonthlySubProduct.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let { offerToken ->
                    launchBillingFlowForSubscription(oneMonthlySubProduct, offerToken)
                } ?: run {
                    Log.e(TAG, "No offer details found for 'one_year' subscription.")
                    //  Toast.makeText(activityContext, "Subscription offer not available.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Subscription not found in available subscription products.")
                // Toast.makeText(activityContext, "Subscription not available yet. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Nothing to purchase for google billing")
            // Toast.makeText(activityContext, "Subscription not available yet. Please try again later.", Toast.LENGTH_SHORT).show()
        }
    }

    fun purchaseAIGpsNavYearSub() {
         if (listAvailAIGpsNavSub.size > 0) {
            val yearlySubProduct = listAvailAIGpsNavSub.find { it.productId == "one_annual" }
            if (yearlySubProduct != null) {
                yearlySubProduct.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let { offerToken ->
                    launchBillingFlowForSubscription(yearlySubProduct, offerToken)
                } ?: run {
                    Log.e(TAG, "No offer details found for 'one_year' subscription.")
                    //Toast.makeText(activityContext, "Subscription offer not available.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Subscription not found in available subscription products.")
                // Toast.makeText(activityContext, "Subscription not available yet. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Nothing to purchase for google billing")
            // Toast.makeText(activityContext, "Subscription not available yet. Please try again later.", Toast.LENGTH_SHORT).show()
        }
    }

    fun purchaseAIGpsNavWeekly() {
        Log.d(TAG, "Going to purchase Weekly subscription")
        if (listAvailAIGpsNavSub.size > 0) {
            val weeklySubProduct = listAvailAIGpsNavSub.find { it.productId == "one_weekly" }
            if (weeklySubProduct != null) {
                weeklySubProduct.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let { offerToken ->
                    launchBillingFlowForSubscription(weeklySubProduct, offerToken)
                } ?: run {
                    Log.e(TAG, "No offer details found for 'one_year' subscription.")
                    //Toast.makeText(activityContext, "Subscription offer not available.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Subscription not found in available subscription products.")
                // Toast.makeText(activityContext, "Subscription not available yet. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Nothing to purchase for google billing")
            // Toast.makeText(activityContext, "Subscription not available yet. Please try again later.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchBillingFlowForInApp(productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult =
            googleBillingAIGpsNavClient.launchBillingFlow(activityContext as Activity, flowParams)
        Log.d(TAG, "Google Billing Response : ${billingResult.responseCode}")
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Billing flow launch failed: ${billingResult.debugMessage}")
        }
    }

    private fun launchBillingFlowForSubscription(
        productDetails: ProductDetails,
        offerToken: String
    ) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken) // Important for subscriptions!
                .build()
        )

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult =
            googleBillingAIGpsNavClient.launchBillingFlow(activityContext as Activity, flowParams)
        Log.d(TAG, "Google Billing Response : ${billingResult.responseCode}")
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "Billing flow launch failed: ${billingResult.debugMessage}")
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // Use skus[0] as there might be multiple SKUs in older versions, but Billing Library 8
                // is designed for one product per purchase for new purchases.
                Log.d(TAG, "onPurchases Successfully Purchased : " + purchase.skus.firstOrNull())
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Google Billing Cancelled")
            // Toast.makeText(activityContext, "Purchase cancelled by user.", Toast.LENGTH_SHORT).show()
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.d(TAG, "Google Billing Purchased Already")
            //  Toast.makeText(activityContext, "You have already purchased this item", Toast.LENGTH_LONG).show()
            // It's good practice to also query purchases and acknowledge if necessary in this case
            fetchAIGpsNavPurchasedInAppsFromConsole()
            fetchAIGpsNavPurchasedSubFromConsole()
        } else {
            Log.d(
                TAG,
                "Google billing other error " + billingResult.responseCode + ": " + billingResult.debugMessage
            )
            //Toast.makeText(activityContext, "Purchase error: ${billingResult.debugMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private val purchaseAcknowledgedListener: AcknowledgePurchaseResponseListener = object
        : AcknowledgePurchaseResponseListener {
        override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
            Log.d(
                TAG,
                "Success Acknowledged : ${billingResult.responseCode}  :${billingResult.debugMessage}"
            )
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                fetchAIGpsNavPurchasedInAppsFromConsole()
                fetchAIGpsNavPurchasedSubFromConsole()
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                Log.d(TAG, "Process acknowledging: ${purchase.skus.firstOrNull()}")
                val acknowledgeParameters = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                googleBillingAIGpsNavClient.acknowledgePurchase(
                    acknowledgeParameters,
                    purchaseAcknowledgedListener
                )
            } else {
                Log.d(TAG, "Purchase already acknowledged: ${purchase.skus.firstOrNull()}")
                // If already acknowledged, still ensure your app's entitlement is granted
                billingAIGpsNavPreferences.edit().putBoolean(purchase.skus.firstOrNull(), true)
                    .apply()
            }
        }
    }


}



