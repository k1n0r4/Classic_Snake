package com.example.snakegame

class Snake {
    companion object {
        var headX = 0f
        var headY = 0f
        var bodyParts = mutableListOf(arrayOf(0f, 0f))  // List to hold the body parts' positions
        var direction = "right"
        var alive = false

        // Checks if the snake is within the game bounds
        fun possibleMove(): Boolean {
            return !(headX < 0f || headX > 1000f || headY < 0f || headY > 1000f)
        }

        // Resets the snake's state
        fun reset() {
            headX = 0f
            headY = 0f
            bodyParts = mutableListOf(arrayOf(0f, 0f))  // Reset to a single starting body part
            direction = "right"
            alive = true
        }

        // Checks if the snake has collided with itself
        fun checkSelfCollision(): Boolean {
            for (i in 1 until bodyParts.size) {  // Start from the second part to avoid head checking itself
                if (bodyParts[i][0] == headX && bodyParts[i][1] == headY) {
                    return true
                }
            }
            return false
        }

        // Moves the snake in the current direction, updating head and body positions
        fun move() {
            // Update body parts to follow the head's previous positions
            for (i in bodyParts.size - 1 downTo 1) {
                bodyParts[i][0] = bodyParts[i - 1][0]
                bodyParts[i][1] = bodyParts[i - 1][1]
            }

            // Move the head in the current direction
            when (direction) {
                "up" -> headY -= 20f
                "down" -> headY += 20f
                "left" -> headX -= 20f
                "right" -> headX += 20f
            }

            // Update head position in body parts list
            bodyParts[0][0] = headX
            bodyParts[0][1] = headY
        }
    }
}
