import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.EnumSet;

/* A simple paint program that is a first demonstration of using an off-screen CANVAS.*/
public class CANVAS extends JPanel {
   
   //The main routine simply opens a window that shows a CANVAS panel.
   
   public static void main(String[] args) {
      JFrame window = new JFrame("CANVAS");
      CANVAS content = new CANVAS();
      window.setContentPane(content);
      window.setJMenuBar(content.getMenuBar());
      window.pack();  
      window.setResizable(false); 
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      window.setLocation( (screenSize.width - window.getWidth())/2,
            (screenSize.height - window.getHeight())/2 );
      window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setVisible(true);
   }
    
   // The public static class CANVAS$Applet represents this program as an applet.  
   public static class Applet extends JApplet {
      public void init() {
         CANVAS content = new CANVAS();
         content.setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
         setContentPane( content );
         setJMenuBar(content.getMenuBar());
      }
   }
    
   // The possible drawing tools in this program. 
   private enum Tool { CURVE, LINE, RECT, OVAL, FILLED_RECT, FILLED_OVAL, SMUDGE, ERASE }
   
   private final static EnumSet<Tool> SHAPE_TOOLS = EnumSet.range(Tool.LINE, Tool.FILLED_OVAL);

   //by default
   private Tool currentTool = Tool.CURVE;

   private Color currentColor = Color.BLACK;
   
   private Color fillColor = Color.WHITE;

   private BufferedImage OSC;

   private boolean dragging;
    
   private int startX, startY;
   
   private int currentX, currentY;
    
   // size of the panel
   public CANVAS() {
      setPreferredSize(new Dimension(840,680));
      MouseHandler mouseHandler = new MouseHandler();
      addMouseListener(mouseHandler);
      addMouseMotionListener(mouseHandler);
   }
   
   public void paintComponent(Graphics g) {

      /* First create the off-screen CANVAS, if it does not already exist. */ 

      if (OSC == null)
         createOSC();

      g.drawImage(OSC,0,0,null);

      if (dragging && SHAPE_TOOLS.contains(currentTool)) {
         g.setColor(currentColor);
         putCurrentShape(g);
      }

   }
   
   private void createOSC() {
      OSC = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
      Graphics osg = OSC.getGraphics();
      osg.setColor(fillColor);
      osg.fillRect(0,0,getWidth(),getHeight());
      osg.dispose();
   }
   
   
   // A utility method to draw the current shape in a given graphics context. 
   private void putCurrentShape(Graphics g) {
      switch (currentTool) {
      case LINE:
         g.drawLine(startX, startY, currentX, currentY);
         break;
      case OVAL:
         putOval(g,false,startX, startY, currentX, currentY);
         break;
      case RECT:
         putRect(g,false,startX, startY, currentX, currentY);
         break;
      case FILLED_OVAL:
         putOval(g,true,startX, startY, currentX, currentY);
         break;
      case FILLED_RECT:
         putRect(g,true,startX, startY, currentX, currentY);
         break;
      }
   }
    
   // Draws a filled or unfilled rectangle with corners at the points (x1,y1) and (x2,y2). 
   private void putRect(Graphics g, boolean filled, int x1, int y1, int x2, int y2) {
      if (x1 == x2 || y1 == y2)
         return;
      if (x2 < x1) {  // Swap x1,x2 if necessary to make x2 > x1.
         int temp = x1;
         x1 = x2;
         x2 = temp;
      }
      if (y2 < y1) {  // Swap y1,y2 if necessary to make y2 > y1.
         int temp = y1;
         y1 = y2;
         y2 = temp;
      }
      if (filled)
         g.fillRect(x1,y1,x2-x1,y2-y1);
      else
         g.drawRect(x1,y1,x2-x1,y2-y1);
   }
   
   // Draws a filled or unfilled oval in the rectangle with corners at the points (x1,y1) and (x2,y2) to draw a filled or unfilled oval.
   
   private void putOval(Graphics g, boolean filled, int x1, int y1, int x2, int y2) {
      if (x1 == x2 || y1 == y2)
         return;
      if (x2 < x1) {  // Swap x1,x2 if necessary to make x2 > x1.
         int temp = x1;
         x1 = x2;
         x2 = temp;
      }
      if (y2 < y1) {  // Swap y1,y2 if necessary to make y2 > y1.
         int temp = y1;
         y1 = y2;
         y2 = temp;
      }
      if (filled)
         g.fillOval(x1,y1,x2-x1,y2-y1);
      else
         g.drawOval(x1,y1,x2-x1,y2-y1);
   }

