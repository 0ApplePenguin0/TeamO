
    // 메시지 모달을 여는 함수
    function openMessageModal(messageId) {
    // AJAX로 서버에서 메시지 상세 내용을 가져옴
    fetch(`readSent?messageId=${messageId}`)
        .then(response => response.json()) // JSON 형식으로 서버 응답을 파싱
        .then(data => {
            // 모달에 메시지 데이터를 채움
            document.getElementById('modal-subject').textContent = data.title;
            document.getElementById('modal-receiver').textContent = data.receiverId + ' (부서: ' + data.departmentName + ', 하위부서: ' + data.teamName + ')';
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

