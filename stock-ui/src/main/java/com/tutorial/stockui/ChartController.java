package com.tutorial.stockui;

import com.tutorial.stockclient.StockClient;
import com.tutorial.stockclient.StockPrice;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static java.lang.String.valueOf;
import static javafx.collections.FXCollections.observableArrayList;

@Component
public class ChartController {

    @FXML
    public LineChart<String, Double> chart;
    private StockClient stockClient;
    private ObservableList<Data<String, Double>> seriesData = observableArrayList();

    public ChartController(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @FXML
    public void initialize() {
        String symbol1 = "SYMBOL1";
        PriceSubscriber priceSubscriber1 = new PriceSubscriber(symbol1);
        stockClient.pricesFor(symbol1).subscribe(priceSubscriber1);

        String symbol2 = "SYMBOL2";
        PriceSubscriber priceSubscriber2 = new PriceSubscriber(symbol2);
        stockClient.pricesFor(symbol2).subscribe(priceSubscriber2);

        ObservableList<Series<String, Double>> data = observableArrayList();
        data.add(priceSubscriber1.getSeries());
        data.add(priceSubscriber2.getSeries());
        chart.setData(data);

    }

    private static class PriceSubscriber implements Consumer<StockPrice> {

        private final ObservableList<Data<String, Double>> seriesData = observableArrayList();
        private final Series<String, Double> series;

        public PriceSubscriber(String symbol) {
            series = new Series<>(symbol, seriesData);
        }

        @Override
        public void accept(StockPrice stockPrice) {
            Platform.runLater(() ->
                    seriesData.addAll(new Data<>(valueOf(stockPrice.getTime().getSecond()),
                            stockPrice.getPrice()))
            );

        }

        public Series<String, Double> getSeries() {
            return series;
        }
    }
}
