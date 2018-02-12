package com.kotlinmap.andres.database_firebase_kotlin

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra("name")
        val url = intent.getStringExtra("url")

        textView.setText("name: " + name + "\n" + ", url: " + url)

        Picasso.with(this@DetailActivity)
                .load(url)
                .error(R.drawable.common_google_signin_btn_icon_dark)
                .into(imageView)

        fab.setOnClickListener { view ->
            Snackbar.make(view, ""+name, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
