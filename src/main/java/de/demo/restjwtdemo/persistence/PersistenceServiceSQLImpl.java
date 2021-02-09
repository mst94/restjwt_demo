package de.demo.restjwtdemo.persistence;

import de.demo.restjwtdemo.model.SQLRoles;
import de.demo.restjwtdemo.model.UserModel;
import de.demo.restjwtdemo.model.UserRolesEnum;
import de.demo.restjwtdemo.model.UserRolesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public boolean createUser(final UserModel user) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            // toDo: Validate input (size etc.) like boolean valid = string.matches("[A-Za-z0-9]{5,}");
            // doing validation in front AND backend offers more security

            // create entry in user table
            preparedStatement = connect.prepareStatement("INSERT INTO user (id, login, password, fname, " +
                    "lname, email) VALUES (default, ?, ?, ?, ? , ?)");
            preparedStatement = prepareUser(preparedStatement, user);
            preparedStatement.executeUpdate();

            UserRolesModel roles = user.getRoles();
            // create roles object if none inserted
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
            preparedStatement = prepareUserRoles(preparedStatement, roles, createdUserId);
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
    public UserModel getUserByUsername(final String username) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT id, login, password FROM user WHERE login = ? ; ");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new Exception("User not found!");

            UserModel resultUser = new UserModel();
            resultUser.setId(resultSet.getInt("id"));
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
    public boolean updateUserById(final int id, final UserModel toUpdate) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();

            // create user update statement
            // id column is immutable
            preparedStatement = connect.prepareStatement("UPDATE user SET login = ?, password = ?, " +
                    "fname = ?, lname = ?, email = ? WHERE id = ?");
            preparedStatement = prepareUser(preparedStatement, toUpdate);
            preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();

            // create roles update
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("UPDATE role SET user_id = ?, role_admin = ?, " +
                    "role_develop = ?, role_cctld = ?, role_gtld = ?, role_billing = ?, role_registry = ?," +
                    "role_purchase_read = ?, role_purchase_write = ?, role_sale_write = ?, role_sql = ? WHERE " +
                    "user_id = ?");
            preparedStatement = prepareUserRoles(preparedStatement, toUpdate.getRoles(), id);
            preparedStatement.setInt(12, id);
            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw e;
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

    private boolean mapIntToBool(final int val) {
        if (val == 1)
            return true;
        return false;
    }

    private int mapBoolToInt(final boolean value) {
        if (value == true)
            return 1;
        else
            return 0;
    }

    private PreparedStatement prepareUser(PreparedStatement preparedUserStatement, UserModel user) throws SQLException {
        // toDo: deal with trim()
        preparedUserStatement.setString(1, user.getLogin());
        preparedUserStatement.setString(2, passwordEncoder.encode(user.getPassword().trim()));
        preparedUserStatement.setString(3, user.getFname());
        preparedUserStatement.setString(4, user.getLname());
        preparedUserStatement.setString(5, user.getEmail());
        return preparedUserStatement;
    }

    private PreparedStatement prepareUserRoles(PreparedStatement preparedRolesStatement, UserRolesModel roles,
                                               int userId) throws SQLException {
        preparedRolesStatement.setInt(1, userId);
        preparedRolesStatement.setInt(2, mapBoolToInt(roles.isRoleAdmin()));
        preparedRolesStatement.setInt(3, mapBoolToInt(roles.isRoleDevelop()));
        preparedRolesStatement.setInt(4, mapBoolToInt(roles.isRoleCCtl()));
        preparedRolesStatement.setInt(5, mapBoolToInt(roles.isRoleGtld()));
        preparedRolesStatement.setInt(6, mapBoolToInt(roles.isRoleBilling()));
        preparedRolesStatement.setInt(7, mapBoolToInt(roles.isRoleRegistry()));
        preparedRolesStatement.setInt(8, mapBoolToInt(roles.isRolePurchaseRead()));
        preparedRolesStatement.setInt(9, mapBoolToInt(roles.isRolePurchaseWrite()));
        preparedRolesStatement.setInt(10, mapBoolToInt(roles.isRoleSaleWrite()));
        preparedRolesStatement.setInt(11, mapBoolToInt(roles.isRoleSql()));
        return preparedRolesStatement;
    }

    public List<GrantedAuthority> getRolesOfUserByUserId(final int userId) throws SQLException {
        List<GrantedAuthority> list = new ArrayList<>();
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT * FROM role WHERE user_id = ? LIMIT 1 ; ");
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            // compare database columns with roles enum and create authority if field value is 1
            for (UserRolesEnum c : UserRolesEnum.values())  {
                try  {
                   int role = resultSet.getInt(c.toString().toLowerCase());
                    if (role == 1)  {
                        list.add(new SimpleGrantedAuthority(c.toString()));
                        System.out.println("user has this role:" +c.toString());
                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
        return list;
    }
}
