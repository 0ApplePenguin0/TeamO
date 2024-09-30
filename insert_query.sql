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
