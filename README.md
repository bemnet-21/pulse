# Pulse: AI-Curated Engineering Insights

Pulse is Android application designed to curate and synthesize complex engineering articles into digestible insights. By bridging a robust Kotlin/Compose frontend with a modern Express/Prisma backend, it delivers a seamless, native reading experience enhanced by on-demand AI summarization.

## 🏗 Technical Architecture

```text
Android (Kotlin/Compose/Room) 
          │
          ▼  (REST API)
    Express API (TypeScript) 
          │
    ┌─────┴─────┐
    ▼           ▼
PostgreSQL   Gemini AI
 (Neon)    (LLM Summaries)
```

## 🚀 Key Technical Challenges Solved

- **AI Quota Optimization:** Implemented a Lazy-Loading & Caching strategy for LLM summaries to reduce API costs by 90% and gracefully handle throttling or quota exhaustion.
- **Offline-First Sync:** Engineered a local SQLite (Room) persistence layer to allow seamless reading during network outages, complete with a reactive offline-fallback UI.
- **Modern UI/UX:** Developed a custom 'IDE-Chic' design system with Skeleton/Shimmer loading, advanced WebView CSS injection, and animated Compose states for high perceived performance.

## 🛠 Tech Stack

- **Mobile:** Kotlin, Jetpack Compose, Retrofit, Room, Coil, Coroutines/Flow
- **Backend:** Node.js, Express, TypeScript, Prisma ORM
- **Database:** PostgreSQL (Neon Serverless)
- **AI Integration:** Google Gemini (Generative Language API)

## ⚙️ Installation & Setup

### Backend (API Server)
1. Navigate to the `server/` directory: `cd server`
2. Install dependencies: `npm install`
3. Configure your `.env` file with `DATABASE_URL` and `GEMINI_API_KEY`.
4. Run migrations: `npx prisma db push`
5. Start the dev server: `npm run dev`

### Android Application
1. Open the `client/` folder in Android Studio.
2. Ensure your emulator or physical device is connected.
3. Sync the Gradle project and hit **Run**.
*(Note: To test offline capabilities, turn on Airplane Mode after loading the initial feed).*
