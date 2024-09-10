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

	public void write(MessageDTO messageDTO, String uploadPath, MultipartFile upload) throws IOException {

		MemberEntity senderEntity = usersRepository.findByMemberId(messageDTO.getSenderUserId());
		MemberEntity receiverEntity = usersRepository.findByMemberId(messageDTO.getReceiverUserId());

		CompanyEntity companyEntity = companyRepository.findByCompanyUrl(messageDTO.getCompanyUrl());
		DepartmentEntity departmentEntity = departmentRepository.findByDepartmentNum(messageDTO.getDepartmentNum());
		SubDepartmentEntity subdepartmentEntity = subdepRepository.findBySubdepNum(messageDTO.getSubdepNum());

		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setTitle(messageDTO.getTitle());
		messageEntity.setContent(messageDTO.getContent());
		messageEntity.setReceiver(receiverEntity);
		messageEntity.setSender(senderEntity);
		messageEntity.setReadChk(false);
		messageEntity.setCompany(companyEntity);
		messageEntity.setDepartment(departmentEntity);
		messageEntity.setSubdepartment(subdepartmentEntity);

		if (upload != null && !upload.isEmpty()) {
			String fileName = FileManager.saveFile(uploadPath, upload);
			messageEntity.setFileName(fileName);
			messageEntity.setOriginalName(upload.getOriginalFilename());
		}

		messageRepository.save(messageEntity);

	}

	public List<MessageDTO> getsentListAll(String userId) {
		Sort sort = Sort.by(Sort.Direction.DESC, "messageNum");
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();

		for (MessageEntity entity : entityList) {
			if (entity.getSender().getMemberId().equals(userId) && entity.isDeleteStatus() != true) {
				MessageDTO dto = MessageDTO.builder().messageNum(entity.getMessageNum()) // 메시지 번호 추가
						.senderUserId(entity.getSender().getMemberId()) // sender의 userId를 추가
						.receiverUserId(entity.getReceiver().getMemberId()) // receiver의 userId를 추가
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

	public List<MessageDTO> getreceivedListAll(String userId) {
		Sort sort = Sort.by(Sort.Direction.DESC, "messageNum");
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();
		for (MessageEntity entity : entityList) {

			if (entity.getReceiver().getMemberId().equals(userId) && entity.isDeleteStatus() != true) {
				MessageDTO dto = MessageDTO.builder().messageNum(entity.getMessageNum()) // 메시지 번호 추가
						.senderUserId(entity.getSender().getMemberId()) // sender의 userId를 추가
						.receiverUserId(entity.getReceiver().getMemberId()) // receiver의 userId를 추가
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

	public List<MessageDTO> getdeletedListAll(String userId) {
		Sort sort = Sort.by(Sort.Direction.DESC, "messageNum");
		List<MessageEntity> entityList = messageRepository.findAll(sort);
		List<MessageDTO> dtoList = new ArrayList<>();
		for (MessageEntity entity : entityList) {

			boolean isSenderOrReceiver = entity.getSender().getMemberId().equals(userId)
					|| entity.getReceiver().getMemberId().equals(userId);

			if (isSenderOrReceiver && entity.isDeleteStatus() == true) {
				MessageDTO dto = MessageDTO.builder().messageNum(entity.getMessageNum()) // 메시지 번호 추가
						.senderUserId(entity.getSender().getMemberId()) // sender의 userId를 추가
						.receiverUserId(entity.getReceiver().getMemberId()) // receiver의 userId를 추가
						.title(entity.getTitle()) // 메시지 제목 추가
						.content(entity.getContent()) // 메시지 내용 추가
						.sentTime(entity.getSentTime()) // 보낸 시간 추가
						.deleteDate(entity.getDeleteDate())
						.readChk(entity.isReadChk()) // 읽음 상태 추가
						.build();

				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	private MessageDTO convertToDTO(MessageEntity entity) {
		return MessageDTO.builder()
				.messageNum(entity.getMessageNum())
				.senderUserId(entity.getSender().getMemberId()) // sender의 userId를 추가
				.receiverUserId(entity.getReceiver().getMemberId()) // receiver의 userId를 추가
				.title(entity.getTitle()) // 메시지 제목 추가
				.content(entity.getContent()) // 메시지 내용 추가
				.sentTime(entity.getSentTime()) // 보낸 시간 추가
				.originalName(entity.getOriginalName())
				.fileName(entity.getFileName())
				.build();
	}


	public MessageDTO getBoard(int messageNum) {
		MessageEntity entity = messageRepository.findByMessageNum(messageNum);

		MessageDTO dto = convertToDTO(entity);
		return dto;
	}


	public void updateReadStatus(int messageNum) {
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new RuntimeException("Message not found"));

		message.setReadChk(true); // 읽음으로 상태 변경
		messageRepository.save(message); // 업데이트된 엔티티 저장
	}

	public void updateDeleteStatus(int messageNum) {
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new RuntimeException("Message not found"));

		message.setDeleteStatus(true);  //삭제된 상태로 변경
		message.setDeleteDate(LocalDateTime.now()); //삭제 시간을 현재로 변경
		messageRepository.save(message); // 업데이트된 엔티티 저장
	}

	public void restoreMessage(int messageNum) {
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new RuntimeException("Message not found"));

		message.setDeleteStatus(false);  //삭제된 상태로 변경
		messageRepository.save(message); // 업데이트된 엔티티 저장
	}

	public void delete(int messageNum) {
		MessageEntity message = messageRepository.findById(messageNum)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));


//		try {
//			fileManager.deleteFile(uploadPath, boardEntity.getFileName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		messageRepository.delete(message);
	}

	public void download(Integer messageNum, HttpServletResponse response, String uploadPath) {
		//전달된 글 번호로 글 정보 조회
		MessageEntity messageEntity = messageRepository.findById(messageNum)
				.orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

		//원래의 파일명
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(messageEntity.getOriginalName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//저장된 파일 경로
		String fullPath = uploadPath + "/" + messageEntity.getFileName();

		//서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
		FileInputStream filein = null;
		ServletOutputStream fileout = null;

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


	public void deleteOldMessages() {
		// 30일이 지난 메시지를 조회
		List<MessageEntity> oldMessages = messageRepository.findByDeleteDateBefore(LocalDateTime.now().minusDays(30));

		// 메시지 삭제 처리
		for (MessageEntity message : oldMessages) {
			messageRepository.delete(message);
		}
	}

	// 부서 선택용 서비스 추가

	private final MemberDetailRepository memberDetailRepository;

	public List<DepartmentEntity> getDepartmentsByCompanyUrl(String companyUrl) {
		return departmentRepository.findByCompany_CompanyUrl(companyUrl);
	}

	public List<SubDepartmentEntity> getSubDepartmentsByDepartmentNum(int departmentNum) {
		return subdepRepository.findByDepartmentDepartmentNum(departmentNum);
	}

	public List<MemberDTO> getMembersBySubDep(int subdepNum) {
		List<MemberDetailEntity> memberDetails = memberDetailRepository.findBySubDepartment_SubdepNum(subdepNum);

		return memberDetails.stream()
				.map(detail -> MemberDTO.builder()
						.memberId(detail.getMember().getMemberId())
						.memberName(detail.getMember().getMemberName())
						.email(detail.getMember().getEmail())
						.rolename(detail.getMember().getRoleName()) // 필드 이름 맞추기
						.build())
				.collect(Collectors.toList());
	}

	//답장용 서비스 추가

	public void sendReply(MessageDTO messageDTO, String uploadPath, MultipartFile upload) throws IOException {
	try {
		MemberEntity senderEntity = usersRepository.findByMemberId(messageDTO.getSenderUserId());
		MemberEntity receiverEntity = usersRepository.findByMemberId(messageDTO.getReceiverUserId());
		CompanyEntity companyEntity = companyRepository.findByCompanyUrl(messageDTO.getCompanyUrl());
		DepartmentEntity departmentEntity = departmentRepository.findByDepartmentNum(messageDTO.getDepartmentNum());
		SubDepartmentEntity subdepartmentEntity = subdepRepository.findBySubdepNum(messageDTO.getSubdepNum());

		MessageEntity messageEntity = new MessageEntity();
		messageEntity.setTitle(messageDTO.getTitle());
		messageEntity.setContent(messageDTO.getContent());
		messageEntity.setSender(senderEntity);
		messageEntity.setReceiver(receiverEntity);
		messageEntity.setCompany(companyEntity);
		messageEntity.setDepartment(departmentEntity);
		messageEntity.setSubdepartment(subdepartmentEntity);

		if (upload != null && !upload.isEmpty()) {
			String fileName = FileManager.saveFile(uploadPath, upload);
			messageEntity.setFileName(fileName);
			messageEntity.setOriginalName(upload.getOriginalFilename());
		}

		messageRepository.save(messageEntity);
	} catch (RuntimeException e) {
		throw new RuntimeException("메시지 저장 중 오류가 발생했습니다.", e);
	}
}
}



