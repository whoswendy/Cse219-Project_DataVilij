package algorithm;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer{

    private DataSet       dataset;
    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;
    private boolean             stop;
    private int num;

    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean cont) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public DataSet getDataset() {
        return dataset;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    @Override
    public void run() {
        num = 0;
        ArrayList<String> labelNames = new ArrayList<>();
        Object[] labels =  dataset.getLabels().keySet().toArray();
        for(int i = 1; i<=maxIterations; i++){
            int rand = (int)(Math.random() * getNumberOfClusters());
            //for every iteration the number of points that change = maxIteration/numPoints?
            // OR maxIterations/numInstances?

            labelNames.add(rand+"");

//            int size = labels.length;
//            int k = (int) (maxIterations/getNumberOfClusters());
//
//            if(size - k > 0) {
//                for (int j = 0; j < k; j++) {
//                    if(num == labels.length) num = (int) (Math.random() * labels.length);                    System.out.println("num = " + num);
//                    dataset.updateLabel((String) labels[num], rand + "");
//                    num++;
//                    size = size - 1;
//                }
//            }else{
//                for (int j = 0; j < (size - k); j++) {
//                    if(num == labels.length) num = (int) (Math.random() * labels.length);
//                    dataset.updateLabel((String) labels[num], rand + "");
//                    num++;
//                    size = size - 1;
//                }
//            }

            if(labels.length > maxIterations){
                int rand2 = (int)(Math.random() * 3 +1);
                int k = i;
                for(int j = 0; j<rand2; j++) {
                    if (k < labels.length) {
                        System.out.println("j = " + j + "k = " + k);
                        dataset.updateLabel((String) labels[k], rand + "");
                        k++;
                    }
                }
            }else {
                if (i < labels.length) {
                    dataset.updateLabel((String) labels[i], rand + "");

                }
            }
            stop = true;
            guarded();
        }

    }

    @Override
    public boolean getStop() {
        return stop;
    }

    @Override
    public synchronized void resume() {
        stop = false;
        notify();
    }

    @Override
    public synchronized void guarded() {
        while(stop){
            try{
                wait();
            }catch (InterruptedException e){
                stop = false;
            }
        }
    }
}
