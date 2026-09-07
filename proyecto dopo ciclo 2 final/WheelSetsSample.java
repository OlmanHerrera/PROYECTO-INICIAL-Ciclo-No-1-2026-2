import java.util.ArrayList;


public class WheelSetsSample {

    public static ArrayList<ArrayList<String>> sampleWheelSets() {
        ArrayList<ArrayList<String>> wheelSets = new ArrayList<ArrayList<String>>();

        ArrayList<String> wheel1 = new ArrayList<String>();
        wheel1.add("red");
        wheel1.add("blue");
        wheel1.add("yellow");
        wheelSets.add(wheel1);

        ArrayList<String> wheel2 = new ArrayList<String>();
        wheel2.add("green");
        wheel2.add("magenta");
        wheelSets.add(wheel2);

        ArrayList<String> wheel3 = new ArrayList<String>();
        wheel3.add("black");
        wheel3.add("white");
        wheel3.add("red");
        wheelSets.add(wheel3);
        return wheelSets;
    }
}