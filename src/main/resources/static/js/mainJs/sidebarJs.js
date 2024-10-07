/* ========= 로그아웃 모달 ========= */
document.addEventListener('DOMContentLoaded', function() {
    const logoutBtn = document.getElementById('logoutBtn');
    const modal = document.getElementById('logoutModal');
    const confirmBtn = document.getElementById('confirmLogout');
    const cancelBtn = document.getElementById('cancelLogout');


    // 로그아웃 모달
    logoutBtn.addEventListener('click', (e) => {
        e.preventDefault();
        modal.style.display = 'block';
    });

    // 로그아웃 확인 -> 로그아웃 처리
    confirmBtn.addEventListener('click', () => {
        window.location.href = "/logout";
        modal.style.display = 'none';
    });

    // 로그아웃 취소
    cancelBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    // 로그아웃 모달이 아닌 곳 클릭 시 모달 창 꺼짐
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });


    /*==================== 하위메뉴 토글  ======================*/

    const menuItems = document.querySelectorAll('.menu-items .nav-links > li > a, .menu-items .logout-mode > li > a');

    menuItems.forEach(item => {
        const submenu = item.nextElementSibling;
        if (submenu && submenu.classList.contains('submenu')) {
            // 초기 상태에서 하위 메뉴 숨기기
            submenu.style.display = 'none';

            // 상위 메뉴 클릭 이벤트 리스너 추가
            item.addEventListener('click', function(e) {
                e.preventDefault(); // 기본 링크 동작 방지

                // 현재 클릭된 메뉴의 하위 메뉴 토글
                if (submenu.style.display === 'none') {
                    submenu.style.display = 'block';
                    item.classList.add('active');
                } else {
                    submenu.style.display = 'none';
                    item.classList.remove('active');
                }

                // 다른 열린 하위 메뉴 닫기
                menuItems.forEach(otherItem => {
                    if (otherItem !== item) {
                        const otherSubmenu = otherItem.nextElementSibling;
                        if (otherSubmenu && otherSubmenu.classList.contains('submenu')) {
                            otherSubmenu.style.display = 'none';
                            otherItem.classList.remove('active');
                        }
                    }
                });
            });
        }
    });
});

// <!--========= 날씨 가져오기 =============== -->
// // 날씨 정보를 출력할 HTML 요소
// const temperatureElement = document.querySelector('.temperature');
// const weatherDescriptionElement = document.querySelector('.weather-description');
// // 날씨 아이콘 매핑
// const weatherIconMap = {
//     '맑음': '<i class="uil uil-sun sun"></i>',
//     '비': '<i class="uil uil-cloud-drizzle rain"></i>',
//     '소나기': '<i class="uil uil-cloud-drizzle rain"></i>',
//     '눈': '<i class="uil uil-snowflake snow"></i>',
//     '비/눈': '<i class="uil uil-snowflake snow"></i>',
//     '알 수 없음': '알 수 없음'
// };
//
// // 날씨 아이콘 업데이트 함수
// function updateWeatherIcon() {
//     const weatherDescriptionElement = document.querySelector('.weather-description');
//     const currentWeather = weatherDescriptionElement.textContent.trim();
//
//     if (currentWeather in weatherIconMap) {
//         weatherDescriptionElement.innerHTML = weatherIconMap[currentWeather];
//     }
// }
//
// // DOM이 로드된 후 이 함수를 호출합니다
// document.addEventListener('DOMContentLoaded', () => {
//     // 날씨 데이터가 비동기적으로 가져와지고 업데이트된다고 가정합니다
//     // 날씨 데이터가 업데이트된 후 이 함수를 호출해야 할 수 있습니다
//     updateWeatherIcon();
// });
//
//
// // 서버에서 날씨 정보를 가져오는 함수
// function getWeatherForCurrentLocation(lat, lon) {
//     fetch(`/api/weather?lat=${lat}&lon=${lon}`)
//         .then(response => response.text())
//         .then(data => {
//             // 가져온 데이터를 파싱하여 화면에 출력
//             const weatherInfo = data.split(', ');
//             const temperature = weatherInfo[0].split(': ')[1];
//             const weatherCondition = weatherInfo[1].split(': ')[1];
//
//             temperatureElement.textContent = `${temperature}`;
//             weatherDescriptionElement.textContent = `${weatherCondition}`;
//
//             updateWeatherIcon();
//         })
//         .catch(error => {
//             alert('날씨 정보를 불러오는 중 오류가 발생했습니다.');
//         });
// }
//
// // 사용자 위치 정보를 가져오는 함수
// function getCurrentLocation() {
//     if (navigator.geolocation) {
//         navigator.geolocation.getCurrentPosition(position => {
//             const lat = position.coords.latitude;
//             const lon = position.coords.longitude;
//             getWeatherForCurrentLocation(lat, lon);
//         }, error => {
//             alert('위치 정보를 가져올 수 없습니다.');
//         });
//     } else {
//         alert('이 브라우저에서는 위치 정보가 지원되지 않습니다.');
//     }
// }
//
// // 페이지 로드 시 위치 정보를 가져옴
// window.onload = getCurrentLocation;

