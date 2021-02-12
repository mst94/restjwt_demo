package de.demo.restjwtdemo.persistence;

import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.model.UserRolesEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

// this is an approach using the spring jdbc template for database operations
@Primary
@Service
public class PersistenceServiceJdbcTemplateImpl implements PersistenceServiceIF {
    private PasswordEncoder passwordEncoder;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean createUser(UserModel user)  {
        // create parameters for insertion
        Map<String, Object> parametersUser = new HashMap<String, Object>();
        parametersUser.put("id", user.getId());
        parametersUser.put("login", user.getLogin());
        parametersUser.put("password", passwordEncoder.encode(user.getPassword()));
        parametersUser.put("fname", user.getFname());
        parametersUser.put("lname", user.getLname());
        parametersUser.put("email", user.getEmail());

        // do insertion and get back auto increment id
        SimpleJdbcInsert userInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("id");
        int id = userInsert.executeAndReturnKey(parametersUser).intValue();

        // create roles entry
        List<UserRolesEnum> roles = user.getRoles();
        // create empty roles list if none stated
        if (roles.isEmpty())
            roles = new ArrayList<>();

        SimpleJdbcInsert roleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("role")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> parametersRoles = new HashMap<String, Object>();
        parametersRoles.put("user_id", id);

        // check which roles are inside user model list
        for (UserRolesEnum c : UserRolesEnum.values()) {
            if (roles.contains(c))
                parametersRoles.put(c.toString().toLowerCase(), 1);
            else
                parametersRoles.put(c.toString().toLowerCase(), 0);
        }
        roleInsert.execute(parametersRoles);
        return true;
    }

    @Override
    public UserModel getUserById(int id)  {
        UserModelRowMapper mapperUser = new UserModelRowMapper();
        UserRoleEnumRowMapper mapperRole = new UserRoleEnumRowMapper();
        UserModel user;
        try {
            user = jdbcTemplate.queryForObject("SELECT * FROM user WHERE id = ?", mapperUser, id);
            List<UserRolesEnum> roles = jdbcTemplate.queryForObject("SELECT * FROM role WHERE user_id = ?", mapperRole, id);
            user.setRoles(roles);
        } catch (DataAccessException e) {
            throw new NoSuchElementException("User not found!");
        }
        return user;
    }

    @Override
    public UserModel getUserByUsername(String username) throws Exception {
        UserModelRowMapper mapper = new UserModelRowMapper();
        UserRoleEnumRowMapper mapperRole = new UserRoleEnumRowMapper();
        UserModel user;
        try {
            user = jdbcTemplate.queryForObject("SELECT * FROM user WHERE login = ?", mapper, username);
            List<UserRolesEnum> roles = jdbcTemplate.queryForObject("SELECT * FROM role WHERE user_id = ?",
                    mapperRole, user.getId());
            user.setRoles(roles);

        } catch (DataAccessException e) {
            throw new NoSuchElementException("User not found!");
        }
        return user;
    }

    @Override
    public boolean updateUserById(int id, UserModel user) throws Exception {
        jdbcTemplate.update("UPDATE user SET login = ?, password = ?, fname = ?, lname = ? , email = ? WHERE id = ?",
                user.getLogin(), passwordEncoder.encode(user.getPassword()), user.getFname(), user.getLname(),
                user.getEmail(), id);

        // create dynamic update sql statement
        StringBuilder updateStatement = new StringBuilder("UPDATE role SET ");
        UserRolesEnum[] possibleRoles = UserRolesEnum.values();
        for (int i = 0; i < possibleRoles.length; i++) {
            updateStatement.append(possibleRoles[i].toString().toLowerCase());
            updateStatement.append(" = ?");
            if (i != possibleRoles.length - 1)
                updateStatement.append(",");
        }
        updateStatement.append(" WHERE user_id = ? ;");

        // fill the prepared update fields with 1/0 entries
        jdbcTemplate.update(updateStatement.toString(), preparedStatement -> {
            int index = 1;
            for (UserRolesEnum c : UserRolesEnum.values()) {
                if (user.getRoles().contains(c))
                    preparedStatement.setInt(index, 1);
                else
                    preparedStatement.setInt(index, 0);
                index++;
            }
            // set user id als last field
            preparedStatement.setInt(possibleRoles.length + 1, id);
        });
        return true;
    }

    @Override
    public boolean deleteUserById(int id) throws Exception {
        // due to cascading deletion it is enough to delete from user table, role is automatically deleted
        jdbcTemplate.update("DELETE FROM user WHERE id = ?", id);
        return true;
    }

    @Override
    public List<GrantedAuthority> getRolesOfUserByUserId(int userId) throws Exception {
        UserRoleRowMapper mapper = new UserRoleRowMapper();
        List<GrantedAuthority> roles = jdbcTemplate.queryForObject("SELECT * FROM role WHERE user_id = ?", mapper, userId);
        return roles;
    }

    // create user model object after reading from user table
    public static class UserModelRowMapper implements RowMapper<UserModel> {
        @Override
        public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserModel user = new UserModel();
            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            user.setFname(rs.getString("fname"));
            user.setLname(rs.getString("lname"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }

    // create list of granted authorities after reading from role table
    public static class UserRoleRowMapper implements RowMapper<List<GrantedAuthority>> {
        @Override
        public List<GrantedAuthority> mapRow(ResultSet rs, int rowNum) throws SQLException {
            List<GrantedAuthority> auths = new ArrayList<>();
            for (UserRolesEnum c : UserRolesEnum.values()) {
                int role = rs.getInt(c.toString().toLowerCase());
                if (role == 1) {
                    auths.add(new SimpleGrantedAuthority(c.toString()));
                }
            }
            return auths;
        }
    }

    // create list of user role enums after deleting from role table
    public static class UserRoleEnumRowMapper implements RowMapper<List<UserRolesEnum>> {
        @Override
        public List<UserRolesEnum> mapRow(ResultSet rs, int rowNum) throws SQLException {
            List<UserRolesEnum> roles = new ArrayList<>();
            for (UserRolesEnum c : UserRolesEnum.values()) {
                int role = rs.getInt(c.toString().toLowerCase());
                if (role == 1) {
                    roles.add(c);
                }
            }
            return roles;
        }
    }
}
