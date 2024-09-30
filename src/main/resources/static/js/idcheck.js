$(document).ready(function() {
    // 중복확인 버튼 클릭 시 모달 창 열림
    $('#idCheck').on("click",checkId);

    // 이메일 중복 확인
    $('#emailCheck').on("click", checkEmail);
});

// ID 중복확인 모달
function checkId() {
    let userId = document.getElementById('userId').value;
    let modal = document.getElementById('idCheckModal');
    let modalMessage = document.getElementById('modalMessage');

    // ID 중복 확인을 위한 ajax
    $.ajax({
        url: '/member/idCheck',
        type: 'post',
        data: {searchId : userId},
        success: function(result) {
            if (result) {
                modalMessage.textContent = "사용 가능한 아이디입니다.";
                modalMessage.style.color = "green";
                isIdChecked = true;
            } else {
                modalMessage.textContent = "중복된 아이디입니다.";
                modalMessage.style.color = "red";
                isIdChecked = false;
            }
             modal.style.display = "block"; // 모달 창 열기
        },
        error: function() {
            alert('Id 조회 실패');}
    }); //ajax
}

// 이메일 중복 확인 함수
function checkEmail() {
    let userEmail = document.getElementById('email').value;
    let modal = document.getElementById('emailCheckModal');
    let modalMessage = document.getElementById('emailModalMessage');

    $.ajax({
        url: '/member/emailCheck',
        type: 'post',
        data: { searchEmail: userEmail },
        success: function(result) {
            if (result) {
                modalMessage.textContent = "사용 가능한 이메일입니다.";
                modalMessage.style.color = "green";
                isEmailChecked = true; // 이메일이 사용 가능하면 체크 상태 변경
            } else {
                modalMessage.textContent = "중복된 이메일입니다.";
                modalMessage.style.color = "red";
                isEmailChecked = false; // 이메일이 중복되면 체크 상태 변경
            }
            modal.style.display = "block"; // 모달 창 열기
        },
        error: function() {
            alert('이메일 중복 확인 요청에 실패했습니다.');
        }
    });
}

// 모달 창 끄기
function closeModal() {
    document.getElementById('idCheckModal').style.display = "none";
}

function closeEmailModal() {
    document.getElementById('emailCheckModal').style.display = "none";
}

// 비밀번호 재확인
function checkPasswordMatch() {
    let password = document.getElementById('password').value;
    let confirmPassword = document.getElementById('confirmPassword').value;
    let errorDiv = document.getElementById('passwordError');

    if (password !== confirmPassword) {
        errorDiv.style.display = "block";
    } else {
        errorDiv.style.display = "none";
    }
}

// 비밀번호 재확인
function validateForm() {
    let password = document.getElementById('password').value;
    let confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }
    return true;
}

// 모달 창의 바깥을 클릭했을 시, 모달창이 꺼지는 로직
window.onclick = function(event) {
    let modal = document.getElementById('idCheckModal');
    if (event.target == modal) {
        modal.style.display = "none";
    }
}


