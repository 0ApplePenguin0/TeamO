
--  회사 테이블 (company)
CREATE TABLE company (
                         company_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         company_name VARCHAR(255) NOT NULL,
                         company_address VARCHAR(255) NOT NULL,
                         company_url VARCHAR(100)
);

--  회원 테이블 (members)
CREATE TABLE members (
                         member_id VARCHAR(100) PRIMARY KEY,
                         member_name VARCHAR(50) NOT NULL,
                         email VARCHAR(50) NOT NULL UNIQUE,
                         member_password VARCHAR(100) NOT NULL,
                         role ENUM('ROLE_USER', 'ROLE_EMPLOYEE', 'ROLE_MANAGER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
                         company_id BIGINT NULL,
                         FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE SET NULL
);

--  부서 테이블 (department)
CREATE TABLE department (
                            department_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            company_id BIGINT NOT NULL,
                            department_name VARCHAR(50) NOT NULL,
                            FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE
);

--  팀 테이블 (team)
CREATE TABLE team (
                      team_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      department_id BIGINT NOT NULL,
                      team_name VARCHAR(50) NOT NULL,
                      FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE CASCADE
);

--  직급 테이블 (position)
CREATE TABLE position (
                          position_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          company_id BIGINT NOT NULL,
                          position_name VARCHAR(50) NOT NULL,
                          FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE
);


-- 회원 상세 정보 테이블 (member_detail)
CREATE TABLE member_detail (
                               member_detail_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               member_id VARCHAR(100) NOT NULL,
                               position_id BIGINT NOT NULL,
                               department_id BIGINT NOT NULL,
                               team_id BIGINT NOT NULL,
                               status VARCHAR(100) NOT NULL DEFAULT '재직 중' COMMENT '퇴직, 출장, 출산휴가 등',
                               profile_url VARCHAR(255) NULL COMMENT '프로필 사진 URL',
                               hire_date DATE NULL,
                               FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
                               FOREIGN KEY (position_id) REFERENCES `position`(position_id) ON DELETE CASCADE,
                               FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE CASCADE,
                               FOREIGN KEY (team_id) REFERENCES team(team_id) ON DELETE CASCADE
);

--  메모 테이블 (memo)
CREATE TABLE memo (
                      memo_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      member_id VARCHAR(100) NOT NULL,
                      content VARCHAR(200) NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

--  메시지 테이블 (message)
CREATE TABLE message (
                         message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         sender_id VARCHAR(100) NOT NULL,
                         receiver_id VARCHAR(100) NOT NULL,
                         title VARCHAR(100) NOT NULL,
                         content VARCHAR(255) NOT NULL,
                         sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT '메시지 읽음 여부',
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '메시지 삭제 여부',
                         delete_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '삭제된 날짜',
                         FOREIGN KEY (sender_id) REFERENCES members(member_id) ON DELETE CASCADE,
                         FOREIGN KEY (receiver_id) REFERENCES members(member_id) ON DELETE CASCADE
);

--  채팅방 종류 테이블 (chatroom_kind)
CREATE TABLE chatroom_kind (
                               chatroom_kind_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               kind VARCHAR(50) NOT NULL COMMENT '회사, 부서, 팀, 프로젝트 등'
);

--  채팅방 테이블 (chatroom)
CREATE TABLE chatroom (
                          chatroom_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          chatroom_kind_id BIGINT NOT NULL,
                          company_id BIGINT NOT NULL,
                          created_by_member_id VARCHAR(100) NOT NULL,
                          chatroom_name VARCHAR(50) NOT NULL,
                          FOREIGN KEY (chatroom_kind_id) REFERENCES chatroom_kind(chatroom_kind_id) ON DELETE CASCADE,
                          FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE,
                          FOREIGN KEY (created_by_member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

--  채팅 메시지 테이블 (chat)
CREATE TABLE chat (
                      chat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      chatroom_id BIGINT NOT NULL,
                      member_id VARCHAR(100) NOT NULL,
                      message VARCHAR(255) NOT NULL,
                      sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      is_deleted BOOLEAN not null DEATULT FALSE,
                      FOREIGN KEY (chatroom_id) REFERENCES chatroom(chatroom_id) ON DELETE CASCADE,
                      FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

--  프로젝트 멤버 테이블 (project_member)
CREATE TABLE project_member (
                                project_member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                chatroom_id BIGINT NOT NULL,
                                member_id VARCHAR(100) NOT NULL,
                                role VARCHAR(50) NOT NULL COMMENT '방장, 부방장, 일반 등',
                                FOREIGN KEY (chatroom_id) REFERENCES chatroom(chatroom_id) ON DELETE CASCADE,
                                FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

-- 결재 양식 테이블 (form_template)
CREATE TABLE form_template (
                               template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               form_name VARCHAR(100) NOT NULL,           -- 양식 이름 (예: 휴가 신청서)
                               form_structure JSON NOT NULL,              -- 양식 구조를 JSON 형태로 저장 (기본 값)
                               is_active BOOLEAN NOT NULL DEFAULT TRUE    -- 양식 사용 여부
);

CREATE TABLE company_custom_template (
                                         company_id BIGINT NOT NULL,                -- 회사 ID
                                         template_id BIGINT NOT NULL,               -- 수정할 기본 양식 ID
                                         custom_structure JSON NOT NULL,            -- 수정된 양식 구조를 JSON 형태로 저장
                                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (company_id, template_id),
                                         FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE,
                                         FOREIGN KEY (template_id) REFERENCES form_template(template_id) ON DELETE CASCADE
);

-- 결재 테이블 (approval)
CREATE TABLE approval (
                          approval_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          template_id BIGINT NOT NULL,
                          requester_id VARCHAR(100) NOT NULL COMMENT '결재 요청자 ID',
                          company_id BIGINT NOT NULL COMMENT '회사 ID',
                          approval_status VARCHAR(50) DEFAULT 'PENDING' COMMENT '전체 결재 상태',
                          request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          title VARCHAR(255) NOT NULL,
                          content JSON NOT NULL COMMENT '결재 내용',
                          FOREIGN KEY (template_id) REFERENCES form_template(template_id) ON DELETE CASCADE,
                          FOREIGN KEY (requester_id) REFERENCES members(member_id) ON DELETE CASCADE,
                          FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE
);

--  결재 라인 테이블 (approval_line)
CREATE TABLE approval_line (
                               approval_line_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               approval_id BIGINT NOT NULL,
                               step_order INT NOT NULL COMMENT '결재 순서',
                               department_id BIGINT NULL COMMENT '결재자 부서',
                               team_id BIGINT NULL COMMENT '결재자 팀',
                               position_id BIGINT NULL COMMENT '결재자 직급',
                               member_id VARCHAR(100) NOT NULL COMMENT '결재자 ID',
                               status VARCHAR(50) DEFAULT 'PENDING' COMMENT '결재 상태 (PENDING, APPROVED, REJECTED)',
                               comment TEXT NULL COMMENT '결재자 코멘트',
                               approval_date TIMESTAMP NULL COMMENT '결재 완료 날짜',
                               FOREIGN KEY (approval_id) REFERENCES approval(approval_id) ON DELETE CASCADE,
                               FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE SET NULL,
                               FOREIGN KEY (team_id) REFERENCES team(team_id) ON DELETE SET NULL,
                               FOREIGN KEY (position_id) REFERENCES `position`(position_id) ON DELETE SET NULL,
                               FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

-- 결재 히스토리 테이블 (approval_history)
CREATE TABLE approval_history (
                                  history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  approval_id BIGINT NOT NULL,
                                  member_id VARCHAR(100) NOT NULL COMMENT '결재자 ID',
                                  status VARCHAR(50) NOT NULL COMMENT '결재 상태 (APPROVED, REJECTED)',
                                  comment TEXT NULL COMMENT '결재자 의견',
                                  approval_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '결재 완료 날짜',
                                  FOREIGN KEY (approval_id) REFERENCES approval(approval_id) ON DELETE CASCADE,
                                  FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

-- 일정 테이블 (schedule)
CREATE TABLE schedule (
                          schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          member_id VARCHAR(100) NOT NULL COMMENT '일정을 등록한 회원',
                          title VARCHAR(255) NULL,
                          description VARCHAR(255) NULL,
                          start_date TIMESTAMP NULL,
                          end_date TIMESTAMP NULL,
                          is_all_day BOOLEAN NULL COMMENT '당일 일정 여부',
                          category_id BIGINT not null,
                          category_num BIGINT default null COMMENT '추가적인 카테고리 분류 번호',
                          FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE cascade,
                          foreign key (category_id) references category(category_id) ON DELETE cascade
);

-- 카테고리 테이블 (category)
CREATE TABLE category (
                          category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          category_name VARCHAR(50) NULL,
                          color VARCHAR(255) NOT NULL
);

-- 출근 테이블 (attendance)
CREATE TABLE attendance (
                            attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            member_id VARCHAR(100) NOT NULL,
                            check_in TIMESTAMP NULL,
                            check_out TIMESTAMP NULL,
                            attendance_date DATE NULL,
                            status VARCHAR(100) NULL COMMENT '출근, 휴가, 반차 등',
                            FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

--  파일 테이블 (file)
CREATE TABLE file (
                      file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      company_id BIGINT NOT NULL,
                      member_id VARCHAR(100) NOT NULL COMMENT '파일을 업로드한 회원',
                      file_name VARCHAR(255) NOT NULL COMMENT '파일 이름',
                      file_url VARCHAR(255) NOT NULL COMMENT '파일이 저장된 URL',
                      file_type VARCHAR(50) NOT NULL COMMENT '파일 유형 (예: 이미지, PDF 등)',
                      file_size BIGINT COMMENT '파일 크기 (바이트)',
                      uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '파일 업로드 시간',
                      is_deleted BOOLEAN DEFAULT FALSE COMMENT '파일 삭제 여부',
                      associated_type VARCHAR(50) NOT NULL COMMENT '연관된 도메인 (CHATROOM, MESSAGE, APPROVAL 등)',
                      associated_id BIGINT NOT NULL COMMENT '연관된 엔티티의 ID (예: message_id, approval_id)',
                      FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE,
                      FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

--  회의실 테이블 (meeting_room)
CREATE TABLE meeting_room (
                              room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              company_id BIGINT NOT NULL,
                              room_name VARCHAR(100) NOT NULL,
                              location VARCHAR(255) NOT NULL,
                              capacity INT NOT NULL COMMENT '최대 수용 인원',
                              room_status VARCHAR(50) DEFAULT 'AVAILABLE' COMMENT '회의실 상태 (AVAILABLE, UNAVAILABLE)',
                              FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE
);

--  회의실 예약 테이블 (meeting_room_reservation)
CREATE TABLE meeting_room_reservation (
                                          reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          room_id BIGINT NOT NULL,
                                          member_id VARCHAR(100) NOT NULL COMMENT '예약을 한 회원 ID',
                                          company_id BIGINT NOT NULL COMMENT '회의실 예약이 속한 회사',
                                          start_time TIMESTAMP NOT NULL COMMENT '회의 시작 시간',
                                          end_time TIMESTAMP NOT NULL COMMENT '회의 종료 시간',
                                          status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED' COMMENT '예약 상태 (CONFIRMED, CANCELED)',
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '예약 요청 시간',
                                          cancel_reason VARCHAR(255) NULL COMMENT '예약 취소 사유',
                                          version BIGINT NOT NULL DEFAULT 0 COMMENT '낙관적 락을 위한 버전',
                                          FOREIGN KEY (room_id) REFERENCES meeting_room(room_id) ON DELETE CASCADE,
                                          FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
                                          FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE,
                                          CONSTRAINT unique_reservation UNIQUE (room_id, start_time, end_time)
);


-- 초대 코드 테이블 (invitation_code)
CREATE TABLE invitation_code (
                                 code_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 company_id BIGINT NOT NULL,
                                 code VARCHAR(100) NOT NULL UNIQUE,
                                 expiration_date TIMESTAMP NULL,
                                 usage_limit INT NULL COMMENT '코드 사용 가능 횟수',
                                 usage_count INT DEFAULT 0 COMMENT '코드가 사용된 횟수',
                                 is_active BOOLEAN DEFAULT TRUE,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 created_by VARCHAR(100),
                                 FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE,
                                 FOREIGN KEY (created_by) REFERENCES members(member_id) ON DELETE SET NULL
);
