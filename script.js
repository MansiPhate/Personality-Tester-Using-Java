let totalQuestions = 15;
let currentQuestionIndex = 0;
let userName = '';
let answers = [];
let questions = [];

document.addEventListener('DOMContentLoaded', () => {
    console.log("🚀 Quiz initialized!");

    const startScreen = document.getElementById('start-screen');
    const quizContainer = document.getElementById('quiz-container');
    const questionContainer = document.getElementById('question-container');
    const startBtn = document.getElementById('start-btn');

    const buttons = {
        skip: document.getElementById('skip-btn'),
        back: document.getElementById('back-btn'),
        submit: document.getElementById('submit-btn'),
        restart: document.getElementById('restart-btn'),
        share: document.getElementById('share-btn'),
        result: {
            restart: document.getElementById('result-restart-btn'),
            share: document.getElementById('result-share-btn')
        }
    };    

    startBtn.addEventListener('click', () => {
        userName = document.getElementById('username').value.trim();
        if (!userName) return alert('Please enter your name!');
        document.getElementById('title').textContent = `${userName}’s Personality Test`;
        startScreen.style.display = 'none';
        quizContainer.style.display = 'block';
        questionContainer.style.display = 'block';
        loadQuestions();
    });

    buttons.skip.addEventListener('click', () => {
        if (currentQuestionIndex < totalQuestions - 1) {
            currentQuestionIndex++;
            console.log(`⏭️ Skipped to Q${currentQuestionIndex + 1}`);
            loadQuestion();
        }
    });
    
    buttons.back.addEventListener('click', () => {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            console.log(`⬅️ Went back to Q${currentQuestionIndex + 1}`);
            loadQuestion();
        }
    });
    
    buttons.submit.addEventListener('click', () => {
        console.log("📤 Submitting answers...");
        showResults(userName);
    });
    
    buttons.restart.addEventListener('click', restartQuiz);
    buttons.result.restart.addEventListener('click', restartQuiz); 
    buttons.share.addEventListener('click', shareResult);
    buttons.result.share.addEventListener('click', shareResult);   
});

async function loadQuestions() {
    try {
        const response = await fetch('http://localhost:9090/PersonalityTest/loadQuestions');
        questions = await response.json();
        totalQuestions = questions.length;
        answers = new Array(totalQuestions).fill(undefined);
        currentQuestionIndex = 0;
        console.log("📚 Questions loaded:", questions);
        loadQuestion();
    } catch (err) {
        console.error("❌ Error loading questions:", err);
        document.getElementById('question-text').textContent = "⚠️ Failed to load questions.";
    }
}

function loadQuestion() {
    const q = questions[currentQuestionIndex];
    const questionText = document.getElementById('question-text');
    const optionsContainer = document.getElementById('options');

    console.log(`📍 Showing Q${currentQuestionIndex + 1}: ${q.question}`);
    questionText.textContent = `${currentQuestionIndex + 1}. ${q.question}`;
    optionsContainer.innerHTML = '';

    q.options.forEach((option, idx) => {
        const btn = document.createElement('button');
        btn.className = `option-btn ${answers[currentQuestionIndex] === idx ? 'selected' : ''}`;
        btn.textContent = option;
        btn.addEventListener('click', () => submitAnswer(idx));
        optionsContainer.appendChild(btn);
    });

    updateProgress();
    toggleQuizButtons();
}

function updateProgress() {
    const progressBar = document.getElementById('progress-bar');
    const percent = ((currentQuestionIndex + 1) / totalQuestions) * 100;
    progressBar.style.width = `${percent}%`;
    console.log(`📊 Progress: ${percent.toFixed(1)}%`);
}

async function submitAnswer(index) {
    answers[currentQuestionIndex] = index;
    console.log(`✅ Q${currentQuestionIndex + 1} Answered: ${questions[currentQuestionIndex].options[index]}`);

    try {
        await fetch(`http://localhost:9090/PersonalityTest/handleAnswer?answer=${index}`);
    } catch (err) {
        console.error("❌ Failed to send answer:", err);
    }

    if (currentQuestionIndex < totalQuestions - 1) {
        currentQuestionIndex++;
        loadQuestion();
    } else {
        console.log("✨ All questions done. Ready to submit.");
        toggleQuizButtons();
    }
}

