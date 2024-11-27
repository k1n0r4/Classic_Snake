package com.example.snakegame

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private var score = 0  // Track score
    private var isPaused = false  // Track pause state
    private var gameJob: Job? = null  // Coroutine job to control the snake movement
    private var snakeSpeed = 150L  // Initial speed (150 ms delay)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.hide()

        // Set score text color to white
        score_text.setTextColor(Color.WHITE)
        score_text.text = getString(R.string.score_text, score)

        // Touch control setup
        canvas.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeLeft() { changeDirection("left") }
            override fun onSwipeRight() { changeDirection("right") }
            override fun onSwipeTop() { changeDirection("up") }
            override fun onSwipeBottom() { changeDirection("down") }
        })

        startGame()  // Start the game loop

        // Control buttons
        button_up.setOnClickListener { changeDirection("up") }
        button_down.setOnClickListener { changeDirection("down") }
        button_left.setOnClickListener { changeDirection("left") }
        button_right.setOnClickListener { changeDirection("right") }
        exit.setOnClickListener { finish(); finishAffinity(); System.exit(0) }

        button_pause.setOnClickListener { togglePause() }  // Pause/Resume button
    }

    // Change direction only if not opposite to current direction
    private fun changeDirection(newDirection: String) {
        if (Snake.direction == "idle" ||
            (newDirection == "left" && Snake.direction != "right") ||
            (newDirection == "right" && Snake.direction != "left") ||
            (newDirection == "up" && Snake.direction != "down") ||
            (newDirection == "down" && Snake.direction != "up")
        ) {
            Snake.alive = true
            Snake.direction = newDirection
        }
    }

    // Toggle pause and resume
    private fun togglePause() {
        isPaused = !isPaused
        if (isPaused) {
            gameJob?.cancel()
            button_pause.text = "Resume"
        } else {
            startGame()
            button_pause.text = "Pause"
        }
    }

    // Start the game loop
    private fun startGame() {
        gameJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                while (Snake.alive && !isPaused) {
                    moveSnake()
                    if (checkCollision()) {
                        resetGame()
                    }
                    canvas.invalidate()
                    delay(snakeSpeed)  // Use variable for dynamic speed
                }
            }
        }
    }

    // Move the snake based on its direction
    private fun moveSnake() {
        when (Snake.direction) {
            "up" -> Snake.headY -= 50
            "down" -> Snake.headY += 50
            "left" -> Snake.headX -= 50
            "right" -> Snake.headX += 50
        }
        Snake.bodyParts.add(arrayOf(Snake.headX, Snake.headY))

        // Check if snake eats food
        if (Snake.headX == Food.posX && Snake.headY == Food.posY) {
            Food.generate()
            score++
            updateScore()

            // Adjust speed dynamically
            if (snakeSpeed > 50) {  // Limit minimum delay to 50ms
                snakeSpeed -= 10  // Increase speed as score increases
            }
        } else {
            Snake.bodyParts.removeAt(0)
        }
    }

    // Check for wall and self-collision
    private fun checkCollision(): Boolean {
        if (!Snake.possibleMove()) return true
        for (i in 0 until Snake.bodyParts.size - 1) {
            if (Snake.headX == Snake.bodyParts[i][0] && Snake.headY == Snake.bodyParts[i][1]) {
                return true
            }
        }
        return false
    }

    private fun displayGameOver() {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@MainActivity, "Game Over", Toast.LENGTH_SHORT).show()
        }
    }

    // Reset game on collision
    private fun resetGame() {
        displayGameOver()  // Display "Game Over" message
        Snake.alive = false
        Snake.reset()
        score = 0
        snakeSpeed = 150L  // Reset speed to initial value
        updateScore()
        Snake.direction = "idle"  // Set direction to idle after reset
    }

    // Update score on the main thread
    private fun updateScore() {
        CoroutineScope(Dispatchers.Main).launch {
            score_text.text = getString(R.string.score_text, score)
        }
    }

    // Swipe listener
    open class OnSwipeTouchListener : View.OnTouchListener {
        private val gestureDetector = GestureDetector(GestureListener())

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return gestureDetector.onTouchEvent(event)
        }

        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onDown(e: MotionEvent) = true

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY) && abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) onSwipeRight() else onSwipeLeft()
                } else if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) onSwipeBottom() else onSwipeTop() // Swapped here
                }
                return true
            }
        }

        open fun onSwipeRight() {}
        open fun onSwipeLeft() {}
        open fun onSwipeTop() {}
        open fun onSwipeBottom() {}
    }
}
