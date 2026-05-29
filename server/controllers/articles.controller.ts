import type { Request, Response } from 'express';
import { fetchArticleById, fetchArticles } from '../services/articles.service.js';
import type { ArticleParam } from '../types.js';

export const getArticles = (req: Request, res: Response) => {
    try {
        const articles = fetchArticles()
        res.status(200).json({ 
            message : "Articles fetched successfully",
            data : articles
         })
    } catch (error) {
        res.status(500).json({ error: "An error occurred while fetching articles." });
    }
}

export const getArticleById = (req: Request<ArticleParam>, res: Response) => {
    const articleId = req.params.id
    if(!articleId) return res.status(400).json({ error: "Article ID is required." })
    try {
        const article = fetchArticleById(articleId)
        if(!article) return res.status(404).json({ error: "Article not found." })
        res.status(200).json({ 
            message : "Article fetched successfully",
            data : article
        })
    } catch(error) {
        res.status(500).json({ error: "An error occurred while fetching the article." });
    }
}                   