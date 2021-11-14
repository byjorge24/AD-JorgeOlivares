package com.ieseljust.ad.myDBMS;

import java.sql.*;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class databaseManager{
    
    String server;
    String port;
    String user;
    String pass;
    String dbname;

    databaseManager(){
        // TO-DO: Inicialització dels atributs de la classe
        //       per defecte
        this.server = "localhost";
        this.port = "3308";
        this.user = "root";
        this.pass = "root";
        this.dbname = "employeesMini";
    }

    databaseManager(String server, String port, String user, String pass, String dbname){
        // TO-DO:   Inicialització dels atributs de la classe
        //          amb els valors indicats
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.dbname = dbname;
    }

    public Connection connectDatabase(){
        
        Connection conn = null;
        
        try {
            // TO-DO:   Crea una connexió a la base de dades,
            //          i retorna aquesta o null, si no s'ha pogut connectar.

            // Passos:
            // 1. Carreguem el driver JDBC
            // 2. Crear la connexió a la BD
            // 3. Retornar la connexió
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.dbname + "? useUnicode=true&characterEncoding=UTF-8&user="+ this.user + "&password=" + this.pass;
            conn = DriverManager.getConnection(connectionUrl);
            // Recordeu el tractament d'errors
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(databaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public void showTables(){
        try {
            // TO-DO: Mostra un llistat amb les taules de la base de dades
            
            // Passos:
            // 1. Establir la connexió a la BD
            // 2. Obtenir les metadades
            // 3. Recórrer el resultset resultant mostrant els resultats
            // 4. Tancar la connexió
            
            DatabaseMetaData metaData = connectDatabase().getMetaData();
            String[] types = {"TABLE"};
            //Retrieving the columns in the database
            ResultSet tables = metaData.getTables(null, null, "%", types);
            while (tables.next()) {
               System.out.println(tables.getString("TABLE_NAME"));
            }
            
            connectDatabase().close();
            
            // Recordeu el tractament d'errors
        } catch (SQLException ex) {
            Logger.getLogger(databaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static String limpia(String datosArray){
        datosArray = datosArray.trim();
        if (datosArray != null && datosArray.length() > 0 && datosArray.charAt(datosArray.length() - 1) == ',') {
          datosArray = datosArray.substring(0, datosArray.length() - 1);
        }
        return datosArray;
   }

    public void insertIntoTable(String table){
        // TO-DO: Afig informació a la taula indicada

        // Passos
        // 1. Estableix la connexió amb la BD
        // 2. Obtenim les columnes que formen la taula (ens interessa el nom de la columna i el tipus de dada)
        // 3. Demanem a l'usuari el valor per a cada columna de la taula
        // 4. Construim la sentència d'inserció a partir de les dades obtingudes
        //    i els valors proporcionats per l'usuari
        
        // Caldrà tenir en compte:
        // - Els tipus de dada de cada camp
        // - Si es tracta de columnes generades automàticament per la BD (Autoincrement)
        //   i no demanar-les
        // - Gestionar els diferents errors
        // - Si la clau primària de la taula és autoincremental, que ens mostre el valor d'aquesta quan acabe.

        try {
            
            ArrayList l_camps = new ArrayList();
            ArrayList l_tipus = new ArrayList();
            ArrayList holders = new ArrayList();
            ArrayList nulls = new ArrayList();
            ArrayList autoincrementable = new ArrayList();
            
            
            DatabaseMetaData dbmd = connectDatabase().getMetaData();
            ResultSet columnes = dbmd.getColumns(this.dbname ,null , table, null);
            
            
            
            while(columnes.next())
            {
                
                l_camps.add(columnes.getString(4));
                l_tipus.add(columnes.getString(6));
                nulls.add(columnes.getString(18));
                autoincrementable.add(columnes.getString(23));
                
                
            }
            
            
            String place_holders = "";
            String campos = "";
            
            for (int i = 0; i < l_camps.size(); i++)
            {
                
                holders.add("?");
                
            }
            for (Object elemento: holders) 
            {
                place_holders += elemento + ", ";
            }
            for (Object elemento: l_camps) 
            {
                campos += elemento + ", ";
            }
            
            String sql = "INSERT INTO " + table + " (" + limpia(campos) + ")" + " VALUES " + "(" + limpia(place_holders) + ")";
            
            PreparedStatement pst = connectDatabase().prepareStatement(sql);
            
            for (int i = 1; i <= l_camps.size(); i++)
            {
                
                if (!"NULL".equals(nulls.get(i - 1)))
                {

                        if ("YES".equals(autoincrementable.get(i - 1)))
                        {

                            System.out.println("El campo " + l_camps.get(i - 1) + " es autoincrementable.");

                        }
                        else
                        {


                            if ("INT".equals(l_tipus.get(i - 1)))
                            {

                                String valor = Leer.leerTexto("Introduce un valor de tipo " + l_tipus.get(i - 1) + " para el campo " + l_camps.get(i - 1) + " :");
                                System.out.println("----------------------------------------------------------");
                                pst.setInt(i, Integer.parseInt(valor));

                            }

                            if("DATE".equals(l_tipus.get(i - 1)))
                            {

                                String fecha = Leer.leerTexto("Introduce un valor de tipo " + l_tipus.get(i - 1) + " para el campo " + l_camps.get(i - 1)+ " :");
                                System.out.println("----------------------------------------------------------");
                                pst.setDate(i, java.sql.Date.valueOf(fecha));

                            }

                            else
                            {

                                String valor2 = Leer.leerTexto("Introduce un valor de tipo " + l_tipus.get(i - 1) + " para el campo " + l_camps.get(i - 1)+ " :");
                                System.out.println("----------------------------------------------------------");
                                pst.setString(i, valor2);

                            }



                        }

                }
                else
                {
                    
                    if ("YES".equals(autoincrementable.get(i - 1)))
                            {

                                System.out.println("El campo " + l_camps.get(i - 1) + " es autoincrementable.");

                            }
                    else
                    {
                        
                        String respuesta = Leer.leerTexto("Deseas insertar algo en la columna " + l_camps.get(i - 1) + " de tipo " + l_tipus.get(i - 1) + "?" + "\nEscribe s|n: ");
                    while(!"s".equals(respuesta) || !"n".equals(respuesta))
                    {

                       respuesta = Leer.leerTexto("Deseas insertar algo en la columna " + l_camps.get(i - 1) + " de tipo " + l_tipus.get(i - 1) + "?" + "\nEscribe s|n: "); 


                    if ("s".equals(respuesta))
                        {

                            if ("YES".equals(autoincrementable.get(i - 1)))
                            {

                                System.out.println("El campo " + l_camps.get(i - 1) + " es autoincrementable.");

                            }
                            else
                            {


                                if ("INT".equals(l_tipus.get(i - 1)))
                                {

                                    String valor = Leer.leerTexto("Introduce un valor de tipo " + l_tipus.get(i - 1) + " para el campo " + l_camps.get(i - 1) + " :");
                                    System.out.println("----------------------------------------------------------");
                                    pst.setInt(i, Integer.parseInt(valor));

                                }

                                if("DATE".equals(l_tipus.get(i - 1)))
                                {

                                    String fecha = Leer.leerTexto("Introduce un valor de tipo " + l_tipus.get(i - 1) + " para el campo " + l_camps.get(i - 1)+ " :");
                                    System.out.println("----------------------------------------------------------");
                                    pst.setDate(i, java.sql.Date.valueOf(fecha));

                                }

                                else
                                {

                                    String valor2 = Leer.leerTexto("Introduce un valor de tipo " + l_tipus.get(i - 1) + " para el campo " + l_camps.get(i - 1)+ " :");
                                    System.out.println("----------");
                                    pst.setString(i, valor2);

                                }



                            }

                        }
                        else if ("n".equals(respuesta))
                        {


                            if ("INT".equals(l_tipus.get(i - 1)))
                                {

                                    pst.setInt(i, Integer.parseInt(""));

                                }

                            else if ("DATE".equals(l_tipus.get(i - 1)))
                            {

                                pst.setDate(i, java.sql.Date.valueOf(""));

                            }

                            else
                            {

                                pst.setString(i, "");

                            }

                        }

                    }
                        
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(databaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    public void showDescTable(String table){
        try {
            // TO-DO: Mostra la descripció de la taula indicada,
            //        mostrant: nom, tipus de dada i si pot tindre valor no nul
            //        Informeu també de les Claus Primàries i externes
            
            Statement statement = connectDatabase().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+ table);
            ResultSetMetaData metaData = resultSet.getMetaData();
            for(int i=1;i <= metaData.getColumnCount();i++)
            {
                System.out.println(metaData.getColumnName(i)+ "\t" +metaData.getColumnTypeName(i) + "\t" + metaData.isNullable(i));
            }
        } catch (SQLException ex) {
            Logger.getLogger(databaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void select(String sentencia){

        String[] subcommand = sentencia.split(" ");
        Connection con = connectDatabase();
        ResultSet rs = null;
        try {
            rs = con.createStatement().executeQuery(sentencia);
            System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT);
            System.out.println("");
            System.out.println("Contingut de " + subcommand[3]+" : ");
            System.out.println("******************************");

            ResultSetMetaData rsmdQuery = rs.getMetaData();
            for (int i = 1; i <= rsmdQuery.getColumnCount(); i++)
                System.out.print(String.format("%-25.25s", rsmdQuery.getColumnName(i)));

            System.out.println();
            System.out.println(ConsoleColors.RESET);

            while (rs.next()) {
                for (int i = 1; i <= rsmdQuery.getColumnCount(); i++)
                    System.out.print(String.format("%-25.25s ", rs.getString(i)));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    public void help(){
        
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "-----------------------------------------------------------------------------------------------------------------------\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "---------------------------------------------------------HELP----------------------------------------------------------\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "-----------------------------------------------------------------------------------------------------------------------\n");
        
        System.out.println("");
        
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "sh tables                   " + "       Mostra les taules de la base de dades\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "describe Nom_de_la_Taula    " + "       Mostra una descripció dels camps de la taula indicada\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "insert Nom_de_la_Taula      " + "       Pregunta a l’usuari camp per camp de la taula, i inserix un registre a la base de dades\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "select *                    " + "       Executarà una consulta select qualsevol en la BD\n");
        System.out.printf(ConsoleColors.YELLOW_BOLD_BRIGHT + "quit                        " + "       Torna al mode general\n");
            
    }
    
    public void startShell(){

        // TO-DO: Inicia la shell del mode base de dades
            Scanner keyboard = new Scanner(System.in);
        String command;

        do {

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# ("+this.user+") on "+this.server+":"+this.port+ "[" + this.dbname + "]" +"> "+ConsoleColors.RESET);
            command = keyboard.nextLine();

                        
            switch (command){
                case "sh tables":
                case "show tables":
                    this.showTables();
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
                        case "describe":
                            this.showDescTable(subcommand[1]);
                            break;
                        case "insert":
                            this.insertIntoTable(subcommand[1]);
                            break;
                        case "select":
                            this.select(command);
                            break;
                        case "quit":
                            break;
                        
                        default:
                            System.out.println(ConsoleColors.RED+"Unknown option"+ConsoleColors.RESET);
                            break;
                    }
            }
        }while(!command.equals("quit"));

        }


}