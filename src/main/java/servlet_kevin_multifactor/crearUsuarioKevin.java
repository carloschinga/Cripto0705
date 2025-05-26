package servlet_kevin_multifactor;

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

@WebServlet(name = "crearUsuarioKevin", urlPatterns = {"/crearUsuarioKevin"})
public class crearUsuarioKevin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String codiUsua, login, contra;
            codiUsua = request.getParameter("codi").trim();
            login = request.getParameter("login").trim();
            contra = request.getParameter("contra").trim();

            //validaciones
            if (codiUsua == null || codiUsua.isEmpty() || !codiUsua.matches("\\d{8}")) {
                JSONObject miJson = new JSONObject();
                miJson.put("resultado", "denegado");
                miJson.put("mensaje", "El DNI debe de ser numérico de 8 dígitos");
                out.print(miJson.toString());
                return;
            }

            if (login == null || login.isEmpty()) {
                JSONObject miJson = new JSONObject();
                miJson.put("resultado", "denegado");
                miJson.put("mensaje", "El usuario o correo no puede estar vacío");
                out.print(miJson.toString());
                return;
            }

            if (contra == null || contra.isEmpty() || contra.length() < 4) {
                JSONObject miJson = new JSONObject();
                miJson.put("resultado", "denegado");
                miJson.put("mensaje", "La contraseña de tener al menos 4 dígitos");
                out.print(miJson.toString());
                return;
            }

            try {
                //Verificamos si el dni ya esta REGISTRADO
                UsuariosJpaController usuDAO = new UsuariosJpaController();
                Usuarios usu = usuDAO.findUsuarios(codiUsua);

                if (usu != null) {
                    JSONObject miJson = new JSONObject();
                    miJson.put("resultado", "denegado");
                    miJson.put("mensaje", "El DNI ya está en uso");
                    out.print(miJson.toString());
                    return;
                }

                //Crear el nuevo usuario
                Usuarios pi = new Usuarios(codiUsua, login, contra, "", false);               
                usuDAO.create(pi);
                
                JSONObject miJson = new JSONObject();
                miJson.put("resultado", "ok");
                out.print(miJson.toString());
            } catch (Exception ex) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("resultado", "error");
                jSONObject.put("mensaje", "Error al crear el usuario: " + ex.getMessage());
                out.print(jSONObject.toString());
            }
        }

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
