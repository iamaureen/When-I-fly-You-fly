/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse.server.fooditemsclient;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ishrat Ahmed
 */
@XmlRootElement(name = "SelectedFoodItems")
public class SelectedFoodItems {
    List<Integer> foodItem;
    
    @XmlElement(name = "FoodItemId")
    public void setFoodItem(List<Integer> foodItem) {
        this.foodItem = foodItem;
    }

    public List<Integer> getFoodItem() {
        return foodItem;
    }

    
    
    
}
