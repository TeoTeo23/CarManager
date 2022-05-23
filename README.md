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
## String to sql.Date converter
Because the Database contains a `Date` value, and because it made the code easier to read, a converter class has been created.

This class converts a String (either taken from the URL or Parsed from an XML file) and converts it to a sql.Date value in order to work with the Database.

Source coude is viewable down below, or in its repository folder.

    public class Converter {
    
    public Converter(){}
    
    public java.sql.Date stringToDate(String date){
        java.sql.Date sqlDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try{
            java.util.Date uDate = format.parse(date);
            sqlDate = new java.sql.Date(uDate.getTime());
        }catch(ParseException exception){}
        return sqlDate;
        }
    }

***
## Commands
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
### XML and errors
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
**It is possible to perform CRUD operations on the database, however those are still under development.**
## Insert (or Create)
The API call (executed in POST) inserts a new revision inside the database.

>http://localhost:8080/CarManager/Servlet/Insert

No visible parameter is needed, however a user should send an XML body containing right and coherent data.

## XML and errors
Response in case of success provides no XML body, it does provide a:

`200` status code however.

Respose in case of failure:

`400 - BAD REQUEST` if the one or more parameter(s) are still missing after parsing the XML body sent.

`500 - INTERNAL SERVER ERROR` if the connection to the database fails for whichever reason.
***
## Retreive (or Read)
Not available yet.
***
## Update (or Edit)
Not available yet.
***
## Delete
Not available yet.
***
Further methods, operations and bug-fixes will be included over time.
