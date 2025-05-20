/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.JwtUtil;

/**
 *
 * @author Piero
 */
@WebServlet(name = "validarGoogle", urlPatterns = {"/validarGoogle"})
public class validarGoogle extends HttpServlet {

   private static final String CLIENT_ID = "681570587240-c5s2m412gc5bclv52jimlcv8se6t7u0l.apps.googleusercontent.com"; // Coloca tu CLIENT_ID aquí

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String credential = request.getParameter("credential");
        HashMap<String, Object> respuesta = new HashMap<>();

        try {
            final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            final GsonFactory gsonFactory = GsonFactory.getDefaultInstance();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String nombre = (String) payload.get("name");

                String tokenJwt = JwtUtil.generarToken(email);

                respuesta.put("resultado", "ok");
                respuesta.put("token", tokenJwt);
                respuesta.put("email", email);
                respuesta.put("nombre", nombre);
            } else {
                respuesta.put("resultado", "error");
                respuesta.put("mensaje", "Token de Google inválido.");
            }

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            respuesta.put("resultado", "error");
            respuesta.put("mensaje", "Error al validar token: " + e.getMessage());
        }

        response.setContentType("application/json");
        response.getWriter().write(new Gson().toJson(respuesta));
    }
}
