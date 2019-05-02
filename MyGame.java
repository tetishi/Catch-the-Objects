import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;
import java.lang.*;

public class Mygame extends JPanel implements Runnable {
    int NUMOFBALLS = 4;
    int NUMOFSQUARES = 2;
    int i, j, x;
    int speed = 10;
    static int FWIDTH = 500, FHEIGHT = 400;
    boolean inside = false;
    Thread th;
    Graphics2D g;
    //creating objects
    Bowl bowl = new Bowl();
    Ball ball[] = new Ball[NUMOFBALLS];
    Square square[] = new Square[NUMOFSQUARES];
    SquareMover squareMover[] = new SquareMover[NUMOFSQUARES];
    BallMover ballMover[] = new BallMover[NUMOFBALLS];


    //static Color c1 = Color.WHITE,c2 = Color.BLACK;
    static Color colorWhite = Color.WHITE;
    static Color colorBlack = Color.BLACK;

    public Mygame(){

        //position of bowl
        bowl.setMx(30);
        bowl.setMy(200);
        setOpaque(false);//do we need this?
        try{
            setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("TimesRoman", 1, 24), new Color(200, 197, 109)));
            for(LookAndFeelInfo inf: UIManager.getInstalledLookAndFeels()){
                if(inf.getName().equals("Nimbus")){
                    UIManager.setLookAndFeel(inf.getClassName());
                    break;
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        // square's setting
        for(i = 0;i < square.length; ++i){
            square[i] = new Square();
            square[i].setMy(40);
            square[i].reset();
        }

        for(j = 0; j < square.length; ++j){
            squareMover[j]= new SquareMover(square[j],bowl);
            squareMover[j].setInitialDelay((j + 1) * 1500);
            squareMover[j].move();

        }
//balls' setting
        for(i = 0;i < ball.length; ++i){
            ball[i] = new Ball();
            ball[i].setMy(30);
            ball[i].reset();
        }

        for(j = 0; j < ball.length; ++j){
            ballMover[j]= new BallMover(ball[j], bowl);
            ballMover[j].setInitialDelay((j + 1) * 1500);
            ballMover[j].move();

        }

        //This to control the bowl by using mouse
        MouseListener ml = new MouseAdapter(){     //to chack if the user clicks on the bowl
            public void mousePressed(MouseEvent me){
                x = (int)me.getPoint().getX();
                if(bowl.contains(me.getPoint())){
                    inside = true;
                }
            }
            public void mouseReleased(MouseEvent me){
                inside = false;
            }
        };
        addMouseListener(ml);

        MouseMotionListener mll = new MouseAdapter(){ //to move the bowl by using mouse
            public void mouseDragged(MouseEvent me){
                if(inside) {
                    bowl.setMx((int)me.getPoint().getX());
                }
            }
        };
        addMouseMotionListener(mll);

        th = new Thread(this);
        th.start();
    }

    public void paint(Graphics g2) {
        g = (Graphics2D)g2;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g);

        //setting the background color
        //From top to bottom, white gradually changes to black
        GradientPaint gpp = new GradientPaint(getWidth(),0, colorWhite,  getWidth(), getHeight(), colorBlack);
        g.setPaint(gpp);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Making the Bowl's position lower.
        bowl.setMy(getHeight() - 35);

        //drawing Bowl
        bowl.drawOn(g);

        for(int i = 0; i < ball.length; ++i){
            ball[i].drawOn(g);
        }

        for(int i = 0; i < square.length; ++i){
            square[i].drawOn(g);
        }
    }

