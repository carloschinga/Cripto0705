package servlet_kevin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.JwtUtil;

/**
 *
 * @author lord_
 */
@WebServlet(name = "validarFacebookKevin", urlPatterns = {"/validarFacebookKevin"})
public class validarFacebookKevin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String jsonString = sb.toString();
        String facebookId = "";
        String nombre = "";

        try {
            JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);
            facebookId = json.get("facebook_id").getAsString();
            nombre = json.get("nombre").getAsString();
        } catch (Exception e) {
            out.println("{\"resultado\":\"error\",\"mensaje\":\"JSON inválido\"}");
            return;
        }

        try {
            // Aquí puedes validar el ID en tu base de datos o registrarlo si es nuevo.
            // Supondremos que lo aceptas directamente.

            // Generar token personalizado
            String token = JwtUtil.generarToken(facebookId);

            // Crear sesión
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", nombre);

            JsonObject respuesta = new JsonObject();
            respuesta.addProperty("resultado", "ok");
            respuesta.addProperty("token", token);
            respuesta.addProperty("nombre", nombre);
            out.println(respuesta.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"resultado\":\"error\",\"mensaje\":\"Excepción interna\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet de logueo con Facebook";
    }
}
