export interface User {
    id: number;
    name: string;
    profile_image: string;
}
export interface Article {
    id: number;
    title: string;
    description: string;
    tags: string;
    tag_list: string[];
    reading_time_minutes: number;
    cover_image?: string;
    social_image?: string;
    body_html?: string;
    published_at: string;
    user: User;
}

export interface ArticleParam {
    id: string;
}