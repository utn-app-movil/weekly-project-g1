package com.example.semana4_ever

import Util.Util
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnShowMsg1 = findViewById<Button>(R.id.btnShowMsg1)
        btnShowMsg1.setOnClickListener(View.OnClickListener{ view ->
            Toast.makeText(this, getString(R.string.Msg1)
                , Toast.LENGTH_LONG).show()
        })

        val clmain = findViewById<ConstraintLayout>(R.id.main)
        class myUndoListener: View.OnClickListener{
            override fun onClick(v: View?) {
                Snackbar.make(clmain, R.string.UndoMsg
                    , Snackbar.LENGTH_LONG).show()
            }
        }

        val btnMgs2 = findViewById<Button>(R.id.btnShowMsg2)
        btnMgs2.setOnClickListener(View.OnClickListener{ view ->
            val mySnakbar = Snackbar.make(clmain, R.string.Msg2
                , Snackbar.LENGTH_LONG)
            mySnakbar.setAction(R.string.UndoAction,myUndoListener())
            mySnakbar.show()
        })

        val btnOpenActivity = findViewById<Button>(R.id.btnOpenActivityMain)
        btnOpenActivity.setOnClickListener(View.OnClickListener{ view ->
           Util.openActivity(this
               , SecondActivity::class.java)
        })
    }
}