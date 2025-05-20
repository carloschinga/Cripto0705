
package util;

/**
 *
 * @author Piero
 */
public class Main {
    public static void main(String[] args) {
        String usuario = "kike";
        String token = JwtUtil.generarToken(usuario);

        System.out.println("🔐 Token generado:");
        System.out.println(token);

        System.out.println("\n✅ ¿Es válido?");
        System.out.println(JwtUtil.validarToken(token));

        System.out.println("\n👤 Usuario extraído del token:");
        System.out.println(JwtUtil.obtenerUsername(token));
    }
}