    public void run(){
        try{
            while(true){
                th.sleep(speed);
                if(bowl.life == 0) { //if the life becomes 0
                    for(int i = 0; i < ball.length; ++i){
                        ball[i].reset();
                        ballMover[i].stop(); //stop
                    }

                    for(int i = 0; i < square.length; ++i){
                        square[i].reset();
                        squareMover[i].stop();
                    }

                    JOptionPane.showMessageDialog(null, "Your Score Was " + bowl.score);
                    int a = JOptionPane.showConfirmDialog(null, "Restart The Game?", "Game Over", JOptionPane.OK_CANCEL_OPTION);

                    if(a == JOptionPane.OK_OPTION){ //if the user clicks ok for "restart the game?"
                        for(int i = 0; i < ball.length; ++i){   //balls move
                            ballMover[i].setInitialDelay((i + 1) * 1500); //each ball's delay
                            ballMover[i].move();
                        }

                        for(int i = 0; i < square.length; ++i){   //squares move
                            squareMover[i].setInitialDelay((i + 1) * 1500);
                            squareMover[i].move();
                        }

                        bowl.score =- 10;  //reset player's score
                        bowl.life = 10;    //reset player's life
                        bowl.updateScoreForBall();   //player gets points
                        bowl.score =- 50;   //reset player's score
                        bowl.updateScoreForSquare();  //player gets points
                    }
                    if(a == JOptionPane.CANCEL_OPTION){
                        System.exit(0);
                    }
                }
                repaint();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void main(String args[]){

        JFrame jfm = new JFrame();
        jfm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfm.setLayout(new BorderLayout());

        jfm.getContentPane().add(new Mygame());

        int a = JOptionPane.showConfirmDialog(null, "Welcome to Catch the Objects!! Start the game?",
                "Starting Menu", JOptionPane.OK_CANCEL_OPTION);
        if(a == JOptionPane.CANCEL_OPTION){
            System.exit(0);
        }

        JLabel l= new JLabel("Welcome! Enjoy!");
        l.setBorder(BorderFactory.createTitledBorder(null, "Catch the Objects!", TitledBorder.CENTER,
                TitledBorder.ABOVE_TOP, new Font("ComicSansMS", 1, 24), Color.PINK));
        jfm.add(l,BorderLayout.NORTH); //north means on the top


        final Mygame myGame = new Mygame();
        myGame.bowl.setOutputComponent(l);

        //keyboards
        KeyListener kl = new KeyAdapter(){
            public void keyPressed(KeyEvent ke){
                if(ke.getKeyCode() == KeyEvent.VK_RIGHT){
                    //if you press --> bowl goes to the right
                    myGame.bowl.setMx(myGame.bowl.X + 25);
                }
                if(ke.getKeyCode() == KeyEvent.VK_LEFT){
                    //if you press <-- bowl goes to the left
                    myGame.bowl.setMx(myGame.bowl.X - 25);
                }
            }
        };
        jfm.addKeyListener(kl);

        jfm.add(myGame);
        jfm.setSize(FWIDTH, FHEIGHT);
        jfm.setVisible(true);
    }
}

class Ball {
    protected Color color = Color.BLACK;
    int X,Y;
    Rectangle r;

    private boolean randomColor = false;
    private Random myRand;

    static Random rn = new Random();

    Ball() {
        //creating an object
        myRand = new Random();
        setRandomColor(randomColor);

    }

    //when bowl and rectangle intersects
    boolean fallsInBowl(Bowl b) {
        return r.intersects(b.r);
    }

    void setMx(int x) {
        X = x;
    }

    void setMy(int y) {
        Y = y;
    }

    public void reset() {
        //height of ball's start position
        setMy(20);
        //width of ball's start points
        setMx(30 + rn.nextInt(Mygame.FWIDTH - 40)); //making balls start from different points
    }

    void drawOn(Graphics2D g) {
        g.setColor(color);

        //setting the shape of balls
        g.fillOval(X, Y, 20, 25);
        r = new Rectangle(X, Y,20,25);
    }

    //creating random colors which are blue, green, or red for balls
    public void setRandomColor(boolean randomColor)
    {
        this.randomColor = randomColor;

        switch (myRand.nextInt(3))
        {
            case 0:  color = Color.BLUE;
                break;
            case 1:  color = Color.GREEN;
                break;
            case 2:  color = Color.RED;
                break;
            default: color = Color.BLACK;
                break;
        }
    }
}

class Square extends Ball {
    //color of Square
    public Color color = Color.YELLOW;

    int X,Y;
    Rectangle r;

    private Random myRand;

    static Random rn = new Random();

    public Square() {
        myRand = new Random();
    }

    //when squares and the bowl intersects
    boolean fallsInBowl(Bowl b){
        return r.intersects(b.r);
    }

    void setMx(int x){
        X = x;
    }

    void setMy(int y){
        Y = y;
    }

    public void reset(){
        //squares' start points of height
        setMy(30);
        //squares' start points of width
        setMx(30 + rn.nextInt(Mygame.FWIDTH - 40)); //making squares start from different points
    }


    void drawOn(Graphics2D g){
        g.setColor(color);

        //setting the shape of squares
        g.fillRect(X, Y, 12, 20);
        r = new Rectangle(X, Y,12,10);
    }
}

class BallMover{
    ActionListener al;
    Timer timer;
    Ball ball;
    Bowl bowl;
    boolean allowed = true;

    public BallMover(Ball ba, Bowl bo){
        ball = ba;
        bowl = bo;
        al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ball.setMy(ball.Y + 1); //to move the balls
                if(isAllowed()) {
                    if(ball.fallsInBowl(bowl)) { //if balls fit in the bowl
                        ball.reset(); //balls reset
                        bowl.updateScoreForBall(); //the user gets points
                    }
                }
                if(ball.r.y + ball.r.height > bowl.r.y + bowl.r.height / 2 && !ball.fallsInBowl(bowl)){ //if balls go further down than the bowl position
                    allowed = false; //player cannot catch balls
                    if(ball.r.y + ball.r.height * 2 >= 275){ //if the balls falls off farther than or equals to 275 which is the bottom of the screen
                        ball.reset(); //the balls' positions reset, then fall off again
                        bowl.life -= 1;  //player's life is deducted 1
                        bowl.updateLife(); //show current player's life
                    }
                } else {
                    allowed = true;
                }
            }
        };
        //the speed of Balls
        timer = new Timer(10, al);
    }
    void setInitialDelay(int i){
        timer.setInitialDelay(i);
    }

    boolean isAllowed(){
        return allowed;
    }

    void move(){
        timer.start();

    }

    void stop(){
        timer.stop();
    }
}

class SquareMover {
    ActionListener al;
    Timer timer;
    Square square;
    Bowl bowl;
    boolean allowed = true;

