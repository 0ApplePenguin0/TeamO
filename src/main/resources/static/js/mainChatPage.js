let websocket = null;  // WebSocket 객체
let currentChatRoomId = 25;  // 기본 채팅방 ID (전체 채팅방)
let currentUserId = null;  // 현재 로그인된 사용자 ID
let currentCompanyId = null;  // 현재 사용자의 회사 ID

// 페이지 로드 시 실행할 초기화 함수
document.addEventListener('DOMContentLoaded', () => {
    initializeChat();
});

// 초기화 함수: 현재 로그인 사용자 ID 가져오고, 회사 정보를 로드한 후 채팅방 메시지 및 유저 목록 로드
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
            // currentUserId가 설정된 후에 채팅방 목록 불러오기
            loadUserChatRooms();
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

// 사용자가 참가 중인 채팅방 목록을 불러오는 함수
function loadUserChatRooms() {
    if (!currentUserId) {
        console.error('User ID is not set.');
        return;
    }

    fetch(`/api/chat/rooms/getChatRoomsByUser/${currentUserId}`)
        .then(response => response.json())
        .then(chatRooms => {
            console.log('Loaded Chat Rooms:', chatRooms);  // 채팅방 목록 확인
            const chatRoomList = document.getElementById('chatroom-list');

            // 기본 채팅방 추가
            chatRoomList.innerHTML = `
                <li><a href="/chat/mainChatPage">전체 채팅방</a></li>
                <li><a href="/chat/teamChatPage">팀 채팅방</a></li>
            `;

            // 동적으로 사용자 참여 채팅방 추가 (24, 25를 제외)
            chatRooms.forEach(room => {
                if (room.chatRoomId !== 24 && room.chatRoomId !== 25) {  // 24, 25 제외 조건
                    const roomElement = document.createElement('li');
                    if (room.createdByMemberId == currentUserId) {
                        roomElement.innerHTML = `
                            <a href="/chat/projectChatPage/${room.chatRoomId}">${room.chatRoomName}</a>
                            <button class="delete-chatroom-btn" data-chatroom-id="${room.chatRoomId}">삭제</button>
                        `;
                    } else {
                        roomElement.innerHTML = `
                            <a href="/chat/projectChatPage/${room.chatRoomId}">${room.chatRoomName}</a>
                            <button class="leave-chatroom-btn" data-chatroom-id="${room.chatRoomId}">나가기</button>
                        `;
                    }
                    chatRoomList.appendChild(roomElement);
                }
            });

            // 각 삭제 버튼에 이벤트 리스너 추가
            document.querySelectorAll('.delete-chatroom-btn').forEach(button => {
                button.addEventListener('click', (event) => {
                    const chatRoomId = event.target.dataset.chatroomId;
                    deleteChatRoom(chatRoomId);
                });
            });
        })
        .catch(error => console.error('Error loading user chat rooms:', error));
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
        loadChatRoomParticipants(currentChatRoomId);  // 채팅방의 참여자 목록 로드
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

// 같은 회사의 사용자 목록을 가져와서 채팅방 참여자로 설정하는 함수
function loadUsersByCompany() {
    // 회사 ID가 설정된 후에 fetch 진행
    if (!currentCompanyId) {
        console.error('Company ID is not set.');
        return;
    }

    fetch(`/api/members/getMembersByCompany/${currentCompanyId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load users by company");
            }
            return response.json();
        })
        .then(users => {
            const participantList = document.getElementById('participant-list');
            if (!participantList) {
                console.error('Participant list element not found.');
                return;
            }
            participantList.innerHTML = '';  // 기존 목록 초기화

            users.forEach(user => {
                const userElement = document.createElement('div');
                userElement.classList.add('participant-item');

                // 현재 로그인한 사용자와 일치하면 "(본인)"으로 표시하고, 초대 버튼을 숨김
                if (user.memberId === currentUserId) {
                    userElement.innerHTML = `
                        ${user.memberName} <span>(본인)</span>
                    `;
                } else {
                    userElement.innerHTML = `
                        ${user.memberName}
                        <button class="invite-btn" data-id="${user.memberId}">초대</button>
                    `;
                }

                participantList.appendChild(userElement);
            });

            // 초대 버튼 클릭 시 이벤트 처리
            document.querySelectorAll('.invite-btn').forEach(button => {
                button.addEventListener('click', (event) => {
                    const memberId = event.target.getAttribute('data-id');
                    inviteUserToChatRoom(memberId);  // 초대 함수 호출
                });
            });
        })
        .catch(error => console.error('Error loading users by company:', error));
}

// 채팅방 생성, 삭제 및 나가기 함수 등은 동일하게 유지

// 채팅방 참여자 목록 불러오기
function loadChatRoomParticipants(chatRoomId) {
    if (!chatRoomId) {
        console.error('Chat Room ID is not provided.');
        return;
    }

    fetch(`/api/chat/rooms/participants/${chatRoomId}`)
        .then(response => response.json())
        .then(participants => {
            const participantList = document.getElementById('d-list');
            if (!participantList) {
                console.error('Participant list element (d-list) not found.');
                return;
            }
            participantList.innerHTML = '';  // 기존 목록 초기화

            // 중복된 참여자를 제거하기 위해 Set 사용
            const uniqueParticipants = new Set();
            participants.forEach(participant => {
                uniqueParticipants.add(participant.memberName);  // 이름만 비교하는 예시
            });

            // 참여자 목록 동적으로 추가
            uniqueParticipants.forEach(participant => {
                const participantElement = document.createElement('li');
                participantElement.textContent = participant;  // 참여자 이름 표시
                participantList.appendChild(participantElement);
            });
        })
        .catch(error => console.error('Error loading participants:', error));
}
