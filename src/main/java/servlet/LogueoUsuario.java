package servlet;

import dao.UsuarioJpaController;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Piero
 */
@WebServlet(name = "LogueoUsuario", urlPatterns = {"/logueousuario"})
public class LogueoUsuario extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Cripto006_war_1.0-SNAPSHOTPU");
            UsuarioJpaController usuDAO = new UsuarioJpaController(emf);

            boolean b = usuDAO.validar(user, pass);

            if (b) {

                HttpSession sesion = request.getSession();
                sesion.setAttribute("usuario", user);
                String token = util.JwtUtil.generarToken(user);

                // Respuesta en formato JSON
                out.println("{\"resultado\":\"ok\",\"token\":\"" + token + "\"}");
            } else {
                out.println("{\"resultado\":\"error\"}");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
