이 브랜치는 기존 branch에서 땡겨와서 message와 융합하고 동작을 확인하기 위한 브랜치 입니다.   
예시 데이터를 활용하려면 회원가입할떄 ID는 1234, 12345로 해주시기 바랍니다.(chatroom은 1234로만 로그인 할것)

create table members (    -- 회원 정보 저장 테이블   
    member_id varchar(50) primary key,   
    member_password varchar(100) not null,   
    member_name varchar(50) not null,   
    email varchar(100),   
    rolename varchar(50) not null default 'ROLE_EMPLOYEE'    
        check(rolename in ('ROLE_EMPLOYEE', 'ROLE_ADMIN', 'ROLE_MANAGER'))   
    company_url varchar(100);   
    FOREIGN KEY (company_url) REFERENCES companys(company_url)   
);   
   
CREATE TABLE companys (        -- 회사 정보 테이블   
    company_url VARCHAR(255) PRIMARY KEY,   
    member_id VARCHAR(50) not null,   
    company_name VARCHAR(100) not null,   
    company_adress VARCHAR(300) not null,   
    FOREIGN KEY (member_id) REFERENCES members(member_id)   
);   
   
CREATE TABLE departments (    -- 부서 정보 테이블   
    department_num INT PRIMARY KEY auto_increment,   
    company_url VARCHAR(255) not null,   
    department_name VARCHAR(100) not null default '발령 대기',   
    FOREIGN KEY (company_url) REFERENCES companys(company_url)   
);   
   
CREATE TABLE subdeps (    -- 부서 상세 정보 테이블   
    subdep_num INT PRIMARY KEY auto_increment,   
    company_url VARCHAR(255) not null,   
    department_num INT not null,   
    subdep_name VARCHAR(100),   
    FOREIGN KEY (company_url) REFERENCES companys(company_url),   
    FOREIGN KEY (department_num) REFERENCES departments(department_num)   
);   
   
CREATE TABLE member_detail (    -- 회원 상세 정보 테이블   
    member_num INT PRIMARY KEY auto_increment,   
    member_id VARCHAR(50) not null,   
    department_num INT not null,   
    company_url VARCHAR(255) not null,   
    member_status VARCHAR(50) not null default '재직',   
    profile VARCHAR(100) not null default 'icon',   
    hire_date DATE,   
    subdep_num INT,   
    FOREIGN KEY (member_id) REFERENCES members(member_id),   
    FOREIGN KEY (department_num) REFERENCES departments(department_num),   
    FOREIGN KEY (company_url) REFERENCES companys(company_url),   
    FOREIGN KEY (subdep_num) REFERENCES subdeps(subdep_num)   
);   
   
CREATE TABLE message (   
    message_num INT PRIMARY KEY AUTO_INCREMENT,   
    company_url VARCHAR(255) not null,   
    department_num INT,   
    subdep_num INT,   
    sender VARCHAR(50) NOT NULL,   
    receiver VARCHAR(50) NOT NULL,   
    title varchar(100) NOT NULL,   
    content VARCHAR(1000) NOT NULL,   
    sent_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   
    read_chk tinyint default '0',   
    delete_status tinyint default '0',   
    delete_date TIMESTAMP,   
    original_name varchar(300),   
    file_name varchar(100),   
    FOREIGN KEY (sender) REFERENCES members(member_id),   
    FOREIGN KEY (receiver) REFERENCES members(member_id),   
    FOREIGN KEY (company_url) REFERENCES companys(company_url),   
    FOREIGN KEY (department_num) REFERENCES departments(department_num),   
    FOREIGN KEY (subdep_num) REFERENCES subdeps(subdep_num)   
);   
   
insert into companys(company_url, member_id, company_name, company_adress) values('muhan', '1234', '무한상사', 'muhan');   
insert into departments(company_url, department_name) values('muhan', '인사관리부');   
insert into subdeps(company_url, department_num, subdep_name) values('muhan', '2', '인사부');   
insert into member_detail(member_id, department_num, company_url, subdep_num) values('1234', '1', 'muhan', '1');   
insert into member_detail(member_id, department_num, company_url, subdep_num) values('12345', '2', 'muhan', '2');   
   
CREATE TABLE chat_room (   
    chatroom_id INT PRIMARY KEY AUTO_INCREMENT,  -- 채팅룸 ID   
    company_url VARCHAR(255) NOT NULL,           -- 회사 URL   
    department_id VARCHAR(50),                   -- 부서 ID (NULL 가능)   
    subdep_id VARCHAR(50),                       -- 서브 부서 ID (NULL 가능)   
    project_num VARCHAR(50),                     -- 프로젝트 번호 (NULL 가능)   
    createdby_id VARCHAR(50) NOT NULL,           --   
    chatroom_name VARCHAR(255) NOT NULL,         -- 채팅룸 이름   
    remarks VARCHAR(255),                    -- 비고   
    FOREIGN KEY (createdby_id) REFERENCES members(member_id) -- 외래 키 제약조건 추가   
    -- FOREIGN KEY (company_url) REFERENCES companys(company_url) -- 외래 키 제약조건 추가   
);   
INSERT INTO chat_room (company_url, department_id, subdep_id, project_num, createdby_id, chatroom_name, remarks) VALUES ('www.workhive.co.kr', NULL, NULL, NULL, '1234', '워크하이브', NULL);
