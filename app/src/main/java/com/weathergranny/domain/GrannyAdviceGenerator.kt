package com.weathergranny.domain

import com.weathergranny.data.model.AdviceTone
import com.weathergranny.data.model.TemperatureComparison
import com.weathergranny.data.model.WeatherCondition
import kotlin.random.Random

object GrannyAdviceGenerator {
    fun generateAdvice(
        condition: WeatherCondition,
        comparison: TemperatureComparison?,
        tone: AdviceTone
    ): String {
        val conditionLine = when (condition) {
            WeatherCondition.SNOWY -> "Wrap up warm, sweetie!"
            WeatherCondition.SUNNY -> "No need for a heavy coat today, dear!"
            WeatherCondition.WINDY -> "Hold onto your hat, love!"
            WeatherCondition.RAINY -> "Take an umbrella, or you'll catch a cold!"
            WeatherCondition.CLOUDY -> "It is a bit gloomy, bring a light layer."
            WeatherCondition.UNKNOWN -> "Check the sky once more before leaving."
        }

        val tempDiffLine = comparison?.let { "${it.summary(com.weathergranny.data.model.TemperatureUnit.CELSIUS)}." }
            ?: "No comparison with yesterday yet."

        val wisdomPool = when (tone) {
            AdviceTone.PLAYFUL -> listOf(
                "And do not skip breakfast, your tummy will protest!",
                "Pack a snack, you always forget and then get grumpy.",
                "Take sunglasses, dramatic squinting is not a style."
            )

            AdviceTone.CONCERNED -> listOf(
                "Please keep water with you and text when you arrive.",
                "Do not stay outside too long if weather gets rough.",
                "Take care of your throat; cold winds sneak up quickly."
            )

            AdviceTone.CARING -> listOf(
                "And don't forget your tea, it keeps you warm.",
                "A small scarf in your bag can save the day.",
                "Remember to rest a little this evening, dear."
            )
        }

        return "$tempDiffLine $conditionLine ${wisdomPool[Random.nextInt(wisdomPool.size)]}".trim()
    }
}
