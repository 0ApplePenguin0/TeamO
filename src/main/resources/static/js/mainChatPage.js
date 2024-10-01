let websocket = null;  // 웹소켓 객체
let currentChatRoomId = null;  // 현재 채팅방 ID
let currentUserId = null;  // 현재 로그인된 사용자 ID

// 유저 목록 보기 버튼 클릭 이벤트
document.getElementById('show-users-btn').onclick = function() {
    const userListContainer = document.getElementById('user-list');
    if (userListContainer.style.display === 'none' || userListContainer.style.display === '') {
        userListContainer.style.display = 'block'; // 목록 표시
        getAllUsers();  // 유저 목록 불러오기
    } else {
        userListContainer.style.display = 'none'; // 목록 숨기기
    }
};

// 유저 목록 불러오기
async function getAllUsers() {
    const currentUserId = await getCurrentUserMemberId();
    if (!currentUserId) {
        console.error("사용자 정보를 가져오지 못했습니다.");
        return;
    }

    try {
        const response = await fetch('/api/members/getmembers');
        const users = await response.json();
        const userListContainer = document.getElementById('user-list');
        userListContainer.innerHTML = ''; // 기존 목록 초기화

        const currentUser = users.find(user => user.memberId === currentUserId);
        const otherUsers = users.filter(user => user.memberId !== currentUserId);

        if (currentUser) {
            const currentUserElement = document.createElement('div');
            currentUserElement.textContent = `${currentUser.memberName} (나)`;
            userListContainer.appendChild(currentUserElement);
        }

        otherUsers.forEach(user => {
            const userElement = document.createElement('div');
            userElement.textContent = `${user.memberName}`;

            const inviteButton = document.createElement('button');
            inviteButton.textContent = '초대';
            inviteButton.style.marginLeft = '10px';
            inviteButton.onclick = function() {
                inviteUserToChatRoom(user.memberId, currentChatRoomId);
            };

            userElement.appendChild(inviteButton);
            userListContainer.appendChild(userElement);
        });

    } catch (error) {
        console.error('사용자 목록을 불러오는 중 오류가 발생했습니다:', error);
    }
}

// 채팅방 추가 버튼 클릭 시 실행되는 함수
document.getElementById('add-room-btn').onclick = async function () {
    const newRoomName = document.getElementById('new-room-name').value.trim();
    if (!newRoomName) {
        alert("채팅방 이름을 입력하세요.");
        return;
    }

    try {
        const response = await fetch('/api/chat/rooms/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
			body: JSON.stringify({
			    chatRoomName: newRoomName,
			    createdByMemberId: currentUserId,
			    companyId: 1  // 임시로 1을 설정
			})

        });

        if (response.ok) {
            alert("채팅방이 생성되었습니다.");
            loadChatRooms();  // 채팅방 목록 새로 불러오기
        } else {
            alert("채팅방 생성에 실패했습니다.");
        }
    } catch (error) {
        console.error("채팅방 생성 중 오류가 발생했습니다:", error);
    }
};
// 현재 채팅방 참여자를 가져오는 함수
async function getChatRoomParticipants() {
    if (!currentChatRoomId) {
        alert("채팅방을 먼저 선택하세요.");
        return;
    }

    try {
        const response = await fetch(`/api/chat/rooms/participants/${currentChatRoomId}`);
        if (response.ok) {
            const participants = await response.json();
            displayParticipants(participants);
        } else {
            console.error("참여자 목록을 가져오는 데 실패했습니다.");
        }
    } catch (error) {
        console.error("참여자 목록을 가져오는 중 오류가 발생했습니다:", error);
    }
}

// 참여자 목록을 화면에 표시하는 함수
function displayParticipants(participants) {
    const participantListContainer = document.getElementById('participant-list');
    participantListContainer.innerHTML = '';  // 기존 목록 초기화

    if (participants.length === 0) {
        participantListContainer.innerHTML = '참여자가 없습니다.';
    } else {
        participants.forEach(participant => {
            const participantElement = document.createElement('div');
            participantElement.textContent = participant;
            participantListContainer.appendChild(participantElement);
        });
    }
}
async function inviteUserToChatRoom(memberId, currentChatRoomId) {
    console.log("memberId, currentChatRoomId", memberId, currentChatRoomId);
    try {
        const response = await fetch('/api/chat/rooms/invite', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 
                chatRoomId: currentChatRoomId,  // 여기서 chatRoomId를 전송
                createdByMemberId: memberId     // 초대할 사용자 ID
            })
        });
        if (response.ok) {
            alert(`${memberId}님이 채팅방에 초대되었습니다.`);
        } else {
            alert("초대에 실패했습니다.");
        }
    } catch (error) {
        console.error('채팅방 초대 중 오류가 발생했습니다:', error);
    }
}
// 페이지 로드 시 채팅방 목록 불러오기
window.onload = async function() {
    currentUserId = await getCurrentUserMemberId();  // 현재 로그인된 사용자 ID 설정
    if (currentUserId) {
        loadChatRooms();  // 채팅방 목록 불러오기
    } else {
        console.error("사용자 정보를 가져오지 못했습니다.");
    }
};

