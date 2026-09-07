import java.util.ArrayList;
import java.util.Random;

/**
 * Representacion de una rueda de la maquina tragamonedas
 *
 * @author Olman Alejandro Herrera || Ahmad Mustafayasser Diaz 
 * @version 0.2
 */
public class Wheel {

    private ArrayList<String> symbols;
    private int currentIndex;
    private boolean locked;
    private static Random rnd = new Random();

    public Wheel() {
        symbols = new ArrayList<String>();
        currentIndex = -1;
        locked = false;
    }

    public void addSymbol(int pos, String color) {
        symbols.add(pos - 1, color);
        if (currentIndex == -1) {
            currentIndex = 0;
        }
    }

    public boolean delSymbol(String color) {
        boolean removed = symbols.remove(color);
        if (removed && (symbols.isEmpty())) {
            currentIndex = -1;
        } else if (removed && currentIndex >= symbols.size()) {
            currentIndex = symbols.size() - 1;
        }
        return removed;
    }

    public boolean setCurrent(String color) {
        int idx = symbols.indexOf(color);
        if (idx == -1) {
            return false;
        }
        currentIndex = idx;
        return true;
    }

    public String getCurrent() {
        if (currentIndex == -1 || symbols.isEmpty()) {
            return null;
        }
        return symbols.get(currentIndex);
    }

    public void spin() {
        if (!symbols.isEmpty() && !locked) {
            currentIndex = rnd.nextInt(symbols.size());
        }
    }
    
    /**
     * Gira la rueda una cantidad de pasos dada. 
     * Soporta pasos negativos para girar en reversa.
     */
    public void spin(int steps) {
        if (!symbols.isEmpty() && !locked) {
            currentIndex = (currentIndex + steps) % symbols.size();
            if (currentIndex < 0) {
                currentIndex += symbols.size();
            }
        }
    }
    
    public void lock() {
        locked = true;
    }
    
    public void unlock() {
        locked = false;
    }
    
    public boolean isLocked() {
        return locked;
    }

    public ArrayList<String> getSymbols() {
        return symbols;
    }

    public int size() {
        return symbols.size();
    }
}