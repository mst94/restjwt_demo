package de.demo.restjwtdemo.persistence;

import de.demo.restjwtdemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

@Service
public class JdbcService {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    @Value("${spring.datasource.url}")
    private String DATASOURCE_URL;

    @Autowired
    private DataSource dataSource;

    public User readUserById(int id) throws Exception {
        try {
            connect = dataSource.getConnection();
            statement = connect.createStatement();
            preparedStatement = connect.prepareStatement("SELECT login FROM user where id= ? ; ");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new Exception("User not found!");

            User resultUser = new User();
            resultUser.setLogin(resultSet.getString("login"));
            return resultUser;
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
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


}
