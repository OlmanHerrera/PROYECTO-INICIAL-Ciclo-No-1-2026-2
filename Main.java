/**
 * Demostracion de la maquina tragamonedas corregida.
 */
public class Main {
    public static void main(String[] args) {
        slotMachine machine = new slotMachine();

        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);

        // Cada color conserva su valor logico, pero visualmente tiene una forma distinta:
        // red=circulo, blue=cuadrado, yellow=triangulo,
        // green=rombo, magenta=estrella, black=hexagono.
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "yellow");
        machine.addSymbol(4, "green");
        machine.addSymbol(5, "magenta");
        machine.addSymbol(6, "black");

        machine.spin();

        System.out.println("Configuracion final:");
        String[] config = machine.configuration();
        for (int i = 0; i < config.length; i++) {
            System.out.println("Rueda " + (i + 1) + ": " + config[i]);
        }
        System.out.println("Jackpot: " + machine.isJackpot());
    }
}
