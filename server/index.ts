import express from 'express';
import type {  Request, Response } from 'express';
import cron from 'node-cron'
import curateArticles from './curate.js';

import articlesRouter from './routes/articles.routes.js';

const app = express();
const port = 8080;

app.listen(port, () => {
    console.log("Server listening on port " + port);
})

cron.schedule('0 */6 * * *', () => {
    console.log("Running scheduled curation")
    curateArticles()
})

app.use('/api/v1/articles', articlesRouter)
