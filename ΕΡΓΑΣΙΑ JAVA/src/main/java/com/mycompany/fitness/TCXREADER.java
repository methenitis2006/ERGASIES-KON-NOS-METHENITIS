/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.fitness;

/**
 *
 * @author user
 */
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.time.LocalDate;

public class TCXREADER {

    public static ACTIVITY read(String filename) {

        ACTIVITY activity = null;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filename));
            doc.getDocumentElement().normalize();

            NodeList activityList = doc.getElementsByTagName("Activity");

            for (int i = 0; i < activityList.getLength(); i++) {

                Element activityElement = (Element) activityList.item(i);
                String sport = activityElement.getAttribute("Sport");

                if (sport.equalsIgnoreCase("Running")) {
                    activity = new RUNNING();
                } else if (sport.equalsIgnoreCase("Walking")) {
                     activity = new WALKING();
                } else if (sport.equalsIgnoreCase("Cycling") || sport.equalsIgnoreCase("Biking")) {
                      activity = new CYCLING();
                } else if (sport.equalsIgnoreCase("Swimming")) {
                      activity = new SWIMMING();
                } else if (sport.equalsIgnoreCase("Other")) {
                     activity = new OTHER();
                } else {
                     System.out.println("Άγνωστο Sport: " + sport + " – παραλείπεται.");
                 continue;
                }
                
                String id = getNodeValue(activityElement, "Id"); // π.χ. 2018-08-10T08:39:31.000Z
                LocalDate date = LocalDate.parse(id.substring(0, 10));
                activity.setDate(date);

                
                NodeList lapList = activityElement.getElementsByTagName("Lap");

                for (int j = 0; j < lapList.getLength(); j++) {
                    Element lapElement = (Element) lapList.item(j);

                    double timeSeconds = Double.parseDouble(
                            getNodeValue(lapElement, "TotalTimeSeconds")
                    );

                    double distanceMeters = Double.parseDouble(
                            getNodeValue(lapElement, "DistanceMeters")
                    );

                    int avgHeartRate = 0;
                    NodeList hrNodes = lapElement.getElementsByTagName("AverageHeartRateBpm");
                    if (hrNodes.getLength() > 0) {
                        Element hrElement = (Element) hrNodes.item(0);
                        avgHeartRate = Integer.parseInt(
                                getNodeValue(hrElement, "Value")
                        );
                    }

                    LAP lap = new LAP(timeSeconds, distanceMeters, avgHeartRate);
                    activity.addLap(lap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return activity;
    }

    private static String getNodeValue(Element parent, String tag) {
        NodeList n = parent.getElementsByTagName(tag);
        if (n == null || n.getLength() == 0) return "0";
        return n.item(0).getTextContent();
    }
}

