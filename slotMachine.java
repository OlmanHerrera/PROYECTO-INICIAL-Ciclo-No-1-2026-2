import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Simulador de una maquina tragamonedas.
 * @author Olman Alejandro Herrera  || Ahmad Mustafayasser Diaz
 * @version 1.1
 */
public class slotMachine {

    private ArrayList<Wheel> wheels;
    private SlotMachineView view;
    private boolean visible;
    private boolean ok;

    /**
     * Crea una maquina tragamonedas sin ruedas, visible por defecto.
     */
    public slotMachine() {
        wheels = new ArrayList<Wheel>();
        view = new SlotMachineView();
        visible = true;
        ok = true;
        view.makeVisible();
    }

    /**
     * Adiciona una rueda vacia en la posicion indicada.
     * Si pos es menor a 1 se usa 1; si es mayor al maximo permitido
     * (cantidad actual de ruedas + 1) se usa ese maximo.
     * @param pos posicion donde insertar la rueda (1-normalizado (based))
     */
    public void addWheel(int pos) {
        int index = normalize(pos, wheels.size() + 1);
        wheels.add(index - 1, new Wheel());
        ok = true;
        refreshView();
    }

    /**
     * Elimina la rueda en la posicion indicada.
     * @param pos posicion de la rueda a eliminar
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            return;
        }
        int index = normalize(pos, wheels.size());
        wheels.remove(index - 1);
        ok = true;
        refreshView();
    }

    /**
     * Adiciona un simbolo del color indicado, en la posicion pos, en
     * todas las ruedas de la maquina.
     * @param pos posicion dentro de cada rueda 
     * @param color color del simbolo
     */
    public void addSymbol(int pos, String color) {
        if (wheels.isEmpty()) {
            return;
        }
        for (Wheel w : wheels) {
            int index = normalize(pos, w.size() + 1);
            w.addSymbol(index, color);
        }
        ok = true;
        refreshView();
    }

    /**
     * Elimina el simbolo del color indicado de todas las ruedas donde exista.
     * @param symbol color del simbolo a eliminar
     */
    public void delSymbol(String symbol) {
        boolean removedAny = false;
        for (Wheel w : wheels) {
            if (w.delSymbol(symbol)) {
                removedAny = true;
            }
        }
        if (!removedAny) {
            return;
        }
        ok = true;
        refreshView();
    }

    /**
     * Ubica manualmente un simbolo como el visible en una rueda especifica.
     * @param wheel indice de la rueda
     * @param symbol color CSS del simbolo a mostrar
     */
    public void placeSymbol(int wheel, String symbol) {
        if (!validWheel(wheel)) {
            return;
        }
        Wheel w = wheels.get(wheel - 1);
        if (!w.setCurrent(symbol)) {
            return;
        }
        ok = true;
        refreshView();
    }

    /**
     * Gira una rueda especifica.
     * @param wheel indice de la rueda (1-based)
     */
    public void spin(int wheel) {
        if (!validWheel(wheel)) {
            return;
        }
        view.spinEffect(wheel - 1);
        wheels.get(wheel - 1).spin();
        ok = true;
        refreshView();
    }

    /**
     * Gira todas las ruedas de la maquina.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            return;
        }
        view.spinEffectAll();
        for (Wheel w : wheels) {
            w.spin();
        }
        ok = true;
        refreshView();
    }

    /**
     * Retorna todos los colores de los simbolos de la maquina, rueda por
     * rueda, en el orden en que estan definidos dentro de cada una (desde 1).
     * @return arreglo con los colores de los simbolos
     */
    public String[] symbols() {
        ArrayList<String> all = new ArrayList<String>();
        for (Wheel w : wheels) {
            all.addAll(w.getSymbols());
        }
        ok = true;
        return all.toArray(new String[0]);
    }

    /**
     * Cuenta la cantidad de colores distintos entre todos los simbolos
     * de la maquina.
     * @return numero de simbolos distintos
     */
    public int distinctSymbols() {
        ArrayList<String> distinct = new ArrayList<String>();
        for (String color : symbols()) {
            if (!distinct.contains(color)) {
                distinct.add(color);
            }
        }
        ok = true;
        return distinct.size();
    }

    /**
     * Retorna el color visible actualmente en cada rueda, ordenados
     * de izquierda a derecha.
     * @return arreglo con la configuracion actual de la maquina
     */
    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).getCurrent();
        }
        ok = true;
        return config;
    }

    /**
     * Indica si la configuracion actual es ganadora, es decir, si todos
     * los simbolos visibles en las ruedas son del mismo color.
     * @return true si la configuracion actual es jackpot
     */
    public boolean isJackpot() {
        String[] config = configuration();
        ok = true;
        if (config.length == 0) {
            return false;
        }
        for (String color : config) {
            if (color == null || !color.equals(config[0])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Hace visible el simulador (habilita los mensajes emergentes y el dibujo).
     */
    public void makeVisible() {
        visible = true;
        view.makeVisible();
        refreshView();
        ok = true;
    }

    /**
     * Hace invisible el simulador; sigue funcionando pero sin mostrar
     * mensajes ni dibujo en pantalla.
     */
    public void makeInvisible() {
        visible = false;
        view.makeInvisible();
        ok = true;
    }

    /**
     * Termina el simulador.
     */
    public void exit() {
        view.makeInvisible();
        visible = false;
        ok = true;
    }

    /**
     * Indica si la ultima operacion realizada fue exitosa.
     * @return true si la ultima operacion se logro realizar
     */
    public boolean ok() {
        return ok;
    }
    /**
     * Ajusta una posicion 1-based al rango valido [1, max].
     * @param pos posicion solicitada
     * @param max valor maximo permitido
     * @return posicion normalizada
     */
    private int normalize(int pos, int max) {
        if (pos < 1) { 
            return 1;
        }
        if (pos > max) {
            return max;
        }
        return pos;
    }

    private boolean validWheel(int wheel) {
        return wheel >= 1 && wheel <= wheels.size();
    }

    /**
     * Actualiza el dibujo de la maquina para que refleje el estado
     * actual (cuantas ruedas hay, que simbolo esta visible en cada una
     * y si esa combinacion es ganadora).
     */
    private void refreshView() {
        view.sync(wheels.size());
        view.setSymbolSets(buildSymbolSets());
        view.updateSymbols(configuration());
        view.setJackpot(isJackpot());
    }

    /**
     * Arma la lista con el conjunto de simbolos de cada rueda, para
     * que la vista pueda usarlos en el efecto visual del spin.
     */
    private ArrayList<ArrayList<String>> buildSymbolSets() {
        ArrayList<ArrayList<String>> sets = new ArrayList<ArrayList<String>>();
        for (Wheel w : wheels) {
            sets.add(w.getSymbols());
        }
        return sets;
    }

}
