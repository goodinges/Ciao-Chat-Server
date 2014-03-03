/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Authentication;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.sql.*;

/**
 *
 * @author Kamran
 */
@Stateless
@LocalBean
public class AuthenticationSessionBean {

    public boolean Athenticate(String username, char[] pass) {
        {
            System.out.println("Athenticate method");
            String IP;
            String Port;
            boolean flag = false;
            Connection conn = null;
            ResultSet rs;
            try {
                String userName = "root";
                String password = "goodinges";
                String url = "jdbc:mysql://localhost:3306/ima-test";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection(url, userName, password);
                System.out.println("Database connection established");
                Statement stmt = conn.createStatement();

                String query = "SELECT * FROM users WHERE username = '";
                query += username;
                query += "'";
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    if (rs.getString("Password").equals(String.valueOf(pass))) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }
                if(flag){
                    
                }
                
                
            } catch (Exception e) {
                System.err.println("Cannot connect to database server");
                System.err.println(e.toString());

            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                        System.out.println("Database connection terminated");
                    } catch (Exception e) { /* ignore close errors */ }
                }
            }

            return flag;
        }
    }

    public boolean signup(String ID, String pass, int question, String answer, String firstName, String lastName, boolean gender, String birthday, String email) {
        Connection conn = null;
        System.out.println("signUp method");
        try {
            String userName = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
            System.out.println("Database connection established");
//               getUserNames(conn);
            String query = "INSERT INTO `ima-test`.`users` (`UserName`, `Password`, `FriendlyName`, `SessionTimeOut`) VALUES ('" + ID + "', '" + pass + "', NULL, NULL)";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            query = "INSERT INTO `ima-test`.`userpersonalinf` (`FirstName`, `LastName`, `Gender`, `Birthday`, `Email`, `SecurityQuestion`, `SecurityAnswere`, `UserName`) VALUES ('" + firstName + "', '" + lastName + "', '" + (gender == true ? "m" : "F") + "', '" + new java.sql.Date(100) + "', '" + email + "', '" + question + "', '" + answer + "', '" + ID + "');";
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            query = "INSERT INTO `ima-test`.`userpresence` (`UserName`, `Presence`, `SinceDate`, `message`) VALUES ('"+ID+"', '1','"+ getSentTextTime()+"', NULL)";
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            String IP;
            String Port;
            query = "INSERT INTO `ima-test`.`usersessions` (`UserName`, `StartDate`, `EndDate`, `Port`, `IP`) VALUES ('"+ID+"', NULL, NULL,NULL, NULL)";
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            
            
        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            System.err.println(e.toString());
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection terminated");
                } catch (Exception e) { /* ignore close errors */ }
            }
        }
        sendConfirmationMail(ID, firstName, lastName, email);

        return true;
    }

    public boolean userNameTaken(String username) {
        System.out.println("usernameTaken method");
        Connection conn = null;
        boolean flag = true;

        try {
            String userName = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
//               System.out.println ("Database connection established");

            String query = "SELECT * FROM users where username='" + username + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (!rs.next()) {

                flag = false;
            }



        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            System.err.println(e.toString());

        } finally {
            if (conn != null) {
                try {
                    conn.close();
//                       System.out.println ("Database connection terminated");
                } catch (Exception e) { /* ignore close errors */ }
            }
        }
        return flag;
    }

    public boolean forgetCred(String username) {
        try {
            /**
             *
             * @author Parvaneh
             */
            String host = "smtp.gmail.com";
            String from = "caiochatgroup@gmail.com";
            String pass = "ciaochat";
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true"); // added this line
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            String email = null;
            //Tannaz
            Connection conn = null;
            String userPassword = null, Name = null;

            try {

                String user = "root";
                String password = "goodinges";
                String url = "jdbc:mysql://localhost:3306/ima-test";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Database connection established");
                String query = "SELECT * FROM users,userpersonalinf WHERE users.username = userpersonalinf.username AND users.username = '";
                query += username;
                query += "'";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    userPassword = rs.getObject(2).toString();
                    Name = rs.getObject(5).toString();
                    Name += " ";
                    Name += rs.getObject(6).toString();
                    email = rs.getObject(9).toString();
                }

            } catch (Exception e) {
                System.err.println("Cannot connect to database server");
                System.err.println(e.toString());

            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                        System.out.println("Database connection terminated");
                    } catch (Exception e) { /* ignore close errors */ }
                }
            }

            String[] to = {email}; // added this line
            System.out.println(email);

            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) { // changed from a while loop
                System.out.println(to[i]);
                toAddress[i] = new InternetAddress(to[i]);
            }
            System.out.println(Message.RecipientType.TO);

            for (int i = 0; i < toAddress.length; i++) { // changed from a while loop
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }


            message.setSubject("Ciao!Chat Username/Password Recovery");
            message.setText("Hello " + Name + "\n" + "Your Ciao!Chat ID :" + "\t" + username + "\n" + "Your Ciao!Chat password is :\t"
                    + userPassword + "\nSee you on Ciao!Chat" + "\n\n" + "The Ciao!Chat team");
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException ex) {
            Logger.getLogger(AuthenticationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    private void sendConfirmationMail(String username, String firstName, String lastName, String email) {
        try {
            String host = "smtp.gmail.com";
            String from = "caiochatgroup@gmail.com";
            String pass = "ciaochat";
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true"); // added this line
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");

            String[] to = {email}; // added this line

            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for (int i = 0; i < to.length; i++) { // changed from a while loop
                toAddress[i] = new InternetAddress(to[i]);
            }
            System.out.println(Message.RecipientType.TO);

            for (int i = 0; i < toAddress.length; i++) { // changed from a while loop
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }


            message.setSubject("Welcome to Ciao! Chat ");
            message.setText("Dear " + firstName + " " + lastName + "\n" + "Welcome to Ciao!Chat. " + "\n" + "Your Ciao!Chat ID is : "
                    + username + "\nSee you on Ciao!Chat!\n" + "\n" + "The Ciao!Chat team");
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException ex) {
            Logger.getLogger(AuthenticationSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String[] getUserInfo(String username) {
        System.out.println("getUserInfo");
        Connection conn = null;
        ResultSet rs = null;
        String[] infos = new String[10];
        try {
            String userName = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
//               System.out.println ("Database connection established");

            String query = "SELECT * FROM userpersonalinf where username='" + username + "'";
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            rs.next();
            infos[0] = rs.getString("FirstName");
            infos[1] = rs.getString("LastName");


        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            System.err.println(e.toString());

        } finally {
            if (conn != null) {
                try {
                    conn.close();
//                       System.out.println ("Database connection terminated");
                } catch (Exception e) { /* ignore close errors */ }
            }
        }
        return infos;
    }
     private String getSentTextTime() {
        GregorianCalendar now = new GregorianCalendar();//gets the current date and time 
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DATE) + 1; //add one because January is integer 0 
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        String output = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        return output;

    }
}
