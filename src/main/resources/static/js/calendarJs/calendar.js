// 이벤트 발생 시 캘린더 초기화
document.addEventListener('DOMContentLoaded', function() {
    let calendarEl = document.getElementById('calendar');            // 캘린더가 표시될 HTML 요소
    let eventModal = document.getElementById('eventModal');          // 일정 확인, 추가 모달 창
	let addEventModal = document.getElementById('addEventModal');  // 새 일정 추가 모달 창
	let modalDate = document.getElementById('modalDate');  // 모달 창에 표시될 클릭한 날짜 정보
	let modalContent = document.getElementById('modalContent');  // 모달 창에 표시될 일정 내용
	let addEventBtn = document.getElementById('addEventBtn');  // 날짜 클릭 시 보이는 모달의 "추가" 버튼 (일정 추가 모달을 열기 위한 버튼)
	let closeModalBtn = document.getElementById('closeModalBtn');  // 날짜 클릭 시 보이는 모달 창 닫기 버튼
	let saveEventBtn = document.getElementById('saveEventBtn');  // 일정 추가 모달에서 "등록하기" 버튼
	let cancelAddEventBtn = document.getElementById('cancelAddEventBtn');  // 일정 추가 모달에서 "취소하기" 버튼
    let selectedDate = '';  // 사용자가 선택한 날짜 저장

	// FullCalendar 초기화
    let calendar = new FullCalendar.Calendar(calendarEl, {
		headerToolbar: {
			left: 'prev,next today',  // 이전, 다음, 오늘 버튼
			center: 'title',  // 제목 (현재 달력의 월 또는 주)
			right: 'dayGridMonth,listMonth'  // 월간 뷰와 리스트 뷰만 표시
		},
        initialView: 'dayGridMonth',        // 초기 캘린더 뷰를 월간(dayGridMonth)으로 설정
		dayMaxEvents: 2,  // 하루에 표시할 최대 이벤트 수 (숫자로 설정)
		dayMaxEventRows: true,  // 여러 줄의 이벤트 표시를 가능하게 설정
        selectable: true,                   // 사용자가 날짜를 선택할 수 있도록 설정 (일정 추가를 위해 사용)
		eventOrder: "-allDay, start, title",  // 일정 우선 순위(allDay > start날짜 > title ) 설정
		
		// 서버(DB)에서 데이터 가져오는 부분 (Ajax 요청)
        events: function(fetchInfo, successCallback, failureCallback) {
		// 일정데이터를 가져오는 Ajax 요청
		fetch("http://localhost:8888/api/schedule/events")  // API 엔드포인트 (서버(DB)에 있는 데이터 불러오기)
			.then(response => response.json())  // 서버 응답을 JSON 형식으로 변환
			.then(data => {
				let events = data.map(function(event) {	// FullCalendar가 이해할 수 있는 형식으로 데이터 변환
					let originalEndDate = event.endDate;  // 실제 종료일 (로직 처리에 사용)
					let displayEndDate = originalEndDate; // 띠 전용 종료일 + 1 일수

					// allDay일 경우 종료일에 하루를 추가 (달력에만 반영)
					if (event.isAllDay && displayEndDate) {
						let displayEndObj = new Date(displayEndDate);
						displayEndObj.setDate(displayEndObj.getDate() + 1);  // 종료일에 하루 추가
						displayEndDate = displayEndObj.toISOString().split('T')[0];  // 날짜만 추출
					}

					return {
		 				id: event.scheduleId, // 일정 ID
		 				title: event.title,	// 일정 제목
		 				start: event.startDate.split('T')[0],  // DB의 start_date 필드에서 날짜만 사용
		 				end: displayEndDate ? displayEndDate : null,  // 달력에만 반영된 종료일
		 				allDay: event.isAllDay,    // allDay 이벤트 여부 확인
		// 				backgroundColor: event.group.groupColor,  // 그룹 색상으로 배경(띠) 색 설정
		// 				borderColor: event.group.groupColor,  // 그룹 색상으로 동그라미 색 설정
		 				textColor: 'white',  // 텍스트 색상
		 				extendedProps: {
		 					scheduleId: event.scheduleId,  // 일정 ID
		 					originalEnd: originalEndDate,  // 실제 종료일 저장
		 					description: event.description  // 일정 설명
//		 					categoryNum: event.categoryNum  // 구분에 따른 ID	//해제시 위에 , 붙여야함
		 				}
		 			};
		 		});
		 		successCallback(events);	// 캘린더에 이벤트를 전달하여 렌더링
		 	})
		 	.catch(error => {
		 		console.error("Error fetching events:", error);
		 		failureCallback(error);
		 	});
		 },
		
		// 사용자가 특정 날짜를 클릭했을 때 실행되는 함수
		dateClick: function(info) {
		    selectedDate = info.date;	// 클릭한 날짜를 저장
		    let options = { weekday: 'short', year: 'numeric', month: 'long', day: 'numeric' };	// 날짜 포맷 설정 (요일은 축약형)
			modalDate.textContent = selectedDate.toLocaleDateString('en-US', options); // 영어로 날짜와 요일(축약) 표시
			
			// 해당 날짜에 속한 이벤트 필터링
			let eventsOnDate = calendar.getEvents().filter(function(event) {
				let eventStartDate = new Date(event.start);  // 이벤트의 시작 날짜
				let eventEndDate = new Date(event.end || event.start);  // 이벤트의 종료 날짜 (없으면 시작 날짜)
				
				// allDay 이고 시작일수와 종료일수가 다를 경우 종료일에서 하루를 빼줌
				if(event.allDay && eventStartDate.toISOString().split('T')[0] !== eventEndDate.toISOString().split('T')[0]) {
					eventEndDate.setDate(eventEndDate.getDate() - 1);
				}

				// 날짜 부분만 비교하기 위해 날짜 정보를 00:00:00 으로 통일
				eventStartDate.setHours(0, 0, 0, 0);  // 이벤트 시작 날짜의 시간 정보를 제거
				eventEndDate.setHours(0, 0, 0, 0);    // 이벤트 종료 날짜의 시간 정보를 제거
				selectedDate.setHours(0, 0, 0, 0);    // 선택한 날짜의 시간 정보를 제거
				
				// 클릭한 날짜가 이벤트 기간 내에 포함되어 있는지 확인
				return selectedDate >= eventStartDate && selectedDate <= eventEndDate;
			});
			
			// 수정 버튼 및 이벤트 목록 표시
			showEventDetails(eventsOnDate);	
			
			// 달력에서 일정추가 버튼 눌렀을 때
			addEventBtn.addEventListener('click', function() {
				openAddEventModal(info.dateStr);
			});
			
		},
		
		// 사용자가 특정 일정을 클릭 시 실행되는 함수
		eventClick: function(info) {
			selectedDate = info.event.start;  // 선택된 일정의 시작 날짜를 저장
			let options = { weekday: 'short', year: 'numeric', month: 'long', day: 'numeric' };  // 날짜 포맷 설정
			modalDate.textContent = selectedDate.toLocaleDateString('en-US', options); // 영어로 날짜와 요일(축약) 표시
			
			// 클릭한 일정의 제목과 내용을 필터링하여 사용
			let eventsOnDate = [info.event];
			
			// 수정 버튼 및 이벤트 목록 표시
			showEventDetails(eventsOnDate);
		}
    });

    calendar.render();  // 캘린더를 화면에 렌더링
	
	// // 체크박스 상태에 따라 일정을 필터링하는 함수
	// function filterEventsByGroup() {
	// 	let selectedGroups = [];
	//
	// 	// 모든 일정을 가져와 필터링
	// 	let allEvents = calendar.getEvents();
	// 	allEvents.forEach(function(event) {
	// 		let groupName = event.extendedProps.group;
	// 		if (selectedGroups.includes(groupName)) {
	// 			event.setProp('display', 'auto');  // 해당 그룹은 보이게 함
	// 		} else {
	// 			event.setProp('display', 'none');  // 해당 그룹이 아닌 것은 숨김
	// 		}
	// 	});
	// }
	
	// 수정 버튼과 이벤트 목록을 모달에 표시하는 함수
	function showEventDetails(eventsOnDate) {
		const existingUpdateButton = document.getElementById('updateEventBtn');
		if (existingUpdateButton) {			// 만약 '수정' 버튼이 있으면
			existingUpdateButton.remove();	// '수정' 버튼 삭제
		}
		
		if (eventsOnDate.length > 0) {	// 필터링된 이벤트가 있을 경우 이벤트 목록을 모달에 표시
			let eventListHTML = '';	// 이벤트 내용을 받을 html
			eventsOnDate.forEach(function(event) {
				eventListHTML += '<div><strong>일정 제목:</strong> ' + event.title + '<br>' +
				'<strong>일정 내용:</strong> ' + (event.extendedProps.content || '내용이 없습니다.') + '</div><br>';
			});
			modalContent.innerHTML = eventListHTML;
			
			let updateEventBtn = document.createElement('button');
			updateEventBtn.textContent = '수정';
			updateEventBtn.id = 'updateEventBtn';
			updateEventBtn.addEventListener('click', function() {
				alert('수정 로직을 여기에 추가하세요!');
			});
			
			let modalActions = document.querySelector('.modal-actions');
			modalActions.insertBefore(updateEventBtn, modalActions.children[1]);
		} else {
			modalContent.textContent = '해당 날짜에는 일정이 없습니다.';
		}
		eventModal.style.display = 'block';
	}
	
	// 일정 추가 모달 창을 여는 함수
	function openAddEventModal(startDate) {
		eventModal.style.display = 'none';	// 기본 모달 닫기
		document.getElementById('eventTitle').value = '';	// 일정 제목 초기화
		document.getElementById('startDate').value = startDate || '';	// 시작 날짜 초기화
		document.getElementById('endDate').value = '';		// 종료 날짜 초기화
		document.getElementById('startTime').value = '';	// 시작 시간 초기화
		document.getElementById('endTime').value = '';		// 종료 시간 초기화
		document.getElementById('eventCategory').value = '구분을 선택해주세요(미선택시 개인으로 저장합니다.)';	// 구분 초기화 (카테고리)
		document.getElementById('eventDetail').value = '';	// 일정 상세 내용 초기화
		addEventModal.style.display = 'block';	// 일정 추가 모달 창 열기
	}

	// 일정 등록 로직
	saveEventBtn.addEventListener('click', function() {
		let eventTitle = document.getElementById('eventTitle').value;	// 입력된 일정 제목
		let startDate = document.getElementById('startDate').value;		// 선택된 시작일
		let endDate = document.getElementById('endDate').value;			// 선택된 종료일
		let startTime = document.getElementById('startTime').value;		// 선택된 시작시간
		let endTime = document.getElementById('endTime').value;			// 선태괸 종료시간
		let isAllDay = !startTime;					// 시작 시간이 없으면 all-day 이벤트로 처리
		let eventCategory = document.getElementById('eventCategory').value;	//선택된 카테고리
		let eventDetail = document.getElementById('eventDetail').value;		// 입력된 상세 내용
		
		// 종료 시간이 입력되지 않은 경우 시작 시간과 동일하게 설정
		if (!endTime) {		
			endTime = startTime;
		}
		
		// 일정 제목과 시작 날짜가 입력되었는지 확인 (필수 항목)
		if (eventTitle && startDate) {
			let eventEnd = endDate ? endDate : startDate;

			// 일정 데이터를 서버로 전송
			const eventData = {
				title: eventTitle,
				content: eventDetail,
				startDate: startDate + (isAllDay ? 'T00:00:00' : 'T' + startTime),	// 시작일 + 시간
				endDate: eventEnd + (isAllDay ? 'T23:59:59' : 'T' + endTime),		// 종료일 + 시간
				allDay: isAllDay ? 1 : 0,
				categoryId: eventCategory === "개인" ? 1 : eventCategory === "회사" ? 2
						: eventCategory === "부서" ? 3 : 4
			};

			// 서버에 이벤트 데이터를 전송
			fetch("http://localhost:8888/api/schedule/add", {	// API 주소로 전송
				method: "POST",		// POST 메소드 사용
				headers: {
					"Content-Type": "application/json"	// JSON 형식의 데이터를 보낸다고 명시
				},
				body: JSON.stringify(eventData)		// JavaScript 객체를 JSON 문자열로 변환
			})
				.then(response => response.json())		// 서버의 응답을 JSON으로 파싱
				.then(data => {
					console.log("Event added:", data);  // 서버에서 받은 데이터를 콘솔에 출력, 확인용
					alert("일정이 성공적으로 추가되었습니다!");
					calendar.refetchEvents();  // 서버(DB)에서 전체 이벤트(일정 데이터)를 다시 가져와 렌더링
					addEventModal.style.display = 'none';	// 일정 추가 모달 닫기
				})
				.catch(error => {
					console.error("Error:", error);		// 에러 발생 시 콘솔에 에러 메시지 출력
					alert("일정 추가 중 오류가 발생했습니다.");
				});

		} else {
			alert('필수 항목을 입력하세요.');
		}
	});
	
	// "취소하기" 버튼 클릭 시 일정 추가 모달 창을 닫는 함수
	cancelAddEventBtn.addEventListener('click', function() {
		addEventModal.style.display = 'none';  // 일정 추가 모달 닫기
	});	
		
    // 모달 창에서 "취소" 버튼 클릭 시 모달 닫기
	closeModalBtn.addEventListener('click', function() {
	    eventModal.style.display = 'none';
	});
	
	//esc 눌르면 모달 닫기
	window.addEventListener('keydown', function(event) {
	    if (event.key === 'Escape') {
	        addEventModal.style.display = 'none';
	        eventModal.style.display = 'none';
	    }
	});
});
