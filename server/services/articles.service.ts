import { articles } from "../data.js"

export const fetchArticles = () => {
    return articles
}

export const fetchArticleById = (id: string) => {
    const articleId = parseInt(id)
    const article = articles.find(a => a.id === articleId)
    return article
}