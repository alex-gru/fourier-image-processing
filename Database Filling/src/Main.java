import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.*;
import java.sql.Connection;
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

    public static void main(String[] args) throws Exception {
        done.add(2);
        done.add(15);
        done.add(17); //17: custom
        done.add(1);
        done.add(3);
        done.add(4);
        done.add(5);
        done.add(6);
        done.add(7);
        done.add(8);
        readMappingFileAndAddPatients();
        readPatientDataAndAddToPatients();
        conn = DBConnector.getConnection();
        insertAllPatientDataIntoDatabase(8);
    }

    private static void insertAllPatientDataIntoDatabase() throws Exception {
        long start = System.nanoTime();
        for (Patient patient : patients) {
            patient.runInserts(conn);
        }
        long finish = System.nanoTime();
        System.out.println("Insertion took " + ((finish - start) / Math.pow(10, 9)) / 60 + " minutes.");
    }

    private static void insertAllPatientDataIntoDatabase(int id) throws Exception {
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
        File dir = new File("data");
        Collection files = FileUtils.listFiles(dir,
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
//                        System.out.println(currPath);
                        String[] params = currPath.split("\\\\");
//                        for (String param : params) {
//                            System.out.println("\t" + param);
//                        }
                        addParamsToCurrPatient(currPatient, currFile, nameWithoutDotCSV, params);
                    }
                }
            }
        }

    }

    private static void addParamsToCurrPatient(Patient currPatient, File currFile, String nameWithoutDotCSV, String[] params) throws Exception {
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
        } else {
            throw new Exception("Found unsupported directory name('Y', 'U', 'V' expected) " + params[2]);
        }

        patientData.nameOfImage = nameWithoutDotCSV;
        patientData.data = new float[65536];
        BufferedReader in = new BufferedReader(new FileReader(currFile));
        String line;
        int position = 0;
        while (position < 65536) {
            line = in.readLine();
            String[] args = line.split(",");
            for (int k = 0; k < args.length; k++) {
                patientData.data[position] = Float.parseFloat(args[k]);
                position++;
            }
//            System.out.println("curr Position: " + position);
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

    public void showRandomPatientInserts() {

        System.out.println("------------------- PATIENT-ID: " + id + " -------------------");
        for (PatientData patientData : patientDataList) {
            int random = ((int) (Math.random() * 65535));
            String insert = "INSERT INTO PatientData values(" + id + ", \'" + patientData.nameOfImage + "\', "
                    + patientData.colorChannel + ", " + patientData.magOrPhase + ", " + random + ", "
                    + patientData.data[random] + ")";
            System.out.println(insert);
        }

    }

    public void runInserts(Connection conn) throws Exception {
        System.out.println("------------------- Running Inserts for PATIENT-ID: " + id + " -------------------");
        for (PatientData patientData : patientDataList) {
            StringBuilder insert = new StringBuilder();
            for (int pos = 0; pos < patientData.data.length; pos++) {
                insert.append("INSERT INTO PatientData values(" + id + ", \'" + patientData.nameOfImage + "\', "
                        + patientData.colorChannel + ", " + patientData.magOrPhase + ", " + pos + ", "
                        + patientData.data[pos] + "); ");
            }
//            System.out.println("<<<<<<<<< " + insert.toString());

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
    float[] data;
}