import express from 'express'
import type { Request, Response } from 'express'
import { getArticleById, getArticles } from '../controllers/articles.controller.js'

const router = express.Router()

router.get("/", getArticles)
router.get("/:id", getArticleById)


export default router