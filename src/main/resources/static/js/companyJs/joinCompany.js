/*====== 회사 참여 모달 ======*/
const joinBtn = document.querySelector('.btn-join');
const joinModal = document.getElementById('joinModal');
const cancelJoin = document.getElementById('cancelJoin');
const nextJoin = document.getElementById('nextJoin');
const joinCompanyNameInput = document.getElementById('joinCompanyNameInput');
const joinCompanyUrl = document.getElementById('joinCompanyUrl');
const joinErrorMessage = document.getElementById('joinErrorMessage');

joinBtn.addEventListener('click', () => {
    joinModal.style.display = 'block';
});

cancelJoin.addEventListener('click', () => {
    joinModal.style.display = 'none';
    resetJoinForm();
});

joinCompanyNameInput.addEventListener('input', () => {
    const companyName = joinCompanyNameInput.value.toLowerCase().replace(/[^a-z0-9]/g, '');
    joinCompanyUrl.textContent = `www.${companyName}.timo.com`;
    checkJoinAvailability(companyName);
});

nextJoin.addEventListener('click', () => {
    // 여기에 다음 페이지로 이동하는 로직을 추가하세요
    console.log('참여 - 다음 페이지로 이동');
});

function checkJoinAvailability(companyName) {
    // 실제로는 서버에 존재 여부 확인 요청을 보내야 합니다.
    // 이 예제에서는 간단히 'abc'와 'xyz'만 존재한다고 가정합니다.
    const existingCompanies = ['abc', 'xyz'];

    if (existingCompanies.includes(companyName)) {
        joinErrorMessage.textContent = "";
        nextJoin.disabled = false;
    } else {
        joinErrorMessage.textContent = "존재하지 않는 주소입니다.";
        nextJoin.disabled = true;
    }
}

function resetJoinForm() {
    joinCompanyNameInput.value = '';
    joinCompanyUrl.textContent = '';
    joinErrorMessage.textContent = '';
}