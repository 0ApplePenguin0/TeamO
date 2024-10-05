/* ===================== 주소 검색 모달 ===================== */
function openPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = '';
            var extraAddr = '';

            if (data.userSelectedType === 'R') { // 도로명 주소 선택
                addr = data.roadAddress; // 도로명 주소를 가져옴
            } else { // 지번 주소 선택
                addr = data.jibunAddress; // 지번 주소를 가져옴
            }

            if (data.userSelectedType === 'R') {
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                document.getElementById("extraadress").value = extraAddr; // 올바른 ID로 수정
            } else {
                document.getElementById("extraadress").value = '';
            }
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('address').value = addr; // 기본 주소에 addr 할당

            document.getElementById("addressdetail").focus();
        }
    }).open();
}

// 폼 주소 결합
function combineAddress() {
    var postcode = document.getElementById('postcode').value;
    var address = document.getElementById('address').value;
    var addressDetail = document.getElementById('addressdetail').value;
    var fullAddress = postcode + ' ' + address + ' ' + addressDetail;

    document.getElementById('company_address').value = fullAddress;
}

// 폼이 제출될 때 combineAddress 함수를 호출하도록 설정
document.querySelector('#multiStepForm').addEventListener('submit', function(e) {
    combineAddress();
});

/* ===================== url 중복확인 ===================== */
let urlChecked = false;
let urlDuplicated = true;

$(document).ready(function() {
    // url 중복확인 버튼 클릭 이벤트
    $('#urlCheck').on("click", function() {
        let companyUrl = $('#companyUrl').val().trim();
        if (companyUrl === '') {
            alert("url을 입력해주세요.");
        } else if (companyUrl.length < 3 || companyUrl.length > 10) {
            alert("url은 3~10자로 입력하세요");
        } else {
            checkUrl(companyUrl);
        }
    });

    // ID 입력 필드 변경 시 체크 상태 초기화
    $('#companyUrl').on("input", function() {
        urlChecked = false;
        urlDuplicated = true;
    });
});

/*  url 중복확인 함수  */
function checkUrl(companyUrl) {
    $.ajax({
        url: '/register/urlCheck',
        type: 'post',
        data: {companyUrl : companyUrl},
        success: function(result) {
            urlChecked = true;   //중복 확인을 클릭했음
            if (result) {
                $('#urlMessage').text("사용 가능한 url입니다.").css("color", "green");
                urlDuplicated = false;
            } else {
                $('#urlMessage').text("중복된 아이디입니다.").css("color", "red");
                urlDuplicated = true;
            }
            openModal();
        },
        error: function() {
            alert('url 조회에 실패하였습니다.');
        }
    });
}

// 모달 열기
function openModal() {
    $('#urlCheckModal').css('display', 'block');
}

// 모달 닫기
function closeModal() {
    $('#urlCheckModal').css('display', 'none');
}

// 폼 제출 시 유효성 검사
function validateForm() {
    if (!urlChecked) {
        alert("URL 중복 확인을 해주세요.");
        return false;
    }
    if (urlDuplicated) {
        alert("이미 사용 중인 URL입니다. 다른 URL을 선택해주세요.");
        return false;
    }
    return true;
}

// 모달 외부 클릭 시 닫기
$(window).on('click', function(event) {
    if ($(event.target).is('#urlCheckModal')) {
        closeModal();
    }
});// url 중복체크 끝

/*===========================================*/
// 모든 단계 가져오기
const steps = document.querySelectorAll('.form-step');
const stepIndicators = document.querySelectorAll('.step');
let currentStep = 0;

// 다음 버튼 기능
document.querySelectorAll('.next-step').forEach(button => {
    button.addEventListener('click', () => {
        if (validateStep(currentStep)) {
            steps[currentStep].style.display = 'none';
            currentStep++;

            // 팀 입력 단계로 넘어갈 때 부서 정보 변경 확인
            if (currentStep === 2 && departmentModified) {
                updateDepartmentSelect();
                departmentModified = false;
            }

            steps[currentStep].style.display = 'block';
            updateStepIndicator();
        }
    });
});

// 이전 버튼 기능
document.querySelectorAll('.prev-step').forEach(button => {
    button.addEventListener('click', () => {
        steps[currentStep].style.display = 'none';
        currentStep--;
        steps[currentStep].style.display = 'block';
        updateStepIndicator();

        // 부서 정보 단계로 돌아갈 때 departmentModified 초기화
        if (currentStep === 1) {
            departmentModified = false;
        }
    });
});


