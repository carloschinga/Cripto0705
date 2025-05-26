package servlet_kevin_multifactor;

import dao.UsuariosJpaController;
import dto.Usuarios;
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

@WebServlet(name = "loginValidarUsuario", urlPatterns = {"/loginValidarUsuario"})
public class loginValidarUsuario extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String usua = request.getParameter("user");
            String pass = request.getParameter("pass");

            try {
                UsuariosJpaController usuDAO = new UsuariosJpaController();
                Usuarios u = usuDAO.validarUsuario(usua, pass);

                JSONObject miJson = new JSONObject();

                if (u == null) {
                    miJson.put("resultado", "error");
                    miJson.put("mensaje", "Usuario no encontrado");
                } else {
                    //Generamos el token al login del usuario
                    String token = JwtUtil.generarToken(u.getLogiUsua());

                    miJson.put("idUsuario", u.getCodiUsua());
                    miJson.put("nombre", u.getLogiUsua());
                    miJson.put("token", token);

                    if (!u.getMfaEstado()) {
                        // MFA no configurado, enviar para configurar
                        miJson.put("resultado", "mfa_config");
                    } else {
                        // MFA activado, pedir segundo paso
                        miJson.put("resultado", "mfa_required");
                    }
                }
                out.print(miJson.toString());
            } catch (Exception ex) {
                response.getWriter().print("{\"resultado\":\"errorServidor\", \"mensaje\":\"" + ex.getMessage() + "\"}");
            }
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
