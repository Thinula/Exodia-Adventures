import java.io.*;
import javax.imageio.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Image;
import java.util.ArrayList;
// for Background Music (Hope you enjoy!)
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class YuGuiOh extends JFrame
{
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // sets Dimensions according to personal Screen Size
    
    // gets all dimensions needed for this program
    static int width = (int) screenSize.getWidth();
    static int height = (int) screenSize.getHeight()-40;
    static int cWidth = width/25;
    static int cHeight = width*5/90;
    
    // cardLabels is for holding the Player's Hand
    static ArrayList<JLabel> cardLabels = new ArrayList<JLabel>(0);
    // cardAILabels is for holding the AI's Hand
    static ArrayList<JLabel> cardAILabels = new ArrayList<JLabel>(0); 
    // mouse adapters used to detect selected card, among other things
    static ArrayList<AdaptMe> adapters = new ArrayList<AdaptMe>(0);
    // used for holding all card info
    static ArrayList<JLabel> cardInfo;
    
    static JFrame frame; // declared to show JOptionPane Messages
    
    // bunch of JPanels used throughout the program to show images or gameplay
    static JPanel game;
    static JPanel win = new JPanel ();
    static JPanel con;  // Used for the actual gameplay
    static JPanel menu;
    
    // several JLabels that are flexible and used throughout to show things such as board pieces
    static JLabel winner;
    static JLabel loser;
    static JLabel playMenu;
    static JLabel bg; 
    static JLabel tutbg;
    static JLabel opSel, plSel;
    static JLabel side1, side2;
    static JLabel bgimg;
    
    // images used for the game
    static Image [] cardImages;// card image data base to save heap space
    static Image menuPic, title, playBtn1, playBtn2, tutBtn1, tutBtn2; // different options in our main menu
    static Image winPic; // winner Picture
    static Image losePic; // loser Picture
    static Image selectPic; // selection Picture (used for Battle Phase)
    static Image label1; // different images used repeatedly in game
    static Image label2; 
    static Image label3; 
    
    // call two Exodia Decks (One for Human, One for AI)
    static Exodia HPlay = new Exodia();
    static Exodia APlay = new Exodia();
    static Deck Deck = HPlay.getDeck();
    static Deck AIDeck = APlay.getDeck();
    
    // the Hands can also be treated like a Deck
    static Deck Hand;
    static Deck AIHand;
    static boolean [][] yourMons = new boolean[4][5]; // to show the status of the board panels
    static BattleMe [][] battle = new BattleMe [2][5]; // to facilitate the battle mechanism
    static int LPH = 8000, LPC = 8000; // set the starting Life Points of both players to 8000 each
    static JLabel phaseName, health, aiHealth; // to show the current Phase, and the Life Points of the Human and AI
    static AttackFace opSmack; // to show the Attack Opponent button
    
    static JButton draw; // JButton to change phases 

    static Tutorial tut; // to show the Tutorial
    
    // different "slot" arrays to hold cards on the field (not made 2D because each has a different function that was easier to do separately)
    static Board [] bbBoard ; // for the AI's Spells
    static Board [] btBoard ; // for the AI's Monsters
    static Board [] tbBoard ; // for the Human's Monsters
    static Board [] ttBoard ; // for the Human's Spells

    public YuGuiOh ()
    {         
        // Declare all needed images with their desired dimensions at the start to save heap space
        menuPic = pic (width, height, "Resources\\Main.jpg"); // all title screen images
        title = pic (cHeight*6, cWidth*4, "Resources\\Title Thing.png");
        playBtn1 = pic (cHeight*5/2,2*cWidth-20, "Resources\\Play1.png");
        playBtn2 = pic (cHeight*5/2,2*cWidth-20, "Resources\\Play2.png");
        tutBtn1 = pic (cHeight*5/2,2*cWidth-20, "Resources\\Tut1.png");
        tutBtn2 = pic (cHeight*5/2,2*cWidth-20, "Resources\\Tut2.png");

        winPic = pic (width, height, "Resources\\Win Screen.png"); // miscellaneous utility images
        losePic = pic (width, height, "Resources\\Lose Screen.png");
        selectPic = pic (width/4, height/4, "Resources\\Selected.png");
        label1 = pic (cHeight + 10, cHeight + 10, "Resources\\BoardPanel.png");
        label2 = pic (width*3/16, height*3/4, "Resources\\BoardPanel.png");
        label3 = pic (cWidth + 10,cHeight + 10, "Resources\\BoardPanel.png");

        opSmack = new AttackFace(); // used for attacking AI directly (if he has no monsters)

        Font fonte = new Font ("Times New Roman", Font.BOLD, 12);

        // opSel is to show the AI's Monster being selected during Battle Phase
        opSel = imageMe (reSize(cHeight*3/2, 40,label1));
        opSel.setText ("Selected");
        opSel.setHorizontalTextPosition(JLabel.CENTER);
        opSel.setVerticalTextPosition(JLabel.CENTER);
        opSel.setFont (fonte);
        opSel.setForeground (Color.WHITE);    

        // plSel is to show the Human's Monster being selected during Battle Phase
        plSel = imageMe (reSize(cHeight*3/2, 40,label1));
        plSel.setText ("Selected");
        plSel.setHorizontalTextPosition(JLabel.CENTER);
        plSel.setVerticalTextPosition(JLabel.CENTER);
        plSel.setFont (fonte);
        plSel.setForeground (Color.WHITE);   

        // initialize Hands as Decks with size 0
        Hand = new Deck(0);
        AIHand = new Deck(0);

        // cardBack image (used for Decks and the AI's Hand)
        Image cardBack = pic (width*3/16-50,(width*3/16-50)*cHeight/cWidth, "Resources\\Card Back.jpg");   

        cardImages = new Image [18] ; // array of cardImages (there are only 18 DIFFERENT cards in the deck, no need to load any more than that)

        for (int i = 0; i < 18; i ++) // get all the cardImages
            cardImages[i] =  pic (width*3/16-50,(width*3/16-50)*cHeight/cWidth,"Resources\\Card" + i + ".png");

        // set the Images (Normal and Back) for all cards in the Human's Deck
        for (int i = 0; i < Deck.getSize(); i ++)
        {
            Deck.get(i).setImage(cardAdd(Deck.get(i).getName()));
            Deck.get(i).setBack(cardBack);    
        }   

        // set the Images (Normal and Back) for all cards in the AI's Deck
        for (int i = 0; i < AIDeck.getSize(); i ++)
        {
            AIDeck.get(i).setImage(cardAdd(Deck.get(i).getName()));
            AIDeck.get(i).setBack(cardBack);    
        }

        int pos;
        for (int j = 0; j < 5; j++) // randomly add 5 cards from the Human's Deck to his Hand
        {
            pos = (int)(Math.random()*Deck.getSize());
            Hand.add(Deck.get(pos));
            Deck.remove(pos);
        }

        int level = (int)(Math.random()*6); // randomly assign a difficulty level (determines the number of Exodia's in the AI's Hand)

        // Add Number of Exodia Cards to AI's Hand based on the level
        if (level > 0)
        {
            pos = AIDeck.search("Forbidden One");
            AIHand.add(AIDeck.get(pos));
            AIDeck.remove(pos);
        }
        if (level > 1)
        {
            pos = AIDeck.search("Right Arm");
            AIHand.add(AIDeck.get(pos));
            AIDeck.remove(pos);
        }
        if (level > 2)
        {
            pos = AIDeck.search("Left Arm");
            AIHand.add(AIDeck.get(pos));
            AIDeck.remove(pos);
        }
        if (level > 3)
        {
            pos = AIDeck.search("Right Leg");
            AIHand.add(AIDeck.get(pos));
            AIDeck.remove(pos);
        }
        if (level > 4) // yes, there is a 1/6 chance of him winning on turn 1 - have fun
        {
            pos = AIDeck.search("Left Leg");
            AIHand.add(AIDeck.get(pos));
            AIDeck.remove(pos);
        }

        Card want = null;
        String name;
        for (int i = 0; i < 5-level; i++) // for the rest of the cards, add non-Exodia cards from the AI's Deck to his Hand
        {
            pos = (int)(Math.random()*AIDeck.getSize());
            want = AIDeck.get(pos);
            name = want.getName();
            if (!(name.equals("Forbidden One") && name.equals("Right Leg") && name.equals("Left Leg") && name.equals("Right Arm") && name.equals("Left Arm")))
            {
                AIHand.add(want);
                AIDeck.remove(pos);
            }
        }

        win.setLayout(null); // sets layout to null to avoid any gui complications

        bg = imageMe (menuPic); // sets background image for menu
        con = new Con(); // creates playing field and everything for playing
        menu = new Menu (); // creates the title screen
        tut = new Tutorial(); // creates the tutorial

        winner = imageMe (winPic); // sets the win screen
        winner.setSize (width, height);

        loser = imageMe (losePic); // sets the lose screen
        loser.setSize (width, height);

        bgimg = imageMe (pic (width, height, "Resources\\Background.png")); // sets the background image of the playing field
        bgimg.setSize (width, height);
        bgimg.setLayout (null);

        bgimg.add (menu);
        win.add (bgimg);

        try // to play Background Music (Original Yugioh Theme Song!)
        {
            AudioInputStream audioInputStream =
                AudioSystem.getAudioInputStream(
                    this.getClass().getResource("Resources\\YuGuiOh Theme.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(clip.LOOP_CONTINUOUSLY); // the music will loop continuously
        }
        catch(Exception ex)
        {
            System.out.println("Wow the audio file can't be found.");
        }

        // the usual stuff needed to set the frame
        setContentPane (win);
        pack ();
        setSize (width, height);
        setTitle ("Yu Gui Oh"); // creative name
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);
    }

    public Image cardAdd (String name) // used to add the Images of Cards in the Decks as shown above, only needed because of heap space
    {
        if (name.equals ("Accumulated Fortune"))
            return cardImages [0];
        else if (name.equals ("Battle Fader"))
            return cardImages [1];
        else if (name.equals ("Forbidden One"))
            return cardImages [2];
        else if (name.equals ("Gift Card"))
            return cardImages [3];
        else if (name.equals ("Hope for Escape"))
            return cardImages [4];
        else if (name.equals ("Jar of Greed"))
            return cardImages [5];
        else if (name.equals ("Left Arm"))
            return cardImages [6];
        else if (name.equals ("Left Leg"))
            return cardImages [7];
        else if (name.equals ("Legacy of Yata-Garasu"))
            return cardImages [8];
        else if (name.equals ("One Day of Peace"))
            return cardImages [9];
        else if (name.equals ("Pot of Greed"))
            return cardImages [10];
        else if (name.equals ("Right Arm"))
            return cardImages [11];
        else if (name.equals ("Right Leg"))
            return cardImages [12];
        else if (name.equals ("Sangan"))
            return cardImages [13];
        else if (name.equals ("Swift Scarecrow"))
            return cardImages [14];
        else if (name.equals ("Threatening Roar"))
            return cardImages [15];
        else if (name.equals ("Upstart Goblin"))
            return cardImages [16];
        else
            return cardImages [17];
    }

    class Menu extends JPanel // main menu panel
    {   
        JLabel play, tutorial, titleThing;
        
        public Menu()
        {
            bg.setLayout (null);
            bg.setSize (width, height);

            // to show title (separate so title doesn't get too warped on different screen sizes)
            titleThing = imageMe (title);
            titleThing.setSize (cHeight*6,cWidth*4);
            titleThing.setLocation (cWidth, height - 5*cWidth);
            titleThing.setOpaque (false);

            // to show the Play button
            play = imageMe (playBtn1);
            play.setSize (cHeight*5/2,2*cWidth-20);
            play.setLocation (cHeight*6 + cWidth + 40, height - 5*cWidth);
            play.setOpaque (false);
            play.addMouseListener (new PlayMenuListen());

            // to show the Tutorial button
            tutorial = imageMe (tutBtn1);  
            tutorial.setSize (cHeight*5/2,2*cWidth-20);
            tutorial.setLocation (cHeight*6 + cWidth + 40, height - 3*cWidth + 20); 
            tutorial.setOpaque (false);        
            tutorial.addMouseListener (new TutMenuListen());

            // add all of them to bg then add bg to the Menu
            bg.add (titleThing);
            bg.add (play);
            bg.add (tutorial);
            this.add (bg);
            this.setSize (width, height);
            this.setVisible (true);
        }

        class PlayMenuListen extends MouseAdapter // for the Play Option in the Main Menu
        {   
            public void mouseClicked (MouseEvent e) // if they click it, remove the Main Menu, add the playing field, and repaint
            {
                bgimg.remove (menu);
                bgimg.add (con);
                bgimg.repaint();
            }

            public void mouseEntered (MouseEvent e) // once they entered, show PlayBtn2 (highlighted button)
            {
                play.setIcon (new ImageIcon (playBtn2));
                play.repaint();
            }

            public void mouseExited (MouseEvent e) // once they exited, show PlayBtn1 (normal button)
            {
                play.setIcon (new ImageIcon (playBtn1));
                play.repaint();
            }
        }

        class TutMenuListen extends MouseAdapter // for the Tutorial Option in the Main Menu
        {   
            public void mouseClicked (MouseEvent e) // if they click it, remove the Main Menu, add the Tutorial, and repaint
            {        
                bgimg.remove (menu);
                tut.reset(); // ensures everytime the tutorial is pressed, it will start on slide one
                bgimg.add (tut);
                bgimg.repaint();
            }

            public void mouseEntered (MouseEvent e) // once they entered, show TutBtn2 (highlighted button)
            {
                tutorial.setIcon (new ImageIcon (tutBtn2));
                tutorial.repaint();
            }

            public void mouseExited (MouseEvent e) // once they exited, show TutBtn1 (normal button)
            {
                tutorial.setIcon (new ImageIcon (tutBtn1));
                tutorial.repaint();
            }
        }
    }
    class Tutorial extends JPanel // to show the Tutorial
    {
        private ImageIcon [] slides; // to show the differenting slides in the Tutorial
        private JButton back, next, main;
        private JLabel slideShow;
        private int counter; // used to determine user's current slide number

        public Tutorial ()
        {
            this.setLayout (null);
            tutbg = new JLabel();
            tutbg.setLayout (null);
            tutbg.setSize (width, height);

            TutEar tutear = new TutEar();

            counter = 0; // start's at the first slide

            slides = new ImageIcon [18];
            for (int i = 0; i < 18; i ++) // add all the Images for the slideshow
                slides[i] = new ImageIcon (pic (960, 720, "Resources\\Slide" + (i + 1) + ".png"));

            slideShow = new JLabel (slides[0]);
            slideShow.setSize (960, 720);
            slideShow.setLocation ((width - 960)/2, (height - 720)/2 - cHeight);

            Font font = new Font ("Times New Roman", Font.BOLD, 14);

            // Back option so user can see previous slide
            back = new JButton ("Back", new ImageIcon (reSize(2*cHeight + 20,cWidth + 20,label1)));
            back.setHorizontalTextPosition(JButton.CENTER);
            back.setVerticalTextPosition(JButton.CENTER);
            back.setFont (font);
            back.setForeground (Color.WHITE);
            back.addActionListener (tutear);
            back.setSize (2*cHeight, cWidth);
            back.setLocation (cWidth, height - 2*cWidth);

            // Next option so user can see next slide
            next = new JButton ("Next", new ImageIcon (reSize(2*cHeight + 20,cWidth + 20,label1)));
            next.setHorizontalTextPosition(JButton.CENTER);
            next.setVerticalTextPosition(JButton.CENTER);
            next.setFont (font);
            next.setForeground (Color.WHITE);
            next.addActionListener (tutear);
            next.setSize (2*cHeight, cWidth);
            next.setLocation (width - 2*cHeight - cWidth, height - 2*cWidth);

            // Back to Main Menu option so user can return to the Main Menu after learning about the game
            main = new JButton ("Back to Main Menu", new ImageIcon (reSize(4*cHeight + 20,cWidth + 20,label1)));
            main.setHorizontalTextPosition(JButton.CENTER);
            main.setVerticalTextPosition(JButton.CENTER);
            main.setFont (font);
            main.setForeground (Color.WHITE);
            main.addActionListener (tutear);
            main.setSize (4*cHeight, cWidth);
            main.setLocation (width/2 - 2*cHeight, height - 2*cWidth);

            // add all the above to the Tutorial
            tutbg.add (slideShow);
            tutbg.add (next);
            tutbg.add (main);
            this.add (tutbg);

            this.setSize (width, height);
            this.setOpaque (false);
            this.setVisible (true);
        }

        public void reset () // Everytime the user presses "Tutorial" in the Main Menu, it will reset to start at the first slide
        {
            counter = 0; // resets to first slide
            slideShow.setIcon (slides[counter]);
            tutbg.remove (back);
            tutbg.add (next);
            tutbg.add (main);
            tut.repaint();
        }

        class TutEar implements ActionListener // listens for the 3 buttons in the Tutorial
        {
            public void actionPerformed (ActionEvent e)
            {
                if (e.getActionCommand().equals ("Next")) // if user wants to go to the next slide
                {
                    counter ++; // move to next slide
                    if (counter == 17) // when user reaches the last slide, remove the Next Button
                        tutbg.remove (next);
                    tutbg.add (back); // anytime next is pressed, the back button should become valid again
                    slideShow.setIcon (slides[counter]); // shows the next slide then repaint
                    tut.repaint();
                }
                if (e.getActionCommand().equals ("Back")) // if user wants to go to the previous slide
                {
                    counter --; // move to the previous slide
                    if (counter == 0) // when user is at beginning slide, remove the Back Button
                        tutbg.remove (back);
                    tutbg.add (next); // anytime back is pressed, the next button should become valid again
                    slideShow.setIcon (slides[counter]); // shows the previous slide then repaints
                    tut.repaint();
                }
                if (e.getActionCommand().equals ("Back to Main Menu")) // if user wants to go to the Main Menu
                {
                    counter = 0; // set slide number to first
                    // remove the Tutorial, add Main Menu and then repaint
                    bgimg.remove(tut); 
                    bgimg.add (menu);
                    bgimg.repaint();
                }
            }
        }
    }

    class Con extends JPanel 
    {
        private ButtonMe ear; // to deal with all the JButtons involved in our program
        private int turn, phase; // to determine current turn and phase
        private Timer timer; // to control timing of AI's Plays

        private SelectMe [][] attack = new SelectMe [4][5]; // creates pop-up Menus for each Board Panel
        public Con() 
        {
            this.setSize (width, height);
            this.setLayout (null);
            this.setOpaque (false);

            // the 4 Board arrays 
            bbBoard = new Board [5];
            btBoard = new Board [5];
            tbBoard = new Board [5];
            ttBoard = new Board [5];
            
            // intialize turn to 0 and phase to 1
            turn = 0;
            phase = 1;

            for (int i = 0; i < bbBoard.length; i ++) // since all boards have the same length, we can put everything under one loop
            {
                // intialize each Board in the Board Arrays
                bbBoard[i] = new Board(i, 0);
                btBoard[i] = new Board(i, 1);
                tbBoard[i] = new Board(i, 3);
                ttBoard[i] = new Board(i, 4);

                // add pop-ups for each slot on the Field (to show the image of the card in the upper left corner)
                // add Mouse Listeners to each Board in the Board Arrays
                attack[0][i] = new SelectMe (0, i, false, 0);
                bbBoard[i].addMouseListener (attack[0][i]);

                attack[1][i] = new SelectMe (1, i, false, 1);
                btBoard[i].addMouseListener (attack[1][i]);

                attack[2][i] = new SelectMe (2, i, false, 2);
                tbBoard[i].addMouseListener (attack[2][i]);

                attack[3][i] = new SelectMe (3, i, false, 3);
                ttBoard[i].addMouseListener (attack[3][i]);

                // 2 middle boards have additional Mouse Listeners for battle phase
                battle [0][i] = new BattleMe (0, i); 
                battle [1][i] = new BattleMe (1, i);

                // add all the Board Arrays to Con
                this.add (bbBoard[i]);
                this.add (btBoard[i]);
                this.add (tbBoard[i]);
                this.add (ttBoard[i]);
            }

            // 2 fonts used to create buttons
            Font foney = new Font ("Times New Roman", Font.BOLD, 24);
            Font fonet = new Font ("Times New Roman", Font.BOLD, 48);

            // to show the current Phase
            phaseName = imageMe (reSize (5*(cHeight+20)-10, cHeight, label1 ));
            phaseName.setText ("Main Phase: Play Cards!");
            phaseName.setHorizontalTextPosition(JButton.CENTER);
            phaseName.setVerticalTextPosition(JButton.CENTER);
            phaseName.setFont (foney);
            phaseName.setForeground (Color.WHITE);
            this.add (phaseName);
            phaseName.setSize (5*(cHeight+20)-10, cHeight);
            phaseName.setLocation (width/2-5*(cHeight + 20)/2, (height/2 - 3*(cHeight + 20)) + 2*(cHeight + 20));

            // to show the Human's Life Points
            health = imageMe (reSize (cHeight * 2, cWidth, label1 ));
            health.setText (String.valueOf(LPH));
            health.setHorizontalTextPosition(JButton.CENTER);
            health.setVerticalTextPosition(JButton.CENTER);
            health.setFont (fonet);
            health.setForeground (Color.WHITE);
            this.add (health);
            health.setSize (cHeight * 2, cWidth);
            health.setLocation (width - cHeight*2 - cWidth, height - 3*cWidth);

            // to show the AI's Life Points
            aiHealth = imageMe (reSize (cHeight * 2, cWidth, label1 ));
            aiHealth.setText (String.valueOf(LPC));
            aiHealth.setHorizontalTextPosition(JButton.CENTER);
            aiHealth.setVerticalTextPosition(JButton.CENTER);
            aiHealth.setFont (fonet);
            aiHealth.setForeground (Color.WHITE);
            this.add (aiHealth);
            aiHealth.setSize (cHeight * 2, cWidth);
            aiHealth.setLocation (width - cHeight*2 - cWidth, cWidth);

            // pretty much our universal action listener, controls the game play
            ear  = new ButtonMe();

            // initialize each of the ArrayLists to size 0 (in case)
            cardLabels = new ArrayList<JLabel>(0);
            adapters = new ArrayList<AdaptMe>(0);
            cardInfo = new ArrayList<JLabel>(0);

            // add all the cards to the Hand and AIHand (this has more to do with showing them on the screen)
            for (int i = 0; i < Hand.getSize (); i++)
                addCard(i);

            for (int i = 0; i < AIHand.getSize(); i++)
                addAICard(i);
                
            // to show the Human's Deck on the Lower Right side of the Field
            side1 = imageMe (pic (cWidth + 10, cHeight + 10, "Resources\\Card Back.jpg"));
            this.add (side1);
            side1.setSize (cWidth + 10, cHeight + 10);
            side1.setLocation (width*3/4 - cWidth, height/2 +  3 * cHeight /2);

            // to show the AI's Deck on the Upper Left side of the Field
            side2 = imageMe (pic (cWidth + 10, cHeight + 10, "Resources\\Card Back.jpg"));
            this.add (side2);
            side2.setSize (cWidth + 10, cHeight + 10);
            side2.setLocation (width/4, height/2 - 4 * cHeight); 

            // the Next Phase button (to transition between Phases)
            draw = new JButton("Next Phase", new ImageIcon (reSize (3*cHeight/2+30,3*cWidth/2+30, label1)));
            draw.setHorizontalTextPosition(JButton.CENTER);
            draw.setVerticalTextPosition(JButton.CENTER);
            Font font = new Font ("Times New Roman", Font.BOLD, 18);
            draw.setFont (font);
            draw.setForeground (Color.WHITE);
            this.add(draw);
            draw.setSize(3*cHeight/2,3*cWidth/2);
            draw.setLocation(width*4/5, (height-3*cWidth/2)/2);
            draw.addActionListener(ear);
        }

        public void addCard (int i) // adds a card to index i in Hand
        {
            Image pic = Hand.get(i).getImage(); // get the Image

            // add the Image to cardLabels
            cardLabels.add (imageMe(reSize(cWidth, cHeight, pic)));
            cardLabels.get (i).setSize (cWidth, cHeight);
            cardLabels.get (i).setOpaque (false);
            this.add (cardLabels.get (i));          
            cardLabels.get (i).setLocation (width/2-5*cWidth + i*cWidth, height - 2*cHeight);
            
            // add adapters to get card selection
            adapters.add (new AdaptMe (width/2-5*cWidth + i*cWidth, height - 2*cHeight));
            cardLabels.get (i).addMouseListener (adapters.get (i));
            cardLabels.get (i).addMouseMotionListener (adapters.get (i));

            // creates information menu for cards
            cardInfo.add (imageMe(label2));

            JLabel cardPic = imageMe (pic);
            cardPic.setSize (width*3/16-50, (width*3/16-50)*cHeight/cWidth);
            cardPic.setLocation (25, 25);
            cardInfo.get(i).add(cardPic);

            Font font = new Font ("Times New Roman", Font.BOLD, 12);
            Font fonty = new Font ("Times New Roman", Font.BOLD, 14);

            JTextPane effect = new JTextPane();

            // show the Effect
            effect.setText (Hand.get(i).getEffect());
            effect.setFont (fonty);
            effect.setForeground (Color.WHITE);
            effect.setOpaque (false);
            effect.setSize(width*3/16-50, height/6);
            effect.setLocation (25, (width*3/16-50)*cHeight/cWidth + 50);
            cardInfo.get(i).add(effect);

            if (Hand.get(i).getForm().equals("Monster")) // if the Card is a Monster, show the Summon Option
            {   
                JButton summon = new JButton ("Summon", new ImageIcon (reSize(150,40,label1)));
                summon.setHorizontalTextPosition(JButton.CENTER);
                summon.setVerticalTextPosition(JButton.CENTER);
                summon.setFont (font);
                summon.setForeground (Color.WHITE);
                cardInfo.get(i).add(summon);
                summon.setSize (100, 40);
                summon.setLocation (width*3/32 - 50, (width*3/16-50)*cHeight/cWidth + 100 + height/10);
                summon.addActionListener (ear);
            }
            else // if the Card is a Spell, show the Play Option
            {
                JButton play = new JButton ("Play", new ImageIcon (reSize(150,40,label1)));
                play.setHorizontalTextPosition(JButton.CENTER);
                play.setVerticalTextPosition(JButton.CENTER);
                play.setFont (font);
                play.setForeground (Color.WHITE);
                cardInfo.get(i).add(play);
                play.setSize (100, 40);
                play.setLocation (width*3/32 - 50, (width*3/16-50)*cHeight/cWidth + 100 + height/10);
                play.addActionListener (ear);
            }

            cardInfo.get(i).add (Box.createRigidArea (new Dimension (0, 25)));

            this.add (cardInfo.get (i));
            cardInfo.get (i).setSize (width*3/16, height*3/4);
            cardInfo.get (i).setVisible (false);
            cardInfo.get (i).setLocation (0, height/8);
        }

        public void addAICard (int i) // add card to AI's Hand at position i
        {
            AIHand.get(i).setFace(false); // so they appear as the back of the card
            Image pic = AIHand.get(i).getImage(); // get the Image

            // add the Image to the cardAILabels
            cardAILabels.add (imageMe(reSize(cWidth, cHeight, pic)));
            cardAILabels.get (i).setSize (cWidth, cHeight);
            cardAILabels.get (i).setOpaque (false);

            this.add (cardAILabels.get (i));
            cardAILabels.get (i).setLocation (width/2-5*cWidth + i*cWidth, 0);
        }

        class SelectMe extends MouseAdapter
        {
            private int row, index, b;
            private Select hover;
            private JLabel info, bg;
            private JLabel atk, def, name;
            private Font font, fontt;

            public SelectMe (int r, int i, boolean check, int  board)
            {
                font = new Font ("Times New Roman", Font.BOLD, 14);
                fontt = new Font ("Times New Roman", Font.BOLD, 18);
                b = board;
                row = r;
                index = i;
                yourMons[r][i] = check;

                hover = new Select ();
            }

            class Select extends JPanel // shows card info when it in on the playing field
            {
                private Image card;
                private JLabel cardShow;

                public Select ()
                {
                    cardShow = new JLabel ();
                    card = null;

                    bg = imageMe (reSize (5*cWidth + 20, 2*cHeight + 20, label1));

                    // for the card name
                    name = new JLabel("", SwingConstants.CENTER);
                    name.setFont (fontt);
                    name.setForeground (Color.WHITE);

                    // for the card attack
                    atk = new JLabel("", SwingConstants.CENTER);
                    atk.setFont (font);
                    atk.setForeground (Color.WHITE);
                    
                    // for the card defense
                    def = new JLabel("", SwingConstants.CENTER);
                    def.setFont (font);
                    def.setForeground (Color.WHITE);

                    bg.add (name);
                    name.setSize (3*cWidth, cWidth/2);

                    // alignment for name
                    if (b == 1 || b == 2)
                        name.setLocation (2*cWidth + 10, cHeight + 10 - cWidth/2);
                    else
                        name.setLocation (2*cWidth + 10, cHeight + 10 - cWidth/4);

                    // monsters have attack and defense, while spells don't
                    if (b == 1 || b == 2)
                    {
                        bg.add (atk);
                        bg.add (def);
                        atk.setSize (cWidth, cWidth/2);
                        atk.setLocation (5*cWidth/2 + 10, cHeight + 10);
                        def.setSize (cWidth, cWidth/2);
                        def.setLocation (7*cWidth/2 + 10, cHeight + 10);
                    }

                    bg.add (cardShow);
                    cardShow.setSize (2*cWidth, 2*cHeight);
                    cardShow.setLocation (10, 10);

                    this.add (bg);
                    bg.setSize (5*cWidth + 20, 2*cHeight + 20);
                    bg.setLocation (0,0);

                    this.setSize(5*cWidth+20,2*cHeight+20);  
                    this.setOpaque(false);
                }

                public void setImge(Image i) // gets the image of the card
                {
                    card = i;
                    if (card != null)
                    {
                        cardShow.setIcon (new ImageIcon (card));
                    }
                    this.repaint();
                }
            }

            public void setMon (boolean c) // sets whether a card is there or not
            {
                yourMons[row][index] = c;
            }

            public boolean getMon () // gets whether a card is there or not
            {
                return yourMons[row][index];
            }

            public void show(Board[] board) // sets attack and defense to that of monster on field
            {
                hover.setImge(reSize(2*cWidth, 2*cHeight,board[index].getCard().getCardImage()));

                name.setText (board[index].getCard().getName());
                atk.setText("Atk: " + board[index].getCard().getAttack());
                def.setText("Def: " + board[index].getCard().getDefense());
            }

            public void mouseEntered (MouseEvent e) // shows menu when you hover over panel
            {                
                try // if there is no card, this catches it
                {
                    // gets the info of the card a puts it on the menu
                    if (b == 0)
                    {
                        hover.setImge(reSize(2*cWidth, 2*cHeight,bbBoard[index].getCard().getCardImage()));
                        name.setText (bbBoard[index].getCard().getName());
                    }
                    else if (b == 1)
                        show (btBoard);
                    else if (b == 2)
                        show (tbBoard);
                    else if (b == 3)
                    {
                        hover.setImge(reSize(2*cWidth, 2*cHeight,ttBoard[index].getCard().getCardImage()));
                        name.setText (ttBoard[index].getCard().getName());
                    }
                }
                catch (Exception error)
                {
                }

                hover.repaint();

                con.add(hover);

                con.repaint();
            }

            public void hoverUpdate() // repaints the menu
            {
                hover.repaint();
            }

            public void mouseExited (MouseEvent e) // removes menu when cursor exits
            {
                con.remove(hover);
                con.repaint();
            }

            public void mouseClicked (MouseEvent e) // allows you to select the panels (used later for battle phase)
            {
                Component comp = (Component) e.getSource();
                int check = -1;
                boolean temp = !yourMons[row][index];

                for (int i = 0; i < yourMons[0].length; i++)
                    yourMons[row][i] = false;

                yourMons[row][index] = temp;
            }
        }

        public boolean AIWinFast() // returns whether AI has all 5 Exodia pieces in his hand
        {
            return AIHand.contain("Forbidden One") && AIHand.contain("Left Arm") && AIHand.contain("Left Leg") && AIHand.contain("Right Leg") && AIHand.contain("Right Arm");
        }

        public boolean HWinFast() // returns whether Human has all 5 Exodia pieces in his hand
        {
            return Hand.contain("Forbidden One") && Hand.contain("Left Arm") && Hand.contain("Left Leg") && Hand.contain("Right Leg") && Hand.contain("Right Arm");
        }

        public void drawAIPhase(int drawNum) // draws drawNum cards for AI
        {
            if (drawNum == 0) // stop calling once drawNum is 0, meaning no more cards to add
                return;
            if (AIHand.getSize() < 10) // as long as the maximum hand limit is not exceeded
            {
                if (AIDeck.getSize() > 0) // as long as there are still cards left in the AI's Deck
                {
                    // draw a card
                    int rand = (int)(Math.random()*(AIDeck.getSize()));
                    AIHand.add(AIDeck.get(rand));
                    addAICard(AIHand.getSize()-1);
                    AIDeck.remove(rand);
                    drawAIPhase(drawNum-1); // call drawAIPhase() again but this time, draw one less card than before
                }
                else
                    side2.setIcon(new ImageIcon (reSize (cWidth + 10, cHeight + 10, label1))); // shows the Deck is empty
                con.repaint(); // repaint
            }
        }

        public boolean play(Board[] board, int pos) // plays a Card in AI's Hand at index pos to the Board Array board
        {
            int put = -1; // finds a place to put it on the Board Array
            for (int i = 0; i < board.length; i ++) // loop through all elements of the in board
            {
                if (!board[i].getFull()) // if the board isn't full, then we can put it in
                {
                    put = i; // set put to current position
                    i = 5; // leave the loop
                }
            }

            if (put!= -1 && pos != -1) // as long as there's a place to put it and the position isn't out of Bounds
            {
                AIHand.get(pos).setFace(true); // make it the actual Image now (since you're playing it)
                board[put].setFull (reSize (cWidth, cHeight, AIHand.get(pos).getImage()), AIHand.get(pos)); // set the put index of board[] to the AI Hand's pos index 
                con.remove(cardAILabels.get(pos)); // remove AI Hand's pos index from con

                // remove AI Hand's pos index from AIHand and cardAILabels
                AIHand.remove (pos);
                cardAILabels.remove(pos);

                for (int i = 0; i < AIHand.getSize (); i++) // reshift all the other cards accordingly
                    cardAILabels.get (i).setLocation (width/2-5*cWidth + i*cWidth, 0);

                // repaint everything
                board[put].repaint();
                con.repaint();
            }
            return put != -1; // return whether or not the card was able to be played (makes it easier to deal with AI)
        }

        public boolean summon (int pos) // Summon a Monster in AI's Hand at index pos
        {
            return play (btBoard, pos); // AI's Monsters go on the btBoard
        }

        public boolean play (int pos) // Play a Spell in AI's Hand at index pos
        {
            return play (bbBoard, pos); // AI's Spells go on the bbBoard
        }

        public void mainPhase() // AI's Main Phase
        {  
            timer = new Timer (1000, new ActionListener() // timer used so user can actually see AI playing the Cards
                {
                    public void actionPerformed (ActionEvent e)
                    {
                        int count = 0; // controls the number of cards AI plays per loop (max 1)
                        if (AIHand.contain("Sangan") && summon(AIHand.search("Sangan")) && count!=1)
                        {
                            summon(AIHand.search("Sangan")); // summon Sangan (if possible)
                            for (int i = 0; i < AIDeck.getSize(); i ++)
                            {
                                String name = AIDeck.get(i).getName();
                                // loop through all Cards in AI's Deck and add the first Exodia Card
                                if(name.equals("Left Leg") || name.equals("Right Leg") || name.equals("Left Arm") || name.equals("Right Arm") || name.equals("Forbidden One"))
                                {
                                    AIHand.add(AIDeck.get(i));
                                    addAICard (AIHand.getSize()-1);
                                    AIDeck.remove(i);
                                    i = AIDeck.getSize();
                                }
                            }
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Swift Scarecrow") && summon(AIHand.search("Swift Scarecrow")) && count!=1)
                        {
                            summon(AIHand.search("Swift Scarecrow")); // summon Swift Scarecrow (if possible)
                            LPC += 1000; // add 1000 LIfePoints to AI
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Accumulated Fortune") && AIHand.getSize() < 5 && count!=1 && AIHand.getSize() < 8)
                        {
                            play(AIHand.search("Accumulated Fortune"));
                            drawAIPhase(2);
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Gift Card") && LPH >= 4000 && LPH <= 6000 && LPC >= LPH && count!=1)
                        {
                            play(AIHand.search("Gift Card"));
                            LPH += 3000;
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Battle Fader") && summon(AIHand.search("Battle Fader")) && count!=1)
                        {
                            summon(AIHand.search("Battle Fader"));
                            int pos = AIDeck.search("Battle Fader");
                            if (pos != -1)
                            {
                                AIHand.add(AIDeck.get(pos));
                                addAICard (AIHand.getSize()-1);
                                AIDeck.remove(pos);
                            }
                            LPH -= 1000;
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("One Day of Peace") && play(AIHand.search("One Day of Peace")) && count!=1 && AIHand.getSize() < 8)
                        {
                            play(AIHand.search("One Day of Peace"));
                            drawAIPhase(2);
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Pot of Greed") && play(AIHand.search("Pot of Greed")) && count!=1 && AIHand.getSize() < 8)
                        {
                            play(AIHand.search("Pot of Greed"));
                            drawAIPhase(2);
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Upstart Goblin") && play(AIHand.search("Upstart Goblin")) && count!=1 && AIHand.getSize() < 9)
                        {
                            play(AIHand.search("Upstart Goblin"));
                            drawAIPhase(1);
                            LPH += 1000;
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Hope for Escape") && play(AIHand.search("Hope for Escape")) && LPH >= LPC + 4000 & LPC >= 3000 && count!=1)
                        {
                            play(AIHand.search("Hope for Escape"));
                            LPC -= 1000;
                            for (int i = 0; i < (LPH-LPC)/2000; i++)
                                drawAIPhase(1);
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Jar of Greed") && play(AIHand.search("Jar of Greed")) && count!=1 && AIHand.getSize() < 9)
                        {
                            play(AIHand.search("Jar of Greed"));
                            drawAIPhase(1);
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Legacy of Yata-Garasu") && play(AIHand.search("Legacy of Yata-Garasu")) && count!=1 && AIHand.getSize() < 9)
                        {
                            play(AIHand.search("Legacy of Yata-Garasu"));
                            drawAIPhase(1);
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Threatening Roar") && play(AIHand.search("Threatening Roar")) && count!=1)
                        {
                            play(AIHand.search("Threatening Roar"));
                            LPC += 1000;
                            LPH -= 1000;
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        else if (AIHand.contain("Waboku") && play(AIHand.search("Waboku")) && count!=1 && AIHand.getSize() < 9)
                        {
                            play(AIHand.search("Waboku")); 
                            // both players draw 1 card
                            drawAIPhase(1);
                            drawPhase(1);
                            count = 1; // AI cannot play anymore Cards this loop
                        }
                        // show the new Life Points
                        health.setText(String.valueOf (LPH));
                        aiHealth.setText(String.valueOf (LPC));
                        health.repaint();
                        aiHealth.repaint();
                        if (count == 0) // if count = 0, that means AI cannot play anymore cards this turn. If so, stop the timer
                        {
                            timer.stop();
                            // repaint the Next Phase button so it's not Grey
                            draw.setIcon (new ImageIcon (reSize (3*cHeight/2+30,3*cWidth/2+30, label1)));
                            draw.repaint();
                        }
                    }
                });
            timer.setRepeats (true);
            timer.start();
        }

        public int minAttack (int atk)
        {
            int minPos = -1;
            Card defend = null;

            for (int i = 0; i < 5; i++)
            {
                if (tbBoard[i].getFull()) // so you don't get NullPointer
                {
                    defend = tbBoard[i].getCard();
                    if (minPos < 0 && defend.getAttack() <= atk) // if this is the first monster that has attack less than atk, set minPos to current position
                        minPos = i;
                    else if (minPos < 0) // if this monster has attack higher than atk, decrement minPos by 1 (so minPos returns -1 iff there are no Human monsters)
                        minPos --;
                    else if (defend.getAttack() <= atk && defend.getAttack() <= tbBoard[minPos].getCard().getAttack()) // otherwise, make sure current monster has least attack
                        minPos = i;
                }
            }
            return minPos; // minPos will return -1 if there are no monsters (so AI can attack directly)}
        }

        public void battleAIPhase() 
        {
            timer = new Timer (1000, new ActionListener() // timer used again so player can see AI battling
                {
                    int minPos;
                    int i = 0;
                    Card fight, defend;
                    public void actionPerformed (ActionEvent e)
                    {
                        if (btBoard[i].getFull()) // so there's no null pointer
                        {
                            fight = btBoard[i].getCard();
                            minPos = minAttack(fight.getAttack());
                            if (minPos == -1) // player has no monsters, so AI attacks player directly
                                LPH -= fight.getAttack();
                            else if (minPos >= 0) // player has monsters
                            {
                                defend = tbBoard[minPos].getCard();

                                if (fight.getAttack() >= defend.getAttack()) // checks if player's monster dies
                                {
                                    tbBoard[minPos].setEmpty();
                                    tbBoard[minPos].repaint();
                                    LPH -= (fight.getAttack()-defend.getAttack());
                                }
                                if (fight.getAttack() <= defend.getAttack()) // checks if the AI's monster dies
                                {
                                    btBoard[i].setEmpty();
                                    btBoard[i].repaint();
                                    LPC -= (defend.getAttack()-fight.getAttack());
                                }

                                // updates the life points
                                health.setText(String.valueOf (LPH));
                                aiHealth.setText(String.valueOf (LPC));
                                health.repaint();
                                aiHealth.repaint();
                                con.repaint();
                            }
                        }
                        i ++;
                        if (i == 5) // loops this 5 times for (theoretically) every monster on AI board
                        {
                            timer.stop();
                            i = 0;
                            draw.setIcon (new ImageIcon (reSize (3*cHeight/2+30,3*cWidth/2+30, label1)));
                            draw.repaint();
                        }    
                    }
                });
            timer.setRepeats (true);
            timer.start();
        }

        public void drawPhase(int drawNum) // allows player to draw
        {
            if (drawNum == 0)
                return;
            if (Hand.getSize() < 10) // draws if player's hand is less than 10
            {
                if(Deck.getSize() > 0)
                {
                    int rand = (int)(Math.random()*(Deck.getSize())); // randomly picks card from deck and adds to hand
                    Hand.add(Deck.get(rand));
                    addCard(Hand.getSize()-1);
                    Deck.remove(rand);
                    drawPhase(drawNum-1);
                }
                else
                    side1.setIcon(new ImageIcon (reSize (cWidth + 10, cHeight + 10, label1))); // shows the deck is empty
                con.repaint();
            }
            else
                JOptionPane.showMessageDialog(frame,"You cannot draw more because you have exceed the Hand limit (10)"); // tells player that he/she is at the card limit
            if (Deck.getSize() == 10)
                JOptionPane.showMessageDialog(frame,"BEWARE! You only have 10 cards left in your Deck! You will lose the Duel when your Deck runs out of cards!"); // warns player if he/she is running out of cards
        }

        class ButtonMe implements ActionListener // essentially controls the game
        {
            public void play(Board[] board)  // adds a card from the hand to the board
            {
                Card card = null;
                int pos = -1, put = -1;
                
                // check which card has been picked
                for (int i = 0; i < adapters.size(); i ++)
                {
                    if (adapters.get(i).getPick())
                        pos = i;
                }

                // sets all picks to false
                for (int i = 0; i < adapters.size(); i ++)
                {
                    adapters.get(i).setPick(false);
                }

                // checks if board still has space
                for (int i = 0; i < 5; i ++)
                {
                    if (!board[i].getFull())
                    {
                        put = i;
                        i = 5;
                    }
                }

                // makes sure card is face up
                Hand.get(pos).setFace(true);

                card = Hand.get(pos);

                // adds card to board
                board[put].setFull (reSize (cWidth, cHeight, Hand.get(pos).getImage()), Hand.get(pos));

                // remove card from visibility
                con.remove(cardLabels.get(pos));
                con.remove(cardInfo.get(pos));

                // actually remove card from hand, and everything assosciated with it
                Hand.remove (pos);
                cardLabels.remove(pos);
                cardInfo.remove(pos);
                adapters.remove(pos);

                // resets all card positions and thier mouse adapters
                for (int i = 0; i < Hand.getSize (); i++)
                {
                    cardLabels.get (i).setLocation (width/2-5*cWidth + i*cWidth, height - 2*cHeight);
                    adapters.get (i).setLoc (width/2-5*cWidth + i*cWidth, height - 2*cHeight);
                }

                effect (card); // executes effect of card
                
                // updates life points
                health.setText(String.valueOf (LPH));
                aiHealth.setText(String.valueOf (LPC));
                health.repaint();
                aiHealth.repaint();

                con.repaint();
            }

            public void result(boolean won) // checks if anyone has won and display accordingly
            {
                bgimg.remove (con);
                if (won)    
                {
                    bgimg.add(winner);
                    winner.setLocation(0,0);
                }
                else
                {
                    bgimg.add(loser);
                    loser.setLocation (0,0);
                }
                bgimg.repaint();
            }

            public void actionPerformed (ActionEvent e) 
            {
                if (e.getActionCommand().equals("Next Phase") && LPH <= 0) // check if player has lost by loss of life points
                    result(false);
                if (e.getActionCommand().equals("Next Phase") && LPC <= 0) // check if AI has lost by loss of life points
                    result(true);
                else if (e.getActionCommand().equals ("Play") && phase == 1 && turn % 2 == 0) // plays a spell
                    play(ttBoard);
                else if (e.getActionCommand().equals ("Summon") && phase == 1 && turn % 2 == 0) // summons a monster
                    play(tbBoard);
                    
                    // controls phases
                if (phase == 0 && turn % 2 == 0) 
                {
                    drawPhase(1);
                    phase = 1;
                }
                else if (e.getActionCommand().equals("Next Phase") && turn % 2 == 0 && turn > 0 && phase != 0)
                {
                    // 0 if Draw Phase, 1 if Main Phase, 2 if Battle Phase, 3 if End Phase
                    if (phase == 3)
                        turn++;
                    phase = (phase+1)%4; 
                }
                else if (e.getActionCommand().equals("Next Phase") && turn == 0 && phase != 0)
                {
                    // 0 if Draw Phase, 1 if Main Phase, 2 if Battle Phase, 3 if End Phase
                    if (phase == 0)
                        phase = 1;
                    else if (phase == 1)
                        phase = 3;
                    else if (phase == 3)
                    {
                        phase = 0;
                        turn++;
                    } 
                }
                
                // executes phase based on the above
                if (phase == 1 && turn % 2 == 0) // player main phase
                {
                    phaseName.setText("Main Phase");
                    phaseName.repaint();
                }
                if (phase == 2 && turn % 2 == 0) // player battle phase
                {
                    phaseName.setText ("Battle Phase");
                    phaseName.repaint();

                    // shows which monster on the AI side was selected
                    con.add(opSel);
                    opSel.setSize (cHeight*3/2, 40);
                    opSel.setLocation (width/2 + (cHeight+20)*5/2, (height/2 - 3*(cHeight + 20)) + 1*(cHeight + 20));
                    opSel.setText ("No selection");

                    // shows which monster on the player's side was selected
                    con.add(plSel);
                    plSel.setSize (cHeight*3/2, 40);
                    plSel.setLocation (width/2 + (cHeight+20)*5/2, (height/2 - 3*(cHeight + 20)) + 4*(cHeight + 20)-50);
                    plSel.setText ("No selection");

                    con.repaint();

                    // checks if board is empty to allow for direct attacking
                    if (!btBoard[0].getFull()&&!btBoard[1].getFull()&&!btBoard[2].getFull()&&!btBoard[3].getFull()&&!btBoard[4].getFull())
                    {
                        opSmack.setFace(true);
                        opSmack.setSelected(false);
                        opSmack.faceEm();
                    }

                    // adds the mouse listeners assosciated with battle phase
                    for (int i = 0; i < btBoard.length; i++)
                    {           
                        btBoard[i].addMouseListener (battle [0][i]);
                        tbBoard[i].addMouseListener (battle [1][i]);
                        battle [1][i].setAttacked(false);
                    }
                }
                else if (phase == 3 && turn % 2 == 0)
                {
                    // removes everything from battle phase
                    opSmack.setFace(false);
                    opSmack.faceEm();

                    con.remove(opSel);
                    con.remove(plSel);
                    con.repaint();

                    for (int i = 0; i < battle[0].length; i++)
                    {
                        battle[0][i].removeStuff ();
                        battle[1][i].removeStuff ();
                        btBoard[i].removeMouseListener(battle[0][i]);
                        tbBoard[i].removeMouseListener(battle[1][i]);
                    }

                    // end phase
                    phaseName.setText ("End Phase");
                    phaseName.repaint();
                    
                    // check if player has won yet
                    if (HWinFast())
                        result(true);
                    else if (Deck.getSize() == 0)
                        result(false);
                }
                else if (e.getActionCommand().equals("Next Phase") && phase == 0 && turn % 2 == 1) // AI main phase
                {
                    // makes next phase button grey to indicate main phase still happening
                    draw.setIcon (new ImageIcon (reSize (3*cHeight/2,3*cWidth/2, selectPic)));
                    draw.repaint();
                    phaseName.setText ("Opponent Main Phase");
                    phaseName.repaint();
                    drawAIPhase(1);
                    con.repaint();
                    phase++;

                    // clears all spell cards played
                    for (int i = 0; i < 5; i++)
                    {
                        bbBoard[i].setEmpty();
                        bbBoard[i].repaint();
                        ttBoard[i].setEmpty();
                        ttBoard[i].repaint();
                    }

                    // runs main phase AI
                    mainPhase();
                    con.repaint();
                }
                else if (e.getActionCommand().equals("Next Phase") && phase == 1 && turn % 2 == 1) // AI battle phase
                {
                    // makes next phase button grey to show battle phase still happening
                    draw.setIcon (new ImageIcon (reSize (3*cHeight/2,3*cWidth/2, selectPic)));
                    draw.repaint();
                    phaseName.setText ("Opponent Battle Phase");
                    phaseName.repaint();
                    
                    // execute battle phase AI
                    battleAIPhase();
                    phase++;
                    con.repaint();
                }
                else if (e.getActionCommand().equals("Next Phase") && phase == 2 && turn % 2 == 1) // AI end phase
                {
                    phaseName.setText ("Opponent End Phase");
                    phaseName.repaint();
                    
                    // check if AI has won yet
                    if (AIWinFast())
                        result(false);
                    else if (AIDeck.getSize() == 0)
                        result(true);
                    phase ++;
                }
                else if (e.getActionCommand().equals("Next Phase") && phase == 3 && turn % 2 == 1) // player main phase (first one was because player's first turn has no battle phase)
                {
                    phaseName.setText ("Main Phase");
                    phaseName.repaint();
                    drawPhase(1);
                    turn ++;
                    phase = 1;
                }
            }
        }

        public void effect (Card card) // odes the effect of the card
        {
            String name = card.getName();

            if (name.equals ("Sangan"))
            {
                for (int i = Deck.getSize()-1; i >= 0; i--)
                {
                    String get = Deck.get(i).getName();
                    if(get.equals("Left Leg") || get.equals("Right Leg") || get.equals("Left Arm") || get.equals("Right Arm") || get.equals("Forbidden One"))
                    {
                        Hand.add(Deck.get(i));
                        addCard (Hand.getSize()-1);
                        Deck.remove(i);
                        i = 0;
                    }
                } 
            }
            else if (name.equals ("Swift Scarecrow"))
                LPH += 1000;
            else if (name.equals ("Accumulated Fortune") && Hand.getSize() < 5)
                drawPhase(2);
            else if (name.equals("Gift Card"))
                LPC += 3000;
            else if (name.equals("Battle Fader"))
            {
                int pos = Deck.search("Battle Fader");
                if (pos != -1)
                {
                    Hand.add(Deck.get(pos));
                    addCard(Hand.getSize()-1);
                    Deck.remove(pos);
                }
                LPC -= 1000;
            }
            else if (name.equals("One Day of Peace") || name.equals("Pot of Greed"))
                drawPhase(2);
            else if (name.equals("Upstart Goblin"))
            {
                drawPhase(1);
                LPC += 1000;
            }
            else if (name.equals("Hope for Escape"))
            {
                LPH -= 1000;
                for (int i = 0; i < (LPC - LPH)/2000; i++)
                    drawPhase(1);
            }
            else if (name.equals("Jar of Greed") || name.equals("Legacy of Yata-Garasu"))
            {
                drawPhase(1);
            }
            else if (name.equals("Threatening Roar"))
            {
                LPH += 1000;
                LPC -= 1000;
            }
            else if (name.equals("Waboku"))
            {
                drawPhase(1);
                drawAIPhase(1);
            }
        }
    }

    class Board extends JPanel // playing field
    {
        private boolean full; // checks if there is anything in the panel
        private Image img; // image of card in panel
        private Image panet; // image of panel
        private Card card; // card stored in panel for further referencing

        public Board (int i, int check) // constructs empty board
        {
            full = false;
            img = null;
            panet = label1;
            card = null;
            setOpaque (false);
            setLocation (width/2-5*(cHeight + 20)/2 + i*(cHeight+20), (height/2 - 3*(cHeight + 20)) + check*(cHeight + 20));
            setSize (cHeight + 20, cHeight + 20);
        }

        public void setEmpty() // sets a board as empty
        {
            full = false;
            img = null;
            card = null;
        }

        public Image getImg() // returns img of card
        {
            return img;
        }

        public Card getCard() // returns card in panel
        {
            return card;
        }

        public void setFull(Image pic, Card c) // puts a card on the panel
        {
            full = true;
            img = pic;
            card = c;
        }

        public boolean getFull() // checks if panel has a card
        {
            return full;
        }

        public void paint (Graphics g) // shows panel
        {
            super.paintComponent (g);
            g.drawImage (panet, 0, 0, this);
            if (full) // shows card only if it is full to avoid null pointer
                g.drawImage (img, (cHeight + 10)/2 - cWidth/2, 5, this);
        }
    }

    class AdaptMe extends MouseAdapter // mouse listeners for card selection
    {
        private boolean pick = false;
        private int beginX;
        private int beginY;
        private JLabel panel; 

        public AdaptMe (int x, int y) // constructs new AdaptMe and stores initial coords
        {
            beginX = x;
            beginY= y;
            panel = imageMe (reSize (width/4, height - 100, label1));
        }

        public void setPick (boolean p) // set whether card has been selected
        {
            pick = p;
        }

        public boolean getPick () // returns whether or not card has been selected
        {
            return pick;
        }

        public int getX () // gets the initial x coord
        {
            return beginX;
        }

        public int getY () // gets the inital y coord
        {
            return beginY;
        } 

        public void setLoc (int x, int y) // sets initial location 
        {
            beginX = x;
            beginY = y;
        }

        public void mouseClicked(MouseEvent e) 
        {
            Component comp = (Component) e.getSource();
            int check = -1;
            boolean temp = !pick; // not just true because it has to toggle

            // makes sure there is only one selection
            for(int i = 0; i < adapters.size (); i ++)
                adapters.get(i).setPick (false);

            pick = temp;

            if (pick)
            {
                for (int i = 0; i < cardLabels.size (); i ++)
                {
                    // removes all other card infos from view
                    cardLabels.get (i).setLocation (adapters.get (i).getX (), adapters.get (i).getY ());
                    cardInfo.get(i).setVisible(false);

                    // shows card info of selected card
                    if (adapters.get(i).getPick())
                        cardInfo.get(i).setVisible(true);
                }
                
                // moves selected card up to "highlight"
                comp.setLocation (beginX, beginY - cHeight/4);
            }
            else
            {
                // moves card back to initial lovation
                comp.setLocation (beginX, beginY);
                
                // clears all card info menus
                for (int i = 0; i < cardLabels.size (); i ++)
                    cardInfo.get(i).setVisible(false);

                // resets the check (in case)
                check = -1;
            }
        }
    }

    class BattleMe extends MouseAdapter // battle phase controller
    {
        private boolean selection = false;
        private boolean selected = false;
        private int row, col;
        private Font font;
        private JButton attack;
        private boolean attacked = false;
        private JLabel showSelect;

        public BattleMe (int r, int c) // creates buttons to select cards to attack
        {
            SelectStuff selectem = new SelectStuff();
            font = new Font ("Times New Roman", Font.BOLD, 12);

            attack = new JButton ("Select", new ImageIcon (reSize(cHeight,40,label1)));
            attack.setHorizontalTextPosition(JButton.CENTER);
            attack.setVerticalTextPosition(JButton.CENTER);
            attack.setFont (font);
            attack.setForeground (Color.WHITE);
            attack.addActionListener (selectem);

            row = r;
            col = c;
        }

        class SelectStuff implements ActionListener // listener for the buttons
        {
            public void actionPerformed (ActionEvent e)
            {
                if (e.getActionCommand ().equals ("Select")) 
                {
                    int pos = -1;

                    // if selected, turn all previous selections false
                    for (int i = 0; i < battle[row].length; i++)
                        battle[row][i].setSelected (false);
                    selected = true;

                    // checks if any monster from the opposite side has bee selected
                    for (int i = 0; i < battle[1-row].length; i++)
                    {
                        if (battle[1-row][i].getSelected())
                        {
                            pos = i;
                            i = battle[1-row].length;
                        }
                    }

                    // greys select button to show something has be selected
                    attack.setIcon (new ImageIcon (reSize (cHeight*2, 50, selectPic)));
                    attack.repaint();

                    if (pos != -1) // if 2 monsters have been selected
                    {
                        // pos is the AI's monster
                        // col is the Human's monster
                        Card fight = null, defend = null;
                        int col1 = row == 0 ? pos:col; // if row = 0, col1=col otherwise, col1 = pos
                        int col2 = row == 1 ? pos:col; // opposite for row = 1

                        fight = tbBoard[col1].getCard();
                        defend = btBoard[col2].getCard();

                        // checks which monsters survive
                        if (fight.getAttack() >= defend.getAttack())
                        {
                            btBoard[col2].setEmpty();
                            btBoard[col2].repaint();
                            LPC -= (fight.getAttack()-defend.getAttack());
                        }
                        if (fight.getAttack() <= defend.getAttack())
                        {
                            tbBoard[col1].setEmpty();
                            tbBoard[col1].repaint();
                            LPH -= (defend.getAttack()-fight.getAttack());
                        }

                        // update life ppoints
                        health.setText(String.valueOf (LPH));
                        aiHealth.setText(String.valueOf (LPC));
                        health.repaint();
                        aiHealth.repaint();

                        // makes it so the player's monster cannot attack a second time
                        if (row == 1)
                            attacked = true;
                        else
                            battle[1][pos].setAttacked(true);

                        // removes select buttons after each complete attack
                        for (int i = 0; i < battle[row].length; i++)
                        {
                            battle[row][i].removeStuff ();
                            battle[1-row][i].removeStuff ();
                        }

                        // clears all selections
                        for (int i = 0; i < battle[row].length; i++)
                        {
                            battle[row][i].setSelected (false);
                            battle[1-row][i].setSelected(false);
                        }

                        // checks if the AI's boards is now empty to show Attack Opponent button
                        if (!btBoard[0].getFull()&&!btBoard[1].getFull()&&!btBoard[2].getFull()&&!btBoard[3].getFull()&&!btBoard[4].getFull())
                        {
                            opSmack.setFace(true);
                            opSmack.setSelected(false);
                            opSmack.faceEm();
                        }
                    }
                    else
                    {
                        // if the player chooses to attack the opponent directly
                        if (opSmack.getSelected() && row == 1)
                        {
                            Card fight = tbBoard[col].getCard();
                            LPC -= fight.getAttack();

                            attacked = true; // sets it so player can't attack again

                            // updates AI health
                            aiHealth.setText(String.valueOf (LPC));
                            aiHealth.repaint();

                            // clears all previous selections
                            for (int i = 0; i < battle[row].length; i++)
                                battle[row][i].removeStuff ();

                            for (int i = 0; i < battle[row].length; i++)
                                battle[row][i].setSelected (false);

                            // clears grey from Attack Opponent button (set grey when pressed)
                            opSmack.setSelected (false);
                            opSmack.faceEm();
                        }
                    }
                } 
            }
        }

        public void setSelect (boolean s) // sets whether or not the board itself is selected
        {
            selection = s;
        }

        public boolean getSelect () // returns whether or not the board itself is selected
        {
            return selection;
        }

        public void setSelected (boolean s) // sets whether or not the monster has been selected to attack
        {
            selected = s;
        }

        public boolean getSelected () // returns whether or not the monster has been selected to attack
        {
            return selected;
        }
        
                public void setAttacked(boolean a) // sets whether or not a monster has attacked already
        {
            attacked = a;
        }

        public boolean getAttacked() // returns whether or not a monster has attacked directly
        {
            return attacked;
        }

        public int getRow () // returns the row of the board
        {
            return row;
        }

        public int getCol () // returns the col of the board
        {
            return col;
        }

        public void setStuff(boolean check1, boolean check2) 
        {
            // every time something is selected and has not attacked, a "Select" button is added
            if(check1&&!check2)
            {
                con.add (attack);
                if (row == 0)
                    attack.setLocation (width/2 + (cHeight+20)*5/2, (height/2 - 3*(cHeight + 20)) + 2*(cHeight + 20) - 50);
                else
                    attack.setLocation (width/2 + (cHeight+20)*5/2, (height/2 - 3*(cHeight + 20)) + 3*(cHeight + 20));
                attack.setSize (cHeight, 40);

                // selection shown
                if (row == 0)
                    opSel.setText (btBoard[col].getCard().getName() + " " + (col + 1));
                if (row == 1)
                    plSel.setText (tbBoard[col].getCard().getName() + " " + (col + 1));

                con.repaint();
            }
            else
            {
                // sets button back to original colour (turned grey when selected)
                attack.setIcon (new ImageIcon (reSize (cHeight*2, 50, label1)));
                attack.repaint();

                // clears text in selection displays
                if (!battle[0][0].getSelect()&&!battle[0][1].getSelect()&&!battle[0][2].getSelect()&&!battle[0][3].getSelect()&&!battle[0][4].getSelect())
                    opSel.setText ("No selection");
                if (!battle[1][0].getSelect()&&!battle[1][1].getSelect()&&!battle[1][2].getSelect()&&!battle[1][3].getSelect()&&!battle[1][4].getSelect())
                    plSel.setText ("No selection");
                    
                    // remove the button (if it was not already removed)
                con.remove (attack);
                con.repaint();
            }
        }

        public void removeStuff() // removes any remaining buttons and clears selections
        {
            attack.setIcon (new ImageIcon (reSize (cHeight*2, 50, label1)));
            attack.repaint();

            if (row == 0)
                opSel.setText ("No selection");
            if (row == 1)
                plSel.setText ("No selection");

            con.remove (attack);
            con.repaint();
        }

        public void mouseClicked(MouseEvent e)
        {
            int check = -1;
            boolean temp = !selection;

            // ensures only one of the panels on either side can be selected at once
            for(int i = 0; i < battle[row].length; i ++)
            {
                battle[row][i].setSelect (false);
                battle[row][i].setSelected (false);
            }

            selection = temp;      

            // runs setStuff which will either display or remove the select button
            if (row == 0)
            {
                if (btBoard[col].getFull())
                {
                    for (int i = 0; i < battle[row].length; i++)
                    {
                        battle[row][i].setStuff (battle[row][i].getSelect(), battle[row][i].getAttacked());
                    }
                }
            }
            else
            {
                if (tbBoard[col].getFull())
                {
                    for (int i = 0; i < battle[row].length; i++)
                    {
                        battle[row][i].setStuff (battle[row][i].getSelect(), battle[row][i].getAttacked());
                    }
                }
            }
        }
    }

    class AttackFace // button for attacking the opponent directly
    {
        private boolean canFace, selected;
        private JButton punch;

        public AttackFace() // constructs button for attacking opponent
        {
            AttacktionListener attacktion = new AttacktionListener();
            canFace = false;
            selected = false;
            Font font = new Font ("Times New Roman", Font.BOLD, 12);
            punch = new JButton ("Attack Opponent", new ImageIcon (reSize(250,40,label1)));
            punch.setHorizontalTextPosition(JButton.CENTER);
            punch.setVerticalTextPosition(JButton.CENTER);
            punch.setFont (font);
            punch.setForeground (Color.WHITE);
            punch.addActionListener (attacktion);
        }

        public boolean getFace () // returns whether or not the player can attack directly
        {
            return canFace;
        }

        public void setFace (boolean c) // sets whether or not the player can attack directly
        {
            canFace = c;
        }

        public boolean getSelected () // returns whether or not the button has been selected
        {
            return selected;
        }

        public void setSelected (boolean c) // sets whether or not the button has been selected
        {
            selected = c;
        }

        public void faceEm () // determines whether or not to add button
        {
            if (canFace) // can attack directly
            {
                // sets grey if selected, black if not
                if (selected)
                    punch.setIcon (new ImageIcon (reSize (250, 40, selectPic)));
                else 
                    punch.setIcon (new ImageIcon (reSize (250, 40, label1)));

                    // adds the button for selection
                con.add (punch);
                punch.setSize (200, 40);
                punch.setLocation (width - cWidth - 200, cWidth + cHeight);
                con.repaint();
            }
            else // cannot attack directly
            {   
                // removes the button
                con.remove (punch);
                con.repaint();
            }
        }

        class AttacktionListener implements ActionListener
        {
            public void actionPerformed (ActionEvent e)
            {
                if (e.getActionCommand().equals ("Attack Opponent"))
                {
                    // toggles selection
                    selected = !selected;
                    
                    // grey if selected, black if not
                    if (selected)
                        punch.setIcon (new ImageIcon (reSize (250, 40, selectPic)));
                    else 
                        punch.setIcon (new ImageIcon (reSize (250, 40, label1)));
                        
                    // checks whether to remove button or not    
                    faceEm();
                }

                // checks if player has selected a monster
                int pos = -1;
                for (int i = 0; i < battle[1].length; i ++)
                {
                    if (battle[1][i].getSelected())
                    {
                        pos = i;
                        i = battle[1].length;
                    }
                }

                // if yes, and the button is selected
                if (selected && pos != -1)
                {
                    Card fight = tbBoard[pos].getCard();
                    LPC -= fight.getAttack();

                    battle[1][pos].setAttacked(true); // player's monster cannot attack again

                    // update AI life points
                    aiHealth.setText(String.valueOf (LPC));
                    aiHealth.repaint();
                    
                    // clears all selections
                    for (int i = 0; i < battle[1].length; i++)
                        battle[1][i].removeStuff ();

                    for (int i = 0; i < battle[1].length; i++)
                        battle[1][i].setSelected (false);

                    selected = false;
                    
                    // updates button
                    opSmack.faceEm();
                }
            }
        }
    }
    
    // utility methods
    public JLabel imageMe (Image pic) // creates a JLabel with an image
    {
        return new JLabel (new ImageIcon(pic));
    }

    public Image pic (int width, int height, String file) // imports an image with specific dimensions
    {
        Image img = null;
        try
        {
            img = ImageIO.read (new File (file));
        }
        catch (IOException e)
        {
        }

        return img.getScaledInstance (width, height, Image.SCALE_SMOOTH);
    }

    public Image reSize (int width, int height, Image img) // resizes any image
    {
        return img.getScaledInstance (width, height, Image.SCALE_SMOOTH);
    }

    public static void main (String[] args) // the main
    {
        // creates a YuGuiOh
        YuGuiOh window = new YuGuiOh ();
        window.setVisible (true);
    }
}
