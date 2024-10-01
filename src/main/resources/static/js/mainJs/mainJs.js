document.addEventListener('DOMContentLoaded', function() {
    const monthYear = document.querySelector('.month-year');
    const calendarDays = document.querySelector('.calendar-days');

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

/* ========= 비콘 모달 ========= */
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


/* ========= 쪽지 및 메모 상세 모달 ========= */
document.addEventListener('DOMContentLoaded', function() {
    const messageModal = document.getElementById('message-modal');
    const memoModal = document.getElementById('memo-modal');
    const messageBox = document.querySelector('.message-box');
    const memoBox = document.querySelector('.memo-box');
    const closeBtn = document.querySelector('.close');
    const memoCloseBtn = document.querySelector('.memo-close');

    // 쪽지 아이템 클릭 이벤트
    messageBox.addEventListener('click', function(e) {
        if (e.target.closest('.message-item')) {
            const messageItem = e.target.closest('.message-item');
            showMessage(messageItem);
        }
    });

    // 메모 아이템 클릭 이벤트
    memoBox.addEventListener('click', function(e) {
        if (e.target.closest('.memo-item')) {
            const memoItem = e.target.closest('.memo-item');
            showMemo(memoItem);
        }
    });

    // 쪽지 모달 닫기 버튼 이벤트
    closeBtn.addEventListener('click', function() {
        messageModal.style.display = 'none';
    });

    // 메모 모달 닫기 버튼 이벤트
    memoCloseBtn.addEventListener('click', function() {
        memoModal.style.display = 'none';
    });

    // 쪽지 내용 표시
    function showMessage(messageItem) {
        const subject = messageItem.querySelector('.subject').textContent;
        const sender = messageItem.querySelector('.sender').textContent;
        const messageDate = messageItem.querySelector('.date').value;

        document.getElementById('modal-subject').textContent = subject;
        document.getElementById('modal-sender').textContent = '보낸 사람: ' + sender;
        document.getElementById('modal-date').textContent = '받은 날짜: ' + messageDate;
        document.getElementById('modal-content').textContent = '여기에 메시지 내용이 표시됩니다.'; // 실제 내용으로 대체 필요

        messageModal.style.display = 'block';
    }

    // 메모 내용 표시
    function showMemo(memoItem) {
        const memoTitle = memoItem.querySelector('.memo-title').textContent;
        const memoDate = memoItem.querySelector('.memo-date').value;

        document.getElementById('modal-memo-title').textContent = memoTitle;
        document.getElementById('modal-memo-date').textContent = '작성 날짜: ' + memoDate;
        document.getElementById('memo-content').textContent = '여기에 메시지 내용이 표시됩니다.'; // 실제 내용으로 대체 필요

        memoModal.style.display = 'block';
    }

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(e) {
        if (e.target == messageModal || e.target == memoModal) {
            messageModal.style.display = 'none';
            memoModal.style.display = 'none';
        }
    });
});