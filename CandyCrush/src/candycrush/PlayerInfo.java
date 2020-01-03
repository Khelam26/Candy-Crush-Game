/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package candycrush;

/**
 * I dont want anyone to create a playerInfo object, that is why i made it abstract. 
 * The purpose of the class to hold data. 
 * Date modified: 12/17/2019
 * @author khelan
 */
public abstract class PlayerInfo {

    public static String name; // player's name
    int swap; // no of swaps he made during the game, maximum number is 5
    int score; // Total points they earned, maximum score is 500
    
    /**
     * Empty constructor
     */
    @SuppressWarnings("empty-statement")
    PlayerInfo(){
        ;
    }
    /**
     * Constructor with multiple arguements
     * @param name, player name (Data type- string)
     * @param swap, no of swaps he made (data type - int)
     * @param score, player's final score (data type - int)
     * @throws IllegalArguementException 
     */
    PlayerInfo(String name, int swap, int score) throws IllegalArguementException{
        setName(name);
        this.swap=swap;
        this.score=score;
    }
    
    /**
     * Sets the player name, if the name is empty then it throws an exception
     * @param name
     * @throws IllegalArguementException
     */
    public static void setName(String name) throws IllegalArguementException{
        if(name.isEmpty()){
            System.out.println("The Name Text Field Cannot be left BLANK. \n\tThe Game wont start unless you enter your NAME");
            throw new IllegalArguementException ("Name cannot be left empty");
        }
            else 
        PlayerInfo.name=name;
    } 

    /**
     * Returns the name of the player, playing the game
     * @return
     */
    public static String getName(){
        return name;
    }

}
