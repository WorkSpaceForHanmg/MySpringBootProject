//StudentService 클래스
package com.basic.myspringboot.service;

import com.basic.myspringboot.controller.dto.StudentDTO;
import com.basic.myspringboot.entity.Student;
import com.basic.myspringboot.entity.StudentDetail;
import com.basic.myspringboot.exception.BusinessException;
import com.basic.myspringboot.exception.ErrorCode;
import com.basic.myspringboot.repository.StudentDetailRepository;
import com.basic.myspringboot.repository.StudentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDetailRepository studentDetailRepository;

    public List<StudentDTO.Response> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public StudentDTO.Response getStudentById(Long id) {
        Student student = studentRepository.findByIdWithStudentDetail(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "id", id));
        return StudentDTO.Response.fromEntity(student);
    }

    public StudentDTO.Response getStudentByStudentNumber(String studentNumber) {
        Student student = studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "student number", studentNumber));
        return StudentDTO.Response.fromEntity(student);
    }

    @Transactional
    public StudentDTO.Response createStudent(StudentDTO.Request request) {
        // Validate student number is not already in use
        if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new BusinessException(ErrorCode.STUDENT_NUMBER_DUPLICATE,
                    request.getStudentNumber());
        }

        // Validate email is not already in use (if provided)
        if (hasEmailAndExists(request.getDetailRequest())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATE,
                    request.getDetailRequest().getEmail());
        }

        // Validate phone number is not already in use
        if (hasDetailAndPhoneNumberExists(request.getDetailRequest())) {
            throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATE,
                    request.getDetailRequest().getPhoneNumber());
        }

        // Create student entity 생성
        Student student = Student.builder()
                .name(request.getName())   //이름
                .studentNumber(request.getStudentNumber()) //학번
                .build();

        // Create StudentDetail 엔티티 생성
        if (request.getDetailRequest() != null) {
            StudentDetail studentDetail = StudentDetail.builder()
                    .address(request.getDetailRequest().getAddress())  //주소
                    .phoneNumber(request.getDetailRequest().getPhoneNumber()) //전화번호
                    .email(request.getDetailRequest().getEmail()) // 이메일
                    .dateOfBirth(request.getDetailRequest().getDateOfBirth()) //생년월일
                    //생성하는 StudentDetail과 연관된 Student 엔티티를 저장
                    .student(student) 
                    .build();

            student.setStudentDetail(studentDetail);
        }

        // Save and return the student
        Student savedStudent = studentRepository.save(student);
        return StudentDTO.Response.fromEntity(savedStudent);
    }

    @Transactional
    public StudentDTO.Response updateStudent(Long id, StudentDTO.Request request) {
        // Find the student
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Student", "id", id));

        // Check if another student already has the student number
        if (!student.getStudentNumber().equals(request.getStudentNumber()) &&
                studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new BusinessException(ErrorCode.STUDENT_NUMBER_DUPLICATE,
                    request.getStudentNumber());
        }

        // Update student basic info
        student.setName(request.getName());
        student.setStudentNumber(request.getStudentNumber());

        // Update student detail if provided
        if (request.getDetailRequest() != null) {
            StudentDetail studentDetail = student.getStudentDetail();

            // Create new detail if not exists
            if (studentDetail == null) {
                studentDetail = new StudentDetail();
                studentDetail.setStudent(student);
                student.setStudentDetail(studentDetail);
            }

            // Validate email is not already in use (if changing)
            if (isEmailChangingAndExists(studentDetail, request.getDetailRequest())) {
                throw new BusinessException(ErrorCode.EMAIL_DUPLICATE,
                        request.getDetailRequest().getEmail());
            }

            // Validate phone number is not already in use (if changing)
            if (isPhoneNumberChangingAndExists(studentDetail, request.getDetailRequest())) {
                throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATE,
                        request.getDetailRequest().getPhoneNumber());
            }

            // Update detail fields
            studentDetail.setAddress(request.getDetailRequest().getAddress());
            studentDetail.setPhoneNumber(request.getDetailRequest().getPhoneNumber());
            studentDetail.setEmail(request.getDetailRequest().getEmail());
            studentDetail.setDateOfBirth(request.getDetailRequest().getDateOfBirth());
        }

        // Save and return updated student
        Student updatedStudent = studentRepository.save(student);
        return StudentDTO.Response.fromEntity(updatedStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Student", "id", id);
        }
        studentRepository.deleteById(id);
    }

    // Helper methods to improve readability and reduce duplication

    private boolean hasEmailAndExists(StudentDTO.StudentDetailDTO detailRequest) {
        return detailRequest != null &&
                detailRequest.getEmail() != null &&
                !detailRequest.getEmail().isEmpty() &&
                studentDetailRepository.existsByEmail(detailRequest.getEmail());
    }

    private boolean hasDetailAndPhoneNumberExists(StudentDTO.StudentDetailDTO detailRequest) {
        return detailRequest != null &&
                studentDetailRepository.existsByPhoneNumber(detailRequest.getPhoneNumber());
    }

    private boolean isEmailChangingAndExists(StudentDetail currentDetail,
                                             StudentDTO.StudentDetailDTO newDetail) {
        return newDetail.getEmail() != null &&
                !newDetail.getEmail().isEmpty() &&
                (currentDetail.getEmail() == null ||
                        !currentDetail.getEmail().equals(newDetail.getEmail())) &&
                studentDetailRepository.existsByEmail(newDetail.getEmail());
    }

    private boolean isPhoneNumberChangingAndExists(StudentDetail currentDetail,
                                                   StudentDTO.StudentDetailDTO newDetail) {
        return (currentDetail.getPhoneNumber() == null ||
                !currentDetail.getPhoneNumber().equals(newDetail.getPhoneNumber())) &&
                studentDetailRepository.existsByPhoneNumber(newDetail.getPhoneNumber());
    }
}