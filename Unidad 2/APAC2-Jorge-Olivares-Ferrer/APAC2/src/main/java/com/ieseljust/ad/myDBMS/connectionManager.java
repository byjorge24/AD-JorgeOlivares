package com.ieseljust.ad.myDBMS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class connectionManager{
    
    String server;
    String port;
    String user;
    String pass;
    
    connectionManager(){
        // TO-DO: Inicialització dels atributs de la classe
        //       per defecte
        this.server = "localhost";
        this.port = "3308";
        this.user = "root";
        this.pass = "root";
    }

    connectionManager(String server, String port, String user, String pass){
        // TO-DO:   Inicialització dels atributs de la classe
        //          amb els valors indicats
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public Connection connectDBMS(){
        
        Connection conn = null;
        
        try {
            // TO-DO:   Crea una connexió a la base de dades,
            //          i retorna aquesta o null, si no s'ha pogut connectar.

            // Passos:
            // 1. Carreguem el driver JDBC
            // 2. Crear la connexió a la BD
            // 3. Retornar la connexió
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://" + this.server + ":" + this.port + "/?useUnicode=true&characterEncoding=UTF-8&user=" + this.user + "&password=" + this.pass;
            conn = DriverManager.getConnection(connectionUrl);
            // Recordeu el tractament d'errors
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public void showInfo(){
        try {
            // TO-DO: Mostra la informació del servidor a partir de les metadades
            // - Nom del SGBD
            // - Driver utilitzat
            // - URL de connexió
            // - Nom de l'usuari connectat            
            
            DatabaseMetaData dbmd = connectDBMS().getMetaData();
            
            System.out.println(dbmd.getDriverName());
            System.out.println(dbmd.getDatabaseProductName());
            System.out.println(dbmd.getURL());
            System.out.println(dbmd.getUserName());
            
            }
        
        // Recordeu el tractament d'errors
        
        catch (SQLException ex) {
            Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    public void showDatabases(){
        try {
            // TO-DO: Mostrem les bases de dades del servidor, bé des del catàleg o amb una consulta
            //Creating a Statement object
            
            DatabaseMetaData dbmt = connectDBMS().getMetaData();
            
            ResultSet rs = dbmt.getCatalogs();
            while(rs.next()) {
                System.out.print(rs.getString(1));
                System.out.println();
            }
            
            // Recordeu el tractament d'errors
        } catch (SQLException ex) {
            Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
    public void help(){
        
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "----------------------------------------------------------------------------------------------\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "---------------------------------------------HELP---------------------------------------------\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "----------------------------------------------------------------------------------------------\n");
        
        System.out.println("");
        
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "show databases o sh db  " + "      Mostra una llista amb les diferents bases de dades del sistema\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "info                    " + "      Mostra informació sobre el SGBD i la connexió\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "import Nom_del_script   " + "      Permetrà executar un script sql indicant la ubicació del fitxer\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "use Nom_de_la_BD        " + "      Canvia al mode de connexió a la base de dades\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "quit                    " + "      Ix de l’aplicació\n");
        
    }

    public void importDatabase(String archivo){
        
        File script=new File(archivo);
        
        System.out.println("Executant l'script "+script.getName());
        
        BufferedReader br=null;
        
        try{
            
            br=new BufferedReader(new FileReader(script));
            
        } catch(FileNotFoundException e){
            
            System.out.println("Error: L'script no existeix.");
            
        };
        String line=null;
        
        StringBuilder sb= new StringBuilder();
        
        // Obtenim el símbol del salt de línia segons el sistema
        
        String breakLine=System.getProperty("line.separator");
        
        try{
            while ((line=br.readLine())!=null) {
                
                sb.append(line);
                sb.append(breakLine);
                
            }
        }catch (IOException e){
            
            System.out.println ("ERROR d'E/s");
            
        }
        // Convertim el stringBuilder en cadena:
        
        String query = sb.toString();
        
        System.out.println("Executant consulta: \n"+query);
        
        try{
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
        } catch (ClassNotFoundException e) {
            
            System.out.println("Error en carregar el driver");
            
        }
        
        try{
            
            Statement st=connectDBMS().createStatement();
            
            int result=st.executeUpdate(query);
            
            System.out.println("Script Executat amb éxit. Eixida: "+ result);
            
            st.close();
            
            connectDBMS().close();
            
        } catch (SQLException e){
            
            System.out.println("Error en l'script "+ e);
            
        }
        
        
    }
        
    public void startShell(){

        Scanner keyboard = new Scanner(System.in);
        String command;

        do {

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# ("+this.user+") on "+this.server+":"+this.port+"> "+ConsoleColors.RESET);
            command = keyboard.nextLine();

                        
            switch (command){
                case "sh db":
                case "show databases":
                    this.showDatabases();
                    break;
                
                case "info":
                    this.showInfo();
                    break;

                case "help":
                    this.help();
                    break;
                    
                case "quit":
                    break;
                    
                default:
                    // Com que no podem utilitzar expressions
                    // regulars en un case (per capturar un "use *")
                    // busquem aquest cas en el default:

                    String[] subcommand=command.split(" ");
                    switch (subcommand[0]){
                        case "use":
                            // TO-DO:
                                // Creem un objecte de tipus databaseManager per connectar-nos a
                                // la base de dades i iniciar una shell de manipulació de BD..
                            
                            databaseManager db;

                            Scanner scanner = new Scanner(System.in);

                            String database;

                            do {
                                
                                database = subcommand[1];
                                System.out.print(ConsoleColors.RESET);

                                db = new databaseManager(server, port, user, pass, database);


                            } while(db.connectDatabase()==null);

                            db.startShell();
                            break;
                        case "import":
                            
                            this.importDatabase(subcommand[1]);
                            break;

                        default:
                            System.out.println(ConsoleColors.RED+"Unknown option"+ConsoleColors.RESET);
                            break;


                    }
            }
        } while(!command.equals("quit"));
    }
}