---
title: PlanPal
emoji: 🗺️
colorFrom: blue
colorTo: indigo
sdk: docker
app_port: 8080
pinned: false
---

# PlanPal Web

A mobile web front end for [PlanPal](https://github.com/sarahhalime/CSC207-Project), the
group trip planner built for CSC207.

The desktop app is Java Swing, which cannot run in a browser or on a phone. This project
keeps the entire business core unchanged and replaces only the delivery mechanism: instead
of Swing views calling controllers, HTTP handlers call the same interactors and return JSON.

## Why this was possible

The original project follows Clean Architecture, so the inner layers have no UI dependency
at all:

| Layer | Files | Depends on Swing/AWT |
|---|---|---|
| `entity` | 26 | 0 |
| `use_case` | 264 | 0 |
| `data_access` | 18 | 0 |
| `view` (desktop only) | 96 | 88 |

All 308 files of business logic were reused as-is. Only the outermost layer was rewritten.

## Running locally

```bash
mvn package
java -jar target/planpal-web.jar
```

Then open <http://localhost:8080>.

The app reads `PlanPalDatabase/` from the working directory, the same as the desktop app.
Copy that folder next to the jar before starting.

To try it from a phone on the same Wi-Fi, find your machine's LAN address and visit
`http://<your-ip>:8080`.

## Configuration

| Variable | Purpose |
|---|---|
| `PORT` | Port to listen on. Defaults to `8080`. Most hosts set this for you. |
| `GEMINI_API_KEY` / `GOOGLE_API_KEY` | Location insights |
| `GOOGLE_MAPS_API_KEY` | Place autocomplete |

Keys may also be placed in a `.env` file beside the jar. **Never commit that file.**

## API

| Method | Path | Returns |
|---|---|---|
| `POST` | `/api/login` | The user, their preferred currency, and their events |
| `GET` | `/api/event?id={id}` | One event with its expenses, activities, budget and balances |
| `POST` | `/api/expense/add` | Adds an expense, then returns the reloaded event |
| `POST` | `/api/expense/pay` | Settles an expense, then returns the reloaded event |
| `GET` | `/api/weather?id={id}` | Forecast for the event's location and dates |
| `GET` | `/api/insight?id={id}` | Gemini planning scores and tags for the location |
| `GET` | `/api/itinerary?id={id}` | The activity timeline with travel estimates |

The last three each call a remote service, so the client requests them separately once the
dashboard is already on screen. A slow or unavailable service costs its own panel only.

## Deploying

```bash
docker build -t planpal-web .
docker run -p 8080:8080 planpal-web
```

The image builds the jar and bakes in the seeded demo data, so a fresh container has
something to show immediately.

## Status

Every use case in the desktop app is reachable from the phone except Google sign-in, which
needs a redirect URI registered against the deployed domain rather than the desktop's
localhost callback.

Two things still differ from the desktop. Custom splits are entered as an even split
between whoever you pick, rather than per-person amounts. And there is no session token:
the browser holds the signed-in user in memory for the page's lifetime, so this is a demo
front end, not something to point at real accounts. Anything the container writes is lost
on redeploy.

The event map prefers Google, but Google Maps Platform refuses every request unless billing
is enabled on the Cloud project. When that happens it falls back to Nominatim, OpenStreetMap's
own geocoder, which needs no key. The browser draws the map itself from an OpenStreetMap
embed, so the map works on a deployment with no Google account at all.
