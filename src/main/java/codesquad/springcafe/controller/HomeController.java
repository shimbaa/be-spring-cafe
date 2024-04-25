package codesquad.springcafe.controller;

import codesquad.springcafe.SessionConst;
import codesquad.springcafe.model.Article;
import codesquad.springcafe.model.User;
import codesquad.springcafe.repository.article.ArticleRepository;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class HomeController {

    private final ArticleRepository articleRepository;

    public HomeController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/")
    public String home(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginUser, Model model) {

        //전체 게시글 목록을 전달한다
        List<Article> articles = articleRepository.findAll();
        model.addAttribute(articles);

        //세션에 회원 데이터 없으면 index 반환
        if (loginUser == null) {
            return "index";
        }

        model.addAttribute("user", loginUser);
        return "index";
    }
}
