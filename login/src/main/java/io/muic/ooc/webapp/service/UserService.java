package io.muic.ooc.webapp.service;

import io.muic.ooc.webapp.model.User;
import org.mindrot.jbcrypt.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final String INSERT_USER_SQL = "INSERT INTO user_table (username, password, display_name) VaLUES (?, ?, ?);";
    private static final String SELECT_USER_SQL = "SELECT * FROM user_table WHERE username = ?;";
    private static final String SELECT_ALL_USER_SQL = "SELECT * FROM user_table;";
    private static final String DELETE_USER_SQL = "DELETE FROM user_table WHERE username = ?;";
    private static final String UPDATE_USER_SQL = "UPDATE user_table SET display_name = ? WHERE username = ?;";
    private static final String UPDATE_USER_PASSWORD_SQL = "UPDATE user_table SET password = ? WHERE username = ?;";

    private DatabaseConnectionService databaseConnectionService;
    private static UserService service;

    public static UserService getInstance() {
        if(service == null){
            service = new UserService();
            service.setDatabaseConnectionService(DatabaseConnectionService.getInstance());
        }
        return service;
    }

    public void setService(UserService service) {
        this.service = service;
    }

    public void setDatabaseConnectionService(DatabaseConnectionService databaseConnectionService){
        this.databaseConnectionService = databaseConnectionService;
    }


    //create new User
    public void createUser(String username, String password, String displayName) throws UserServiceException {
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL);
                ){
            ps.setString(1,username);
            ps.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setString(3,displayName);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e){
            throw new UserNameNotUniqueException(String.format("Username %s already exist.",username));
        } catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }

    public User findByUsername(String username){
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_USER_SQL);
            ){
            ps.setString(1,username);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return new User(
              resultSet.getLong("id"),
              resultSet.getString("username"),
              resultSet.getString("password"),
              resultSet.getString("display_name")
            );

        } catch (SQLException throwables) {
            return null;
        }
    }

    //List all the user in the database
    public List<User> findAll(){
        List<User> users = new ArrayList<>();
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_USER_SQL);
                ){
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                users.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("display_name")));
            }
        } catch (SQLException throwables) {
            return null;
        }
        return users;
    }

    public boolean deleteUserByUsername(String username){
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(DELETE_USER_SQL);
        ){
            ps.setString(1,username);
            int deleteCount = ps.executeUpdate();
            connection.commit();
            return deleteCount > 0;
        } catch (SQLException throwables) {
            return false;
        }
    }

    public void updateUserByUsername(String username, String displayname) throws UserServiceException {
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL);
        ) {
            ps.setString(1, displayname);
            ps.setString(2, username);
            ps.executeUpdate();
            connection.commit();
        }catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        }

    }
    public void changePassword(String username, String newPassword) throws UserServiceException {
        try(
                Connection connection = databaseConnectionService.getConnection();
                PreparedStatement ps = connection.prepareStatement(UPDATE_USER_PASSWORD_SQL);
        ) {
            ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            ps.setString(2, username);
            ps.executeUpdate();
            connection.commit();
        }catch (SQLException throwables) {
            throw new UserServiceException(throwables.getMessage());
        }
    }


    public static void main(String[] args) throws UserServiceException {
        UserService userService = UserService.getInstance();
        userService.setDatabaseConnectionService(DatabaseConnectionService.getInstance());
        userService.createUser("ok","ok","jsDelete");
    }
}
