package org.fon.handlers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fon.models.elements.FonElement;

import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseHandler {
    String url = "jdbc:sqlite:fons.db";
    Connection connection = null;
    Statement statement = null;

    Map<String, ObservableList<FonElement>> fonElementListMap = null;
    ObservableList<FonElement> transactionsList = null;

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
            statement.execute("CREATE TABLE IF NOT EXISTS \"İŞLEM GEÇMİŞİ\"(\"AD\", \"Adet\", \"Alış/Satış Fiyatı\", \"Toplam\", \"Talep Edildi\", \"İşlem Tarihi\");");

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

                tmpFonElement.setCount(Math.min(tmpFonElement.getCount() + fonElement.getCount(), 0));

                if ((tmpFonElement.getCount() == 0) && (tmpFonElement.getDemand() <= 0.001)) {
                    fonElementList.remove(tmpFonElement);
                    deleteFon(tmpFonElement);
                    return;
                }

                tmpFonElement.setPrice(newTotalPrice / tmpFonElement.getCount());
                tmpFonElement.setDemand(tmpFonElement.getDemand() + fonElement.getDemand());

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
        char decimalSeparator = ((DecimalFormat) DecimalFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator();

        for (List<FonElement> fonElementList : fonElementListMap.values()) {
            for (FonElement fonElement : fonElementList) {
                String pageContents = null;
                try {
                    pageContents = WebPageReader.readWebPage("https://www.tefas.gov.tr/FonAnaliz.aspx?FonKod=" + fonElement.getName());
                } catch (IOException e) {
                    LogHandler.printStackTrace(e);
                    continue;
                }

                Double price = fonElement.getTodayPrice();
                Double percentage = fonElement.getChangePercentage();

                Pattern pricePattern = Pattern.compile("<li>Son Fiyat \\(TL\\)<br />.*?<span>.*?</span>");
                Matcher priceMatcher = pricePattern.matcher(pageContents);
                if (priceMatcher.find()) {
                    String token = priceMatcher.group();
                    String valueStr = token.substring(token.lastIndexOf("<span>") + 6, token.lastIndexOf("</span>"));

                    price = Double.parseDouble(valueStr.replace(',', '.'));

                    if (price <= 0)
                        continue;
                }

                Pattern percentagePattern = Pattern.compile("Getiri \\(%\\)<br />.*?<span>%.*?</span>");
                Matcher percentageMatcher = percentagePattern.matcher(pageContents);
                if (percentageMatcher.find()) {
                    String token = percentageMatcher.group();
                    String valueStr = token.substring(token.lastIndexOf("<span>%") + 7, token.lastIndexOf("</span>"));

                    percentage = Double.parseDouble(valueStr.replace(',', '.'));
                }

                Double finalPrice = price;
                Double finalPercentage = percentage;

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                String dateTime = dtf.format(LocalDateTime.now());

                Platform.runLater(() -> {
                    fonElement.setTodayPrice(finalPrice);
                    fonElement.setChangePercentage(finalPercentage);
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
                continue;
            }
        }

        try {
            transactionsList = FXCollections.observableArrayList();

            ResultSet rs = statement.executeQuery( "SELECT * FROM \"İŞLEM GEÇMİŞİ\";");
            while (rs.next()) {
                transactionsList.add(new FonElement("",
                        rs.getString(1),
                        rs.getInt(2),
                        Double.parseDouble(rs.getString(3) != null ? rs.getString(3) : "0"),
                        Double.parseDouble(rs.getString(4) != null ? rs.getString(4) : "0"),
                        Double.parseDouble(rs.getString(5) != null ? rs.getString(5) : "0"),
                        0.0,
                        rs.getString(7)));
            }
        } catch (SQLException e) {
            LogHandler.printStackTrace(e);
        }
    }

    public ObservableList<FonElement> getTransactions() {
        return transactionsList;
    }
}
