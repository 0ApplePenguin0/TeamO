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

	let isEdit = false;  // 일정 추가/수정 모드를 구분하는 변수
	let currentEventId = null;  // 수정 중인 일정의 ID를 저장할 변수
	let userRole = '';	    // role 정보를 저장할 변수 선언

	// FullCalendar 초기화
	let calendar = new FullCalendar.Calendar(calendarEl, {
		headerToolbar: {
			left: 'prev,next today',  // 이전, 다음, 오늘 버튼
			center: 'title',  // 제목 (현재 달력의 월 또는 주)
			right: 'addEventButton dayGridMonth,listMonth'  // 커스텀으로 만든 일정추가 버튼 + 월간 뷰와 리스트 뷰만 표시
		},
		initialView: 'dayGridMonth',        // 초기 캘린더 뷰를 월간(dayGridMonth)으로 설정
		dayMaxEvents: 2,  // 하루에 표시할 최대 이벤트 수 (숫자로 설정)
		dayMaxEventRows: true,  // 여러 줄의 이벤트 표시를 가능하게 설정
		selectable: true,                   // 사용자가 날짜를 선택할 수 있도록 설정 (일정 추가를 위해 사용)
		eventOrder: "-allDay, start, title",  // 일정 우선 순위(allDay > start날짜 > title ) 설정
		displayEventTime: false,  // 시간을 아예 표시하지 않도록 설정
		customButtons: {	// 커스텀 버튼 설정
			addEventButton: {	//	일정추가 버튼 설정
				text: '일정 추가',	// 버튼에 표시될 텍스트
				click: function() {
					// 일정 추가 모달을 여는 로직
					openAddEventModal();  // 일정 추가 모달 열기 함수 호출
				}
			}
		},
		// 서버(DB)에서 데이터 가져오는 부분 (Ajax 요청)
		events: function(fetchInfo, successCallback, failureCallback) {
			// 일정데이터를 가져오는 Ajax 요청
			fetch("/api/schedule/events")  // API 엔드포인트 (서버(DB)에 있는 데이터 불러오기)
				.then(response => response.json())  // 서버 응답을 JSON 형식으로 변환
				.then(data => {
					let events = data.map(function(event) {	// FullCalendar가 이해할 수 있는 형식으로 데이터 변환
						let originalEndDate = event.endDate;  // 실제 종료일 (로직 처리에 사용)
						let displayEndDate = originalEndDate; // 띠 전용 종료일 + 1 일수 (+1 처리는 밑에서)

						// allDay일 경우 종료일에 하루를 추가 (달력에만 반영)
						if (event.isAllDay && originalEndDate) {
							let displayEndObj = new Date(originalEndDate);
							displayEndObj.setDate(displayEndObj.getDate() + 1);  // 종료일에 하루 추가 (allDay일 경우)
							displayEndDate = displayEndObj.toISOString();  // 날짜 및 시간 모두 포함
						} else if (!event.isAllDay && originalEndDate) {
							// 종료일이 존재할 경우 그대로 사용 (시간 포함)
							displayEndDate = originalEndDate;
						}

						return {
							id: event.scheduleId, // 일정 ID
							title: event.title,	// 일정 제목
							start: event.isAllDay ? event.startDate.split('T')[0] : event.startDate,  // allDay일 경우 날짜만, 아니면 시간까지 포함
							end: displayEndDate ? originalEndDate : null,  // 달력에만 반영된 종료일
							isAllDay: event.isAllDay,    // allDay 이벤트 여부 확인
							backgroundColor: event.color,  // 그룹 색상으로 배경(띠) 색 설정
							borderColor: event.color,  // 그룹 색상으로 동그라미 색 설정
							textColor: 'white',  // 텍스트 색상
							extendedProps: {
								scheduleId: event.scheduleId,  // 일정 ID
								originalEnd: originalEndDate,  // 실제 종료일 저장
								description: event.description,  // 일정 설명
								categoryId: event.categoryId, // 구분 ID
								categoryNum: event.categoryNum  // 구분에 따른 여러가지 ID
							}
						};
					});
					successCallback(events);	// 캘린더에 이벤트를 전달하여 렌더링
					updateTodayTaskList(); // 캘린더 렌더링 후 '오늘의 할일' 업데이트
				})
				.catch(error => {
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
				if(event.isAllDay && eventStartDate.toISOString().split('T')[0] !== eventEndDate.toISOString().split('T')[0]) {
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

	// 체크박스를 DOM에 추가
	let checkboxGroup = `
    <div id="checkboxGroup" style="display: inline-block; margin-left: 10px;">
        <label><input type="checkbox" id="checkbox1" value="category1" checked> 개인</label>
        <label><input type="checkbox" id="checkbox2" value="category2" checked> 회사</label>
        <label><input type="checkbox" id="checkbox3" value="category3" checked> 부서</label>
        <label><input type="checkbox" id="checkbox4" value="category4" checked> 팀</label>
    </div>
`;

	// 캘린더 랜더링 후 DOM에 체크박스 추가
	document.querySelector('.fc-today-button').insertAdjacentHTML('afterend', checkboxGroup);

	// 체크박스 상태에 따른 필터링 함수 호출
	document.getElementById('checkbox1').addEventListener('change', filterEventsByCategory);
	document.getElementById('checkbox2').addEventListener('change', filterEventsByCategory);
	document.getElementById('checkbox3').addEventListener('change', filterEventsByCategory);
	document.getElementById('checkbox4').addEventListener('change', filterEventsByCategory);

	// 체크박스 상태에 따라 일정을 필터링하는 함수
	function filterEventsByCategory() {
		let selectedCategories = [];

		// 각 체크박스의 상태 확인
		if (document.getElementById('checkbox1').checked) {
			selectedCategories.push(1); // 개인
		}
		if (document.getElementById('checkbox2').checked) {
			selectedCategories.push(2); // 회사
		}
		if (document.getElementById('checkbox3').checked) {
			selectedCategories.push(3); // 부서
		}
		if (document.getElementById('checkbox4').checked) {
			selectedCategories.push(4); // 팀
		}

		// 모든 일정을 가져와 필터링
		let allEvents = calendar.getEvents();
		allEvents.forEach(function(event) {
			let categoryId = event.extendedProps.categoryId;

			// 선택된 카테고리가 없으면 모든 일정 숨김
			if (selectedCategories.length === 0) {
				event.setProp('display', 'none'); // 모든 일정 숨기기
			} else {
				// 선택된 카테고리에 해당하는 일정만 표시하고 나머지는 숨김
				if (selectedCategories.includes(categoryId)) {
					event.setProp('display', 'auto'); // 해당 카테고리는 보이게
				} else {
					event.setProp('display', 'none'); // 해당 카테고리가 아니면 숨김
				}
			}
		});
	}

	// 이벤트 목록을 모달에 표시하는 함수
	function showEventDetails(eventsOnDate) {
		if (eventsOnDate.length > 0) {	// 필터링된 이벤트가 있을 경우 이벤트 목록을 모달에 표시
			let eventListHTML = '';	// 이벤트 내용을 받을 html
			eventsOnDate.forEach(function(event) {
				eventListHTML += `
				<button id="eventContent" class="event-button" data-schedule-id="${event.id}" style="border:none;background:none;text-align:left;padding:10px;cursor:pointer;">
					<strong>일정 제목:</strong> ${event.title} <br>
					<strong>일정 내용:</strong> ${event.extendedProps.description || '일정 내용이 없습니다.'}
				</button><br>
			`;
			});
			modalContent.innerHTML = eventListHTML;

			// 각 버튼에 클릭 이벤트 추가하여 수정 모달로 전송
			document.querySelectorAll('.event-button').forEach(button => {
				button.addEventListener('click', function() {
					let eventId = this.getAttribute('data-schedule-id');
					let selectedEvent = eventsOnDate.find(e => e.id == eventId);
					openEditEventModal(selectedEvent);  // 수정 모달 호출
				});
			});
		} else {
			modalContent.textContent = '해당 날짜에는 일정이 없습니다.';
		}
		eventModal.style.display = 'block';
	}

	// 로그인 사용자의 role을 가져와 '회사' 옵션 추가 하는 로직
	function addCompanyOptionIfAdmin() {
		// 로그인 사용자의 role을 가져와 '회사' 옵션 추가
		fetch("http://localhost:8888/api/schedule/role")  // 서버에서 사용자 role을 가져오는 API
			.then(response => response.json())
			.then(data => {
				userRole = data.role; // role 정보 받기

				if (userRole === 'ROLE_ADMIN') {
					// ROLE_ADMIN 일 경우 '회사' 카테고리가 이미 있는지 확인
					let existingOption = Array.from(eventCategory.options).find(option => option.value === '회사');
					if (!existingOption) {
						// '회사' 옵션이 없을 때만 추가
						let companyOption = document.createElement('option');
						companyOption.value = '회사';
						companyOption.text = '회사';
						eventCategory.appendChild(companyOption);
					}
				}
			})
			.catch(() => {
				alert('사용자 role을 가져오는 중 오류가 발생했습니다. 관리자에게 문의하세요.');
			});
	}

	// 일정 추가 모달 창을 여는 함수
	function openAddEventModal(startDate) {
		// 모달창 셋팅
		eventModal.style.display = 'none';	// 기본 모달 닫기
		document.getElementById('deleteEventBtn').style.display = 'none';	// 삭제 버튼 숨기기

		document.getElementById('eventTitle').value = '';	// 일정 제목 초기화
		document.getElementById('startDate').value = startDate || '';	// 시작 날짜 초기화
		document.getElementById('endDate').value = '';		// 종료 날짜 초기화
		document.getElementById('startTime').value = '';	// 시작 시간 초기화
		document.getElementById('endTime').value = '';		// 종료 시간 초기화
		document.getElementById('eventCategory').value = '구분을 선택해주세요(미선택시 개인으로 저장합니다.)';	// 구분 초기화 (카테고리)
		document.getElementById('eventDetail').value = '';	// 일정 상세 내용 초기화
		isEdit = false;	// 일정 추가 모드 (수정은 false)
		currentEventId = null;  // 수정할 이벤트 ID 초기화
		addEventModal.style.display = 'block';	// 일정 추가 모달 창 열기

		// '회사' 옵션 추가 로직 호출
		addCompanyOptionIfAdmin();  // 함수 호출
	}

	// 일정 등록 로직
	saveEventBtn.addEventListener('click', function() {
		if (isEdit) {
			// 일정 수정 로직 호출
			saveEditedEvent(currentEventId);
		} else {
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
					description: eventDetail,
					startDate: startDate + (isAllDay ? 'T00:00:00' : 'T' + startTime),	// 시작일 + 시간
					endDate: eventEnd + (isAllDay ? 'T23:59:59' : 'T' + endTime),		// 종료일 + 시간
					isAllDay: isAllDay ? 1 : 0,
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
					.then(() => {
						alert("일정이 성공적으로 추가되었습니다!");
						calendar.refetchEvents();  // 서버(DB)에서 전체 이벤트(일정 데이터)를 다시 가져와 렌더링
						addEventModal.style.display = 'none';	// 일정 추가 모달 닫기
					})
					.catch(() => {
						alert("일정 추가 중 오류가 발생했습니다.");
					});

			} else {
				alert('필수 항목을 입력하세요.');
			}
		}
	});

	// 일정 수정 모달 창을 여는 함수
	function openEditEventModal(event) {
		eventModal.style.display = 'none';  // 기존 모달 닫기

		// 모달 제목을 "일정 수정하기"로 변경
		document.querySelector('#addEventModal h2').textContent = '일정 수정하기';
		// 삭제 버튼 표시
		document.getElementById('deleteEventBtn').style.display = 'inline-block';

		// 시작 시간 및 종료 시간 확인
		let startDate = event.start ? new Date(event.start).toISOString().split('T')[0] : '';  // YYYY-MM-DD 형식으로 변환
		let endDate = event.end ? new Date(event.end).toISOString().split('T')[0] : '';
		let startTime = event.start ? new Date(event.start).toTimeString().slice(0, 5) : '';  // HH:MM 형식으로 변환
		let endTime = event.end ? new Date(event.end).toTimeString().slice(0, 5) : '';

		// 카테고리 숫자를 구분 이름으로 변환
		let categoryName = '';
		switch(event.extendedProps.categoryId) {
			case 1:
				categoryName = '개인';
				break;
			case 2:
				categoryName = '회사';
				break;
			case 3:
				categoryName = '부서';
				break;
			case 4:
				categoryName = '팀';
				break;
			default:
				categoryName = '구분을 선택해주세요(미선택시 개인으로 저장합니다.)';
				break;
		}

		// isAllDay 구분
		let isAllDay = event.extendedProps.isAllDay ? true : false;

		if (isAllDay) {
			// All-day 이벤트의 경우 하루를 추가해 정확한 날짜를 맞춤
			let correctedStartDate = new Date(event.start);
			correctedStartDate.setDate(correctedStartDate.getDate() + 1); // 하루 추가

			// All-day 이벤트인 경우 시간 필드를 비웁니다.
			document.getElementById('startTime').value = '';
			document.getElementById('endTime').value = '';
			document.getElementById('startDate').value = correctedStartDate.toISOString().split('T')[0];  // 시작 날짜 채우기
			document.getElementById('endDate').value = endDate;  // 종료 날짜 채우기
		} else {
			// 시간 이벤트인 경우 시간 필드를 채웁니다.
			document.getElementById('startTime').value = startTime !== '00:00' ? startTime : '';
			document.getElementById('endTime').value = endTime !== '00:00' ? endTime : '';
			document.getElementById('startDate').value = startDate;  // 시작 날짜 채우기
			document.getElementById('endDate').value = endDate;  // 종료 날짜 채우기
		}

		// 일정 정보를 모달에 채워넣기
		document.getElementById('eventTitle').value = event.title;  // 일정 제목을 모달에 채우기
		document.getElementById('eventCategory').value = categoryName;  // 구분(카테고리) 값 채우기
		document.getElementById('eventDetail').value = event.extendedProps.description;  // 상세 내용 채우기

		// '회사' 옵션 추가 로직 호출
		addCompanyOptionIfAdmin();  // 함수 호출

		isEdit = true;  // 일정 수정 모드
		currentEventId = event.id;  // 수정할 이벤트 ID 저장
		addEventModal.style.display = 'block';  // 일정 수정 모달 창 열기
	}

	// 일정 수정 로직
	function saveEditedEvent(eventId) {
		let eventTitle = document.getElementById('eventTitle').value;  // 입력된 일정 제목
		let startDate = document.getElementById('startDate').value;    // 선택된 시작일
		let endDate = document.getElementById('endDate').value;        // 선택된 종료일
		let startTime = document.getElementById('startTime').value;    // 선택된 시작시간
		let endTime = document.getElementById('endTime').value;        // 선택된 종료시간
		let isAllDay = !startTime;                                     // 시작 시간이 없으면 all-day 이벤트로 처리
		let eventCategory = document.getElementById('eventCategory').value;  // 선택된 카테고리
		let eventDetail = document.getElementById('eventDetail').value;      // 입력된 상세 내용

		// 종료 시간이 입력되지 않은 경우 시작 시간과 동일하게 설정
		if (!endTime) {
			endTime = startTime;
		}

		// 일정 제목과 시작 날짜가 입력되었는지 확인 (필수 항목)
		if (eventTitle && startDate) {
			let eventEnd = endDate ? endDate : startDate;

			// 일정 데이터를 서버로 전송
			const eventData = {
				scheduleId: eventId,  // 수정할 일정의 ID
				title: eventTitle,
				description: eventDetail,
				startDate: startDate + (isAllDay ? 'T00:00:00' : 'T' + startTime),  // 시작일 + 시간
				endDate: eventEnd + (isAllDay ? 'T23:59:59' : 'T' + endTime),       // 종료일 + 시간
				isAllDay: isAllDay ? 1 : 0,
				categoryId: eventCategory === "개인" ? 1 : eventCategory === "회사" ? 2
					: eventCategory === "부서" ? 3 : 4
			};

			// 서버에 수정된 이벤트 데이터를 전송
			fetch(`http://localhost:8888/api/schedule/update/${eventId}`, {  // API 주소로 전송 (PUT 메소드 사용)
				method: "PUT",  // PUT 메소드로 수정
				headers: {
					"Content-Type": "application/json"  // JSON 형식의 데이터를 보낸다고 명시
				},
				body: JSON.stringify(eventData)  // JavaScript 객체를 JSON 문자열로 변환
			})
				.then(response => {
					if (!response.ok) {
						// HTTP 응답 상태가 200-299가 아닐 경우 에러 처리
						return response.json().then(data => {
							throw new Error(data.message);  // 메시지 설정 (서버에서 받은 메시지 또는 기본 메시지)
						});
					}
					return response.json();  // 응답이 성공일 때만 JSON 파싱
				})
				.then(() => {
					alert("일정이 성공적으로 수정되었습니다!");
					calendar.refetchEvents();  // 서버(DB)에서 전체 이벤트(일정 데이터)를 다시 가져와 렌더링
					addEventModal.style.display = 'none';  // 일정 추가 모달 닫기
				})
				.catch(() => {
					alert("일정 수정 중 오류가 발생했습니다.");  // 에러 메시지 표시
				});

		} else {
			alert('필수 항목을 입력하세요.');
		}
	}

	// 일정 삭제하기 구현로직 (삭제하기 버튼 클릭시 실행)
	document.getElementById('deleteEventBtn').addEventListener('click', function() {
		if (confirm("정말 이 일정을 삭제하시겠습니까?")) {
			fetch(`http://localhost:8888/api/schedule/delete/${currentEventId}`, {  // DELETE 메소드로 일정 삭제 요청
				method: 'DELETE'
			})
				.then(response => {
					if (response.ok) {
						alert('일정이 삭제되었습니다.');
						calendar.refetchEvents();  // 캘린더를 새로고침하여 변경된 이벤트 표시
						addEventModal.style.display = 'none';  // 모달 닫기
					} else {
						alert('일정 삭제에 실패했습니다.');
					}
				})
				.catch(() => {
					alert('일정 삭제 중 오류가 발생했습니다.');
				});
		}
	});

	// 오늘의 할일, 오늘 일정 필터링 후 '오늘의 할일' 리스트를 업데이트를 구현하는 함수
	function updateTodayTaskList() {
		// 오늘 날짜를 정의
		let today = new Date();
		today.setHours(0, 0, 0, 0);  // 시간을 00:00:00으로 설정하여 날짜만 비교

		let eventsToday = calendar.getEvents().filter(event => {
			let eventStartDate = new Date(event.start);
			let eventEndDate = new Date(event.end || event.start);

			// allDay 일정을 처리하기 위한 종료 날짜 조정
			if (event.isAllDay && eventStartDate.toISOString().split('T')[0] !== eventEndDate.toISOString().split('T')[0]) {
				eventEndDate.setDate(eventEndDate.getDate() - 1);
			}

			eventStartDate.setHours(0, 0, 0, 0);
			eventEndDate.setHours(0, 0, 0, 0);

			return today >= eventStartDate && today <= eventEndDate;
		});

		let todayTaskList = document.getElementById('todayTaskList');
		todayTaskList.innerHTML = '';  // 기존 리스트 초기화

		if (eventsToday.length > 0) {
			eventsToday.forEach(event => {
				let time = event.extendedProps.isAllDay ? '하루 종일' : new Date(event.start).toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
				let li = document.createElement('li');
				li.textContent = `${time} - ${event.title}`;
				todayTaskList.appendChild(li);
			});
		} else {
			let li = document.createElement('li');
			li.textContent = '금일 저장된 일정이 없습니다.';
			todayTaskList.appendChild(li);
		}
	}


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
