package com.example.myapicall.Dashboard.UI

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapicall.Dashboard.Adapter.MyAdapter
import com.example.myapicall.Dashboard.Model.GetFamilyListModel
import com.example.myapicall.Loader
import com.example.myapicall.MainActivity
import com.example.myapicall.R
import com.example.myapicall.UserAuth.Model.Constant
import com.example.myapicall.UserAuth.Network.RestApi
import com.example.myapicall.UserAuth.Network.Service
import com.example.myapicall.databinding.FragmentHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private lateinit var mAuth : FirebaseAuth

    var google = false
    private lateinit var newRecyclerView: RecyclerView
    lateinit var loading : Loader
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var title : String

    var temp = 0
    private val list = ArrayList<GetFamilyListModel.Data.Data>()
    private val tempList = ArrayList<GetFamilyListModel.Data.Data>()
    var page = 1
    var isLoading = false
    var isSearching = false
    var isSort = false
    var total = 0

    lateinit var adapter: MyAdapter
    lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        loading = Loader(this)

        googleWelcome()

        // GET API to Retrieve data
        getFamilyData()
        setAdapter()
        pagination()

        binding.logoutIV.setOnClickListener {
            alertDialog()
        }

        binding.filterLL.setOnClickListener {
            sortpopup()
        }

        binding.fab.setOnClickListener {
            navigateToRequestFamily()
        }

        binding.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }

        })

        return binding.root
    }

    private fun googleWelcome() {
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(),gso)

        val auth = Firebase.auth
        val user = auth.currentUser
        if (user != null) {
            google = true
            Log.d("goglee","${google}")
        }
    }

    private fun filter(text : String?){
        tempList.clear()
        //val filterList = ArrayList<GetFamilyListModel.Data.Data>()
        if (!text.isNullOrEmpty()){
            isSearching = true
            for (item in list){
                Log.d("item","$text")
                if (item.requestTitle.lowercase().contains(text.lowercase())){
                    Log.d("item1","$item")
                    tempList.add(item)
                    //list.add(item)
                }
            }
            list.clear()
            list.addAll(tempList)
            adapter.notifyDataSetChanged()
            //adapter.filterList(filterList)
        }else{
            Log.d("else part of filter","else")
            isSearching = false
            page = 1
            getFamilyData()
        }
        /*if (filterList.isEmpty() && text == null){
            adapter.filterList(list)
        }*/
    }
    /*private fun getPage(responseData: List<GetFamilyListModel.Data.Data?>?) {
        isLoading = true
        val start = ((page)*limit) -1
        val end = page * limit

        for (i in start..end){
            numberList.add("Item "+ i.toString())
        }
        Handler().postDelayed({
            if (::adapter.isInitialized){
                adapter.notifyDataSetChanged()
            }
            else{
                adapter = MyAdapter(responseData)
                binding.recyclerView.adapter = adapter
            }
            isLoading = false
        },5000)
    }*/

    private fun pagination(){
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val totall = adapter.itemCount

                Log.d("visibleItemCount+pastVisibleItem","${visibleItemCount + pastVisibleItem}")
                Log.d("visibleItemCount+pastVisibleItem1","$temp")

                if((visibleItemCount+pastVisibleItem)>= totall){
                    Log.d("temp","${temp}")
                    Log.d("temp","$total")

                    if(list.size != total && !isSearching){
                        page++
                        Log.d("page","$page")
                        getFamilyData()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    fun alertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Do you want to proceed?")

        builder.setPositiveButton("Yes"){
                _, _ ->
            Log.d("goglee1","${google}")
            if (google == true){
                Log.d("goglee2","${google}")
                signOutAndStartSignInActivity()
            }else{
                navigateToLogin()
            }

        }
        builder.setNegativeButton("No"){
                _,_ ->
        }

        val alert : AlertDialog = builder.create()
        alert.setCancelable(false)
        alert.show()
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@HomeFragment.requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun navigateToRequestFamily() {
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.framelayout2,RequestFragment())?.addToBackStack("home")?.commit()
    }

    private fun setAdapter() {
        newRecyclerView = binding.recyclerView
        layoutManager = LinearLayoutManager(requireContext())
        newRecyclerView.layoutManager = layoutManager
        //newRecyclerView.hasFixedSize()
        adapter = MyAdapter(list)
        newRecyclerView.adapter = adapter
    }

    /*private fun getUserData() {
        for (i in images.indices){
            val detail = Details(images[i],role[i],phone[i],status[i],gender[i])
            newArrayList.add(detail)
        }
        newRecyclerView.adapter = MyAdapter(newArrayList)
    }*/

    private fun sortpopup() {
        val popupMenu = PopupMenu(requireContext(), binding.filterLL)

        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {menuItem ->
            title = menuItem.title.toString()
            sortt(title)
            true
        }
        popupMenu.show()
    }

    private fun sortt(title: String) {
        isSort = true

        if (title == "A-Z"){
            for (item in list){
            }
            list.sortBy { it.requestTitle.lowercase() }
            adapter.notifyDataSetChanged()
        }else{
            Log.d("sort","Z-A")
            list.sortBy { it.requestTitle.lowercase() }
            list.reverse()
            adapter.notifyDataSetChanged()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(),MainActivity::class.java)
        Constant.token = ""
        saveSharedPref()
        startActivity(intent)
        activity?.finish()
    }
    private fun saveSharedPref(){
        val sharePref = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putString("token", Constant.token)
        editor.apply()
    }

    private fun getFamilyData() {
        val retrofit = RestApi.getRetrofitInstance()
        val service = retrofit.create(Service::class.java)
        //loading.startloader()

        Log.d("token in home", Constant.token)
        service.getFamily(page, "Bearer ${Constant.token}").enqueue(object : Callback<GetFamilyListModel>{
            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(
                call: Call<GetFamilyListModel>,
                response: Response<GetFamilyListModel>
            ) {
                if (response.isSuccessful){
                    Log.d("isSuccessful", Constant.token)
                    //loading.stopLoader()
                    val arrayList : List<GetFamilyListModel.Data.Data>? = response.body()?.data?.data
                    if (!arrayList.isNullOrEmpty()){
                        if (page == 1){
                            total = response.body()?.data?.total ?: 0
                            list.clear()
                            list.addAll(arrayList)
                            //tempList.addAll(list)
                            Log.d("notify", "Constant.token")
                            adapter.notifyDataSetChanged()
                            binding.tvNoData.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                        else{
                            list.addAll(arrayList)
                            if (isSort){
                                Log.d("title","$title")
                                sortt(title)
                                isSort = false
                            }
                            Log.d("notify1", "Constant.token")
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else{
                        if (list.isEmpty()){
                            //binding.tvNoData.visibility = View.VISIBLE
                            //binding.recyclerView.visibility = View.GONE
                        }
                    }
                }
                else{
                    //loading.stopLoader()
                    Log.d("else error","error")

                    when (response.code()){
                        400 -> {
                            Toast.makeText(requireContext(),"Bad Request",Toast.LENGTH_LONG).show()
                            binding.tvNoData.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                        401 -> {
                            Toast.makeText(requireContext(),"Unauthorized",Toast.LENGTH_LONG).show()
                            binding.tvNoData.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                        404 -> {
                            Toast.makeText(requireContext(),"Not Found",Toast.LENGTH_LONG).show()
                            binding.tvNoData.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                        500 -> {
                            Toast.makeText(requireContext(),"Internal Server Error",Toast.LENGTH_LONG).show()
                            binding.tvNoData.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                    }
                }

            }

            override fun onFailure(call: Call<GetFamilyListModel>, t: Throwable) {
                Log.d("Errorr","$t")
                //loading.stopLoader()
            }

        })
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}