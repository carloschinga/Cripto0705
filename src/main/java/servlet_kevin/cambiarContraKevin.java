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

@WebServlet(name = "cambiarContraKevin", urlPatterns = {"/cambiarContraKevin"})
public class cambiarContraKevin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {

            String token = request.getParameter("token");

            if (JwtUtil.validarToken(token)) {

                String id, contraActual, contraNueva;
                id = request.getParameter("id");
                contraActual = request.getParameter("actual");
                contraNueva = request.getParameter("nueva");

                try {
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Cripto006_war_1.0-SNAPSHOTPU");
                    UsuarioJpaController usuDAO = new UsuarioJpaController(emf);

                    Usuario usu = usuDAO.findUsuario(Integer.parseInt(id));

                    JSONObject miJson = new JSONObject();

                    if (usu != null && usu.getPassUsua().equals(contraActual)) {
                        //Cambiar la contraseña
                        String cambioClave = usuDAO.cambiarClave(usu, contraNueva);
                        if (cambioClave.equals("Clave cambiada")) {
                            miJson.put("resultado", "ok");
                            miJson.put("mensaje", "Se cambió la clave");
                        } else {
                            miJson.put("resultado", "error");
                            miJson.put("mensaje", "No se pudo cambiar la clave");
                        }
                    } else {
                        miJson.put("resultado", "error");
                        miJson.put("mensaje", "Usuario no encontrado o contraseña actual incorrecta");
                    }

                    out.print(miJson.toString());

                } catch (Exception ex) {
                    out.print("{\"resultado\":\"errorServidor\", \"mensaje\":\"" + ex.getMessage() + "\"}");
                }
            } else {
                JSONObject miJson = new JSONObject();
                miJson.put("resultado", "error");
                miJson.put("mensaje", "Ud. no tiene acceso a esta API");
                out.print(miJson.toString());
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