// 단계 표시기 업데이트
function updateStepIndicator() {
    stepIndicators.forEach((indicator, index) => {
        if (index <= currentStep) {
            indicator.classList.add('active');
        } else {
            indicator.classList.remove('active');
        }
    });
}

// 각 단계 제약조건 체크
function validateStep(step) {
    switch(step) {
        case 0:
            // 회사 정보 제약조건 체크
            return validateCompanyInfo();
        case 1:
            // 부서 정보 제약조건 체크
            return validateDepartmentInfo();
        case 2:
            // 팀 정보 제약조건 체크 (항상 true 반환)
            return true;
        case 3:
            // 직급 정보 제약조건 체크
            return validatePositionInfo();
        default:
            return true;
    }
}

// 회사 정보 제약조건 체크
function validateCompanyInfo() {
    const companyName = document.getElementById('company_name').value.trim();
    const companyUrl = document.getElementById('companyUrl').value.trim();
    const address = document.getElementById('address').value.trim();
    const addressDetail = document.getElementById('addressdetail').value.trim();

    if (companyName === '') {
        alert('회사명을 입력해주세요.');
        return false;
    }
    if (companyUrl === '') {
        alert('회사 URL을 입력해주세요.');
        return false;
    }
    if (!urlChecked) {
        alert('URL 중복 확인을 해주세요.');
        return false;
    }
    if (urlDuplicated) {
        alert('이미 사용 중인 URL입니다. 다른 URL을 선택해주세요.');
        return false;
    }
    if (address === '') {
        alert('회사 주소를 입력해주세요.');
        return false;
    }
    return true;
}

// 부서 정보 제약조건 체크
function validateDepartmentInfo() {
    const departments = document.querySelectorAll('input[name^="department["]');
    const filledDepartments = Array.from(departments).filter(dept => dept.value.trim() !== '');

    if (filledDepartments.length === 0) {
        alert('최소 하나의 부서를 입력해주세요.');
        return false;
    }
    return true;
}

// 직급 정보 제약조건 체크
function validatePositionInfo() {
    const positions = document.querySelectorAll('#positionInputs input[type="text"]');
    if (positions.length === 0) {
        alert('최소 하나의 직급을 입력해주세요.');
        return false;
    }
    for (let pos of positions) {
        if (pos.value.trim() === '') {
            alert('모든 직급명을 입력해주세요.');
            return false;
        }
    }
    return true
}

/* =============== 부서 입력 =============== */
let departmentCount = 1;
let departmentModified = false;

function toggleDepartmentInput(button) {
    const departmentList = button.closest('.departmentList');
    if (button.textContent === '+') {
        // 새 부서 입력 추가
        const newDepartment = document.createElement('div');
        newDepartment.className = 'departmentList';
        newDepartment.innerHTML = `
            <input type="text" name="department[${departmentCount}][name]" placeholder="부서명" oninput="updateDepartmentSelect()">
            <button type="button" class="department-toggle-btn department-add-btn" onclick="toggleDepartmentInput(this)">+</button>
        `;
        departmentList.parentNode.insertBefore(newDepartment, departmentList.nextSibling);
        button.textContent = '-';
        button.classList.remove('department-add-btn');
        button.classList.add('remove-btn');
        departmentCount++;
    } else {
        // 부서 입력 제거
        departmentList.remove();
    }
    departmentModified = true;
    updateDepartmentSelect();
}

// 전역 변수로 선택된 부서를 추적
let selectedDepartments = new Set();

// 부서 셀렉트 옵션
function updateDepartmentSelect() {
    const departmentSelects = document.querySelectorAll('#departmentSelects select');
    const departments = Array.from(document.querySelectorAll('input[name^="department["]'))
        .map(input => input.value)
        .filter(value => value.trim() !== '');

    departmentSelects.forEach(select => {
        const currentValue = select.value;
        select.innerHTML = '<option value="">부서 선택</option>';
        departments.forEach((dept, index) => {
            const option = document.createElement('option');
            option.value = dept;
            option.textContent = dept;
            select.appendChild(option);
        });

        if (!departments.includes(currentValue)) {
            select.value = "";
            const container = select.closest('.select-group').nextElementSibling;
            if (container && container.classList.contains('department-container')) {
                container.remove();
            }
        } else {
            select.value = currentValue;
        }
    });

    // 존재하지 않는 부서에 대한 팀 입력 컨테이너 제거
    const teamContainers = document.querySelectorAll('.department-container');
    teamContainers.forEach(container => {
        const departmentName = container.querySelector('h3').textContent;
        if (!departments.includes(departmentName)) {
            container.remove();
        }
    });

    departmentModified = true;
}

