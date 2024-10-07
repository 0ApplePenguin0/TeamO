function validateForm() {
    var companyName = document.getElementById('company_name').value;
    var postcode = document.getElementById('postcode').value;
    var address = document.getElementById('address').value;
    var addressdetail = document.getElementById('addressdetail').value;
    var companyUrl = document.getElementById('companyUrl').value;
    var companyAddress = document.getElementById('company_address').value;

    // 필수 입력값을 검사
    if (!companyName) {
        alert('회사 이름을 입력해주세요.');
        return false; // 제출 중지
    }
    if (!postcode) {
        alert('우편번호를 입력해주세요.');
        return false; // 제출 중지
    }
    if (!address) {
        alert('기본 주소를 입력해주세요.');
        return false; // 제출 중지
    }
    if (!addressdetail) {
        alert('상세 주소를 입력해주세요.');
        return false; // 제출 중지
    }
    if (!companyUrl) {
        alert('회사 URL을 입력해주세요.');
        return false; // 제출 중지
    }

    // 부서와 직급 검증
    var departmentList = document.getElementById('departmentList').children.length;
    if (departmentList === 0) {
        alert('적어도 하나의 부서를 추가해주세요.');
        return false;
    }

    var positionList = document.getElementById('positionList').children.length;
    if (positionList === 0) {
        alert('적어도 하나의 직급을 추가해주세요.');
        return false;
    }

    if (!companyAddress) {
        alert('주소를 저장해주세요.');
        return false;
    }

    return true; // 검증 통과
}

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


// 부서 추가
function addDepartment() {
    var departmentList = document.getElementById('departmentList');
    var DepartmentId = departmentList.children.length + 1;

    var departmentDiv = document.createElement('div');
    departmentDiv.setAttribute('class', 'department-item');
    departmentDiv.setAttribute('id', `department-${DepartmentId}`);
    departmentDiv.innerHTML = `
                <label>부서 ${DepartmentId}:</label>
                <input type="text" name="department[${DepartmentId}][name]" placeholder="부서명">
                <button type="button" onclick="addTeam(${DepartmentId})">하위부서 추가</button>
                <button type="button" onclick="removeDepartment(${DepartmentId})">부서 삭제</button>
                <div id="TeamList-${DepartmentId}"></div>
                <br>
            `;
    departmentList.appendChild(departmentDiv);
}

function removeDepartment(DepartmentId) {
    var departmentDiv = document.getElementById(`department-${DepartmentId}`);
    departmentDiv.remove(); // 부서 삭제
}

// 하위부서 추가
function addTeam(DepartmentId) {
    var TeamList = document.getElementById(`TeamList-${DepartmentId}`);
    var teamNum = TeamList.children.length + 1; // 하위부서 번호

    var teamDiv = document.createElement('div');
    teamDiv.setAttribute('class', 'team-item');
    teamDiv.setAttribute('id', `team-${DepartmentId}-${teamNum}`);
    teamDiv.innerHTML = `
<label>하위부서 ${teamNum}:</label>
        <input type="text" name="department[${DepartmentId}][teams][${teamNum}][name]" placeholder="하위부서명">
        <button type="button" onclick="removeTeam(${DepartmentId}, ${teamNum})">하위부서 삭제</button>
        <br>
        `;
    TeamList.appendChild(teamDiv);
}

function removeTeam(DepartmentId, teamNum) {
    var teamDiv = document.getElementById(`team-${DepartmentId}-${teamNum}`);
    teamDiv.remove(); // 하위부서 삭제
}

// 직급 추가
function addPosition() {
    var positionList = document.getElementById('positionList');
    var positionNum = positionList.children.length + 1;

    var positionDiv = document.createElement('div');
    positionDiv.innerHTML = `
            <label>직급 ${positionNum}:</label>
            <input type="text" name="positions[${positionNum}][name]" placeholder="직급명">
            <br>
        `;
    positionList.appendChild(positionDiv);
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

