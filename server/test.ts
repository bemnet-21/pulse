import { PrismaClient } from '@prisma/client';
import { PrismaPg } from '@prisma/adapter-pg';
import pool from "./db/index.ts";

const adapter = new PrismaPg(pool);
const prisma = new PrismaClient({  adapter });

async function bruteForce() {
  try {
    console.log("🚀 Attempting to save a test article...");

    const article = await prisma.article.upsert({
      where: { id: 12345 },
      update: {},
      create: {
        id: 12345,
        title: "Database Test Article",
        description: "If you see this, the database is working!",
        tags: "test,database",
        reading_time_minutes: 5,
        cover_image: "https://picsum.photos/200",
        body_html: "<h1>Success!</h1><p>The curation engine is now linked to Postgres.</p>",
        published_at: "new Date()",
        user: {
          connectOrCreate: {
            where: { id: 1 },
            create: {
              id: 1,
              name: "Test User",
                profile_image: "https://picsum.photos/50",
            }
          }
        }
      },
    });

    console.log("✅ Article Saved Successfully:", article.title);
    
    const count = await prisma.article.count();
    console.log(`📊 New Article Count: ${count}`);

  } catch (error) {
    console.error("❌ Failed to save article:");
    console.error(error);
  } finally {
    await prisma.$disconnect();
  }
}

bruteForce();