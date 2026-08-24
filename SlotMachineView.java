import java.util.ArrayList;
import java.util.Random;

/**
 * Dibuja la maquina tragamonedas usando el paquete shapes: un Rectangle
 * grande como cuerpo y un Rectangle por cada rueda, que se pinta
 * directamente con el color del simbolo que esta visible en ese momento.
 * 
 * @author Olman Alejandro Herrera || Ahmad Mustafayasser Diaz
 *
 */
public class SlotMachineView {

    private static final int MARGIN = 20;
    private static final int WHEEL_WIDTH = 40;
    private static final int WHEEL_GAP = 10;
    private static final int WHEEL_HEIGHT = 100;
    private static final int BODY_TOP = 40;
    private static final int BODY_LEFT = 40;
    private static final String NORMAL_BODY_COLOR = "blue";
    private static final String JACKPOT_BODY_COLOR = "yellow";
    private static final String EMPTY_COLOR = "white";
    private static final int SPIN_FLICKERS = 5;

    private static final int RECTANGLE_DEFAULT_X = 70;
    private static final int RECTANGLE_DEFAULT_Y = 15;

    private Rectangle body;
    private int bodyX;
    private int bodyY;

    private ArrayList<Rectangle> wheelShapes;
    private ArrayList<int[]> wheelPositions;      
    private ArrayList<ArrayList<String>> wheelSymbolSets;
    private boolean shown;
    private Random rnd;

    public SlotMachineView() {
        body = new Rectangle();
        bodyX = RECTANGLE_DEFAULT_X;
        bodyY = RECTANGLE_DEFAULT_Y;
        wheelShapes = new ArrayList<Rectangle>();
        wheelPositions = new ArrayList<int[]>();
        wheelSymbolSets = new ArrayList<ArrayList<String>>();
        shown = false;
        rnd = new Random();
        body.changeColor(NORMAL_BODY_COLOR);
    }

    /**
     * Ajusta cuantas ruedas se estan mostrando 
     */
    public void sync(int wheelCount) {
        while (wheelShapes.size() < wheelCount) {
            addWheelShape();
        }
        while (wheelShapes.size() > wheelCount) {
            removeLastWheelShape();
        }
        layout(wheelCount);
    }

    /**
     * Guarda, para cada rueda, el conjunto completo de simbolos que
     * tiene disponibles. Se usa despues para el efecto visual del spin.
     */
    public void setSymbolSets(ArrayList<ArrayList<String>> symbolSets) {
        wheelSymbolSets = symbolSets;
    }

    /**
     * Pinta cada rueda con el color de su simbolo visible actual.
     */
    public void updateSymbols(String[] configuration) {
        for (int i = 0; i < configuration.length && i < wheelShapes.size(); i++) {
            String color = configuration[i];
            wheelShapes.get(i).changeColor(color == null ? EMPTY_COLOR : color); // Si una rueda no tiene simbolo visible, se deja en blanco.
        }
    }

    /**
     * Efecto visual de giro para una sola rueda: cambia su color varias
     * veces rapido, tomando colores al azar del propio conjunto de
     * simbolos de esa rueda. El color final lo deja despues updateSymbols.
     */
    public void spinEffect(int wheelIndex) {
        if (wheelIndex < 0 || wheelIndex >= wheelShapes.size()) {
            return;
        }
        if (wheelIndex >= wheelSymbolSets.size()) {
            return;
        }
        ArrayList<String> set = wheelSymbolSets.get(wheelIndex);
        if (set == null || set.isEmpty()) {
            return;
        }
        Rectangle wheel = wheelShapes.get(wheelIndex);
        for (int i = 0; i < SPIN_FLICKERS; i++) {
            wheel.changeColor(set.get(rnd.nextInt(set.size())));
        }
    }

    /**
     * Aplica el efecto de giro a todas las ruedas (para el spin general).
     */
    public void spinEffectAll() {
        for (int i = 0; i < wheelShapes.size(); i++) {
            spinEffect(i);
        }
    }

    /**
     * Cambia el color del cuerpo de la maquina para mostrar que hay jackpot
     */
    public void setJackpot(boolean jackpot) {
        body.changeColor(jackpot ? JACKPOT_BODY_COLOR : NORMAL_BODY_COLOR);
    }

    public void makeVisible() {
        shown = true;
        body.makeVisible();
        for (Rectangle wheel : wheelShapes) {
            wheel.makeVisible();
        }
    }

    public void makeInvisible() {
        shown = false;
        body.makeInvisible();
        for (Rectangle wheel : wheelShapes) {
            wheel.makeInvisible();
        }
    }

    private void addWheelShape() {
        Rectangle wheel = new Rectangle();
        wheel.changeColor(EMPTY_COLOR);
        if (shown) {
            wheel.makeVisible();
        }
        wheelShapes.add(wheel);
        wheelPositions.add(new int[] {RECTANGLE_DEFAULT_X, RECTANGLE_DEFAULT_Y});
    }

    private void removeLastWheelShape() {
        int last = wheelShapes.size() - 1;
        wheelShapes.get(last).makeInvisible();
        wheelShapes.remove(last);
        wheelPositions.remove(last);
    }

    /**
     * Recalcula tamano del cuerpo y posicion de cada rueda segun
     * cuantas hay, moviendo cada figura desde donde quedo la ultima vez.
     */
    private void layout(int wheelCount) {
        int bodyWidth = wheelCount == 0
            ? WHEEL_WIDTH + 2 * MARGIN
            : 2 * MARGIN + wheelCount * WHEEL_WIDTH + (wheelCount - 1) * WHEEL_GAP;
        int bodyHeight = WHEEL_HEIGHT + 2 * MARGIN;

        body.changeSize(bodyHeight, bodyWidth);
        moveBodyTo(BODY_LEFT, BODY_TOP);

        for (int i = 0; i < wheelCount; i++) {
            int x = BODY_LEFT + MARGIN + i * (WHEEL_WIDTH + WHEEL_GAP);
            int y = BODY_TOP + MARGIN;
            wheelShapes.get(i).changeSize(WHEEL_HEIGHT, WHEEL_WIDTH);
            moveTo(wheelShapes.get(i), wheelPositions.get(i), x, y);
        }
    }

    private void moveBodyTo(int x, int y) {
        body.moveHorizontal(x - bodyX);
        body.moveVertical(y - bodyY);
        bodyX = x;
        bodyY = y;
    }

    private void moveTo(Rectangle shape, int[] currentPos, int x, int y) {
        shape.moveHorizontal(x - currentPos[0]);
        shape.moveVertical(y - currentPos[1]);
        currentPos[0] = x;
        currentPos[1] = y;
    }
}
