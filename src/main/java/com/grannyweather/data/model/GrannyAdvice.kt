class GrannyAdvice {
    companion object {
        private val randomAdvice = listOf(
            "Don't forget to drink plenty of water!",
            "Have you had your breakfast yet, dear?",
            "Remember to call your mother!",
            "A cup of hot tea makes everything better.",
            "Don't stay up too late tonight!",
            "Make sure to eat your vegetables, sweetie."
        )

        fun getRandomAdvice(): String = randomAdvice.random()

        fun getTemperatureAdvice(tempDiff: Double): String = when {
            tempDiff > 5 -> "My goodness, it's much warmer today! You won't need that heavy coat."
            tempDiff > 2 -> "It's a bit warmer today, dear. A light jacket should do."
            tempDiff < -5 -> "Brrr! It's gotten much colder! Bundle up extra warm today!"
            tempDiff < -2 -> "It's a bit chillier today, love. Better take a warm sweater."
            else -> "Temperature's about the same as yesterday, dearie."
        }
    }
} 