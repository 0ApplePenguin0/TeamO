document.getElementById('delete-btn').addEventListener('click', function() {
    var selectedMessages = [];

    document.querySelectorAll('.message-check:checked').forEach(function(checkbox) {
        selectedMessages.push(checkbox.value);
    });

    if (selectedMessages.length > 0) {
        if (confirm('선택된 메모를 삭제하시겠습니까?')) {
            // 서버로 삭제 요청 보내기
            $.ajax({
                url: 'delete',
                type: 'POST',
                data: JSON.stringify(selectedMessages), // 배열을 JSON으로 변환하여 전송
                contentType: 'application/json; charset=utf-8',
                success: function(response) {
                    alert('선택된 메세지가 삭제되었습니다.');
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

document.getElementById('compose-btn').addEventListener('click', function() {
    var selectedMessages = [];

    document.querySelectorAll('.message-check:checked').forEach(function(checkbox) {
        selectedMessages.push(checkbox.value);
    });

    if (selectedMessages.length > 0) {
        if (confirm('선택된 메모를 복원하시겠습니까?')) {
            // 서버로 삭제 요청 보내기
            $.ajax({
                url: 'restoreMessage',
                type: 'POST',
                data: JSON.stringify(selectedMessages), // 배열을 JSON으로 변환하여 전송
                contentType: 'application/json; charset=utf-8',
                success: function(response) {
                    alert('선택된 메세지가 복원되었습니다.');
                    location.reload(); // 삭제 후 페이지 새로고침
                },
                error: function(error) {
                    alert('메모 복원에 실패하였습니다.');
                }
            });
        }
    } else {
        alert('복원할 메모를 선택해주세요.');
    }
});