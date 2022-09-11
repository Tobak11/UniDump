/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calcthings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tobak11
 */
public class ForexPairData {
    private ArrayList<Double> values;
    
    private ArrayList<Double> slowAvgs;
    private ArrayList<Double> medAvgs;
    private ArrayList<Double> fastAvgs;
    
    private String fxName;
    private int SLOW_AVG_IDX;
    private int MED_AVG_IDX;
    private int FAST_AVG_IDX;
    
    //For balance management optimization
    private ArrayList<Double> lastTradeRates;
    
    private int BALANCE_WEIGHT = 4;
    //private int LAST_X_TRADES = 50;
    
    //0 - 1
    private int BALANCE_FRACTION = 1;
    
    //For AWMA simulation
    private ArrayList<Double> AWMAPowValues;
    
    public ForexPairData(String fxName){
        this.fxName = fxName;
        
        values = new ArrayList<>();
        readValues(values);
        
        lastTradeRates = new ArrayList<>();
    }
    
    //SMA Based
    public double calcSMA(ArrayList<Double> values){
        double valueSum = 0;
        double count = 0;
        
        for(int i=0;i<values.size();i++){
            count++;
            valueSum += values.get(i);
        }
        
        return valueSum/count;
    }
    
    public double calcDelayedSMA(ArrayList<Double> values, int delay){
        double valueSum = 0;
        double count = 0;
        
        for(int i=0;i<values.size()-delay;i++){
            count++;
            valueSum += values.get(i);
        }
        
        return valueSum/count;
    }
        
