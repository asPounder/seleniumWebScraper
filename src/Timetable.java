public class Timetable {
    private String date;
    private type? timetableData

    public Timetable(String PATH) {
        
    }

    public void get() {
        try (ObjectInputStream os = new ObjectInputStream(new FileInputStream(BINARY_PATH))) {
            timetable = (List<List<String>>) os.readObject();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
        } catch (IOException e) {
            throw new IOException("Unable to fetch from timetable.bin file");
        }
    }

    public void set() {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(BINARY_PATH))) {

        os.writeObject(timetable);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Unable to find timetable.bin file. It should be in src directory");
        } catch (IOException e) {
            throw new IOException("Unable to write to timetable.bin file");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yyyy", new Locale("pl"));
        String timetableDate = LocalDate.parse(timetable.get(0).get(0), formatter).toString();
        tryToSet("date", timetableDate, config);
        try (FileOutputStream fo = new FileOutputStream(CONFIG_PATH)) {
            config.store(fo, null);
        } catch (FileNotFoundException e){
            throw new FileNotFoundException("Could not locate the config.properties file");
        }
    }

    
}