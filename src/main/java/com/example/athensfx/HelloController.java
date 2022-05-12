package com.example.athensfx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class HelloController {

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
    protected void onCreateNew() {
        Genesis.createPopulation(Integer.parseInt(a.getText()), Integer.parseInt(b.getText()), Integer.parseInt(c.getText()));
        aLabel.setText(a.getText());
        bLabel.setText(b.getText());
        cLabel.setText(c.getText());
        selectedPopulationID.setText(String.valueOf(Genesis.selectedPopulationIndex));
    }

    @FXML
    protected void onPreviousPopulation() {
        if (Genesis.selectedPopulationIndex == 0) return;
        Genesis.selectedPopulation = Genesis.populations.get(--Genesis.selectedPopulationIndex);
        selectedPopulationID.setText(String.valueOf(Genesis.selectedPopulationIndex));
    }

    @FXML
    protected void onNextPopulation() {
        if (Genesis.selectedPopulationIndex >= Genesis.populations.size() - 1) return;
        Genesis.selectedPopulation = Genesis.populations.get(++Genesis.selectedPopulationIndex);
        selectedPopulationID.setText(String.valueOf(Genesis.selectedPopulationIndex));
    }

    void setInfo(float[] values) {
        menProgressBar.setProgress(values[1]);
        menPercentage.setText(String.valueOf(values[1]));
        womenProgressBar.setProgress(values[2]);
        womenPercentage.setText(String.valueOf(values[2]));
        alivePeople.setText(String.valueOf((int) (values[3] + values[4])));
        philanderers.setText(String.valueOf((int) values[5]));
        faithfulMen.setText(String.valueOf((int) values[6]));
        fastWomen.setText(String.valueOf((int) values[7]));
        coyWomen.setText(String.valueOf((int) values[8]));
    }


}