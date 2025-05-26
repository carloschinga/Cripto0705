package servlet_kevin_multifactor;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import dao.UsuariosJpaController;
import dto.Usuarios;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import util.JwtUtil;

@WebServlet(name = "mostrarQR", urlPatterns = {"/mostrarQR"})
public class mostrarQR extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        if (token == null || !JwtUtil.validarToken(token)) {
            response.setContentType("application/json");
            JSONObject miJson = new JSONObject();
            miJson.put("resultado", "error");
            miJson.put("mensaje", "Ud. no tiene acceso a esta API");
            try (PrintWriter out = response.getWriter()) {
                out.print(miJson.toString());
            }
            return;
        }

        String user = request.getParameter("user"); // DNI
        UsuariosJpaController usuDAO = new UsuariosJpaController();
        Usuarios usua = usuDAO.findUsuarios(user);

        if (usua == null) {
            response.setContentType("application/json");
            JSONObject miJson = new JSONObject();
            miJson.put("resultado", "error");
            miJson.put("mensaje", "Usuario no encontrado");
            try (PrintWriter out = response.getWriter()) {
                out.print(miJson.toString());
            }
            return;
        }

        String secret = usua.getMfaSecreto();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();

        if (secret == null || secret.isEmpty()) {
            GoogleAuthenticatorKey key = gAuth.createCredentials();
            secret = key.getKey();
            usua.setMfaSecreto(secret);
            usua.setMfaEstado(false); // Aún no activado realmente
            try {
                usuDAO.edit(usua);
            } catch (Exception ex) {
                Logger.getLogger(mostrarQR.class.getName()).log(Level.SEVERE, null, ex);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JSONObject miJson = new JSONObject();
                miJson.put("resultado", "error");
                miJson.put("mensaje", "Error al guardar clave MFA");
                try (PrintWriter out = response.getWriter()) {
                    out.print(miJson.toString());
                }
                return;
            }
        }

        String otpAuthURL = getOtpAuthURL("SistemaKevin3.0", usua.getCodiUsua(), secret);

        try {
            response.setContentType("image/png");
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 150, 150);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", response.getOutputStream());
        } catch (WriterException e) {
            throw new IOException(e);
        }
    }

    private String getOtpAuthURL(String appName, String user, String secret) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                appName, user, secret, appName);
    }

    @Override
    public String getServletInfo() {
        return "Genera código QR para MFA";
    }

}
