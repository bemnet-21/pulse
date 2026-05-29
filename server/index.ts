import express from 'express';
import type {  Request, Response } from 'express';

import articlesRouter from './routes/articles.routes.js';

const app = express();
const port = 8080;

app.listen(port, () => {
    console.log("Server listening on port " + port);
})

app.use('/api/v1/articles', articlesRouter)
