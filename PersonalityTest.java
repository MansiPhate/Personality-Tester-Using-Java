import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class PersonalityTest {
    private static int score = 0;
    private static int questionIndex = 0;

    private static String[][] questions = {
        {"😎 How do you handle stress?", "Stay calm and think it through", "Talk to a friend", "Panic and overthink"},
        {"👥 Do you prefer working alone or in a team?", "Alone", "Team", "Depends on the task"},
        {"💡 How do you make decisions?", "Logically", "Emotionally", "Mix of both"},
        {"🎯 What motivates you the most?", "Achievements", "Helping others", "Creativity"},
        {"🌈 How do you feel about trying new things?", "Excited", "Anxious", "Depends on the situation"},
        {"🔍 How do you solve problems?", "Analytically", "Creatively", "By asking others"},
        {"❤️ How do you handle criticism?", "Take it positively", "Feel hurt but learn", "Ignore it"},
        {"😇 Are you more introverted or extroverted?", "Introverted", "Extroverted", "Ambivert"},
        {"🚀 How do you approach goals?", "Plan carefully", "Go with the flow", "Set a general direction"},
        {"🕰️ How do you manage your time?", "Strictly follow schedule", "Flexible", "Manage as needed"},
        {"😴 How do you relax?", "Meditation", "Talking to friends", "Watching shows"},
        {"🤯 How do you handle failure?", "Learn from it", "Feel down but try again", "Avoid thinking about it"},
        {"🎨 How creative are you?", "Highly creative", "Average", "Not creative"},
        {"💬 How do you communicate?", "Directly", "Diplomatically", "Through humor"},
        {"🎉 What’s your social style?", "Party animal", "Occasional socializer", "Prefer staying in"}
    };

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);

        server.createContext("/PersonalityTest/loadQuestions", (exchange -> {
            handleCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) return; // ⬅ Important!
            
            String response = loadQuestions();
            sendResponse(exchange, response);
        }));        

        server.createContext("/PersonalityTest/handleAnswer", (exchange -> {
            handleCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) return;
        
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int answer = Integer.parseInt(query.split("=")[1]);
                handleAnswer(answer);
                String response = loadQuestions();
                sendResponse(exchange, response);
            }
        }));

        server.createContext("/PersonalityTest/resetQuiz", (exchange -> {
            handleCors(exchange);
            resetQuiz();
            String response = loadQuestions();
            sendResponse(exchange, response);
        }));

        server.setExecutor(null);
        System.out.println("Server started on http://localhost:9090");
        server.createContext("/PersonalityTest/getResult", (exchange -> {
            handleCors(exchange);
            String response = getResult();
            sendResponse(exchange, response);
        }));        
        server.start();
    }

    private static void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }
    }    

    private static String loadQuestions() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < questions.length; i++) {
            json.append(String.format(
                "{\"question\": \"%s\", \"options\": [\"%s\", \"%s\", \"%s\"]}",
                questions[i][0],
                questions[i][1],
                questions[i][2],
                questions[i][3]
            ));
            if (i != questions.length - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }    

    private static void handleAnswer(int answer) {
        score += answer;
        questionIndex++;
    }

    private static String getResult() {
        String result;
        if (score <= 15) {
            result = "🧠 Personality Type: The Analyzer\n\n" +
                "You thrive in structured systems and intellectual spaces. With a mind built for strategy, you cut through noise with clarity and reason. Details are your playground.\n\n" +
                "✨ Strengths: Logic, focus, strategic thinking.\n" +
                "💔 Weaknesses: Can be rigid, emotionally distant.\n" +
                "💘 In Relationships: You’re grounded and reliable, but might need reminders to share your inner world.\n" +
                "🧑‍💼 Ideal Careers: Engineer, Financial Analyst, Software Architect, Risk Consultant.\n\n" +
                "🧠 Psych Insight: Like 'INTJs' and 'ISTJs', you lean into structure and mastery. You analyze before acting, preferring clarity over chaos. You’re often five steps ahead—quietly shaping the systems others rely on.";
        } else if (score <= 25) {
            result = "💖 Personality Type: The Empath\n\n" +
                "You feel deeply and love harder. Your heart is a magnet for authenticity, and your presence often feels like home to others.\n\n" +
                "✨ Strengths: Emotional awareness, creativity, compassion.\n" +
                "💔 Weaknesses: Easily drained, avoids confrontation.\n" +
                "💘 In Relationships: Romantic and intuitive—you're the kind who remembers small details and deep feelings.\n" +
                "🧑‍💼 Ideal Careers: Therapist, Social Worker, Art Teacher, Nonprofit Leader.\n\n" +
                "🧠 Psych Insight: Like 'INFPs' and 'INFJs', you're inner-world focused, driven by values and meaning. You seek emotional truth and depth—often sensing what others feel before they speak it.";
        } else if (score <= 35) {
            result = "🌈 Personality Type: The Dreamer\n\n" +
                "You're the poetic soul with a mind full of wonder. Daydreams aren’t distractions—they’re blueprints to your future. People find peace and playfulness in your vibe.\n\n" +
                "✨ Strengths: Imagination, optimism, resilience.\n" +
                "💔 Weaknesses: Avoids conflict, fears being misunderstood.\n" +
                "💘 In Relationships: Romantic and supportive—you paint a future together through love.\n" +
                "🧑‍💼 Ideal Careers: Animator, Writer, UX Designer, Storyteller.\n\n" +
                "🧠 Psych Insight: Like 'ISFPs' and 'ENFPs', you’re led by wonder and emotion. You find patterns in dreams and meaning in beauty—often seeing magic others miss.";
        } else if (score <= 45) {
            result = "🎯 Personality Type: The Visionary\n\n" +
                "You’re fueled by possibilities and progress. You’re a natural leader, innovator, and creative powerhouse—always scanning for what’s next.\n\n" +
                "✨ Strengths: Leadership, adaptability, charisma.\n" +
                "💔 Weaknesses: Impulsive, struggles with follow-through.\n" +
                "💘 In Relationships: Exciting and magnetic—you pull people into your orbit but need a grounded partner.\n" +
                "🧑‍💼 Ideal Careers: Startup Founder, Trend Analyst, Brand Manager, Futurist.\n\n" +
                "🧠 Psych Insight: Like 'ENTPs' and 'ENFPs', your energy thrives on novelty and expansion. You ideate endlessly, connect dots no one else sees, and inspire movement just by thinking out loud.";
        } else if (score <= 55) {
            result = "🔮 Personality Type: The Mystic\n\n" +
                "You’re the quiet observer with ancient energy. Introspective and soulful, you bring peace, purpose, and presence to every room you enter.\n\n" +
                "✨ Strengths: Intuition, empathy, mindfulness.\n" +
                "💔 Weaknesses: Overthinks purpose, can withdraw too deeply.\n" +
                "💘 In Relationships: You seek sacred, authentic connection—not surface-level noise.\n" +
                "🧑‍💼 Ideal Careers: Meditation Coach, Poet, Holistic Therapist, Spiritual Guide.\n\n" +
                "🧠 Psych Insight: Like 'INFJs' and 'ISFJs', you carry a deep sense of mission. You feel life’s undercurrents and often serve as a quiet healer—seeing through masks with uncanny clarity.";
        } else if (score <= 65) {
            result = "🧬 Personality Type: The Alchemist\n\n" +
                "You mix vision with intuition and insight with action. Whether it’s work, love, or art—you weave magic wherever you go.\n\n" +
                "✨ Strengths: Balance, creative intelligence, leadership.\n" +
                "💔 Weaknesses: Hard to pin down, juggles too much.\n" +
                "💘 In Relationships: You’re transformative and devoted—helping your partner evolve.\n" +
                "🧑‍💼 Ideal Careers: Consultant, Brand Architect, Creative Coach, Innovation Lead.\n\n" +
                "🧠 Psych Insight: Like 'ENFJs' and 'ENTPs', you blend intuition and charisma to guide others. You’re a catalyst for growth—seeing not just who someone is, but who they could become.";
        } else if (score <= 75) {
            result = "🔥 Personality Type: The Rebel\n\n" +
                "Rules? You write your own. You're bold, disruptive, and unapologetically you. You challenge the norm and spark revolutions with your vibe.\n\n" +
                "✨ Strengths: Boldness, authenticity, risk-taking.\n" +
                "💔 Weaknesses: Easily bored, clashes with authority.\n" +
                "💘 In Relationships: Passionate, exciting, intense—but needs space to stay free.\n" +
                "🧑‍💼 Ideal Careers: Activist, Musician, Trend Creator, Political Strategist.\n\n" +
                "🧠 Psych Insight: Like 'ENTPs' and 'ESTPs', you're wired for action and disruption. You process the world through rapid experimentation and big-picture vision, thriving in chaos others fear.";
        } else {
            result = "🌱 Personality Type: The Nurturer\n\n" +
                "You care. Deeply. You bring growth, safety, and a sense of calm to everyone around you. You’re the steady heart of your community.\n\n" +
                "✨ Strengths: Loyalty, patience, emotional presence.\n" +
                "💔 Weaknesses: Self-sacrificing, avoids conflict.\n" +
                "💘 In Relationships: Loving and committed—you build love slowly and intentionally.\n" +
                "🧑‍💼 Ideal Careers: Teacher, Nurse, Wellness Coach, Family Therapist.\n\n" +
                "🧠 Psych Insight: Like 'ISFJs' and 'ESFJs', your strength is in showing up—consistently and with love. You anchor others with care and calm, even when the world is spinning.";
        }                
    
        // Escape quotes and line breaks for JSON-like format (still plain string)
        return "{\"result\": \"" + result.replace("\n", "\\n").replace("\"", "\\\"") + "\"}";
    }    

    private static void resetQuiz() {
        score = 0;
        questionIndex = 0;
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }    
}
