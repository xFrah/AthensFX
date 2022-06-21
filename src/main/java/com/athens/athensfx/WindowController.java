package com.athens.athensfx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;

import static com.athens.athensfx.Genesis.selectedPopulation;
import static com.athens.athensfx.Genesis.selectedPopulationIndex;

/** This is the window controller class. All the interactions
* (create new population, switch between populations, etc.)
* are handled here and the respective methods are called to perform those actions. */
public class WindowController {

    @FXML
    TextArea console;
    @FXML
    Text aLabel;
    @FXML
    Text bLabel;
    @FXML
    Text cLabel;
    @FXML
    Text selectedPopulationID;
    @FXML
    Text menPercentage;
    @FXML
    Text womenPercentage;
    @FXML
    Button nextPopulation;
    @FXML
    Button previousPopulation;
    @FXML
    Button pause;
    @FXML
    Button kill;
    @FXML
    TextField a;
    @FXML
    TextField b;
    @FXML
    TextField c;
    @FXML
    TextField startingPopulationInput;
    @FXML
    Button createNew;
    @FXML
    ProgressBar menProgressBar;
    @FXML
    ProgressBar womenProgressBar;
    @FXML
    LineChart<Number, Number> ratioChart;
    @FXML
    AreaChart<Number, Number> memoryChart;
    @FXML
    Slider iterationDelaySlider;
    @FXML
    Slider refreshDelaySlider;
    @FXML
    PieChart pieChart;
    @FXML
    Slider womenRatioSlider;
    @FXML
    Slider menRatioSlider;
    @FXML
    TitledPane dropDown;
    @FXML
    Slider aSlider;
    @FXML
    Slider bSlider;
    @FXML
    Slider cSlider;
    @FXML
    Slider copulatingSlider;
    @FXML
    Sphere earth;
    ValueAxis<Number> xAxis;
    ValueAxis<Number> xAxis2;
    XYChart.Series<Number,Number> memorySeries = new XYChart.Series<>();
    PieChart.Data p1 = new PieChart.Data("Faithful", 1);
    PieChart.Data p2 = new PieChart.Data("Philanderer", 1);
    PieChart.Data p3 = new PieChart.Data("CoyWoman", 1);
    PieChart.Data p4 = new PieChart.Data("FastWoman", 1);
    private double angle = 0.0;
    private final Runtime runtime = Runtime.getRuntime();

    /** This method handles the createNew button press.
    * It calls the createPopulation method in Genesis with all the parameters entered by the user.
    * This method also sets the a,b,c labels, refreshed the window reflecting the changes
    * and closes the population creation menu. */
    @FXML
    protected void onCreateNew() {
        Genesis.createPopulation(Integer.parseInt(a.getText()), Integer.parseInt(b.getText()), Integer.parseInt(c.getText()), menRatioSlider.getValue(), womenRatioSlider.getValue(),Integer.parseInt(startingPopulationInput.getText()));
        aLabel.setText(a.getText());
        bLabel.setText(b.getText());
        cLabel.setText(c.getText());
        populationReload();
        dropDown.setExpanded(false);
        if (pause.isDisabled()) {
            buttonsStart();
        }
    }

    /** This handles the previousPopulation click. It simply refreshes the view and changes the selectedPopulation.
    * Obviously this is done only if there is a previous population. */
    @FXML
    protected void onPreviousPopulation() {
        if (Genesis.selectedPopulationIndex == 0) return;
        selectedPopulation = Genesis.populations.get(--Genesis.selectedPopulationIndex);
        populationReload();
    }

    /** This handles the nextPopulation click. It simply refreshes the view and changes the selectedPopulation.
    * Obviously this is done only if there is a next population. */
    @FXML
    protected void onNextPopulation() {
        if (Genesis.selectedPopulationIndex >= Genesis.populations.size() - 1) return;
        selectedPopulation = Genesis.populations.get(++Genesis.selectedPopulationIndex);
        populationReload();
    }