   private void repaintRect(int x1, int y1, int x2, int y2) {
      if (x2 < x1) {  // Swap x1,x2 if necessary to make x2 >= x1.
         int temp = x1;
         x1 = x2;
         x2 = temp;
      }
      if (y2 < y1) {  // Swap y1,y2 if necessary to make y2 >= y1.
         int temp = y1;
         y1 = y2;
         y2 = temp;
      }
      x1--;
      x2++;
      y1--;
      y2++;
      repaint(x1,y1,x2-x1,y2-y1);
   }
   
   
   // Creating a menu bar for use with this panel

   public JMenuBar getMenuBar() {
      JMenuBar menubar = new JMenuBar();
      JMenu colorMenu = new JMenu("Color");
      JMenu toolMenu = new JMenu("Tool");
      menubar.add(colorMenu);
      menubar.add(toolMenu);
      ActionListener listener = new MenuHandler();
      JMenuItem item;
      item = new JMenuItem("Draw With Black");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With White");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Red");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Green");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Blue");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Draw With Yellow");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Select Drawing Color...");
      item.addActionListener(listener);
      colorMenu.add(item);
      colorMenu.addSeparator();
      item = new JMenuItem("Fill With Color...");
      item.addActionListener(listener);
      colorMenu.add(item);
      item = new JMenuItem("Curve");
      item.addActionListener(listener);
      toolMenu.add(item);
      toolMenu.addSeparator();
      item = new JMenuItem("Line");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Rectangle");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Oval");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Filled Rectangle");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Filled Oval");
      item.addActionListener(listener);
      toolMenu.add(item);
      toolMenu.addSeparator();
      item = new JMenuItem("Smudge");
      item.addActionListener(listener);
      toolMenu.add(item);
      item = new JMenuItem("Erase");
      item.addActionListener(listener);
      toolMenu.add(item);
      return menubar;
   }
   
   
   private class MenuHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         String command = evt.getActionCommand();
         if (command.equals("Select Drawing Color...")) {
            Color newColor = JColorChooser.showDialog(CANVAS.this, 
                  "Select Drawing Color", currentColor);
            if (newColor != null)
               currentColor = newColor;
         }
         else if (command.equals("Fill With Color...")) {
            Color newColor = JColorChooser.showDialog(CANVAS.this, 
                  "Select Fill Color", fillColor);
            if (newColor != null) {
               fillColor = newColor;
               Graphics osg = OSC.getGraphics();
               osg.setColor(fillColor);
               osg.fillRect(0,0,OSC.getWidth(),OSC.getHeight());
               osg.dispose();
               CANVAS.this.repaint();
            }
         }
         else if (command.equals("Draw With Black"))
            currentColor = Color.BLACK;
         else if (command.equals("Draw With White"))
            currentColor = Color.WHITE;
         else if (command.equals("Draw With Red"))
            currentColor = Color.RED;
         else if (command.equals("Draw With Green"))
            currentColor = Color.GREEN;
         else if (command.equals("Draw With Blue"))
            currentColor = Color.BLUE;
         else if (command.equals("Draw With Yellow"))
            currentColor = Color.YELLOW;
         else if (command.equals("Curve"))
            currentTool = Tool.CURVE;
         else if (command.equals("Line"))
            currentTool = Tool.LINE;
         else if (command.equals("Rectangle"))
            currentTool = Tool.RECT;
         else if (command.equals("Oval"))
            currentTool = Tool.OVAL;
         else if (command.equals("Filled Rectangle"))
            currentTool = Tool.FILLED_RECT;
         else if (command.equals("Filled Oval"))
            currentTool = Tool.FILLED_OVAL;
         else if (command.equals("Smudge"))
            currentTool = Tool.SMUDGE;
         else if (command.equals("Erase"))
            currentTool = Tool.ERASE;
      }
   } // end nested class MenuHandler
    
   // This nested class defines the object that listens for mouse and mouse motion events on the panel.  
   private class MouseHandler implements MouseListener, MouseMotionListener {
      
      int prevX, prevY;  // Previous position of mouse during a drag.     
      double[][] smudgeRed, smudgeGreen, smudgeBlue;  // data for smudge tool

      void applyToolAlongLine(int x1, int y1, int x2, int y2) {
         Graphics g = OSC.getGraphics();
         g.setColor(fillColor);    // (for ERASE only)
         int w = OSC.getWidth();   // (for SMUDGE only)
         int h = OSC.getHeight();  // (for SMUDGE only)
         int dist = Math.max(Math.abs(x2-x1),Math.abs(y2-y1));
             
         double dx = (double)(x2-x1)/dist;
         double dy = (double)(y2-y1)/dist;
         for (int d = 1; d <= dist; d++) {
                // Apply the tool at one of the points (x,y) along the line from (x1,y1) to (x2,y2).
            int x = (int)Math.round(x1 + dx*d);
            int y = (int)Math.round(y1 + dy*d);
            if (currentTool == Tool.ERASE) {
                   // Erase a 10-by-10 block of pixels around (x,y)
               g.fillRect(x-5,y-5,10,10);
               repaint(x-5,y-5,10,10);
            }
            else { 
                  
               for (int i = 0; i < 7; i++)
                  for (int j = 0; j < 7; j++) {
                     int r = y + j - 3;
                     int c = x + i - 3;
                     if (!(r < 0 || r >= h || c < 0 || c >= w || smudgeRed[i][j] == -1)) {
                        int curCol = OSC.getRGB(c,r);
                        int curRed = (curCol >> 16) & 0xFF;
                        int curGreen = (curCol >> 8) & 0xFF;
                        int curBlue = curCol & 0xFF;
                        int newRed = (int)(curRed*0.7 + smudgeRed[i][j]*0.3);
                        int newGreen = (int)(curGreen*0.7 + smudgeGreen[i][j]*0.3);
                        int newBlue = (int)(curBlue*0.7 + smudgeBlue[i][j]*0.3);
                        int newCol = newRed << 16 | newGreen << 8 | newBlue;
                        OSC.setRGB(c,r,newCol);
                        smudgeRed[i][j] = curRed*0.3 + smudgeRed[i][j]*0.7;
                        smudgeGreen[i][j] = curGreen*0.3 + smudgeGreen[i][j]*0.7;
                        smudgeBlue[i][j] = curBlue*0.3 + smudgeBlue[i][j]*0.7;
                     }
                  }
               repaint(x-3,y-3,7,7);
            }
         }
         g.dispose();
      }
      //Start a drag operation.
    
      public void mousePressed(MouseEvent evt) {
         startX = prevX = currentX = evt.getX();
         startY = prevY = currentY = evt.getY();
         dragging = true;
         if (currentTool == Tool.ERASE) {
             
            Graphics g = OSC.getGraphics();
            g.setColor(fillColor);
            g.fillRect(startX-5,startY-5,10,10);
            g.dispose();
            repaint(startX-5,startY-5,10,10);
         }
         else if (currentTool == Tool.SMUDGE) {
            if (smudgeRed == null) {
               smudgeRed = new double[7][7];
               smudgeGreen = new double[7][7];
               smudgeBlue = new double[7][7];
            }
            int w = OSC.getWidth();
            int h = OSC.getHeight();
            int x = evt.getX();
            int y = evt.getY();
            for (int i = 0; i < 7; i++)
               for (int j = 0; j < 7; j++) {
                  int r = y + j - 3;
                  int c = x + i - 3;
                  if (r < 0 || r >= h || c < 0 || c >= w) {
                     smudgeRed[i][j] = -1;
                  }
                  else {
                     int color = OSC.getRGB(c,r);
                     smudgeRed[i][j] = (color >> 16) & 0xFF;
                     smudgeGreen[i][j] = (color >> 8) & 0xFF;
                     smudgeBlue[i][j] = color & 0xFF;
                  }
               }
         }
      }     
      public void mouseDragged(MouseEvent evt) {
         currentX = evt.getX();
         currentY = evt.getY();
         if (currentTool == Tool.CURVE) {
            Graphics g = OSC.getGraphics();
            g.setColor(currentColor);
            g.drawLine(prevX,prevY,currentX,currentY);
            g.dispose();
            repaintRect(prevX,prevY,currentX,currentY);
         }
         else if (SHAPE_TOOLS.contains(currentTool)) {
            repaintRect(startX,startY,prevX,prevY);
            repaintRect(startX,startY,currentX,currentY);
         }
         else {
               // Tool has to be ERASE or SMUDGE
            applyToolAlongLine(prevX,prevY,currentX,currentY);
         }
         prevX = currentX;
         prevY = currentY;
      }
  
      public void mouseReleased(MouseEvent evt) {
         dragging = false;
         if (SHAPE_TOOLS.contains(currentTool)) {
            Graphics g = OSC.getGraphics();
            g.setColor(currentColor);
            putCurrentShape(g);
            g.dispose();
            repaint();
         }
      }
      
      public void mouseMoved(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { }
      
   } // end nested class MenuHandler
} // end class CANVAS
