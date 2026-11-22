# Vibe-weather

A minimal, personal weather app: one screen, hourly rain probability for the
next 20 hours, and a 7-day outlook. No ads, no accounts, no bloat.

## Status

Planning phase — design decisions locked on 2026-11-22:

- Stack: Kotlin + Jetpack Compose
- UI: immersive, weather-reactive scenes
- Data: pluggable `WeatherProvider` seam; Open-Meteo + QWeather adapters
  shipped together and switchable in-app for comparison

The authoritative plan (Chinese) lives in [`docs/PLAN.md`](docs/PLAN.md).

## Structure

```
Vibe-weather/
├── AGENTS.md        # repo rules for AI agents
├── README.md
└── docs/
    └── PLAN.md      # design & architecture plan
```

## License

TBD
