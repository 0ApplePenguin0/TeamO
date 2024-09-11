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
	private final SubDepartmentRepository subdepRepository;

	// 메시지 작성 메서드
	public void write(MessageDTO messageDTO, String uploadPath, MultipartFile upload) throws IOException {
		// 발신자와 수신자 정보를 조회
		MemberEntity senderEntity = usersRepository.findByMemberId(messageDTO.getSenderUserId());
		MemberEntity receiverEntity = usersRepository.findByMemberId(messageDTO.getReceiverUserId());
		// 회사, 부서, 하위 부서 정보 조회
		CompanyEntity companyEntity = companyRepository.findByCompanyUrl(messageDTO.getCompanyUrl());
		DepartmentEntity departmentEntity = departmentRepository.findByDepartmentNum(messageDTO.getDepartmentNum());
		SubDepartmentEntity subdepartmentEntity = subdepRepository.findBySubdepNum(messageDTO.getSubdepNum());
		// 메시지 엔티티 생성 및 설정
		MessageEntity messageEntity = new MessageEntity();
		// 제목 설정
		messageEntity.setTitle(messageDTO.getTitle());
		// 내용 설정
		messageEntity.setContent(messageDTO.getContent());
		// 수신자 설정
		messageEntity.setReceiver(receiverEntity);
		// 발신자 설정
		messageEntity.setSender(senderEntity);
		// 메시지 읽은 여부 설정(false로)
		messageEntity.setReadChk(false);
		// 메시지 회사URL 설정
		messageEntity.setCompany(companyEntity);
		// 메시지 부서 설정
		messageEntity.setDepartment(departmentEntity);
		// 메시지 하위부서 설정
		messageEntity.setSubdepartment(subdepartmentEntity);

		//쪽지에 파일이 있을경우
		if (upload != null && !upload.isEmpty()) {
			//파일을 저장하고 파일 이름을 반환받음
			String fileName = FileManager.saveFile(uploadPath, upload);
			//파일 이름 설정
			messageEntity.setFileName(fileName);
			//원본 파일 이름 설정
			messageEntity.setOriginalName(upload.getOriginalFilename());
		}
		// 메시지 저장
		messageRepository.save(messageEntity);

	}

	// 발신한 메시지 목록 조회
	public List<MessageDTO> getsentListAll(String userId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		Sort sort = Sort.by(Sort.Direction.DESC, "messageNum");
		// 모든 메시지 조회
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();


		for (MessageEntity entity : entityList) {
			// 메시지 엔티티를 DTO로 변환하여 발신자 ID가 주어진 userId와 일치하고 삭제되지 않은 메시지만 필터링
			if (entity.getSender().getMemberId().equals(userId) && entity.isDeleteStatus() != true) {
				MessageDTO dto = MessageDTO.builder()
						.messageNum(entity.getMessageNum()) // 메시지 번호 추가
						.senderUserId(entity.getSender().getMemberId()) // 발신자Id를 추가
						.receiverUserId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
						.title(entity.getTitle()) // 메시지 제목 추가
						.content(entity.getContent()) // 메시지 내용 추가
						.sentTime(entity.getSentTime()) // 보낸 시간 추가
						.readChk(entity.isReadChk()) // 읽음 상태 추가
						.build();
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	// 수신한 메시지 목록 조회
	public List<MessageDTO> getreceivedListAll(String userId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		Sort sort = Sort.by(Sort.Direction.DESC, "messageNum");
		// 모든 메시지 조회
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();
		for (MessageEntity entity : entityList) {
			// 메시지 엔티티를 DTO로 변환하여 수신자 ID가 주어진 userId와 일치하고 삭제되지 않은 메시지만 필터링
			if (entity.getReceiver().getMemberId().equals(userId) && entity.isDeleteStatus() != true) {
				MessageDTO dto = MessageDTO.builder().messageNum(entity.getMessageNum()) // 메시지 번호 추가
						.senderUserId(entity.getSender().getMemberId()) // 발신자Id를 추가
						.receiverUserId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
						.title(entity.getTitle()) // 메시지 제목 추가
						.content(entity.getContent()) // 메시지 내용 추가
						.sentTime(entity.getSentTime()) // 보낸 시간 추가
						.readChk(entity.isReadChk()) // 읽음 상태 추가
						.build();

				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	// 임시삭제된 메시지 목록 조회
	public List<MessageDTO> getdeletedListAll(String userId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		Sort sort = Sort.by(Sort.Direction.DESC, "messageNum");
		// 모든 메시지 조회
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();
		for (MessageEntity entity : entityList) {
// 메시지 엔티티를 DTO로 변환하여 발신자 또는 수신자가 주어진 userId와 일치하고 삭제된 메시지만 필터링
			boolean isSenderOrReceiver = entity.getSender().getMemberId().equals(userId)
					|| entity.getReceiver().getMemberId().equals(userId);

			if (isSenderOrReceiver && entity.isDeleteStatus() == true) {
				MessageDTO dto = MessageDTO.builder().messageNum(entity.getMessageNum()) // 메시지 번호 추가
						.senderUserId(entity.getSender().getMemberId()) // 발신자Id를 추가
						.receiverUserId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
						.title(entity.getTitle()) // 메시지 제목 추가
						.content(entity.getContent()) // 메시지 내용 추가
						.sentTime(entity.getSentTime()) // 보낸 시간 추가
						.deleteDate(entity.getDeleteDate()) // 삭제한 일시
						.readChk(entity.isReadChk()) // 읽음 상태 추가
						.build();

				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	// MessageEntity를 MessageDTO로 변환
	private MessageDTO convertToDTO(MessageEntity entity) {
		return MessageDTO.builder()
				.messageNum(entity.getMessageNum()) //메세지 번호 추가
				.senderUserId(entity.getSender().getMemberId()) // 발신자Id를 추가
				.receiverUserId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
				.title(entity.getTitle()) // 메시지 제목 추가
				.content(entity.getContent()) // 메시지 내용 추가
				.sentTime(entity.getSentTime()) // 보낸 시간 추가
				.originalName(entity.getOriginalName()) //저장된 파일 원본 이름
				.fileName(entity.getFileName()) // 저장된 파일명
				.build();
	}


	// 메시지 읽기
	public MessageDTO getBoard(int messageNum) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity entity = messageRepository.findByMessageNum(messageNum);
		// 조회된 엔티티를 DTO로 변환하여 반환
		MessageDTO dto = convertToDTO(entity);
		return dto;
	}

	// 메시지 읽음 상태 업데이트
	public void updateReadStatus(int messageNum) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new RuntimeException("Message not found"));
		// 메시지 읽음 상태로 변경
		message.setReadChk(true);
		// 업데이트된 엔티티 저장
		messageRepository.save(message);
	}

	// 메시지 삭제 상태 업데이트(임시 보관함으로 이동시키기 위해)
	public void updateDeleteStatus(int messageNum) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new RuntimeException("Message not found"));

		// 메시지를 삭제된 상태로 변경하고 삭제 날짜를 현재로 설정
		message.setDeleteStatus(true);  //삭제된 상태로 변경
		message.setDeleteDate(LocalDateTime.now()); //삭제 시간을 현재로 변경
		// 업데이트된 엔티티 저장
		messageRepository.save(message);
	}

	// 메시지 복원
	public void restoreMessage(int messageNum) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new RuntimeException("Message not found"));

		// 메시지 삭제 상태를 복원
		message.setDeleteStatus(false);  // 삭제되지 않은 상태로 변경
		// 업데이트된 엔티티 저장
		messageRepository.save(message);
	}

	// 메시지 삭제
	public void delete(int messageNum) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
		// 메시지 삭제
		messageRepository.delete(message);
	}
	// 파일 다운로드 기능
	public void download(Integer messageNum, HttpServletResponse response, String uploadPath) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity messageEntity = messageRepository.findById(messageNum)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		// 원래의 파일명으로 파일 다운로드 설정
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(messageEntity.getOriginalName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 저장된 파일 경로 설정
		String fullPath = uploadPath + "/" + messageEntity.getFileName();

		//서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
		FileInputStream filein = null;
		ServletOutputStream fileout = null;

		// 파일 읽기 및 클라이언트에게 전달
		try {
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();

			//Spring의 파일 관련 유틸 이용하여 출력
			FileCopyUtils.copy(filein, fileout);

			filein.close();
			fileout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 30일이 지난 메시지 삭제
	public void deleteOldMessages() {
		// 30일이 지난 메시지를 조회
		List<MessageEntity> oldMessages = messageRepository.findByDeleteDateBefore(LocalDateTime.now().minusDays(30));
		// 메시지 삭제 처리
		for (MessageEntity message : oldMessages) {
			messageRepository.delete(message);
		}
	}


	private final MemberDetailRepository memberDetailRepository;

	// 부서 선택용 서비스 추가
	public List<DepartmentEntity> getDepartmentsByCompanyUrl(String companyUrl) {
		// 회사 URL로 부서 목록 조회
		return departmentRepository.findByCompany_CompanyUrl(companyUrl);
	}

	// 하위부서 선택용 서비스 추가
	public List<SubDepartmentEntity> getSubDepartmentsByDepartmentNum(int departmentNum) {
		// 부서 번호로 하위 부서 목록 조회
		return subdepRepository.findByDepartmentDepartmentNum(departmentNum);
	}

	// 멤버 선택용 서비스 추가
	public List<MemberDTO> getMembersBySubDep(int subdepNum) {
		// 하위 부서 번호로 멤버 목록 조회
		List<MemberDetailEntity> memberDetails = memberDetailRepository.findBySubDepartment_SubdepNum(subdepNum);

		//위에서 받은 리스트를 스트림으로 변환
		return memberDetails.stream()
				// 맵 함수를 이용해 스트림의 각 요소를 변환
				.map(detail -> MemberDTO.builder()
						.memberId(detail.getMember().getMemberId())  // 멤버 ID
						.memberName(detail.getMember().getMemberName()) // 멤버 이름
						.email(detail.getMember().getEmail())  // 이메일
						.rolename(detail.getMember().getRoleName())  // 역할 이름
						.build())
				// 변환된 DTO를 리스트로 수집
				.collect(Collectors.toList());
	}

	//답장용 작성을 위한 서비스
	public void sendReply(MessageDTO messageDTO, String uploadPath, MultipartFile upload) throws IOException {
	try {
		// 발신자 ID로 발신자 엔티티 조회
		MemberEntity senderEntity = usersRepository.findByMemberId(messageDTO.getSenderUserId());
		// 수신자 ID로 수신자 엔티티 조회
		MemberEntity receiverEntity = usersRepository.findByMemberId(messageDTO.getReceiverUserId());
		// 회사 URL로 회사 엔티티 조회
		CompanyEntity companyEntity = companyRepository.findByCompanyUrl(messageDTO.getCompanyUrl());
		// 부서 번호로 부서 엔티티 조회
		DepartmentEntity departmentEntity = departmentRepository.findByDepartmentNum(messageDTO.getDepartmentNum());
		// 하위 부서 번호로 하위 부서 엔티티 조회
		SubDepartmentEntity subdepartmentEntity = subdepRepository.findBySubdepNum(messageDTO.getSubdepNum());

		// 새로운 메시지 엔티티 생성
		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setTitle(messageDTO.getTitle());  // 제목 설정
		messageEntity.setContent(messageDTO.getContent()); // 내용 설정
		messageEntity.setSender(senderEntity); // 발신자 설정
		messageEntity.setReceiver(receiverEntity);  // 수신자 설정
		messageEntity.setCompany(companyEntity);   // 회사URL 설정
		messageEntity.setDepartment(departmentEntity);  // 부서 설정
		messageEntity.setSubdepartment(subdepartmentEntity);  // 하위 부서 설정

		//쪽지에 파일이 있을경우
		if (upload != null && !upload.isEmpty()) {
			//파일을 저장하고 파일 이름을 반환받음
			String fileName = FileManager.saveFile(uploadPath, upload);
			//파일 이름 설정
			messageEntity.setFileName(fileName);
			//원본 파일 이름 설정
			messageEntity.setOriginalName(upload.getOriginalFilename());
		}
		// 메시지 엔티티를 저장소에 저장
		messageRepository.save(messageEntity);
	} catch (RuntimeException e) {
		// 메시지 저장 중 오류 발생 시 예외 처리
		throw new RuntimeException("메시지 저장 중 오류가 발생했습니다.", e);
	}
}
}



