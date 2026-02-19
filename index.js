const express = require('express');
const path = require('path');

const app = express();
const port = process.env.PORT || 3000;

const advicePool = [
  '3°C colder than yesterday, better wear a scarf!',
  'A little warmer today, no heavy coat needed.',
  'Looks rainy, take an umbrella and call me later.',
  'Windy outside — hold onto your hat, dear!'
];

app.use(express.static(path.join(__dirname, 'public')));

app.get('/api/mock-weather', (_, res) => {
  const temp = 18 + Math.random() * 12;
  const conditions = ['Sunny', 'Cloudy', 'Rainy', 'Windy'];
  const condition = conditions[Math.floor(Math.random() * conditions.length)];
  const advice = advicePool[Math.floor(Math.random() * advicePool.length)];

  res.json({
    location: 'Istanbul',
    temperatureCelsius: temp,
    description: condition,
    grannyAdvice: advice
  });
});

app.listen(port, () => {
  console.log(`Preview server running at http://localhost:${port}`);
});
