package servlet_kevin;

import dao.UsuarioJpaController;
import dto.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import util.JwtUtil;

@WebServlet(name = "validarUsuarioKevin", urlPatterns = {"/validarUsuarioKevin"})
public class validarUsuarioKevin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {

            String usua = request.getParameter("user");
            String pass = request.getParameter("pass");

            try {
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Cripto006_war_1.0-SNAPSHOTPU");
                UsuarioJpaController usuDAO = new UsuarioJpaController(emf);
                Usuario u = usuDAO.validarUsuario(usua, pass);

                JSONObject miJson = new JSONObject();

                if (u == null) {
                    miJson.put("resultado", "error");
                    miJson.put("mensaje", "Usuario no encontrado");
                } else {
                    //Generamos el token al login del usuario
                    String token = JwtUtil.generarToken(u.getLogiUsua());

                    miJson.put("resultado", "ok");
                    miJson.put("idUsuario", u.getCodiUsua());
                    miJson.put("nombre", u.getLogiUsua());
                    miJson.put("token", token);
                }
                out.print(miJson.toString());
            } catch (Exception ex) {
                out.print("{\"resultado\":\"errorServidor\", \"mensaje\":\"" + ex.getMessage() + "\"}");
            }

        } catch (Exception e) {
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
