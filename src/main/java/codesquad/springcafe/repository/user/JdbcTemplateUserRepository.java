package codesquad.springcafe.repository.user;

import codesquad.springcafe.model.User;
import codesquad.springcafe.model.dto.UserEditData;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTemplateUserRepository implements UserRepository {

    private final Logger logger = LoggerFactory.getLogger(JdbcTemplateUserRepository.class);
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcTemplateUserRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public User save(User user) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(user);
        Number key = jdbcInsert.executeAndReturnKey(param);
        user.setId(key.longValue());
        logger.info("SAVED USER : {}", user);
        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, user_id, password, name, email FROM users";

        return template.query(sql, (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("user_id"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("email")
        ));
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        String sql = "select id, user_id, password, name, email from users where user_id = :userId";
        try {
            Map<String, Object> param = Map.of("userId", userId);
            User user = template.queryForObject(sql, param, userRowMapper());
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getLong("id"),
                rs.getString("user_id"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("email")
        );
    }

    @Override
    public void editUserInfo(Long id, UserEditData editData) {
        String sql = "UPDATE USERS " +
                "SET NAME = :name, EMAIL = :email WHERE ID = :id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("name", editData.getName())
                .addValue("email", editData.getEmail())
                .addValue("id", id);

        template.update(sql, param);
    }
}