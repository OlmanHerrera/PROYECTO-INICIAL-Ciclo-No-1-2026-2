import java.util.ArrayList;
import java.util.Random;


/**
 * Dibuja la maquina tragamonedas manteniendo el fondo de la rueda blanco 
 * y mostrando el simbolo geometrico centrado sin tapar el cuerpo.
 * 
 * @author Olman Alejandro Herrera || Ahmad Mustafayasser Diaz
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
    
    private static final int CIRCLE_DEFAULT_X = 20; 
    private static final int CIRCLE_DEFAULT_Y = 60;

    private Rectangle body;
    private int bodyX;
    private int bodyY;

    private ArrayList<Rectangle> wheelBackgrounds;
    private ArrayList<Circle> wheelSymbols; 
    private ArrayList<int[]> wheelBgPositions;      
    private ArrayList<int[]> wheelSymbolPositions;      
    private ArrayList<ArrayList<String>> wheelSymbolSets;
    
    private boolean shown;
    private Random rnd;

    public SlotMachineView() {
        body = new Rectangle();
        bodyX = RECTANGLE_DEFAULT_X;
        bodyY = RECTANGLE_DEFAULT_Y;
        
        wheelBackgrounds = new ArrayList<Rectangle>();
        wheelSymbols = new ArrayList<Circle>();
        wheelBgPositions = new ArrayList<int[]>();
        wheelSymbolPositions = new ArrayList<int[]>();
        wheelSymbolSets = new ArrayList<ArrayList<String>>();
        
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

    public void updateSymbols(String[] configuration) {
        for (int i = 0; i < configuration.length && i < wheelSymbols.size(); i++) {
            String color = configuration[i];
            Circle symbolShape = wheelSymbols.get(i);
            
            // Forzamos que el fondo de la rueda siempre sea blanco
            wheelBackgrounds.get(i).changeColor(EMPTY_COLOR);

            if (color == null) {
                symbolShape.makeInvisible();
            } else {
                symbolShape.changeColor(color);
                if (shown) {
                    symbolShape.makeVisible();
                }
            }
        }
    }

    public void spinEffect(int wheelIndex) {
        if (wheelIndex < 0 || wheelIndex >= wheelSymbols.size() || wheelIndex >= wheelSymbolSets.size()) {
            return;
        }
        ArrayList<String> set = wheelSymbolSets.get(wheelIndex);
        if (set == null || set.isEmpty()) {
            return;
        }
        Circle symbolShape = wheelSymbols.get(wheelIndex);
        if (!shown) return;
        
        symbolShape.makeVisible();
        for (int i = 0; i < SPIN_FLICKERS; i++) {
            symbolShape.changeColor(set.get(rnd.nextInt(set.size())));
        }
    }

    public void spinEffectAll() {
        for (int i = 0; i < wheelSymbols.size(); i++) {
            spinEffect(i);
        }
    }

    public void setJackpot(boolean jackpot) {
        // Cambia el color del cuerpo exterior, pero redibuja los elementos internos para que no los tape
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
        for (Circle symbol : wheelSymbols) {
            symbol.makeVisible();
        }
    }

    public void makeInvisible() {
        shown = false;
        body.makeInvisible();
        for (Rectangle bg : wheelBackgrounds) {
            bg.makeInvisible();
        }
        for (Circle symbol : wheelSymbols) {
            symbol.makeInvisible();
        }
    }

    private void addWheelShape() {
        Rectangle bg = new Rectangle();
        bg.changeColor(EMPTY_COLOR); 
        
        Circle symbol = new Circle(); 
        
        if (shown) {
            bg.makeVisible();
            symbol.makeVisible();
        }
        
        wheelBackgrounds.add(bg);
        wheelSymbols.add(symbol);
        wheelBgPositions.add(new int[] {RECTANGLE_DEFAULT_X, RECTANGLE_DEFAULT_Y});
        wheelSymbolPositions.add(new int[] {CIRCLE_DEFAULT_X, CIRCLE_DEFAULT_Y});
    }

    private void removeLastWheelShape() {
        int last = wheelBackgrounds.size() - 1;
        wheelBackgrounds.get(last).makeInvisible();
        wheelSymbols.get(last).makeInvisible();
        
        wheelBackgrounds.remove(last);
        wheelSymbols.remove(last);
        wheelBgPositions.remove(last);
        wheelSymbolPositions.remove(last);
    }

    private void layout(int wheelCount) {
        int effectiveCount = Math.max(wheelCount, 1);
        int bodyWidth = 2 * MARGIN + effectiveCount * WHEEL_WIDTH + (effectiveCount - 1) * WHEEL_GAP;
        int bodyHeight = WHEEL_HEIGHT + 2 * MARGIN;

        body.changeSize(bodyHeight, bodyWidth);
        moveBodyTo(BODY_LEFT, BODY_TOP);

        for (int i = 0; i < wheelCount; i++) {
            int xBg = BODY_LEFT + MARGIN + i * (WHEEL_WIDTH + WHEEL_GAP);
            int yBg = BODY_TOP + MARGIN;
            
            wheelBackgrounds.get(i).changeSize(WHEEL_HEIGHT, WHEEL_WIDTH);
            moveShape(wheelBackgrounds.get(i), wheelBgPositions.get(i), xBg, yBg, false);
            
            // Centrar la figura dentro de la rueda
            int xSym = xBg + (WHEEL_WIDTH / 2) - 15; 
            int ySym = yBg + (WHEEL_HEIGHT / 2) - 15; 
            moveShape(wheelSymbols.get(i), wheelSymbolPositions.get(i), xSym, ySym, true);
        }
        
        if (shown) {
            redistributeLayers();
        }
    }

    // Asegura el orden correcto de las capas en el Canvas (Cuerpo -> Fondos de ruedas -> Figuras)
    private void redistributeLayers() {
        body.makeInvisible();
        body.makeVisible();
        
        for (int i = 0; i < wheelBackgrounds.size(); i++) {
            wheelBackgrounds.get(i).makeInvisible();
            wheelBackgrounds.get(i).makeVisible();
            
            wheelSymbols.get(i).makeInvisible();
            wheelSymbols.get(i).makeVisible();
        }
    }

    private void moveBodyTo(int x, int y) {
        body.moveHorizontal(x - bodyX);
        body.moveVertical(y - bodyY);
        bodyX = x;
        bodyY = y;
    }

    private void moveShape(Object shape, int[] currentPos, int x, int y, boolean isCircle) {
        int dx = x - currentPos[0];
        int dy = y - currentPos[1];
        
        if (isCircle) {
            ((Circle) shape).moveHorizontal(dx);
            ((Circle) shape).moveVertical(dy);
        } else {
            ((Rectangle) shape).moveHorizontal(dx);
            ((Rectangle) shape).moveVertical(dy);
        }
        
        currentPos[0] = x;
        currentPos[1] = y;
    }
}