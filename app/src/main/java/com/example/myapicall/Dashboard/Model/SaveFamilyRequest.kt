package com.example.myapicall.Dashboard.Model


import com.google.gson.annotations.SerializedName

data class SaveFamilyRequest(
    @SerializedName("status")
    val status: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("request_id")
        val requestId: String?,
        @SerializedName("request_title")
        val requestTitle: String?,
        @SerializedName("contact_name")
        val contactName: String?,
        @SerializedName("contact_email")
        val contactEmail: String?,
        @SerializedName("contact_phone")
        val contactPhone: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("request_from")
        val requestFrom: String?,
        @SerializedName("person_age")
        val personAge: String?,
        @SerializedName("support_type")
        val supportType: String?,
        @SerializedName("staff_gender")
        val staffGender: String?,
        @SerializedName("staff_vehicle")
        val staffVehicle: String?,
        @SerializedName("staffing_need_time")
        val staffingNeedTime: String?,
        @SerializedName("have_pets")
        val havePets: String?,
        @SerializedName("about")
        val about: String?,
        @SerializedName("address_line_1")
        val addressLine1: String?,
        @SerializedName("address_line_2")
        val addressLine2: String?,
        @SerializedName("city")
        val city: String?,
        @SerializedName("state")
        val state: String?,
        @SerializedName("zip")
        val zip: String?,
        @SerializedName("country")
        val country: String?,
        @SerializedName("request_status")
        val requestStatus: String?,
        @SerializedName("last_updated_by")
        val lastUpdatedBy: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("id")
        val id: Int?
    )
}