import express from 'express';
import cron from 'node-cron'
import curateArticles from './curate.js';

import articlesRouter from './routes/articles.routes.js';

const app = express();
const port = 8080;

app.use(express.json());

app.get('/api/v1/health', (_req, res) => {
    res.status(200).json({ status: 'ok' });
})

app.listen(port, () => {
})

cron.schedule('0 */6 * * *', () => {
    curateArticles()
})

app.use('/api/v1/articles', articlesRouter)
