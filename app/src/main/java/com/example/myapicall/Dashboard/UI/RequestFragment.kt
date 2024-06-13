package com.example.myapicall.Dashboard.UI

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.myapicall.Dashboard.IocalStorage.RealPathUtil
import com.example.myapicall.Dashboard.Model.SaveFamilyRequest
import com.example.myapicall.Loader
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Constant
import com.example.myapicall.UserAuth.Network.RestApi
import com.example.myapicall.UserAuth.Network.Service
import com.example.myapicall.Util.ImageHelper
import com.example.myapicall.databinding.FragmentRequestBinding
import com.google.android.material.imageview.ShapeableImageView
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream


class RequestFragment : Fragment() {
    private var _binding : FragmentRequestBinding? = null
    private val binding get() = _binding!!
    var dropdwonItemSelected : String? = null
    lateinit var loading : Loader
    private var gender : String? = null
    lateinit var imageUri : Uri
    private val imageHelper = ImageHelper()
    var file: File? = null
    var path : String? = null

    private var isReadPermissionGranted = false
    private var isCameraPermissionGranted = false

    private val permissionLauncher : ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){

        isReadPermissionGranted = it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
        if (isReadPermissionGranted){

                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getImage.launch(galleryIntent)

        }
    }

    private val permissionLauncher2 : ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
        isCameraPermissionGranted = it[android.Manifest.permission.CAMERA] ?: isCameraPermissionGranted

        if (isCameraPermissionGranted){
            contract.launch(imageUri)
        }

    }


    private val contract = registerForActivityResult(
        ActivityResultContracts.TakePicture()){
        binding.imageIV.setImageURI(null)
        binding.imageIV.setImageURI(imageUri)
    }

    private var getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            if (selectedImageUri != null) {
                binding.imageIV.setImageURI(null)
                binding.imageIV.setImageURI(selectedImageUri)
                imageHelper.uriToFile(requireContext() , selectedImageUri).let { fil ->
                    file = File(fil!!.absolutePath)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRequestBinding.inflate(inflater, container, false)

        loading = Loader(this)
        imageUri = createImageUri()!!

        Log.d("token in requestfrag",Constant.token)
        textColorChange()
        genderFocus()
        dropdownItems()
        jobFocusListner()
        nameFocusListner()
        contactFocusListner()
        roleFocusListner()
        dropFocusListner()

        binding.submitbtn.setOnClickListener {
            verify()
        }

        binding.closeIV.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.imageIV.setOnClickListener {
            //getImage.launch("image/*")
            //contract.launch(imageUri)
            showDialog()
        }

        return binding.root
    }

    private fun textColorChange() {
        binding.jobTV.setText(HtmlCompat.fromHtml("Job Title<font color='red'>*</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))

        binding.nameTV.setText(HtmlCompat.fromHtml("Agency Name<font color='red'>*</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))

        binding.roleTV.setText(HtmlCompat.fromHtml("Contact Person<font color='red'>*</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))

        binding.contactTV.setText(HtmlCompat.fromHtml("Contact Number<font color='red'>*</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))

        binding.dropTv.setText(HtmlCompat.fromHtml("How soon do you need staffing?<font color='red'>*</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
    }

    private fun requestGalleryPermission(){
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()
        if (!isReadPermissionGranted){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }else{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImage.launch(galleryIntent)
        }
    }
    private fun requestCameraPermission(){
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest : MutableList<String> = ArrayList()
        if (!isCameraPermissionGranted){
            permissionRequest.add(android.Manifest.permission.CAMERA)
        }
        if (permissionRequest.isNotEmpty()){
            permissionLauncher2.launch(permissionRequest.toTypedArray())
        }else{
            contract.launch(imageUri)
        }
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet)

        val camera = dialog.findViewById<ShapeableImageView>(R.id.cameraIV)
        val gallery = dialog.findViewById<ShapeableImageView>(R.id.galleryIV)

        camera.setOnClickListener {
            requestCameraPermission()
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            requestGalleryPermission()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun createImageUri() : Uri?{
        val image = File(requireContext().filesDir,"camera_photo.png")
        return FileProvider.getUriForFile(requireContext(),
            "com.example.myapicall.FileProvider",
            image
        )
    }

    private fun goToGallery() {
        //getImage.launch("image/*")
    }

    private fun genderFocus() {
        binding.maleLL.setOnClickListener {
            gender = "Male"
                binding.maleLL.background = ContextCompat.getDrawable(requireContext(),R.drawable.backgroundhighlight)
                binding.femaleLL.background = ContextCompat.getDrawable(requireContext(),R.drawable.borderlargegrey)
                binding.doesLL.background = ContextCompat.getDrawable(requireContext(),R.drawable.borderlargegrey)

        }
        binding.femaleLL.setOnClickListener {
            gender = "Female"
                binding.femaleLL.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.backgroundhighlight)
                binding.maleLL.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
                binding.doesLL.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
        }
        binding.doesLL.setOnClickListener {
            gender = "Doesn't Matter"
                binding.doesLL.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.backgroundhighlight)
                binding.maleLL.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
                binding.femaleLL.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
        }

        binding.inhouseLL.setOnClickListener {
            gender = "In home"
            binding.inhouseLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.backgroundhighlight)
            binding.communityLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
            binding.bothLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
        }
        binding.communityLL.setOnClickListener {
            gender = "Community"
            binding.communityLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.backgroundhighlight)
            binding.inhouseLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
            binding.bothLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
        }
        binding.bothLL.setOnClickListener {
            gender = "Both"
            binding.bothLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.backgroundhighlight)
            binding.communityLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
            binding.inhouseLL.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.borderlargegrey)
        }
    }

    private fun verify() {
        Log.d("submit","check1")
        if (varifyJob()==null && varifyname()==null && varifycontact()==null && varifyrole()==null && varifydrop()==null){
            alertDialog()
        }else{
            Log.d("submit","check2")
            if (binding.jobET.text?.isEmpty() == true){
                Log.d("submit","check2.1")
                binding.jobContainer.helperText = "required"
            }
            else if (binding.nameET.text?.isEmpty() == true){
                binding.nameContainer.helperText = "required"
            }
            else if (binding.contactET.text?.isEmpty() == true){
                binding.contactContainer.helperText = "required"
            }
            else if (binding.roleET.text?.isEmpty() == true){
                binding.roleContainer.helperText = "required"
            }
            else if (dropdwonItemSelected == null){
                binding.dropdownContainer.helperText = "required"
            }
        }
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Do you want to proceed?")

        builder.setPositiveButton("Yes"){
                _, _ ->
            postFamilyDetails()
        }
        builder.setNegativeButton("No"){
            _,_ ->
        }

        val alert : AlertDialog = builder.create()
        alert.setCancelable(false)
        alert.show()

    }


    private fun postFamilyDetails() {
        val retrofit = RestApi.getRetrofitInstance()
        val service = retrofit.create(Service::class.java)

        /*val hashmap : HashMap<String, Any?> = HashMap()
        hashmap["request_from"] = "family"
        hashmap["contact_name"] = "Family"
        hashmap["contact_email"] = "family111@mailinator.com"
        hashmap["contact_phone"] = binding.contactET.text.toString().toLong()
        hashmap["person_age"] = 50
        hashmap["support_type"] = 3
        hashmap["staff_gender"] = gender
        hashmap["staff_vehicle"] = "yes"
        hashmap["have_pets"] = "no"
        hashmap["request_title"] = binding.roleET.text.toString()
        hashmap["about"] = ""
        hashmap["address_line_1"] = ""
        hashmap["address_line_2"] = ""
        hashmap["city"] = ""
        hashmap["state"] = ""
        hashmap["zip"] = ""
        hashmap["image"] = binding.imageIV
        hashmap["staffing_need_time"] = dropdwonItemSelected*/

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("request_from","family")
        builder.addFormDataPart("contact_name","Family")
        builder.addFormDataPart("contact_email","family111@mailinator.com")
        builder.addFormDataPart("contact_phone",binding.contactET.text.toString())
        builder.addFormDataPart("person_age","50")
        builder.addFormDataPart("support_type","3")
        builder.addFormDataPart("staff_gender",gender!!)
        builder.addFormDataPart("staff_vehicle","yes")
        builder.addFormDataPart("have_pets","no")
        builder.addFormDataPart("request_title",binding.roleET.text.toString())
        builder.addFormDataPart("about","")
        builder.addFormDataPart("address_line_1","")
        builder.addFormDataPart("address_line_2","")
        builder.addFormDataPart("city","")
        builder.addFormDataPart("state","")
        builder.addFormDataPart("zip","")
        //builder.addFormDataPart("image","Family")
        builder.addFormDataPart("staffing_need_time",dropdwonItemSelected!!)
//      builder.addFormDataPart("image", file!!.name, file!!.asRequestBody("multipart/form-data".toMediaTypeOrNull()))

        builder.addFormDataPart("image", file!!.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file!!))

        val requestBody = builder.build()
       /* val requestBody = FormBody.Builder()
            .add("request_from","family")
            .add("contact_name","Family")
            .add("contact_email","family111@mailinator.com")
            .add("contact_phone",binding.contactET.text.toString())
            .add("person_age","50")
            .add("support_type","3")
            .add("staff_gender",gender!!)
            .add("staff_vehicle","yes")
            .add("have_pets","no")
            .add("request_title",binding.roleET.text.toString())
            .add("about","")
            .add("address_line_1","")
            .add("address_line_2","")
            .add("city","")
            .add("state","")
            .add("zip","")
            .add("image",file.toString())
            .add("staffing_need_time",dropdwonItemSelected!!)
            .build()*/

        //val request = Request.Builder().url("http://demo.kitlabs.us/api/save_family_request").post(requestBody).build()
        loading.startloader()

        service.postFamily("Bearer ${Constant.token}", requestBody).enqueue(object : Callback<SaveFamilyRequest>{
            override fun onResponse(
                call: Call<SaveFamilyRequest>,
                response: Response<SaveFamilyRequest>
            ) {
                if(response.isSuccessful){
                    loading.stopLoader()
                    //Toast.makeText(requireContext(), "${response.body()?.message}",Toast.LENGTH_LONG).show()
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }else{
                    loading.stopLoader()
                    when (response.code()){
                        400 -> Toast.makeText(requireContext(),"Bad Request",Toast.LENGTH_LONG).show()
                        401 -> Toast.makeText(requireContext(),"Unauthorized",Toast.LENGTH_LONG).show()
                        404 -> Toast.makeText(requireContext(),"Not Found",Toast.LENGTH_LONG).show()
                        else -> {
                            Log.e("TAG", "onResponse: else")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SaveFamilyRequest>, t: Throwable) {
                loading.stopLoader()
                Log.d("error", "error => ${t.localizedMessage}")
            }
        })
    }

   /* private fun postFamily(){

        val requestBody = FormBody.Builder()
            .add("request_from","family")
            .add("request_name","Family")
            .add("contact_email","family111@mailinator.com")
            .add("contact_phone","7777889999")
            .add("person_age","50")
            .add("support_type","3")
            .add("staff_gender","Male")
            .add("staff_vehicle","yes")
            .add("have_pets","no")
            .add("request_title","Doctor")
            .add("about","")
            .add("address_line_1","")
            .add("address_line_2","")
            .add("city","")
            .add("state","")
            .add("zip","")
            .add("image","")
            .add("staffing_need_time","month")
            .build()
        val request = Request.Builder().url("http://demo.kitlabs.us/api/save_family_request").post(requestBody).build()

        client.newCall(request).enqueue(object : okhttp3.Callback{
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("failure","$e")
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }
                    //Toast.makeText(requireContext(),"${response.body}",Toast.LENGTH_LONG).show()
                    println(response.body!!.string())
                }
            }
        })
    }
*/
    private fun jobFocusListner() {
        binding.jobET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.jobContainer.helperText = varifyJob()
            }else{
                val jobText = binding.jobET.text.toString()
                if (jobText.isNotEmpty()){
                    binding.jobContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun varifyJob(): String? {
        val jobText = binding.jobET.text.toString()
        if (jobText.isEmpty()){
            binding.jobContainer.boxStrokeColor = Color.RED
            return " can not be Empty"
        }else{
            binding.jobContainer.boxStrokeColor = Color.parseColor("#4899EA")
        }
        return null
    }

    private fun nameFocusListner() {
        binding.nameET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.nameContainer.helperText = varifyname()
            }else{
                val nametext = binding.nameET.text.toString()
                if (nametext.isNotEmpty()){
                    binding.nameContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun varifyname(): String? {
        val nametext = binding.nameET.text.toString()
        if (nametext.isEmpty()){
            binding.nameContainer.boxStrokeColor = Color.RED
            return " can not be Empty"
        }else{
            binding.nameContainer.boxStrokeColor = Color.parseColor("#4899EA")
        }
        return null
    }
    private fun roleFocusListner() {
        binding.roleET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.roleContainer.helperText = varifyrole()
            }else{
                val roletext = binding.roleET.text.toString()
                if (roletext.isNotEmpty()){
                    binding.roleContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun varifyrole(): String? {
        val roletext = binding.roleET.text.toString()
        if (roletext.isEmpty()){
            binding.roleContainer.boxStrokeColor = Color.RED
            return " can not be Empty"
        }else{
            binding.roleContainer.boxStrokeColor = Color.parseColor("#4899EA")
        }
        return null
    }

    private fun contactFocusListner() {
        binding.contactET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.contactContainer.helperText = varifycontact()
            }else{
                val contacttext = binding.contactET.text.toString()
                if (contacttext.isNotEmpty()){
                    binding.contactContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun varifycontact(): String? {
        val contacttext = binding.contactET.text.toString()
        if (contacttext.isEmpty()){
            binding.contactContainer.boxStrokeColor = Color.RED
            return " can not be Empty"
        }else{
            binding.contactContainer.boxStrokeColor = Color.parseColor("#4899EA")
        }
        return null
    }

    private fun dropFocusListner() {
        binding.dropdownautoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                binding.dropdownContainer.helperText = varifydrop()
            }else{
                if (dropdwonItemSelected != null){
                    binding.dropdownContainer.boxStrokeColor = Color.RED
                    binding.dropdownContainer.boxStrokeColor = Color.parseColor("#4899EA")
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun varifydrop(): String? {
        if (dropdwonItemSelected == null){
            binding.dropdownContainer.boxStrokeColor = Color.RED
            return " can not be Empty"
        }else{
            binding.dropdownContainer.boxStrokeColor = Color.parseColor("#4899EA")
        }
        return null
    }

    private fun dropdownItems() {
        val dropdwonItem = listOf("immediately",
                "within-a-week",
                "within-a-month")
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,dropdwonItem)
        binding.dropdownautoComplete.setAdapter(arrayAdapter)
        binding.dropdownautoComplete.setOnItemClickListener { parent, view, position, id ->
            dropdwonItemSelected = parent.getItemAtPosition(position).toString()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = RequestFragment()
    }
}