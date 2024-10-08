let websocket = null;  // WebSocket 객체
let currentChatRoomId = null;  // 채팅방 ID
let currentUserId = null;  // 현재 로그인된 사용자 ID
let currentUserName = null;  // 현재 로그인된 사용자 이름
let currentCompanyId = null;  // 현재 사용자의 회사 ID
let invitedUsers = [];  // 초대된 사용자들의 ID를 저장하는 배열

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    currentChatRoomId = getChatRoomIdFromUrl();
    initializeChat();
});

// URL에서 chatRoomId 추출
function getChatRoomIdFromUrl() {
    const path = window.location.pathname;
    const segments = path.split('/');
    return segments[segments.length - 1];  // URL 마지막 부분이 chatRoomId
}

// 초기화 함수: 사용자 ID 및 회사 정보, 채팅방 초기 설정
function initializeChat() {
    getCurrentUserMemberId()
        .then(() => getCompanyId())
        .then(() => loadInvitedUsers())  // 서버에서 이미 초대된 사용자 목록을 불러옴
        .then(() => {
            loadUsersByCompany();  // 같은 회사의 유저 목록을 불러옴
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
            loadUserChatRooms();  // 현재 사용자 ID가 설정된 후 채팅방 목록 불러오기
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
// 서버에서 초대된 사용자 목록 불러오기
function loadInvitedUsers() {
    return fetch(`/api/chat/rooms/invitedUsers/${currentChatRoomId}`)
        .then(response => response.json())
        .then(users => {
            invitedUsers = users.map(user => user.memberId);  // 초대된 사용자 목록을 저장
        })
        .catch(error => console.error('Error loading invited users:', error));
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
            console.log('Loaded Chat Rooms:', chatRooms);
            const chatRoomList = document.getElementById('chatroom-list');
            
            // 기존 채팅방 목록을 초기화
            chatRoomList.innerHTML = '';

            // 채팅방 목록을 새로 그리기
            chatRooms.forEach(room => {
                if (room.chatRoomId !== 24 && room.chatRoomId !== 25) {
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

            // 각 나가기 버튼에 이벤트 리스너 추가
            document.querySelectorAll('.leave-chatroom-btn').forEach(button => {
                button.addEventListener('click', (event) => {
                    const chatRoomId = event.target.dataset.chatroomId;
                    leaveChatRoom(chatRoomId);
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

function sendMessage(messageContent) {
    const message = {
        chatRoomId: currentChatRoomId,  // 추출된 채팅방 ID
        memberId: currentUserId,  // 현재 로그인한 사용자 ID
        message: messageContent
    };
    websocket.send(JSON.stringify(message));  // 메시지를 WebSocket으로 전송
}

function displayMessage(message) {
    const messageContainer = document.getElementById('chat-messages');
    const messageElement = document.createElement('div');
    messageElement.textContent = message;  // 수신한 메시지를 표시
    messageContainer.appendChild(messageElement);
}

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

// 사용자 목록을 불러와 채팅방 참여자로 표시
function loadUsersByCompany() {
    fetch(`/api/members/getMembersByCompany/${currentCompanyId}`)
        .then(response => response.json())
        .then(users => {
            const participantList = document.getElementById('participant-list');
            participantList.innerHTML = '';  // 기존 목록 초기화

            users.forEach(user => {
                const userElement = document.createElement('div');
                userElement.classList.add('participant-item');

                if (user.memberId === currentUserId) {
                    userElement.innerHTML = `${user.memberName} <span>(본인)</span>`;
                } else {
                    const isInvited = invitedUsers.includes(user.memberId);  // 초대된 사용자 여부 확인
                    userElement.innerHTML = `
                        ${user.memberName}
                        <button class="invite-btn" data-id="${user.memberId}" ${isInvited ? 'disabled' : ''}>
                            ${isInvited ? '이미 초대됨' : '초대'}
                        </button>
                    `;
                }

                participantList.appendChild(userElement);
            });

            document.querySelectorAll('.invite-btn').forEach(button => {
                button.addEventListener('click', event => {
                    const memberId = event.target.getAttribute('data-id');
                    inviteUserToChatRoom(memberId);
                });
            });
        })
        .catch(error => console.error('Error loading users by company:', error));
}

document.addEventListener('DOMContentLoaded', () => {
    const createRoomBtn = document.getElementById('create-chatroom-btn');
    const modal = document.getElementById('create-chatroom-modal');
    const closeModalBtn = document.getElementById('cancel-create-btn');
    const createConfirmBtn = document.getElementById('confirm-create-btn');

    if (createRoomBtn) {
        createRoomBtn.addEventListener('click', () => {
            modal.style.display = 'block';
        });
    }

    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    }

    if (createConfirmBtn) {
        createConfirmBtn.addEventListener('click', () => {
            const chatRoomName = document.getElementById('new-chatroom-name').value.trim();
            if (!chatRoomName) {
                alert('채팅방 이름을 입력해주세요.');
                return;
            }

            const requestData = {
                chatRoomName: chatRoomName,
                createdByMemberId: currentUserId,
                companyId: currentCompanyId
            };
            console.log("서버로 생성할 채팅룸 정보 보냄 ", requestData);
            fetch('/api/chat/rooms/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to create chat room');
                }
                return response.json();
            })
            .then(chatRoomId => {
                console.log(`채팅방 생성 완료: ${chatRoomId}`);
                modal.style.display = 'none';
                loadUserChatRooms();
            })
            .catch(error => {
                console.error('Error creating chat room:', error);
                alert('채팅방 생성에 실패했습니다. 다시 시도해주세요.');
            });
        });
    }

    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
});

function deleteChatRoom(chatRoomId) {
    if (!chatRoomId) {
        console.error('Chat Room ID is not provided.');
        return;
    }

    if (confirm('정말 이 채팅방을 삭제하시겠습니까?')) {
        fetch(`/api/chat/rooms/delete/${chatRoomId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to delete chat room');
            }
            console.log(`채팅방 삭제 완료: ${chatRoomId}`);
            loadUserChatRooms();
        })
        .catch(error => {
            console.error('Error deleting chat room:', error);
            alert('채팅방 삭제에 실패했습니다. 다시 시도해주세요.');
        });
    }
}

function loadChatRoomParticipants(chatRoomId) {
    if (!chatRoomId) {
        console.error('Chat Room ID is not provided.');
        return;
    }
    console.debug("현재 채팅방 현재 채팅방", chatRoomId);
    fetch(`/api/chat/rooms/participants/${chatRoomId}`)
        .then(response => response.json())
        .then(participants => {
            console.log('Loaded Participants:', participants);
            const participantList = document.getElementById('invited-list');
            participantList.innerHTML = '';

            participants.forEach(participant => {
                const participantElement = document.createElement('li');
                participantElement.textContent = participant.memberName;
                participantList.appendChild(participantElement);
            });
        })
        .catch(error => console.error('Error loading participants:', error));
}

// 사용자 초대
function inviteUserToChatRoom(memberId) {
    if (invitedUsers.includes(memberId)) {
        alert('이미 초대된 사용자입니다.');
        return;
    }

    fetch('/api/chat/rooms/invite', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            chatRoomId: currentChatRoomId,
            memberId: memberId
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to invite user');
        }
        return response.text();
    })
    .then(result => {
        alert(result);
        invitedUsers.push(memberId);  // 초대된 사용자 목록에 추가
        loadUsersByCompany();  // 목록 갱신
    })
    .catch(error => console.error('Error inviting user:', error));
}

function leaveChatRoom(chatRoomId) {
    if (!confirm('정말 이 채팅방에서 나가시겠습니까?')) {
        return;
    }

    fetch('/api/chat/rooms/leave', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            chatRoomId: chatRoomId,
            memberId: currentUserId
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to leave chat room');
        }
        return response.text();
    })
    .then(result => {
        alert(result);
        loadUserChatRooms();
    })
    .catch(error => {
        console.error('Error leaving chat room:', error);
        alert('채팅방 나가기에 실패했습니다. 다시 시도해주세요.');
    });
}
