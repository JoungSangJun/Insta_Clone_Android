package com.example.instajoin


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InstaLoginActivity : AppCompatActivity() {
    var username: String = "" //유저 아이디
    var password: String = "" //유저 비밀번호

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insta_login)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        findViewById<EditText>(R.id.id_input).doAfterTextChanged {
            username = it.toString() //EditText에 아이디 입력되면 값 가져오기
        }
        findViewById<EditText>(R.id.pw_input).doAfterTextChanged {
            password = it.toString() //EditText에 비밀번호 입력되면 값 가져오기
        }

        //회원가입 화면으로 화면전환
        findViewById<TextView>(R.id.insta_join).setOnClickListener {
            startActivity(Intent(this@InstaLoginActivity, InstaJoinActivity::class.java))
        }

        findViewById<TextView>(R.id.login_btn).setOnClickListener {
            val user = HashMap<String, Any>() //유저정보 아이디 비밀번호 저장
            user.put("username", username)
            user.put("password", password)
            retrofitService.instaLogin(user).enqueue(object : Callback<User> {
                //로그인 성공시
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user: User = response.body()!!
                        // 자동로그인 위해 sharedPreferences에 로그인상태 저장
                        val sharedPreferences =
                            getSharedPreferences("user_info", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("token", user.token)
                        editor.putString("user_id", user.id.toString())
                        editor.commit()
                        startActivity(
                            Intent(
                                this@InstaLoginActivity,
                                InstaMainActivity::class.java
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("testt", t.message.toString())
                }

            })
        }
    }
}