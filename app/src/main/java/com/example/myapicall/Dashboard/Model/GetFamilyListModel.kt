package com.example.myapicall.Dashboard.Model


import com.google.gson.annotations.SerializedName

data class GetFamilyListModel(
    @SerializedName("status")
    val status: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int?,
        @SerializedName("data")
        val `data`: List<Data>?,
        @SerializedName("first_page_url")
        val firstPageUrl: String?,
        @SerializedName("from")
        val from: Int?,
        @SerializedName("last_page")
        val lastPage: Int?,
        @SerializedName("last_page_url")
        val lastPageUrl: String?,
        @SerializedName("next_page_url")
        val nextPageUrl: Any?,
        @SerializedName("path")
        val path: String?,
        @SerializedName("per_page")
        val perPage: Int?,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any?,
        @SerializedName("to")
        val to: Int?,
        @SerializedName("total")
        val total: Int?
    ) {
        data class Data(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("request_id")
            val requestId: String?,
            @SerializedName("user_id")
            val userId: Int?,
            @SerializedName("request_from")
            val requestFrom: String?,
            @SerializedName("request_title")
            val requestTitle: String,
            @SerializedName("contact_name")
            val contactName: String?,
            @SerializedName("contact_email")
            val contactEmail: String?,
            @SerializedName("contact_phone")
            val contactPhone: String?,
            @SerializedName("person_age")
            val personAge: Int?,
            @SerializedName("support_type")
            val supportType: String?,
            @SerializedName("staffing_need_time")
            val staffingNeedTime: String?,
            @SerializedName("staff_gender")
            val staffGender: String?,
            @SerializedName("staff_vehicle")
            val staffVehicle: Int?,
            @SerializedName("have_pets")
            val havePets: Int?,
            @SerializedName("about")
            val about: String?,
            @SerializedName("image")
            val image: String?,
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
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
        )
    }
}