package com.athens.athensfx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.*;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import org.controlsfx.control.ToggleSwitch;

import static com.athens.athensfx.Genesis.selectedPopulation;
import static com.athens.athensfx.Genesis.selectedPopulationIndex;

public class WindowController {
    // This is the window controller class. All the interactions
    // (create new population, switch between populations, etc.)
    // are handled here and the respective methods are called to perform those actions.

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
    Button createNew;
    @FXML
    ProgressBar menProgressBar;
    @FXML
    ProgressBar womenProgressBar;
    @FXML
    LineChart<Number, Number> ratioChart;
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
    PieChart.Data p1 = new PieChart.Data("Faithful", 1);
    PieChart.Data p2 = new PieChart.Data("Philanderer", 1);
    PieChart.Data p3 = new PieChart.Data("CoyWoman", 1);
    PieChart.Data p4 = new PieChart.Data("FastWoman", 1);
    private double angle = 0.0;

    @FXML
    protected void onCreateNew() {
        // This method handles the createNew button press.
        // It calls the createPopulation method in Genesis with all the parameters entered by the user.
        // This method also sets the a,b,c labels, refreshed the window reflecting the changes
        // and closes the population creation menu.
        Genesis.createPopulation(Integer.parseInt(a.getText()), Integer.parseInt(b.getText()), Integer.parseInt(c.getText()), menRatioSlider.getValue(), womenRatioSlider.getValue());
        aLabel.setText(a.getText());
        bLabel.setText(b.getText());
        cLabel.setText(c.getText());
        populationReload();
        dropDown.setExpanded(false);
        if (pause.isDisabled()) {
            buttonsStart();
        }
    }

    @FXML
    protected void onPreviousPopulation() {
        // This handles the previousPopulation click. It simply refreshes the view and changes the selectedPopulation.
//      // Obviously this is done only if there is a previous population.
        if (Genesis.selectedPopulationIndex == 0) return;
        selectedPopulation = Genesis.populations.get(--Genesis.selectedPopulationIndex);
        populationReload();
    }

    @FXML
    protected void onNextPopulation() {
        // This handles the nextPopulation click. It simply refreshes the view and changes the selectedPopulation.
//      // Obviously this is done only if there is a next population.
        if (Genesis.selectedPopulationIndex >= Genesis.populations.size() - 1) return;
        selectedPopulation = Genesis.populations.get(++Genesis.selectedPopulationIndex);
        populationReload();
    }

    @FXML
    protected void onPauseToggle() {
        // This method handles the pause/play button.
        // If the population is paused, the run method of PopulationUpdaterLock is put on wait
        // until the user presses the resume button.
        // At that point this method notifies the thread that will then resume.
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


    @FXML
    void setPopulationIterationDelay() {
        // This handles the changes to iterationDelaySlider and so it sets the delay to the iteration
        // (how much time is added after the computation is completed).
        // It can be used to slow down the program for a more in-depth look at how each iteration changes the data.
        selectedPopulation.setIterationDelay((int) iterationDelaySlider.getValue());
    }

    @FXML
    void setCopulatingRatio(){
        int copulatingRatio = (int) copulatingSlider.getValue();
        System.out.println(copulatingRatio);
    }

    @FXML
    void setRefreshDelay() {
        // This handles the changes to refreshDelaySlider and so it sets the data refresh delay.
        // It can be useful on low-end machines to save up on computational resources.
        Genesis.WindowUpdater.refreshDelay = (int) refreshDelaySlider.getValue();
    }

    @FXML
    void setParameters() {
        // This method updates the a,b,c parameters when the user changes them using the sliders.
        // The parameters are updated using the updateParameters method in Population.
        // (check the method in Population for more info).
        // It also updates the a,b,c labels on the window
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
        if (selectedPopulation.isPaused()) return;
        float menRatio = values[4] / (values[3] + values[4]);
        float womenRatio = values[6] / (values[6] + values[5]);
        seriesUpdate(menRatio, womenRatio);
        pieChartUpdate();
        consoleReload(values, menRatio, womenRatio);
        angle += 0.5;
        earth.setRotate(angle);
        if (selectedPopulation.stopper.update(menRatio, womenRatio)) onPauseToggle();
        menProgressBar.setProgress(menRatio);
        menPercentage.setText(Math.round(menRatio * 8) + "/8");
        womenProgressBar.setProgress(womenRatio);
        womenPercentage.setText(Math.round(womenRatio * 6) + "/6");
    }

    void consoleReload(float[] values, float menRatio, float womenRatio) {
        // This method refreshes the "console" you can see on the window, updating the "logs" every iteration.
        console.setText("---------- iteration " + values[0] + " ----------" +
                "\n- Population: " + (int) (((values[1] + values[2]) - values[7])) +
                "\n- F: " + (int) values[4] + ", P: " + (int) values[3] + ", C: " + (int) values[6] + ", S: " + (int) values[5] +
                "\n- Ratio: " + menRatio + ", " + womenRatio +
                "\n- Speed: " + (int) (((values[1] + values[2]) / values[8]) * 1000) + " p/s" + // todo mathematical proof of this thing, maybe V = people/1ms, so by multiplying by 1000, we find people/1000ms
                "\n- Execution Time: " + (int) values[8] + " ms");
    }

    private void seriesUpdate(float menRatio, float womenRatio) {
        // This method updates the chart series with the new ratios from the last iteration.
        selectedPopulation.menHolder.series.getData().add(new LineChart.Data<>(selectedPopulation.menHolder.series.getData().size(), menRatio));
        selectedPopulation.womenHolder.series.getData().add(new LineChart.Data<>(selectedPopulation.womenHolder.series.getData().size(), womenRatio));
    }

    void lineChartReload() {
        // This method updates the line chart
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

    void populationReload() {
        // This method updates the window with the selected population's info
        console.clear();
        pause.setText((selectedPopulation.isPaused()) ? "RESUME" : "PAUSE");
        lineChartReload();
        selectedPopulationID.setText(String.valueOf(selectedPopulationIndex+1) + "/" + String.valueOf(Genesis.populations.size()));
        iterationDelaySlider.setValue(selectedPopulation.getIterationDelay());
        aSlider.setValue(selectedPopulation.a);
        bSlider.setValue(selectedPopulation.b);
        cSlider.setValue(selectedPopulation.c);
        float[] info = selectedPopulation.getInfo();
        float menRatio = info[4] / (info[3] + info[4]);
        float womenRatio = info[6] / (info[6] + info[5]);
        consoleReload(info, menRatio, womenRatio);
        seriesUpdate(menRatio, womenRatio);
        kill.setDisable(Genesis.populations.size() == 1);
    }

    void pieChartUpdate() {
        // This method updates the pie chart values with the latest values.
        p1.setPieValue(selectedPopulation.faithfulMen.get());
        p2.setPieValue(selectedPopulation.philanderers.get());
        p3.setPieValue(selectedPopulation.coyWomen.get());
        p4.setPieValue(selectedPopulation.fastWomen.get());
    }

    public void bakeThePie() {
        // This method updates the pie chart in the window.
        pieChart.setData(FXCollections.observableArrayList(p1, p2, p3, p4));
    }

    void buttonsStart() {
        // This method enables the buttons, when a population is started.
        aSlider.setDisable(false);
        bSlider.setDisable(false);
        cSlider.setDisable(false);
        pause.setDisable(false);
        iterationDelaySlider.setDisable(false);
    }




}