    public SquareMover(Square s,Bowl b){
        square = s;
        bowl = b;
        al = new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                square.setMy(square.Y + 1); //to move the bsquares
                if(isAllowed()){
                    if(square.fallsInBowl(bowl)){ //if squares fit in the bowl
                        square.reset(); //squares reset
                        bowl.updateScoreForSquare(); //the player gets points
                    }
                }
                if(square.r.y + square.r.height > bowl.r.y + bowl.r.height / 2 && !square.fallsInBowl(bowl)){ //if squares go further down than the bowl position
                    allowed = false; //the player cannot catch squares
                    if(square.r.y + square.r.height * 2 >= 275){ //if squares fall off farther than or equals to 275 which is the bottom of the screen
                        square.reset();  //the squares' positions reset, then fall off again
                        bowl.life -= 1; //player's life is deducted 1
                        bowl.updateLife(); //show current player's life
                    }
                } else {
                    allowed = true;
                }
            }
        };
        //speed of squares
        timer = new Timer(30, al);
    }
    void setInitialDelay(int i){
        timer.setInitialDelay(i);
    }

    boolean isAllowed(){
        return allowed;
    }

    void move(){
        timer.start();
    }

    void stop(){
        timer.stop();
    }
}

class Bowl {

    int X, Y;
    Rectangle r;
    JLabel l;
    int score = 0, life = 10;

    Bowl() {
    }

    void setMx(int dx) {
        X = dx;
    }

    void setOutputComponent(JLabel lb) {
        l = lb;
    }

    void updateScoreForSquare() {

        if (score < 100) {
            l.setText("Score = " + (score += 50) + "          Life = " + (life));

        } else {
            l.setText("Score = " + (score += 25) + "          Life = " + (life));
        }
    }

    void updateScoreForBall() {
        if (score < 100) {
            l.setText("Score = " + (score += 10) + "          Life = " + (life));

        } else {
            l.setText("Score = " + (score += 5) + "          Life = " + (life));
        }
    }

    void updateLife() {
        l.setText("Score = " + (score += 0) + "          Life = " + (life));
    }

    void setMy(int dy) {
        Y = dy;
    }

    boolean contains(Point p) {
        return r.contains(p);
    }

    void drawOn(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.fillArc(X - 15, Y - 10, 50, 30, 0, -180);
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(X - 15, Y, 50, 10);
        r = new Rectangle(X - 15, Y, 30, 20);
    }
}
