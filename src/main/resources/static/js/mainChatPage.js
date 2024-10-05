let websocket = null;  // WebSocket 객체
let currentChatRoomId = 25;  // 고정된 채팅방 ID
let currentUserId = null;  // 현재 로그인된 사용자 ID
let currentCompanyId = null;  // 현재 사용자의 회사 ID

// 페이지 로드 시 실행할 초기화 함수
document.addEventListener('DOMContentLoaded', () => {
    initializeChat();
});

// 초기화 함수: 현재 로그인 사용자 ID를 가져오고 회사 정보를 로드, 채팅방 메시지를 불러옴
function initializeChat() {
    getCurrentUserMemberId()  // 현재 로그인한 사용자 ID 가져오기
        .then(() => getCompanyId())  // 회사 ID 가져오기
        .then(() => {
            loadUsersByCompany();  // 같은 회사의 유저 목록을 불러와서 채팅방 참여자로 표시
            loadMessages(currentChatRoomId);  // 채팅방의 기존 메시지 로드
            setupWebSocketConnection();  // WebSocket 연결 설정
        })
        .catch(error => console.error('Error initializing chat:', error));
}

// 현재 로그인한 사용자의 memberId를 가져오는 함수 추가
function getCurrentUserMemberId() {
    return fetch('/api/members/getCurrentUserMemberId')  // 서버에서 사용자 ID 가져오기
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch current user memberId');
            }
            return response.text();
        })
        .then(memberId => {
            currentUserId = memberId;  // 현재 사용자 ID 설정
            console.log(`Current User ID: ${currentUserId}`);  // 로그로 확인
        })
        .catch(error => console.error('Error fetching current user memberId:', error));
}

// 현재 로그인된 사용자의 회사 ID를 가져오는 함수 추가
function getCompanyId() {
    return fetch('/api/members/getCompanyId')  // 서버에서 회사 ID 가져오기
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch company ID');
            }
            return response.text();
        })
        .then(companyId => {
            currentCompanyId = companyId;  // 현재 회사 ID 설정
            console.log(`Current Company ID: ${currentCompanyId}`);  // 로그로 확인
        })
        .catch(error => console.error('Error fetching company ID:', error));
}

// 같은 회사의 사용자 목록을 가져와서 채팅방 참여자로 설정하는 함수
function loadUsersByCompany() {
    fetch(`/api/members/getMembersByCompany/${currentCompanyId}`)
        .then(response => response.json())
        .then(users => {
            console.log('Loaded Users:', users);  // 사용자 목록 확인
            const participantList = document.getElementById('participant-list');
            participantList.innerHTML = '<h3>채팅방 참여자 목록</h3>';  // 헤더 설정

            users.forEach(user => {
                const userElement = document.createElement('div');
                userElement.classList.add('participant-item');

                // 현재 로그인한 사용자와 일치하면 "(본인)"으로 표시
                const isCurrentUser = user.memberId === currentUserId ? '(본인)' : '';
                userElement.textContent = `${user.memberName} (${user.email}) ${isCurrentUser}`;

                participantList.appendChild(userElement);
            });
        })
        .catch(error => console.error('Error loading users:', error));
}

function setupWebSocketConnection() {
    if (websocket) {
        websocket.close();  // 기존 연결이 있을 경우 닫음
    }
	console.log("chatRoomId 전달 ", currentChatRoomId);
    // WebSocket 연결 시 채팅방 ID를 URL 파라미터로 전달
    const wsUrl = `ws://${window.location.host}/ws/chat?chatRoomId=${currentChatRoomId}`;
    websocket = new WebSocket(wsUrl);

    websocket.onopen = function () {
        console.log(`Connected to WebSocket room: ${currentChatRoomId}`);
    };

    websocket.onmessage = function (event) {
        displayMessage(event.data);  // 실시간 메시지 수신 시 표시
    };

    websocket.onclose = function () {
        console.log(`Disconnected from WebSocket room`);
    };

    websocket.onerror = function (event) {
        console.error(`WebSocket error: ${event.message}`);
    };

    // 메시지 전송 버튼 클릭 시 WebSocket 메시지 전송
    document.getElementById('send-btn').onclick = function () {
        const messageContent = document.getElementById('chat-input').value.trim();
        if (messageContent) {
            sendMessage(messageContent);
            document.getElementById('chat-input').value = '';  // 입력창 초기화
        }
    };
}


// 메시지 전송
function sendMessage(messageContent) {
    const message = {
        chatRoomId: currentChatRoomId,  // 고정된 채팅방 ID
        memberId: currentUserId,  // 현재 로그인한 사용자 ID
        message: messageContent
    };
    websocket.send(JSON.stringify(message));  // 메시지를 WebSocket으로 전송
}

// 메시지를 표시하는 함수
function displayMessage(message) {
    const messageContainer = document.getElementById('chat-messages');
    const messageElement = document.createElement('div');
    messageElement.textContent = message;  // 수신한 메시지를 표시
    messageContainer.appendChild(messageElement);
}

// 특정 채팅방의 이전 메시지를 서버에서 불러오는 함수
function loadMessages(roomId) {
    fetch(`/api/chat/messages/${roomId}`)
        .then(response => response.json())
        .then(messages => {
            const messageContainer = document.getElementById('chat-messages');
            messageContainer.innerHTML = '';  // 기존 메시지 초기화
            messages.forEach(message => {
                displayMessage(`${message.memberId}: ${message.message}`);  // 이전 메시지 표시
            });
        })
        .catch(error => console.error('Error loading messages:', error));
}