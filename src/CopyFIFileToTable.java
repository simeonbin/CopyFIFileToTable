import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.util.*;
//import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.*;
import org.apache.commons.math3.stat.descriptive.moment.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Simeon
 */

public class CopyFIFileToTable extends JFrame {
  // Text file info
  private final JTextField jtfFilename = new JTextField();
  private final JTextArea jtaFile = new JTextArea();

  // JDBC and table info
//  private final JComboBox jcboDriver = new JComboBox(new String[] {
//    "sun.jdbc.odbc.JdbcOdbcDriver", "com.mysql.jdbc.Driver", 
//    "oracle.jdbc.driver.OracleDriver"});
  private final JTextField jtfFI1Mean = new JTextField();
   private final JTextField jtfCalcFI2MeanNov2014 = new JTextField();
    
//  private final JComboBox jcboURL = new JComboBox(new String[] {
//    "jdbc:odbc:FinancialInstrument",
//    "jdbc:mysql://localhost/test", 
//    "jdbc:oracle:thin:@liang.armstrong.edu:1521:orcl"});
  
  private final JComboBox jcboFI3Statistics = new JComboBox(new String[] {
    "Median","Mean", "Max", "Min", "N", "Standard Deviation", "Sum", "Variance"} );

 // private final JTextField jtfResultFI3Statistics = new JTextField();
 // private final JTextField jtfTableName = new JTextField();
   private final JTextArea jtaMultiplierTable = new JTextArea();
  private final JTextField jtfResultFI3Statistics = new JTextField();