    public double[] startSMASimulation(int slowIdx, int fastIdx){
        this.SLOW_AVG_IDX = slowIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcSMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i))));
            fastAvgs.add(calcSMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i))));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        double lastBuySum = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = false;
                    
                    lastBuySum = values.get(i-1);
                    buyCount++;
                    
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = true;
                    
                    sellCount++;
                    
                    sellSum += values.get(i-1);
                }
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=lastBuySum;
        }
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public double[] startDelayedSMASimulation(int slowIdx, int fastIdx, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedSMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), delay));
            fastAvgs.add(calcDelayedSMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        double lastBuySum = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = false;
                    
                    lastBuySum = values.get(i-1);
                    buyCount++;
                    
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = true;
                    
                    sellCount++;
                    
                    sellSum += values.get(i-1);
                }
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=lastBuySum;
        }
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    //LWMA Based
    public double calcLWMA(ArrayList<Double> values){
        double valueSum = 0;
        double div = 0;
        
        for(int i=0;i<values.size();i++){
            div+=(i+1);
            valueSum += values.get(i)*(i+1);
        }
        
        return valueSum/div;
    }
    
    public double calcDelayedLWMA(ArrayList<Double> values, int delay){
        double valueSum = 0;
        double div = 0;
        
        for(int i=0;i<values.size()-delay;i++){
            div+=(i+1);
            valueSum += values.get(i)*(i+1);
        }
        
        return valueSum/div;
    }
    
    public double calcDelayedAWMA(ArrayList<Double> values, int delay){
        double valueSum = 0;
        double div = 0;
        
        for(int i=0;i<values.size()-delay;i++){
//            //SQRT
//            div+=Math.sqrt(i+1);
//            valueSum += values.get(i)*Math.sqrt(i+1);
            
//            //QUADRATIC
//            div+=(i+1)*(i+1);
//            valueSum += values.get(i)*(i+1)*(i+1);
            
            //POWD
            double powdValue = AWMAPowValues.get(i);
            div+=powdValue;
            valueSum += values.get(i)*powdValue;
        }
        
        return valueSum/div;        
    }
    
    public double startLWMASimulation(int slowIdx){
        this.SLOW_AVG_IDX = slowIdx;
        
        slowAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcLWMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i))));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        double lastBuySum = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && values.get(i-1) < slowAvgs.get(i-SLOW_AVG_IDX)){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && values.get(i-1) > slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = false;
                    
                    lastBuySum = values.get(i-1);
                    buyCount++;
                    
                    buySum += values.get(i-1);
                    
                }
                
                if(!canBuy && values.get(i-1) < slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = true;
                    
                    sellCount++;
                    
                    sellSum += values.get(i-1);
                }
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=lastBuySum;
        }
        
        return sellSum/buySum;
    }
    
    public double[] startLWMASimulation(int slowIdx, int fastIdx){
        this.SLOW_AVG_IDX = slowIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcLWMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i))));
            fastAvgs.add(calcLWMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i))));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        double lastBuySum = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = false;
                    
                    lastBuySum = values.get(i-1);
                    buyCount++;
                    
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = true;
                    
                    sellCount++;
                    
                    sellSum += values.get(i-1);
                }
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=lastBuySum;
        }
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public double[] startDelayedLWMASimulation(int slowIdx, int fastIdx, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedLWMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), delay));
            fastAvgs.add(calcDelayedLWMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        double lastBuySum = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = false;
                    
                    lastBuySum = values.get(i-1);
                    buyCount++;
                    
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = true;
                    
                    sellCount++;
                    
                    sellSum += values.get(i-1);
                }
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=lastBuySum;
        }
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    //EMA based
    public double calcEMA(ArrayList<Double> values, double smoothing){
        double k = smoothing/(values.size() + 1);
        
        double yesterdayMA = calcSMA(new ArrayList<>(values.subList(0, values.size()-1)));
        
        return values.get(values.size()-1) * k + yesterdayMA * (1-k);
    }
    
    public double calcDelayedEMA(ArrayList<Double> values, double smoothing, int delay){
        double k = smoothing/(values.size() + 1 - delay);
        
        double yesterdayMA = calcSMA(new ArrayList<>(values.subList(0, values.size()-1-delay)));
        
        return values.get(values.size()-1-delay) * k + yesterdayMA * (1-k);
    }
    
    public double calcDelayedAWEMA(ArrayList<Double> values, double smoothing, int delay){
        double k = smoothing/(values.size() + 1 - delay);
        
        double yesterdayMA = calcDelayedAWMA(new ArrayList<>(values.subList(0, values.size()-1-delay)), 0);
        
        return values.get(values.size()-1-delay) * k + yesterdayMA * (1-k);
    }
    
    public double[] startEMASimulation(int slowIdx, int fastIdx, double smoothing){
        this.SLOW_AVG_IDX = slowIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcEMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), smoothing));
            fastAvgs.add(calcEMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), smoothing));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        double lastBuySum = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = false;
                    
                    lastBuySum = values.get(i-1);
                    buyCount++;
                    
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = true;
                    
                    sellCount++;
                    
                    sellSum += values.get(i-1);
                }
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=lastBuySum;
        }
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public double[] startDelayedEMASimulation(int slowIdx, int fastIdx, double smoothing, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedEMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), smoothing, delay));
            fastAvgs.add(calcDelayedEMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), smoothing, delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        double lastBuySum = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = false;
                    
                    lastBuySum = values.get(i-1);
                    buyCount++;
                    
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)){
                    canBuy = true;
                    
                    sellCount++;
                    
                    sellSum += values.get(i-1);
                }
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=lastBuySum;
        }
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public synchronized double[] startSMADBCrossSimulation(int slowIdx, int medIdx, int fastIdx, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.MED_AVG_IDX = medIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        medAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedSMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), delay));
            medAvgs.add(calcDelayedSMA(new ArrayList<Double>(values.subList(i-MED_AVG_IDX, i)), delay));
            fastAvgs.add(calcDelayedSMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        //To simulate account data
        double moneyPortion = 0;
        double buyPrice = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)) ){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && (fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)) ){
                    canBuy = false;
                    
                    //TRADE HISTORY BASED WEIGHTING
                    if(lastTradeRates.size() >= 5){
                        
                        double lastTradeAverage = 0;
                        for(int ltrIdx = 0;ltrIdx<lastTradeRates.size();ltrIdx++){
                            lastTradeAverage += lastTradeRates.get(ltrIdx);
                        }
                        lastTradeAverage /= lastTradeRates.size();
                        
                        
                        if(AccountData.BALANCE - ((AccountData.BALANCE + AccountData.INVESTED) * 0.05 * Math.pow(lastTradeAverage, BALANCE_WEIGHT)) > 0){
                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED) * 0.05 * Math.pow(lastTradeAverage, BALANCE_WEIGHT);
                        }else{
                            moneyPortion = AccountData.BALANCE;
                        }
                        
                    }else{
                        if((AccountData.BALANCE - (AccountData.BALANCE + AccountData.INVESTED) * 0.05) > 0){
                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED)*0.05;
                        }else{
                            moneyPortion = AccountData.BALANCE;
                        }
                    }
                    AccountData.INVESTED += moneyPortion;
                    AccountData.BALANCE -= moneyPortion;
                    
                    buyPrice = values.get(i-1);
                    
                    buyCount++;
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && ( (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX))) ){
                    canBuy = true;
                    
                    AccountData.INVESTED -= moneyPortion;
                    AccountData.BALANCE += (((values.get(i-1)/buyPrice) - 1) * moneyPortion * 20 + moneyPortion) * 0.99967765713 ;
                    
                    AccountData.balanceChanged = true;
                    lastTradeRates.add(values.get(i-1)/buyPrice);
                    
                    sellCount++;
                    sellSum += values.get(i-1);
                    
                }
            }
            
            try {
                if(AccountData.balanceChanged){
                    System.out.println(fxName + " BALANCE: " + AccountData.BALANCE + ", INVESTED: " + AccountData.INVESTED);
                    
                    AccountData.balanceChanged = false;
                }
                
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(ForexPairData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(buyCount != sellCount){
            sellSum+=buyPrice;
            
            AccountData.INVESTED -= moneyPortion;
            AccountData.BALANCE += moneyPortion;
        }
        
        //System.out.println(this.fxName + ": " + sellSum/buySum);
        System.out.println(fxName + " finalBALANCE: " + AccountData.BALANCE);
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public synchronized double[] startEMADBCrossSimulation(int slowIdx, int medIdx, int fastIdx, double smoothing, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.MED_AVG_IDX = medIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        medAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedEMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), smoothing, delay));
            medAvgs.add(calcDelayedEMA(new ArrayList<Double>(values.subList(i-MED_AVG_IDX, i)), smoothing, delay));
            fastAvgs.add(calcDelayedEMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), smoothing, delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        //To simulate account data
        double moneyPortion = 0;
        double buyPrice = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)) ){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && (fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)) ){
                    canBuy = false;
                    
//                    //TRADE HISTORY BASED WEIGHTING
//                    if(lastTradeRates.size() >= 5){
//                        
//                        double lastTradeAverage = 0;
//                        for(int ltrIdx = 0;ltrIdx<lastTradeRates.size();ltrIdx++){
//                            lastTradeAverage += lastTradeRates.get(ltrIdx);
//                        }
//                        lastTradeAverage /= lastTradeRates.size();
//                        
//                        
//                        if(AccountData.BALANCE - ((AccountData.BALANCE + AccountData.INVESTED) * 0.015 * Math.pow(lastTradeAverage, BALANCE_WEIGHT)) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED) * 0.015 * Math.pow(lastTradeAverage, BALANCE_WEIGHT);
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                        
//                    }else{
//                        if((AccountData.BALANCE - (AccountData.BALANCE + AccountData.INVESTED) * 0.015) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED)*0.015;
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                    }
//                    AccountData.INVESTED += moneyPortion;
//                    AccountData.BALANCE -= moneyPortion;
                    
                    buyPrice = values.get(i-1);
                    
                    buyCount++;
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && ( (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX))) ){
                    canBuy = true;
                    
//                    AccountData.INVESTED -= moneyPortion;
//                    AccountData.BALANCE += (((values.get(i-1)/buyPrice) - 1) * moneyPortion * 20 + moneyPortion) * 0.99967765713 ;
//                    
//                    AccountData.balanceChanged = true;
//                    lastTradeRates.add(values.get(i-1)/buyPrice);
                    
                    sellCount++;
                    sellSum += values.get(i-1);
                    
                }
            }
            
