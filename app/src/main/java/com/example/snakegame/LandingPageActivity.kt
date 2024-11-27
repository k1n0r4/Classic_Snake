package com.example.snakegame

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_landing_page.*

class LandingPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // Start Game button click listener
        start_game.setOnClickListener {
            // Launch the main game activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Exit Game button click listener
        exit_game.setOnClickListener {
            // Close the app
            finish()
            System.exit(0)
        }
    }
}
