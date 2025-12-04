package com.gaurav.fieldagent.ui

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gaurav.fieldagent.R
import com.gaurav.fieldagent.data.model.User

class AgentProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_profile)

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER_EXTRA", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<User>("USER_EXTRA")
        }

        if (user != null) {
            val userImage = findViewById<ImageView>(R.id.user_image)
            val userName = findViewById<TextView>(R.id.user_name)
            val userEmail = findViewById<TextView>(R.id.user_email)
            val userPhone = findViewById<TextView>(R.id.user_phone)

            userName.text = "${user.firstName} ${user.lastName}"
            userEmail.text = user.email
            userPhone.text = user.phone

            // In a real app, you'd use a library like Glide or Coil to load the image
            userImage.setImageResource(R.mipmap.ic_launcher)
        }
    }
}