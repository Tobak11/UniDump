/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calcthings;

/**
 *
 * @author tobak11
 */
public class AccountData {
    public static volatile double BALANCE = 1000;
    public static volatile double INVESTED = 0;
    
    public static volatile double BEST_RATE = Double.MIN_VALUE;
    public static volatile double WORST_RATE = Double.MAX_VALUE;
    
    public static volatile double WIN_COUNT = 0;
    public static volatile double LOSE_COUNT = 0;
    public static volatile double WIN_SUM = 0;
    public static volatile double LOSE_SUM = 0;
    
    
    //Just to print readably
    public static volatile boolean balanceChanged = false;
}
