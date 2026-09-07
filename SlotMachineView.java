import java.util.ArrayList;
import java.util.Random;

/**
 * Vista grafica de la maquina tragamonedas.
 * Cada rueda tiene fondo blanco y un simbolo que puede cambiar tanto
 * de color como de forma durante el giro.
 */
public class SlotMachineView {

    private static final int MARGIN = 20;
    private static final int WHEEL_WIDTH = 56;
    private static final int WHEEL_GAP = 12;
    private static final int WHEEL_HEIGHT = 104;
    private static final int BODY_TOP = 40;
    private static final int BODY_LEFT = 40;
    private static final String NORMAL_BODY_COLOR = "blue";
    private static final String JACKPOT_BODY_COLOR = "yellow";
    private static final String EMPTY_COLOR = "white";

    // Mas cuadros y una pausa perceptible: ahora el giro realmente se ve.
    private static final int SPIN_FLICKERS = 14;
    private static final int SPIN_DELAY_MS = 75;
    private static final int SYMBOL_SIZE = 34;

    private static final int RECTANGLE_DEFAULT_X = 70;
    private static final int RECTANGLE_DEFAULT_Y = 15;
    private static final int SYMBOL_DEFAULT_X = 20;
    private static final int SYMBOL_DEFAULT_Y = 60;

    private Rectangle body;
    private int bodyX;
    private int bodyY;

    private ArrayList<Rectangle> wheelBackgrounds;
    private ArrayList<SlotSymbol> wheelSymbols;
    private ArrayList<int[]> wheelBgPositions;
    private ArrayList<int[]> wheelSymbolPositions;
    private ArrayList<ArrayList<String>> wheelSymbolSets;
    private ArrayList<Boolean> hasCurrentSymbol;

    private boolean shown;
    private Random rnd;

    public SlotMachineView() {
        body = new Rectangle();
        bodyX = RECTANGLE_DEFAULT_X;
        bodyY = RECTANGLE_DEFAULT_Y;

        wheelBackgrounds = new ArrayList<Rectangle>();
        wheelSymbols = new ArrayList<SlotSymbol>();
        wheelBgPositions = new ArrayList<int[]>();
        wheelSymbolPositions = new ArrayList<int[]>();
        wheelSymbolSets = new ArrayList<ArrayList<String>>();
        hasCurrentSymbol = new ArrayList<Boolean>();

        shown = false;
        rnd = new Random();
        body.changeColor(NORMAL_BODY_COLOR);
    }

    public void sync(int wheelCount) {
        while (wheelBackgrounds.size() < wheelCount) {
            addWheelShape();
        }
        while (wheelBackgrounds.size() > wheelCount) {
            removeLastWheelShape();
        }
        layout(wheelCount);
    }

    public void setSymbolSets(ArrayList<ArrayList<String>> symbolSets) {
        wheelSymbolSets = symbolSets;
    }

    /** Muestra la configuracion real en la que termino cada rueda. */
    public void updateSymbols(String[] configuration) {
        for (int i = 0; i < wheelSymbols.size(); i++) {
            wheelBackgrounds.get(i).changeColor(EMPTY_COLOR);

            String symbol = (i < configuration.length) ? configuration[i] : null;
            if (symbol == null) {
                hasCurrentSymbol.set(i, false);
                wheelSymbols.get(i).makeInvisible();
            } else {
                hasCurrentSymbol.set(i, true);
                displaySymbol(i, symbol);
            }
        }
    }

    /**
     * Animacion de una sola rueda. En cada cuadro cambia color Y figura.
     */
    public void spinEffect(int wheelIndex) {
        if (!validSet(wheelIndex) || !shown) return;

        SlotSymbol shape = wheelSymbols.get(wheelIndex);
        shape.makeVisible();
        for (int i = 0; i < SPIN_FLICKERS; i++) {
            ArrayList<String> set = wheelSymbolSets.get(wheelIndex);
            String symbol = set.get(rnd.nextInt(set.size()));
            displaySymbol(wheelIndex, symbol);
            Canvas.getCanvas().wait(SPIN_DELAY_MS);
        }
    }

    /**
     * Animacion conjunta. Todas las ruedas cambian cuadro por cuadro,
     * como los rodillos de una tragamonedas.
     */
    public void spinEffectAll() {
        if (!shown) return;

        boolean any = false;
        for (int i = 0; i < wheelSymbols.size(); i++) {
            if (validSet(i)) {
                wheelSymbols.get(i).makeVisible();
                any = true;
            }
        }
        if (!any) return;

        for (int frame = 0; frame < SPIN_FLICKERS; frame++) {
            for (int i = 0; i < wheelSymbols.size(); i++) {
                if (validSet(i)) {
                    ArrayList<String> set = wheelSymbolSets.get(i);
                    String symbol = set.get(rnd.nextInt(set.size()));
                    displaySymbol(i, symbol);
                }
            }
            Canvas.getCanvas().wait(SPIN_DELAY_MS);
        }
    }

    public void setJackpot(boolean jackpot) {
        body.changeColor(jackpot ? JACKPOT_BODY_COLOR : NORMAL_BODY_COLOR);
        if (shown) {
            redistributeLayers();
        }
    }

    public void makeVisible() {
        shown = true;
        body.makeVisible();
        for (Rectangle bg : wheelBackgrounds) {
            bg.makeVisible();
        }
        for (int i = 0; i < wheelSymbols.size(); i++) {
            if (hasCurrentSymbol.get(i)) {
                wheelSymbols.get(i).makeVisible();
            }
        }
    }