//            try {
//                if(AccountData.balanceChanged){
//                    System.out.println(fxName + " BALANCE: " + AccountData.BALANCE + ", INVESTED: " + AccountData.INVESTED);
//                    
//                    AccountData.balanceChanged = false;
//                }
//                
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ForexPairData.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
        if(buyCount != sellCount){
            sellSum+=buyPrice;
//            
//            AccountData.INVESTED -= moneyPortion;
//            AccountData.BALANCE += moneyPortion;
        }
        
        //System.out.println(this.fxName + ": " + sellSum/buySum);
//        System.out.println(fxName + " finalBALANCE: " + AccountData.BALANCE);
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public synchronized double[] startLWMADBCrossSimulation(int slowIdx, int medIdx, int fastIdx, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.MED_AVG_IDX = medIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        medAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedLWMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), delay));
            medAvgs.add(calcDelayedLWMA(new ArrayList<Double>(values.subList(i-MED_AVG_IDX, i)), delay));
            fastAvgs.add(calcDelayedLWMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        //To simulate account data
        double moneyPortion = 0;
        double buyPrice = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)) ){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && (fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)) ){
                    canBuy = false;
                    
//                    //TRADE HISTORY BASED WEIGHTING
//                    if(lastTradeRates.size() >= 5){
//                        
//                        double lastTradeAverage = 0;
//                        for(int ltrIdx = 0;ltrIdx<lastTradeRates.size();ltrIdx++){
//                            lastTradeAverage += lastTradeRates.get(ltrIdx);
//                        }
//                        lastTradeAverage /= lastTradeRates.size();
//                        
//                        
//                        if(AccountData.BALANCE - ((AccountData.BALANCE + AccountData.INVESTED) * 0.015 * Math.pow(lastTradeAverage, BALANCE_WEIGHT)) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED) * 0.015 * Math.pow(lastTradeAverage, BALANCE_WEIGHT);
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                        
//                    }else{
//                        if((AccountData.BALANCE - (AccountData.BALANCE + AccountData.INVESTED) * 0.015) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED)*0.015;
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                    }
//                    AccountData.INVESTED += moneyPortion;
//                    AccountData.BALANCE -= moneyPortion;
                    
                    buyPrice = values.get(i-1);
                    
                    buyCount++;
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && ( (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX))) ){
                    canBuy = true;
                    
//                    AccountData.INVESTED -= moneyPortion;
//                    AccountData.BALANCE += (((values.get(i-1)/buyPrice) - 1) * moneyPortion * 20 + moneyPortion) * 0.99967765713 ;
//                    
//                    AccountData.balanceChanged = true;
//                    lastTradeRates.add(values.get(i-1)/buyPrice);
                    
                    sellCount++;
                    sellSum += values.get(i-1);
                    
                }
            }
            
