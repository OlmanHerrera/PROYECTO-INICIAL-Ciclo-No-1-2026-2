import java.util.ArrayList;

/**
 * Simulador de una maquina tragamonedas.
 * @author Olman Alejandro Herrera  || Ahmad Mustafayasser Diaz
 * @version 1.2
 */
public class slotMachine {

    private ArrayList<Wheel> wheels;
    private SlotMachineView view;
    private boolean visible;
    private boolean ok;

    public slotMachine() {
        wheels = new ArrayList<Wheel>();
        view = new SlotMachineView();
        visible = true;
        ok = true;
        view.makeVisible();
    }

    public void addWheel(int pos) {
        int index = normalize(pos, wheels.size() + 1);
        wheels.add(index - 1, new Wheel());
        ok = true;
        refreshView();
    }

    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            ok = false;
            return;
        }
        int index = normalize(pos, wheels.size());
        wheels.remove(index - 1);
        ok = true;
        refreshView();
    }

    /**
     * Intercambia la posicion de dos ruedas.
     */
    public void swap(int wheel1, int wheel2) {
        if (validWheel(wheel1) && validWheel(wheel2)) {
            Wheel temp = wheels.get(wheel1 - 1);
            wheels.set(wheel1 - 1, wheels.get(wheel2 - 1));
            wheels.set(wheel2 - 1, temp);
            ok = true;
            refreshView();
        } else {
            ok = false;
        }
    }

    /**
     * Fija una rueda para que no gire.
     */
    public void lock(int wheel) {
        if (validWheel(wheel)) {
            wheels.get(wheel - 1).lock();
            ok = true;
        } else {
            ok = false;
        }
    }

    /**
     * Suelta una rueda fijada.
     */
    public void unlock(int wheel) {
        if (validWheel(wheel)) {
            wheels.get(wheel - 1).unlock();
            ok = true;
        } else {
            ok = false;
        }
    }

    public void addSymbol(int pos, String color) {
        if (wheels.isEmpty()) {
            ok = false;
            return;
        }
        for (Wheel w : wheels) {
            int index = normalize(pos, w.size() + 1);
            w.addSymbol(index, color);
        }
        ok = true;
        refreshView();
    }

    public void delSymbol(String symbol) {
        boolean removedAny = false;
        for (Wheel w : wheels) {
            if (w.delSymbol(symbol)) {
                removedAny = true;
            }
        }
        if (!removedAny) {
            ok = false;
            return;
        }
        ok = true;
        refreshView();
    }

    public void placeSymbol(int wheel, String symbol) {
        if (!validWheel(wheel)) {
            ok = false;
            return;
        }
        Wheel w = wheels.get(wheel - 1);
        if (!w.setCurrent(symbol)) {
            ok = false;
            return;
        }
        ok = true;
        refreshView();
    }

    public void spin(int wheel) {
        if (!validWheel(wheel) || wheels.get(wheel - 1).isLocked()) {
            ok = false;
            return;
        }
        view.spinEffect(wheel - 1);
        wheels.get(wheel - 1).spin();
        ok = true;
        refreshView();
    }

    /**
     * Rota una rueda un numero de pasos visiblemente paso a paso.
     */
    public void spin(int wheel, int steps) {
        if (!validWheel(wheel) || wheels.get(wheel - 1).isLocked()) {
            ok = false;
            return;
        }
        Wheel w = wheels.get(wheel - 1);
        int direction = steps >= 0 ? 1 : -1;
        int absSteps = Math.abs(steps);
        
        for (int i = 0; i < absSteps; i++) {
            w.spin(direction);
            refreshView();
            if (visible) {
                try {
                    Thread.sleep(150); // Simula el paso a paso visual
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        ok = true;
    }

    /**
     * Deja la maquina en una configuracion dada.
     */
    public void spin(String[] setSymbols) {
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            ok = false;
            return;
        }
        boolean allSuccess = true;
        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (!w.isLocked()) {
                if (!w.setCurrent(setSymbols[i])) {
                    allSuccess = false; // Falla si un simbolo no existe en la rueda
                }
            }
        }
        ok = allSuccess;
        refreshView();
    }

    public void spin() {
        if (wheels.isEmpty()) {
            ok = false;
            return;
        }
        view.spinEffectAll();
        for (Wheel w : wheels) {
            if (!w.isLocked()) {
                w.spin();
            }
        }
        ok = true;
        refreshView();
    }

    public String[] symbols() {
        ArrayList<String> all = new ArrayList<String>();
        for (Wheel w : wheels) {
            all.addAll(w.getSymbols());
        }
        ok = true;
        return all.toArray(new String[0]);
    }

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

    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).getCurrent();
        }
        ok = true;
        return config;
    }

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

    public void makeVisible() {
        visible = true;
        view.makeVisible();
        refreshView();
        ok = true;
    }

    public void makeInvisible() {
        visible = false;
        view.makeInvisible();
        ok = true;
    }

    public void exit() {
        view.makeInvisible();
        visible = false;
        ok = true;
        System.exit(0);
    }

    public boolean ok() {
        return ok;
    }

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

    private void refreshView() {
        view.sync(wheels.size());
        view.setSymbolSets(buildSymbolSets());
        view.updateSymbols(configuration());
        view.setJackpot(isJackpot());
    }

    private ArrayList<ArrayList<String>> buildSymbolSets() {
        ArrayList<ArrayList<String>> sets = new ArrayList<ArrayList<String>>();
        for (Wheel w : wheels) {
            sets.add(w.getSymbols());
        }
        return sets;
    }
}