import java.awt.*;
import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;

public class Card
{
    private Image image,cardback;
    private String name, effect, form; // form is whether it is Monster or Spell
    private int attack, defense;
    private boolean faceUp = true; // whether card is face up or not

    public Card(int num) // Constructor has int Parameter to make File Reading easier
    {
        image = null; // set Default to Null
        try // for the File Readings
        {
            // read the data containing each Card's name, effect and form
            BufferedReader br = new BufferedReader(new FileReader("Resources\\Card Data.txt"));
            String line = "";
            for (int i = 0; i < num; i++)
                line = br.readLine(); // read up to line # num of the Card
            int ind = line.indexOf("/");
            // Get the Name, Effect of Form for the Card
            name = line.substring(0,ind);
            line = line.substring(ind+1);
            ind = line.indexOf("/");
            effect = line.substring(0,ind);
            form = line.substring(ind+1);
            // read the data containing each Card's attack and defense
            br = new BufferedReader(new FileReader("Resources\\Card Data 2.txt"));
            for (int i = 0; i < num; i++)
                line = br.readLine(); // read up to line # num of the Card
            ind = line.indexOf("/");
            // Get the Attack and Defense for the Card
            attack = Integer.parseInt(line.substring(0,ind));
            defense = Integer.parseInt(line.substring(ind+1));
        }
        catch (Exception e)
        {
        }
    }

    public Image getImage()
    {
        if (faceUp) // if faceUp, return Proper Image
            return image;
        else
            return cardback; // otherwise, return it's Back
    }

    public Image getCardImage()
    {
        return image; // returns Card Image
    }

    public void setFace(boolean face)
    {
        faceUp = face; // sets Face 
    }

    public void setImage(Image img)
    {
        image = img;  // sets Image
    }

    public void setBack(Image img)
    {
        cardback = img; // sets CardBack Image
    }

    public String getEffect()
    {
        return effect; // returns Card Effect
    }

    public String getForm()
    {
        return form; // returns Card Form
    }

    public String getName()
    {
        return name; // returns Card Name
    }

    public int getAttack()
    {
        return attack; // used to get attack
    }

    public int getDefense()
    {
        return defense; // used to get defense
    }
}
