/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Authentication;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ejb.Stateless;

/**
 *
 * @author Kamran
 */
@WebService(serviceName = "AuthenticationWS")
@Stateless()
public class AuthenticationWS {
    @EJB
    private AuthenticationSessionBean ejbRef;// Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Web Service Operation")

    @WebMethod(operationName = "Athenticate")
    public boolean Athenticate(@WebParam(name = "username") String username, @WebParam(name = "pass") char[] pass) {
        return ejbRef.Athenticate(username, pass);
    }

    @WebMethod(operationName = "signup")
    public boolean signup(@WebParam(name = "ID") String ID, @WebParam(name = "pass") String pass, @WebParam(name = "question") int question, @WebParam(name = "answer") String answer, @WebParam(name = "firstName") String firstName, @WebParam(name = "lastName") String lastName, @WebParam(name = "gender") boolean gender, @WebParam(name = "birthday") String birthday, @WebParam(name = "email") String email) {
        return ejbRef.signup(ID, pass, question, answer, firstName, lastName, gender, birthday, email);
    }

    @WebMethod(operationName = "userNameTaken")
    public boolean userNameTaken(@WebParam(name = "username") String username) {
        return ejbRef.userNameTaken(username);
    }

    @WebMethod(operationName = "forgetCred")
    public boolean forgetCred(@WebParam(name = "username") String username){
        return ejbRef.forgetCred(username);
    }

    @WebMethod(operationName = "getUserInfo")
    public String[] getUserInfo(@WebParam(name = "username") String username) {
        return ejbRef.getUserInfo(username);
    }
    
}
