$(document).ready(function() {
    // 중복확인 버튼 클릭 시 모달 창 열림
    $('#idCheck').on("click", function() {
        let userId = $('#userId').val().trim();
        if (userId === '') {
            alert("아이디를 입력해주세요.");
        } else if (userId.length < 3 || userId.length > 10) {
            alert("ID는 3~10자로 입력하세요");
        } else {
            checkId();
        }
    });

    $('#emailCheck').on("click", function() {
        if ($('#email').val().trim() === '') {
            alert("이메일을 입력해주세요.");
        } else {
            checkEmail();
        }
    });

    // ID 입력 필드 변경 시 체크 상태 초기화
    $('#userId').on("input", function() {
        idChecked = false;
        idDuplicated = true;
    });

    // 이메일 입력 필드 변경 시 체크 상태 초기화
    $('#email').on("input", function() {
        emailChecked = false;
        emailDuplicated = true;
    });
});

// ID 중복확인 모달
function checkId() {
    let userId = $('#userId').val();
    let modal = $('#idCheckModal');
    let idMessage = $('#idMessage');

    // ID 중복 확인을 위한 ajax
    $.ajax({
        url: '/member/idCheck',
        type: 'post',
        data: {searchId : userId},
        success: function(result) {
            idChecked = true;   //중복 확인을 클릭했음
            if (result) {
                idMessage.text("사용 가능한 아이디입니다.").css("color", "green");
                idDuplicated = false;
            } else {
                idMessage.text("중복된 아이디입니다.").css("color", "red");
                idDuplicated = true;
            }
            modal.show();
        },
        error: function() {
            alert('ID 조회에 실패하였습니다.');
        }
    });
}

// email 중복확인 모달
function checkEmail() {
    let email = $('#email').val();
    let modal = $('#emailCheckModal');
    let emailMessage = $('#emailMessage');

    // email 중복 확인을 위한 ajax
    $.ajax({
        url: '/member/emailCheck',
        type: 'post',
        data: {searchEmail: email},
        success: function(result) {
            emailChecked = true;   //중복 확인을 클릭했음
            if (result) {
                emailMessage.text("사용 가능한 이메일입니다.").css("color", "green");
                emailDuplicated = false;
            } else {
                emailMessage.text("중복된 이메일입니다.").css("color", "red");
                emailDuplicated = true;
            }
            modal.show();
        },
        error: function() {
            alert('이메일 조회에 실패하였습니다.');
        }
    });
}

// 이하 코드는 이전과 동일...

// 모달 창 끄기
function closeModal() {
    document.getElementById('idCheckModal').style.display = "none";
    document.getElementById('emailCheckModal').style.display = "none";
}

// 비밀번호 재확인
function checkPasswordMatch() {
    let password = $('#password').val();
    let confirmPassword = $('#confirmPassword').val();
    let errorDiv = $('#passwordError');

    if (password !== confirmPassword) {
        errorDiv.show();
    } else {
        errorDiv.hide();
    }
}

// 폼 제출 전 최종 확인
function validateForm() {
    let password = $('#password').val();
    let confirmPassword = $('#confirmPassword').val();

    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }
    return true;
}

// 모달 창의 바깥을 클릭했을 시, 모달창이 꺼지는 로직
$(window).on('click', function(event) {
    if ($(event.target).is('#idCheckModal, #emailCheckModal')) {
        $('#idCheckModal, #emailCheckModal').hide();
    }
});