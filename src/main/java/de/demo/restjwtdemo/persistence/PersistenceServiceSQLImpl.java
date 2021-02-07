package de.demo.restjwtdemo.persistence;

import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.model.UserRolesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

@Service
public class PersistenceServiceSQLImpl implements PersistenceServiceIF {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean createUser(UserModel user) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            // toDo: Validate input (size etc.) like boolean valid = string.matches("[A-Za-z0-9]{5,}");
            // doing validation in front AND backend offers more security

            // create entry in user table
            preparedStatement = connect.prepareStatement("INSERT INTO user (id, login, password, fname, " +
                    "lname, email) VALUES (default, ?, ?, ?, ? , ?)");
            preparedStatement.setString(1, user.getLogin().trim());
            preparedStatement.setString(2, passwordEncoder.encode(user.getPassword().trim()));
            preparedStatement.setString(3, user.getFname().trim());
            preparedStatement.setString(4, user.getLname().trim());
            preparedStatement.setString(5, user.getEmail().trim());
            preparedStatement.executeUpdate();

            UserRolesModel roles = user.getRoles();

            // create roles object if no roles inserted
            if (roles == null)
                roles = new UserRolesModel();

            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT id FROM user WHERE login = ? ;");
            preparedStatement.setString(1, user.getLogin().trim());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int createdUserId = resultSet.getInt("id");

            // create entry in role table
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("INSERT INTO role (id, user_id, role_admin, " +
                    "role_develop, role_cctld, role_gtld, role_billing, role_registry," +
                    "role_purchase_read, role_purchase_write, role_sale_write, role_sql) VALUES (" +
                    "default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, createdUserId);
            preparedStatement.setInt(2, mapBoolToInt(roles.isRoleAdmin()));
            preparedStatement.setInt(3, mapBoolToInt(roles.isRoleDevelop()));
            preparedStatement.setInt(4, mapBoolToInt(roles.isRoleCCtl()));
            preparedStatement.setInt(5, mapBoolToInt(roles.isRoleGtld()));
            preparedStatement.setInt(6, mapBoolToInt(roles.isRoleBilling()));
            preparedStatement.setInt(7, mapBoolToInt(roles.isRoleRegistry()));
            preparedStatement.setInt(8, mapBoolToInt(roles.isRolePurchaseRead()));
            preparedStatement.setInt(9, mapBoolToInt(roles.isRolePurchaseWrite()));
            preparedStatement.setInt(10, mapBoolToInt(roles.isRoleSaleWrite()));
            preparedStatement.setInt(11, mapBoolToInt(roles.isRoleSql()));
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            close();
        }
    }

    @Override
    public UserModel getUserById(int id) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT user.id, user.login, user.password, user.fname, " +
                    "user.lname, user.email, role.role_admin FROM user " +
                    "LEFT JOIN role ON user.id = role.user_id where user.id= ? ; ");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new Exception("User not found!");

            UserModel resultUser = new UserModel();
            resultUser.setId(resultSet.getInt("user.id"));
            resultUser.setLogin(resultSet.getString("user.login"));
            resultUser.setPassword(resultSet.getString("user.password"));
            resultUser.setFname(resultSet.getString("user.fname"));
            resultUser.setLname(resultSet.getString("user.lname"));
            resultUser.setEmail(resultSet.getString("user.email"));

            // be aware of that if no role entry for this user id exists, all roles are set to false!
            UserRolesModel rights = new UserRolesModel();
            rights.setRoleAdmin(mapIntToBool(resultSet.getInt("role.role_admin")));
            resultUser.setRoles(rights);
            return resultUser;
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    @Override
    public UserModel getUserByUsername(String username) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT login, password FROM user where login = ? ; ");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new Exception("User not found!");

            UserModel resultUser = new UserModel();
            resultUser.setLogin(resultSet.getString("login"));
            resultUser.setPassword(resultSet.getString("password"));
            return resultUser;
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    @Override
    public boolean updateUserById(int id) {
        return false;
    }

    @Override
    public boolean deleteUserById(int id) throws Exception {
        try {
            connect = dataSource.getConnection();
            // because ON DELETE CASCADE option is set, role entry of user is automatically deleted as well
            preparedStatement = connect.prepareStatement("DElETE FROM user WHERE id = ? ; ");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
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

    private boolean mapIntToBool(int val) {
        if (val == 1)
            return true;
        return false;
    }

    private int mapBoolToInt(boolean value) {
        if (value == true)
            return 1;
        else
            return 0;
    }

}
