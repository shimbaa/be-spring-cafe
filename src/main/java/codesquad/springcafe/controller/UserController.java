package codesquad.springcafe.controller;


import codesquad.springcafe.SessionConst;
import codesquad.springcafe.model.dto.UserLoginData;
import codesquad.springcafe.model.dto.UserProfileData;
import codesquad.springcafe.model.User;
import codesquad.springcafe.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user/add")
    public String userForm() {
        return "user/form";
    }

    @PostMapping("/user/add")
    public String create(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("/users")
    public String userList(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute(users);
        return "user/list";
    }

    @GetMapping("/user/{userId}")
    public String userProfile(@PathVariable String userId, Model model) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserProfileData dto = new UserProfileData();
            dto.setUserId(user.getUserId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            model.addAttribute(dto);
            return "user/profile";
        }
        //todo 해당 아이디의 user 없을 시 예외 처리
        // 누군가 url을 임의로 입력 했을 경우이다
        return null;
    }

    @GetMapping("/user/login")
    public String login() {
        return "user/login";
    }

    @PostMapping("/user/login")
    public String login(@ModelAttribute UserLoginData loginData, @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {
        logger.info("LOGIN TRY USER ID : {}", loginData.getUserId());
        logger.info("LOGIN TRY PASSWORD : {}", loginData.getPassword());

        Optional<User> userOptional = userRepository.findByUserId(loginData.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("FOUND USER INFO DURING LOGIN PROCESS : {}", user);
            String password = loginData.getPassword();
            if (user.isPasswordEquals(password)) {
                //비밀번호가 일치하는 경우
                HttpSession session = request.getSession(true); // 디폴트값이 true
                session.setAttribute(SessionConst.LOGIN_MEMBER, user);

                logger.info("LOGIN SUCCESS USER ID : {}", loginData.getUserId());
                logger.info("LOGIN SUCCESS PASSWORD : {}", loginData.getPassword());
                return "redirect:" + redirectURL;
            }
            return "user/login_failed";
        }
        return "user/login_failed";
    }

    @PostMapping("/user/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/user/edit")
    public String edit() {

        return "user/edit_form";
    }
}