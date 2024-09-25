// 페이지 로드 시 setCurrentName 호출하여 currentName 설정
window.onload = function() {
    // 메인페이지 클릭 이벤트를 호출하여 사용자 목록을 자동으로 불러오도록 함
    document.getElementById('main-page').click();
};
let currentChatRoom = null;
loadChatRooms();

async function loadChatRooms() {
    const currentUserId = await getCurrentUserMemberId(); // 현재 로그인된 사용자 ID 가져오기
    if (!currentUserId) {
        console.error("사용자 정보를 가져오지 못했습니다.");
        return;
    }

    console.log("currentUserId : ", currentUserId);

    const response = await fetch(`/api/chat/rooms/getChatRoomsByUser/${currentUserId}`); // 서버에서 채팅방 목록을 가져오는 API 호출

    if (response.ok) {
        if (response.status === 204) {
            // 204 No Content일 경우 처리
            console.log("채팅방이 없습니다.");
            const chatRoomListContainer = document.getElementById('chat-rooms');
            chatRoomListContainer.innerHTML = ''; // 기존 목록 초기화
            return;
        }

        const chatRooms = await response.json();
        const chatRoomListContainer = document.getElementById('chat-rooms');
        chatRoomListContainer.innerHTML = ''; // 기존 목록 초기화
        console.log("chatRooms : ", chatRooms);
        
        chatRooms.forEach(room => {
            const roomElement = document.createElement('div');
            roomElement.textContent = room; 
            roomElement.classList.add('chat-room');
            // 채팅방 클릭 이벤트 추가
            roomElement.onclick = function () {
                document.getElementById('room-title').textContent = room;
				currentChatRoom = room;
				console.log("현재 채팅창 이름", currentChatRoom);	
            };
            chatRoomListContainer.appendChild(roomElement);
        });
    } else {
        console.error("채팅방 목록을 불러오지 못했습니다.");
    }
}



//현재 사용자의 아이디
async function getCurrentUserMemberId() {
    try {
        // 현재 로그인된 사용자의 memberId를 가져오는 API 호출
        const response = await fetch('/api/members/getCurrentUserMemberId');
        if (response.ok) {
            const memberId = await response.text(); // 응답은 문자열로 반환되므로 text() 사용
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

document.getElementById('add-room-btn').onclick = async function() {
    const roomName = document.getElementById('new-room-name').value;
    if (roomName.trim() !== "") {
        const currentUserId = await getCurrentUserMemberId(); // 로그인된 사용자 ID 가져오기
        if (!currentUserId) {
            alert("사용자 정보를 가져오지 못했습니다.");
            return;
        }

        // 서버에서 현재 사용자의 회사 정보를 받아오기 (API 호출을 통해 처리)
        const companyUrlResponse = await fetch('/api/members/getCompanyId');
        const companyUrl = await companyUrlResponse.text();

        const newRoom = {
            chatRoomName: roomName,  // 채팅방 이름
            createdByMemberId: currentUserId, // 로그인된 사용자의 ID를 추가
            companyId: companyUrl // 서버에서 받은 companyUrl을 사용
        };

        console.log("새로운 룸 생성", newRoom);
        try {
            const response = await fetch('/api/chat/rooms/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newRoom) // 서버로 새로운 방 정보 전송
            });
            if (response.ok) {
                alert("채팅방이 추가되었습니다.");
                document.getElementById('new-room-name').value = ''; // 입력 필드 초기화
                loadChatRooms();  // 채팅방 목록 새로 불러오기
            } else {
                alert("채팅방 추가에 실패했습니다.");
            }
        } catch (error) {
            console.error("채팅방 추가 중 오류가 발생했습니다:", error);
        }
    } else {
        alert("채팅방 이름을 입력해주세요.");
    }
};

// 채팅방 삭제 기능
async function deleteChatRoom(roomId) {
  
}

// 유저 목록 불러오기
async function getAllUsers() {
    const currentUserId = await getCurrentUserMemberId(); // 현재 로그인된 사용자 ID 가져오기
    if (!currentUserId) {
        console.error("사용자 정보를 가져오지 못했습니다.");
        return;
    }

    try {
        const response = await fetch('/api/members/getmembers');
        const users = await response.json();
        const userListContainer = document.getElementById('user-list');
        userListContainer.innerHTML = ''; // 기존 목록 초기화

        // 로그인된 사용자를 맨 위에 표시하기 위해 사용자 목록에서 필터링
        const currentUser = users.find(user => user.memberId === currentUserId);
        const otherUsers = users.filter(user => user.memberId !== currentUserId);

        // 현재 사용자 항목 추가 (맨 위에 표시)
        if (currentUser) {
            const currentUserElement = document.createElement('div');
            currentUserElement.textContent = `${currentUser.memberName} (나)`;

            // 초대 버튼을 표시하지 않음
            userListContainer.appendChild(currentUserElement);
        }

        // 다른 사용자 항목 추가
        otherUsers.forEach(user => {
            const userElement = document.createElement('div');
            userElement.textContent = `${user.memberName}`;

            // 초대 버튼 생성
            const inviteButton = document.createElement('button');
            inviteButton.textContent = '초대';
            inviteButton.style.marginLeft = '10px';
            inviteButton.onclick = function() {
                inviteUserToChatRoom(user.memberId, currentChatRoom); // 초대 기능 호출
            };

            userElement.appendChild(inviteButton); // 사용자 항목에 초대 버튼 추가
            userListContainer.appendChild(userElement);
        });

        console.log(users);
    } catch (error) {
        console.error('사용자 목록을 불러오는 중 오류가 발생했습니다:', error);
    }
}


// 사용자를 채팅방에 초대하는 함수
async function inviteUserToChatRoom(memberId, currentChatRoom) {
    console.log("memberId, roomId : ", memberId, currentChatRoom);
    try {
        const response = await fetch('/api/chat/rooms/invite', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            // ChatRoomDTO에 맞춰 데이터를 보내도록 수정
            body: JSON.stringify({ 
                chatRoomName: currentChatRoom,  // 채팅방 이름
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

// 전체 채팅방 클릭 이벤트 추가
document.getElementById('main-page').onclick = async function() {
    document.getElementById('room-title').textContent = "전체 채팅방";

};

// 부서채팅 클릭 이벤트 추가
document.getElementById('main-page2').onclick = async function() {
    document.getElementById('room-title').textContent = "부서 채팅방";

};
