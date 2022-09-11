package calcthings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalcThings {
    public static ArrayList<String> fxPairNames;
    public static ArrayList<ForexPairData> forexPairs;
    
    public static TestUtil TU;
    
    public static void main(String[] args) throws FileNotFoundException {
        //Program init
        fxPairNames = new ArrayList<>(Arrays.asList(
            "EURUSD", "EURCHF", "EURGBP", "EURJPY", "EURAUD", "USDCAD", "USDCHF", "USDJPY"
            ,"USDMXN", "GBPCHF", "GBPJPY", "GBPUSD", "AUDJPY", "AUDUSD", "CHFJPY", "NZDJPY"
            ,"NZDUSD", "XAUUSD", "EURCAD", "AUDCAD", "CADJPY", "EURNZD", "GRXEUR", "NZDCAD"
            ,"SGDJPY", "USDHKD", "USDNOK", "USDTRY", "XAUAUD", "AUDCHF", "AUXAUD", "EURHUF"
            ,"EURPLN", "HKXHKD", "NZDCHF", "SPXUSD", "USDHUF", "USDPLN", "USDZAR", "XAUGBP" 
            ,"XAUCHF", "ZARJPY", "BCOUSD", "XAGUSD", "EURCZK", "EURSEK", "GBPAUD", "GBPNZD"
            ,"JPXJPY", "UDXUSD", "USDCZK", "USDSEK", "WTIUSD", "XAUEUR", "AUDNZD", "CADCHF"
            ,"EURDKK", "EURNOK", "EURTRY", "GBPCAD", "NSXUSD", "UKXGBP", "USDDKK", "USDSGD"
            //,"ETXEUR","FRXEUR"
        ));
        
        forexPairs = new ArrayList<>();
        for(int i=0;i<fxPairNames.size();i++){
            forexPairs.add(new ForexPairData(fxPairNames.get(i)));
        }
        
        TU = new TestUtil();
        
        
        
//        //BRUTE FORCE
//        ExecutorService exServ = Executors.newFixedThreadPool(32);
//        
//        int xFrom=222; int xTo=228; 
//        int zFrom=50; int zTo=60; 
//        int yFrom=48;
//        int delayFrom=0; int delayTo=1;
//        double smtFrom=0.0; double smtTo=1.0; double smtStep = 0.1;
//        
//        TU.initEMAArrays(xFrom, xTo, zFrom, zTo, yFrom, delayFrom, delayTo, smtFrom, smtTo, smtStep);
//        
//        int elementIdx = 0;
//        
//        for(int x=xFrom;x<=xTo;x++){
//            for(int z = zFrom;z<=zTo;z++){
//                for(int y=yFrom;y<=z;y++){
//                    for(int delay=delayFrom;delay<=delayTo;delay++){
//                        for(double smtIdx = smtFrom;smtIdx<=smtTo;smtIdx+=smtStep){
//                            final int currElementIdx = elementIdx;
//                            elementIdx++;
//                            
//                            final int finalX = x;
//                            final int finalZ = z;
//                            final int finalY = y;
//                            final int finalDelay = delay;
//                            final double finalSmt = smtIdx;
//                            
//                            exServ.submit(new Runnable(){
//                                @Override
//                                public void run() {
//                                    double rateSum = 0;
//                                    double transactionCount = 0;
//
//                                    for(int i=0;i<forexPairs.size();i++){
//                                        double[] results = forexPairs.get(i).startAWEMADBCrossSimulation(finalX, finalZ, finalY, finalSmt, finalDelay);
//                                        rateSum += results[0];
//                                        transactionCount += results[1];
//                                    }
//
//                                    double resultPart = rateSum/fxPairNames.size();
//                                    //EMA
//                                    String paramString = "Periods: " + finalX + " : " + finalZ + " : " + finalY + ", transaction count: " + transactionCount + ", delay: " + finalDelay + ", smoothing: " + finalSmt;
//                                    
//                                    //LWMA/SMA
//                                    //String paramString = "Periods: " + finalX + " : " + finalZ + " : " + finalY + ", transaction count: " + transactionCount + ", delay: " + finalDelay;
//                                    
//                                    //AWMA
//                                    //String paramString = "Periods: " + finalX + " : " + finalZ + " : " + finalY + ", transaction count: " + transactionCount + ", delay: " + finalDelay;
//                                    TU.results.set(currElementIdx, resultPart);
//                                    TU.resultParams.set(currElementIdx, paramString);
//                                    TU.allRdy.set(currElementIdx, true);
//                                }
//                            });  
//                        }
//                    }   
//                }
//            }
//        }
        
//        //REAL-TIME SIMULATION
//        ExecutorService exServ = Executors.newFixedThreadPool(32);
//        
//        for(int i=0;i<forexPairs.size();i++){
//            final int finalI = i;
//            exServ.submit(new Runnable(){
//
//                @Override
//                public void run() {
//                    forexPairs.get(finalI).startAWEMADBCrossSimulation(222, 46, 46, 0.4, 1);
//                }
//            });
//        }
//        
//        exServ.shutdown();
        
        
        //1 parameterization
        double rateSum = 0;
        double transactionCount = 0;
        
        for(int i=0;i<forexPairs.size();i++){
            double[] results = forexPairs.get(i).startAWEMADBCrossSimulation(222, 46, 46, 0.4, 1);
            rateSum += results[0];
            transactionCount += results[1];
        }

        double resultPart = rateSum/fxPairNames.size();

        //Filling arrays with good results
        TU.results.add(resultPart);

        String paramString = "Periods: " + 222 + " : " + 46 + " : " + 46 + ", transaction count: " + transactionCount + " smoothing: " + 0.4 + ", delay: " + 1;
        TU.resultParams.add(paramString);
        
      
//        int halfMinCounter = 0;
//        //Check if all thread finished
//        while(!checkThreads()){
//            try {
//                Thread.sleep(30000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(CalcThings.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            halfMinCounter++;
//
//            final int finalHalfMinCounter = halfMinCounter;
//            
//            if(finalHalfMinCounter%60==0){
//                Thread writeFileThread = new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        try{
//                            PrintWriter fileWrite = new PrintWriter("./Results/" + new File(finalHalfMinCounter + ".txt"));
//                            for(int i=0;i<TU.results.size();i++){
//                              if(TU.results.get(i) != -1){
//                                  fileWrite.println(TU.results.get(i) + " - " + TU.resultParams.get(i));
//                              }
//                            }
//                            fileWrite.close();  
//                        }catch(Exception ex){
//                            ex.printStackTrace();
//                        }
//
//                    }
//                });
//                writeFileThread.setPriority(writeFileThread.MAX_PRIORITY);
//                writeFileThread.start();
//            }
//        }
        
        for(int i=0;i<TU.results.size()-1;i++){
            for(int j=i+1;j<TU.results.size();j++){
                if(TU.results.get(i) < TU.results.get(j)){
                    double tempRes = TU.results.get(i);
                    String tempResParams = TU.resultParams.get(i);
                    
                    TU.results.set(i, TU.results.get(j));
                    TU.results.set(j, tempRes);
                    
                    TU.resultParams.set(i, TU.resultParams.get(j));
                    TU.resultParams.set(j, tempResParams);
                }
            }
        }
        
        PrintWriter fileWrite = new PrintWriter("./Results/" + new File("finalResults" + ".txt"));
        for(int i=0;i<TU.results.size();i++){
            System.out.println(TU.results.get(i) + " - " + TU.resultParams.get(i));
            fileWrite.println(TU.results.get(i) + " - " + TU.resultParams.get(i));
        }
        fileWrite.close();
        
//        exServ.shutdown();
    }
    
    //OTHERS
    public static void changeTimeFrame(int minutes, String fx){
        try {
            ArrayList<Float> minValues = new ArrayList<>();
            
            Scanner fileScan = new Scanner(new File("./ForexDataInMinutes/" + fx));
            while(fileScan.hasNext()){
                minValues.add(Float.valueOf(fileScan.nextLine()));
            }
            fileScan.close();
            
            //PrintWriter fileWrite = new PrintWriter(new File("./ForexDataInHours/" + fx + "_" + "H1" + ".txt"));
            PrintWriter fileWrite = new PrintWriter(new File("./ForexDataInHalfHours/" + fx + "_" + "M30" + ".txt"));
            for(int i=0;i<minValues.size();i++){
                if(i%minutes == 0){
                    fileWrite.println(minValues.get(i));
                }
            }
            fileWrite.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CalcThings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static boolean checkThreads(){
        int rdyCounter = 0;
        
        for(int i=0;i<TU.allRdy.size();i++){
            if(TU.allRdy.get(i))
                rdyCounter++;
        }
        
        System.out.println((double)rdyCounter/(double)TU.allRdy.size());
        
        return rdyCounter==TU.allRdy.size();
    }
}