//            try {
//                if(AccountData.balanceChanged){
//                    System.out.println(fxName + " BALANCE: " + AccountData.BALANCE + ", INVESTED: " + AccountData.INVESTED);
//                    
//                    AccountData.balanceChanged = false;
//                }
//                
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ForexPairData.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
        if(buyCount != sellCount){
            sellSum+=buyPrice;
            
            AccountData.INVESTED -= moneyPortion;
            AccountData.BALANCE += moneyPortion;
        }
        
        //System.out.println(this.fxName + ": " + sellSum/buySum);
//        System.out.println(fxName + " finalBALANCE: " + AccountData.BALANCE);
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public synchronized double[] startAWMADBCrossSimulation(int slowIdx, int medIdx, int fastIdx, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.MED_AVG_IDX = medIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        medAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        AWMAPowValues = new ArrayList<>();
        for(int i=0;i<SLOW_AVG_IDX;i++){
            AWMAPowValues.add(Math.pow((i+1), 16.0/15.0));
        }
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedAWMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), delay));
            medAvgs.add(calcDelayedAWMA(new ArrayList<Double>(values.subList(i-MED_AVG_IDX, i)), delay));
            fastAvgs.add(calcDelayedAWMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        //To simulate account data
        double moneyPortion = 0;
        double buyPrice = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)) ){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && (fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)) ){
                    canBuy = false;
                    
//                    //TRADE HISTORY BASED WEIGHTING
//                    if(lastTradeRates.size() >= 5){
//                        
//                        double lastTradeAverage = 0;
//                        for(int ltrIdx = 0;ltrIdx<lastTradeRates.size();ltrIdx++){
//                            lastTradeAverage += lastTradeRates.get(ltrIdx);
//                        }
//                        lastTradeAverage /= lastTradeRates.size();
//                        
//                        
//                        if(AccountData.BALANCE - ((AccountData.BALANCE + AccountData.INVESTED) * 0.015 * Math.pow(lastTradeAverage, BALANCE_WEIGHT)) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED) * 0.015 * Math.pow(lastTradeAverage, BALANCE_WEIGHT);
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                        
//                    }else{
//                        if((AccountData.BALANCE - (AccountData.BALANCE + AccountData.INVESTED) * 0.015) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED)*0.015;
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                    }
//                    AccountData.INVESTED += moneyPortion;
//                    AccountData.BALANCE -= moneyPortion;
                    
                    buyPrice = values.get(i-1);
                    
                    buyCount++;
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && ( (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX))) ){
                    canBuy = true;
                    
//                    AccountData.INVESTED -= moneyPortion;
//                    AccountData.BALANCE += (((values.get(i-1)/buyPrice) - 1) * moneyPortion * 20 + moneyPortion) * 0.99967765713 ;
//                    
//                    AccountData.balanceChanged = true;
//                    lastTradeRates.add(values.get(i-1)/buyPrice);
                    
                    sellCount++;
                    sellSum += values.get(i-1);
                    
                }
            }
            