/* =============== 팀 입력 =============== */
// 부서 선택
function addDepartmentSelect() {
    const departmentSelects = document.getElementById('departmentSelects');
    const newSelect = document.createElement('div');
    newSelect.className = 'select-group';
    newSelect.innerHTML = `
        <select onchange="showTeamInputs(this)">
            <option value="">부서 선택</option>
        </select>
        <button type="button" class="add-btn" onclick="addDepartmentSelect()">+</button>
    `;
    departmentSelects.appendChild(newSelect);

    updateDepartmentSelect();
    updateAddRemoveButtons();
}

// 하위 부서(팀) 입력 창 보여주기
function showTeamInputs(select) {
    const selectedDepartment = select.value;
    const container = select.closest('.select-group').nextElementSibling;

    if (container && container.classList.contains('department-container')) {
        container.remove(); // 기존 컨테이너 제거
    }

    if (selectedDepartment) {
        const newContainer = document.createElement('div');
        newContainer.className = 'department-container';
        newContainer.innerHTML = `
            <h3>${selectedDepartment}</h3>
            <div class="input-group">
                <input type="text" placeholder="팀명" required>
                <button type="button" class="add-btn" onclick="addTeamInput(this)">+</button>
            </div>
        `;
        select.closest('.select-group').after(newContainer);
    }
}

// 하위 부서(팀) 입력
function addTeamInput(button) {
    const container = button.closest('.department-container');
    if (!container) return;

    const newInput = document.createElement('div');
    newInput.className = 'input-group';
    newInput.innerHTML = `
        <input type="text" placeholder="팀명">
        <button type="button" class="add-btn" onclick="addTeamInput(this)">+</button>
    `;
    container.appendChild(newInput);

    // 현재 버튼을 '-' 버튼으로 변경
    button.textContent = '-';
    button.onclick = function() { removeTeamInput(this); };
    button.classList.remove('add-btn');
    button.classList.add('remove-btn');
}

function removeTeamInput(button) {
    const inputGroup = button.closest('.input-group');
    if (inputGroup) {
        inputGroup.remove();
    }
    updateLastTeamInputButton(button.closest('.department-container'));
}

function updateLastTeamInputButton(container) {
    if (!container) return;

    const inputGroups = container.querySelectorAll('.input-group');
    const lastInputGroup = inputGroups[inputGroups.length - 1];

    if (lastInputGroup) {
        const button = lastInputGroup.querySelector('button');
        button.textContent = '+';
        button.onclick = function() { addTeamInput(this); };
        button.classList.remove('remove-btn');
        button.classList.add('add-btn');
    }
}


function updateAddRemoveButtons() {
    const selectGroups = document.querySelectorAll('#departmentSelects .select-group');
    selectGroups.forEach((group, index) => {
        const button = group.querySelector('button');
        if (index === selectGroups.length - 1) {
            button.textContent = '+';
            button.onclick = addDepartmentSelect;
            button.classList.remove('remove-btn');
            button.classList.add('add-btn');
        } else {
            button.textContent = '-';
            button.onclick = () => removeDepartmentSelect(group);
            button.classList.remove('add-btn');
            button.classList.add('remove-btn');
        }
    });
}

function removeDepartmentSelect(group) {
    const container = group.nextElementSibling;
    if (container && container.classList.contains('department-container')) {
        container.remove();
    }
    group.remove();
    updateAddRemoveButtons();
}

// 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function() {
    updateDepartmentSelect();
    updateAddRemoveButtons();

    // 부서 입력 필드에 이벤트 리스너 추가
    document.getElementById('departmentInputs').addEventListener('input', function(event) {
        if (event.target.name && event.target.name.startsWith('department[')) {
            departmentModified = true;
        }
    });
});

// 직급을 위한 동적 입력 필드
let positionCount = 1;

function togglePositionInput(button) {
    const positionList = button.closest('.positionList');
    if (button.textContent === '+') {
        // 새 직급 입력 추가
        const newPosition = document.createElement('div');
        newPosition.className = 'positionList';
        newPosition.innerHTML = `
            <input type="text" name="position[${positionCount}]" placeholder="직급">
            <button type="button" class="position-toggle-btn position-add-btn" onclick="togglePositionInput(this)">+</button>
        `;
        positionList.parentNode.insertBefore(newPosition, positionList.nextSibling);
        button.textContent = '-';
        button.classList.remove('position-add-btn');
        button.classList.add('remove-btn');
        positionCount++;
    } else {
        // 직급 입력 제거
        positionList.remove();
    }
}

// 폼 초기화
updateStepIndicator();