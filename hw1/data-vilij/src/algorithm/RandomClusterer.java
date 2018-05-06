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
        for(int i = 1; i<= getNumberOfClusters(); i++){
            labelNames.add(i+"");
        }
        Object[] labels =  dataset.getLabels().keySet().toArray();
        int group;
        if(labels.length > labelNames.size()) {
            group = (int) Math.ceil((double) labels.length / (double) labelNames.size());
        }else {
            group = 1;
        }
        System.out.println("group = " + group);
        int k = 0;
        int label = 0;
        int point = 0;
        for(int i = 1; i<=maxIterations; i++){
            //for every iteration the number of points that change = maxIteration/numPoints?
            // OR maxIterations/numInstances?
            System.out.println("Iteration number " + i);
            if(labels.length > maxIterations){
                if(k < group) {
                    for (int j = 0; j < Math.ceil((double) labels.length / (double) maxIterations); j++) {
                        if (point < labels.length && label < labelNames.size()) {
                            dataset.updateLabel((String) labels[point], labelNames.get(label));
                            point++;
                            k++;
                        }
                    }
                }else{
                    k = 0;
                    label ++;
                    for (int j = 0; j < Math.ceil((double) labels.length / (double) maxIterations); j++) {
                        if (point < labels.length && label < labelNames.size()) {
                            dataset.updateLabel((String) labels[point], labelNames.get(label));
                            point++;
                            k++;
                        }
                    }
                }
            }else{
                if(k < group){
                    if (point < labels.length && label < labelNames.size())
                    {
                        dataset.updateLabel((String) labels[point], labelNames.get(label));
                        point++;
                        k++;
                    }
                }else{
                    k = 0;
                    label ++;
                    if (point < labels.length && label < labelNames.size()) {
                        dataset.updateLabel((String) labels[point], labelNames.get(label));
                        point++;
                        k++;
                    }
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