    public void makeInvisible() {
        shown = false;
        body.makeInvisible();
        for (Rectangle bg : wheelBackgrounds) {
            bg.makeInvisible();
        }
        for (SlotSymbol symbol : wheelSymbols) {
            symbol.makeInvisible();
        }
    }

    private boolean validSet(int index) {
        return index >= 0 && index < wheelSymbols.size()
                && index < wheelSymbolSets.size()
                && wheelSymbolSets.get(index) != null
                && !wheelSymbolSets.get(index).isEmpty();
    }

    private void displaySymbol(int wheelIndex, String symbol) {
        SlotSymbol symbolShape = wheelSymbols.get(wheelIndex);
        symbolShape.changeSymbol(symbol, shapeForSymbol(symbol));
        if (shown) {
            symbolShape.makeVisible();
        }
    }

    /**
     * La logica original guarda los simbolos como Strings de color.
     * Para no romper esa API, cada valor recibe tambien una forma estable.
     */
    private String shapeForSymbol(String symbol) {
        if (symbol == null) return "circle";
        String s = symbol.toLowerCase();
        if (s.equals("red")) return "circle";
        if (s.equals("blue")) return "square";
        if (s.equals("yellow")) return "triangle";
        if (s.equals("green")) return "diamond";
        if (s.equals("magenta")) return "star";
        if (s.equals("black")) return "hexagon";

        // Para cualquier simbolo nuevo: forma determinista segun su texto.
        String[] forms = {"circle", "square", "triangle", "diamond", "star", "hexagon"};
        return forms[(s.hashCode() & 0x7fffffff) % forms.length];
    }

    private void addWheelShape() {
        Rectangle bg = new Rectangle();
        bg.changeColor(EMPTY_COLOR);

        SlotSymbol symbol = new SlotSymbol();
        symbol.changeSize(SYMBOL_SIZE);

        wheelBackgrounds.add(bg);
        wheelSymbols.add(symbol);
        wheelBgPositions.add(new int[] {RECTANGLE_DEFAULT_X, RECTANGLE_DEFAULT_Y});
        wheelSymbolPositions.add(new int[] {SYMBOL_DEFAULT_X, SYMBOL_DEFAULT_Y});
        hasCurrentSymbol.add(false);

        if (shown) {
            bg.makeVisible();
            // No mostramos un simbolo azul por defecto cuando la rueda esta vacia.
            symbol.makeInvisible();
        }
    }

    private void removeLastWheelShape() {
        int last = wheelBackgrounds.size() - 1;
        wheelBackgrounds.get(last).makeInvisible();
        wheelSymbols.get(last).makeInvisible();

        wheelBackgrounds.remove(last);
        wheelSymbols.remove(last);
        wheelBgPositions.remove(last);
        wheelSymbolPositions.remove(last);
        hasCurrentSymbol.remove(last);
    }

    private void layout(int wheelCount) {
        int effectiveCount = Math.max(wheelCount, 1);
        int bodyWidth = 2 * MARGIN + effectiveCount * WHEEL_WIDTH
                + (effectiveCount - 1) * WHEEL_GAP;
        int bodyHeight = WHEEL_HEIGHT + 2 * MARGIN;

        body.changeSize(bodyHeight, bodyWidth);
        moveBodyTo(BODY_LEFT, BODY_TOP);

        for (int i = 0; i < wheelCount; i++) {
            int xBg = BODY_LEFT + MARGIN + i * (WHEEL_WIDTH + WHEEL_GAP);
            int yBg = BODY_TOP + MARGIN;

            wheelBackgrounds.get(i).changeSize(WHEEL_HEIGHT, WHEEL_WIDTH);
            moveRectangle(wheelBackgrounds.get(i), wheelBgPositions.get(i), xBg, yBg);

            int xSym = xBg + (WHEEL_WIDTH - SYMBOL_SIZE) / 2;
            int ySym = yBg + (WHEEL_HEIGHT - SYMBOL_SIZE) / 2;
            moveSlotSymbol(wheelSymbols.get(i), wheelSymbolPositions.get(i), xSym, ySym);
        }

        if (shown) {
            redistributeLayers();
        }
    }

    /** Mantiene el cuerpo detras, luego las ventanas y por ultimo los simbolos. */
    private void redistributeLayers() {
        body.makeInvisible();
        body.makeVisible();

        for (int i = 0; i < wheelBackgrounds.size(); i++) {
            wheelBackgrounds.get(i).makeInvisible();
            wheelBackgrounds.get(i).makeVisible();

            wheelSymbols.get(i).makeInvisible();
            if (hasCurrentSymbol.get(i) || validSet(i)) {
                // Si esta girando puede estar visible; updateSymbols ajusta el estado final.
                wheelSymbols.get(i).makeVisible();
            }
        }
    }

    private void moveBodyTo(int x, int y) {
        body.moveHorizontal(x - bodyX);
        body.moveVertical(y - bodyY);
        bodyX = x;
        bodyY = y;
    }

    private void moveRectangle(Rectangle shape, int[] currentPos, int x, int y) {
        int dx = x - currentPos[0];
        int dy = y - currentPos[1];
        shape.moveHorizontal(dx);
        shape.moveVertical(dy);
        currentPos[0] = x;
        currentPos[1] = y;
    }

    private void moveSlotSymbol(SlotSymbol shape, int[] currentPos, int x, int y) {
        int dx = x - currentPos[0];
        int dy = y - currentPos[1];
        shape.moveHorizontal(dx);
        shape.moveVertical(dy);
        currentPos[0] = x;
        currentPos[1] = y;
    }
}