async function showResults() {
    try {
        console.log("📤 Fetching final result from backend...");

        const response = await fetch('http://localhost:9090/PersonalityTest/getResult');
        const data = await response.json();

        console.log("🎁 Raw backend response:", data);

        let raw = data.result || '';
        raw = raw.replace(/\\n/g, '\n').replace(/\r\n/g, '\n').trim();

        const username = document.getElementById("username").value || "Your";
        console.log("👤 Username at results:", username);
        console.log("📦 Cleaned Raw Result:", raw);

        const resultContainer = document.getElementById("result-container");
        const title = document.getElementById("result-title");
        const desc = document.getElementById("result-description");
        const details = document.getElementById("result-details");

        if (!resultContainer || !title || !desc || !details) {
            console.error("🛑 Missing result display elements in DOM!");
            return;
        }

        // Break result into pieces
        const lines = raw.split('\n').map(line => line.trim()).filter(line => line !== '');
        const titleLine = lines[0] || "Your Personality Type";
        const descLine = lines[1] || "No description available.";
        const remainingLines = lines.slice(2).join('\n');

        // Update UI with title and description
        title.textContent = `${username}'s Result – ${titleLine}`;
        desc.textContent = descLine;
        details.innerHTML = '';

        // Split detailed result by emojis or markers
        const strengthWeakness = remainingLines.match(/✨[\s\S]*?💔[\s\S]*?(?=💘|🧑‍💼|🧠|$)/)?.[0]?.trim() || '';
        const relationshipCareer = remainingLines.match(/💘[\s\S]*?(🧑‍💼[\s\S]*?)?(?=🧠|$)/)?.[0]?.trim() || '';
        const psychInsight = remainingLines.match(/🧠[\s\S]*$/)?.[0]?.trim() || '';
        
        const sections = [strengthWeakness, relationshipCareer, psychInsight];
                                                
        if (sections.length === 0 || remainingLines.length === 0) {
            details.innerHTML = `<p>No additional insights found. 😢</p>`;
        } else {
            sections.forEach((section, index) => {
                const p = document.createElement('p');
                p.textContent = section.trim();
                details.appendChild(p);
                console.log(`📄 Section ${index + 1}:`, section.trim());
            });
        }

        console.log("🧹 Hiding quiz and review containers, showing results now...");

        // Hide other containers
        document.getElementById('start-screen')?.classList.add("hidden");
        document.getElementById('quiz-container')?.classList.add("hidden");
        document.getElementById('review-container')?.classList.add("hidden");

        // Reset and show result container with full effect
        resultContainer.classList.remove("hidden");
        resultContainer.classList.add("show");
        resultContainer.style.display = "block";
        resultContainer.style.opacity = "0";
        resultContainer.style.visibility = "visible";
        resultContainer.style.pointerEvents = "auto";
        resultContainer.style.transform = "translateY(0)";
        resultContainer.style.transition = "opacity 0.6s ease, transform 0.6s ease";

        setTimeout(() => {
            resultContainer.style.opacity = "1";
            console.log("🎯 Result should now be fully visible.");
        }, 50);

    } catch (err) {
        console.error("❌ Failed to fetch or display result:", err);
        const errorEl = document.getElementById("result-description");
        if (errorEl) {
            errorEl.textContent = "Something went wrong while loading your results. 😢";
        }
    }
}

let isSharing = false;
function shareResult() {
    if (isSharing) return; // Prevent multiple rapid clicks

    const title = document.getElementById("result-title")?.innerText || "";
    const description = document.getElementById("result-description")?.innerText || "";
    const details = document.getElementById("result-details")?.innerText || "";

    const resultText = `${title}\n\n${description}\n\n${details}`;

    // If Web Share API is supported
    if (navigator.share) {
        isSharing = true;
        navigator.share({
            title: "My Personality Test Result",
            text: resultText
        }).then(() => {
            console.log("✅ Shared successfully");
        }).catch((error) => {
            console.error("❌ Error sharing:", error);
        }).finally(() => {
            isSharing = false;
        });
    } else {
        // Fallback: Copy to clipboard
        navigator.clipboard.writeText(resultText).then(() => {
            alert("✅ Result copied! You can paste it anywhere.");
        });
    }
}
    
