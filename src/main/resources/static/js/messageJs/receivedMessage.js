let currentMessageId;

function openMessageModal(messageId) {
    currentMessageId = messageId; // 메시지 ID 저장
    // AJAX로 서버에서 메시지 상세 내용을 가져옴
    fetch(`readreceived?messageId=${messageId}`)
        .then(response => response.json()) // JSON 형식으로 서버 응답을 파싱
        .then(data => {
            // 모달에 메시지 데이터를 채움
            document.getElementById('modal-subject').textContent = data.title;
            document.getElementById('modal-sender').textContent = data.senderId + ' (부서: ' + data.departmentName + ', 하위부서: ' + data.teamName + ')';
            document.getElementById('modal-date').textContent = data.sentAt;
            document.getElementById('modal-content').textContent = data.content;

            const downloadLink = document.getElementById('modal-file-link');
            downloadLink.href = `/main/board/download?messageId=${messageId}`;
            downloadLink.style.display = 'inline'; // 링크를 보이게 설정
            downloadLink.textContent = '파일 다운로드';

            // 모달을 화면에 표시
            document.getElementById('message-modal').style.display = 'block';
        })
        .catch(error => console.error('Error:', error)); // 오류 발생 시 콘솔에 에러 출력
}

function closeMessageModal() {
    document.getElementById('message-modal').style.display = 'none';
}

// 모달 외부를 클릭하면 닫히도록 처리
window.onclick = function(event) {
    var modal = document.getElementById('message-modal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}





document.getElementById('delete-btn').addEventListener('click', function() {
    var selectedMessages = [];

    document.querySelectorAll('.message-check:checked').forEach(function(checkbox) {
        selectedMessages.push(checkbox.value);
    });

    if (selectedMessages.length > 0) {
        if (confirm('선택된 메모를 삭제하시겠습니까?')) {
            // 서버로 삭제 요청 보내기
            $.ajax({
                url: 'updateDeleteStatus',
                type: 'POST',
                data: JSON.stringify(selectedMessages), // 배열을 JSON으로 변환하여 전송
                contentType: 'application/json; charset=utf-8',
                success: function(response) {
                    alert('선택된 메모가 휴지통으로 이동되었습니다.');
                    location.reload(); // 삭제 후 페이지 새로고침
                },
                error: function(error) {
                    alert('메모 삭제 실패하였습니다.');
                }
            });
        }
    } else {
        alert('삭제할 메모를 선택해주세요.');
    }
});

function openModal() {
    document.getElementById('myModal').style.display = "block"; // 모달 열기
}

function closeModal() {
    document.getElementById('myModal').style.display = "none"; // 모달 닫기
}


function redirectToReplyPage() {
    window.open(`/main/message/reply?messageId=${currentMessageId}`, '_blank');
}


