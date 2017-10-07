import java.util.ArrayList;

public class Exodia
{
    private Deck Deck;

    public Exodia() // constructs the Deck for Exodia
    {
        Deck = new Deck(0);
        Deck.add(new Card(1));
        Deck.add(new Card(2));
        Deck.add(new Card(3));
        Deck.add(new Card(4));
        Deck.add(new Card(5));
        Deck.add(new Card(6));
        for (int i = 0; i < 2; i++)
        {
            Deck.add(new Card(7));
            Deck.add(new Card(8));
        }
        for (int i = 0; i < 3; i++)
        {
            Deck.add(new Card(9));
            Deck.add(new Card(10));
            Deck.add(new Card(11));
            Deck.add(new Card(12));
            Deck.add(new Card(13));
            Deck.add(new Card(14));
            Deck.add(new Card(15));
            Deck.add(new Card(16));
            Deck.add(new Card(17));
            Deck.add(new Card(18));
        }
    }
    
    public Deck getDeck() // returns the Deck
    {
        return Deck;
    }
}
