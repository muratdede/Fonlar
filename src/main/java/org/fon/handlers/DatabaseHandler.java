package org.fon.handlers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.fon.models.elements.FonElement;
import org.fon.models.elements.TransactionElement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fon.handlers.TefasParser.DEFAULT_PERIYOD;

public class DatabaseHandler {
    String url = "jdbc:sqlite:fons.db";
    Connection connection = null;
    Statement statement = null;

    Map<String, ObservableList<FonElement>> fonElementListMap = null;
    ObservableList<TransactionElement> transactionsList = null;

    String[] fonCategories = {"HİSSE FONLARI",
            "KIYMETLİ MADENLER",
            "DEĞİŞKEN",
            "KARMA",
            "FON SEPETİ",
            "KATILIM",
            "PARA PİYASASI"};

    public ObservableList<String> getFonCategories() {

        return FXCollections.observableArrayList(fonCategories);
    }

    public DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();

            for (String fonCategory : fonCategories) {
                statement.execute("CREATE TABLE IF NOT EXISTS \"" + fonCategory + "\"(\"AD\", \"Adet\", \"Birim Maliyet\", \"Birim Fiyatı\", \"% Değişim\", \"Talep Edildi\", \"Güncellenme Tarihi\");");
            }
            statement.execute("CREATE TABLE IF NOT EXISTS \"İŞLEM GEÇMİŞİ\"(\"AD\", \"Adet\", \"Alış/Satış Fiyatı\", \"İşlem Tarihi\");");

        } catch (SQLException e) {
            LogHandler.printStackTrace(e);
            Platform.exit();
        }

        updateFonElementList();
    }

    public ObservableList<FonElement> getFonList(String fonCategory) {
        return fonElementListMap.getOrDefault(fonCategory, null);
    }


    public void addFon(FonElement fonElement) {
        ObservableList<FonElement> fonElementList = getFonList(fonElement.getCategory());

        for (FonElement tmpFonElement : fonElementList) {
            if (tmpFonElement.getName().equals(fonElement.getName())) {
                Double newTotalPrice = tmpFonElement.getPrice() * tmpFonElement.getCount() + fonElement.getPrice() * fonElement.getCount();

                tmpFonElement.setCount(Math.max(tmpFonElement.getCount() + fonElement.getCount(), 0));

                if ((tmpFonElement.getCount() <= 0) && (tmpFonElement.getDemand() <= 0.001)) {
                    fonElementList.remove(tmpFonElement);
                    deleteFon(tmpFonElement);
                    return;
                }

                if (fonElement.getCount() > 0)
                    tmpFonElement.setPrice(newTotalPrice / tmpFonElement.getCount());

                tmpFonElement.setDemand(Math.max(tmpFonElement.getDemand() + fonElement.getDemand(), 0));

                updateFon(tmpFonElement);
                return;
            }
        }

        fonElement.setCount(Math.max(fonElement.getCount(), 0));
        if ((fonElement.getCount() > 0) || (fonElement.getDemand() >= 0.001)) {
            fonElementList.add(fonElement);
            insertFon(fonElement);
        }
    }

    public void updatePrices() {
        for (List<FonElement> fonElementList : fonElementListMap.values()) {
            for (FonElement fonElement : fonElementList) {
                JSONObject response;
                try {
                    response = TefasApiClient.fetchFonPriceData(fonElement.getName(), DEFAULT_PERIYOD);
                } catch (IOException e) {
                    LogHandler.printStackTrace(e);
                    continue;
                }

                JSONArray resultList = response.getJSONArray("resultList");
                if (resultList.isEmpty()) {
                    throw new RuntimeException("API'den veri alınamadı: " + fonElement.getName());
                }

                JSONObject todayObject = resultList.getJSONObject(0);
                JSONObject yesterdayObject = resultList.getJSONObject(0);

                double todayPrice = todayObject.getDouble("fiyat");
                if (todayPrice <= 0.0) {
                    continue;
                }

                double yesterdayPrice = yesterdayObject.getDouble("fiyat");
                double changePercentage = (todayPrice - yesterdayPrice) / yesterdayPrice;

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                String dateTime = dtf.format(LocalDateTime.now());

                Platform.runLater(() -> {
                    fonElement.setTodayPrice(todayPrice);
                    fonElement.setChangePercentage(changePercentage);
                    fonElement.setLastUpdate(dateTime);
                    new Thread(()-> updateFon(fonElement)).start();
                });
            }
        }
    }

    public synchronized void insertFon(FonElement fonElement) {
        try {
            statement.executeUpdate(fonElement.toSqlInsertString());
        } catch (SQLException e) {
            LogHandler.printStackTrace(e);
        }
    }

    public synchronized void updateFon(FonElement fonElement) {
        try {
            statement.executeUpdate(fonElement.toSqlUpdateString());
        } catch (SQLException e) {
            LogHandler.printStackTrace(e);
        }
    }

    public synchronized void deleteFon(FonElement fonElement) {
        try {
            statement.executeUpdate(fonElement.toSqlRemoveString());
        } catch (SQLException e) {
            LogHandler.printStackTrace(e);
        }
    }

    private void updateFonElementList() {
        fonElementListMap = new HashMap<>();

        for (String fonCategory : fonCategories) {
            ObservableList<FonElement> fonElementList = FXCollections.observableArrayList();
            try {
                ResultSet rs = statement.executeQuery( "SELECT * FROM \"" + fonCategory + "\";");

                while (rs.next()) {
                    fonElementList.add(new FonElement(fonCategory,
                            rs.getString(1),
                            rs.getInt(2),
                            Double.parseDouble(rs.getString(3) != null ? rs.getString(3) : "0"),
                            Double.parseDouble(rs.getString(4) != null ? rs.getString(4) : "0"),
                            Double.parseDouble(rs.getString(5) != null ? rs.getString(5) : "0"),
                            Double.parseDouble(rs.getString(6) != null ? rs.getString(6) : "0"),
                            rs.getString(7)));
                }
                fonElementListMap.put(fonCategory, fonElementList);
            } catch (SQLException e) {
                LogHandler.printStackTrace(e);
            }
        }

        try {
            transactionsList = FXCollections.observableArrayList();

            ResultSet rs = statement.executeQuery( "SELECT * FROM \"İŞLEM GEÇMİŞİ\";");
            while (rs.next()) {
                transactionsList.add(0, new TransactionElement(
                        rs.getString(1),
                        rs.getInt(2),
                        Double.parseDouble(rs.getString(3) != null ? rs.getString(3) : "0"),
                        rs.getString(4)));
            }
        } catch (SQLException e) {
            LogHandler.printStackTrace(e);
        }
    }

    public ObservableList<TransactionElement> getTransactions() {
        return transactionsList;
    }

    public void insertTransaction(FonElement fonElement) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
        String dateTime = dtf.format(LocalDateTime.now());

        TransactionElement transactionElement = new TransactionElement(fonElement.getName(),
                fonElement.getCount(),
                fonElement.getPrice(),
                dateTime);

        transactionsList.add(0, transactionElement);
        try {
            statement.executeUpdate(transactionElement.toSqlInsertString());
        } catch (SQLException e) {
            LogHandler.printStackTrace(e);
        }
    }
}
