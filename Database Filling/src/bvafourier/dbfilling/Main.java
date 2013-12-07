package bvafourier.dbfilling;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.*;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * User: alexgru
 */
public class Main {
    static LinkedList<Patient> patients = new LinkedList<Patient>();
    static Connection conn;
    static LinkedList<Integer> done = new LinkedList<Integer>();
    static LinkedList<String> patterns = new LinkedList<String>();

    public static void main(String[] args) throws Exception {
        conn = DBConnector.getConnection();

//        patterns.add("Pit Pattern I");
//        patterns.add("Pit Pattern II");
//        patterns.add("Pit Pattern III L");
//        patterns.add("Pit Pattern III S");
//        patterns.add("Pit Pattern IV");
//        patterns.add("Pit Pattern V");
//        readMappingFileAndAddPatients();
//        readPatientDataAndAddToPatients();
//        insertAllPatientDataIntoDatabase();

//        trySomeArrayEntries();
    }

    private static void trySomeArrayEntries() throws SQLException {
        ResultSet resultSet = conn.createStatement().executeQuery("select * from PATIENTDATA where imagename='AKH0008-cut-1294663808994.png' and colorchannel=3;");
        resultSet.next();
        resultSet.next();
        Object array = resultSet.getArray(6).getArray();
        Double[][] arr = (Double[][]) array;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                System.out.println(arr[i][j]);
            }
        }
    }

    private static void insertAllPatientDataIntoDatabase() throws Exception {
        long start = System.nanoTime();
        for (int i = 0; i < patients.size(); i++) {
            patients.get(i).runInserts(conn);
        }
        long finish = System.nanoTime();
        System.out.println("Insertion in total took " + ((finish - start) / Math.pow(10, 9)) / 60 + " minutes.");
    }

    private static void insertPatientDataIntoDatabase(int id) throws Exception {
        if (!done.contains(id)) {
            for (Patient patient : patients) {
                if (patient.id == id) {
                    long start = System.nanoTime();
                    patient.runInserts(conn);
                    long finish = System.nanoTime();
                    System.out.println("Insertion took " + ((finish - start) / Math.pow(10, 9)) / 60 + " minutes.");
                    break;
                }
            }
        } else {
            System.out.println("ID " + id + " already done!");
        }
    }

    private static void readMappingFileAndAddPatients() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("patientmapping.csv"));
        String line;
        while (true) {
            line = in.readLine();
            if (line == null) {
                break;
            }
            String[] args = line.split(";");
            addOrSkipIfExists(args[0], Integer.parseInt(args[1]));
        }
    }

    private static void showAssociatedFileNamesOfPatient(int id) {
        System.out.println("Total number of patients: " + patients.size());
        boolean exists = false;
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).id == id) {
                exists = true;
                Patient patient = patients.get(i);
                System.out.println("Images of patient: " + patient.id);
                for (int j = 0; j < patient.associatedFileNames.size(); j++) {
                    System.out.println(patient.associatedFileNames.get(j));
                }
            }
        }
        if (!exists) {
            System.out.println("Patient with id " + id + " does not exist.");
        }
    }

    private static void addOrSkipIfExists(String fileName, int patientId) {
        boolean patientExisting = false;
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            if (patient.id == patientId) {
                patientExisting = true;
                if (!patient.associatedFileNames.contains(fileName)) {
                    patient.associatedFileNames.add(fileName);
                }
            }
        }
        if (!patientExisting) {
            Patient patient = new Patient(patientId);
            patient.associatedFileNames = new LinkedList<String>();
            patient.associatedFileNames.add(fileName);
            patients.add(patient);
        }
    }

    private static void readPatientDataAndAddToPatients() throws Exception {
        File dir = new File("path of csv files directory");
        File patternDir = new File("filepath of uhl's pattern images directory");
        Collection files = FileUtils.listFiles(dir,
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY
        );
        Collection patternFiles = FileUtils.listFiles(patternDir,
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY
        );

        for (int i = 0; i < patients.size(); i++) {
            Patient currPatient = patients.get(i);
            currPatient.patientDataList = new LinkedList<PatientData>();
            LinkedList<String> associatedFileNames = currPatient.associatedFileNames;
            for (int j = 0; j < associatedFileNames.size(); j++) {
                Iterator<File> it = files.iterator();
                while (it.hasNext()) {
                    File currFile = it.next();
                    String nameWithoutDotCSV = currFile.getName().substring(0, currFile.getName().length() - 4);
                    if (nameWithoutDotCSV.equals(associatedFileNames.get(j))) {
                        String currPath = currFile.getPath();
                        String[] params = currPath.split("\\\\");
                        Iterator<File> patternIt = patternFiles.iterator();
                        String pattern = null;
                        while (patternIt.hasNext()) {
                            File curr = patternIt.next();
                            if (curr.getName().equals(nameWithoutDotCSV)) {
                                String[] dirs = curr.getPath().split("\\\\");
//                                String[] dirs = it.next().getPath().split("\\\\");
                                pattern = dirs[6];
//                                System.out.println("Pattern: " + pattern);
                                break;
                            }
                        }
                        addParamsToCurrPatient(currPatient, currFile, nameWithoutDotCSV, params, pattern);
                    }
                }
            }
        }

    }

    private static void addParamsToCurrPatient(Patient currPatient, File currFile, String nameWithoutDotCSV,
                                               String[] params, String pattern) throws Exception {
        PatientData patientData = new PatientData();
        if (params[1].equals("mag")) {
            patientData.magOrPhase = 0;
        } else if (params[1].equals("phase")) {
            patientData.magOrPhase = 1;
        } else {
            throw new Exception("Found unsupported directory name('mag' or 'phase' expected) " + params[1]);
        }
        if (params[2].equals("Y")) {
            patientData.colorChannel = 0;
        } else if (params[2].equals("U")) {
            patientData.colorChannel = 1;
        } else if (params[2].equals("V")) {
            patientData.colorChannel = 2;
        } else if (params[2].equals("R")) {
            patientData.colorChannel = 3;
        } else if (params[2].equals("G")) {
            patientData.colorChannel = 4;
        } else if (params[2].equals("B")) {
            patientData.colorChannel = 5;
        } else {
            throw new Exception("Found unsupported directory name('Y', 'U', 'V', 'R', 'G', 'B' expected) " + params[2]);
        }

        patientData.pattern = patterns.indexOf(pattern);
        patientData.nameOfImage = nameWithoutDotCSV;
        patientData.data = new float[256][256];
        BufferedReader in = new BufferedReader(new FileReader(currFile));

        String lineString;
        for (int line = 0; line < patientData.data.length; line++) {
            lineString = in.readLine();
            String[] values = lineString.split(",");
            for (int k = 0; k < values.length; k++) {
                patientData.data[line][k] = Float.parseFloat(values[k]);
            }
        }
        currPatient.patientDataList.add(patientData);
    }
}


