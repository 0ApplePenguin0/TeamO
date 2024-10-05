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
function initMap() {
    let mapContainer = document.getElementById('map');
    let mapOption = {
        center: new kakao.maps.LatLng(userLatitude, userLongitude),
        level: 3
    };
    let map = new kakao.maps.Map(mapContainer, mapOption);
}

// 페이지 로드 시 지도 초기화
window.onload = function() {
    getUserLocation(function() {
        initMap();
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

<!--========= 날씨 가져오기 =============== -->
// 날씨 정보를 출력할 HTML 요소
const temperatureElement = document.querySelector('.temperature');
const weatherDescriptionElement = document.querySelector('.weather-description');
// 날씨 아이콘 매핑
const weatherIconMap = {
    '맑음': '<i class="uil uil-sun sun"></i>',
    '비': '<i class="uil uil-cloud-drizzle rain"></i>',
    '소나기': '<i class="uil uil-cloud-drizzle rain"></i>',
    '눈': '<i class="uil uil-snowflake snow"></i>',
    '비/눈': '<i class="uil uil-snowflake snow"></i>',
    '알 수 없음': '알 수 없음'
};

// 날씨 아이콘 업데이트 함수
function updateWeatherIcon() {
    const weatherDescriptionElement = document.querySelector('.weather-description');
    const currentWeather = weatherDescriptionElement.textContent.trim();

    if (currentWeather in weatherIconMap) {
        weatherDescriptionElement.innerHTML = weatherIconMap[currentWeather];
    }
}

// DOM이 로드된 후 이 함수를 호출합니다
document.addEventListener('DOMContentLoaded', () => {
    // 날씨 데이터가 비동기적으로 가져와지고 업데이트된다고 가정합니다
    // 날씨 데이터가 업데이트된 후 이 함수를 호출해야 할 수 있습니다
    updateWeatherIcon();
});


// 서버에서 날씨 정보를 가져오는 함수
function getWeatherForCurrentLocation(lat, lon) {
    fetch(`/api/weather?lat=${lat}&lon=${lon}`)
        .then(response => response.text())
        .then(data => {
            // 가져온 데이터를 파싱하여 화면에 출력
            const weatherInfo = data.split(', ');
            const temperature = weatherInfo[0].split(': ')[1];
            const weatherCondition = weatherInfo[1].split(': ')[1];

            temperatureElement.textContent = `${temperature}`;
            weatherDescriptionElement.textContent = `${weatherCondition}`;

            updateWeatherIcon();
        })
        .catch(error => {
            alert('날씨 정보를 불러오는 중 오류가 발생했습니다.');
        });
}

// 사용자 위치 정보를 가져오는 함수
function getCurrentLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(position => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;
            getWeatherForCurrentLocation(lat, lon);
        }, error => {
            alert('위치 정보를 가져올 수 없습니다.');
        });
    } else {
        alert('이 브라우저에서는 위치 정보가 지원되지 않습니다.');
    }
}

// 페이지 로드 시 위치 정보를 가져옴
window.onload = getCurrentLocation;