// 현재 사용자의 ID를 가져오는 함수
async function getCurrentUserMemberId() {
    try {
        const response = await fetch('/api/members/getCurrentUserMemberId');
        if (response.ok) {
            const memberId = await response.text();  // 응답을 문자열로 변환
            console.log("현재 로그인된 사용자 ID: ", memberId);
            return memberId;
        } else {
            console.error("사용자 정보를 가져오지 못했습니다.");
            return null;
        }
    } catch (error) {
        console.error("현재 로그인된 사용자 정보를 가져오는 중 오류가 발생했습니다:", error);
        return null;
    }
}

// 채팅방에 들어갈 때 이전 메시지 로드
async function loadMessages(chatRoomId) {
    const response = await fetch(`/api/chat/messages/${chatRoomId}`);
    if (response.ok) {
        const messages = await response.json();
        const messageContainer = document.getElementById('chat-messages');
        messageContainer.innerHTML = ''; // 기존 메시지 초기화

        // 저장된 메시지 불러오기
        messages.forEach(message => {
            const messageElement = document.createElement('div');
            messageElement.textContent = `[${message.sentAt}] ${message.memberId}: ${message.message}`;
            messageContainer.appendChild(messageElement);
        });
    }
}

// WebSocket으로 서버에 메시지 전송 및 수신
function joinChatRoom(roomId) {
    currentChatRoomId = roomId;

    // 이전 메시지 불러오기
    loadMessages(roomId);

    if (websocket !== null) {
        websocket.close();  // 기존 연결이 있을 경우 닫음
    }

    // WebSocket 연결 설정
    websocket = new WebSocket(`ws://localhost:8888/ws/chat/${roomId}`);

    websocket.onopen = function(event) {
        console.log(`Connected to WebSocket room ${roomId}`);
    };

    websocket.onmessage = function(event) {
        const messageContainer = document.getElementById('chat-messages');
        const messageElement = document.createElement('div');
        messageElement.textContent = event.data;  // 서버에서 전송된 메시지 표시
        messageContainer.appendChild(messageElement);
    };

    websocket.onclose = function(event) {
        console.log(`Disconnected from WebSocket room ${roomId}`);
    };

	// 메시지 전송 버튼 클릭 시 WebSocket 메시지 전송
	document.getElementById('send-btn').onclick = function() {
	    const messageContent = document.getElementById('chat-input').value;
	    if (messageContent.trim() !== "") {
	        // 메시지를 JSON 형태로 변환해서 WebSocket으로 전송
	        const message = JSON.stringify({
	            chatRoomId: currentChatRoomId,
	            memberId: currentUserId, // 현재 로그인한 사용자 ID
	            message: messageContent
	        });
	        
	        websocket.send(message); // 메시지를 WebSocket으로 전송
	        document.getElementById('chat-input').value = '';  // 입력창 초기화
	    }
	};
}

// 채팅방 목록 불러오기
async function loadChatRooms() {
    const currentUserId = await getCurrentUserMemberId(); // 현재 로그인된 사용자 ID 가져오기
    if (!currentUserId) {
        console.error("사용자 정보를 가져오지 못했습니다.");
        return;
    }

    const response = await fetch(`/api/chat/rooms/getChatRoomsByUser/${currentUserId}`);
    if (response.ok) {
        const chatRooms = await response.json();
        const chatRoomListContainer = document.getElementById('chat-rooms');
        chatRoomListContainer.innerHTML = ''; // 기존 목록 초기화

        // 서버에서 반환된 chatRooms 배열에서 chatRoomName과 chatRoomId 모두를 사용
        chatRooms.forEach(room => {
            const roomElement = document.createElement('div');
            roomElement.textContent = room.chatRoomName;  // 채팅방 이름 표시
            roomElement.classList.add('chat-room');

            // 채팅방 클릭 이벤트 추가
            roomElement.onclick = async function () {
                document.getElementById('room-title').textContent = room.chatRoomName;
                currentChatRoomId = room.chatRoomId;  // WebSocket 연결을 위한 ID 설정
                joinChatRoom(room.chatRoomId);  // WebSocket 연결
            };

            chatRoomListContainer.appendChild(roomElement);
        });
    } else {
        console.error("채팅방 목록을 불러오는 데 실패했습니다.");
    }
	// 채팅방 삭제 버튼 클릭 시 실행되는 함수
	document.getElementById('delete-room-btn').onclick = async function () {
	    if (!currentChatRoomId) {
	        alert("삭제할 채팅방을 선택하세요.");
	        return;
	    }

	    const confirmDelete = confirm("정말로 이 채팅방을 삭제하시겠습니까?");
	    if (!confirmDelete) return;

	    try {
	        const response = await fetch(`/api/chat/rooms/delete/${currentChatRoomId}`, {
	            method: 'DELETE'
	        });

	        if (response.ok) {
	            alert("채팅방이 삭제되었습니다.");
	            loadChatRooms(); // 채팅방 목록 새로 불러오기
	        } else {
	            alert("채팅방 삭제에 실패했습니다.");
	        }
	    } catch (error) {
	        console.error("채팅방 삭제 중 오류가 발생했습니다:", error);
	    }
	};
}
