import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class PersonalityTest {
    private static int score = 0;
    private static int questionIndex = 0;

    private static final String[][] questions = {
        {"🌟 How do you typically recharge?", "Spending time alone", "Being around others", "Depends on mood"},
        {"🧠 What's your decision-making style?", "Logical and structured", "Intuitive and emotional", "Flexible"},
        {"🎨 How do you express yourself creatively?", "Through planning and structure", "Emotionally and freely", "In spontaneous bursts"},
        {"💬 How do you handle difficult conversations?", "Direct and honest", "Diplomatic and gentle", "Avoid them"},
        {"🔥 How do you react under pressure?", "Calm and analytical", "Driven and energetic", "Emotional or overwhelmed"},
        {"🧩 When solving problems, you prefer:", "Step-by-step reasoning", "Thinking outside the box", "Working with others"},
        {"❤️ How do you connect with people emotionally?", "Cautiously but deeply", "Openly and empathetically", "Lightheartedly"},
        {"🚀 How do you pursue goals?", "With detailed plans", "By following intuition", "Adaptively and flexibly"},
        {"🌍 In social settings, you're usually:", "Reserved but observant", "Outgoing and lively", "Somewhere in-between"},
        {"🎯 What drives your ambition?", "Achievement and mastery", "Helping others", "Exploring new things"},
        {"🌱 How do you grow through failure?", "Analyze and improve", "Feel and reflect", "Let it go and move on"},
        {"💭 How often do you reflect on your feelings?", "Often and deeply", "Sometimes", "Rarely"},
        {"🎉 At a party, you are most likely to:", "Observe and listen", "Be the center of attention", "Float between groups"},
        {"🔮 What matters more?", "Facts and truth", "Meaning and connection", "Possibility and experience"},
        {"🧘 How do you find peace?", "Through structure", "Through inner awareness", "Through experiences"}
    };

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);

        server.createContext("/PersonalityTest/loadQuestions", exchange -> {
            handleCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) return;
            sendResponse(exchange, loadQuestions());
        });

        server.createContext("/PersonalityTest/handleAnswer", exchange -> {
            handleCors(exchange);
            if ("OPTIONS".equals(exchange.getRequestMethod())) return;

            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                int answer = Integer.parseInt(query.split("=")[1]);
                handleAnswer(answer);
                sendResponse(exchange, loadQuestions());
            }
        });

        server.createContext("/PersonalityTest/getResult", exchange -> {
            handleCors(exchange);
            sendResponse(exchange, getResult());
        });

        server.createContext("/PersonalityTest/resetQuiz", exchange -> {
            handleCors(exchange);
            resetQuiz();
            sendResponse(exchange, loadQuestions());
        });

        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server running at http://localhost:9090");
    }

    private static void handleCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
        }
    }

    private static String loadQuestions() {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < questions.length; i++) {
            json.append(String.format(
                "{\"question\": \"%s\", \"options\": [\"%s\", \"%s\", \"%s\"]}",
                questions[i][0], questions[i][1], questions[i][2], questions[i][3]
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

    private static void resetQuiz() {
        score = 0;
        questionIndex = 0;
    }

    private static String getResult() {
        String result;

        if (score <= 15) {
            result = "🔍 Personality Type: The Analyst\n\n" +
                    "You’re driven by reason and clarity. You find comfort in facts, frameworks, and long-term goals.\n\n" +
                    "✨ Strengths: Logical, strategic, efficient\n" +
                    "💔 Weaknesses: Can seem cold or overly critical\n" +
                    "💘 Relationships: Reserved but loyal. You express love through action, not words.\n" +
                    "🧑‍💼 Ideal Careers: Engineer, Economist, Researcher, Analyst\n\n" +
                    "🧠 Insight: You thrive in patterns and processes. Like INTJs or ISTJs, you're a master of systems and foresight.";
        } else if (score <= 25) {
            result = "💖 Personality Type: The Empath\n\n" +
                    "You feel the emotional undercurrent of every situation. Deep connection is your core language.\n\n" +
                    "✨ Strengths: Emotionally intelligent, nurturing, intuitive\n" +
                    "💔 Weaknesses: Overextends for others, conflict avoidant\n" +
                    "💘 Relationships: You love deeply, often sensing others' needs before they say them.\n" +
                    "🧑‍💼 Ideal Careers: Therapist, Social Worker, Artist, Educator\n\n" +
                    "🧠 Insight: Like INFPs or INFJs, your compass is compassion. You bring healing into any space.";
        } else if (score <= 35) {
            result = "🌈 Personality Type: The Creator\n\n" +
                    "You're inspired by imagination and driven to build new things. Creativity is your currency.\n\n" +
                    "✨ Strengths: Visionary, expressive, adaptable\n" +
                    "💔 Weaknesses: Easily bored, emotionally volatile\n" +
                    "💘 Relationships: Passionate and open. You thrive with someone who encourages your originality.\n" +
                    "🧑‍💼 Ideal Careers: Designer, Writer, Performer, Innovator\n\n" +
                    "🧠 Insight: Like ENFPs or ISFPs, you live in possibility. Your mind is a canvas always creating.";
        } else if (score <= 45) {
            result = "🧭 Personality Type: The Explorer\n\n" +
                    "You crave freedom, adventure, and growth. Rules are suggestions to you.\n\n" +
                    "✨ Strengths: Independent, curious, bold\n" +
                    "💔 Weaknesses: Impatient, struggles with routine\n" +
                    "💘 Relationships: Free-spirited. You bring excitement but need personal space to thrive.\n" +
                    "🧑‍💼 Ideal Careers: Travel Blogger, Entrepreneur, Filmmaker, Scientist\n\n" +
                    "🧠 Insight: Like ENTPs or ESTPs, you're always seeking 'what’s next.' Life is your playground.";
        } else if (score <= 55) {
            result = "🧬 Personality Type: The Architect\n\n" +
                    "You build not just ideas but systems, lives, and structures that last.\n\n" +
                    "✨ Strengths: Detail-oriented, organized, disciplined\n" +
                    "💔 Weaknesses: Rigid under change, perfectionistic\n" +
                    "💘 Relationships: You show love through loyalty and planning your future together.\n" +
                    "🧑‍💼 Ideal Careers: Architect, Project Manager, Programmer, Engineer\n\n" +
                    "🧠 Insight: Like ISTJs and ESTJs, you bring order from chaos. Your calm presence creates stability.";
        } else {
            result = "🔮 Personality Type: The Mystic\n\n" +
                    "You walk through the world with quiet depth and soul. You see the unseen.\n\n" +
                    "✨ Strengths: Insightful, reflective, wise\n" +
                    "💔 Weaknesses: Overthinks, isolates\n" +
                    "💘 Relationships: Seeks a soulmate, not a fling. Meaning > surface.\n" +
                    "🧑‍💼 Ideal Careers: Counselor, Poet, Spiritual Guide, Writer\n\n" +
                    "🧠 Insight: Like INFJs or ISFPs, you feel the emotional layers most ignore. You're here to connect and awaken.";
        }

        return "{\"result\": \"" + result.replace("\n", "\\n").replace("\"", "\\\"") + "\"}";
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
