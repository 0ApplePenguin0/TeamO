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
function combineAddress() {
    var address = document.getElementById('address').value;

    if (!address) {
        alert("주소 저장에 실패했습니다. 필수 항목을 모두 입력해주세요.");
        return; // 오류 발생 시 함수 종료
    }

    // 주소를 하나로 통합
    var fullAddress = `${address}`;

    // 통합된 주소를 'address' 필드에 설정
    document.getElementById('company_address').value = fullAddress;

    alert("주소가 저장되었습니다!");
}

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

// 각 단계 유효성 검사
function validateStep(step) {
    // 각 단계에 대한 유효성 검사 로직 추가
    switch(step) {
        case 0:
            // 회사 정보 유효성 검사
            return validateCompanyInfo();
        case 1:
            // 부서 정보 유효성 검사
            return validateDepartmentInfo();
        case 2:
            // 팀 정보 유효성 검사
            return validateTeamInfo();
        case 3:
            // 직급 정보 유효성 검사
            return validatePositionInfo();
        default:
            return true;
    }
}

// 각 단계에 대한 유효성 검사 함수 구현
function validateCompanyInfo() {
    // 여기에 유효성 검사 로직 추가
    return true;
}

function validateDepartmentInfo() {
    // 여기에 유효성 검사 로직 추가
    return true;
}

function validateTeamInfo() {
    // 여기에 유효성 검사 로직 추가
    return true;
}

function validatePositionInfo() {
    // 여기에 유효성 검사 로직 추가
    return true;
}

// 부서를 위한 동적 입력 필드
let departmentCount = 1;

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
    updateDepartmentSelect();
}

// 부서 선택 옵션 업데이트
function updateDepartmentSelect() {
    const departmentSelect = document.getElementById('departmentSelect');
    departmentSelect.innerHTML = '<option value="">부서 선택</option>';
    document.querySelectorAll('input[name^="department["]').forEach((input, index) => {
        if (input.value) {
            const option = document.createElement('option');
            option.value = index;
            option.textContent = input.value;
            departmentSelect.appendChild(option);
        }
    });
}

// 팀을 위한 동적 입력 필드
function addDepartmentSelect() {
    const departmentSelects = document.getElementById('departmentSelects');
    const newSelect = document.createElement('div');
    newSelect.className = 'select-group';
    newSelect.innerHTML = `
        <select onchange="showTeamInputs(this)">
            <option value="">부서 선택</option>
            ${departments.map(dept => `<option value="${dept}">${dept}</option>`).join('')}
        </select>
        <button type="button" class="add-btn" onclick="addDepartmentSelect()">+</button>
    `;
    departmentSelects.appendChild(newSelect);

    // 이전 선택 그룹의 '+' 버튼 제거
    const selectGroups = departmentSelects.getElementsByClassName('select-group');
    if (selectGroups.length > 1) {
        const previousGroup = selectGroups[selectGroups.length - 2];
        previousGroup.removeChild(previousGroup.lastElementChild);
    }
}

function showTeamInputs(select) {
    const departmentContainer = select.closest('.select-group').nextElementSibling;
    if (departmentContainer && departmentContainer.classList.contains('department-container')) {
        departmentContainer.remove();
    }

    if (select.value) {
        const selectedDepartment = select.options[select.selectedIndex].text;
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

function addTeamInput(button) {
    const container = button.closest('.department-container');
    const newInput = document.createElement('div');
    newInput.className = 'input-group';
    newInput.innerHTML = `
        <input type="text" placeholder="팀명" required>
        <button type="button" class="add-btn" onclick="addTeamInput(this)">+</button>
    `;
    container.appendChild(newInput);

    // 이전 입력 그룹의 '+' 버튼 제거
    const inputGroups = container.getElementsByClassName('input-group');
    if (inputGroups.length > 1) {
        const previousGroup = inputGroups[inputGroups.length - 2];
        previousGroup.removeChild(previousGroup.lastElementChild);
    }
}

document.getElementById('teamForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const teamData = {};
    const departmentContainers = document.getElementsByClassName('department-container');
    Array.from(departmentContainers).forEach(container => {
        const department = container.querySelector('h3').textContent;
        const teams = Array.from(container.querySelectorAll('input')).map(input => input.value);
        teamData[department] = teams;
    });
    console.log('입력된 팀 데이터:', teamData);
    // 여기에 다음 페이지로 이동하는 로직을 추가하세요
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