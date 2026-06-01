import { GoogleGenerativeAI } from '@google/generative-ai';

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY!);
export const generateSummary = async (content: string): Promise<string> => {
    const truncated = content.slice(0, 4000);

    const model = genAI.getGenerativeModel({ model: 'gemini-2.5-flash' });

    const prompt = `Summarize this engineering article into exactly 3 technical bullet points for a developer. Use a professional, minimalist tone. Return only the bullet points.\n\n${truncated}`;

    try {
        const result = await model.generateContent(prompt);
        return result.response.text();
    } catch (error: any) {
        throw error;
    }
};
