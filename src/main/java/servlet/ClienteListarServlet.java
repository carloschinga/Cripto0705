package servlet;

import dto.Cliente;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Piero
 */
@WebServlet(name = "ClienteListarServlet", urlPatterns = {"/clientes"})
public class ClienteListarServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("com.mycompany_Cripto006_war_1.0-SNAPSHOTPU");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        boolean b = util.JwtUtil.validarToken(token);
        EntityManager em = emf.createEntityManager();

        if (b) {
            List<Cliente> clientes = em.createNamedQuery("Cliente.findAll", Cliente.class).getResultList();
            JSONArray jsonArray = new JSONArray();
            for (Cliente c : clientes) {
                JSONObject obj = new JSONObject();
                obj.put("codiClie", c.getCodiClie());
                obj.put("appaClie", c.getAppaClie());
                obj.put("apmaClie", c.getApmaClie());
                obj.put("nombClie", c.getNombClie());
                jsonArray.put(obj);
            }

            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonArray.toString());
            } finally {
                em.close();
            }
        } else {
            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"resultado\":\"error\"}");
            } finally {
                em.close();
            }
        }

    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
    }
}
