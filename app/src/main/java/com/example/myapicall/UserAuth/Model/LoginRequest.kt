package com.example.myapicall.UserAuth.Model


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("status")
    val status: String?,
    @SerializedName("message")
    val message: Message?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Message(
        @SerializedName("token")
        val token: String?
    )

    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("active_status")
        val activeStatus: Int?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("register_type")
        val registerType: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("email_verified_at")
        val emailVerifiedAt: Any?,
        @SerializedName("phone")
        val phone: Any?,
        @SerializedName("location")
        val location: Any?,
        @SerializedName("fcm_token")
        val fcmToken: Any?,
        @SerializedName("socialLogID")
        val socialLogID: Any?,
        @SerializedName("image_path")
        val imagePath: Any?,
        @SerializedName("registration_number")
        val registrationNumber: Any?,
        @SerializedName("device_type")
        val deviceType: String?,
        @SerializedName("about_user")
        val aboutUser: Any?,
        @SerializedName("last_updated_by")
        val lastUpdatedBy: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("token")
        val token: String?,
        @SerializedName("user_role")
        val userRole: Int?
    )
}