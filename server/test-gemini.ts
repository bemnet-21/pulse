import axios from 'axios';
import "dotenv/config";

async function listModels() {
    const key = process.env.GEMINI_API_KEY;
    const url = `https://generativelanguage.googleapis.com/v1/models?key=${key}`;
    try {
        const res = await axios.get(url);
        console.log("Available Models:", res.data.models.map((m: any) => m.name));
    } catch (e: any) {
        console.error(e.response?.data || e.message);
    }
}
listModels();