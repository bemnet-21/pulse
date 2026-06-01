import axios from 'axios';
import "dotenv/config";

async function listModels() {
    const key = process.env.GEMINI_API_KEY;
    const url = `https://generativelanguage.googleapis.com/v1/models?key=${key}`;
    try {
        await axios.get(url);
    } catch (e: any) {
    }
}
listModels();