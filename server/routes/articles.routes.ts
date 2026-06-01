import express from 'express'
import { getArticleById, getArticles, summarizeArticle } from '../controllers/articles.controller.js'

const router = express.Router()

router.get("/", getArticles)
router.get("/:id", getArticleById)
router.post("/:id/summarize", summarizeArticle)

export default router