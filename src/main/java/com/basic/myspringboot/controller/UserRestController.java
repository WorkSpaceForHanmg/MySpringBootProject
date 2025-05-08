package com.basic.myspringboot.controller;


import com.basic.myspringboot.entity.User;
import com.basic.myspringboot.exception.BusinessException;
import com.basic.myspringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@Controller + @ResponseBody
@RestController
@RequiredArgsConstructor
//final 인 변수를 초기화하는 생성자를 자동으로 생성해주는 역할을 하는 롬복 어노테이션
@RequestMapping("/api/users")       //
public class UserRestController {
    private final UserRepository userRepository;

    //Constructor Injection
//    public UserRestController(UserRepository userRepository) {
//        System.out.println(">>> UserController " + userRepository.getClass().getName());
//        this.userRepository = userRepository;
//    }                                     <<<주석인 이유는 @RequiredArgsConstructor 덕분에 앞으로 새로 추가 되도 이런 구절이 필요없음

    @PostMapping
    public User create(@RequestBody User user) {
        return userRepository.save(user);   //save 는 등록
    }

    @GetMapping
    public List<User> getUser() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")      //{id} 아이디 값이 바뀔때마다 적용가능 //@PathVariable 로 값을 받아오고
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        //public <U> Optional<U> map(Function<? super T,? extends U> mapper)
        //Function 의 추상메서드 R apply(T t)
        ResponseEntity<User> responseEntity = optionalUser
                .map(user -> ResponseEntity.ok(user))  //Optional<ResponseEntity>
                //.orElse(ResponseEntity.notFound().build());
                .orElse(new ResponseEntity("User Not Found", HttpStatus.NOT_FOUND));  //404와 함께 body문 출력
        return responseEntity;

//        return optionalUser.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/email/{email}/")       //http://localhost:8080/api/users/id/100
    public User getUserByEmail(@PathVariable String email){
        Optional<User> optionalUser =userRepository.findByEmail(email);
        User existUser =
                optionalUser.orElseThrow(() -> new BusinessException("User Not Found",HttpStatus.NOT_FOUND));
        return existUser;
    }


}