//            try {
//                if(AccountData.balanceChanged){
//                    System.out.println(fxName + " BALANCE: " + AccountData.BALANCE + ", INVESTED: " + AccountData.INVESTED);
//                    
//                    AccountData.balanceChanged = false;
//                }
//                
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ForexPairData.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
        if(buyCount != sellCount){
            sellSum+=buyPrice;
            
//            AccountData.INVESTED -= moneyPortion;
//            AccountData.BALANCE += moneyPortion;
        }
        
        //System.out.println(this.fxName + ": " + sellSum/buySum);
//        System.out.println(fxName + " finalBALANCE: " + AccountData.BALANCE);
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    public synchronized double[] startAWEMADBCrossSimulation(int slowIdx, int medIdx, int fastIdx, double smoothing, int delay){
        this.SLOW_AVG_IDX = slowIdx;
        this.MED_AVG_IDX = medIdx;
        this.FAST_AVG_IDX = fastIdx;
        
        slowAvgs = new ArrayList<>();
        medAvgs = new ArrayList<>();
        fastAvgs = new ArrayList<>();
        
        AWMAPowValues = new ArrayList<>();
        for(int i=0;i<SLOW_AVG_IDX;i++){
            AWMAPowValues.add(Math.pow((i+1), 10.0/11.0));
        }
        
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            slowAvgs.add(calcDelayedAWEMA(new ArrayList<Double>(values.subList(i-SLOW_AVG_IDX, i)), smoothing, delay));
            medAvgs.add(calcDelayedAWEMA(new ArrayList<Double>(values.subList(i-MED_AVG_IDX, i)), smoothing, delay));
            fastAvgs.add(calcDelayedAWEMA(new ArrayList<Double>(values.subList(i-FAST_AVG_IDX, i)), smoothing, delay));
        }
        
        boolean fastAvgInPos = false;
        
        boolean canBuy = true;
        
        double buySum = 0;
        double sellSum = 0;
        
        double buyCount = 0;
        double sellCount = 0;
        
        //To simulate account data
        double moneyPortion = 0;
        double buyPrice = 0;
        
        for(int i=SLOW_AVG_IDX;i<values.size();i++){
            if(!fastAvgInPos && (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX)) ){
                fastAvgInPos = true;
            }
            
            if(fastAvgInPos){
                if(canBuy && (fastAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX) && medAvgs.get(i-SLOW_AVG_IDX) >= slowAvgs.get(i-SLOW_AVG_IDX)) ){
                    canBuy = false;
                    
//                    //TRADE HISTORY BASED WEIGHTING
//                    if(lastTradeRates.size() >= 5){
//                        
//                        double lastTradeAverage = 0;
//                        for(int ltrIdx = 0;ltrIdx<lastTradeRates.size();ltrIdx++){
//                            lastTradeAverage += lastTradeRates.get(ltrIdx);
//                        }
//                        lastTradeAverage /= lastTradeRates.size();
//                        
//                        
//                        if(AccountData.BALANCE - ((AccountData.BALANCE + AccountData.INVESTED) * 0.05 * Math.pow(lastTradeAverage, BALANCE_WEIGHT)) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED) * 0.05 * Math.pow(lastTradeAverage, BALANCE_WEIGHT);
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                        
//                    }else{
//                        if((AccountData.BALANCE - (AccountData.BALANCE + AccountData.INVESTED) * 0.05) > 0){
//                            moneyPortion = (AccountData.BALANCE + AccountData.INVESTED)*0.05;
//                        }else{
//                            moneyPortion = AccountData.BALANCE;
//                        }
//                    }
//                    AccountData.INVESTED += moneyPortion;
//                    AccountData.BALANCE -= moneyPortion;
                    
                    buyPrice = values.get(i-1);
                    
                    buyCount++;
                    buySum += values.get(i-1);
                }
                
                if(!canBuy && ( (fastAvgs.get(i-SLOW_AVG_IDX) < slowAvgs.get(i-SLOW_AVG_IDX))) ){
                    canBuy = true;
                    
//                    AccountData.INVESTED -= moneyPortion;
//                    AccountData.BALANCE += (((values.get(i-1)/buyPrice) - 1) * moneyPortion * 20 + moneyPortion) * 0.99967765713 ;
//                    
//                    AccountData.balanceChanged = true;
//                    lastTradeRates.add(values.get(i-1)/buyPrice);
                    
                    //For variance data
                    if(values.get(i-1)/buyPrice < AccountData.WORST_RATE){
                        AccountData.WORST_RATE = values.get(i-1)/buyPrice;
                    }
                    if(values.get(i-1)/buyPrice > AccountData.BEST_RATE){
                        AccountData.BEST_RATE = values.get(i-1)/buyPrice;
                    }
                    
                    if(values.get(i-1)/buyPrice >= 1.0){
                        AccountData.WIN_COUNT++;
                        AccountData.WIN_SUM += values.get(i-1)/buyPrice;
                    }else{
                        AccountData.LOSE_COUNT++;
                        AccountData.LOSE_SUM += values.get(i-1)/buyPrice;
                    }
                    
                    sellCount++;
                    sellSum += values.get(i-1);
                    
                }
            }
            
