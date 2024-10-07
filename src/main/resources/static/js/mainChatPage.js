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
	                    if(room.createdByMemberId == currentUserId) {
	                        console.log("채팅방 생성자와 현재 로그인 사용자 비교", room.createdByMemberId, currentUserId);
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
	            console.log('Loaded Users:', users);  // 사용자 목록 확인
	            const participantList = document.getElementById('participant-list');
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
	
	// 채팅방 생성
	document.addEventListener('DOMContentLoaded', () => {
	    const createRoomBtn = document.getElementById('create-chatroom-btn');
	    const modal = document.getElementById('create-chatroom-modal');
	    const closeModalBtn = document.getElementById('cancel-create-btn');
	    const createConfirmBtn = document.getElementById('confirm-create-btn');
	    const chatRoomTitle = document.getElementById('chat-room-title');  // 이 부분에서 chatRoomTitle 변수를 가져옵니다.
	
	    // '채팅방 생성' 버튼 클릭 시 모달 열기
	    if (createRoomBtn) {
	        createRoomBtn.addEventListener('click', () => {
	            modal.style.display = 'block';
	        });
	    }
	
	    // '취소' 버튼 클릭 시 모달 닫기
	    if (closeModalBtn) {
	        closeModalBtn.addEventListener('click', () => {
	            modal.style.display = 'none';
	        });
	    }
	
	    // '생성' 버튼 클릭 시 채팅방 생성 로직 수행
	    if (createConfirmBtn) {
	        createConfirmBtn.addEventListener('click', () => {
	            const chatRoomName = document.getElementById('new-chatroom-name').value.trim();
	            if (!chatRoomName) {
	                alert('채팅방 이름을 입력해주세요.');
	                return;
	            }
	
	            // 서버로 전송할 데이터 구성
	            const requestData = {
	                chatRoomName: chatRoomName,
	                createdByMemberId: currentUserId,
	                companyId: currentCompanyId
	            };
	            console.log("서버로 생성할 채팅룸 정보 보냄 ", requestData);
	
	            // 서버로 채팅방 생성 요청 보내기
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
	                modal.style.display = 'none';  // 모달 닫기
	                loadUserChatRooms();  // 새로운 채팅방 목록을 갱신하는 함수 호출
	                chatRoomTitle.textContent = chatRoomName;  // 채팅방 제목 갱신
	            })
	            .catch(error => {
	                console.error('Error creating chat room:', error);
	                alert('채팅방 생성에 실패했습니다. 다시 시도해주세요.');
	            });
	        });
	    }
	
	    // 모달 외부를 클릭하면 모달을 닫음
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
	            loadUserChatRooms();  // 삭제 후 채팅방 목록을 갱신합니다.
	        })
	        .catch(error => {
	            console.error('Error deleting chat room:', error);
	            alert('채팅방 삭제에 실패했습니다. 다시 시도해주세요.');
	        });
	    }
	}
	
	// 채팅방 참여자 목록 불러오기
	function loadChatRoomParticipants(chatRoomId) {
	    if (!chatRoomId) {
	        console.error('Chat Room ID is not provided.');
	        return;
	    }
	    fetch(`/api/chat/rooms/participants/${chatRoomId}`)
	        .then(response => response.json())
	        .then(participants => {
	            console.log('Loaded Participants:', participants);
	            const participantList = document.getElementById('d-list');
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
	
	// 사용자가 채팅방에서 나가는 함수
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
	            memberId: currentUserId  // 현재 로그인한 사용자 ID
	        })
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error('Failed to leave chat room');
	        }
	        return response.text();
	    })
	    .then(result => {
	        alert(result);  // 성공 메시지를 사용자에게 보여줌
	        loadUserChatRooms();  // 채팅방 목록을 갱신하여 사용자가 나간 채팅방을 제거함
	    })
	    .catch(error => {
	        console.error('Error leaving chat room:', error);
	        alert('채팅방 나가기에 실패했습니다. 다시 시도해주세요.');
	    });
	}
	// 사용자를 채팅방에 초대하는 함수
	function inviteUserToChatRoom(memberId) {
	    fetch('/api/chat/rooms/invite', {
	        method: 'POST',
	        headers: {
	            'Content-Type': 'application/json'
	        },
	        body: JSON.stringify({
	            chatRoomId: currentChatRoomId,  // 현재 채팅방 ID
	            memberId: memberId  // 초대할 사용자 ID
	        })
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error('Failed to invite user');
	        }
	        return response.text();
	    })
	    .then(result => {
	        alert(result);  // 성공 메시지를 사용자에게 보여줌
	    })
	    .catch(error => {
	        console.error('Error inviting user:', error);
	        alert('사용자 초대에 실패했습니다. 다시 시도해주세요.');
	    });
	}