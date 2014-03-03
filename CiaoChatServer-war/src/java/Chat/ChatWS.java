/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Chat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.Oneway;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author Parvaneh
 */
@WebService(serviceName = "ChatWS")
@Stateless()
public class ChatWS {

    /** This is a sample web service operation */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "AddContact")
    public boolean AddContact(@WebParam(name = "UserID") String UserID, @WebParam(name = "FriendID") String FriendID) {
        boolean ret = false;
        try {
            //TODO write your implementation code here:

            Connection conn = null;
            boolean flag = true;
            String projectID = null;
            Statement stmt;
            PreparedStatement upstmt;
            ResultSet rs = null;


            String user = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conn = (Connection) DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Database connection established");

            // Getting the projectID if not exist building one

            String query = "SELECT *  FROM project";

            stmt = conn.prepareCall(query);
            rs = stmt.executeQuery(query);

            while (rs.next() && flag) {
                if (rs.getString("AdminUserName").equals(UserID)) {
                    flag = false;
                    projectID = rs.getString("ProjectID");
                }
            }
            if (flag) {
                String query2 = "INSERT INTO project (`ProjectID`, `AdminUserName`) VALUES (Null,?)";
                upstmt = conn.prepareStatement(query2);
                upstmt.setString(1, UserID);
                upstmt.executeUpdate();
            }

            stmt = conn.prepareCall(query);
            rs = stmt.executeQuery(query);

            while (rs.next() && flag) {
                if (rs.getString("AdminUserName").equals(UserID)) {
                    flag = false;
                    projectID = rs.getString("ProjectID");
                }

            }




            // Adding to ProjectUsers the Contact    
            String query3 = "INSERT INTO projectusers (`ProjectID`, `ContactUserName`) VALUES (?,?)";
            upstmt = conn.prepareStatement(query3);
            upstmt.setString(1, projectID);
            upstmt.setString(2, FriendID);
            upstmt.executeUpdate();
            ret = true;


        } catch (SQLException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "RemoveContact")
    public boolean RemoveContact(@WebParam(name = "userID") String userID, @WebParam(name = "FriendID") String FriendID) {
        boolean ret = false;
        try {


            Connection conn = null;


            String user = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            try {
                conn = (Connection) DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Database connection established");

            String query = "SELECT * FROM Project,ProjectUsers WHERE Project.ProjectID=ProjectUsers.ProjectID ";
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            PreparedStatement pstmt;
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            ResultSet rs2;
            try {
                while (rs.next()) {
                    try {
                        if (rs.getString("AdminUserName").equalsIgnoreCase(userID) && rs.getString("ContactUserName").equalsIgnoreCase(FriendID)) {

                            String sqlstr = "DELETE FROM ProjectUsers WHERE ContactUserName=? AND ProjectID=?";



                            pstmt = conn.prepareStatement(sqlstr);
                            pstmt.setString(1, FriendID);
                            pstmt.setString(2, rs.getString("ProjectID"));
                            pstmt.executeUpdate();


                            ret = true;


                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }


        } catch (InstantiationException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "requestChat")
    public InetSocketAddress requestChat(@WebParam(name = "ID") String ID) {

        int port = 444;
        String ip = "127.0.0.1";
        try {

            Connection conn = null;

            String user = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conn = (Connection) DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Database connection established");
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }

            String query = "Select * FROM userSessions ";
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                while (rs.next()) {
                    if (rs.getString("username").equalsIgnoreCase(ID)) {
                        ip = rs.getString("IP");
                        port = rs.getInt("Port");
                        System.out.print(port);
                        System.out.print(ip);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new InetSocketAddress(ip, port);

    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "InviteFriend")
    public boolean InviteFriend(@WebParam(name = "UserID") String UserID, @WebParam(name = "name") String name, @WebParam(name = "email") String email) {
        //TODO write your implementation code here:
        return false;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "search")
    public Vector<String> search(@WebParam(name = "ID") String ID, @WebParam(name = "name") String name, @WebParam(name = "email") String email) {
        Vector<String> vec = new Vector<String>();

        try {
            Connection conn = null;


            String username = "t-sattari";
            String user = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            try {
                conn = (Connection) DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Database connection established");

            String query = "SELECT * FROM userPersonalInf";
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                while (rs.next()) {
                    String userName = rs.getString("userName");
                    String fName = rs.getString("FirstName");
                    String LName = rs.getString("LastName");
                    String mail = rs.getString("Email");
                    String result;
                    if (userName.equalsIgnoreCase(ID) || fName.equalsIgnoreCase(name) || LName.equalsIgnoreCase(name) || mail.equalsIgnoreCase(email)) {
                        result = userName + " " + fName + " " + LName + " " + mail;
                        vec.add(result);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }

            return vec;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vec;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getContactlist")
    public Vector getContactlist(@WebParam(name = "userID") String userID) {
        Vector<String> Result = new Vector<String>();
        try {

            Connection conn = null;
            Statement stmt = null;
            Statement stmt2 = null;
            String ProjectID = null;
            ResultSet rs = null;
            boolean flag = true;



            String user = "root";
            String password = "goodinges";
            String url = "jdbc:mysql://localhost:3306/ima-test";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            try {
                conn = (Connection) DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Database connection established");

            String query = "SELECT * FROM Project,ProjectUsers WHERE Project.ProjectID=ProjectUsers.ProjectID ";
            try {
                stmt = conn.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                while (rs.next()) {
                    if (rs.getString("AdminUserName").equals(userID)) {

                        String st = rs.getString("ContactUserName");
                        /*st += "    ";
                        st += GetUserPresence(rs.getString("ContactUserName"));*/
                        Result.add(st);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }






        } catch (InstantiationException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < Result.size(); i++) {
            System.out.println(Result.elementAt(i));
        }
        return Result;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getIP")
    public String getIP(@WebParam(name = "id") String id) {
        //TODO write your implementation code here:
        return requestChat(id).getAddress().toString().substring(1);
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getPort")
    public int getPort(@WebParam(name = "id") String id) {
        //TODO write your implementation code here:
        return requestChat(id).getPort();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "sendIP")
    @Oneway
    public void sendIP(@WebParam(name = "ip") String ip, @WebParam(name = "id") String id) {

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


            stmt.executeUpdate("UPDATE userSessions SET IP='" + ip + "' WHERE UserName='" + id + "'");

        } catch (SQLException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "sendPort")
    @Oneway
    public void sendPort(@WebParam(name = "port") int port, @WebParam(name = "id") String id) {
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
            String query = "SELECT * FROM userSessions";

            stmt.executeUpdate("UPDATE userSessions SET Port='" + port + "' WHERE UserName='" + id + "'");


            System.out.println(port);
        } catch (SQLException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getProfilePicture")
    public List<Byte> getProfilePicture(@WebParam(name = "userName") String userName) {
        FileInputStream fis = null;
        List<Byte> list = new ArrayList<Byte>();
        try {
            /*InputStream in = null;
            ByteArrayOutputStream bos;
            bos = new ByteArrayOutputStream();
            try {
            URL resource = this.getClass().getResource("/chat/pictures/" + userName + ".jpg");
            System.err.println(resource.getPath());
            in = resource.openStream();
            byte[] buf = new byte[1000000];
            for (int i=0,read; (read = in.read(buf)) != -1;i++) {
            bos.write(buf, 0, read);
            }
            } catch (IOException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
            try {
            in.close();
            } catch (IOException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
             */
            //return bos.toByteArray();
            File f = new File("profilepictures/" + userName + ".jpg");
            System.out.println(f.getAbsolutePath());
            fis = new FileInputStream(f);
            byte[] bytes = new byte[1000000];
            int size = fis.read(bytes);
            for (int i = 0; i < size; i++) {
                list.add(bytes[i]);
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "sendProfilePicture")
    @Oneway
    public void sendProfilePicture(@WebParam(name = "picture") java.util.List<Byte> list,@WebParam(name = "username") String username) {
        try {
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                bytes[i] = list.get(i);
            }
            System.out.println(bytes[list.size()-2]);
            File im = new File("profilepictures/" + username + ".jpg");
            FileOutputStream fos;
           
                fos = new FileOutputStream(im);
                fos.write(bytes);
                fos.flush();
                fos.close();
                System.out.println(im.getAbsolutePath());
                
        } catch (IOException ex) {
            Logger.getLogger(ChatWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
