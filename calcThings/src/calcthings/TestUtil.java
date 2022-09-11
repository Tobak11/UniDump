/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calcthings;

import java.util.ArrayList;

/**
 *
 * @author tobak11
 */
public class TestUtil {
    public volatile ArrayList<Double> results;
    public volatile ArrayList<String> resultParams;
    public volatile ArrayList<Boolean> allRdy;

    public TestUtil() {
        results = new ArrayList<>();
        resultParams = new ArrayList<>();
        allRdy = new ArrayList<>();
    }
    
    public void initEMAArrays(int xFrom, int xTo, int zFrom, int zTo, int yFrom, int delayFrom, int delayTo, double smtFrom, double smtTo, double smtStep){
        for(int x=xFrom;x<=xTo;x++){
            for(int z = zFrom;z<=zTo;z++){
                for(int y=yFrom;y<=z;y++){
                    for(int delay=delayFrom;delay<=delayTo;delay++){
                        for(double smtIdx = smtFrom;smtIdx<=smtTo;smtIdx+=smtStep){
                            results.add(-1.0);
                            resultParams.add("Nope");
                            allRdy.add(false);
                        }
                    }   
                }
            }
        }
        
        System.out.println("Number of tests: " + results.size());
    }
}
