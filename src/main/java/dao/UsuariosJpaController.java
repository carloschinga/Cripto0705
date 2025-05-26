package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dto.Usuarios;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author USUARIO
 */
public class UsuariosJpaController implements Serializable {

    public UsuariosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Cripto006_war_1.0-SNAPSHOTPU");

    public UsuariosJpaController() {
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuarios usuarios) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(usuarios);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuarios(usuarios.getCodiUsua()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuarios usuarios) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            usuarios = em.merge(usuarios);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuarios.getCodiUsua();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getCodiUsua();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            em.remove(usuarios);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuarios findUsuarios(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    //Método validar Kevin
    public Usuarios validarUsuario(String usu, String pass) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Usuarios.validarUsuario");
            q.setParameter("logiUsua", usu);
            q.setParameter("passUsua", pass);
            Usuarios usua = (Usuarios) q.getSingleResult();
            return usua;
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    //Método cambiar clave Kevin
    public String cambiarClave(Usuarios usu, String nuevaClave) {
        EntityManager em = getEntityManager();
        try {
            Usuarios uuu = findUsuarios(usu.getCodiUsua());
            if (uuu != null) {
                uuu.setPassUsua(nuevaClave);
                edit(uuu);
                return "Clave cambiada";
            } else {
                return "Usuario no encontrado";
            }
        } catch (Exception ex) {
            return null;
        } finally {
            em.close();
        }
    }

    //Prueba piloto de métodos
    public static void main(String[] args) {
        UsuariosJpaController usuDAO = new UsuariosJpaController();

        String logi = "churre";
        String pass = "2004";
        Usuarios u = usuDAO.validarUsuario(logi, pass);
        if (u == null) {
            System.out.println("Credenciales incorrectos");
        } else {
            System.out.println("Usuario encontrado: ");
            System.out.println("Usuario: " + u.getLogiUsua());
            System.out.println("Contra: " + u.getPassUsua());
        }   

//        Usuarios usu = usuDAO.findUsuarios("73252202");
//        if (usu != null) {
//            String resultado = usuDAO.cambiarClave(usu, "2910");
//            System.out.println(resultado);
//        } else {
//            System.out.println("Usuario no encontrado");
//        }
    }

}
