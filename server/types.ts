export interface Article {
    id: number;
    title: string;
    description: string;
    tags: string;
    reading_time_minutes: number;
    cover_image: string;
    body_html: string;
}

export interface ArticleParam {
    id: string;
}