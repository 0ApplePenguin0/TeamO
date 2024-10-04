let websocket = null;  // WebSocket 객체
let currentChatRoomId = null;  // 현재 채팅방 ID
let currentUserId = null;  // 현재 로그인된 사용자 ID

// 페이지 로드 시 실행할 초기화 함수
document.addEventListener('DOMContentLoaded', () => {
    initializeChat();
});

// 초기화 함수: 현재 로그인 사용자 ID를 가져오고 채팅방 목록을 로드
function initializeChat() {
    getCurrentUserMemberId()
        .then(() => loadChatRooms())
        .catch(error => console.error('Error initializing chat:', error));
}

// 현재 로그인한 사용자의 memberId 가져오기
function getCurrentUserMemberId() {
    return fetch('/api/members/getCurrentUserMemberId')
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

// 채팅방 목록을 서버에서 가져오는 함수
function loadChatRooms() {
    fetch(`/api/chat/rooms/getChatRoomsByUser/${currentUserId}`)
        .then(response => response.json())
        .then(chatRooms => {
            console.log('Loaded Chat Rooms:', chatRooms);  // 응답 데이터 확인
            const chatRoomList = document.getElementById('chat-rooms');
            chatRoomList.innerHTML = '';  // 기존 목록 초기화

            // 기본 채팅방 먼저 추가 (회사, 부서)
            const defaultRooms = chatRooms.filter(room => room.kind === '회사' || room.kind === '부서');
            const projectRooms = chatRooms.filter(room => room.kind === '프로젝트');

            // 기본 채팅방(회사, 부서) 표시
            defaultRooms.forEach(room => {
                const roomElement = createChatRoomElement(room);
                chatRoomList.appendChild(roomElement);
            });

            // 프로젝트 채팅방 표시
            projectRooms.forEach(room => {
                const roomElement = createChatRoomElement(room);
                chatRoomList.appendChild(roomElement);
            });
        })
        .catch(error => console.error('Error loading chat rooms:', error));
}

// 채팅방 목록 항목 생성
function createChatRoomElement(room) {
    const roomElement = document.createElement('div');
    roomElement.classList.add('chat-room-item');
    roomElement.textContent = room.chatRoomName;
    roomElement.onclick = () => joinChatRoom(room.chatRoomId);  // 클릭하면 해당 채팅방 입장
    return roomElement;
}

// 새로운 채팅방 추가
document.getElementById('add-room-btn').onclick = function () {
    const roomName = document.getElementById('new-room-name').value.trim();
    if (roomName) {
        createNewChatRoom(roomName);
    } else {
        alert('채팅방 이름을 입력해주세요.');
    }
};

// 채팅방 추가 후 목록을 다시 로드
function createNewChatRoom(roomName) {
    fetch('/api/chat/rooms/add', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            chatRoomName: roomName,
            createdByMemberId: currentUserId,
            companyId: 1,  // 회사 ID (임의 값)
            kind: '프로젝트'  // 프로젝트 채팅방으로 생성
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to create chat room');
        }
        return response.text();
    })
    .then(() => {
        alert(`채팅방 "${roomName}"이(가) 생성되었습니다.`);
        loadChatRooms();  // 채팅방 목록 갱신
    })
    .catch(error => console.error('Error creating chat room:', error));
}

// 특정 채팅방에 입장하는 함수
function joinChatRoom(roomId) {
    currentChatRoomId = roomId;
    setupWebSocketConnection(roomId);  // WebSocket 연결 설정
    loadMessages(roomId);  // 채팅방의 이전 메시지 불러오기
}

// WebSocket 연결 설정
function setupWebSocketConnection(roomId) {
    if (websocket) {
        websocket.close();  // 기존 연결이 있을 경우 닫음
    }

    const wsUrl = `ws://${window.location.host}/ws/chat/${roomId}`;
    websocket = new WebSocket(wsUrl);

    websocket.onopen = function () {
        console.log(`Connected to WebSocket room ${roomId}`);
    };

    websocket.onmessage = function (event) {
        displayMessage(event.data);  // 메시지 수신 시 표시
    };

    websocket.onclose = function () {
        console.log(`Disconnected from WebSocket room ${roomId}`);
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
        chatRoomId: currentChatRoomId,
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
                displayMessage(`${message.memberId}: ${message.message}`);
            });
        })
        .catch(error => console.error('Error loading messages:', error));
}
