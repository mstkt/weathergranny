@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrannyWeatherTheme {
                GrannyWeatherNavigation()
            }
        }
    }
} 