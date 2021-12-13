
//Calendar.java

 import java.applet.Applet;
  import java.awt.*;
  import java.util.Date;
 
  public class Calendar extends Applet
    {

    static final int YTOP = 90;    
    static final int YHEADER = 30;   
    static final int NCELLX = 7;    
    static final int CELLSIZE = 60;  
    static final int MARGIN = 8;    
    static final int FEBRUARY = 1;  
 
    Label yearLabel = new Label("Year:");
    TextField yearTextField = new TextField("1996", 5);
    Label monthLabel = new Label("Month:");
    Choice monthChoice = new Choice();
    Button newCalButton = new Button("New Calendar");
 
    Date now = new Date();

    Font smallArialFont = new Font("Arial", Font.PLAIN, 15);
    Font largeArialFont = new Font("Arial", Font.BOLD, 30);
 
    String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday",
                     "Thursday", "Friday", "Saturday"};
 
    String months[] = {"January", "February", "March", "April",
                       "May", "June", "July", "August", "September",
                       "October", "November", "December"};
 
    int DaysInMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    int userMonth;
    int userYear;
 
    public void init()
      {
      setBackground(Color.CYAN);

      userMonth = now.getMonth();
      userYear  = now.getYear() + 1900;

      yearLabel.setFont(smallArialFont);
      add(yearLabel);

      yearTextField.setFont(smallArialFont);
      yearTextField.setText(String.valueOf(userYear));
      add(yearTextField);

      monthLabel.setFont(smallArialFont);
      add(monthLabel);

      monthChoice.setFont(smallArialFont);
      for (int i = 0; i < 12; i++)
        monthChoice.addItem(months[i]);
      monthChoice.select(userMonth);
      add(monthChoice);

      newCalButton.setFont(smallArialFont);
      add(newCalButton);
      } 
 
    public void paint(Graphics g)
      {
      FontMetrics fm;   
      int fontAscent;   
      int dayPos;       
      int xSize, ySize;  
      int numRows;        
      int xNum, yNum;     
      int numDays;      
      String dayStr;    
      int marg;          
      String caption;   

      fm = g.getFontMetrics();
      fontAscent = fm.getAscent();
      dayPos = YTOP + (YHEADER + fontAscent) / 2;

      xSize = NCELLX * CELLSIZE;

      g.drawRect(0, YTOP, xSize, YHEADER);

      for (int i = 0; i < NCELLX; i++)
        g.drawString(days[i], (CELLSIZE-fm.stringWidth(days[i]))/2 + i*CELLSIZE,
                     dayPos);

      numRows = NumberRowsNeeded(userYear, userMonth);

      ySize =  numRows * CELLSIZE;
      for (int i = 0; i <= xSize; i += CELLSIZE)
        g.drawLine(i, YTOP + YHEADER, i, YTOP + YHEADER + ySize);

      for (int i = 0, j = YTOP + YHEADER; i <= numRows; i++, j += CELLSIZE)
        g.drawLine(0, j, xSize, j);

      xNum = (CalcFirstOfMonth(userYear, userMonth) + 1) * CELLSIZE - MARGIN;
      yNum = YTOP + YHEADER + MARGIN + fontAscent;

      numDays = DaysInMonth[userMonth] +
                ((IsLeapYear(userYear) && (userMonth == FEBRUARY)) ? 1 : 0);

      for (int day = 1; day <= numDays; day++)
        {
        dayStr = String.valueOf(day);
        g.drawString(dayStr, xNum - fm.stringWidth(dayStr), yNum);
        xNum += CELLSIZE;
        if (xNum > xSize)
          {
          xNum = CELLSIZE - MARGIN;
          yNum += CELLSIZE;
          }  
        } 
 
      g.setFont(largeArialFont);
      fm = g.getFontMetrics();
      marg = 2 * fm.getDescent();
 
      caption = months[userMonth] + " " + String.valueOf(userYear);
      g.drawString(caption, (xSize-fm.stringWidth(caption))/2, YTOP - marg);
      }
 
    public boolean action(Event e, Object o)
      {
      int userYearInt;
 
      if (e.target instanceof Button)
        {
        if ("New Calendar".equals((String)o))
          {
          userMonth = monthChoice.getSelectedIndex();
 
          userYearInt = Integer.parseInt(yearTextField.getText(), 10);
          if (userYearInt > 1581)
            userYear = userYearInt;
 
          repaint();
          return true;
          } 
        } 
 
      return false;
      } 
 
    int NumberRowsNeeded(int year, int month)
      {
      int firstDay;     
      int numCells;     
 
      if (year < 1582) return (-1);
 
      if ((month < 0) || (month > 11)) return (-1);
 
      firstDay = CalcFirstOfMonth(year, month);
 
      if ((month == FEBRUARY) && (firstDay == 0) && !IsLeapYear(year))
        return (4);
 
      numCells = firstDay + DaysInMonth[month];
 
      if ((month == FEBRUARY) && (IsLeapYear(year))) numCells++;
 
      return ((numCells <= 35) ? 5 : 6);
      } 
 
    int CalcFirstOfMonth(int year, int month)
      {
      int firstDay;    
      int i; 
        
      if (year < 1582) return (-1);
 
      if ((month < 0) || (month > 11)) return (-1);
 
      firstDay = CalcJanuaryFirst(year);

      for (i = 0; i < month; i++)
        firstDay += DaysInMonth[i];
 
      if ((month > FEBRUARY) && IsLeapYear(year)) firstDay++;
 
      return (firstDay % 7);
      } 
 
    boolean IsLeapYear(int year)
      {
 
      if ((year % 100) == 0) return((year % 400) == 0);
 
      return ((year % 4) == 0);
      } 

    int CalcJanuaryFirst(int year)
      {
 
      if (year < 1582) return (-1);

      return ((5 + (year - 1582) + CalcLeapYears(year)) % 7);
      }
 
    int CalcLeapYears(int year)
      {

      int leapYears;    
      int hundreds; 
      int fourHundreds;   
 
      if (year < 1582) return (-1);
 
      leapYears = (year - 1581) / 4;
      hundreds = (year - 1501) / 100;
      leapYears -= hundreds;
 
      fourHundreds = (year - 1201) / 400;
      leapYears += fourHundreds;
 
      return (leapYears);
      } 
 
  }
  


//Calendar.html
/*

<html>
   <body>
      <applet code="Calendar" width="470" height="470">
      </applet>
   </body>
</html>
*/
