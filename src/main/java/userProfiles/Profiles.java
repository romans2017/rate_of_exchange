package userProfiles;

import java.io.*;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Profiles implements Serializable {

    private final ConcurrentMap<String, ProfileSettings> mapProfiles;
    private static Profiles instance;

    private Profiles() {
        mapProfiles = new ConcurrentHashMap<>();
    }

    /**
     * получает экземпляр Profiles из сериализованного файла или, если файла нет - создает новый
     */
    public static Profiles getInstance() {
        if (instance == null) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(".\\src\\main\\resources\\profiles.dat"))) {
                instance = (Profiles) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                instance = new Profiles();
            }
        }
        return instance;
    }

    /**
     * получает ProfileSettings из мапы или, если нет - создает новый, возвращает его и добавляет в мапу
     */
    public ProfileSettings getProfileSettings(String chatId) {

        return Optional
                .ofNullable(mapProfiles.get(chatId))
                .orElse(getDefaultProfileSettings(chatId));
    }

    private void updateProfileSettings(String chatId, ProfileSettings profileSettings) {
        mapProfiles.put(chatId, profileSettings);
    }

    private ProfileSettings getDefaultProfileSettings(String chatId) {

        ProfileSettings profileSettings = new ProfileSettings();
        updateProfileSettings(chatId, profileSettings);
        return profileSettings;
    }

    /**
     * сохраняет текущий экземпляр Profiles в сериализованный файл по определенному расписанию (каждые 5 минут)
     */
    public void SchedulerSaveToFile() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(".\\src\\main\\resources\\profiles.dat"))) {
                    objectOutputStream.writeObject(instance);
                } catch (IOException ignored) {
                }
            }
        }, 1000L, 5L * 60L * 1000L);

    }
}
