public class Timetable {
    private String date;
    private String[][] timetableData;
    private final String BINARY_PATH;

    public Timetable(String PATH) {
        this.BINARY_PATH = PATH;
    }

    public int load() {
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(this.BINARY_PATH))) {
            List<List<String>> timetable = (List<List<String>>) os.readObject();
        } catch (Exception e) {
            return -1;
        }

        this.date = timetable.get(0).get(0);
        this.timetableData = new String[timetable.size()-1][2];
        for (int i = 1; i < timetable.size(); i++) {
            this.timetableData[i-1][0] = timetable.get(i).get(0);
            this.timetableData[i-1][1] = timetable.get(i).get(1);
        }

        return 0;
    }

    public String getDate() {
        return this.date; // throw something?
    }

    public String[][] getTimetableData() {
        return this.timetableData; // throw something?
    }

    public void set(final String CONFIG_PATH, final String DATE) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(this.BINARY_PATH))) {
            os.writeObject(timetable);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
        } catch (IOException e) {
            throw new IOException("Unable to write to timetable.bin file");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yyyy", new Locale("pl"));
        String timetableDate = LocalDate.parse(DATE, formatter).toString();
        tryToSet("date", timetableDate, config);
        try (FileOutputStream fo = new FileOutputStream(CONFIG_PATH)) {
            config.store(fo, null);
        } catch (FileNotFoundException e){
            throw new FileNotFoundException("Could not locate the config.properties file");
        }
    }

    
}