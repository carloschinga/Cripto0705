package servlet_kevin_multifactor;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import dao.UsuariosJpaController;
import dto.Usuarios;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import util.JwtUtil;

@WebServlet(name = "verificarMFACodigo", urlPatterns = {"/verificarMFACodigo"})
public class verificarMFACodigo extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String token = request.getParameter("token");
            if (token == null || !JwtUtil.validarToken(token)) {
                JSONObject miJson = new JSONObject();
                miJson.put("resultado", "error");
                miJson.put("mensaje", "Ud. no tiene acceso a esta API o token inválido");
                out.print(miJson.toString());
                return;
            }

            String user = request.getParameter("user");
            String codigo = request.getParameter("codigo");

            UsuariosJpaController usuDAO = new UsuariosJpaController();
            Usuarios u = usuDAO.findUsuarios(user);

            JSONObject json = new JSONObject();
            if (u == null) {
                json.put("resultado", "error");
                json.put("mensaje", "Usuario no encontrado");
            } else {
                GoogleAuthenticator gAuth = new GoogleAuthenticator();
                String secret = u.getMfaSecreto();

                try {
                    boolean valido = gAuth.authorize(secret, Integer.parseInt(codigo));
                    if (valido) {
//                        // Generar nuevo token JWT al pasar MFA para ejecución de otros
//                        String newToken = JwtUtil.generarToken(u.getLogiUsua());
                        json.put("resultado", "ok");
//                        json.put("newToken", newToken);
                    } else {
                        json.put("resultado", "error");
                        json.put("mensaje", "Código MFA inválido");
                    }
                } catch (NumberFormatException ex) {
                    json.put("resultado", "error");
                    json.put("mensaje", "Código MFA no válido");
                }
            }
            out.print(json.toString());
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
