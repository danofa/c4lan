
package org.damcode.game.c4;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dm
 */
public class CyclicIntList {

    int pointer = 0;
    List elements;
    
    public CyclicIntList() {
        elements = new ArrayList();
    }
    
    public void add(int i){
        elements.add(i);
    }
    
    public Integer get(){
        if(elements.isEmpty()){
            return -1;
        }
        if(pointer > elements.size()-1){
            pointer = 0;
        }
        Integer i = (Integer) elements.get(pointer);
        return i;
    }
    
    public void next(){
        pointer++;
    }
    
}
