const logoutBtn = document.querySelector('.logout-btn');
const modal = document.getElementById('logoutModal');
const confirmBtn = document.getElementById('confirmLogout');
const cancelBtn = document.getElementById('cancelLogout');

// 로그아웃 모달 
logoutBtn.addEventListener('click', () => {
    modal.style.display = 'block';
});

// 로그아웃 확인 -> 로그아웃 처리
confirmBtn.addEventListener('click', () => {
    window.location.href="/logout";
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