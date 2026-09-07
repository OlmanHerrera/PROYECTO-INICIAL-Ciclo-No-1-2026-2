import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * Simbolo grafico de una rueda de la maquina tragamonedas.
 * Puede cambiar de color y tambien de forma durante el giro.
 */
public class SlotSymbol {
    private int size;
    private int xPosition;
    private int yPosition;
    private String color;
    private String type;
    private boolean isVisible;

    public SlotSymbol() {
        size = 34;
        xPosition = 20;
        yPosition = 60;
        color = "red";
        type = "circle";
        isVisible = false;
    }

    public void makeVisible() {
        isVisible = true;
        draw();
    }

    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    public void changeSize(int newSize) {
        erase();
        size = Math.max(8, newSize);
        draw();
    }

    public void changeColor(String newColor) {
        erase();
        color = visibleColor(newColor);
        draw();
    }

    public void changeType(String newType) {
        erase();
        type = newType;
        draw();
    }

    /** Cambia color y forma con un solo redibujado. */
    public void changeSymbol(String newColor, String newType) {
        erase();
        color = visibleColor(newColor);
        type = newType;
        draw();
    }

    private String visibleColor(String requested) {
        // Un simbolo blanco desapareceria sobre el fondo blanco de la rueda.
        // Lo mostramos gris oscuro, conservando "white" como valor logico en Wheel.
        if (requested == null) return "black";
        if (requested.equalsIgnoreCase("white")) return "gray";
        return requested;
    }

    private void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color, buildShape());
        }
    }

    private void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }

    private Shape buildShape() {
        if ("square".equals(type)) {
            return new Rectangle2D.Double(xPosition, yPosition, size, size);
        }
        if ("triangle".equals(type)) {
            Path2D.Double p = new Path2D.Double();
            p.moveTo(xPosition + size / 2.0, yPosition);
            p.lineTo(xPosition + size, yPosition + size);
            p.lineTo(xPosition, yPosition + size);
            p.closePath();
            return p;
        }
        if ("diamond".equals(type)) {
            Path2D.Double p = new Path2D.Double();
            p.moveTo(xPosition + size / 2.0, yPosition);
            p.lineTo(xPosition + size, yPosition + size / 2.0);
            p.lineTo(xPosition + size / 2.0, yPosition + size);
            p.lineTo(xPosition, yPosition + size / 2.0);
            p.closePath();
            return p;
        }
        if ("star".equals(type)) {
            return regularStar(5);
        }
        if ("hexagon".equals(type)) {
            Path2D.Double p = new Path2D.Double();
            for (int i = 0; i < 6; i++) {
                double angle = Math.toRadians(-90 + i * 60);
                double px = xPosition + size / 2.0 + Math.cos(angle) * size / 2.0;
                double py = yPosition + size / 2.0 + Math.sin(angle) * size / 2.0;
                if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
            }
            p.closePath();
            return p;
        }
        return new Ellipse2D.Double(xPosition, yPosition, size, size);
    }

    private Shape regularStar(int points) {
        Path2D.Double p = new Path2D.Double();
        double cx = xPosition + size / 2.0;
        double cy = yPosition + size / 2.0;
        double outer = size / 2.0;
        double inner = outer * 0.43;
        for (int i = 0; i < points * 2; i++) {
            double radius = (i % 2 == 0) ? outer : inner;
            double angle = Math.toRadians(-90 + i * 180.0 / points);
            double px = cx + Math.cos(angle) * radius;
            double py = cy + Math.sin(angle) * radius;
            if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
        }
        p.closePath();
        return p;
    }
}
