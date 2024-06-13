package com.example.myapicall.UserAuth.Network

import com.example.myapicall.Dashboard.Model.GetFamilyListModel
import com.example.myapicall.Dashboard.Model.SaveFamilyRequest
import com.example.myapicall.UserAuth.Model.LoginData
import com.example.myapicall.UserAuth.Model.LoginRequest
import com.example.myapicall.UserAuth.Model.LoginResponse
import com.example.myapicall.UserAuth.Model.Register
import com.example.myapicall.UserAuth.Model.RegisterResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface Service {

    @POST("register")
    fun postRegister(@Body register: Register): Call<RegisterResponse>

    @POST("login")
    fun postLogin(@Body loginData : LoginData): Call<LoginRequest>


    @POST("save_family_request")
    fun postFamily(
        @Header("Authorization") authToken: String,
        //@Header("Accept") accept: String,
        //@FieldMap hashmap: HashMap<String, Any?>
        @Body body: RequestBody,

    ): Call<SaveFamilyRequest>

    @GET("get_family_request")
    fun getFamily(
        @Query("page") page: Int,
        @Header("Authorization") authToken: String):Call<GetFamilyListModel>

    @POST("save_fcm_token")
    @FormUrlEncoded
    fun postFCMToken(
        @Header("Accept") accept : String,
        @Header("Authorization") authToken : String,
        @FieldMap map : HashMap<String,String?>
    ) : Call<RegisterResponse>
}
