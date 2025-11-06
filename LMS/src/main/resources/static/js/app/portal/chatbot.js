/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 23.     	김수현            최초 생성
 * 2025. 10. 24.     	김수현            자주 묻는 질문 토글 기능 추가
 *
 * </pre>
 */

// 전역 변수
let chatbotStompClient = null;
let sessionId = 'session_' + Date.now();
let isConnected = false;
let currentBotMessage = null;
let fullAnswer = '';

// WebSocket 연결
function connectChatbot() {
    const socket = new SockJS('/lms/ws-stomp');
    chatbotStompClient = Stomp.over(socket);

    chatbotStompClient.connect({}, function(frame) {
        console.log('===> 챗봇 WebSocket 연결 성공');
        isConnected = true;

        // 응답 구독
        chatbotStompClient.subscribe('/topic/ai/' + sessionId, function(message) {
            handleAIResponse(JSON.parse(message.body));
        });
    }, function(error) {
        console.error('===> 챗봇 WebSocket 연결 실패', error);
        isConnected = false;
    });
}

// AI 응답 처리
function handleAIResponse(data) {
    const messagesContainer = document.getElementById('chatbot-messages');

    if (data.type === 'START') {
        // 타이핑 인디케이터 표시
        currentBotMessage = document.createElement('div');
        currentBotMessage.className = 'message bot-message';
        currentBotMessage.innerHTML = '<div class="message-content"><div class="typing-indicator"><span></span><span></span><span></span></div></div>';
        messagesContainer.appendChild(currentBotMessage);
        scrollToBottom();
        fullAnswer = '';

    } else if (data.type === 'CHUNK') {
        // 타이핑 인디케이터 제거하고 텍스트 추가
        if (currentBotMessage && currentBotMessage.querySelector('.typing-indicator')) {
            currentBotMessage.innerHTML = '<div class="message-content ai-response"></div>';
        }
        // 한 글자씩 화면에 추가
        fullAnswer += data.chunk;

        // 줄바꿈 처리
        const contentDiv = currentBotMessage.querySelector('.message-content');
        contentDiv.innerHTML = formatTextWithLineBreaks(fullAnswer);

        scrollToBottom();

    } else if (data.type === 'END') {
        console.log('===> 답변 완료');
        currentBotMessage = null;

    } else if (data.type === 'ERROR') {
        if (currentBotMessage) {
            currentBotMessage.querySelector('.message-content').textContent = data.error;
        }
    }
}

// 답변 줄바꿈 처리
function formatTextWithLineBreaks(text) {
    return text
        .replace(/\n/g, '<br>')
        .replace(/\r\n/g, '<br>');
}

// 메시지 전송
function sendChatbotMessage(questionText) {
    // questionText가 제공되면 사용, 아니면 입력창에서 가져오기
    const input = document.getElementById('chatbot-input');
    const question = questionText || input.value.trim();

    if (!question) return;

    if (!isConnected) {
        alert('챗봇 서버에 연결 중입니다. 잠시 후 다시 시도해주세요.');
        return;
    }

    // 사용자 메시지 표시
    addUserMessage(question);
    input.value = '';

    // WebSocket으로 전송
    chatbotStompClient.send('/app/ai/ask', {}, JSON.stringify({
        question: question,
        sessionId: sessionId
    }));
}

// 사용자 메시지 추가
function addUserMessage(text) {
    const messagesContainer = document.getElementById('chatbot-messages');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message user-message';
    messageDiv.innerHTML = '<div class="message-content">' + escapeHtml(text) + '</div>';
    messagesContainer.appendChild(messageDiv);
    scrollToBottom();
}

// HTML 이스케이프
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 스크롤 아래로
function scrollToBottom() {
    const messagesContainer = document.getElementById('chatbot-messages');
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 자주 묻는 질문 토글 기능
function initQuickQuestions() {
    const toggleBtn = document.getElementById('quick-toggle-btn');
    const questionsContainer = document.getElementById('quick-questions-container');
    const quickButtons = document.querySelectorAll('.quick-btn');

    // 토글 버튼 클릭
    toggleBtn.addEventListener('click', function() {
        const isHidden = questionsContainer.classList.contains('quick-questions-hidden');

        if (isHidden) {
            // 펼치기
            questionsContainer.classList.remove('quick-questions-hidden');
            toggleBtn.classList.add('active');
        } else {
            // 접기
            questionsContainer.classList.add('quick-questions-hidden');
            toggleBtn.classList.remove('active');
        }
    });

    // 질문 버튼 클릭
    quickButtons.forEach(button => {
        button.addEventListener('click', function() {
            const question = this.getAttribute('data-question');

            // 버튼 클릭 애니메이션
            this.style.transform = 'scale(0.95)';
            setTimeout(() => {
                this.style.transform = '';
            }, 100);

            // 질문 전송
            setTimeout(() => {
                sendChatbotMessage(question);

                // ⭐ 질문 전송 후 자동으로 접기
                questionsContainer.classList.add('quick-questions-hidden');
                toggleBtn.classList.remove('active');
            }, 200);
        });
    });
}

// 챗봇 초기화
function initChatbot() {
    const chatbotButton = document.getElementById('chatbot-button');
    const chatbotContainer = document.getElementById('chatbot-container');
    const chatbotClose = document.getElementById('chatbot-close');
    const chatbotInput = document.getElementById('chatbot-input');
    const chatbotSend = document.getElementById('chatbot-send');

    // 챗봇 열기
    chatbotButton.addEventListener('click', function(e) {
        e.preventDefault();
        chatbotContainer.style.display = 'flex';
        chatbotInput.focus();

        // 최초 연결
        if (!isConnected) {
            connectChatbot();
        }
    });

    // 챗봇 닫기
    chatbotClose.addEventListener('click', function() {
        chatbotContainer.style.display = 'none';
    });

    // 전송 버튼
    chatbotSend.addEventListener('click', function() {
        sendChatbotMessage();
    });

    // Enter 키
    chatbotInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendChatbotMessage();
        }
    });

    // 자주 묻는 질문 초기화
    initQuickQuestions();
}

// DOM 로드 후 초기화
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initChatbot);
} else {
    initChatbot();
}