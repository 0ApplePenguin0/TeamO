// 메시지 팝업을 열고 메시지 읽음 상태를 업데이트하는 함수
function openMessagePopup(messageId) {
    var url = 'readReceived?messageId=' + messageId; // 메시지 읽기 페이지 URL 생성
    var windowFeatures = 'left=500,top=300,width=600,height=400,location=no,titlebar=no,scrollbars=yes'; // 팝업 창 속성 설정
    window.open(url, 'messageWin', windowFeatures); // 팝업 창 열기

    // 읽음 상태 업데이트
    fetch(`updateReadStatus?messageId=${messageId}`)
        .then(response => response.text()) // 서버에서 응답을 텍스트로 받음
        .then(result => {
            if (result === 'success') {
                console.log('Message status updated to read.'); // 성공 메시지 출력
            } else {
                console.error('Failed to update message status.'); // 실패 메시지 출력
            }
        });
}

// 메시지를 삭제하는 함수
function deleteMessage(messageId) {
    // 사용자에게 삭제 확인 메시지 표시
    if (confirm("이 메시지를 삭제하시겠습니까?")) {
        // 삭제 상태 업데이트 요청
        fetch(`updateDeleteStatus?messageId=${messageId}`)
            .then(response => response.text()) // 서버에서 응답을 텍스트로 받음
            .then(result => {
                if (result === 'success') {
                    alert('메시지가 삭제되었습니다.'); // 성공 메시지 출력
                    location.reload(); // 페이지 새로고침
                } else {
                    alert('메시지 삭제에 실패했습니다.'); // 실패 메시지 출력
                }
            })
            .catch(error => console.error('Error:', error)); // 오류 발생 시 콘솔에 출력
    }
}

// 쪽지 작성 클릭시 페이지 이동
document.addEventListener('DOMContentLoaded', function() {
    const composeBtn = document.getElementById('compose-btn');

    composeBtn.addEventListener('click', function() {
        window.location.href = '/main/message/send';
    });
});