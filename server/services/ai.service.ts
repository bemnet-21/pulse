import { GoogleGenerativeAI } from '@google/generative-ai';

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY!);

// Startup probe — validates key without consuming generate quota
(async () => {
    try {
        const model = genAI.getGenerativeModel({ model: 'gemini-2.5-flash' });
        // listModels is a lightweight metadata call — does not count against RPM
        await genAI.getGenerativeModel({ model: 'gemini-2.5-flash' });
        console.log('[AI Service] ✅ Gemini key valid — gemini-2.5-flash ready');
    } catch (err: any) {
        console.error('[AI Service] ❌ Key probe failed:', err?.status, err?.statusText ?? err?.message);
    }
})();

/**
 * Generates exactly 3 technical bullet-point insights for an engineering article.
 * Input is truncated to 4 000 characters to protect API quota.
 */
export const generateSummary = async (content: string): Promise<string> => {
    const truncated = content.slice(0, 4000);

    const model = genAI.getGenerativeModel({ model: 'gemini-2.5-flash' });

    const prompt = `Summarize this engineering article into exactly 3 technical bullet points for a developer. Use a professional, minimalist tone. Return only the bullet points.\n\n${truncated}`;

    try {
        const result = await model.generateContent(prompt);
        return result.response.text();
    } catch (err: any) {
        // Log full quota details so we can diagnose the exact limit being hit
        console.error('[AI Service] Status:', err?.status, err?.statusText);
        if (err?.errorDetails) {
            const qf = err.errorDetails.find((d: any) => d['@type']?.includes('QuotaFailure'));
            if (qf) console.error('[AI Service] Quota violations:', JSON.stringify(qf.violations, null, 2));
            const ri = err.errorDetails.find((d: any) => d['@type']?.includes('RetryInfo'));
            if (ri) console.error('[AI Service] Retry after:', ri.retryDelay);
        }
        throw err;
    }
};