class Patient {

    int id;
    LinkedList<PatientData> patientDataList;
    LinkedList<String> associatedFileNames;

    public Patient(int id) {
        this.id = id;
    }


    public void runInserts(Connection conn) throws Exception {
        System.out.println("------------------- Running Inserts for PATIENT-ID: " + id + " -------------------");
        for (PatientData patientData : patientDataList) {

            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO PatientData values(").append(id).append(", \'").append(patientData
                    .nameOfImage).append("\', ").append(patientData.colorChannel).append(", ").append(patientData
                    .magOrPhase).append(", ").append(patientData.pattern).append(", ");
            insert.append("'{ ");
            for (int line = 0; line < patientData.data.length; line++) {
                insert.append("{");
                for (int col = 0; col < patientData.data[0].length; col++) {
                    if (col != 255) {
                        insert.append(patientData.data[line][col]).append(", ");
                    } else {
                        insert.append(patientData.data[line][col]);
                    }
                }
                if (line != 255) {
                    insert.append("}, ");
                } else {
                    insert.append("} ");
                }
            }
            insert.append("}');");
//            System.out.println(insert.toString());
            try {
                conn.createStatement().execute(insert.toString());
            } catch (SQLException e) {
                throw new Exception("An exception was thrown. \nPatient-id: " + id + ", patientImage: " + patientData.nameOfImage, e);
            }
        }
    }
}

class PatientData {
    String nameOfImage;
    int colorChannel;
    int magOrPhase;
    float[][] data;
    int pattern;
}