    /** This method handles the pause/play button.
    * If the population is paused, the run method of PopulationUpdaterLock is put on wait
    * until the user presses the resume button.
    * At that point this method notifies the thread that will then resume. */
    @FXML
    protected void onPauseToggle() {
        if (selectedPopulation.isPaused()) {
            selectedPopulation.setPaused(false);
            synchronized (selectedPopulation.pauseLock) {
                selectedPopulation.pauseLock.notifyAll(); // resume the process
            }
            pause.setText("PAUSE");
        } else {
            selectedPopulation.setPaused(true);
            pause.setText("RESUME");
        }
    }

    @FXML
    protected void onKill(){
        selectedPopulation.stop();
        Genesis.populations.remove(selectedPopulation);
        if((Genesis.populations.size() == selectedPopulationIndex)){
            selectedPopulationIndex--;
        }
        selectedPopulation = Genesis.populations.get(
                selectedPopulationIndex);
        populationReload();
    }

    /** This handles the changes to iterationDelaySlider and so it sets the delay to the iteration
    * (how much time is added after the computation is completed).
    * It can be used to slow down the program for a more in-depth look at how each iteration changes the data. */
    @FXML
    void setPopulationIterationDelay() {
        selectedPopulation.setIterationDelay((int) iterationDelaySlider.getValue());
    }

    @FXML
    void setCopulatingRatio(){  // todo check if this returns the correct values.
        double copulatingRatio = copulatingSlider.getValue()/100.0;
        selectedPopulation.setCopulatingRatio(copulatingRatio);
    }

    /** This handles the changes to refreshDelaySlider and so it sets the data refresh delay.
     It can be useful on low-end machines to save up on computational resources. */
    @FXML
    void setRefreshDelay() {
        Genesis.WindowUpdater.refreshDelay = (int) refreshDelaySlider.getValue();
    }

    /** This method updates the a,b,c parameters when the user changes them using the sliders.
    * The parameters are updated using the updateParameters method in Population.
    * (check the method in Population for more info).
    * It also updates the a,b,c labels on the window. */
    @FXML
    void setParameters() {
        int aNew = (int) aSlider.getValue();
        int bNew = (int) bSlider.getValue();
        int cNew = (int) cSlider.getValue();

        selectedPopulation.a = aNew;
        selectedPopulation.b = bNew;
        selectedPopulation.c = cNew;
        selectedPopulation.updateParameters();

        aLabel.setText(String.valueOf(aNew));
        bLabel.setText(String.valueOf(bNew));
        cLabel.setText(String.valueOf(cNew));
    }

    void setInfo(float[] values) {
        // todo write comment for this
        int size = selectedPopulation.womenHolder.series.getData().size();
        if (size > 100) { // TODO this needs an optimization
            xAxis.setLowerBound(size - 100);
            xAxis.setUpperBound(size);
        }
        int memSize = memorySeries.getData().size();
        if (memSize > 100) { // TODO this needs an optimization
            xAxis2.setLowerBound(memSize - 100);
            xAxis2.setUpperBound(memSize);
        }
        memoryUpdate();
        if (selectedPopulation.isPaused()) return;
        float menRatio = values[4] / (values[3] + values[4]);
        float womenRatio = values[6] / (values[6] + values[5]);
        seriesUpdate(menRatio, womenRatio);
        pieChartUpdate();
        consoleReload(values, menRatio, womenRatio);
        angle += 0.5;
        earth.setRotate(angle);
        if (selectedPopulation.pauser.update(menRatio, womenRatio)) onPauseToggle();
        menProgressBar.setProgress(menRatio);
        menPercentage.setText(Math.round(menRatio * 8) + "/8");
        womenProgressBar.setProgress(womenRatio);
        womenPercentage.setText(Math.round(womenRatio * 6) + "/6");
    }

