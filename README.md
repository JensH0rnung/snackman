# SnackMan

**SnackMan** is a 3D multiplayer browser game — a Pac-Man-style maze where you play a chicken collecting snacks and eggs while ghosts chase you. Jump into a lobby, pick your role, and race for the top of the leaderboard.

> Built as a university software-engineering team project (winter semester 2024 @Hochschule RheinMain).

## Features
- Real-time **multiplayer** via WebSocket/STOMP (lobbies, role selection)
- Single-player and multiplayer modes
- 3D maze gameplay with **Three.js**
- Leaderboard, calorie overlay, i18n (multi-language)

## Tech stack
| Layer | Tech |
|---|---|
| Frontend | Vue 3, TypeScript, Vite, Three.js, Pinia, vue-router, vue-i18n |
| Backend | Spring Boot 3.3.5, Java 21, WebSocket/STOMP, REST |
| Game logic | Jython (Python 2.7) scripts |
| Quality | SonarQube, JaCoCo, JUnit, Mockito |

## Getting started

### Backend (Spring Boot)
```bash
./gradlew bootRun
```
`bootJar` automatically builds the frontend and copies it into `src/main/resources/public/`.

### Frontend (Vue 3 + Vite)
```bash
cd frontend
npm install
npm run dev
```

## Project structure
```
src/        Spring Boot backend (REST + WebSocket, game logic in Jython)
frontend/   Vue 3 + Three.js client
```

## Quality
- Unit tests with JUnit/Mockito, coverage via JaCoCo
- Static analysis via SonarQube (token supplied via CI/secrets, not committed)