  private final JButton jbtViewFile = new JButton("View File as CSV");
  private final JButton jbtCopy = new JButton("Copy Financial Instrument File to Derby Table");
  private final JButton jbtCalcFI1Mean = new JButton("Calculate FI1 Mean");
  private final JButton jbtCalcFI2MeanNov2014 = new JButton("Calc FI2 Mean Nov 2014");
  private final JButton jbtCalcFI3Statistics = new JButton("Calc FI3 Statistics");
  private final JButton jbtViewFileWithMultiplier = new JButton("View File with Multiplier applied");
  private JLabel jlblStatus = new JLabel();

  
  public CopyFIFileToTable() {
      
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add(new JLabel("Filename"), BorderLayout.WEST);
    jPanel1.add(jbtViewFile, BorderLayout.EAST);
    jtfFilename.setPreferredSize(new Dimension(100, 16));
    jPanel1.add(jtfFilename, BorderLayout.CENTER);
    jtfFilename.setText("example_input.txt");

    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new BorderLayout());
    jPanel2.setBorder(new TitledBorder("Source Input Financial Instrument CSV File"));
    jPanel2.add(jPanel1, BorderLayout.NORTH);
    jPanel2.add(new JScrollPane(jtaFile), BorderLayout.CENTER);

    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new GridLayout(5, 0));
 //   jPanel3.add(new JLabel("JDBC Driver"));
    jPanel3.add (jbtCalcFI1Mean);
 //   jPanel3.add(new JLabel("Database URL"));
    jPanel3.add (jbtCalcFI2MeanNov2014);
  //  jPanel3.add(new JLabel("Username"));
      jPanel3.add(jbtCalcFI3Statistics);
    jPanel3.add(new JLabel("Results of FI3 Statistics"));
    jPanel3.add(jbtViewFileWithMultiplier);

    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new GridLayout(5, 0));
    
    jtfFI1Mean.setEditable(true);
    jPanel4.add(jtfFI1Mean);
    
    jtfCalcFI2MeanNov2014.setEditable(true);
    jPanel4.add(jtfCalcFI2MeanNov2014);
    jPanel4.add(jcboFI3Statistics);
 //   jPanel4.add(jtfCalcFI3Statistics);
    jPanel4.add(jtfResultFI3Statistics);
    jPanel4.add(jtaMultiplierTable);

    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new BorderLayout());
    jPanel5.setBorder(new TitledBorder("Target Database Table"));
    jPanel5.add(jbtCopy, BorderLayout.SOUTH);
    jPanel5.add(jPanel3, BorderLayout.WEST);
    jPanel5.add(jPanel4, BorderLayout.CENTER);

    add(jlblStatus, BorderLayout.SOUTH);
    add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
      jPanel2, jPanel5), BorderLayout.CENTER);
   
    jbtViewFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        showFile();
      }
    });

    jbtCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
          copyFile();
        }
        catch (Exception ex) {
          jlblStatus.setText(ex.toString());
        }
      }
    });
    
    jbtCalcFI1Mean.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
          CalculateFI1Mean();
        }
        catch (Exception ex) {
          jlblStatus.setText(ex.toString());
        }
      }
    });
    
    jbtCalcFI2MeanNov2014.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
          CalculateFI2MeanNov2014();
        }
        catch (Exception ex) {
          jlblStatus.setText(ex.toString());
        }
      }
    });
    
     jbtCalcFI3Statistics.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
            int indexFI3Statistics = ( jcboFI3Statistics.getSelectedIndex() );
            CalculateFI3Statistics(indexFI3Statistics);
        }
        catch (Exception ex) {
          jlblStatus.setText(ex.toString());
        }
      }
    });
     
     jbtViewFileWithMultiplier.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
         //   int indexFI3Statistics = ( jcboFI3Statistics.getSelectedIndex() );
            showFileWithMultiplier();
        }
        catch (Exception ex) {
          jlblStatus.setText(ex.toString());
        }
      }
    });
     
  }

  /** Display the file in the text area */
  private void showFile() {
    Scanner input = null;
    jtaFile.setText("");
    
    try {
      // Use a Scanner to read text from the file
  //    input = new Scanner(new File(jtfFilename.getText().trim()));
            input = new Scanner(new File("example_input.txt"));

      // Read a line and append the line to the text area
      while (input.hasNext()) {
          String str = input.nextLine();
           String[] tokens = str.split(",");

       String strAppend = "'" + tokens[0] + "'" + ",";
           jtaFile.append(strAppend);
           
           strAppend = "'" + tokens[1] + "'" + ",";
           jtaFile.append(strAppend);
           
           Double dblAppend = Double.valueOf(tokens[2]);
           jtaFile.append(String.valueOf(dblAppend) + '\n');
        }
      }
    catch (FileNotFoundException ex) {
      System.out.println("File not found: " + jtfFilename.getText());
    }
    
    catch (Exception ex) {
      ex.printStackTrace();
    }
    
    finally {
      if (input != null) input.close();
    }
  }
  
    private void showFileWithMultiplier() throws SQLException {
    Scanner input = null;
    ArrayList<Double> multiplierModifier= new ArrayList();
    ArrayList<String> instrumentName = new ArrayList();
    Connection conn = null;
    
    jtaFile.setText("");
        
    try {
        conn = DriverManager.getConnection("jdbc:derby://localhost:1527/FI;create=true;");
         conn.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    System.out.println("Database connected");
    
    // Get a new statement for the current connection
    Statement statement = conn.createStatement(
      ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    
    String sqlSelectMultiplier= "select NAME, MULTIPLIER from APP.INSTRUMENT_PRICE_MULTIPLIER";
      // Get ResultSet
    ResultSet resultSet = statement.executeQuery(sqlSelectMultiplier);
    
    ResultSetMetaData rsMetaData = resultSet.getMetaData();
    resultSet.beforeFirst();
    int i; int j=0;
    
    while (resultSet.next()) {
        
      for (i = 1; i <= rsMetaData.getColumnCount(); i++) {
        System.out.printf("%-12s\t", resultSet.getObject(i));
      System.out.println();
    
      if (i==1) { 
          jtaMultiplierTable.append(resultSet.getString(i) + " ");          
          instrumentName.add(resultSet.getString(i));
      }
      else {
          jtaMultiplierTable.append(String.valueOf(resultSet.getDouble(i) ) );  
          multiplierModifier.add(resultSet.getDouble(i));
      }
    }
      jtaMultiplierTable.append("\n");
   }
     
    try {
      // Use a Scanner to read text from the file
  //    input = new Scanner(new File(jtfFilename.getText().trim()));
            input = new Scanner(new File("example_input.txt"));

      // Read a line and append the line to the text area
      while (input.hasNext()) {
          String str = input.nextLine();
           String[] tokens = str.split(",");

       String strAppend = "'" + tokens[0] + "'" + ",";
           jtaFile.append(strAppend);
           
          strAppend = "'" + tokens[1] + "'" + ",";
           jtaFile.append(strAppend);
           
           Double dblAppend = Double.valueOf(tokens[2]);
           int iInstrument = linearSearch(instrumentName, tokens[0]);
           if (iInstrument != -1) dblAppend *= multiplierModifier.get(iInstrument);
                
           jtaFile.append(String.valueOf(dblAppend) + '\n');
        }
      }
    catch (FileNotFoundException ex) {
      System.out.println("File not found: " + jtfFilename.getText());
    }
    
   catch (Exception ex) {
      ex.printStackTrace();
    }
    
    finally {
      if (input != null) input.close();
      conn.close();
    }
  }
  
  
 private int linearSearch(ArrayList<String> list, String key){
              
        for(int i=0;i<list.size();i++){
            if  (list.get(i).equalsIgnoreCase(key)) {
                return i;
            }
        }
        return -1;
    }

  private void copyFile() throws Exception {
      Connection conn= null;
      Statement stmt = null;
      
    // Load the JDBC driver
          try {
                Class.forName("org.apache.derby.jdbc.ClientDriver");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Derby driver not found.");
        }
        System.out.println("Driver loaded");
        try {
             conn = DriverManager.getConnection("jdbc:derby://localhost:1527/FI;create=true;");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
//    Connection conn = DriverManager.getConnection("jdbc:ucanaccess://c:/data/FinancialInstrument.accdb;memory=false");

//=========
   try {  
       
    System.out.println("Database connected");

//    System.out.println("Creating database schema...");
//      stmt = conn.createStatement();
//      
//      String sql = "CREATE DATABASE FI2";
//      stmt.executeUpdate(sql);
//      System.out.println("Database created successfully...");
      
      //=========
   String createString =
        "create table " + "FI2" +
        ".INSTRUMENT_PRICE_MULTIPLIER " +
        "( ID integer NOT NULL, " +
        "NAME varchar(30) NOT NULL, " +
        "MULTIPLIER double NOT NULL, " +
        "PRIMARY KEY (ID) )";

    stmt = null;
    try {
        stmt = conn.createStatement();
        stmt.executeUpdate(createString);
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (stmt != null) { stmt.close(); }
    }

// The following method, CoffeesTable.createTable, creates the COFFEES table:
     createString =
        "create table " + "FI2" +
        ".INSTRUMENT_TABLE " +
        "( ID INT NOT NULL, " +
        "INSTRUMENT_NAME varchar(30) NOT NULL, " +
        "DATE varchar(25) NOT NULL, " +
        "VALUE double NOT NULL, " +
        "PRIMARY KEY (ID) ) ";
                                
    stmt = null;
    try {
        stmt = conn.createStatement();
        stmt.executeUpdate(createString);
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        if (stmt != null) { stmt.close(); }
    }
  
   //=========
      
 } catch(SQLException se) {
      //Handle errors for JDBC
      se.printStackTrace();
   } finally  {
      //finally block used to close resources
      try {
         if(stmt!=null)
            stmt.close();
      } catch(SQLException se2) {
            se2.printStackTrace();
      }
   }
   
 // Read each line from the text file and insert it to the table
    insertRows(conn);
    
    conn.close();
  }

  private void insertRows(Connection connection) { 
      String strAppend=null;
      int id=0;
    // Build the SQL INSERT statement
//    String sqlInsert = "insert into " + jtfTableName.getText()
//      + " values (";
    String sqlInsert = "insert into " + "APP.INSTRUMENT_TABLE" + " values (";
    // Use a Scanner to read text from the file
    Scanner input = null;

    // Get file name from the text field
 //   String filename = jtfFilename.getText().trim();
  String   filename = "example_input.txt";
    try {
      // Create a scanner
      input = new Scanner(new File(filename));
      // Create a statement
      Statement statement = connection.createStatement();
      System.out.println("Driver major version? " +
        connection.getMetaData().getDriverMajorVersion());

      // Determine if batchUpdatesSupported is supported
      boolean batchUpdatesSupported = false;
      try {
        if (connection.getMetaData().supportsBatchUpdates()) {
          batchUpdatesSupported = true;
          System.out.println("Batch updates supported");
          jlblStatus.setText("Batch updates supported");
        }
        else {
          System.out.println("The driver is of JDBC 2 type, but " +
            "does not support batch updates");
        }
      }
      catch (UnsupportedOperationException ex) {
        System.out.println("The driver does not support JDBC 2");
      }
         //    batchUpdatesSupported = false;
      // Determine if the driver is capable of batch updates
      if (batchUpdatesSupported) {
        // Read a line and add the insert table command to the batch
        while (input.hasNext()) { 
//       statement.addBatch(sqlInsert + input.nextLine() + ")");
            id++;
            String str = input.nextLine();
            String[] tokens = str.split(",");
            
           String strAppend1 = "'" + tokens[0] + "'" + ",";
           String strAppend2 = "'" + tokens[1] + "'" + ",";
           Double dblAppend3 = Double.valueOf(tokens[2]);
           strAppend = (strAppend1 + strAppend2 + dblAppend3);
           
           statement.addBatch (sqlInsert + id + "," + strAppend + ")" );
        }
        statement.executeBatch();
        jlblStatus.setText("Batch updates completed");
        System.out.println("Batch updates completed");
      }
      else {
          
        // Read a line and execute insert table command
        while (input.hasNext()) {
            id++;
            
            String str = input.nextLine();
            String[] tokens = str.split(",");
            
           String strAppend1 = "'" + tokens[0] + "'" + ",";
           String strAppend2 = "'" + tokens[1] + "'" + ",";
           Double dblAppend3 = Double.valueOf(tokens[2]);
           strAppend = (strAppend1 + strAppend2 + dblAppend3);
           int executeUpdate = statement.executeUpdate(sqlInsert + id + "," + strAppend + ")");
        }
        jlblStatus.setText("Single row update completed");
      }
    }
    catch (SQLException ex) {
      System.out.println(ex);
    }
    catch (FileNotFoundException ex) {
      System.out.println("File not found: " + filename);
    }
//    catch (IOException ex) {
//      ex.printStackTrace();
//    }
    finally {
      if (input != null) input.close();
     
    }
  }
  
  private void CalculateFI1Mean() throws SQLException {
    Connection conn = null;
    try {
        conn = DriverManager.getConnection("jdbc:derby://localhost:1527/FI;create=true;");
         conn.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    System.out.println("Database connected");
    
    // Get a new statement for the current connection
    Statement statement = conn.createStatement(
      ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    
    String sqlSelectCalcFI1Mean= "select AVG(INSTRUMENT_TABLE.VALUE) FROM INSTRUMENT_TABLE WHERE (INSTRUMENT_NAME = 'INSTRUMENT1')";
      // Get ResultSet
    ResultSet resultSet = statement.executeQuery(sqlSelectCalcFI1Mean);
    
    ResultSetMetaData rsMetaData = resultSet.getMetaData();
    resultSet.beforeFirst();
    int i;
    while (resultSet.next()) {
      for (i = 1; i <= rsMetaData.getColumnCount(); i++) {
        System.out.printf("%-12s\t", resultSet.getObject(i));
      System.out.println();
      jtfFI1Mean.setText(resultSet.getObject(i).toString());
    }
   }   
    conn.close();
  }
  
  private void CalculateFI2MeanNov2014() throws SQLException {
    Connection conn = null;
    try {
        conn = DriverManager.getConnection("jdbc:derby://localhost:1527/FI;create=true;");
         conn.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    System.out.println("Database connected");
    
//    String sqlSelectCalcFI2Mean= "select AVG(value) AS Average\n" + "from instrument_table\n" +
//                                  "where (SUBSTR(instrument_table.date,4) LIKE 'Nov%') \n" +
//                                   "AND (SUBSTR(instrument_table.date,8) LIKE '2014')\n" +
//                                   "AND (INSTRUMENT_NAME = 'INSTRUMENT2')";
    
    String sqlSelectCalcFI2Mean= "select AVG(value) AS Average\n" + "from instrument_table\n" +
                                  "where (SUBSTR(instrument_table.date,4) LIKE ? ) \n" +
                                   "AND (SUBSTR(instrument_table.date,8) LIKE ? )\n" +
                                   "AND (INSTRUMENT_NAME = 'INSTRUMENT2')";
            
    // Get a new statement for the current connection
  // Statement statement = conn.createStatement(
   //   ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
////    
      // Get ResultSet
    // ResultSet resultSet = statement.executeQuery(sqlSelectCalcFI2Mean);
    
    PreparedStatement statement = conn.prepareStatement(sqlSelectCalcFI2Mean, 
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    
    statement.setString(1, "Nov%");
    statement.setString(2, "2014");
    ResultSet resultSet = statement.executeQuery();
   
    ResultSetMetaData rsMetaData = resultSet.getMetaData();
    resultSet.beforeFirst();
    int i;
    while (resultSet.next()) {
      for (i = 1; i <= rsMetaData.getColumnCount(); i++) {
        System.out.printf("%-12s\t", resultSet.getObject(i));
      System.out.println();
      jtfCalcFI2MeanNov2014.setText(resultSet.getObject(i).toString());
    }
   }
    
    conn.close();
  }
  
  private void CalculateFI3Statistics(int indexStatistics) throws Exception {
      // double[] sample = new double[10000];
      ArrayList<Double> sample = new ArrayList();
    Connection conn = null;
    double vStat=0;
    
    DescriptiveStatistics stats = new DescriptiveStatistics();
    try {
        conn = DriverManager.getConnection("jdbc:derby://localhost:1527/FI;create=true;");
         conn.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    System.out.println("Database connected");

    String sqlSelectCalcFI3Statistics= "select value from APP.INSTRUMENT_TABLE\n" +
                                        "WHERE (INSTRUMENT_NAME = 'INSTRUMENT3')";
            
    // Get a new statement for the current connection
   Statement statement = conn.createStatement(
      ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
  
 //      Get ResultSet
     ResultSet resultSet = statement.executeQuery(sqlSelectCalcFI3Statistics);
    
//    PreparedStatement statement = conn.prepareStatement(sqlSelectCalcFI3Statistics, 
//            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
   
//    statement.setString(1, "INSTRUMENT3");
//    ResultSet resultSet = statement.executeQuery();
   
    ResultSetMetaData rsMetaData = resultSet.getMetaData();
    resultSet.beforeFirst();
    int i; int j=0;
    while (resultSet.next()) {
        
      for (i = 1; i <= rsMetaData.getColumnCount(); i++) {
        System.out.printf("%-12s\t", resultSet.getDouble(i));
      System.out.println();
      sample.add ((double)resultSet.getDouble(i));
    }
   } 
    
     // Add the data from the series to stats
    for (i = 0; i < sample.size(); i++) {
        stats.addValue(sample.get(i));
    }
    
    switch (indexStatistics) {
        case 0: double   median = CalculateMedian (sample); 
                vStat = median; break;
            
        case 1: double mean = stats.getMean();
                vStat = mean; break;
       
        case 2: double max = stats.getMax();
                vStat = max; break;
                       
        case 3: double min = stats.getMin();
                vStat = min; break;
       
        case 4: double N = stats.getN();
                vStat = N; break;

        case 5: double standardDeviation = stats.getStandardDeviation();
                vStat = standardDeviation; break;
       
        case 6: double Sum = stats.getSum();
                  vStat = Sum; break;
            
        case 7: double Variance = stats.getVariance();
                vStat = Variance; break;
            
        default: median = CalculateMedian(sample);vStat = median; break;
    }
   
     System.out.println("Value of FI3 Statistic = " + vStat);
     jtfResultFI3Statistics.setText(String.valueOf(vStat) );
     jlblStatus.setText(String.valueOf(vStat));
    conn.close();
}

private double CalculateMedian(ArrayList numArray) {
double median;
    Collections.sort(numArray);

if ( ( (numArray.size()) % 2) == 0 ) 
    median = ((double) numArray.get(numArray.size()/2) + 
                (double)numArray.get(numArray.size()/2) - 1)/2;
else
    median = (double) numArray.get(numArray.size()/2);

return median;
}

 public static void main(String args[]) {
    JFrame frame = new CopyFIFileToTable();
    frame.setTitle("ReadFinancialInstrumentFileToTable");
    frame.setSize(700, 200);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
