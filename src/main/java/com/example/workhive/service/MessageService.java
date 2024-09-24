package com.example.workhive.service;

import com.example.workhive.domain.dto.MemberDTO;
import com.example.workhive.domain.dto.MessageDTO;
import com.example.workhive.domain.entity.*;
import com.example.workhive.repository.*;
import com.example.workhive.util.FileManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MessageService {

	private final MessageRepository messageRepository;
	private final MemberRepository usersRepository;
	private final CompanyRepository companyRepository;
	private final DepartmentRepository departmentRepository;
	private final TeamRepository teamRepository;

	// 메시지 작성 메서드
	public void write(MessageDTO messageDTO, String uploadPath, MultipartFile upload) throws IOException {
		// 발신자와 수신자 정보를 조회
		
	}

	// 발신한 메시지 목록 조회
	public List<MessageDTO> getsentListAll(String userId) {
		// 메시지 번호를 기준으로 내림차순 정렬
	
		return null;
	}

	// 수신한 메시지 목록 조회
	public List<MessageDTO> getreceivedListAll(String userId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		
		return null;
	}

	// 임시삭제된 메시지 목록 조회
	public List<MessageDTO> getdeletedListAll(String userId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		
		return null;
	}



	// 메시지 읽기
	public MessageDTO getBoard(int messageNum) {
		
		return null;
	}

	// 메시지 읽음 상태 업데이트
	public void updateReadStatus(int messageNum) {
		
	}

	// 메시지 삭제 상태 업데이트(임시 보관함으로 이동시키기 위해)
	public void updateDeleteStatus(int messageNum) {
	}

	// 메시지 복원
	public void restoreMessage(int messageNum) {
		
	}

	// 메시지 삭제
	public void delete(int messageNum) {
		
	}
	// 파일 다운로드 기능
	public void download(Integer messageNum, HttpServletResponse response, String uploadPath) {
		
	}

	// 30일이 지난 메시지 삭제
	public void deleteOldMessages() {
		
	}


	private final MemberDetailRepository memberDetailRepository;

	// 부서 선택용 서비스 추가
	public List<DepartmentEntity> getDepartmentsByCompanyUrl(String companyUrl) {
		// 회사 URL로 부서 목록 조회
		return null;
	}


	//답장용 작성을 위한 서비스
	public void sendReply(MessageDTO messageDTO, String uploadPath, MultipartFile upload) throws IOException {

}
}



