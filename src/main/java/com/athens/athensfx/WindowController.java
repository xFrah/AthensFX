package com.athens.athensfx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import static com.athens.athensfx.Genesis.populations;
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
    Text alivePeople;
    @FXML
    Text philanderers;
    @FXML
    Text faithfulMen;
    @FXML
    Text coyWomen;
    @FXML
    Text fastWomen;
    @FXML
    Text selectedPopulationID;
    @FXML
    Text menPercentage;
    @FXML
    Text womenPercentage;
    @FXML
    Button play;
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
    Slider growthIndexSlider;
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
    PieChart.Data p1 = new PieChart.Data("Faithful", 0);
    PieChart.Data p2 = new PieChart.Data("Philanderer", 0);
    PieChart.Data p3 = new PieChart.Data("CoyWoman", 0);
    PieChart.Data p4 = new PieChart.Data("FastWoman", 0);


    @FXML
    protected void onCreateNew() {
        Genesis.createPopulation(Integer.parseInt(a.getText()), Integer.parseInt(b.getText()), Integer.parseInt(c.getText()));
        aLabel.setText(a.getText());
        bLabel.setText(b.getText());
        cLabel.setText(c.getText());
        populationChange();
        dropDown.setExpanded(false);
    }

    @FXML
    protected void onPreviousPopulation() {
        if (Genesis.selectedPopulationIndex == 0) return;
        selectedPopulation = Genesis.populations.get(--Genesis.selectedPopulationIndex);
        populationChange();
    }

    @FXML
    protected void onNextPopulation() {
        if (Genesis.selectedPopulationIndex >= Genesis.populations.size() - 1) return;
        selectedPopulation = Genesis.populations.get(++Genesis.selectedPopulationIndex);
        populationChange();
    }

    @FXML
    void setPopulationIterationDelay() {
        selectedPopulation.iterationDelay = (int) iterationDelaySlider.getValue();
    }

    @FXML
    void setPopulationGrowthIndex() {
        selectedPopulation.growthIndex = (float) growthIndexSlider.getValue();
    }

    @FXML
    void setRefreshDelay() {
        Genesis.WindowUpdater.refreshDelay = (int) refreshDelaySlider.getValue();
    }

    void setInfo(float[] values) {
        menProgressBar.setProgress(values[1]);
        menPercentage.setText(String.valueOf(values[1]));
        womenProgressBar.setProgress(values[2]);
        womenPercentage.setText(String.valueOf(values[2]));
        alivePeople.setText(String.valueOf((int) (values[3] + values[4]))); // does it continue to update if we do this just once?
        philanderers.setText(String.valueOf((int) values[5]));
        faithfulMen.setText(String.valueOf((int) values[6]));
        fastWomen.setText(String.valueOf((int) values[7]));
        coyWomen.setText(String.valueOf((int) values[8]));
        pieChartUpdate();
        if (selectedPopulation.seriesWomen.getData().size() > 100) { // TODO this needs an optimization
            ((ValueAxis<Number>) ratioChart.getXAxis()).setLowerBound(selectedPopulation.seriesWomen.getData().size() - 100);
            ((ValueAxis<Number>) ratioChart.getXAxis()).setUpperBound(selectedPopulation.seriesWomen.getData().size());
            //selectedPopulation.seriesWomen.getData().remove(0);
            //selectedPopulation.seriesMen.getData().remove(0);

        }
        // console.appendText("\n---- iteration " + values[0] + " ----" +
        //        "\n- Population: " + (int) (values[3] + values[4]) + "(" + (int) values[3] + ", " + (int) values[4] + ")" +
        //        "\n- Normals(M, F): " + (int) values[6] + ", " + (int) values[8] + " = " + ((int) (values[6] + values[8])) +
        //        "\n- Hornies(M, F): " + (int) values[5] + ", " + (int) values[5] + " = " + ((int) (values[5] + values[7])) +
        //        "\n- Ratio: " + values[1] + ", " + values[2] + "\n");
    }

    void lineChartUpdate() {
        ratioChart.getData().clear();
        ratioChart.getData().add(selectedPopulation.seriesMen);
        ratioChart.getData().add(selectedPopulation.seriesWomen);
        if (selectedPopulation.seriesWomen.getData().size() > 100) { // TODO this needs an optimization
            ((ValueAxis<Number>) ratioChart.getXAxis()).setLowerBound(selectedPopulation.seriesWomen.getData().size() - 100);
            ((ValueAxis<Number>) ratioChart.getXAxis()).setUpperBound(selectedPopulation.seriesWomen.getData().size());
        } else {
            ((ValueAxis<Number>) ratioChart.getXAxis()).setLowerBound(0);
            ((ValueAxis<Number>) ratioChart.getXAxis()).setUpperBound(100);
        }
    }

    void populationChange() {
        console.clear();
        lineChartUpdate();
        selectedPopulationID.setText(String.valueOf(Genesis.selectedPopulationIndex));
        iterationDelaySlider.setValue(selectedPopulation.iterationDelay);
        growthIndexSlider.setValue(selectedPopulation.growthIndex);
    }

    void pieChartUpdate() {
        p1.setPieValue(selectedPopulation.faithfulMen.get());
        p2.setPieValue(selectedPopulation.philanderers.get());
        p3.setPieValue(selectedPopulation.coyWomen.get());
        p4.setPieValue(selectedPopulation.fastWomen.get());
    }


    public void bakeThePie() {
        pieChart.setData(FXCollections.observableArrayList(p1, p2, p3, p4));
    }

}