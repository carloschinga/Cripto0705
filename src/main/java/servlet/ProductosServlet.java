/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import dto.Producto;
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
import util.JwtUtil;

/**
 *
 * @author Piero
 */
@WebServlet(name = "ProductosServlet", urlPatterns = {"/productos"})
public class ProductosServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("com.mycompany_Cripto006_war_1.0-SNAPSHOTPU");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        boolean valido = JwtUtil.validarToken(token);
        EntityManager em = emf.createEntityManager();

        if (valido) {
            List<Producto> productos = em.createNamedQuery("Producto.findAll", Producto.class).getResultList();
            JSONArray jsonArray = new JSONArray();

            for (Producto p : productos) {
                JSONObject obj = new JSONObject();
                obj.put("codiProd", p.getCodiProd());
                obj.put("nombProd", p.getNombProd());
                obj.put("precProd", p.getPrecProd());
                obj.put("stockProd", p.getStockProd());
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
