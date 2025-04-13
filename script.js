let questions = [];
let currentQuestionIndex = 0;
let totalQuestions = 0;
let userName = '';
let answers = [];

document.addEventListener("DOMContentLoaded", () => {
  const startBtn = document.getElementById("start-btn");
  const usernameInput = document.getElementById("username");

  startBtn.addEventListener("click", () => {
    userName = usernameInput.value.trim();
    if (!userName) {
      alert("Please enter your name!");
      return;
    }

    document.getElementById("quiz-username").textContent = `${userName}'s Quiz`;
    switchSection("start-section", "quiz-section");
    loadQuestions();
  });

  document.getElementById("back-btn").addEventListener("click", () => {
    if (currentQuestionIndex > 0) {
      currentQuestionIndex--;
      loadQuestion();
    }
  });

  document.getElementById("skip-btn").addEventListener("click", () => {
    if (currentQuestionIndex < totalQuestions - 1) {
      currentQuestionIndex++;
      loadQuestion();
    }
  });

  document.getElementById("submit-btn").addEventListener("click", showResults);
  document.getElementById("restart-btn").addEventListener("click", restartQuiz);
  document.getElementById("result-restart-btn").addEventListener("click", restartQuiz);
  document.getElementById("share-btn").addEventListener("click", toggleShareMenu);
  document.getElementById("result-share-btn").addEventListener("click", shareResult);
});

function switchSection(hideId, showId) {
  document.getElementById(hideId).classList.add("hidden");
  document.getElementById(showId).classList.remove("hidden");
  document.getElementById(showId).classList.add("active");
}

async function loadQuestions() {
  try {
    const res = await fetch("http://localhost:9090/PersonalityTest/loadQuestions");
    questions = await res.json();
    totalQuestions = questions.length;
    answers = new Array(totalQuestions).fill(undefined);
    currentQuestionIndex = 0;
    loadQuestion();
  } catch (err) {
    console.error("❌ Failed to load questions:", err);
    document.getElementById("question-text").textContent = "Error loading questions.";
  }
}

function loadQuestion() {
    const q = questions[currentQuestionIndex];
    const questionText = document.getElementById("question-text");
    const optionsContainer = document.getElementById("options");
  
    questionText.textContent = `${currentQuestionIndex + 1}. ${q.question}`;
    optionsContainer.innerHTML = "";
  
    q.options.forEach((option, index) => {
      const btn = document.createElement("button");
      btn.textContent = option;
      btn.className = answers[currentQuestionIndex] === index ? "selected" : "";
      btn.addEventListener("click", () => {
        submitAnswer(index);
      });
      optionsContainer.appendChild(btn);
    });
  
    // Progress bar
    updateProgress();
  
    // Buttons visibility
    const atStart = currentQuestionIndex === 0;
    const atEnd = currentQuestionIndex === totalQuestions - 1;
  
    document.getElementById("back-btn").style.display = atStart ? "none" : "inline-block";
    document.getElementById("skip-btn").style.display = atEnd ? "none" : "inline-block";
    const submitBtn = document.getElementById("submit-btn");

    if (currentQuestionIndex === totalQuestions - 1) {
      submitBtn.classList.remove("hidden");
      submitBtn.style.display = "inline-block";
    } else {
      submitBtn.classList.add("hidden");
      submitBtn.style.display = "none";
    }
}
  
function toggleNavButtons() {
  const atStart = currentQuestionIndex === 0;
  const atEnd = currentQuestionIndex === totalQuestions - 1;

  document.getElementById("back-btn").style.display = atStart ? "none" : "inline-block";
  document.getElementById("skip-btn").style.display = atEnd ? "none" : "inline-block";
  document.getElementById("submit-btn").style.display = atEnd ? "inline-block" : "none";
  document.getElementById("restart-btn").style.display = "none";
  document.getElementById("share-btn").style.display = "none";
}

function updateProgress() {
  const progressBar = document.getElementById("progress-bar");
  const percent = ((currentQuestionIndex + 1) / totalQuestions) * 100;
  progressBar.style.width = `${percent}%`;
}

async function submitAnswer(index) {
    answers[currentQuestionIndex] = index;
  
    try {
      await fetch(`http://localhost:9090/PersonalityTest/handleAnswer?answer=${index}`);
    } catch (err) {
      console.error("❌ Failed to send answer:", err);
    }
  
    if (currentQuestionIndex < totalQuestions - 1) {
      currentQuestionIndex++;
      loadQuestion();
    } else {
      // On last question, reload UI so submit appears
      loadQuestion();
    }
}    

async function showResults() {
  try {
    const res = await fetch("http://localhost:9090/PersonalityTest/getResult");
    const data = await res.json();
    const raw = (data.result || "").replace(/\\n/g, "\n").trim();
    const lines = raw.split("\n").filter(Boolean);

    document.getElementById("result-title").textContent = `${userName}'s Personality – ${lines[0] || ""}`;
    document.getElementById("result-description").textContent = lines[1] || "";

    const resultDetails = document.getElementById("result-details");
    resultDetails.innerHTML = "";
    lines.slice(2).forEach((line) => {
      const p = document.createElement("p");
      p.textContent = line;
      resultDetails.appendChild(p);
    });

    switchSection("quiz-section", "result-section");
  } catch (err) {
    console.error("❌ Failed to fetch results:", err);
    alert("Could not load result.");
  }
}

async function restartQuiz() {
  try {
    await fetch("http://localhost:9090/PersonalityTest/resetQuiz");
    location.reload();
  } catch (err) {
    alert("Failed to reset quiz.");
  }
}

function toggleShareMenu() {
  const menu = document.getElementById("share-menu");
  menu.classList.toggle("show");
}

function shareResult() {
  const title = document.getElementById("result-title").innerText;
  const description = document.getElementById("result-description").innerText;
  const details = document.getElementById("result-details").innerText;
  const text = `${title}\n\n${description}\n\n${details}`;

  if (navigator.share) {
    navigator.share({
      title: "My Personality Test Result",
      text,
    }).catch((err) => console.error("Sharing failed", err));
  } else {
    navigator.clipboard.writeText(text).then(() => alert("Result copied!"));
  }
}

function shareTo(platform) {
  const title = document.getElementById("result-title").innerText;
  const description = document.getElementById("result-description").innerText;
  const details = document.getElementById("result-details").innerText;
  const message = `🌟 Check out my personality result!\n\n${title}\n${description}\n${details}`;
  const encoded = encodeURIComponent(message);
  let url = "";

  switch (platform) {
    case "twitter":
      url = `https://twitter.com/intent/tweet?text=${encoded}`;
      break;
    case "whatsapp":
      url = `https://wa.me/?text=${encoded}`;
      break;
    case "linkedin":
      url = `https://www.linkedin.com/sharing/share-offsite/?url=https://your-quiz-site.com&summary=${encoded}`;
      break;
    case "copy":
      navigator.clipboard.writeText(`${message}\nTry it at https://your-quiz-site.com`);
      alert("Copied to clipboard!");
      return;
  }

  if (url) window.open(url, "_blank");
}
