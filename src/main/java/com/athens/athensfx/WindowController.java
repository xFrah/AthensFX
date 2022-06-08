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
    LineChart<Number,Number> ratioChart;
    @FXML
    Slider iterationDelaySlider;
    @FXML
    ToggleSwitch growthSwitch;
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
    Sphere earth;
    private double angle = 0.0;
    ValueAxis<Number> xAxis;
    PieChart.Data p1 = new PieChart.Data("Faithful", 1);
    PieChart.Data p2 = new PieChart.Data("Philanderer", 1);
    PieChart.Data p3 = new PieChart.Data("CoyWoman", 1);
    PieChart.Data p4 = new PieChart.Data("FastWoman", 1);

    @FXML
    protected void onCreateNew() {
        Genesis.createPopulation(Integer.parseInt(a.getText()), Integer.parseInt(b.getText()), Integer.parseInt(c.getText()), menRatioSlider.getValue(), womenRatioSlider.getValue());
        aLabel.setText(a.getText());
        bLabel.setText(b.getText());
        cLabel.setText(c.getText());
        populationReload();
        dropDown.setExpanded(false);
        if (pause.isDisabled()) { buttonsStart(); }
    }

    @FXML
    protected void onPauseToggle() {
        if (selectedPopulation.isPaused()) {
            selectedPopulation.setPaused(false);
            synchronized (selectedPopulation.pauseLock) {selectedPopulation.pauseLock.notifyAll();}
            pause.setText("PAUSE");
        } else {
            selectedPopulation.setPaused(true);
            pause.setText("RESUME");
        }
    }

    @FXML
    protected void onPreviousPopulation() {
        if (Genesis.selectedPopulationIndex == 0) return;
        selectedPopulation = Genesis.populations.get(--Genesis.selectedPopulationIndex);
        populationReload();
    }

    @FXML
    protected void onNextPopulation() {
        if (Genesis.selectedPopulationIndex >= Genesis.populations.size() - 1) return;
        selectedPopulation = Genesis.populations.get(++Genesis.selectedPopulationIndex);
        populationReload();
    }

    @FXML
    void setPopulationIterationDelay() {
        selectedPopulation.setIterationDelay((int) iterationDelaySlider.getValue());
    }

    @FXML
    void setPopulationGrowth() {
        selectedPopulation.setGrowth(growthSwitch.isSelected());
    }

    @FXML
    void setRefreshDelay() {
        Genesis.WindowUpdater.refreshDelay = (int) refreshDelaySlider.getValue();
    }

    @FXML
    void setParameters() {
        selectedPopulation.a = (int) aSlider.getValue();
        selectedPopulation.b = (int) bSlider.getValue();
        selectedPopulation.c = (int) cSlider.getValue();
        selectedPopulation.updateParameters();
        aLabel.setText(String.valueOf(selectedPopulation.a));
        bLabel.setText(String.valueOf(selectedPopulation.b));
        cLabel.setText(String.valueOf(selectedPopulation.c));
    }

    void setInfo(float[] values) {
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
        console.setText("---------- iteration " + values[0] + " ----------" +
                "\n- Population: " + (int) (((values[1] + values[2]) - values[7])) +
                "\n- F: " + (int) values[4] + ", P: " + (int) values[3] + ", C: " + (int) values[6] + ", S: " + (int) values[5] +
                "\n- Ratio: " + menRatio + ", " + womenRatio +
                "\n- Speed: " + (int) (((values[1]+values[2]) / values[8]) * 1000) + " p/s"+ // todo mathematical proof of this thing, maybe V = people/1ms, so by multiplying by 1000, we find people/1000ms
                "\n- Execution Time: " + (int) values[8] + " ms");
    }

    private void seriesUpdate(float menRatio, float womenRatio) {
        selectedPopulation.menHolder.series.getData().add(new LineChart.Data<>(selectedPopulation.menHolder.series.getData().size(), menRatio));
        selectedPopulation.womenHolder.series.getData().add(new LineChart.Data<>(selectedPopulation.womenHolder.series.getData().size(), womenRatio));
    }

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

    void populationReload() {
        console.clear();
        pause.setText((selectedPopulation.isPaused()) ? "RESUME" : "PAUSE");
        lineChartReload();
        selectedPopulationID.setText(String.valueOf(Genesis.selectedPopulationIndex));
        iterationDelaySlider.setValue(selectedPopulation.getIterationDelay());
        growthSwitch.setSelected(selectedPopulation.isGrowing());
        aSlider.setValue(selectedPopulation.a);
        bSlider.setValue(selectedPopulation.b);
        cSlider.setValue(selectedPopulation.c);
        float [] info = selectedPopulation.getInfo();
        float menRatio = info[4] / (info[3] + info[4]);
        float womenRatio = info[6] / (info[6] + info[5]);
        consoleReload(info, menRatio, womenRatio);
        seriesUpdate(menRatio, womenRatio);
    }

    void pieChartUpdate() {
        p1.setPieValue(selectedPopulation.faithfulMen.get());
        p2.setPieValue(selectedPopulation.philanderers.get());
        p3.setPieValue(selectedPopulation.coyWomen.get());
        p4.setPieValue(selectedPopulation.fastWomen.get());
    }

    void buttonsStart () {
        aSlider.setDisable(false);
        bSlider.setDisable(false);
        cSlider.setDisable(false);
        pause.setDisable(false);
        iterationDelaySlider.setDisable(false);
        growthSwitch.setDisable(false);
    }

    public void bakeThePie() {
        pieChart.setData(FXCollections.observableArrayList(p1, p2, p3, p4));
    }

}