commit;

select * from company;
select * from members;
select * from member_detail;
select * from department;
select * from team;
select * from position;
select * from invitation_code;
select * from memo;
select * from message;
select * from chatroom;
select * from chatroom_kind;
select * from chat;
select * from project_member;
select * from schedule;
select * from category;
select * from schedule_category;
select * from approval;
select * from approval_line;
select * from approval_content;
select * from approval_history;
select * from form_template;
select * from company_custom_template;
select * from meeting_room;
select * from meeting_room_reservation;
select * from attendance;
select * from file;

insert into invitation_code values (1, 1, 'abc','2024-10-10 12:12:12', 1, 1, true, '2024-09-20 12:12:12', '123');

INSERT INTO invitation_code (company_id, code, expiration_date, usage_limit, usage_count, is_active, created_by)
VALUES (1, 'abc', '2024-09-22 23:59:59', 100, 0, TRUE, '123');

insert into chatroom_kind values (1, 'project');

set foreign_key_checks = 0;

    // 카테고리, 일정의 띠 색깔 값 순서중요!!!!!!!!!!!!!
insert into category(category_name, color) values ('개인','#4BB4BF');
insert into category(category_name, color) values ('회사','#BFBDBA');
insert into category(category_name, color) values ('부서','#F2EA79');
insert into category(category_name, color) values ('팀','#A7F272');


drop table company;
drop table members;
drop table member_detail;
drop table department;
drop table team;
drop table position;
drop table invitation_code;
drop table memo;
drop table message;
drop table chatroom;
drop table chatroom_kind;
drop table chat;
drop table project_member;
drop table schedule;
drop table category;
drop table approval;
drop table approval_line;
drop table approval_content;
drop table approval_history;
drop table form_template;
drop table company_custom_template;
drop table meeting_room;
drop table meeting_room_reservation;
drop table attendance;
drop table file;

set foreign_key_checks = 0;



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
    "type": "text",
    "label": "제목",
    "name": "title",
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
    "type": "text",
    "label": "제목",
    "name": "title",
    "required": true
  },
  {
    "type": "textarea",
    "label": "내용",
    "name": "content",
    "required": true
  }
]', TRUE);
