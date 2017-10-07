import java.util.ArrayList;

public class Deck
{
    private ArrayList<Card> deck; // data field ArrayList of Cards called deck
    
    public Deck(int size) // creates a new Deck with Size size
    {
        deck = new ArrayList<Card>(size);
    }

    public int getSize() // gets the size of the Deck
    {
        return deck.size();
    } 
    
    public Card get(int pos) // gets the Card at a specified index of the Deck
    {
        return deck.get(pos);
    }

    public void remove(int pos) // removes a Card at a specified index from the Deck
    {
        deck.remove(pos); 
    }

    public void add(Card card) // adds a Card card to the Deck
    {
        deck.add(card); 
    }
    
    public boolean contain(String cardName) // checks whter deck has this card
    {
        for (int i = 0; i < deck.size(); i++) // loops through entire deck
        {
            if (deck.get(i).getName().equals(cardName)) // if the current card's Name is cardName
                return true; // return true
        }
        return false; // return false if it was not found
    }
    
    public int search(String cardName) // searches the Deck for a Card with name cardName
    {
        for (int i = 0; i < deck.size(); i++) // loops though the entire deck
        {
            if (deck.get(i).getName().equals(cardName)) // if the current card's Name is cardName
                return i; // return the position of the card
        }
        return -1; // if the card wasn't found
    }  
}
