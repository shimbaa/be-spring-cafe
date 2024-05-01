package codesquad.springcafe.repository.user;

import codesquad.springcafe.model.User;
import codesquad.springcafe.model.dto.UserEditData;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    List<User> findAll();

    Optional<User> findByUserId(String userId);

    void editUserInfo(Long id, UserEditData editData);
}
