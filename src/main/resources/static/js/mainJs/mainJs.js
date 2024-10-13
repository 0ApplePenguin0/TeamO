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

});

/*=============== 비콘 ====================*/
let userLatitude;
let userLongitude;

// 사용자의 현재 위치 가져오기
function getUserLocation(callback) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            userLatitude = position.coords.latitude;
            userLongitude = position.coords.longitude;
            callback();
        }, function(error) {
            alert('위치 정보를 가져올 수 없습니다.');
        });
    } else {
        alert('GPS를 지원하지 않습니다');
    }
}

// 지도 초기화
// function initMap() {
//     let mapContainer = document.getElementById('map');
//     let mapOption = {
//         center: new kakao.maps.LatLng(userLatitude, userLongitude),
//         level: 3
//     };
//     let map = new kakao.maps.Map(mapContainer, mapOption);
// }
//
// 페이지 로드 시 지도 초기화
window.onload = function() {
    getUserLocation(function() {
        // initMap();
    });
};

// 시간 포맷팅 함수 추가
function formatTime(timeString) {
    if (!timeString) return '00:00';
    const date = new Date(timeString);
    return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: false });
}

// 출근 버튼 클릭 시 처리
document.getElementById('checkInBtn').addEventListener('click', function() {
    event.preventDefault();
    fetch('/attendance/check-in', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            latitude: userLatitude,
            longitude: userLongitude
        })
    }).then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                document.getElementById('checkInTime').textContent = formatTime(data.checkInTime);
                document.getElementById('checkInBtn').disabled = true;
                document.getElementById('checkOutBtn').disabled = false;
            } else {
                alert(data.message);
            }
        }).catch(error => {
        console.error('Error:', error);
    });
});

// 퇴근 버튼 클릭 시 처리
document.getElementById('checkOutBtn').addEventListener('click', function() {
    event.preventDefault();
    fetch('/attendance/check-out', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            latitude: userLatitude,
            longitude: userLongitude
        })
    }).then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                document.getElementById('checkOutTime').textContent = formatTime(data.checkOutTime);
                document.getElementById('checkOutBtn').disabled = true;
            } else {
                alert(data.message);
            }
        }).catch(error => {
        console.error('Error:', error);
    });
});

