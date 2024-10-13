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
	private final TeamRepository subdepRepository;
	private final FileRepository fileRepository;

	// 메시지 작성 메서드
	@Transactional
	public void write(MessageDTO messageDTO, String uploadPath, MultipartFile upload, Long companyId) throws IOException {
		// 발신자와 수신자 정보를 조회

		MemberEntity senderEntity = usersRepository.findByMemberId(messageDTO.getSenderId());
		MemberEntity receiverEntity = usersRepository.findByMemberId(messageDTO.getReceiverId());
		// 메시지 엔티티 생성 및 설정
		MessageEntity messageEntity = new MessageEntity();
		// 제목 설정
		messageEntity.setTitle(messageDTO.getTitle());
		// 내용 설정
		messageEntity.setContent(messageDTO.getContent());
		// 수신자 설정
		messageEntity.setReceiver(receiverEntity);
		messageEntity.setSentAt(LocalDateTime.now());
		// 발신자 설정
		messageEntity.setSender(senderEntity);
		// 메시지 읽은 여부 설정(false로)
		messageEntity.setRead(false);

		messageRepository.save(messageEntity);

		Long fileId = null; // 파일 ID 초기화
		Long messageId = messageEntity.getMessageId(); // save 후에 호출해야 합니다


		if (upload != null && !upload.isEmpty()) {
			fileId = saveFileAndGetId(uploadPath, upload, companyId, senderEntity.getMemberId(), messageId);
		}
	}

	@Transactional
	public Long saveFileAndGetId(String uploadPath, MultipartFile upload, Long CompanyId, String memberId, Long messageId) throws IOException {
		// 파일 저장 로직
		String fileName = FileManager.saveFile(uploadPath, upload);
		CompanyEntity companyEntity = companyRepository.findByCompanyId(CompanyId);
		if (companyEntity == null) {
			throw new RuntimeException("Company not found with ID: " + CompanyId);
		}
		MemberEntity senderEntity = usersRepository.findByMemberId(memberId);
		if (senderEntity == null) {
			throw new RuntimeException("Member not found with ID: " + memberId);
		}
		// 파일 정보를 file 테이블에 저장
		FileEntity fileEntity = new FileEntity();
		fileEntity.setCompany(companyEntity);
		fileEntity.setMember(senderEntity);
		fileEntity.setFileName(fileName);
		fileEntity.setFileUrl(uploadPath + "/" + fileName); // 실제 URL 설정
		fileEntity.setFileType(upload.getContentType());
		fileEntity.setFileSize(upload.getSize());
		fileEntity.setUploadedAt(LocalDateTime.now());
		fileEntity.setAssociatedType("MESSAGE");
		fileEntity.setAssociatedId(messageId); // 메시지 ID는 메시지를 저장한 후 가져와야 함

		fileRepository.save(fileEntity); // 파일 저장
		return fileEntity.getFileId(); // 파일 ID 반환
	}

	// 발신한 메시지 목록 조회
	public List<MessageDTO> getsentListAll(String memberId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		Sort sort = Sort.by(Sort.Direction.DESC, "messageId");
		// 모든 메시지 조회
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();


		for (MessageEntity entity : entityList) {
			// 메시지 엔티티를 DTO로 변환하여 발신자 ID가 주어진 memberId와 일치하고 삭제되지 않은 메시지만 필터링
			if (entity.getSender().getMemberId().equals(memberId) && entity.isDeleted() != true) {
				MessageDTO dto = MessageDTO.builder()
						.messageId(entity.getMessageId()) // 메시지 번호 추가
						.senderId(entity.getSender().getMemberId()) // 발신자Id를 추가
						.receiverId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
						.title(entity.getTitle()) // 메시지 제목 추가
						.content(entity.getContent()) // 메시지 내용 추가
						.sentAt(entity.getSentAt()) // 보낸 시간 추가
						.isRead(entity.isRead()) // 읽음 상태 추가
						.build();
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	// 수신한 메시지 목록 조회
	public List<MessageDTO> getreceivedListAll(String memberId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		Sort sort = Sort.by(Sort.Direction.DESC, "messageId");
		// 모든 메시지 조회
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();
		for (MessageEntity entity : entityList) {
			// 메시지 엔티티를 DTO로 변환하여 수신자 ID가 주어진 memberId와 일치하고 삭제되지 않은 메시지만 필터링
			if (entity.getReceiver().getMemberId().equals(memberId) && entity.isDeleted() != true) {
				MessageDTO dto = MessageDTO.builder().messageId(entity.getMessageId()) // 메시지 번호 추가
						.senderId(entity.getSender().getMemberId()) // 발신자Id를 추가
						.receiverId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
						.title(entity.getTitle()) // 메시지 제목 추가
						.content(entity.getContent()) // 메시지 내용 추가
						.sentAt(entity.getSentAt()) // 보낸 시간 추가
						.isRead(entity.isRead()) // 읽음 상태 추가
						.build();

				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	// 임시삭제된 메시지 목록 조회
	public List<MessageDTO> getdeletedListAll(String memberId) {
		// 메시지 번호를 기준으로 내림차순 정렬
		Sort sort = Sort.by(Sort.Direction.DESC, "messageId");
		// 모든 메시지 조회
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();
		for (MessageEntity entity : entityList) {
// 메시지 엔티티를 DTO로 변환하여 발신자 또는 수신자가 주어진 memberId와 일치하고 삭제된 메시지만 필터링
			boolean isSenderOrReceiver = entity.getSender().getMemberId().equals(memberId)
					|| entity.getReceiver().getMemberId().equals(memberId);

			if (isSenderOrReceiver && entity.isDeleted()) {
				MessageDTO dto = MessageDTO.builder().messageId(entity.getMessageId()) // 메시지 번호 추가
						.senderId(entity.getSender().getMemberId()) // 발신자Id를 추가
						.receiverId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
						.title(entity.getTitle()) // 메시지 제목 추가
						.content(entity.getContent()) // 메시지 내용 추가
						.sentAt(entity.getSentAt()) // 보낸 시간 추가
						.isRead(entity.isRead()) // 읽음 상태 추가
						.build();

				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	// MessageEntity를 MessageDTO로 변환
	private MessageDTO convertToDTO(MessageEntity entity) {
		return MessageDTO.builder()
				.messageId(entity.getMessageId()) //메세지 번호 추가
				.senderId(entity.getSender().getMemberId()) // 발신자Id를 추가
				.receiverId(entity.getReceiver().getMemberId()) // 수신자Id를 추가
				.title(entity.getTitle()) // 메시지 제목 추가
				.content(entity.getContent()) // 메시지 내용 추가
				.sentAt(entity.getSentAt()) // 보낸 시간 추가
				.build();
	}


	// 메시지 읽기
	public MessageDTO getBoard(Long messageId) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity entity = messageRepository.findByMessageId(messageId);
		// 조회된 엔티티를 DTO로 변환하여 반환
		MessageDTO dto = convertToDTO(entity);
		return dto;
	}

	// 메시지 읽음 상태 업데이트
	public void updateReadStatus(Long messageId) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageId)
				.orElseThrow(() -> new RuntimeException("Message not found"));
		// 메시지 읽음 상태로 변경
		message.setRead(true);
		// 업데이트된 엔티티 저장
		messageRepository.save(message);
	}

	// 메시지 삭제 상태 업데이트(임시 보관함으로 이동시키기 위해)
	public void updateDeleteStatus(Long messageId) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageId)
				.orElseThrow(() -> new RuntimeException("Message not found"));

		// 메시지를 삭제된 상태로 변경하고 삭제 날짜를 현재로 설정
		message.setDeleted(true);  //삭제된 상태로 변경
		message.setDeleteDate(LocalDateTime.now());
		// 업데이트된 엔티티 저장
		messageRepository.save(message);
	}

	// 메시지 복원
	public void restoreMessage(Long messageId) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageId)
				.orElseThrow(() -> new RuntimeException("Message not found"));

		// 메시지 삭제 상태를 복원
		message.setDeleted(false);  // 삭제되지 않은 상태로 변경
		message.setDeleteDate(null);
		// 업데이트된 엔티티 저장
		messageRepository.save(message);
	}

	// 메시지 삭제
	public void delete(Long messageId) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		MessageEntity message = messageRepository.findById(messageId)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));
		// 메시지 삭제
		messageRepository.delete(message);
	}
	// 파일 다운로드 기능
	public void download(Integer messageId, HttpServletResponse response, String uploadPath) {
		// 주어진 메시지 번호로 메시지 엔티티 조회
		FileEntity fileEntity = fileRepository.findByAssociatedId(Long.valueOf(messageId))
				.orElseThrow(() -> new EntityNotFoundException("파일이 없습니다."));


		// 원래의 파일명으로 파일 다운로드 설정
		// 원래의 파일명으로 파일 다운로드 설정
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileEntity.getFileName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}


