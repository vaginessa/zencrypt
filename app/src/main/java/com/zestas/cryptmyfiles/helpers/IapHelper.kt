package com.zestas.cryptmyfiles.helpers

import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zestas.cryptmyfiles.BuildConfig
import com.zestas.cryptmyfiles.dataItemModels.ZenCryptSettingsModel
import com.zestas.cryptmyfiles.fragments.SettingsFragment
import games.moisoni.google_iab.BillingConnector
import games.moisoni.google_iab.BillingEventListener
import games.moisoni.google_iab.enums.ErrorType
import games.moisoni.google_iab.enums.PurchasedResult
import games.moisoni.google_iab.models.BillingResponse
import games.moisoni.google_iab.models.PurchaseInfo
import games.moisoni.google_iab.models.SkuInfo
import kotlinx.coroutines.launch

class IapHelper {
    companion object {
        fun initBillingConnector(activity: AppCompatActivity): BillingConnector {
            val billingConnector = BillingConnector(
                activity,
                BuildConfig.ApiKey) //"license_key" - public developer key from Play Console
                .setNonConsumableIds(listOf("zencrypt_pro")) //to set non-consumable ids - call only for non-consumable products
                .autoAcknowledge() //legacy option - better call this. Alternatively purchases can be acknowledge via public method "acknowledgePurchase(PurchaseInfo purchaseInfo)"
                .autoConsume() //legacy option - better call this. Alternatively purchases can be consumed via public method consumePurchase(PurchaseInfo purchaseInfo)"
                .enableLogging() //to enable logging for debugging throughout the library - this can be skipped
                .connect() //to connect billing client with Play Console

            billingConnector.setBillingEventListener(object : BillingEventListener {
                override fun onProductsFetched(@NonNull skuDetails: List<SkuInfo>) {
                    if ( billingConnector.isPurchased(skuDetails.first() ) == PurchasedResult.YES )
                        activity.lifecycleScope.launch {
                            ZenCryptSettingsModel.isProUser.update(true)
                        }
                }

                override fun onPurchasedProductsFetched(@NonNull purchases: List<PurchaseInfo>) {
                    /*Provides a list with fetched purchased products*/
                }

                override fun onProductsPurchased(@NonNull purchases: List<PurchaseInfo>) {
                    activity.lifecycleScope.launch {
                        ZenCryptSettingsModel.isProUser.update(true)
                    }
                    SnackBarHelper.showSnackBarLove("Thank you for purchasing ZenCrypt pro! You are awesome!")
                    FragmentHelper.replaceFragmentWithDelay(SettingsFragment())

                }

                override fun onPurchaseAcknowledged(@NonNull purchase: PurchaseInfo) {
                    /*Callback after a purchase is acknowledged*/

                    /*
                     * Grant user entitlement for NON-CONSUMABLE products and SUBSCRIPTIONS here
                     *
                     * Even though onProductsPurchased is triggered when a purchase is successfully made
                     * there might be a problem along the way with the payment and the purchase won't be acknowledged
                     *
                     * Google will refund users purchases that aren't acknowledged in 3 days
                     *
                     * To ensure that all valid purchases are acknowledged the library will automatically
                     * check and acknowledge all unacknowledged products at the startup
                     * */
                    activity.lifecycleScope.launch {
                        ZenCryptSettingsModel.isProUser.update(true)
                    }
                }

                override fun onPurchaseConsumed(@NonNull purchase: PurchaseInfo) {
                    /*Callback after a purchase is consumed*/

                    /*
                     * CONSUMABLE products entitlement can be granted either here or in onProductsPurchased
                     * */
                }

                override fun onBillingError(
                    @NonNull billingConnector: BillingConnector,
                    @NonNull response: BillingResponse
                ) {
                    /*Callback after an error occurs*/
                    when (response.errorType) {
                        ErrorType.CLIENT_NOT_READY -> { }
                        ErrorType.CLIENT_DISCONNECTED -> { }
                        ErrorType.SKU_NOT_EXIST -> { }
                        ErrorType.CONSUME_ERROR -> { }
                        ErrorType.ACKNOWLEDGE_ERROR -> {
                            activity.lifecycleScope.launch {
                                ZenCryptSettingsModel.isProUser.update(false)
                            }
                        }
                        ErrorType.ACKNOWLEDGE_WARNING -> {
                            SnackBarHelper.showSnackBarInfo("You have bought ZenCrypt pro, but payment is in pending status.")
                            activity.lifecycleScope.launch {
                                ZenCryptSettingsModel.isProUser.update(false)
                            }
                        }
                        ErrorType.FETCH_PURCHASED_PRODUCTS_ERROR -> { }
                        ErrorType.BILLING_ERROR -> {
                            SnackBarHelper.showSnackBarError("Billing error!")
                            activity.lifecycleScope.launch {
                                ZenCryptSettingsModel.isProUser.update(false)
                            }
                        }
                        ErrorType.USER_CANCELED -> SnackBarHelper.showSnackBarError("Payment cancelled.")
                        ErrorType.SERVICE_UNAVAILABLE -> { }
                        ErrorType.BILLING_UNAVAILABLE -> SnackBarHelper.showSnackBarError("Billing Unavailable.")
                        ErrorType.ITEM_UNAVAILABLE -> { }
                        ErrorType.DEVELOPER_ERROR -> { }
                        ErrorType.ERROR -> {
                            SnackBarHelper.showSnackBarError("Something went wrong.")
                            activity.lifecycleScope.launch {
                                ZenCryptSettingsModel.isProUser.update(false)
                            }
                        }
                        ErrorType.ITEM_ALREADY_OWNED -> {
                            activity.lifecycleScope.launch {
                                ZenCryptSettingsModel.isProUser.update(true)
                            }
                        }
                        ErrorType.ITEM_NOT_OWNED -> {
                            activity.lifecycleScope.launch {
                                ZenCryptSettingsModel.isProUser.update(false)
                            }
                        }
                        null -> { }
                    }
                }
            })

            return billingConnector
        }
    }
}