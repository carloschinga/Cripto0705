package dto;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author USUARIO
 */
@Entity
@Table(name = "usuarios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuarios.findAll", query = "SELECT u FROM Usuarios u"),
    @NamedQuery(name = "Usuarios.findByCodiUsua", query = "SELECT u FROM Usuarios u WHERE u.codiUsua = :codiUsua"),
    @NamedQuery(name = "Usuarios.findByLogiUsua", query = "SELECT u FROM Usuarios u WHERE u.logiUsua = :logiUsua"),
    @NamedQuery(name = "Usuarios.findByPassUsua", query = "SELECT u FROM Usuarios u WHERE u.passUsua = :passUsua"),
    @NamedQuery(name = "Usuarios.validarUsuario", query = "SELECT u FROM Usuarios u WHERE u.logiUsua = :logiUsua AND u.passUsua = :passUsua"),
    @NamedQuery(name = "Usuarios.findByMfaSecreto", query = "SELECT u FROM Usuarios u WHERE u.mfaSecreto = :mfaSecreto"),
    @NamedQuery(name = "Usuarios.findByMfaEstado", query = "SELECT u FROM Usuarios u WHERE u.mfaEstado = :mfaEstado")})
public class Usuarios implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "codiUsua")
    private String codiUsua;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "logiUsua")
    private String logiUsua;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "passUsua")
    private String passUsua;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "mfaSecreto")
    private String mfaSecreto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mfaEstado")
    private boolean mfaEstado;

    public Usuarios() {
    }

    public Usuarios(String codiUsua) {
        this.codiUsua = codiUsua;
    }

    public Usuarios(String codiUsua, String logiUsua, String passUsua, String mfaSecreto, boolean mfaEstado) {
        this.codiUsua = codiUsua;
        this.logiUsua = logiUsua;
        this.passUsua = passUsua;
        this.mfaSecreto = mfaSecreto;
        this.mfaEstado = mfaEstado;
    }

    public String getCodiUsua() {
        return codiUsua;
    }

    public void setCodiUsua(String codiUsua) {
        this.codiUsua = codiUsua;
    }

    public String getLogiUsua() {
        return logiUsua;
    }

    public void setLogiUsua(String logiUsua) {
        this.logiUsua = logiUsua;
    }

    public String getPassUsua() {
        return passUsua;
    }

    public void setPassUsua(String passUsua) {
        this.passUsua = passUsua;
    }

    public String getMfaSecreto() {
        return mfaSecreto;
    }

    public void setMfaSecreto(String mfaSecreto) {
        this.mfaSecreto = mfaSecreto;
    }

    public boolean getMfaEstado() {
        return mfaEstado;
    }

    public void setMfaEstado(boolean mfaEstado) {
        this.mfaEstado = mfaEstado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codiUsua != null ? codiUsua.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuarios)) {
            return false;
        }
        Usuarios other = (Usuarios) object;
        if ((this.codiUsua == null && other.codiUsua != null) || (this.codiUsua != null && !this.codiUsua.equals(other.codiUsua))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "dto.Usuarios[ codiUsua=" + codiUsua + " ]";
    }

}
