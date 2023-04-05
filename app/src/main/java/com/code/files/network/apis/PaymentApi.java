package com.code.files.network.apis;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PaymentApi {

    @FormUrlEncoded
    @POST("store_payment_info")
    Call<ResponseBody> savePayment(@Header("API-KEY") String apiKey,
                                   @Field("plan_id") String planId,
                                   @Field("user_id") String userId,
                                   @Field("paid_amount") String paidAmount,
                                   @Field("payment_info") String paymentInfo,
                                   @Field("payment_method") String paymentMethod);


}
