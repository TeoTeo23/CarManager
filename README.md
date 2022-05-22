# CAR MANAGER
## Access a car's revision dates and plate stored in a MySQL database. Data is available in XML format.
***
## Important Informations
The API offers various calls, however keep in mind that the program is still under heavy development; further informations can be found below in the document.
***
## Database
To use both Java and MySQL, JDBC drivers have been used. 

To manage each revision, a Database has been created; below the the creation of said Database with relative Table:

    CREATE DATABASE CentroRevisioni;
    USE CentroRevisioni;

    CREATE TABLE revisioni(
    targa VARCHAR(16) NOT NULL,
    anno_immatricolazione INTEGER NOT NULL,
    data_revisione DATE NOT NULL,
    mese_scadenza INTEGER NOT NULL,
    anno_scadenza INTEGER,
    esito CHAR(1) NOT NULL,
    PRIMARY KEY(targa, data_revisione)
    );

***
## Seek cars with an expiring revision
The API call (executed in GET) returns an XML file which stores every car with an expiring revision. 

Every date between a certain span time will be included in the XML file.

>http://localhost:8080/CarManager/Servlet/Expiring?start={month/year}&end={month/year}
### Paramters
Format of both parameters is (month/year) e.g. 05/2022.
1. `start`
    * The initial value of the intended range of dates.
2. `end`
    * The end value of the intended range of dates.
### XML and Errors
Example of API repsonse in case of success:

    <revisions>
    <car>
        <plate>X5D3SL</plate>
    </car>
    <car>
        <plate>XV452BP</plate>
    </car>
    </revisions>
Example of API response in case of failure:

`400 - BAD REQUEST` if either the initial or final span is missing; wheter the parameter is malformed or completely missing, a `400` code will be returned.

`500 - INTERNAL SERVER ERROR` if the connection to the database fails for whichever reason.
***
## Seek revision dates of a specific car
The API call (executed in GET) returns an XML file which stores the given car's revision dates.
>http://localhost:8080/CarManager/Servlet/info?plate={plate}
### Parameters
1. `plate`
    * The plate of the given car.
### XML
Example of API response in case of success:

    <revision>
    <revisionDate>2032-08-10</revisionDate>
    <expireMonth>5</expireMonth>
    <expireYear>2034</expireYear>
    <esit>N</esit>
    </revision>
Example of API response in case of failure:

`400 - BAD REQUEST` if the plate is missing; wheter malformed or completely missing, a `400` code will be returned.

`500 - INTERNAL SERVER ERROR` if the connection to the database fails for whichever reason.
***
## Seek every information of a given car
The API call (executed in GET) returns an XML file which stores every available data of a given car.
>http://localhost:8080/CarManager/Servlet/revisions?plate={plate}
### Parameters
1. `plate`
    * The plate of the given car.
### XML
Example of API response in case of success:

    <revision>
    <car>
    <plate>X5D3SL</plate>
    <matricolation>2022</matricolation>
    <revDate>2032-08-10</revDate>
    <expireMonth>5</expireMonth>
    <expireYear>2034</expireYear>
    <esit>N</esit>
    </car>
    </revision>
Example of API response in case of failure:

`400 - BAD REQUEST` if the plate is missing; wheter malformed or completely missing, a `400` code will be returned.

`500 - INTERNAL SERVER ERROR` if the connection to the database fails for whichever reason.
***
### Register a new renovation for a given car
The API call (executed in POST) registers a new car in the "CentroRevisioni" database. Specifically inside the only available table: "revisioni".
>http://localhost:8080/CarManager/Servlet/register
### Parameters
The request has no visible parameters, however, an XML file containing every data should be sent. Inside the table, the following data is stored:
1. `targa` of type VARCHAR(16)
    * The plate of the given car.
2. `anno_immatricolazione` of type INTEGER
    * The year of matricolation of the car.
3. `data_revisione` of type DATE
    * The year of the first revision performed on the car.
4. `mese_scadenza` of type INTEGER
    * The expiring month of the current revision.
5. `anno_scadenza` of type INTEGER
    * The expiring year of the current revision.
6. `esito` of type CHAR(1)
    * The final esit of the revision: can be either 'S' for "success" or 'N' for failure.

For context, the table's primary key is the `(targa, data_revisione)` pair.
***
## CRUD Operations
It is possible to perform CRUD operations on the database, however those are still under development.

The only existing function is "DELETE", which is still commented in the source:

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        if(connection == null){
            response.sendError(500, "Unable to connect to specified database.\r\n");
            return;
        }
        
        String[] sel = request.getRequestURL().toString().split("/");
        String end = sel[sel.length - 1];
        
        if(end.equalsIgnoreCase("Delete")){
            String plate = request.getParameter("plate");
            if(plate == null || plate.isBlank()){
                response.sendError(400, "Missing parameter.\r\n");
                return;
            }
            try{
                String query = "DELETE FROM revisioni WHERE targa = '" + plate + "';";
                PreparedStatement ps = connection.prepareStatement(query);
                if(ps.executeUpdate() > 0)
                    response.setStatus(200);
                else
                    response.sendError(400, "Missing plate.\r\n");
            ps.close();
            }catch(SQLException exception){ response.sendError(500, "Unable to execute 'DELETE' operation.\r\n"); }
        }
    }
***
Further methods, operations and bug-fixes will be included over time.
