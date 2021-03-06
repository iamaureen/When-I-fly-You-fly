/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse.server.fooditems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Ishrat Ahmed
 */
public class ItemXMLHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(ItemXMLHandler.class);

    public String addFood(FoodItem foodItem) throws FileNotFoundException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerException {


        String rs = null;
        String itemID = null;
        boolean itemFound = false;
        int minRandom;
        //System.properties("user.dir") gave the absolute path address for glassfish server instead of the current project location. so used this 
        //function to get the path to the project location and then the xml file
        String dir =  this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        String path = getFolderPath(dir);
        //String tempPath = "C:\\Users\\Ishrat Ahmed\\Documents\\NetBeansProjects\\FoodItems";
        String fullPath =  path;
        File file = new File(fullPath);
        if (file.exists()) {
            rs = "file found";
            InputStream inputStream= new FileInputStream(file);
	    Reader reader = new InputStreamReader(inputStream,"UTF-8");
	    InputSource src = new InputSource(reader);
            
            DocumentBuilderFactory builderFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFac.newDocumentBuilder();
            Document d = builder.parse(src);
            
            NodeList allNodes = d.getElementsByTagName("FoodItem");
            //get all the ids in a list
            List<Integer> allID = new ArrayList<>();
            for (int i = 0; i < allNodes.getLength(); i++) {
                
                Element eachElement = (Element) allNodes.item(i);                
                String id = eachElement.getElementsByTagName("id").item(0).getTextContent();
                allID.add(Integer.parseInt(id));		
            }
            
            //search xml to see if the item exist or not
            for (int i = 0; i < allNodes.getLength(); i++) {
                
                Element eachElement = (Element) allNodes.item(i);
                
                String category = eachElement.getElementsByTagName("category").item(0).getTextContent();
		String name = eachElement.getElementsByTagName("name").item(0).getTextContent();
                if (foodItem.getCategory().equals(category) && foodItem.getName().equals(name)) {
                    itemID = eachElement.getElementsByTagName("id").item(0).getTextContent();
                    itemFound = true;
                    break;
                }
            }
            
            if(itemFound){
                return itemID;
            }else{
                //add the new item
                Element eachElement = d.createElement("FoodItem");
                eachElement.setAttribute("country", foodItem.getCountry());

                //setting the ID of new Element
                Element elementID = d.createElement("id");
                
                // set object ID; check if ID is in the list, generate new ID
               Random id = new Random();               
               int randID = ThreadLocalRandom.current().nextInt(0, 500);
               while(allID.contains(randID)){                   
                   randID = ThreadLocalRandom.current().nextInt(0, 500);
               }
               foodItem.setID(randID);
               elementID.setTextContent(String.valueOf(foodItem.getID()));
                
                //name
                Element elementName = d.createElement("name");
                elementName.setTextContent(foodItem.getName());
                
                //desecription
                Element elementDescription = d.createElement("description");
                elementDescription.setTextContent(foodItem.getDescription());

                //category
                Element elementCategory = d.createElement("category");
                elementCategory.setTextContent(foodItem.getCategory());

                //price
                Element elementPrice = d.createElement("price");
                elementPrice.setTextContent(foodItem.getPrice());
                
                //appending all the elements
		eachElement.appendChild(elementID);
		eachElement.appendChild(elementName);
		eachElement.appendChild(elementDescription);
		eachElement.appendChild(elementCategory);
		eachElement.appendChild(elementPrice);
                
                Element rootTag = (Element) d.getElementsByTagName("FoodItemData").item(0);
                rootTag.appendChild(eachElement);
                //saving the data to file
                Transformer xform = TransformerFactory.newInstance().newTransformer();
                Result output = new StreamResult(fullPath);
                Source input = new DOMSource(d);
                xform.transform(input, output);
                return "true";
            }     
            	    

                        
        } else {
            rs = fullPath+ " ::: File not found";
            return rs;
        }
        
        
    }
    
    public RetrievedFoodItems getFoodItem(List<Integer> foodItemId) throws UnsupportedEncodingException, FileNotFoundException, ParserConfigurationException, SAXException, IOException{
        
        String rs = null;
        List<FoodItem> foodItemsListFromXml = new ArrayList<FoodItem>();  ;
        //get the path for xml file
        String dir =  this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        String path = getFolderPath(dir);
        String fullPath =  path;
        File file = new File(fullPath);
        
        if(file.exists()){
            
            rs = "file found";
            
            InputStream inputStream= new FileInputStream(file);
	    Reader reader = new InputStreamReader(inputStream,"UTF-8");
	    InputSource src = new InputSource(reader);
            
            DocumentBuilderFactory builderFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFac.newDocumentBuilder();
            Document d = builder.parse(src);
            
            NodeList allNodes = d.getElementsByTagName("FoodItem");
            
            //get all the food objects from the xml file and put them into a list
            for (int i = 0; i < allNodes.getLength(); i++) {
                
                Element eElement = (Element) allNodes.item(i);
                FoodItem foodObj = new FoodItem();
                
                foodObj.setID(Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent()));
                foodObj.setCategory(eElement.getElementsByTagName("category").item(0).getTextContent());
                foodObj.setCountry(eElement.getAttribute("country"));
                foodObj.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
                foodObj.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
                foodObj.setPrice(eElement.getElementsByTagName("price").item(0).getTextContent());
                
                foodItemsListFromXml.add(foodObj);
            }
            
            List<FoodItem> validFoodItems = new ArrayList<FoodItem>();
            List<Integer> invalidFoodItems = new ArrayList<Integer>();
            FoodItem food;
            
            //foodItemId-->list that contains id of food object from client
            
            for (Integer foodId : foodItemId) {
                food = searchItem(foodId, foodItemsListFromXml);
                if (food != null) {
                    validFoodItems.add(food);
                } else {
                    invalidFoodItems.add(foodId);
                }
            }
            
            RetrievedFoodItems retrievedFoodItems = new RetrievedFoodItems();
            retrievedFoodItems.setFoodItem(validFoodItems);
            retrievedFoodItems.setInvalidFoodItem(invalidFoodItems);

            return retrievedFoodItems;
            //return rs;
            
         }else{
            System.out.println("File not found");
            rs = "File not found";
           // return rs;
        }
        
        return null;
    }
    
      private FoodItem searchItem(Integer foodId, List<FoodItem> foodItemsList ) {
        for (FoodItem food : foodItemsList) {
            if (food.getID() == foodId) {
                return food;
            }
        }
        return null;
    }

    private String getFolderPath(String dir) throws UnsupportedEncodingException {
        
            //dir = file:/C:/Users/Ishrat Ahmed/Documents/NetBeansProjects/FoodItems/target/FoodItems/WEB-INF/classes/edu/asu/cse/server/fooditems/ItemXMLHandler.class
            //first split against file:/, then /FoodItems/
            //we get C:\Users\Ishrat Ahmed\Documents\NetBeansProjects\
            String fullPath = URLDecoder.decode(dir, "UTF-8");
            String pathArr[] = fullPath.split("file:/");
            fullPath = pathArr[1];
            String pathArr1[] = fullPath.split("/FoodItems/");
            fullPath = pathArr1[0];           
            String path = "";
            // to read a file from webcontent
            path = new File(fullPath).getPath() + File.separatorChar + "FoodItems\\FoodItemData.xml";
            return path;
            
    }

  
    
}
