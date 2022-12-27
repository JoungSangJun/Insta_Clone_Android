package com.example.instajoin

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class InstaProfileFragment : Fragment() {

    lateinit var userProfileImageView: ImageView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.insta_profile_fragment, container)
    }

    //이미지 새로고침(onViewCreated와 같은 기능 생명주기가 다름)
    override fun onResume() {
        super.onResume()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        val sp = (activity as InstaMainActivity).getSharedPreferences(
            "user_info",
            Context.MODE_PRIVATE
        )
        val token = sp.getString("token", "")
        header.put("Authorization", "token" + token!!)

        val glide = Glide.with(activity as InstaMainActivity)

        //서버에게 자신에게 알리는 token 전송
        retrofitService.getUserInfo(header).enqueue(object : Callback<UserInfo> {
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                val userInfo: UserInfo? = response.body()
                if (userInfo == null) {

                } else {
                    //유저 토큰에 있는 유저 이미지 띄우기
                    userInfo.profile.image?.let {
                        glide.load(it).into(userProfileImageView)
                    }
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfileImageView = view.findViewById(R.id.profile_img)

        //로그인한 사용자의 프로필사진 받아오기
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)
        val header = HashMap<String, String>()
        val sp = (activity as InstaMainActivity).getSharedPreferences(
            "user_info",
            Context.MODE_PRIVATE
        )
        val token = sp.getString("token", "")
        header.put("Authorization", "token" + token!!)

        val glide = Glide.with(activity as InstaMainActivity)

        retrofitService.getUserInfo(header).enqueue(object : Callback<UserInfo> {
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                val userInfo: UserInfo? = response.body()
                if (userInfo == null) {

                } else {
                    userInfo.profile.image?.let {
                        glide.load(it).into(userProfileImageView)
                    }
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
            }
        })

        view.findViewById<TextView>(R.id.change_img).setOnClickListener {
            startActivity(
                Intent(
                    activity as InstaMainActivity,
                    InstaChangeProfileActivity::class.java
                )
            )
        }
    }

}