class WeatherNotificationWorker(
    context: Context,
    params: WorkParameters,
    private val weatherRepository: WeatherRepository,
    private val preferencesRepository: PreferencesRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val currentWeather = weatherRepository.getCurrentWeather()
            val yesterdayWeather = preferencesRepository.getYesterdayWeather()
            val tempDiff = currentWeather.currentTemp - (yesterdayWeather?.currentTemp ?: currentWeather.currentTemp)
            
            val grannyMessage = "${GrannyAdvice.getTemperatureAdvice(tempDiff)}\n${GrannyAdvice.getRandomAdvice()}"
            
            createNotification(grannyMessage)
            
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
    
    private fun createNotification(message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Granny's Weather Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_granny)
            .setContentTitle("Granny's Weather Advice")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
            
        notificationManager.notify(1, notification)
    }
    
    companion object {
        private const val CHANNEL_ID = "granny_weather_channel"
        
        fun scheduleDaily(context: Context, hour: Int, minute: Int) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            
            val dailyWorkRequest = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(
                    calendar.timeInMillis - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
                .build()
                
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "weather_notification",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    dailyWorkRequest
                )
        }
    }
} 