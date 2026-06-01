import { PrismaClient } from "@prisma/client";
import { PrismaPg } from "@prisma/adapter-pg";
import pool from "../db/index.ts";


const adapter = new PrismaPg(pool)
const prisma = new PrismaClient({ adapter })

export const fetchArticles = async () => {
    const response = await prisma.article.findMany({
        select: {
            id: true,
            title: true,
            description: true,
            tags: true,
            reading_time_minutes: true,
            cover_image: true,
        },
        orderBy: { published_at: "desc" }
    })
    return response
}

export const fetchArticleById = async (id: string) => {
    const articleId = parseInt(id)
    const article = await prisma.article.findUnique({
        where: { id: articleId },
        include: {
            user: true,
        }
    })
    return article
}

export const saveAiSummary = async (id: string, summary: string) => {
    const articleId = parseInt(id)
    await prisma.article.update({
        where: { id: articleId },
        data: { ai_summary: summary }
    })
}