//            try {
//                if(AccountData.balanceChanged){
//                    System.out.println(fxName + " BALANCE: " + AccountData.BALANCE + ", INVESTED: " + AccountData.INVESTED);
//                    
//                    AccountData.balanceChanged = false;
//                }
//                
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ForexPairData.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
        if(buyCount != sellCount){
            sellSum+=buyPrice;
            
//            AccountData.INVESTED -= moneyPortion;
//            AccountData.BALANCE += moneyPortion;
        }
        
        //System.out.println(this.fxName + ": " + sellSum/buySum);
//        System.out.println(fxName + " finalBALANCE: " + AccountData.BALANCE);
        
        System.out.println("---" + this.fxName + "---");
        System.out.println("Worst rate: " + AccountData.WORST_RATE);
        System.out.println("Best rate: " + AccountData.BEST_RATE);
        System.out.println("Lose count: " + AccountData.LOSE_COUNT);
        System.out.println("Win count: " + AccountData.WIN_COUNT);
        System.out.println("Lose average: " + AccountData.LOSE_SUM/AccountData.LOSE_COUNT);
        System.out.println("Win average: " + AccountData.WIN_SUM/AccountData.WIN_COUNT);
        
        return new double[]{sellSum/buySum, sellCount};
    }
    
    //OTHERS
    public void readValues(ArrayList<Double> values){
        try {
            Scanner fileScan = new Scanner(new File("./ForexDataInHours/" + fxName + "_H1.txt"));
            //Scanner fileScan = new Scanner(new File("./ForexDataInMinutes/" + fxName));
            
//            int i=0;
            while(fileScan.hasNext()){
//                if(i%30 == 0)
                    values.add(Double.valueOf(fileScan.nextLine()));
                
//                if(fileScan.hasNext())
//                    fileScan.nextLine();
//                
//                i++;
            }
            fileScan.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalcThings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
