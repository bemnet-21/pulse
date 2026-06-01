import type { Request, Response } from 'express';
import { fetchArticleById, fetchArticles, saveAiSummary } from '../services/articles.service.js';
import { generateSummary } from '../services/ai.service.js';
import type { ArticleParam } from '../types.js';

export const getArticles = async (req: Request, res: Response) => {
    try {
        const articles = await fetchArticles()
        res.status(200).json({ 
            message : "Articles fetched successfully",
            data : articles
         })
    } catch (error) {
        res.status(500).json({ error: "An error occurred while fetching articles." });
    }
}

export const getArticleById = async (req: Request<ArticleParam>, res: Response) => {
    const articleId = req.params.id
    if(!articleId) return res.status(400).json({ error: "Article ID is required." })
    try {
        const article = await fetchArticleById(articleId)
        if(!article) return res.status(404).json({ error: "Article not found." })
        res.status(200).json({ 
            message : "Article fetched successfully",
            data : article
        })
    } catch(error) {
        res.status(500).json({ error: "An error occurred while fetching the article." });
    }
}

export const summarizeArticle = async (req: Request<ArticleParam>, res: Response) => {
    const articleId = req.params.id;
    if (!articleId) return res.status(400).json({ error: "Article ID is required." });

    try {
        const article = await fetchArticleById(articleId);
        if (!article) return res.status(404).json({ error: "Article not found." });

        if (article.ai_summary) {
            return res.status(200).json({
                message: "AI summary retrieved from cache.",
                data: { ai_summary: article.ai_summary }
            });
        }

        if (!article.body_html || article.body_html.length < 200) {
            return res.status(400).json({
                error: "Article body is missing or too short to summarize."
            });
        }

        const summary = await generateSummary(article.body_html);

        await saveAiSummary(articleId, summary);

        return res.status(200).json({
            message: "AI summary generated successfully.",
            data: { ai_summary: summary }
        });

    } catch (error: any) {
        return res.status(500).json({
            error: "AI service error: " + (error?.message || "Unknown error occurred.")
        });
    }
}