    /** This method refreshes the "console" you can see on the window, updating the "logs" every iteration. */
    void consoleReload(float[] values, float menRatio, float womenRatio) {
        console.setText("---------- iteration " + values[0] + " ----------" +
                "\n- Population: " + (int) (((values[1] + values[2]) - values[7])) +
                "\n- F: " + (int) values[4] + ", P: " + (int) values[3] + ", C: " + (int) values[6] + ", S: " + (int) values[5] +
                "\n- Ratio: " + menRatio + ", " + womenRatio +
                "\n- Speed: " + (int) (((values[1] + values[2]) / values[8]) * 1000) + " p/s" + // todo mathematical proof of this thing, maybe V = people/1ms, so by multiplying by 1000, we find people/1000ms
                "\n- Execution Time: " + (int) values[8] + " ms");
    }

    /** This method updates the chart series with the new ratios from the last iteration. */
    private void seriesUpdate(float menRatio, float womenRatio) {
        selectedPopulation.menHolder.series.getData().add(new LineChart.Data<>(selectedPopulation.menHolder.series.getData().size(), menRatio));
        selectedPopulation.womenHolder.series.getData().add(new LineChart.Data<>(selectedPopulation.womenHolder.series.getData().size(), womenRatio));
    }

    /** This method updates the chart series with the new ratios from the last iteration. */
    void memoryUpdate() {
        memorySeries.getData().add(new AreaChart.Data<>(memorySeries.getData().size(), ((float) (runtime.totalMemory() - runtime.freeMemory())/(float) runtime.maxMemory())*100));
    }

    /** This method updates the line chart */
    void lineChartReload() {
        ratioChart.getData().clear();
        ratioChart.getData().add(selectedPopulation.womenHolder.series);
        ratioChart.getData().add(selectedPopulation.menHolder.series);
        int size = selectedPopulation.womenHolder.series.getData().size();
        if (size > 100) {
            xAxis.setLowerBound(size - 100);
            xAxis.setUpperBound(size);
        } else {
            xAxis.setLowerBound(0);
            xAxis.setUpperBound(100);
        }
    }

    /** This method updates the window with the selected population's info */
    void populationReload() {
        console.clear();
        pause.setText((selectedPopulation.isPaused()) ? "RESUME" : "PAUSE");
        lineChartReload();
        selectedPopulationID.setText(selectedPopulationIndex + 1 + "/" + Genesis.populations.size());
        iterationDelaySlider.setValue(selectedPopulation.getIterationDelay());
        aSlider.setValue(selectedPopulation.a);
        bSlider.setValue(selectedPopulation.b);
        cSlider.setValue(selectedPopulation.c);
        copulatingSlider.setValue(selectedPopulation.copulatingRatio*100);
        float[] info = selectedPopulation.getInfo();
        float menRatio = info[4] / (info[3] + info[4]);
        float womenRatio = info[6] / (info[6] + info[5]);
        consoleReload(info, menRatio, womenRatio);
        seriesUpdate(menRatio, womenRatio);
        kill.setDisable(Genesis.populations.size() == 1);
    }

    /** This method updates the pie chart values with the latest values. */
    void pieChartUpdate() {
        p1.setPieValue(selectedPopulation.faithfulMen.get());
        p2.setPieValue(selectedPopulation.philanderers.get());
        p3.setPieValue(selectedPopulation.coyWomen.get());
        p4.setPieValue(selectedPopulation.fastWomen.get());
    }

    /** This method updates the pie chart in the window. */
    public void bakeThePie() {
        pieChart.setData(FXCollections.observableArrayList(p1, p2, p3, p4));
    }

    /** This method enables the buttons, when a population is started. */
    void buttonsStart() {
        aSlider.setDisable(false);
        bSlider.setDisable(false);
        cSlider.setDisable(false);
        pause.setDisable(false);
        iterationDelaySlider.setDisable(false);
        refreshDelaySlider.setDisable(false);
        copulatingSlider.setDisable(false);
    }




}