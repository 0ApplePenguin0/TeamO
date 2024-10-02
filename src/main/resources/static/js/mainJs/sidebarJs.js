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

    const menuItems = document.querySelectorAll('.menu-items .nav-links > li > a');

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