function shareTo(platform) {
    const title = document.getElementById("result-title")?.innerText || "";
    const description = document.getElementById("result-description")?.innerText || "";
    const details = document.getElementById("result-details")?.innerText || "";

    const text = `💫 I just discovered my personality type! 💫\n\n${title}\n\n${description}\n\n${details}`;
    const encoded = encodeURIComponent(text);

    let url = "";

    switch (platform) {
        case 'twitter':
            url = `https://twitter.com/intent/tweet?text=${encoded}`;
            break;
        case 'whatsapp':
            url = `https://wa.me/?text=${encoded}`;
            break;
        case 'linkedin':
            url = `https://www.linkedin.com/sharing/share-offsite/?url=https://your-app.com&summary=${encoded}`;
            break;
        case 'copy':
            navigator.clipboard.writeText(`${text}\n\nCheck your vibe at {not uploaded yet}}`);
            alert("📋 Copied to clipboard!");
            return;
    }

    if (url) window.open(url, '_blank');
}

async function restartQuiz() {
    console.clear();
    location.reload();
    try {
        // Optional backend reset
        await fetch('http://localhost:9090/PersonalityTest/resetQuiz');

        // Reset state
        currentQuestionIndex = 0;
        answers = [];

        console.log("🧼 Clearing state...");

        // Hide all dynamic containers 
        const containersToHide = [
            'quiz-container',
            'question-container',
            'share-menu',
            'result-container',
            'result-buttons' 
        ];

        containersToHide.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.classList.add('hidden');
                el.style.display = 'none';
                el.style.opacity = '0';
            }
        });

        const resultTitle = document.getElementById('result-title');
        if (resultTitle) resultTitle.textContent = '';

        const resultDesc = document.getElementById('result-description');
        if (resultDesc) resultDesc.textContent = '';

        const resultDetails = document.getElementById('result-details');
        if (resultDetails) resultDetails.innerHTML = '';

        const questionContainer = document.getElementById('question-container');
        if (questionContainer) questionContainer.innerHTML = '';

        const usernameInput = document.getElementById('username');
        if (usernameInput) usernameInput.value = '';

        const title = document.getElementById('title');
        if (title) title.textContent = 'Discover Your Inner Vibe 🌈';

        const startScreen = document.getElementById('start-screen');
        if (startScreen) {
            startScreen.classList.remove('hidden');
            startScreen.style.display = 'block';
        }

        const resultActions = document.getElementById('result-actions');
        if (resultActions) {
            resultActions.classList.add('hidden');
            resultActions.style.display = 'none';
            resultActions.style.opacity = '0';
        }

        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });

        // ♻Reset navigation buttons (if used)
        if (typeof toggleQuizButtons === 'function') {
            toggleQuizButtons();
        }
        console.log("✅ Quiz reset and UI restored.");
    } catch (err) {
        console.error("❌ Error resetting quiz:", err);
        alert("Something went wrong. Try again.");
    }
}

function toggleQuizButtons() {
    const atEnd = currentQuestionIndex === totalQuestions - 1;
    const atStart = currentQuestionIndex === 0;
    const skipBtn = document.getElementById('skip-btn');
    const backBtn = document.getElementById('back-btn');
    const submitBtn = document.getElementById('submit-btn');
    const restartBtn = document.getElementById('restart-btn');

    if (skipBtn) skipBtn.style.display = (!atEnd) ? 'inline-block' : 'none';
    if (backBtn) backBtn.style.display = (!atStart) ? 'inline-block' : 'none';
    if (submitBtn) submitBtn.style.display = atEnd ? 'inline-block' : 'none';
    if (restartBtn) restartBtn.style.display = 'none'; // Hidden until result screen
}

// Insert this below all other functions, near the end
document.addEventListener('click', function (e) {
    const menu = document.getElementById('share-menu');
    if (
        menu &&
        !menu.contains(e.target) &&
        e.target.id !== 'result-share-btn' &&
        e.target.id !== 'share-btn'
    ) {
        menu.style.display = 'none';
    }
});
