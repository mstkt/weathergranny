@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var notificationTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var useAutoLocation by remember { mutableStateOf(true) }
    var grannyStyle by remember { mutableStateOf("Caring") }
    var useCelsius by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Notification Time Setting
        Text(
            text = "Notification Time",
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = {
                // Show time picker
            }
        ) {
            Text(text = "Set Time: ${notificationTime.format(DateTimeFormatter.ofPattern("HH:mm"))}")
        }
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Location Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Use Automatic Location",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = useAutoLocation,
                onCheckedChange = { useAutoLocation = it }
            )
        }
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Granny Style Selection
        Text(
            text = "Granny's Style",
            style = MaterialTheme.typography.titleMedium
        )
        RadioGroup(
            options = listOf("Caring", "Playful", "Concerned"),
            selectedOption = grannyStyle,
            onOptionSelected = { grannyStyle = it }
        )
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Temperature Unit Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Use Celsius",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = useCelsius,
                onCheckedChange = { useCelsius = it }
            )
        }
    }
} 