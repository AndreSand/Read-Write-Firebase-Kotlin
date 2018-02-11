package com.kotlinmap.andres.database_firebase_kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.toast
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
val TAG= "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn_click_me = findViewById(R.id.button2) as Button

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        txView.setText("Hello")

        //Updating database
        myRef.setValue("Hello, World! Andres")

        // set on-click listener
        btn_click_me.setOnClickListener {
            // Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            toast("updating realtime database Andres111");
            myRef.setValue("Hello, World! Andres111")

            // Read from the database
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = dataSnapshot.getValue(String::class.java)
                    Log.d(TAG, "Value is: " + value!!)
                    txView.setText(value)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })

        }



    }


}
