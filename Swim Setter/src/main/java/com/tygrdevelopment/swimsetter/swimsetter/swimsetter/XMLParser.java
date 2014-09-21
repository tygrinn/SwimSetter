package com.tygrdevelopment.swimsetter.swimsetter.swimsetter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class XMLParser{
    public static Set parseSet(String filename) throws Exception{
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        XmlPullParser xpp = factory.newPullParser();

        InputStream is = new FileInputStream(filename);
        xpp.setInput(is, null);

        int eventType = xpp.getEventType();

        boolean isLoop = false;
        Set set = new Set();
        Section section = new Section();
        Slice slice = new Slice();
        Loop loop = new Loop();

        do {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(xpp.getName().equalsIgnoreCase("SET")){
                        set = new Set(xpp.getAttributeValue(0));
                    }
                    if(xpp.getName().equalsIgnoreCase("SECTION")){
                        section = new Section(xpp.getAttributeValue(0));
                    }
                    if(xpp.getName().equalsIgnoreCase("LOOP")){
                        loop = new Loop(Integer.parseInt(xpp.getAttributeValue(0)));
                        isLoop = true;
                    }
                    if(xpp.getName().equalsIgnoreCase("SLICE")){
                        int reps = 0;
                        int seconds = 0;
                        int distance = 0;
                        int type = 0;
                        String details = "";

                        for(int i = 0; i < xpp.getAttributeCount(); i++){
                            if(xpp.getAttributeName(i).equalsIgnoreCase("REPS"))
                                reps = Integer.parseInt(xpp.getAttributeValue(i));
                            if(xpp.getAttributeName(i).equalsIgnoreCase("DISTANCE"))
                                distance = Integer.parseInt(xpp.getAttributeValue(i));
                            if(xpp.getAttributeName(i).equalsIgnoreCase("SECONDS"))
                                seconds = Integer.parseInt(xpp.getAttributeValue(i));
                            if(xpp.getAttributeName(i).equalsIgnoreCase("TYPE"))
                                type = Integer.parseInt(xpp.getAttributeValue(i));
                            if(xpp.getAttributeName(i).equalsIgnoreCase("DETAILS"))
                                details = xpp.getAttributeValue(i);
                        }

                        slice = new Slice(reps, distance, seconds, type, details);
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if(xpp.getName().equalsIgnoreCase("SET")){
                        return set;
                    }
                    if(xpp.getName().equalsIgnoreCase("SECTION")){
                        set.addSection(section);
                    }
                    if(xpp.getName().equalsIgnoreCase("LOOP")){
                        section.addLoop(loop);
                        isLoop = false;
                    }
                    if(xpp.getName().equalsIgnoreCase("SLICE")){
                        if(isLoop)
                            loop.addSlice(slice);
                        else
                            section.addSlice(slice);
                    }
                    break;
                case XmlPullParser.TEXT:
                    break;
                default: break;
            }

            eventType = xpp.next();

        }while(eventType != XmlPullParser.END_DOCUMENT);

        return set;
    }

    public static void writeSet(String filename, Set set){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element rootElement = document.createElement("set");
            rootElement.setAttribute("name", set.getName());


            Integer[] loopIndexes;
            Object[] slices;
            Slice[] loopSlices;
            Section[] sections = set.getSections();
            Element sectionElement;
            Element loopElement;
            Element sliceElement;

            for(Section section : sections){

                loopIndexes = section.getLoopIndexes();
                slices = section.getSlices();

                sectionElement = document.createElement("section");
                sectionElement.setAttribute("name", section.getName());

                int hasLoop = 0;

                for(int i = 0; i < slices.length; i++){
                    if(hasLoop < loopIndexes.length && i == loopIndexes[hasLoop]){
                        hasLoop++;

                        loopSlices = ((Loop)slices[i]).getSlices();

                        loopElement = document.createElement("loop");
                        loopElement.setAttribute("reps", Integer.toString(((Loop)slices[i]).getReps()));

                        for(Slice s : loopSlices) {
                            sliceElement = document.createElement("slice");
                            sliceElement.setAttribute("reps", Integer.toString(s.getReps()));
                            sliceElement.setAttribute("distance", Integer.toString(s.getDistance()));
                            sliceElement.setAttribute("seconds", Integer.toString(s.getSeconds()));
                            sliceElement.setAttribute("type", Integer.toString(s.getType()));
                            sliceElement.setAttribute("details", s.getDetails());
                            loopElement.appendChild(sliceElement);
                        }
                        sectionElement.appendChild(loopElement);
                    }
                    else{
                        sliceElement = document.createElement("slice");
                        sliceElement.setAttribute("reps", Integer.toString(((Slice)slices[i]).getReps()));
                        sliceElement.setAttribute("distance", Integer.toString(((Slice)slices[i]).getDistance()));
                        sliceElement.setAttribute("seconds", Integer.toString(((Slice)slices[i]).getSeconds()));
                        sliceElement.setAttribute("type", Integer.toString(((Slice)slices[i]).getType()));
                        sliceElement.setAttribute("details", ((Slice)slices[i]).getDetails());
                        sectionElement.appendChild(sliceElement);
                    }
                }
                rootElement.appendChild(sectionElement);
            }

            document.appendChild(rootElement);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Properties properties = new Properties();
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty(OutputKeys.METHOD, "xml");
            properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            properties.setProperty(OutputKeys.VERSION, "1.0");
            properties.setProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperties(properties);

            Source input = new DOMSource(document);
            Result output = new StreamResult(new File(filename));
            transformer.transform(input, output);


        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
