/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dto.Usuario;
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
 * @author Piero
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getCodiUsua()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            usuario = em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getCodiUsua();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getCodiUsua();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public boolean validar(String usu, String pass) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Usuario.validar");
            q.setParameter("logiUsua", usu);
            q.setParameter("passUsua", pass);
            List<Usuario> lista = q.getResultList();
            if (lista.size() != 0) {
                return true;
            }
            return false;
        } finally {
            em.close();
        }
    }

    //Método validar Kevin
    public Usuario validarUsuario(String usu, String pas) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Usuario.validar");
            q.setParameter("logiUsua", usu);
            q.setParameter("passUsua", pas);
            Usuario usuario = (Usuario) q.getSingleResult();
            return usuario;
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    //Método cambiar clave Kevin
    public String cambiarClave(Usuario usu, String nuevaClave) {
        EntityManager em = getEntityManager();
        try {
            Usuario uuu = findUsuario(usu.getCodiUsua());
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Cripto006_war_1.0-SNAPSHOTPU");
        UsuarioJpaController usuDAO = new UsuarioJpaController(emf);

        Usuario usu = usuDAO.findUsuario(Integer.parseInt("73252202"));
        if (usu != null) {
            String resultado = usuDAO.cambiarClave(usu, "2910");
            System.out.println(resultado);
        } else {
            System.out.println("Usuario no encontrado");
        }
    }
}
