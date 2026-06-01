import "dotenv/config"
import axios from "axios"
import { PrismaClient } from "@prisma/client"
import { PrismaPg } from "@prisma/adapter-pg"
import type { Article } from "./types.js"
import pool from "./db/index.ts"

const adapter = new PrismaPg(pool)
const prisma = new PrismaClient({ adapter })

function normalizeTags(detail: Article): string {
    const tag_list = (detail as Article & { tag_list?: unknown }).tag_list
    if (Array.isArray(tag_list)) {
        return tag_list.filter((tag): tag is string => typeof tag === "string").join(",")
    }

    const tags = (detail as Article & { tags?: unknown }).tags
    if (Array.isArray(tags)) {
        return tags.filter((tag): tag is string => typeof tag === "string").join(",")
    }

    if (typeof tags === "string") {
        return tags
    }

    return ""
}

function resolveUserId(detail: Article): number {
    const user = (detail as Article & { user?: { id?: unknown; user_id?: unknown } }).user
    const idCandidate = user?.id ?? user?.user_id

    if (typeof idCandidate === "number") {
        return idCandidate
    }

    throw new Error(`Missing numeric user id for article ${detail.id}`)
}

async function curateArticles() {
    try {
        const response = await axios.get("https://dev.to/api/articles?per_page=10&top=7", {
            headers: { 'User-Agent': 'Pulse-App' }
        })
        const articles: Article[] = response.data

        for (const article of articles) {
            const detailResp = await axios.get(`https://dev.to/api/articles/${article.id}`, {
                headers: { 'User-Agent': 'Pulse-App' }
            })
            const detail: Article = detailResp.data
            const userId = resolveUserId(detail)

            await prisma.article.upsert({
                where: { id: detail.id },
                update: {}, 
                create: {
                    id: detail.id,
                    title: detail.title,
                    description: detail.description,
                    tags: normalizeTags(detail),
                    reading_time_minutes: detail.reading_time_minutes,
                    cover_image: detail.cover_image ?? null,
                    social_image: detail.social_image ?? null,
                    body_html: detail.body_html ?? null,
                    published_at: detail.published_at,
                    user: {
                        connectOrCreate: {
                            where: { id: userId },
                            create: {
                                id: userId,
                                name: detail.user.name,
                                profile_image: detail.user.profile_image ?? null,
                            }
                        }
                    },
                },
            })
        }

    } catch(err) {
    } finally {
        await prisma.$disconnect()
    }
}

curateArticles()

export default curateArticles