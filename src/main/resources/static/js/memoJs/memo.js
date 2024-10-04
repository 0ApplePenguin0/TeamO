document.addEventListener('DOMContentLoaded', function() {
    // 요소 선택
    const compose = document.getElementById('compose-btn');
    const memoModal = document.getElementById('memo-modal');
    const memoList = document.querySelector('.memo-grid');
    const composeModal = document.getElementById('compose-modal');
    const composeForm = document.getElementById('addMemo');

    <!-- =============== 메모 읽기 모달 =============== -->
    // 메모 클릭 시 모달 오픈
    memoList.addEventListener('click', function(e) {
        if (e.target.classList.contains('memo-checkbox')) {
            return;
        }

        if (e.target.closest('.memo-item')) {
            const item = e.target.closest('.memo-item');
            showMemo(item);
        }
    });

    // 메모 내용 모달에 띄우기
    function showMemo(item) {
        const memoId = item.querySelector('#memoId').textContent;
        const memoContent = item.querySelector('#memoContent').textContent
        const memoDate = item.querySelector('#memoDate').textContent;

        document.getElementById('modal-subject').textContent = memoId;
        document.getElementById('modal-content').textContent = memoContent;
        document.getElementById('modal-date').textContent = memoDate;

        memoModal.style.display = 'block';
    }

    // 모달 닫기 버튼 이벤트
    const closeBtns = document.getElementsByClassName('close');

    Array.from(closeBtns).forEach(btn => {
        btn.addEventListener('click', function() {
            memoModal.style.display = 'none';
            composeModal.style.display = 'none';
        });
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(e) {
        if (e.target == memoModal) {
            memoModal.style.display = 'none';
        }
        if (e.target == composeModal) {
            composeModal.style.display = 'none';
        }
    });

    <!-- =============== 메모 작성 모달 =============== -->
    //메모 작성 버튼 처리
    compose.addEventListener('click', function() {
        composeModal.style.display = 'block';
    });

    // 메모 작성 폼 제출 이벤트
    composeForm.addEventListener('submit', function(e) {
        e.preventDefault();

        let formData = composeForm;

        $.ajax({
            url: '/memo/addMemo',
            type: 'post',
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (e) {
                alert('메모가 저장되었습니다.');
            },
            error: function (e) {
                alert('메모 저장 실패하였습니다.');
            }
        });

        composeModal.style.display = 'none';
        composeForm.reset();
    });

});