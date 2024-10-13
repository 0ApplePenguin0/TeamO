


    // 카테고리, 일정의 띠 색깔 값 순서중요!!!!!!!!!!!!!
insert into category(category_name, color) values ('개인','#4BB4BF');
insert into category(category_name, color) values ('회사','#BFBDBA');
insert into category(category_name, color) values ('부서','#F2EA79');
insert into category(category_name, color) values ('팀','#A7F272');

INSERT INTO form_template (form_name, form_structure, is_active)
VALUES ('휴가/연차 신청서', '[
  {
    "type": "text",
    "label": "신청자 이름",
    "name": "applicantName",
    "required": true
  },
  {
    "type": "date",
    "label": "시작 날짜",
    "name": "startDate",
    "required": true
  },
  {
    "type": "date",
    "label": "종료 날짜",
    "name": "endDate",
    "required": true
  },
  {
    "type": "select",
    "label": "휴가 종류",
    "name": "leaveType",
    "required": true,
    "values": [
      {"label": "연차", "value": "annual"},
      {"label": "반차", "value": "half"},
      {"label": "병가", "value": "sick"},
      {"label": "기타", "value": "other"}
    ]
  },
  {
    "type": "textarea",
    "label": "사유",
    "name": "reason",
    "required": false
  }
]', TRUE);


INSERT INTO form_template (form_name, form_structure, is_active)
VALUES ('물품 신청서', '[
  {
    "type": "text",
    "label": "신청자 이름",
    "name": "applicantName",
    "required": true
  },
  {
    "type": "text",
    "label": "물품명",
    "name": "itemName",
    "required": true
  },
  {
    "type": "number",
    "label": "수량",
    "name": "quantity",
    "required": true
  },
  {
    "type": "textarea",
    "label": "사용 목적",
    "name": "purpose",
    "required": false
  }
]', TRUE);

INSERT INTO form_template (form_name, form_structure, is_active)
VALUES ('지출 결의서', '[
  {
    "type": "text",
    "label": "신청자 이름",
    "name": "applicantName",
    "required": true
  },
  {
    "type": "number",
    "label": "금액",
    "name": "amount",
    "required": true
  },
  {
    "type": "date",
    "label": "지출 날짜",
    "name": "expenseDate",
    "required": true
  },
  {
    "type": "textarea",
    "label": "지출 내역",
    "name": "details",
    "required": true
  }
]', TRUE);

INSERT INTO form_template (form_name, form_structure, is_active)
VALUES ('기안서', '[
  {
    "type": "text",
    "label": "작성자 이름",
    "name": "writerName",
    "required": true
  },
  {
    "type": "textarea",
    "label": "내용",
    "name": "content",
    "required": true
  }
]', TRUE);

INSERT INTO form_template (form_name, form_structure, is_active)
VALUES ('보고서', '[
  {
    "type": "text",
    "label": "작성자 이름",
    "name": "writerName",
    "required": true
  },
  {
    "type": "textarea",
    "label": "내용",
    "name": "content",
    "required": true
  }
]', TRUE);

-- 기본 값 삽입
    INSERT INTO chatroom_kind (chatroom_kind_id, kind)
    VALUES
        (1, '회사'),
        (2, '부서'),
        (3, '팀'),
        (4, '프로젝트');