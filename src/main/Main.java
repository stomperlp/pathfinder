package main;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        GraphicsHandler g = new GraphicsHandler();
        g.setVisible(true);
        ArrayList<String> i = new ArrayList<>();
        i.add(null);
        for(String s : i) {
            System.out.println(s);
        }
        System.out.println(i.contains(null) + "");
    }
}
