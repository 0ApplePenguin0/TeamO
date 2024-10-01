/*====== 회사 생성 모달 ======*/
const createBtn = document.querySelector('.btn-create');
const createModal = document.getElementById('createModal');
const cancelCreate = document.getElementById('cancelCreate');
const nextCreate = document.getElementById('nextCreate');
const companyNameInput = document.getElementById('companyNameInput');
const companyUrl = document.getElementById('companyUrl');
const errorMessage = document.getElementById('errorMessage');

createBtn.addEventListener('click', () => {
    createModal.style.display = 'block';
});

cancelCreate.addEventListener('click', () => {
    createModal.style.display = 'none';
    resetForm();
});

companyNameInput.addEventListener('input', () => {
    const companyName = companyNameInput.value.toLowerCase().replace(/[^a-z0-9]/g, '');
    companyUrl.textContent = `www.${companyName}.timo.com`;
    checkDuplication(companyName);
});

nextCreate.addEventListener('click', () => {
    // 여기에 다음 페이지로 이동하는 로직을 추가하세요
    console.log('다음 페이지로 이동');
});

function checkDuplication(companyName) {
    // 실제로는 서버에 중복 확인 요청을 보내야 합니다.
    // 이 예제에서는 간단히 'abc'와 'xyz'가 이미 사용 중이라고 가정합니다.
    const usedNames = ['abc', 'xyz'];

    if (usedNames.includes(companyName)) {
        errorMessage.textContent = "중복된 주소입니다.";
        nextCreate.disabled = true;
    } else {
        errorMessage.textContent = "";
        nextCreate.disabled = false;
    }
}

function resetForm() {
    companyNameInput.value = '';
    companyUrl.textContent = '';
    errorMessage.textContent = '';
    document.getElementById('companyFullName').value = '';
    document.getElementById('companyAddress').value = '';
}

