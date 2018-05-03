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

        ArrayList<String> labelNames = new ArrayList<>();
        Object[] labels =  dataset.getLabels().keySet().toArray();
        for(int i = 0; i<maxIterations && i < getNumberOfClusters(); i++){
            int rand = (int)(Math.random() * getNumberOfClusters());
            //for every iteration the number of points that change = maxIteration/numPoints?
            // OR maxIterations/numInstances?

            labelNames.add(rand+"");

            dataset.updateLabel((String) labels[i],rand+"");
            System.out.println("size = " + dataset.getLabels().size());
            System.out.println("random");
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
