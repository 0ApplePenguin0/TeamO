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
                document.getElementById('checkInTime').textContent = "출근 시간: " + data.checkInTime;
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
                document.getElementById('checkOutTime').textContent = "퇴근 시간: " + data.checkOutTime;
                document.getElementById('checkOutBtn').disabled = true;
            } else {
                alert(data.message);
            }
        }).catch(error => {
        console.error('Error:', error);
    });
});