//		String fullPath = uploadPath + "/" + fileEntity.getFileName();
		String fullPath = "D:/tempUpload/" + fileEntity.getFileName();
		log.debug("파일 경로: " + fullPath);

		// 서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력 스트림
		try (FileInputStream filein = new FileInputStream(fullPath);
			 ServletOutputStream fileout = response.getOutputStream()) {

			// Spring의 파일 관련 유틸 이용하여 출력
			FileCopyUtils.copy(filein, fileout);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// 30일이 지난 메시지 삭제
	public void deleteOldMessages() {
		List<MessageEntity> oldMessages = messageRepository.findByDeleteDateBefore(LocalDateTime.now().minusDays(30));
		// 메시지 삭제 처리
		for (MessageEntity message : oldMessages) {
			messageRepository.delete(message);
		}
	}


	private final MemberDetailRepository memberDetailRepository;

	// 부서 선택용 서비스 추가
	public List<DepartmentEntity> getDepartmentsByCompanyId(Long companyId) {
		// 회사 URL로 부서 목록 조회
		return departmentRepository.findByCompany_CompanyId(companyId);
	}

	// 하위부서 선택용 서비스 추가
	public List<TeamEntity> getTeamsByDepartmentId(Long departmentId) {
		// 부서 번호로 하위 부서 목록 조회
		return subdepRepository.findByDepartmentDepartmentId(departmentId);
	}

	// 멤버 선택용 서비스 추가
	public List<MemberDTO> getMembersBySubDep(Long teamId) {
		// 하위 부서 번호로 멤버 목록 조회
		List<MemberDetailEntity> memberDetails = memberDetailRepository.findByTeam_TeamId(teamId);

		//위에서 받은 리스트를 스트림으로 변환
		return memberDetails.stream()
				// 맵 함수를 이용해 스트림의 각 요소를 변환
				.map(detail -> MemberDTO.builder()
						.memberId(detail.getMember().getMemberId())  // 멤버 ID
						.memberName(detail.getMember().getMemberName()) // 멤버 이름
						.email(detail.getMember().getEmail())  // 이메일// 역할 이름
						.build())
				// 변환된 DTO를 리스트로 수집
				.collect(Collectors.toList());
	}

	//답장용 작성을 위한 서비스
	public void sendReply(MessageDTO messageDTO, MultipartFile upload,String uploadPath, Long companyId) throws IOException {
	try {
		// 발신자 ID로 발신자 엔티티 조회
		MemberEntity senderEntity = usersRepository.findByMemberId(messageDTO.getSenderId());
		// 수신자 ID로 수신자 엔티티 조회
		MemberEntity receiverEntity = usersRepository.findByMemberId(messageDTO.getReceiverId());

		// 새로운 메시지 엔티티 생성
		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setTitle(messageDTO.getTitle());  // 제목 설정
		messageEntity.setContent(messageDTO.getContent()); // 내용 설정
		messageEntity.setSender(senderEntity); // 발신자 설정
		messageEntity.setReceiver(receiverEntity);  // 수신자 설정

		messageRepository.save(messageEntity);

		Long fileId = null; // 파일 ID 초기화
		Long messageId = messageEntity.getMessageId(); // save 후에 호출해야 합니다
		//쪽지에 파일이 있을경우
		if (upload != null && !upload.isEmpty()) {
			fileId = saveFileAndGetId(uploadPath, upload, companyId, senderEntity.getMemberId(), messageId);
		}
		// 메시지 엔티티를 저장소에 저장

	} catch (RuntimeException e) {
		// 메시지 저장 중 오류 발생 시 예외 처리
		throw new RuntimeException("메시지 저장 중 오류가 발생했습니다.", e);
	}
}
}



