import java.util.ArrayList;
import java.util.Random;

/**
 * Represetacion de una rueda de la maquina tragamonedas
 *
 * @author Olman Alejandro Herrera || Ahmad Mustafayasser Diaz 
 * @version 0.1 (provisional)
 */
public class Wheel {

    private ArrayList<String> symbols;
    private int currentIndex;
    private static Random rnd = new Random();

    public Wheel() {
        symbols = new ArrayList<String>();
        currentIndex = -1;
    }

    /**
     * Inserta un simbolo en la posicion indicada
     * @param pos posicion 
     * @param color color del simbolo
     */
    public void addSymbol(int pos, String color) {
        symbols.add(pos - 1, color);
        if (currentIndex == -1) {
            currentIndex = 0;
        }
    }

    /**
     * Elimina la primera ocurrencia del color indicado.
     * @param color color a eliminar
     * @return true si se elimino algun simbolo
     */
    public boolean delSymbol(String color) {
        boolean removed = symbols.remove(color);
        if (removed && (symbols.isEmpty())) {
            currentIndex = -1;
        } else if (removed && currentIndex >= symbols.size()) {
            currentIndex = symbols.size() - 1;
        }
        return removed;
    }

    /**
     * Fija el simbolo visible de la rueda, si existe entre sus simbolos.
     * @param color color a mostrar
     * @return true si el color existe en la rueda
     */
    public boolean setCurrent(String color) {
        int idx = symbols.indexOf(color);
        if (idx == -1) {
            return false;
        }
        currentIndex = idx;
        return true;
    }

    /**
     * @return el color actualmente visible en la rueda, o null si no hay simbolos
     */
    public String getCurrent() {
        if (currentIndex == -1 || symbols.isEmpty()) {
            return null;
        }
        return symbols.get(currentIndex);
    }

    /**
     * Selecciona al azar un nuevo simbolo visible.
     */
    public void spin() {
        if (!symbols.isEmpty()) {
            currentIndex = rnd.nextInt(symbols.size());
        }
    }

    /**
     * @return los colores de los simbolos de la rueda en orden (desde 1)
     */
    public ArrayList<String> getSymbols() {
        return symbols;
    }

    /**
     * @return cantidad de simbolos en la rueda
     */
    public int size() {
        return symbols.size();
    }
}
