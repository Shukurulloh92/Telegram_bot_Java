package com.company.telegram;

import com.company.enums.Role;
import com.company.model.Users;
import com.company.servise.PdfWriterClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.telegram.Bot.users;

public class SQLServiseBot {
    public static Connection connect() throws SQLException {
        Properties props = new Properties();
        Path myPath = Paths.get("src/main/resources/database.properties");

        try {
            BufferedReader bf = Files.newBufferedReader(myPath,
                    StandardCharsets.UTF_8);

            props.load(bf);
        } catch (IOException ex) {
            Logger.getLogger(SQLServiseBot.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.passwd");

        return DriverManager.getConnection(url, user, password);
    }


    public static ArrayList<String> getSubjects() {
        ArrayList<String> subjectNameList = new ArrayList<>();

        String selectResult = "SELECT name FROM subject ORDER BY id;";

        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(selectResult);
             ResultSet rs = pst.executeQuery()) {


            while (rs.next()) {

                String name = rs.getString(1);
                subjectNameList.add(name);
            }
            return subjectNameList;


        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }


    public static String getQuestion(boolean help) {

        if (users.getCurrentSubject() == null && users.getCurrenTestNumber() == null) {
            return null;
        }

        String sql = "SELECT subject.name, question.id, question.name, " +
                "variant1, variant2, variant3, variant4, correct_variant, ball, conclusion " +
                "FROM question, subject " +
                "where subject_id=subject.id and subject.name = '" + users.getCurrentSubject() + "' " +
//                "and question.id = " + users.getCurrenTestNumber() +
                "ORDER BY question.id " +
                "LIMIT " + users.getCurrenTestNumber() + " OFFSET " + (users.getCurrenTestNumber() - 1) + ";";


        try (Connection con = connect();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String str = "Subject:  " +
                        rs.getString(1) + "\n" +
                        "\nQuestion  " + users.getCurrenTestNumber() +    //(rs.getInt(2));
                        ". " + (rs.getString(3)) + "\n\n";

                str += (rs.getString(4)) + "\n" +
                        (rs.getString(5)) + "\n" +
                        (rs.getString(6)) + "\n" +
                        (rs.getString(7)) + "\n\n";

                if (users.getRole().toString().equals("ADMIN") || help) {
                    String strCorrect = ("Correct variant: ");
                    strCorrect += (rs.getString(8)) + "\n\n";
                    str += strCorrect;
                }

                str += "If you solve this quiz you will get "+ rs.getString(9)+" points \n\n";

                if (users.getRole().toString().equals("ADMIN") || help && null != rs.getString(10)) {
                    str += "Conclusion: " + (rs.getString(10));
                }

                System.out.println("QuestionId: " + rs.getString(2) + ", Correct variant: " + rs.getString(8));
                return str;
            }

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }


    public static String countBall() {

        int i = 0;
        int countCorrectAnswer = 0;
        int usersCountedBall = 0;

        StringBuilder returnText = new StringBuilder("   Subject name: \"")
                .append(users.getCurrentSubject()).append("\"\n");

        String selectResult = "SELECT conclusion, correct_variant, ball, question.id FROM question, subject " +
                "where subject_id=subject.id and subject.name = '" + users.getCurrentSubject() +
                "' ORDER BY id limit " + users.getCurrenTestNumber();


        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(selectResult);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                String conclusion = rs.getString(1);
                String correctAnswer = rs.getString(2);
                int ball = rs.getInt(3);
//                Integer qustionId = rs.getInt(4);

                returnText.append("\n\nQuestion ").append(i + 1).append(".  ");

                if (users.getTheyAnswer().get(i).equals(correctAnswer.substring(0, 1))) {
                    returnText.append("Your answer: \"").append(users.getTheyAnswer().get(i)).append("\" is correct.\n");
                    usersCountedBall += ball;
                    countCorrectAnswer++;
                } else {
                    returnText.append("Your answer: \"").append(users.getTheyAnswer().get(i)).append("\" is incorrect.\n");
                }

                if (users.getRole().toString().equals("ADMIN")) {
                    returnText.append("Conclusion: ").append(conclusion);
                }
                i++;
            }

            users.score += usersCountedBall;

            returnText.append("\n\n\n                                                                   **********")
                    .append("\nYour result:")
                    .append("\nTotal correct answer: ").append(countCorrectAnswer)
                    .append("\nTotal wrong answer: ").append(i - countCorrectAnswer)
                    .append("\nYou scored ").append(usersCountedBall).append(" points")
                    .append("\nYour total score is ").append(users.score)
                    .append("\nMarks obtained: ").append(countCorrectAnswer).append("/").append(i)
                    .append(" (").append(countCorrectAnswer * 100 / i).append("%)");

            Integer certicateId = null;
            if ((countCorrectAnswer * 100 / i) >= 85) {
                certicateId = PdfWriterClass.addNewcerticateId();
                PdfWriterClass.awardWithCertificate(certicateId);
            }

            SQLServiseBot.insertResponse(String.valueOf(returnText), certicateId);

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return String.valueOf(returnText);
    }



    public static ArrayList<Users> getUsers() {
        ArrayList<Users> usersArrayList = new ArrayList<>();

        String selectResult = "SELECT * FROM users ORDER BY id;";

        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(selectResult);
             ResultSet rs = pst.executeQuery()) {


            while (rs.next()) {

                int idd = rs.getInt(1);
                long userId = rs.getLong(2);
                String firstName = rs.getString(3);
                String lastName = rs.getString(4);
                String role = rs.getString(5);
                String phoneNumber = rs.getString(6);
                String currentQuery = rs.getString(7);
                String currentSubject = rs.getString(8);
                Integer currenTestNumber = rs.getInt(9);
                String theyAnswer = rs.getString(10);
                Integer score = rs.getInt(11);


                ArrayList<String> answerList = new ArrayList<>();
                if (theyAnswer != null) {
                    for (int i = 0; i < theyAnswer.length(); i++) {
                        if (theyAnswer.charAt(i) == '1' || theyAnswer.charAt(i) == '2' ||
                                theyAnswer.charAt(i) == '3' || theyAnswer.charAt(i) == '4') {
                            answerList.add(String.valueOf(theyAnswer.charAt(i)));
                        }
                    }
                }

                Users users = new Users(idd, userId, firstName, lastName, Role.valueOf(role), phoneNumber,
                        currentQuery, currentSubject, currenTestNumber, answerList, score);

                usersArrayList.add(users);
            }
            return usersArrayList;


        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }


    public static Users getUser(Long id) {
        Users users = new Users();

        String selectResult = "SELECT * FROM users WHERE user_id = " + id + " ;";

        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(selectResult);
             ResultSet rs = pst.executeQuery()) {


            while (rs.next()) {

                int idd = rs.getInt(1);
                long userId = rs.getLong(2);
                String firstName = rs.getString(3);
                String lastName = rs.getString(4);
                String role = rs.getString(5);
                String phoneNumber = rs.getString(6);
                String currentQuery = rs.getString(7);
                String currentSubject = rs.getString(8);
                Integer currenTestNumber = rs.getInt(9);
                String theyAnswer = rs.getString(10);
                Integer score = rs.getInt(11);

                ArrayList<String> answerList = new ArrayList<>();
                if (theyAnswer != null) {
                    for (int i = 0; i < theyAnswer.length(); i++) {
                        if (theyAnswer.charAt(i) == '1' || theyAnswer.charAt(i) == '2' ||
                                theyAnswer.charAt(i) == '3' || theyAnswer.charAt(i) == '4') {
                            answerList.add(String.valueOf(theyAnswer.charAt(i)));
                        }
                    }
                }
                users.theyAnswer = answerList;

                users = new Users(idd, userId, firstName, lastName, Role.valueOf(role), phoneNumber,
                        currentQuery, currentSubject, currenTestNumber, answerList, score);

            }
            return users;


        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }


    public static String usersResponseHistory() {

        StringBuilder returnText = new StringBuilder();

        String name = users.getFirstName();
        if (users.getLastName() != null) {
            name += " " + users.getLastName();
        }

        String selectResult = "SELECT text, created_date, created_time " +
                "FROM response " +
                "where user_id = " + users.getId() + ";";
//                " AND subject_name = '" + users.getCurrentSubject() +"';";


        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(selectResult);
             ResultSet rs = pst.executeQuery()) {

            returnText.append("   Dear ").append(name).append(" , a list of all your test solutions");

            while (rs.next()) {

                String responseText = rs.getString(1);
                String createdDate = rs.getString(2);
                String createdTime = rs.getString(3);

                returnText.append("\n\n\n").append(responseText)
                        .append("\nDate: ").append(createdDate).append(" ")
                        .append(createdTime, 0, 8);

            }

            return String.valueOf(returnText);

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return null;
    }


    public static ArrayList<Integer> getCertificateIds() {
        ArrayList<Integer> certificateIdsList = new ArrayList<>();

        String selectResult = "SELECT certificate_id_list FROM response;";

        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(selectResult);
             ResultSet rs = pst.executeQuery()) {


            while (rs.next()) {

                Integer id = rs.getInt(1);
                certificateIdsList.add(id);
            }
            return certificateIdsList;


        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static void insertUser() {

        String sql = "INSERT INTO users (user_id, first_name, last_name, role, phone_number)" +
                " VALUES (?, ?, ?, ?, ?);";


        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setLong(1, users.userId);
            pst.setString(2, users.firstName);
            pst.setString(3, users.lastName);
            pst.setString(4, users.role.toString());
            pst.setString(5, users.phoneNumber);
            pst.executeUpdate();

            System.out.println("Insert into: " + users);

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }


    public static void updateUser() {

        String[] array = new String[users.theyAnswer.size()];
        users.theyAnswer.toArray(array);


        String sql = "UPDATE users SET user_id = ?, first_name = ?, last_name = ?, " +
                "role = ?, phone_number = ?, current_query = ?, current_subject = ?" +
                ", current_test_number = ?, they_answer = ?, score = ? " +
                "WHERE user_id = " + users.getUserId() + ";";


        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setLong(1, users.userId);
            pst.setString(2, users.firstName);
            pst.setString(3, users.lastName);
            pst.setString(4, users.role.toString());
            pst.setString(5, users.phoneNumber);
            pst.setString(6, users.currentQuery);
            pst.setString(7, users.currentSubject);
            pst.setInt(8, users.currenTestNumber);
            Array sqlArray = con.createArrayOf("varchar", array);
            pst.setArray(9, sqlArray);
            pst.setInt(10, users.score);
            pst.executeUpdate();
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }


    public static void insertResponse(String text, Integer certicateId) {

        String sql;
        if(certicateId != null){
            sql = "INSERT INTO response (text, user_id, subject_name, certificate_id_list) " +
                    "VALUES (?, ?, ?, ?);";
        }else {
            sql = "INSERT INTO response (text, user_id, subject_name) " +
                    "VALUES (?, ?, ?);";
        }


        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, text);
            pst.setInt(2, users.id);
            pst.setString(3, users.getCurrentSubject());
            if(certicateId != null){
                pst.setInt(4, certicateId);
            }

            pst.executeUpdate();

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

//    public static ArrayList<Integer> getUserCertificateIds() {
    public static HashMap<Integer, String> getUserCertificateIds() {
//        ArrayList<Integer> certificateIdsList = new ArrayList<>();
        HashMap<Integer, String> hashMap = new HashMap<>();

        String selectResult = "SELECT certificate_id_list, subject_name FROM response " +
                "WHERE user_id = " + users.getId() +" ORDER BY id;";

        try (Connection con = SQLServiseBot.connect();
             PreparedStatement pst = con.prepareStatement(selectResult);
             ResultSet rs = pst.executeQuery()) {


            while (rs.next()) {

                int id = rs.getInt(1);
                String name = rs.getString("subject_name");
                if(id != 0 ){
//                    certificateIdsList.add(id);
                    hashMap.put(id, name);
                }

            }
            return hashMap;


        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    SQLServiseBot.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
