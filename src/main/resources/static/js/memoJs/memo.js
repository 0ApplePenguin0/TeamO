document.addEventListener('DOMContentLoaded', function() {
    // 요소 선택
    const compose = document.getElementById('compose-btn');
    const memoModal = document.getElementById('memo-modal');
    const memoList = document.querySelector('.memo-grid');
    const composeModal = document.getElementById('compose-modal');
    const composeForm = document.getElementById('addMemo');
    const deleteBtn = document.getElementById('delete-memo');
    const editBtn = document.getElementById('edit-memo');

    let currentMemoId = null;  // 현재 메모 ID 저장

    //<!-- =============== 메모 읽기 모달 =============== -->
    // 메모 클릭 시 모달 오픈
    memoList.addEventListener('click', function(e) {
        if (e.target.classList.contains('memo-checkbox')) {
            return;
        }

        if (e.target.closest('.memo-item')) {
            const item = e.target.closest('.memo-item');
            showMemo(item);
        }
    });

    function showMemo(item) {
    const memoIdText = item.querySelector('#memoId').textContent;  // memoIdText로 변수 선언
    const memoId = memoIdText.replace('#', '').trim();  // memoIdText를 기반으로 memoId 값 생성
    const memoContent = item.querySelector('#memoContent').textContent;
    const memoDate = item.querySelector('#memoDate').textContent;

    currentMemoId = memoId;  // currentMemoId에 올바른 값 할당

    // 선택된 memoId와 content를 확인 (임시)
    console.log("Original memoId:", memoIdText);
    console.log("Transformed memoId:", memoId);
    console.log("Content:", memoContent);

    document.getElementById('modal-subject').textContent = memoIdText;
    document.getElementById('modal-content').textContent = memoContent;
    document.getElementById('modal-date').textContent = memoDate;

    memoModal.style.display = 'block';
}


    // 모달 닫기 버튼 이벤트
    const closeBtns = document.getElementsByClassName('close');

    Array.from(closeBtns).forEach(btn => {
        btn.addEventListener('click', function() {
            memoModal.style.display = 'none';
            composeModal.style.display = 'none';
        });
    });

    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(e) {
        if (e.target == memoModal) {
            memoModal.style.display = 'none';
        }
        if (e.target == composeModal) {
            composeModal.style.display = 'none';
        }
    });

    //<!-- =============== 메모 작성 모달 =============== -->
    //메모 작성 버튼 처리
    compose.addEventListener('click', function() {
        composeModal.style.display = 'block';
    });

  // 메모 작성 폼 제출
  composeForm.addEventListener('submit', function(e) {
      e.preventDefault();

       // 폼에서 값 수집
          let memoBodyElement = document.getElementById("memo-body");
          let content = memoBodyElement.value.trim();


      if (!content) {
          alert('메모 내용을 입력해주세요.');
          return;
      }

      // JSON 데이터 생성
      let jsonData = {
          content: content
      };

      $.ajax({
          url: '/memo/addMemo',
          type: 'post',
          contentType: "application/json",
          data: JSON.stringify(jsonData),
          success: function (response) {
           // 만약 'redirect:list'라는 응답을 받으면 페이지를 새로고침
              if (response === "redirect:list") {
                  alert('메모가 저장되었습니다.');
                  location.reload();  // 페이지를 새로고침하여 메모 목록 갱신
              }
          },
          error: function (error) {
              alert('메모 저장 실패하였습니다.');
          }
      });

      // 모달 닫고 폼 초기화
      composeModal.style.display = 'none';
      composeForm.reset();
  });


 // 삭제 버튼 클릭 이벤트
    deleteBtn.addEventListener('click', function() {
        if (confirm('정말로 삭제하시겠습니까?')) {
            if (currentMemoId) {
                $.ajax({
                    url: `/memo/deleteMemo/${currentMemoId}`,  // 해당 메모 ID로 삭제 요청
                    type: 'DELETE',
                    success: function(response) {
                        alert('메모가 삭제되었습니다.');
                        memoModal.style.display = 'none';
                        location.reload();  // 페이지 새로고침으로 삭제된 메모 반영
                    },
                    error: function(error) {
                        alert('메모 삭제 실패하였습니다.');
                    }
                });
            } else {
                alert('삭제할 메모를 선택해주세요.');
            }
        }
    });

    // 전체 메모 삭제 버튼 클릭 시
    document.getElementById('delete-btn').addEventListener('click', function() {
        var selectedMemos = [];

        // 체크된 메모들의 memoId를 배열에 저장
        document.querySelectorAll('.memo-checkbox:checked').forEach(function(checkbox) {
            selectedMemos.push(checkbox.value);
        });

        // 삭제할 메모가 선택되었는지 확인
        if (selectedMemos.length > 0) {
            if (confirm('선택된 메모를 삭제하시겠습니까?')) {
                // 서버로 삭제 요청 보내기
                $.ajax({
                    url: '/memo/deleteMemos',
                    type: 'DELETE',
                    data: JSON.stringify(selectedMemos), // 배열을 JSON으로 변환하여 전송
                    contentType: 'application/json; charset=utf-8',
                    success: function(response) {
                        alert('선택된 메모가 삭제되었습니다.');
                        location.reload(); // 삭제 후 페이지 새로고침
                    },
                    error: function(error) {
                        alert('메모 삭제 실패하였습니다.');
                    }
                });
            }
        } else {
            alert('삭제할 메모를 선택해주세요.');
        }
    });



    // 수정 버튼 클릭 이벤트
    editBtn.addEventListener('click', function() {
        const memoContent = prompt("새로운 내용을 입력하세요:", document.getElementById('modal-content').textContent);

    if (memoContent) {
        let jsonData = {
            memoId: currentMemoId,  // 수정할 메모의 ID
            content: memoContent   // 메모의 새로운 내용
        };

        console.log("수정 중인 메모 ID:", currentMemoId);
        console.log("수정된 내용:", memoContent);

        $.ajax({
            url: '/memo/update',  // 메모 수정 처리할 서버 경로
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(jsonData),  // JSON 데이터로 전송
            success: function(response) {
                alert('메모가 성공적으로 수정되었습니다.');
                memoModal.style.display = 'none';
                location.reload();  // 페이지를 새로고침하여 수정 사항 반영
            },
            error: function(error) {
                alert('메모 수정에 실패했습니다.');
            }
        });
    }
});





});