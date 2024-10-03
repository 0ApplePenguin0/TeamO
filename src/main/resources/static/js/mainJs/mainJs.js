document.addEventListener('DOMContentLoaded', function() {
    const monthYear = document.querySelector('.month-year');
    const calendarDays = document.querySelector('.calendar-days');

    <!-- ============ 달력 보여주기 ============ -->
    function generateCalendar() {
        const currentDate = new Date();
        const month = currentDate.getMonth();
        const year = currentDate.getFullYear();

        const firstDayOfMonth = new Date(year, month, 1);
        const lastDayOfMonth = new Date(year, month + 1, 0);

        monthYear.textContent = `${currentDate.toLocaleString('default', { month: 'long' })} ${year}`;

        const startingDay = firstDayOfMonth.getDay();
        const totalDays = lastDayOfMonth.getDate();

        let calendarHTML = '';

        for (let i = 0; i < startingDay; i++) {
            calendarHTML += '<div></div>';
        }

        for (let i = 1; i <= totalDays; i++) {
            if (i === currentDate.getDate()) {
                calendarHTML += `<div class="current-date">${i}</div>`;
            } else {
                calendarHTML += `<div>${i}</div>`;
            }
        }

        calendarDays.innerHTML = calendarHTML;
    }

    generateCalendar();
});

/* ========= 비콘 ========= */
document.addEventListener('DOMContentLoaded', function() {
    const startWorkBtn = document.getElementById('startWork');
    const leaveBtn = document.getElementById('leave');
    const startTimeSpan = document.getElementById('startTime');
    const leaveTimeSpan = document.getElementById('leaveTime');

    function updateTime(element) {
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        element.textContent = `${hours}:${minutes}`;
    }

    startWorkBtn.addEventListener('click', function() {
        updateTime(startTimeSpan);
    });

    leaveBtn.addEventListener('click', function() {
        updateTime(leaveTimeSpan);
    });
});

/* ========= 메모 상세보기 모달 ========= */
document.addEventListener('DOMContentLoaded', function() {
    // 요소 선택
    const memoModal = document.getElementById('memo-modal');
    const memoList = document.querySelector('.memo-box');

    <!-- =============== 메모 읽기 모달 =============== -->
    // 메모 클릭 시 모달 오픈
    memoList.addEventListener('click', function(e) {
        if (e.target.closest('.memo-item')) {
            const item = e.target.closest('.memo-item');
            showMemo(item);
        }
    });

    // 메모 내용 모달에 띄우기
    function showMemo(item) {
        const memoId = item.querySelector('#memoId').value;
        const memoContent = item.querySelector('#memoContent').getAttribute('data-full-content');
        const memoDate = item.querySelector('#memoDate').value;

        document.getElementById('memo-modal-subject').textContent = memoId;
        document.getElementById('memo-modal-content').textContent = memoContent;
        document.getElementById('memo-modal-date').textContent = memoDate;

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
