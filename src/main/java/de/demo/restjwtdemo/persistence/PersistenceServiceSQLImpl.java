package de.demo.restjwtdemo.persistence;

import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.model.UserRolesEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PersistenceServiceSQLImpl implements PersistenceServiceIF {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private DataSource dataSource;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean createUser(final UserModel user) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();

            // create entry in user table
            preparedStatement = connect.prepareStatement("INSERT INTO user (id, login, password, fname, " +
                    "lname, email) VALUES (default, ?, ?, ?, ? , ?)");
            prepareUser(preparedStatement, user);
            preparedStatement.executeUpdate();

            // create roles entry
            List<UserRolesEnum> roles = user.getRoles();
            // create empty roles list if none stated
            if (roles.isEmpty())
                roles = new ArrayList<>();

            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT id FROM user WHERE login = ? ;");
            preparedStatement.setString(1, user.getLogin().trim());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            // get created user id due to auto increment
            int createdUserId = resultSet.getInt("id");

            // create entry in role table
            statement = connect.createStatement();
            StringBuilder insertStatement = new StringBuilder("INSERT INTO role (id, user_id, ");
            UserRolesEnum[] possibleRoles = UserRolesEnum.values();
            for (int i = 0; i < possibleRoles.length; i++) {
                insertStatement.append(possibleRoles[i].toString().toLowerCase());
                if (i != possibleRoles.length - 1)
                    insertStatement.append(",");
            }
            insertStatement.append(") VALUES (default, ?, ");
            for (int z = 0; z < possibleRoles.length; z++) {
                insertStatement.append("?");
                if (z != possibleRoles.length - 1)
                    insertStatement.append(",");
            }
            insertStatement.append(");");
            preparedStatement = connect.prepareStatement(insertStatement.toString());
            preparedStatement.setInt(1, createdUserId);
            prepareUserRoles(preparedStatement, roles, 2);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            close();
        }
    }

    @Override
    public UserModel getUserById(final int id) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT user.id, user.login, user.password, user.fname, " +
                    "user.lname, user.email FROM user WHERE user.id= ? ; ");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new NoSuchElementException("User not found!");

            UserModel resultUser = creaeUserModelFromResultSet(resultSet);

            // be aware of that if no role entry for this user id exists, all roles are set to false!
            // check which role enums are inside list of roles and translate them to new simple granted objects for auth list
            List<GrantedAuthority> roles = getRolesOfUserByUserId(id);
            List<UserRolesEnum> rolesEnum = new ArrayList<>();
            for (UserRolesEnum c : UserRolesEnum.values()) {
                if (roles.contains(new SimpleGrantedAuthority(c.toString()))) {
                    rolesEnum.add(c);
                }
            }
            resultUser.setRoles(rolesEnum);
            return resultUser;
        } finally {
            close();
        }
    }

    @Override
    public UserModel getUserByUsername(final String username) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT id, login, password, fname, lname, email FROM user" +
                    " WHERE login = ? ; ");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new NoSuchElementException("User not found!");

            return creaeUserModelFromResultSet(resultSet);
        } finally {
            close();
        }
    }

    @Override
    public boolean updateUserById(final int id, final UserModel toUpdate) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();

            // create user update statement
            // id column is immutable
            preparedStatement = connect.prepareStatement("UPDATE user SET login = ?, password = ?, " +
                    "fname = ?, lname = ?, email = ? WHERE id = ?");
            prepareUser(preparedStatement, toUpdate);
            preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();

            // create roles update
            statement = connect.createStatement();
            StringBuilder updateStatement = new StringBuilder("UPDATE role SET ");
            UserRolesEnum[] possibleRoles = UserRolesEnum.values();
            for (int i = 0; i < possibleRoles.length; i++) {
                updateStatement.append(possibleRoles[i].toString().toLowerCase());
                updateStatement.append(" = ?");
                if (i != possibleRoles.length - 1)
                    updateStatement.append(",");
            }
            updateStatement.append(" WHERE user_id = ? ;");
            preparedStatement = connect.prepareStatement(updateStatement.toString());
            prepareUserRoles(preparedStatement, toUpdate.getRoles(), 1);
            preparedStatement.setInt(11, id);
            preparedStatement.executeUpdate();
            return true;
        } finally {
            close();
        }
    }

    @Override
    public boolean deleteUserById(final int id) throws Exception {
        try {
            connect = dataSource.getConnection();
            // because ON DELETE CASCADE option is set, role entry of user is automatically deleted as well
            preparedStatement = connect.prepareStatement("DElETE FROM user WHERE id = ? ; ");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } finally {
            close();
        }
        return false;
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // parse result set and fill corresponding user model fields
    private UserModel creaeUserModelFromResultSet(ResultSet result) throws SQLException {
        UserModel resultUser = new UserModel();
        resultUser.setId(resultSet.getInt("id"));
        resultUser.setLogin(resultSet.getString("login"));
        resultUser.setPassword(resultSet.getString("password"));
        resultUser.setFname(resultSet.getString("fname"));
        resultUser.setLname(resultSet.getString("lname"));
        resultUser.setEmail(resultSet.getString("email"));
        return resultUser;
    }

    // fill prepared statement with user data
    private void prepareUser(PreparedStatement preparedUserStatement, UserModel user) throws Exception {
        preparedUserStatement.setString(1, user.getLogin());
        preparedUserStatement.setString(2, passwordEncoder.encode(user.getPassword()));
        preparedUserStatement.setString(3, user.getFname());
        preparedUserStatement.setString(4, user.getLname());
        preparedUserStatement.setString(5, user.getEmail());
    }

    // translate the list of roles enums into the prepared statement for sql
    private void prepareUserRoles(PreparedStatement preparedRolesStatement, List<UserRolesEnum> roles,
                                  int parameterStartIndex) throws SQLException {
        // caution: sql parameter index starts with 1, list index with 0!
        // sql parameter index 1 already used for setting user id
        int index = parameterStartIndex;
        for (UserRolesEnum c : UserRolesEnum.values()) {
            if (roles.contains(c))
                preparedRolesStatement.setInt(index, 1);
            else
                preparedRolesStatement.setInt(index, 0);
            index++;
        }
    }

    // get a list of simple granted authorities roles
    public List<GrantedAuthority> getRolesOfUserByUserId(final int userId) throws Exception {
        List<GrantedAuthority> list = new ArrayList<>();
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT * FROM role WHERE user_id = ? LIMIT 1 ; ");
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            // compare database columns with roles enum and create authority if field value is 1
            for (UserRolesEnum c : UserRolesEnum.values()) {
                try {
                    int role = resultSet.getInt(c.toString().toLowerCase());
                    if (role == 1) {
                        list.add(new SimpleGrantedAuthority(c.toString()));
                        System.out.println("user has this role:" + c.toString());
                    }
                } catch (SQLException e) {
                    throw new Exception("A database error has occurred!");
                }
            }
        } finally {
            close();
        }
        return list;
    }
}
